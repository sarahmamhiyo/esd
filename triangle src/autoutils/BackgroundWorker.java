/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autoutils;

import app.Config;
import app.Main;
import app.SystemInit;
import dbutils.DbManager;
import emailutils.SendMail;
import htmlutils.HTMLReceiptProcessor;
import invoicetypeutils.FilesScanner;
import invoicetypeutils.InvoiceSigner;
import java.awt.Color;
import java.io.File;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pdfutils.PRNGenerator;
import pdfutils.PdfManager;
import tests.DbTest;

/**
 *
 * @author Tinashe
 */
public class BackgroundWorker {

    String TAG = "";
    public static ExecutorService executor = null;
    public static boolean stop_background_tasks = false;
    public static int total_sap_invoices_received = 0;
    public static int total_prn_files_created = 0;
    public static int total_html_receipts_received = 0;
    public static int total_sap_invoices_signed = 0;
    public static int total_signed_invoices_sent = 0;
    public static HashMap<String, ArrayList<String>> outbox = new HashMap();

    public static void main(String[] args) {
        new BackgroundWorker();
    }

    public BackgroundWorker() {
        //create the thread pool executor service
        this.executor = Executors.newFixedThreadPool(10);
        //Start the CheckInvoicesThread 
        CheckAndProcessInvoicesThread check_new_invoices = new CheckAndProcessInvoicesThread();
        //check_new_invoices.run();

        //Start the CheckAndProcessHTMLReceiptsThread
        CheckAndProcessHTMLReceiptsThread check_new_receipts = new CheckAndProcessHTMLReceiptsThread();
        //check_new_receipts.run();

        //Start the SendEmailsThread 
        SendEmailsThread send_emails_thread = new SendEmailsThread();

        executor.submit(check_new_invoices);
        executor.submit(check_new_receipts);
        executor.submit(send_emails_thread);

        //executor.shutdown();
    }

    class CheckAndProcessInvoicesThread implements Runnable {

        String TAG = "CheckAndProcessInvoicesThread";

        @Override
        public void run() {
            while (stop_background_tasks == false) {
                //Submit a task to check for new invoices
                Future<ArrayList<File>> check_result = executor.submit(new CheckNewInvoicesTask());
                try {
                    //Block & wait for result
                    ArrayList<File> files_found_and_renamed = check_result.get();
                    if (files_found_and_renamed != null && files_found_and_renamed.size() > 0) {
                        //Set total_sap_invoices_received
                        total_sap_invoices_received += files_found_and_renamed.size();
                        //Extract invoice data from the new invoices found
                        DbManager db_manager = new DbManager();
                        for (int i = 0; i < files_found_and_renamed.size(); i++) {
                            File file_found_and_renamed = files_found_and_renamed.get(i);
                            //Set the processed flag for this invoice
                            String file_name = file_found_and_renamed.getName().replace(".sen", "");
                            System.out.println(TAG + ", now processing: " + file_name);
                            if (db_manager.set_invoice_processed_status(file_name + ".pdf", 1)) {
                                //Submit an ExtractInvoiceDataTask task for execution
                                Future<HashMap<String, String>> extraction_response = executor.submit(new ExtractInvoiceDataTask(file_found_and_renamed));
                                //Block & get the response
                                HashMap<String, String> invoice_data_values = extraction_response.get();
                                //Check response
                                if (invoice_data_values != null) {
                                    //Generate the PRN data from this data
                                    Future<HashMap<String, HashMap<Integer, ArrayList<String>>>> generate_prn_data_response = executor.submit(new GeneratePRNDataTask(invoice_data_values));
                                    //Block & get response
                                    HashMap<String, HashMap<Integer, ArrayList<String>>> prn_data = generate_prn_data_response.get();
                                    //Check the response
                                    if (prn_data != null) {
                                        //Create PRN file
                                        Future<Boolean> generate_prn_file_response = executor.submit(new GeneratePRNFileTask(file_name, prn_data));
                                        //Block & get response
                                        boolean generate_prn_file_status = generate_prn_file_response.get();
                                        if (generate_prn_file_status) {
                                            //Update the invoice status in db
                                            String recepient = null;
                                            for (Object key : prn_data.keySet()) {
                                                String field_name = (String) key;
                                                if (field_name.equalsIgnoreCase("User Email")) {
                                                    HashMap<Integer, ArrayList<String>> field_values_map = prn_data.get(key);
                                                    ArrayList<String> field_values_array = field_values_map.get(0);
                                                    recepient = field_values_array.get(0);
                                                }
                                            }

                                            if (recepient != null) {
                                                file_name = file_name + ".pdf";
                                                db_manager = new DbManager();
                                                if (!db_manager.set_invoice_prn_created_status(file_name, recepient, 1)) {
                                                    System.out.println(TAG + ", failed to set prn_status in db for invoice: " + file_found_and_renamed.getAbsolutePath());
                                                } else {
                                                    //Set total_sap_invoices_received
                                                    total_prn_files_created += 1;
                                                }
                                            } else {
                                                System.out.println(TAG + ", failed to get recepient for invoice: " + file_found_and_renamed.getAbsolutePath());
                                            }

                                        } else {
                                            System.out.println(TAG + ", failed to generate prn file from invoice: " + file_found_and_renamed.getAbsolutePath());
                                        }
                                    } else {
                                        //This could be an empty invoice, delete from db if it doesnt contain "_invalid_invoice" and mark as not an invoice
                                        if (!file_name.contains("_invalid_invoice")) {
                                            String invoice_name = file_name + ".pdf";
                                            if (db_manager.delete_empty_invoice(invoice_name)) {
                                                //Now rename the file to _invalid_invoice
                                                File existing_file = file_found_and_renamed;
                                                File invalid_invoice_file = new File(Config.SAP_INVOICES_PATH + file_name + "_invalid_invoice.pdf");
                                                //Rename the invoice
                                                if (existing_file.renameTo(invalid_invoice_file)) {
                                                    System.out.println(TAG + ", failed to generate prn data from invoice: " + file_found_and_renamed.getAbsolutePath());
                                                } else {
                                                    System.out.println(TAG + ", failed to rename invalid invoice to: " + invalid_invoice_file.getAbsolutePath());
                                                }

                                            } else {
                                                System.out.println(TAG + ", failed to delete empty invoice from db: " + invoice_name);
                                            }
                                        } else {
                                            System.out.println(TAG + ", failed to generate prn data from invoice: " + file_found_and_renamed.getAbsolutePath());
                                        }
                                    }
                                } else {
                                    System.out.println(TAG + ", failed to extract data from invoice or its a duplicate: " + file_found_and_renamed.getAbsolutePath());
                                }
                            } else {
                                System.out.println(TAG + ", failed to set processed flag for: " + file_found_and_renamed.getName());
                            }
                        }
                    } else {
                        //No new files for other tasks to process
                        Thread.sleep(3000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(BackgroundWorker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(BackgroundWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        class CheckNewInvoicesTask implements Callable<ArrayList<File>> {

            String TAG = "CheckNewInvoicesTask";

            @Override
            public ArrayList<File> call() throws Exception {
                ArrayList<File> new_invoices_found = null;
                //Get the FileScanner object
                FilesScanner files_scanner = new FilesScanner();
                new_invoices_found = files_scanner.check_for_new_sap_invoices_new();
                if (new_invoices_found != null && new_invoices_found.size() > 0) {
                    System.out.println(TAG + ", " + new_invoices_found.size() + " new invoices found.");
                    return new_invoices_found;
                } else {
                    System.out.println(TAG + ", no new invoices found.");
                    return null;
                }
            }

        }

        class ExtractInvoiceDataTask implements Callable<HashMap<String, String>> {

            File file_found_and_renamed = null;
            String existing_duplicate_invoice_name = null;

            public ExtractInvoiceDataTask(File file_found_and_renamed) {
                this.file_found_and_renamed = file_found_and_renamed;
            }

            @Override
            public HashMap<String, String> call() throws Exception {
                if (file_found_and_renamed != null) {
                    //Get file at current index
                    File f = file_found_and_renamed;
                    String file_path_name = f.getAbsolutePath();
                    String file_name = f.getName();

                    //Get page count for this file
                    PdfManager pdf_manager = new PdfManager();
                    int page_count = pdf_manager.get_number_of_pages(file_path_name);
                    //Extract the data
                    PRNGenerator prn_generator = new PRNGenerator();
                    HashMap<String, String> invoice_data_values = prn_generator.extract_invoice_data(file_name, page_count);
                    if (invoice_data_values != null) {
                        //Check if this invoice is duplicate or not
                        String status = check_if_its_duplicate(file_name, invoice_data_values);
                        if (status != null) {
                            if (status.equalsIgnoreCase("not duplicate")) {
                                //Send the data extracted for prn & signing
                                return invoice_data_values;
                            } else if (status.equalsIgnoreCase("duplicate")) {
                                //Dont submit for further processing, it has already been re-sent
                                return null;
                            } else {
                                //Unknown response, from duplicate check
                                return null;
                            }
                        } else {
                            //An error occured while checking duplicity
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }

            }

            String check_if_its_duplicate(String file_name, HashMap<String, String> extracted_invoice_data_values) {
                System.out.println(TAG + ", check_if_its_duplicate(): initial file_name: " + file_name);
                String status = null;
                //For safety set filename as pdf
                file_name = file_name.replace("sen", "pdf");
                //file_name = file_name + ".pdf";
                //Create the data for duplicate checking 
                String invoice_to = (String) extracted_invoice_data_values.get("Invoice To");
                invoice_to = invoice_to.trim();
                String invoice_number = (String) extracted_invoice_data_values.get("Invoice Number");
                invoice_number = invoice_number.trim();
                String grand_total = (String) extracted_invoice_data_values.get("Grand Total");
                grand_total = grand_total.trim();

                HashMap<String, String> invoice_data = new HashMap();
                //Get the invoice to
                invoice_data.put("invoice_to", invoice_to);
                //Get the invoice number
                invoice_data.put("invoice_number", invoice_number);
                //Get the Grand Total
                invoice_data.put("grand_total", grand_total);
                //Add file name
                invoice_data.put("file_name", file_name);
                //Get the recepient 
                String recepient = extracted_invoice_data_values.get("User Email");
                DbManager db_manager = new DbManager();
                //Check if duplicate invoice exists
                HashMap<String, String> existing_invoice_data = check_for_duplicate_invoice_by_data(invoice_data);
                if (existing_invoice_data == null) {
                    //This is not a duplicate invoice, add it to sap_invoices_data in db
                    if (db_manager.insert_invoice_data(invoice_data)) {
                        //Proceed to PRN & signign
                        System.out.println(TAG + ", This is not a duplicate invoice, have added it to sap_invoices_data in db.");
                        status = "not duplicate";
                    } else {
                        //An error occured
                        System.out.println(TAG + ", This is not a duplicate invoice, failed to added it to sap_invoices_data in db.");
                        status = null;
                    }
                } else {
                    /*
                     This is a duplicate invoice, resend the already signed invoice.
                     Prevent user from creating PRN or a signed invoice
                     */
                    //Get the invoice data to be used to sign this new
                    HashMap<String, String> invoice_data_values = db_manager.get_invoice_data(existing_duplicate_invoice_name);
                    if (invoice_data_values != null) {
                        System.out.println(TAG + ", invoice_data_values found: ");
                        System.out.println(invoice_data_values);
                        String existing_file_name = invoice_data_values.get("file_name");
                        //Check if the filenames are the same
                        if (file_name.equalsIgnoreCase(existing_duplicate_invoice_name)) {
                            //Resend the earlier signed invoice
                            if (db_manager.reset_invoice_sent_status(file_name, 0)) {
                                System.out.println(TAG + ", Duplicate invoice (" + file_name + ") set for re-sending.");
                                status = "duplicate";
                            } else {
                                System.out.println(TAG + ", Failed to set duplicate invoice (" + file_name + ") for re-sending.");
                                status = null;
                            }
                        } else {
                            //Sign the new duplicate invoice and send
                            String signature = invoice_data_values.get("signature");
                            //Temp signature
                            if (signature == null) {
                                signature = "none";
                            }

                            //Check if signature is not none
                            if (signature.equalsIgnoreCase("none")) {
                                //Existing duplicate Duplicate not signed, delete unsigned invoice
                                if (db_manager.delete_duplicate_unsigned_invoice(existing_invoice_data.get("filename"))) {
                                    //Insert this new invoice for processing
                                    if (db_manager.insert_invoice_data(invoice_data)) {
                                        //Proceed to PRN & signign
                                        System.out.println(TAG + ", This is a new duplicate invoice, have added it to sap_invoices_data in db.");
                                        status = "not duplicate";
                                    } else {
                                        //An error occured
                                        System.out.println(TAG + ", This is not a duplicate invoice, failed to added it to sap_invoices_data in db.");
                                        status = null;
                                    }
                                } else {
                                    System.out.println(TAG + ", Failed to delete unsigned existing invoice (" + existing_invoice_data.get("filename") + ").");
                                }

                            } else {
                                String invoice_name = file_name.replace("pdf", "sen");
                                InvoiceSigner signer = new InvoiceSigner();
                                boolean sign_status = signer.sign_invoice(invoice_name, signature);
                                if (sign_status) {
                                    //Submit this new signed invoice for sending
                                    if (db_manager.insert_duplicate_signed_invoice_for_sending(file_name, recepient)) {
                                        System.out.println(TAG + ", Signed & submitted for re-sending (" + file_name + ").");
                                        //Mark the file as duplicate
                                        status = "duplicate";
                                    } else {
                                        System.out.println(TAG + ", Failed to submit for re-sending (" + file_name + ").");
                                        status = null;
                                    }
                                } else {
                                    System.out.println(TAG + "Failed to sign duplicate invoice (" + file_name + ").");
                                    status = null;
                                }
                            }
                        }
                    } else {
                        System.out.println(TAG + ", Failed to get data values from sap_invoices_data in db for: " + file_name);
                        status = null;
                    }
                }

                return status;
            }

            HashMap<String, String> check_for_duplicate_invoice_by_data(HashMap<String, String> invoice_data) {
                //Check for duplicate data in db
                DbManager db_manager = new DbManager();
                HashMap<String, String> existing_invoice_data = db_manager.check_for_duplicate_invoice_by_data(invoice_data);
                if (existing_invoice_data != null) {
                    existing_duplicate_invoice_name = existing_invoice_data.get("filename");
                    System.out.println(TAG + ", check_for_duplicate_invoice_by_data(): duplicate invoice data exists.");
                    return existing_invoice_data;
                } else {
                    System.out.println(TAG + ", check_for_duplicate_invoice_by_data(): duplicate invoice data doesnt exists.");
                    return null;
                }
            }

        }//End of ExtractInvoiceDataTask

        class GeneratePRNDataTask implements Callable<HashMap<String, HashMap<Integer, ArrayList<String>>>> {

            String TAG = "GeneratePRNDataTask";
            HashMap<String, String> invoice_data_values = null;

            public GeneratePRNDataTask(HashMap<String, String> invoice_data_values) {
                this.invoice_data_values = invoice_data_values;
            }

            @Override
            public HashMap<String, HashMap<Integer, ArrayList<String>>> call() throws Exception {
                if (invoice_data_values != null) {
                    PRNGenerator prn_generator = new PRNGenerator();
                    HashMap<String, HashMap<Integer, ArrayList<String>>> prn_data = prn_generator.generatePRNData(invoice_data_values);
                    if (prn_data != null) {
                        //Check if the invoice is not empty, by check for the grand total & line items
                        if (check_if_invoice_is_empty(prn_data)) {
                            return prn_data;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            boolean check_if_invoice_is_empty(HashMap<String, HashMap<Integer, ArrayList<String>>> prn_data) {
                boolean contains_line_items = false;
                boolean has_grand_total = false;
                for (Object key : prn_data.keySet()) {
                    String field_name = (String) key;
                    HashMap<Integer, ArrayList<String>> field_values_map = prn_data.get(key);
                    for (int i = 0; i < field_values_map.size(); i++) {
                        ArrayList<String> field_values_array = field_values_map.get(i);
                        if (!field_name.equalsIgnoreCase("Line items")) {
                            String field_value = field_values_array.get(0);
                            if (field_name.equalsIgnoreCase("Grand Total")) {
                                if (field_value.matches(".*\\d.*")) {
                                    has_grand_total = true;
                                }
                            }
                        } else {
                            contains_line_items = true;
                        }

                    }
                }//End loop

                if (contains_line_items && has_grand_total) {
                    return true;
                } else {
                    return false;
                }
            }

        }//End of GeneratePRNDataTask

        class GeneratePRNFileTask implements Callable<Boolean> {

            String file_name = null;
            HashMap<String, HashMap<Integer, ArrayList<String>>> prn_data = null;

            public GeneratePRNFileTask(String file_name, HashMap<String, HashMap<Integer, ArrayList<String>>> prn_data) {
                this.file_name = file_name;
                this.prn_data = prn_data;
            }

            @Override
            public Boolean call() throws Exception {
                if (file_name != null && prn_data != null) {
                    PRNGenerator prn_generator = new PRNGenerator();
                    boolean create_prn_status = prn_generator.generatePRN(file_name, prn_data);
                    if (create_prn_status) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }

        }//End of GeneratePRNFileTask

    }

    class CheckAndProcessHTMLReceiptsThread implements Runnable {

        String TAG = "CheckAndProcessHTMLReceipts";

        @Override
        public void run() {
            while (stop_background_tasks == false) {
                try {
                    Future<ArrayList<File>> check_response = executor.submit(new CheckNewReceiptsTask());
                    //Block & get response
                    ArrayList<File> new_receipts_found = check_response.get();
                    if (new_receipts_found != null && new_receipts_found.size() > 0) {
                        //Set total_html_receipts_received
                        total_html_receipts_received += new_receipts_found.size();
                        //Extract the signatures from these receipts
                        for (int i = 0; i < new_receipts_found.size(); i++) {
                            File f = new_receipts_found.get(i);
                            String file_name = f.getName();
                            Future<String> extract_response = executor.submit(new ExtractSignatureTask(file_name));
                            //Block & get response
                            String signature = extract_response.get();
                            if (signature != null) {
                                //Sign the invoice
                                Future<Boolean> signing_response = executor.submit(new SignInvoiceTask(file_name, signature));
                                //Block & get response
                                boolean signing_status = signing_response.get();
                                if (signing_status == false) {
                                    System.out.println(TAG + ", failed to sign invoice: " + file_name);
                                } else {
                                    //Set total_sap_invoices_signed
                                    total_sap_invoices_signed += 1;
                                }
                            } else {
                                System.out.println(TAG + ", failed to extract signature from receipt: " + file_name);
                            }
                        }
                    } else {
                        Thread.sleep(3000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(BackgroundWorker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(BackgroundWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        class CheckNewReceiptsTask implements Callable<ArrayList<File>> {

            String TAG = "CheckNewReceiptsTask";

            @Override
            public ArrayList<File> call() throws Exception {
                ArrayList<File> new_receipts_found = null;
                //Get the FileScanner object
                FilesScanner files_scanner = new FilesScanner();
                new_receipts_found = files_scanner.check_for_new_html_receipts();
                if (new_receipts_found != null && new_receipts_found.size() > 0) {
                    System.out.println(TAG + ", " + new_receipts_found.size() + " new HTML receipts found.");
                    return new_receipts_found;
                } else {
                    System.out.println(TAG + ", no new HTML receipts found.");
                    return null;
                }
            }

        }//End of CheckNewReceiptsTask

        class ExtractSignatureTask implements Callable<String> {

            String file_name = null;

            public ExtractSignatureTask(String file_name) {
                this.file_name = file_name;
            }

            @Override
            public String call() throws Exception {
                if (file_name != null) {
                    HTMLReceiptProcessor html_processor = new HTMLReceiptProcessor();
                    //Get the signature
                    String signature = html_processor.extract_signature(file_name);
                    if (signature != null) {
                        return signature;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }

        }//End ExtractSignatureTask

        class SignInvoiceTask implements Callable<Boolean> {

            String TAG = "SignInvoiceTask";
            String invoice_name = null, signature = null;

            public SignInvoiceTask(String invoice_name, String signature) {
                this.invoice_name = invoice_name;
                this.signature = signature;
            }

            @Override
            public Boolean call() throws Exception {
                if (invoice_name != null && signature != null) {
                    InvoiceSigner signer = new InvoiceSigner();
                    boolean sign_status = signer.sign_invoice(invoice_name, signature);
                    if (sign_status) {
                        //Update the invoice status in db
                        DbManager db_manager = new DbManager();
                        invoice_name = invoice_name.replace("sen", "pdf");
                        boolean signing_status = db_manager.set_invoice_signed_status(invoice_name, 1);
                        if (signing_status == false) {
                            System.out.println(TAG + ", failed to set signed status in db for invoice: " + invoice_name);
                            app.Logger log = new app.Logger();
                            ArrayList<String> info_lines = new ArrayList();
                            info_lines.add(TAG + ": failed to set signed status in db for invoice (" + invoice_name + ")");
                            log.writeLog(invoice_name, info_lines);
                            return false;
                        } else {
                            //Set signature on invoice data table
                            String file_name = invoice_name.replace("sen", "pdf");
                            if (db_manager.set_signature_on_invoice_data(file_name, signature)) {
                                return true;
                            } else {
                                System.out.println(TAG + ", failed to set signature in db for invoice: " + file_name);
                                app.Logger log = new app.Logger();
                                ArrayList<String> info_lines = new ArrayList();
                                info_lines.add(TAG + ": failed to set signature in db for invoice (" + invoice_name + ")");
                                log.writeLog(invoice_name, info_lines);
                                return false;
                            }

                        }
                    } else {
                        System.out.println(TAG + ", failed to sign invoice: " + invoice_name);
                        app.Logger log = new app.Logger();
                        ArrayList<String> info_lines = new ArrayList();
                        info_lines.add(TAG + ": failed to sign invoice (" + invoice_name + ")");
                        log.writeLog(invoice_name, info_lines);
                        return false;
                    }
                } else {
                    return false;
                }
            }

        }

    }//End CheckAndProcessHTMLReceiptsThread

    class SendEmailsThread implements Runnable {

        String TAG = "SendEmailsThread";

        @Override
        public void run() {
            while (stop_background_tasks == false) {
                try {
                    //Check for signed invoices ready for sending
                    Future<HashMap<String, ArrayList<String>>> check_response = executor.submit(new CheckUnsentInvoicesTask());
                    //Block & get response
                    HashMap<String, ArrayList<String>> unsent_invoices_found = check_response.get();
                    int total_send_failures = 0;
                    if (unsent_invoices_found != null && unsent_invoices_found.size() > 0) {
                        System.out.println(TAG + ", " + unsent_invoices_found.size() + " new unsent invoices found");
                        //Submit the found invoices for sending

                        HashMap<String, Future> sending_responses_map = send_invoices_one_by_one(unsent_invoices_found);
                        if (sending_responses_map != null && sending_responses_map.size() > 0) {
                            System.out.println(TAG + ", total email processed invoices: " + sending_responses_map.size());
                        } else {
                            System.out.println(TAG + ", no email processed invoices");
                        }

                        /*
                         HashMap<String, Future> sending_responses_map = send_invoices(unsent_invoices_found);
                         if (sending_responses_map != null && sending_responses_map.size() > 0) {
                         //Submit task to check for sending responses
                         executor.submit(new CheckEmailSendingResponsesTask(sending_responses_map));
                         }
                         */
                    } else {
                        System.out.println(TAG + ", No unsent invoices found");
                        Thread.sleep(4000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(BackgroundWorker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(BackgroundWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        HashMap<String, Future> send_invoices(HashMap<String, ArrayList<String>> unsent_invoices_found) {
            //Send these invoices
            boolean prev_response_received = false;
            int loop_count = 0;
            String invoice_name = null;
            ArrayList<String> invoice_data_array = null;
            String recepient = null;
            Future<Boolean> sending_response = null;
            HashMap<String, Future> sending_responses_map = new HashMap();
            int total_send_failures = 0;
            DbManager db_manager = new DbManager();
            for (Object key : unsent_invoices_found.keySet()) {
                invoice_name = (String) key;
                System.out.println(TAG + ", sending new email for: " + invoice_name + " loop_count: " + loop_count);
                invoice_data_array = unsent_invoices_found.get(key);
                recepient = invoice_data_array.get(2);
                //Check if recepient is not empty
                recepient = recepient.trim();
                System.out.println(TAG + ", recepient: " + recepient);
                if (recepient != null && recepient.length() > 0) {
                    //Check if email is properly struvtured
                    int index_of_opening_square_brackets = recepient.indexOf('<');
                    if (index_of_opening_square_brackets != -1) {
                        int index_of_closing_square_brackets = recepient.indexOf('>');
                        if (index_of_closing_square_brackets != -1) {
                            recepient = recepient.substring(index_of_opening_square_brackets + 1, index_of_closing_square_brackets);
                        } else {
                            recepient = recepient.substring(index_of_opening_square_brackets);
                        }
                    }
                } else {
                    System.out.println(TAG + ", Invoice: " + invoice_name + " recepient email is empty");
                    recepient = Config.TEST_RECEPIENT;
                }
                //Submit the SendInvoiceTask to executor
                sending_response = executor.submit(new SendInvoiceTask(invoice_name, recepient));
                //Set sending progress in db
                if (db_manager.set_invoice_sending_status(invoice_name, "in progress")) {
                    //Add this future response to sending_responses_map
                    sending_responses_map.put(invoice_name, sending_response);
                } else {
                    //Add this future response to sending_responses_map, anyway
                    sending_responses_map.put(invoice_name, sending_response);
                }

                loop_count++;

            }//End for loop

            return sending_responses_map;
        }

        HashMap<String, Future> send_invoices_one_by_one(HashMap<String, ArrayList<String>> unsent_invoices_found) {
            //Send these invoices
            boolean prev_response_received = false;
            int loop_count = 0;
            String invoice_name = null;
            ArrayList<String> invoice_data_array = null;
            String recepient = null;
            Future<Boolean> sending_response = null;
            HashMap<String, Future> sending_responses_map = new HashMap();
            int total_send_failures = 0;
            DbManager db_manager = new DbManager();
            ArrayList<String> sent_invoices = new ArrayList();

            //for (Object key : unsent_invoices_found.keySet()) {
            for (Object key : unsent_invoices_found.keySet()) {
                invoice_name = (String) key;
                System.out.println(TAG + ", sending new email for: " + invoice_name + " loop_count: " + loop_count);
                invoice_data_array = unsent_invoices_found.get(key);
                recepient = invoice_data_array.get(2);
                //Check if recepient is not empty
                recepient = recepient.trim();
                System.out.println(TAG + ", recepient: " + recepient);
                if (recepient != null && recepient.length() > 0 && !recepient.equalsIgnoreCase("no set")) {
                    //Check if email is properly struvtured
                    int index_of_opening_square_brackets = recepient.indexOf('<');
                    if (index_of_opening_square_brackets != -1) {
                        int index_of_closing_square_brackets = recepient.indexOf('>');
                        if (index_of_closing_square_brackets != -1) {
                            recepient = recepient.substring(index_of_opening_square_brackets + 1, index_of_closing_square_brackets);
                        } else {
                            recepient = recepient.substring(index_of_opening_square_brackets);
                        }
                    }
                } else {
                    System.out.println(TAG + ", Invoice: " + invoice_name + " recepient email is empty or not set");
                    recepient = Config.TEST_RECEPIENT;
                }
                //Submit the SendInvoiceTask to executor
                sending_response = executor.submit(new SendInvoiceTask(invoice_name, recepient));

                //Set sending progress in db
                if (db_manager.set_invoice_sending_status(invoice_name, "in progress")) {
                    //Add this future response to sending_responses_map
                    sending_responses_map.put(invoice_name, sending_response);
                } else {
                    //Add this future response to sending_responses_map, anyway
                    sending_responses_map.put(invoice_name, sending_response);
                }

                try {
                    //Block & sending response
                    boolean sending_status = sending_response.get();
                    //Update the sent_status & progress in db
                    int sent_status = 0;
                    if (sending_status) {
                        System.out.println(TAG + ", invoice: " + invoice_name + ", sent successifully to: " + recepient);
                        sent_status = 1;
                        //Set total_signed_invoices_sent
                        total_signed_invoices_sent += 1;
                    } else {
                        System.out.println(TAG + "Failed to send " + invoice_name + ", to: " + recepient);
                        sent_status = 0;
                    }

                    //Update invoice sent flag in db
                    if (db_manager.set_invoice_sent_status(invoice_name, sent_status)) {
                        System.out.println(TAG + ", total emails in outbox b4 removal: " + outbox.size());
                        //Store sent invoice
                        sent_invoices.add(invoice_name);
                        //Remove from out hashmap
                        //outbox.remove(invoice_name);
                        System.out.println(TAG + ", signed invoice: " + invoice_name + " sent status updated successfully.");
                        //Delete the sending failure log if it had been created
                        app.Logger log = new app.Logger();
                        log.deleteLog(invoice_name);
                        System.out.println(TAG + ", total remaining emails in outbox: " + outbox.size());
                    } else {
                        System.out.println(TAG + ", failed to update sent status in db for: " + invoice_name);
                        app.Logger log = new app.Logger();
                        ArrayList<String> info_lines = new ArrayList();
                        info_lines.add(TAG + ", signed invoice: " + invoice_name + " sending failed.");
                        log.writeLog(invoice_name, info_lines);
                        //Re-submit this file for re-sending by removing it from outbox
                        System.out.println(TAG + ", current outbox with: " + invoice_name);
                        System.out.println(outbox);
                        //outbox.remove(invoice_name);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(BackgroundWorker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(BackgroundWorker.class.getName()).log(Level.SEVERE, null, ex);
                }

                loop_count++;
                System.out.println(TAG + ", going to next loop : " + loop_count);

            }

            //Remove sent invoices from outbox
            if (sent_invoices != null && sent_invoices.size() > 0) {
                for (int i = 0; i < sent_invoices.size(); i++) {
                    invoice_name = (String) sent_invoices.get(i);
                    //Remove from outbox
                    outbox.remove(invoice_name);
                }
            }
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(BackgroundWorker.class.getName()).log(Level.SEVERE, null, ex);
            }

            return sending_responses_map;
        }

        class CheckUnsentInvoicesTask implements Callable<HashMap<String, ArrayList<String>>> {

            @Override
            public HashMap<String, ArrayList<String>> call() throws Exception {
                DbManager db_manager = new DbManager();

                int initial_outbox_size = 0;
                if (outbox != null) {
                    initial_outbox_size = outbox.size();
                } else {
                    outbox = new HashMap();
                }

                outbox = db_manager.get_unsent_invoices();
                if (outbox != null && outbox.size() > initial_outbox_size) {
                    return outbox;
                } else {
                    return null;
                }
            }

        }//End of CheckUnsentInvoicesTask

        class SendInvoiceTask implements Callable<Boolean> {

            String TAG = "SendInvoiceTask";
            String invoice_name = null, recepient = null;

            public SendInvoiceTask(String invoice_name, String recepient) {
                this.invoice_name = invoice_name;
                if (recepient != null) {
                    this.recepient = recepient;
                } else {
                    if (Config.TEST_RECEPIENT != null && !Config.TEST_RECEPIENT.equalsIgnoreCase("0")) {
                        this.recepient = Config.TEST_RECEPIENT;
                    } else {
                        this.recepient = recepient;
                    }
                }

            }

            @Override
            public Boolean call() throws Exception {
                if (invoice_name != null && recepient != null) {
                    //Send email
                    String subject = "Signed Invoice: " + invoice_name.replace(".pdf", "");
                    String body = "Find attached signed invoice.";
                    String file_path_name = Config.SIGNED_INVOICES_PATH + invoice_name;
                    //String temporary_recepient = "radsys@mgi.co.zw";
                    System.out.println(TAG + ", sending to: " + recepient);
                    String sending_status = SendMail.send_with_tls(recepient, subject, body, invoice_name, file_path_name);
                    if (sending_status.equalsIgnoreCase("success")) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    System.out.println(TAG + ", error sending invoice, either invoice_name or recepient are empty.");
                    app.Logger log = new app.Logger();
                    ArrayList<String> info_lines = new ArrayList();
                    info_lines.add(TAG + ": failed to set signed status in db for invoiceerror sending invoice, either invoice_name or recepient are empty (" + invoice_name + ")");
                    log.writeLog(invoice_name, info_lines);
                    return false;
                }

            }

        }//End of SendInvoiceTask

        class CheckEmailSendingResponsesTask implements Callable<Integer> {

            String TAG = "CheckEmailSendingResponsesTask";
            HashMap<String, Future> sending_responses_map = null;

            public CheckEmailSendingResponsesTask(HashMap<String, Future> sending_responses_map) {
                this.sending_responses_map = sending_responses_map;
            }

            @Override
            public Integer call() throws Exception {
                int total_responses_received = 0;

                if (sending_responses_map != null && sending_responses_map.size() > 0) {
                    DbManager db_manager = new DbManager();
                    for (Object key : sending_responses_map.keySet()) {
                        String invoice_name = (String) key;
                        //Get the current future
                        Future<Boolean> sending_response = sending_responses_map.get(key);
                        boolean response_received = false;
                        int total_trials = 0;
                        while (response_received == false) {
                            System.out.println(TAG + ", checking sent response for invoice: " + invoice_name + " , total_trial: " + total_trials);
                            //Increment total_trials
                            total_trials++;

                            //Block & check for response
                            try {
                                boolean sending_status = sending_response.get(3, TimeUnit.SECONDS);
                                //Update the sent_status & progress in db
                                int sent_status = 0;
                                if (sending_status) {
                                    sent_status = 1;
                                } else {
                                    sent_status = 0;
                                }
                                //Update invoice sent flag in db
                                if (db_manager.set_invoice_sent_status(invoice_name, sent_status)) {
                                    if (sent_status == 1) {
                                        //Set total_signed_invoices_sent
                                        total_signed_invoices_sent += 1;
                                        System.out.println(TAG + ", signed invoice: " + invoice_name + " sent successfully.");
                                        //Delete the sending failure log if it had been created
                                        app.Logger log = new app.Logger();
                                        log.deleteLog(invoice_name);
                                        //Remove from outbox
                                        outbox.remove(invoice_name);
                                    } else {
                                        System.out.println(TAG + ", signed invoice: " + invoice_name + " sending failed.");
                                        app.Logger log = new app.Logger();
                                        ArrayList<String> info_lines = new ArrayList();
                                        info_lines.add(TAG + ", signed invoice: " + invoice_name + " sending failed.");
                                        log.writeLog(invoice_name, info_lines);
                                        //Re-submit this file for re-sending by removing it from outbox
                                        System.out.println(TAG + ", current outbox with: " + invoice_name);
                                        System.out.println(outbox);
                                        outbox.remove(invoice_name);
                                        System.out.println(TAG + ", new outbox without: " + invoice_name);
                                        System.out.println(outbox);

                                    }
                                } else {
                                    System.out.println(TAG + ", failed to update sent status in db for: " + invoice_name);
                                }
                                //Set response_received flag
                                response_received = true;
                                //Set total_responses_received counter
                                total_responses_received++;
                            } catch (TimeoutException ex) {
                                //Sleep & check again
                                Thread.sleep(2000);
                                System.out.println(TAG + ", rechecking for response at total_trials: " + total_trials);
                                if (total_trials >= 30) {
                                    //Update invoice sent flag in db
                                    if (db_manager.set_invoice_sent_status(invoice_name, 0)) {
                                        System.out.println(TAG + ", signed invoice: " + invoice_name + " sending failed.");
                                        app.Logger log = new app.Logger();
                                        ArrayList<String> info_lines = new ArrayList();
                                        info_lines.add(TAG + ", signed invoice: " + invoice_name + " sending failed.");
                                        log.writeLog(invoice_name, info_lines);
                                    } else {
                                        System.out.println(TAG + ", failed to update sent status in db for: " + invoice_name);
                                    }
                                    //End this loop & move to the next email
                                    break;
                                }
                            }
                        }//End check while loop
                    }

                } else {
                    System.out.println(TAG + ", no email sending responses to check.");

                }
                return total_responses_received;
            }

        }

    }

    class CheckLicenceTask implements Runnable {

        String TAG = "CheckLicenceTask";

        @Override
        public void run() {
            while (stop_background_tasks == false) {
                check_licence();
                //Sleep for a day
                long half_a_day = 24 * 60 * 60 * 1000;
                try {
                    Thread.sleep(half_a_day);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BackgroundWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

    }

    void check_licence() {
        //Get licensing info
        Config.CURRENT_MACHINE_INFO = SystemInit.get_system_info();
        if (Config.CURRENT_MACHINE_INFO != null && Config.CURRENT_MACHINE_INFO.size() > 0) {
            //Read the saved machine info
            Config.SAVED_MACHINE_INFO = (HashMap<String, String>) SystemInit.deserialize_object_from_file("aclin");
            if (Config.SAVED_MACHINE_INFO != null && Config.SAVED_MACHINE_INFO.size() > 0) {
                //Compare the parameters
                if (Config.CURRENT_MACHINE_INFO.get("OS_Type").equals(Config.SAVED_MACHINE_INFO.get("OS_Type"))) {
                    if (Config.CURRENT_MACHINE_INFO.get("UUID").equals(Config.SAVED_MACHINE_INFO.get("UUID"))) {
                        //Same machine detected, check the license expiry status
                        LocalDate current_date = SystemInit.string_to_localdate(Config.CURRENT_MACHINE_INFO.get("CURRENT_DATE"));
                        LocalDate expiry_date = SystemInit.string_to_localdate(Config.SAVED_MACHINE_INFO.get("DATE_OF_EXPIRY"));
                        LocalDate reminder_date = SystemInit.string_to_localdate(Config.SAVED_MACHINE_INFO.get("DATE_OF_REMINDER"));

                        int result = expiry_date.compareTo(current_date);
                        long days = DAYS.between(current_date, expiry_date);
                        if (result < 0) {
                            System.out.println(days + " days after exiry date..");
                            Main.run_in_manual_mode(1);
                        } else if (result > 0) {
                            System.out.println(days + " days before exiry date..");
                            if (check_if_today_is_reminder_date(reminder_date, current_date)) {
                                //Send reminder
                                String subject = "License Renewal Reminder";
                                String body = "Your license subscription for this application expires on (" + expiry_date + ").\nPlease contact " + Config.SALES_ADDRESS + " to make arrangements for the renewal.\n\nKind Regards, Support.";
                                String sending_status = SendMail.send_with_tls(Config.ADMIN_ADDRESS, subject, body, null, null);
                                if (sending_status.equalsIgnoreCase("success")) {
                                    System.out.println(TAG + ", License subscription reminder sent to: " + Config.ADMIN_ADDRESS);
                                } else {
                                    System.out.println(TAG + ", Failed to send license subscription reminder to: " + Config.ADMIN_ADDRESS);
                                }
                            }
                        } else if (result == 0) {
                            System.out.println("Now is exiry date..");
                        }

                        if (Config.SAVED_MACHINE_INFO.get("Status").equalsIgnoreCase("registered active")) {
                            //Run app in normal mode
                            Config.LICENCE_MODE = 1;
                        } else {
                            //App needs activation
                            Config.LICENCE_MODE = 0;
                            Main.run_in_manual_mode(1);
                        }

                    } else {
                        //Different UUID detected from activated type i.e re-installation detected with same OS type
                        System.out.println("OS re-installation detected.");
                        //Set activation status
                        //Config.CURRENT_MACHINE_INFO.put("Status", "inactive");
                        //Read and save this machines' details
                        if (SystemInit.serialize_object_to_file("aclin", Config.CURRENT_MACHINE_INFO)) {
                            System.out.println("Re-installation licence info saved.");
                            System.out.println(Config.CURRENT_MACHINE_INFO);
                            //Assign the current info to the saved info map
                            Config.SAVED_MACHINE_INFO = Config.CURRENT_MACHINE_INFO;
                            //Show license verification/activation screen
                            Config.LICENCE_MODE = 2;
                            Main.run_in_manual_mode(1);
                        } else {
                            System.out.println("Failed to save re-installation licence info.");
                        }
                    }
                } else {
                    //Different OS detected from activated type i.e new installation detected
                    System.out.println("Different OS detected from activated type.");
                    //Set activation status
                    Config.CURRENT_MACHINE_INFO.put("Status", "inactive");
                    //Read and save this machines' details
                    if (SystemInit.serialize_object_to_file("aclin", Config.CURRENT_MACHINE_INFO)) {
                        System.out.println("Fresh installation licence info saved.");
                        System.out.println(Config.CURRENT_MACHINE_INFO);
                        //Assign the current info to the saved info map
                        Config.SAVED_MACHINE_INFO = Config.CURRENT_MACHINE_INFO;
                        //Show license verification/activation screen
                        Config.LICENCE_MODE = 3;
                        Main.run_in_manual_mode(1);
                    } else {
                        System.out.println("Failed to save fresh installation licence info.");

                    }
                }
            } else {
                //Failed to read current machine info i.e new installation detected
                System.out.println("Failed to read saved machine info.");
                //Set activation status
                Config.CURRENT_MACHINE_INFO.put("Status", "inactive");
                //Read and save this machines' details
                if (SystemInit.serialize_object_to_file("aclin", Config.CURRENT_MACHINE_INFO)) {
                    System.out.println("New installation licence info saved.");
                    System.out.println(Config.CURRENT_MACHINE_INFO);
                    //Assign the current info to the saved info map
                    Config.SAVED_MACHINE_INFO = Config.CURRENT_MACHINE_INFO;
                    //Show license verification/activation screen
                    Config.LICENCE_MODE = 4;
                    Main.run_in_manual_mode(1);
                } else {
                    System.out.println("Failed to save new installation licence info.");
                }
            }
        } else {
            //Failed to read current machine info
            System.out.println("Failed to read current machine info.");
            Config.LICENCE_MODE = 5;
            Main.run_in_manual_mode(1);
        }
    }

    boolean check_if_today_is_reminder_date(LocalDate reminder_date, LocalDate current_date) {
        //Check if today is reminder date
        if (reminder_date.compareTo(current_date) == 0) {
            //Send reminder

            return true;
        } else {
            return false;
        }
    }

}

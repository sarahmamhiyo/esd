/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoicetypeutils;

import app.Config;
import app.Logger;
import dbutils.DbManager;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import pdfutils.PRNGenerator;
import pdfutils.PdfManager;

/**
 *
 * @author Tinashe
 */
public class FilesScanner {

    static String TAG = "FilesScanner";

    public static void main(String[] args) {
        /*
         ArrayList<File> files_found = check_for_new_sap_invoices();
         if (files_found != null) {
         System.out.println(files_found.size() + " new SAP invoices found");
         } else {
         System.out.println("No new SAP invoices found.");
         }
        
         ArrayList<File> receipts_found = check_for_new_html_receipts();
         if (receipts_found != null) {
         System.out.println(receipts_found.size() + " new HTML receipts found");
         } else {
         System.out.println("No new HTML receipts found.");
         }
         */
    }

    public ArrayList<File> check_for_new_sap_invoices() {
        // try-catch block to handle exceptions
        try {
            File f = new File(Config.SAP_INVOICES_PATH);

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    // We want to find only .c files
                    return name.endsWith(".pdf");
                }
            };

            File[] files = f.listFiles(filter);

            if (files != null && files.length > 0) {
                // Get the names of the files & rename them as seen
                ArrayList<File> files_found_and_renamed = new ArrayList();
                //Create db manager object
                DbManager db_manager = new DbManager();
                for (int i = 0; i < files.length; i++) {
                    //Check if this file is an invoice
                    if (check_if_its_invoice(files[i].getAbsolutePath())) {
                        String file_name = files[i].getName();
                        System.out.println(file_name);
                        //Check if this is not a duplicate invoice
                        if (db_manager.check_for_duplicate_invoice(file_name) == false) {
                            //Rename the the file with .sen extension
                            String seen_file_pathname = files[i].getAbsolutePath().replace(".pdf", ".sen");
                            //Create a seen file
                            File seen_file = new File(seen_file_pathname);

                            //Check if sen file already exeists
                            if (seen_file.exists()) {
                                //Generate a new sen file name
                                int suffix = 1;
                                String suffix_str = "_" + suffix;
                                seen_file_pathname = seen_file_pathname.replace(".sen", "") + suffix_str + ".sen";
                                seen_file = new File(seen_file_pathname);
                                while (seen_file.exists()) {
                                    suffix_str = "";
                                    suffix++;
                                    suffix_str = "_" + suffix;
                                    seen_file_pathname = files[i].getAbsolutePath().replace(".pdf", ".sen");
                                    seen_file_pathname = seen_file_pathname.replace(".sen", "") + suffix_str + ".sen";
                                    seen_file = new File(seen_file_pathname);
                                    suffix_str = "";
                                }
                            }

                            //Perform the rename op
                            if (files[i].renameTo(seen_file)) {
                                //Rename successful, add to files_found_and_renamed array & db
                                files_found_and_renamed.add(seen_file);
                                //Save the file_name in db
                                if (!db_manager.insert_invoice(file_name, "no set")) {
                                    String error_msg = TAG + ", Failed to save new SAP invoice in db: " + file_name;
                                    System.out.println(error_msg);
                                }
                            } else {
                                //Failed to rename file, maybe its in use somewhere, skip it
                                //Will try it again on next scan
                                System.out.println("Failed to rename file as seen: " + file_name);
                            }
                        } else {
                            //Submit the earlier invoice for re-sending
                            if (db_manager.reset_invoice_sent_status(file_name, 0)) {
                                //Rename the duplicate file with .dup extension
                                String duplicate_file_pathname = files[i].getAbsolutePath().replace(".pdf", ".dup");
                                //Create a dup file
                                File duplicate_file = new File(duplicate_file_pathname);
                                //Check if the duplicate file already exists
                                if (duplicate_file.exists()) {
                                    //Generate a new duplicate name
                                    int suffix = 1;
                                    String suffix_str = "_" + suffix;
                                    duplicate_file_pathname = duplicate_file_pathname.replace(".dup", "") + suffix_str + ".dup";
                                    duplicate_file = new File(duplicate_file_pathname);
                                    while (duplicate_file.exists()) {
                                        suffix_str = "";
                                        suffix++;
                                        suffix_str = "_" + suffix;
                                        duplicate_file_pathname = files[i].getAbsolutePath().replace(".pdf", ".dup");
                                        duplicate_file_pathname = duplicate_file_pathname.replace(".dup", "") + suffix_str + ".dup";
                                        duplicate_file = new File(duplicate_file_pathname);
                                        suffix_str = "";
                                    }
                                }
                                //Perform the rename op
                                if (files[i].renameTo(duplicate_file)) {
                                    //Rename successful, skip this file
                                } else {
                                    //Failed to rename file, maybe its in use somewhere, skip it
                                    //Will try it again on next scan
                                    System.out.println(TAG + ", Failed to rename file as duplicate: " + file_name);
                                }
                            } else {
                                //Failed to submit duplicate invoice for re-sending
                                System.out.println(TAG + ", Failed to submit duplicate invoice for re-sending: " + file_name);
                            }
                        }
                    } else {
                        //Rename the non-invoice file with .not extension
                        String non_invoice_file_pathname = files[i].getAbsolutePath().replace(".pdf", ".not");
                        //Create a not file
                        File non_invoice_file = new File(non_invoice_file_pathname);

                        if (non_invoice_file.exists()) {
                            //Generate a new duplicate name
                            int suffix = 1;
                            String suffix_str = "_" + suffix;
                            non_invoice_file_pathname = non_invoice_file_pathname.replace(".not", "") + suffix_str + ".not";
                            non_invoice_file = new File(non_invoice_file_pathname);
                            while (non_invoice_file.exists()) {
                                suffix_str = "";
                                suffix++;
                                suffix_str = "_" + suffix;
                                non_invoice_file_pathname = files[i].getAbsolutePath().replace(".pdf", ".not");
                                non_invoice_file_pathname = non_invoice_file_pathname.replace(".not", "") + suffix_str + ".not";
                                non_invoice_file = new File(non_invoice_file_pathname);
                                suffix_str = "";
                            }
                        }
                        //Perform the rename op
                        if (files[i].renameTo(non_invoice_file)) {
                            //Rename successful, skip this file
                        } else {
                            //Failed to rename file, maybe its in use somewhere, skip it
                            //Will try it again on next scan
                            System.out.println("Failed to rename file as not invoice: " + files[i].getName());
                        }
                    }
                }

                //Close the db
                //db_manager.close();
                return files_found_and_renamed;
            } else {
                return null;
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<File> check_for_new_sap_invoices_new() {
        // try-catch block to handle exceptions
        try {
            File f = new File(Config.SAP_INVOICES_PATH);

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    // We want to find only .c files
                    return name.endsWith(".pdf");
                }
            };

            File[] files = f.listFiles(filter);

            if (files != null && files.length > 0) {
                // Get the names of the files & rename them as seen
                ArrayList<File> files_found_and_renamed = new ArrayList();
                //Create db manager object
                DbManager db_manager = new DbManager();
                for (int i = 0; i < files.length; i++) {
                    //Check if this file is an invoice
                    if (check_if_its_invoice(files[i].getAbsolutePath())) {
                        String file_name = files[i].getName();
                        System.out.println(file_name);
                        //Extract invoice data for duplicate checking
                        HashMap<String, String> invoice_data = extract_invoice_data(file_name);
                        if (invoice_data != null) {
                            //Check if this is not a duplicate invoice
                            HashMap<String, String> existing_invoice_data = db_manager.check_for_duplicate_invoice_by_data(invoice_data);
                            if (existing_invoice_data == null) {
                                System.out.println(TAG + ", this is not a duplicate invoice: " + file_name);
                                //Invoice not duplicate, rename the the file with .sen extension
                                File seen_file = rename_invoice(files[i], ".sen");
                                //Perform the rename op
                                if (files[i].renameTo(seen_file)) {
                                    //Rename successful, add to files_found_and_renamed array & db
                                    files_found_and_renamed.add(seen_file);
                                    //Save the file_name in db
                                    if (!db_manager.insert_invoice(file_name, "no set")) {
                                        String error_msg = TAG + ", Failed to save new SAP invoice in db: " + file_name;
                                        System.out.println(error_msg);
                                    }
                                } else {
                                    //Failed to rename file, maybe its in use somewhere, skip it
                                    //Will try it again on next scan
                                    System.out.println("Failed to rename file as seen: " + file_name);
                                }
                            } else {
                                System.out.println(TAG + ", this is a duplicate invoice: " + file_name);
                                System.out.println(TAG + ", its duplicate to invoice: " + existing_invoice_data.get("filename"));
                                //Check if the existing duplicate is signed
                                String signature = existing_invoice_data.get("signature");
                                System.out.println(TAG + ", existing signature: " + signature);
                                if (signature.equalsIgnoreCase("none")) {
                                    //Delete this unsigned invoice 
                                    if (db_manager.delete_duplicate_unsigned_invoice(existing_invoice_data.get("filename"))) {
                                        //Delete the prn file for this unsigned invoice
                                        boolean prn_file_deleted = false;
                                        File prn_file = new File(Config.PRN_FILES_PATH + existing_invoice_data.get("filename").replace(".pdf", ".prn"));
                                        System.out.println(TAG + ", ZWL PRN file path to delete: " + prn_file.getAbsolutePath());
                                        //Check if the PRN file exists in ZWL folder
                                        if (prn_file.exists()) {
                                            //Delete it
                                            if (!prn_file.delete()) {
                                                System.out.println(TAG + ", Failed to delete ZWL PRN file path: " + prn_file.getAbsolutePath());
                                            } else {
                                                prn_file_deleted = true;
                                                System.out.println(TAG + ", Deleted ZWL PRN file path: " + prn_file.getAbsolutePath());
                                            }
                                        } else {
                                            prn_file = new File(Config.USD_PRN_FILES_PATH + existing_invoice_data.get("filename").replace(".pdf", ".prn"));
                                            System.out.println(TAG + ", USD PRN file path to delete: " + prn_file.getAbsolutePath());
                                            if (prn_file.exists()) {
                                                //Delete it
                                                if (!prn_file.delete()) {
                                                    System.out.println(TAG + ", Failed to delete USD PRN file path: " + prn_file.getAbsolutePath());
                                                } else {
                                                    prn_file_deleted = true;
                                                    System.out.println(TAG + ", Deleted USD PRN file path: " + prn_file.getAbsolutePath());
                                                }
                                            } else {
                                                prn_file_deleted = true;
                                                System.out.println(TAG + ", ZWL or USD PRN file doesnt exist: " + prn_file.getAbsolutePath());
                                            }
                                        }

                                        //Check PRN file deleted
                                        if (prn_file_deleted) {
                                            //Insert this new invoice for processing
                                            File seen_file = rename_invoice(files[i], ".sen");
                                            //Perform the rename op
                                            if (files[i].renameTo(seen_file)) {
                                                //Rename successful, add to files_found_and_renamed array & db
                                                files_found_and_renamed.add(seen_file);
                                                //Save the file_name in db
                                                if (!db_manager.insert_invoice(file_name, "no set")) {
                                                    String error_msg = TAG + ", Failed to save new SAP invoice in db: " + file_name;
                                                    System.out.println(error_msg);
                                                }
                                            } else {
                                            //Failed to rename file, maybe its in use somewhere, skip it
                                                //Will try it again on next scan
                                                System.out.println("Failed to rename file as seen: " + file_name);
                                            }
                                        } else {
                                            //Failed to delete PRN file
                                            System.out.println(TAG + ", failed to delete PRN for unsigned existing duplicate invoice : " + existing_invoice_data.get("filename"));
                                        }
                                    } else {
                                        System.out.println(TAG + ", failed to delete unsigned existing duplicate invoice: " + existing_invoice_data.get("filename"));
                                    }
                                } else {
                                    //Rename the duplicate file with .dup extension
                                    File duplicate_file = rename_invoice(files[i], ".dup");
                                    //Perform the rename op
                                    if (files[i].renameTo(duplicate_file)) {
                                        //Sign and send this invoice
                                        String invoice_name = duplicate_file.getName();
                                        InvoiceSigner signer = new InvoiceSigner();
                                        boolean sign_status = signer.sign_invoice(invoice_name, signature);
                                        if (sign_status) {
                                            //Submit this new signed invoice for sending
                                            if (db_manager.insert_duplicate_signed_invoice_for_sending(file_name, invoice_data.get("recepient"))) {
                                                System.out.println(TAG + ", Signed & submitted for re-sending (" + file_name + ").");
                                            } else {
                                                System.out.println(TAG + ", Failed to submit for re-sending (" + file_name + ").");
                                            }
                                        } else {
                                            System.out.println(TAG + "Failed to sign duplicate invoice (" + file_name + ").");
                                        }
                                    } else {
                                        //Failed to rename file, maybe its in use somewhere, skip it
                                        //Will try it again on next scan
                                        System.out.println(TAG + ", Failed to rename file as duplicate: " + file_name);
                                    }
                                }

                            }
                        } else {
                            //Failed to extract invoice data
                            System.out.println(TAG + ", Failed to extract invoice data for duplicate checking: " + file_name);
                        }
                    } else {
                        //Rename the non-invoice file with .not extension
                        String non_invoice_file_pathname = files[i].getAbsolutePath().replace(".pdf", ".not");
                        //Create a not file
                        File non_invoice_file = new File(non_invoice_file_pathname);

                        if (non_invoice_file.exists()) {
                            //Generate a new duplicate name
                            int suffix = 1;
                            String suffix_str = "_" + suffix;
                            non_invoice_file_pathname = non_invoice_file_pathname.replace(".not", "") + suffix_str + ".not";
                            non_invoice_file = new File(non_invoice_file_pathname);
                            while (non_invoice_file.exists()) {
                                suffix_str = "";
                                suffix++;
                                suffix_str = "_" + suffix;
                                non_invoice_file_pathname = files[i].getAbsolutePath().replace(".pdf", ".not");
                                non_invoice_file_pathname = non_invoice_file_pathname.replace(".not", "") + suffix_str + ".not";
                                non_invoice_file = new File(non_invoice_file_pathname);
                                suffix_str = "";
                            }
                        }
                        //Perform the rename op
                        if (files[i].renameTo(non_invoice_file)) {
                            //Rename successful, skip this file
                        } else {
                            //Failed to rename file, maybe its in use somewhere, skip it
                            //Will try it again on next scan
                            System.out.println("Failed to rename file as not invoice: " + files[i].getName());
                        }
                    }
                }

                //Close the db
                //db_manager.close();
                return files_found_and_renamed;
            } else {
                return null;
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public File rename_invoice(File f, String extension) {
        String new_file_pathname = f.getAbsolutePath().replace(".pdf", extension);
        //Create a new file
        File new_file = new File(new_file_pathname);

        //Check if new_file already exeists
        if (new_file.exists()) {
            //Generate a new_file name
            int suffix = 1;
            String suffix_str = "_" + suffix;
            new_file_pathname = new_file_pathname.replace(extension, "") + suffix_str + extension;
            new_file = new File(new_file_pathname);
            while (new_file.exists()) {
                suffix_str = "";
                suffix++;
                suffix_str = "_" + suffix;
                new_file_pathname = f.getAbsolutePath().replace(".pdf", extension);
                new_file_pathname = new_file_pathname.replace(extension, "") + suffix_str + extension;
                new_file = new File(new_file_pathname);
                suffix_str = "";
            }
        }

        return new_file;
    }

    public HashMap<String, String> extract_invoice_data(String file_name) {
        PRNGenerator prn_generator = new PRNGenerator();
        PdfManager pdf_manager = new PdfManager();
        int page_count = pdf_manager.get_number_of_pages(Config.SAP_INVOICES_PATH + file_name);
        System.out.println(TAG + ", extract_invoice_data(): file_name: " + file_name + ", page_count: " + page_count);
        HashMap<String, String> invoice_data_values = prn_generator.extract_invoice_data(file_name, page_count);
        if (invoice_data_values != null) {
            System.out.println(TAG + " extract_invoice_data(): invoice_data_values:");
            System.out.println(invoice_data_values);
            //Create the data for duplicate checking 
            String invoice_to = (String) invoice_data_values.get("Invoice To");
            invoice_to = invoice_to.trim();
            String invoice_number = (String) invoice_data_values.get("Invoice Number");
            invoice_number = invoice_number.trim();
            String grand_total = (String) invoice_data_values.get("Grand Total");
            grand_total = grand_total.trim();

            HashMap<String, String> invoice_data = new HashMap();
            //Get the invoice to
            invoice_data.put("invoice_to", invoice_to);
            //Get the invoice number
            invoice_data.put("invoice_number", invoice_number);
            //Get the Grand Total
            invoice_data.put("grand_total", grand_total);
            //Add file name
            //invoice_data.put("file_name", file_name);
            //Get the recepient 
            String recepient = invoice_data_values.get("User Email");
            invoice_data.put("recepient", recepient);

            return invoice_data;
        } else {
            return null;
        }
    }

    public boolean check_if_its_invoice(String file_path_name) {
        //Get page count for this file
        PdfManager pdf_manager = new PdfManager();
        int page_count = pdf_manager.get_number_of_pages(file_path_name);
        if (page_count > 0) {
            String pageText = pdf_manager.get_page_text(1, file_path_name);
            if (pageText != null) {
                if (pageText.contains("Fiscal Tax Invoice")
                        || pageText.contains("FISCAL TAX INVOICE")
                        || pageText.contains("INVOICE")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                System.out.println(TAG + ", Page 1 is empty for document:");
                System.out.println(file_path_name);
                return false;
            }
        } else {
            System.out.println(TAG + ", This document is empty:");
            System.out.println(file_path_name);
            return false;
        }
    }

    public ArrayList<File> check_for_new_html_receipts() {
        // try-catch block to handle exceptions
        try {
            File f = new File(Config.HTML_RECEIPTS_PATH);

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    // We want to find only .c files
                    return name.endsWith(".html");
                }
            };

            File[] files = f.listFiles(filter);

            if (files != null && files.length > 0) {
                // Get the names of the files & rename them as seen
                ArrayList<File> files_found_and_renamed = new ArrayList();
                //Create db manager object
                DbManager db_manager = new DbManager();
                for (int i = 0; i < files.length; i++) {
                    String file_name = files[i].getName();
                    System.out.println(file_name);
                    //Check if this is not a duplicate invoice
                    if (db_manager.check_for_duplicate_invoice(file_name) == false) {
                        //Rename the the file with .sen extension
                        String seen_file_pathname = files[i].getAbsolutePath().replace(".html", ".sen");
                        //Create a seen file
                        File seen_file = new File(seen_file_pathname);

                        //Check if seen_file already exists
                        if (seen_file.exists()) {
                            int suffix = 1;
                            String suffix_str = "_" + suffix;
                            seen_file_pathname = seen_file_pathname.replace(".sen", "") + suffix_str + ".sen";
                            seen_file = new File(seen_file_pathname);
                            while (seen_file.exists()) {
                                suffix_str = "";
                                suffix++;
                                suffix_str = "_" + suffix;
                                seen_file_pathname = files[i].getAbsolutePath().replace(".html", ".sen");
                                seen_file_pathname = seen_file_pathname.replace(".sen", "") + suffix_str + ".sen";
                                seen_file = new File(seen_file_pathname);
                                suffix_str = "";
                            }
                        }

                        //Perform the rename op
                        if (files[i].renameTo(seen_file)) {
                            //Rename successful, add to files_found_and_renamed array & db
                            files_found_and_renamed.add(seen_file);
                            //Save the file_name in db
                            if (!db_manager.insert_invoice(file_name, "no set")) {
                                String error_msg = TAG + ", Failed to save new SAP invoice in db: " + file_name;
                                System.out.println(error_msg);
                            }
                        } else {
                            //Failed to rename file, maybe its in use somewhere, skip it
                            //Will try it again on next scan
                            System.out.println("Failed to rename file as seen: " + file_name);
                        }
                    } else {
                        //Rename the duplicate file with .dup extension
                        String duplicate_file_pathname = files[i].getAbsolutePath().replace(".html", ".dup");
                        //Create a dup file
                        File duplicate_file = new File(duplicate_file_pathname);

                        //Check if the duplicate file already exists
                        if (duplicate_file.exists()) {
                            //Generate a new duplicate name
                            int suffix = 1;
                            String suffix_str = "_" + suffix;
                            duplicate_file_pathname = duplicate_file_pathname.replace(".dup", "") + suffix_str + ".dup";
                            duplicate_file = new File(duplicate_file_pathname);
                            while (duplicate_file.exists()) {
                                suffix_str = "";
                                suffix++;
                                suffix_str = "_" + suffix;
                                duplicate_file_pathname = files[i].getAbsolutePath().replace(".html", ".dup");
                                duplicate_file_pathname = duplicate_file_pathname.replace(".dup", "") + suffix_str + ".dup";
                                duplicate_file = new File(duplicate_file_pathname);
                                suffix_str = "";
                            }
                        }

                        //Perform the rename op
                        if (files[i].renameTo(duplicate_file)) {
                            //Rename successful, skip this file
                        } else {
                            //Failed to rename file, maybe its in use somewhere, skip it
                            //Will try it again on next scan
                            System.out.println("Failed to rename file as duplicate: " + file_name);
                        }
                    }

                }

                //Close the db
                //db_manager.close();
                return files_found_and_renamed;
            } else {
                return null;
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static ArrayList<File> check_for_new_html_receipts_old() {
        // try-catch block to handle exceptions
        try {
            File f = new File(Config.HTML_RECEIPTS_PATH);

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    // We want to find only .html files
                    return name.endsWith(".html");
                }
            };
            File[] files = f.listFiles(filter);
            // Get the names of the files & rename them as seen
            ArrayList<File> files_found_and_renamed = new ArrayList();
            for (int i = 0; i < files.length; i++) {
                String file_name = files[i].getName();
                System.out.println(file_name);
                //Rename the the file with .sen extension
                String seen_file_pathname = files[i].getAbsolutePath().replace(".html", ".sen");
                //Create a seen file
                File seen_file = new File(seen_file_pathname);
                //Perform the rename op
                if (files[i].renameTo(seen_file)) {
                    //Rename successful, add to files_found_and_renamed array & db
                    files_found_and_renamed.add(seen_file);
                } else {
                    //Failed to rename file, maybe its in use somewhere, skip it
                    //Will try it again on next scan
                }

            }
            return files_found_and_renamed;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoicetypeutils;

import app.Config;
import dbutils.DbManager;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
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

    public boolean check_if_its_invoice(String file_path_name) {
        //Get page count for this file
        PdfManager pdf_manager = new PdfManager();
        int page_count = pdf_manager.get_number_of_pages(file_path_name);
        if (page_count > 0) {
            String pageText = pdf_manager.get_page_text(1, file_path_name);
            if (pageText != null) {
                if (pageText.contains("Fiscal Tax Invoice")
                        || pageText.contains("FISCAL TAX INVOICE")
                        || pageText.contains("INVOICE")
                        ) {
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

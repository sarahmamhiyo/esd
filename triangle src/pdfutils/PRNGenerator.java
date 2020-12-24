/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfutils;

import app.Config;
import dbutils.DbManager;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Tinashe
 */
public class PRNGenerator {

    String TAG = "PRNGenerator";

    public HashMap<String, String> extract_invoice_data(String file_name, int page_count) {
        HashMap<String, String> extracted_data_values = null;
        String file_path_name = Config.SAP_INVOICES_PATH + file_name;
        //Chech the document orientation
        PdfManager pdf_manager = new PdfManager();
        String orientation = pdf_manager.get_page_orientation(file_path_name);
        if (orientation != null) {
            System.out.println(TAG + ": preparePRNData(): document orientation: " + orientation);
            if (orientation.equalsIgnoreCase("landscape")) {
                //Check if doc is single or multi page
                if (page_count == 1) {
                    //Extract data from a single page landscape invoice
                    extracted_data_values = extract_single_page_data("landscape invoice", file_path_name, page_count);
                } else if (page_count > 1) {
                    //Extract data from a multi page landscape invoice
                    extracted_data_values = extract_multi_page_data("landscape multipage", file_path_name, page_count);
                }
            } else if (orientation.equalsIgnoreCase("portrait")) {
                //Check if doc is single or multi page
                if (page_count == 1) {
                    //Extract data from a single page portrait invoice
                    extracted_data_values = extract_single_page_data("portrait invoice", file_path_name, page_count);
                } else if (page_count > 1) {
                    //Extract data from a multi page portrait invoice
                    extracted_data_values = extract_multi_page_data("portrait multipage", file_path_name, page_count);
                }
            }
        } else {
            //Failed to get orientation
            String msg = TAG + ": preparePRNData(): Failed to get orientation for file: " + file_name;
            Config.DATA_EXTRACTION_ERRORS.put("Error #52", msg);
            System.out.println(msg);
        }

        return extracted_data_values;
    }

    HashMap<String, String> extract_single_page_data(String template, String file_path_name, int page_number) {
        HashMap<String, String> extracted_data_values = new HashMap();
        //Add invoice type at first position in extracted_data_values
        extracted_data_values.put("invoice_type", template);
        //Get the template bounderies
        JSONObject invoice_type_object = (JSONObject) Config.INVOICE_DATA_BOUNDERIES.get(template);
        String status_msg = TAG + ": extract_single_page_data(): Template bounderies:";
        System.out.println(status_msg);
        System.out.println(invoice_type_object);
        //Get the require fields
        PdfManager pdf_manager = new PdfManager();
        for (Object key : invoice_type_object.keySet()) {
            String required_data_field = (String) key;
            JSONArray data_field_tags = (JSONArray) invoice_type_object.get(key);
            String start_tag = (String) data_field_tags.get(0);
            String end_tag = (String) data_field_tags.get(1);

            //Get the pageText 
            String pageText = pdf_manager.get_page_text(page_number, file_path_name);
            if (pageText != null) {
                //Extract the data 
                DataExtractor data_extractor = new DataExtractor();
                String extracted_value = data_extractor.extract_data(pageText, start_tag, end_tag);
                if (!extracted_value.contains("Error #")) {
                    //Add to extracted_data_values
                    extracted_data_values.put(required_data_field, extracted_value);
                    //For protrait invoice type extract currency from line items
                    if (template.contains("portrait")) {
                        if (required_data_field.contains("Line items")) {
                            String currency = "ZWL";
                            if (extracted_value.contains("ZWL")) {
                                currency = "ZWL";
                            }
                            if (extracted_value.contains("USD")) {
                                currency = "USD";
                            }

                            //Add to extracted_data_values
                            extracted_data_values.put("Currency", currency);
                        }
                    }
                } else {
                    //Failed to get page text
                    String msg = TAG + ": extract_single_page_data(): Failed to extract (" + required_data_field + ") value from page text for page (" + page_number + ") in file: " + file_path_name;
                    Config.DATA_EXTRACTION_ERRORS.put("Error #51", msg);
                    System.out.println(msg);
                }
            } else {
                //Failed to get page text
                String msg = TAG + ": extract_single_page_data(): Failed to get page text for page (" + page_number + ") in file: " + file_path_name;
                Config.DATA_EXTRACTION_ERRORS.put("Error #50", msg);
                System.out.println(msg);
            }

        }

        return extracted_data_values;

    }

    HashMap<String, String> extract_multi_page_data(String template, String file_path_name, int page_count) {
        HashMap<String, String> extracted_data = new HashMap();
        //Check which templates to use
        if (page_count == 2) {
            //Document has only first & last page
            for (int i = 0; i < page_count; i++) {
                if (i == 0) {
                    extracted_data = get_page_data(template + " first", file_path_name, 1, extracted_data);
                    System.out.println(TAG + ": extract_multi_page_data(): first: ");
                    System.out.println(extracted_data);
                } else if (i == 1) {
                    extracted_data = get_page_data(template + " last", file_path_name, 2, extracted_data);
                    System.out.println(TAG + ": extract_multi_page_data(): last: ");
                    System.out.println(extracted_data);
                }
            }
        } else if (page_count > 2) {
            //Document has first, middle & last pages
            for (int i = 0; i < page_count; i++) {
                if (i == 0) {
                    extracted_data = get_page_data(template + " first", file_path_name, 1, extracted_data);
                } else if (i > 0 && i < (page_count - 1)) {
                    extracted_data = get_page_data(template + " middle", file_path_name, i + 1, extracted_data);
                } else if (i == (page_count - 1)) {
                    extracted_data = get_page_data(template + " last", file_path_name, i + 1, extracted_data);
                }
            }
        }
        return extracted_data;
    }

    HashMap<String, String> get_page_data(String page_template, String file_path_name, int page_number, HashMap<String, String> extracted_data_values) {
        if (page_number == 1) {
            //Create the hashmap where cumulative data will be put from multiple pages
            extracted_data_values = new HashMap();
            //Add invoice type at first position in extracted_data_values
            extracted_data_values.put("invoice_type", page_template.replace(" first", ""));

        }
        //Get the template bounderies
        JSONObject invoice_type_object = (JSONObject) Config.INVOICE_DATA_BOUNDERIES.get(page_template);

        PdfManager pdf_manager = new PdfManager();
        for (Object key : invoice_type_object.keySet()) {
            String required_data_field = (String) key;
            JSONArray data_field_tags = (JSONArray) invoice_type_object.get(key);
            String start_tag = (String) data_field_tags.get(0);
            String end_tag = (String) data_field_tags.get(1);

            //Get the pageText 
            String pageText = pdf_manager.get_page_text(page_number, file_path_name);
            if (pageText != null) {
                //Extract the data 
                DataExtractor data_extractor = new DataExtractor();
                String extracted_value = data_extractor.extract_data(pageText, start_tag, end_tag);
                if (!extracted_value.contains("Error #") && extracted_value != null) {
                    //Add to extracted_data_values
                    System.out.println(TAG + ":  get_page_data(): extracted_value for page_number: " + page_number + " , required_data_field: " + required_data_field);
                    System.out.println(extracted_value);
                    if (required_data_field.equalsIgnoreCase("Line items")) {
                        if (page_number == 1) {
                            extracted_data_values.put(required_data_field, extracted_value.trim());
                            //For protrait invoice type extract currency from line items
                            if (page_template.contains("portrait")) {
                                if (required_data_field.contains("Line items")) {
                                    String currency = "ZWL";
                                    if (extracted_value.contains("ZWL")) {
                                        currency = "ZWL";
                                    }
                                    if (extracted_value.contains("USD")) {
                                        currency = "USD";
                                    }
                                    //Add to extracted_data_values
                                    extracted_data_values.put("Currency", currency);
                                }
                            }
                        } else if (page_number > 1) {
                            //Join these line items with those from previous page
                            String previous_line_items = extracted_data_values.get(required_data_field);
                            String new_line_items = previous_line_items + "\n" + extracted_value.trim();
                            //Add back to main hash map
                            extracted_data_values.put(required_data_field, new_line_items);
                        }
                    } else {
                        //Other fields not line items
                        extracted_data_values.put(required_data_field, extracted_value);
                    }
                } else {
                    //Failed to get page text
                    String msg = TAG + ": get_page_data(): Failed to extract (" + required_data_field + ") value from page text for page (" + page_number + ") in file: " + file_path_name;
                    Config.DATA_EXTRACTION_ERRORS.put("Error #54", msg);
                    System.out.println(msg);
                }
            } else {
                //Failed to get page text
                String msg = TAG + ": get_page_data(): Failed to get page text for page (" + page_number + ") in file: " + file_path_name;
                Config.DATA_EXTRACTION_ERRORS.put("Error #53", msg);
                System.out.println(msg);
            }
        }
        return extracted_data_values;
    }

    public HashMap<String, HashMap<Integer, ArrayList<String>>> generatePRNData(HashMap<String, String> invoice_data_values) {
        HashMap<String, HashMap<Integer, ArrayList<String>>> prn_data = new HashMap();
        String invoice_type = invoice_data_values.get("invoice_type");
        //Loop through invoice_data_values getting values
        for (Object key : invoice_data_values.keySet()) {
            String field_name = (String) key;
            System.out.println("generatePRNData(): field_name: " + field_name);
            //Check if this field is not Line items
            if (!field_name.equalsIgnoreCase("Line items")) {
                //Get other fields directly without further processing
                String field_value = invoice_data_values.get(key).trim();
                //Put it in prn_data
                ArrayList<String> field_values_array = new ArrayList();
                field_values_array.add(field_value);
                HashMap<Integer, ArrayList<String>> field_values_map = new HashMap();
                field_values_map.put(0, field_values_array);
                prn_data.put(field_name, field_values_map);
            } else {
                //Line items need extra processing
                String field_value = invoice_data_values.get(key);
                HashMap<Integer, ArrayList<String>> line_items_map = null;
                if (invoice_type.contains("landscape")) {
                    //extract single page landscape invoice line items
                    if (invoice_type.equalsIgnoreCase("landscape invoice")) {
                        line_items_map = process_single_page_landscape_invoice_line_items(field_value);
                    } else if (invoice_type.equalsIgnoreCase("landscape multipage")) {
                        line_items_map = process_multipage_page_landscape_invoice_line_items(field_value);
                    }
                    //System.out.println("generatePRNData(): line_items_map: ");
                    //System.out.println(line_items_map);
                    prn_data.put(field_name, line_items_map);
                } else if (invoice_type.contains("portrait")) {
                    //extract portrait line items
                    if (invoice_type.equalsIgnoreCase("portrait invoice")) {
                        line_items_map = process_portrait_line_items(field_value);
                    } else if (invoice_type.equalsIgnoreCase("portrait multipage")) {
                        line_items_map = process_portrait_line_items(field_value);
                    }
                    prn_data.put(field_name, line_items_map);
                }
            }
        }

        return prn_data;
    }

    HashMap<Integer, ArrayList<String>> process_single_page_landscape_invoice_line_items(String line_items_str) {
        HashMap<Integer, ArrayList<String>> line_items = new HashMap();
        //Explode the string along \n characters
        String[] line_item_entries = line_items_str.trim().split("\n");
        //System.out.println("line_item_entries len: " + line_item_entries.length);
        //Loop through the line_item_entries processing each line item
        for (int entry_index = 0; entry_index < line_item_entries.length; entry_index++) {
            String line_item_entry = line_item_entries[entry_index];
            //Explode line_item_entry into words along space characters
            String[] line_item_entry_words = line_item_entry.split(" ");

            int index_of_ea_field = 0;
            /*
             //Locate index of UOM field
             int index_of_ea_field = 0;
             for (int i = 0; i < line_item_entry_words.length; i++) {
             String current_line_item_entry_word = line_item_entry_words[i];
             for (int u = 0; u < Config.UNIT_OF_MEASUREMENTS.size(); u++) {
             String current_uom_value = Config.UNIT_OF_MEASUREMENTS.get(u);
             if (current_line_item_entry_word.equalsIgnoreCase(current_uom_value)) {
             index_of_ea_field = i;
             }
             }

             }//End loop
             */
            //Get the line item description & line_item_qty
            String line_item_desc = "",
                    line_item_qty = "",
                    unit_price_excl_discount = "",
                    total_value_excl_discount = "",
                    discount_amount = "",
                    discount_percentage = "",
                    total_excl_vat = "",
                    vat = "",
                    total_all_incl = "";
            System.out.println();
            System.out.println(TAG + ", process_single_page_landscape_invoice_line_items(): line_item_entry: ");
            System.out.println(line_item_entry);
            System.out.println();
            System.out.println(TAG + ", process_single_page_landscape_invoice_line_items(): line_item_entry_words.length: " + line_item_entry_words.length);
            System.out.println();
            System.out.println(TAG + ", process_single_page_landscape_invoice_line_items(): line_item_entry_words elements:");
            ArrayList<String> temp_array = new ArrayList();
            for (int i = 0; i < line_item_entry_words.length; i++) {
                if (line_item_entry_words[i].trim().length() > 0) {
                    temp_array.add(line_item_entry_words[i]);
                    System.out.println("Element " + i + ": " + line_item_entry_words[i]);
                }
            }

            if (line_item_entry_words.length != temp_array.size()) {
                //Re-create the line_item_entry_words with non-empty elements only
                line_item_entry_words = new String[temp_array.size()];
                //Copy all elements from temp_array to the new line_item_entry_words
                for (int t = 0; t < temp_array.size(); t++) {
                    line_item_entry_words[t] = temp_array.get(t);
                }

                //Locate index of UOM field
                for (int i = 0; i < line_item_entry_words.length; i++) {
                    String current_line_item_entry_word = line_item_entry_words[i];
                    for (int u = 0; u < Config.UNIT_OF_MEASUREMENTS.size(); u++) {
                        String current_uom_value = Config.UNIT_OF_MEASUREMENTS.get(u);
                        if (current_line_item_entry_word.equalsIgnoreCase(current_uom_value)) {
                            index_of_ea_field = i;
                        }
                    }

                }//End loop

                System.out.println();
                System.out.println(TAG + ", process_single_page_landscape_invoice_line_items(): index_of_ea_field true block: " + index_of_ea_field);
                System.out.println();

                for (int i = 0; i < line_item_entry_words.length; i++) {
                    if (i < (index_of_ea_field - 2)) {
                        line_item_desc = line_item_desc + " " + line_item_entry_words[i];
                        line_item_desc = line_item_desc.replace("#", "");
                    } else if (i == (index_of_ea_field - 1)) {
                        //line_item_qty = line_item_entry_words[i].replace(".", "").replace(",", "");
                        line_item_qty = line_item_entry_words[i].replace(",", "");
                    } else if (i == (index_of_ea_field + 1)) {
                        unit_price_excl_discount = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 2)) {
                        total_value_excl_discount = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 3)) {
                        discount_amount = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 4)) {
                        discount_percentage = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 5)) {
                        total_excl_vat = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 6)) {
                        vat = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 7)) {
                        total_all_incl = line_item_entry_words[i];
                    }

                }
            } else {

                //Locate index of UOM field
                for (int i = 0; i < line_item_entry_words.length; i++) {
                    String current_line_item_entry_word = line_item_entry_words[i];
                    for (int u = 0; u < Config.UNIT_OF_MEASUREMENTS.size(); u++) {
                        String current_uom_value = Config.UNIT_OF_MEASUREMENTS.get(u);
                        if (current_line_item_entry_word.equalsIgnoreCase(current_uom_value)) {
                            index_of_ea_field = i;
                        }
                    }

                }//End loop

                System.out.println();
                System.out.println(TAG + ", process_single_page_landscape_invoice_line_items(): index_of_ea_field else block: " + index_of_ea_field);
                System.out.println();

                for (int i = 0; i < line_item_entry_words.length; i++) {
                    if (i < (index_of_ea_field - 2)) {
                        line_item_desc = line_item_desc + " " + line_item_entry_words[i];
                        line_item_desc = line_item_desc.replace("#", "");
                    } else if (i == (index_of_ea_field - 1)) {
                        line_item_qty = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 1)) {
                        unit_price_excl_discount = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 2)) {
                        total_value_excl_discount = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 3)) {
                        discount_amount = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 4)) {
                        discount_percentage = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 5)) {
                        total_excl_vat = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 6)) {
                        vat = line_item_entry_words[i];
                    } else if (i == (index_of_ea_field + 7)) {
                        total_all_incl = line_item_entry_words[i];
                    }

                }
            }

            System.out.println();
            System.out.println(TAG + ", process_single_page_landscape_invoice_line_items() : Line Item fields extracted for entry_index: " + entry_index);
            System.out.println("line_item_desc: " + line_item_desc);
            System.out.println("line_item_qty: " + line_item_qty);
            System.out.println("unit_price_excl_discount: " + unit_price_excl_discount);
            System.out.println("total_value_excl_discount: " + total_value_excl_discount);
            System.out.println("discount_amount: " + discount_amount);
            System.out.println("discount_percentage: " + discount_percentage);
            System.out.println("total_excl_vat: " + total_excl_vat);
            System.out.println("vat: " + vat);
            System.out.println("total_all_incl: " + total_all_incl);

            //Put the line item entry values in an array list
            ArrayList<String> line_item_entry_values = new ArrayList();
            line_item_entry_values.add(line_item_desc);
            line_item_entry_values.add(line_item_qty);
            line_item_entry_values.add(unit_price_excl_discount);
            line_item_entry_values.add(total_value_excl_discount);
            line_item_entry_values.add(discount_amount);
            line_item_entry_values.add(discount_percentage);
            line_item_entry_values.add(total_excl_vat);
            line_item_entry_values.add(vat);
            line_item_entry_values.add(total_all_incl);

            //Put in line_items
            line_items.put(entry_index, line_item_entry_values);

        }//End of main loop

        return line_items;
    }

    HashMap<Integer, ArrayList<String>> process_multipage_page_landscape_invoice_line_items(String line_items_str) {

        HashMap<Integer, ArrayList<String>> line_items = new HashMap();
        //Explode the string along \n characters
        String[] line_item_entries = line_items_str.trim().split("\n");
        System.out.println("line_item_entries len: " + line_item_entries.length);
        //Loop through the line_item_entries processing each line item
        for (int entry_index = 0; entry_index < line_item_entries.length; entry_index++) {
            String line_item_entry = line_item_entries[entry_index];
            //Replace consecutive spaces
            line_item_entry = line_item_entry.replace("  ", " ");
            //Explode line_item_entry into words along space characters
            String[] line_item_entry_words = line_item_entry.split(" ");

            //Locate index of EA field
            int index_of_uom_field = 0;
            for (int i = 0; i < line_item_entry_words.length; i++) {
                String current_line_item_entry_word = line_item_entry_words[i];
                for (int u = 0; u < Config.UNIT_OF_MEASUREMENTS.size(); u++) {
                    String current_uom_value = Config.UNIT_OF_MEASUREMENTS.get(u);
                    if (current_line_item_entry_word.equalsIgnoreCase(current_uom_value)) {
                        index_of_uom_field = i;
                    }
                }
                /*
                 if (line_item_entry_words[i].equalsIgnoreCase("TO")) {
                 index_of_uom_field = i;
                 }
                 */
            }//End loop

            //Get the line item description & line_item_qty
            String line_item_desc = "",
                    line_item_qty = "",
                    unit_price_excl_discount = "",
                    total_value_excl_freight = "",
                    freight_amount = "",
                    recoveries = "",
                    total_excl_vat = "",
                    vat = "",
                    total_all_incl = "";

            System.out.println("line_item_entry: " + entry_index + " : " + line_item_entry);
            for (int i = 0; i < line_item_entry_words.length; i++) {
                System.out.println("line item word: " + i + " : " + line_item_entry_words[i]);
                if (i < (index_of_uom_field - 2)) {
                    line_item_desc = line_item_desc + " " + line_item_entry_words[i];
                } else if (i == (index_of_uom_field - 1)) {
                    line_item_qty = line_item_entry_words[i];
                    if (!line_item_qty.contains(".")) {
                        line_item_qty = "1.000";
                    }
                } else if (i == (index_of_uom_field + 1)) {
                    unit_price_excl_discount = line_item_entry_words[i].replace(",", "");
                } else if (i == (index_of_uom_field + 2)) {
                    total_value_excl_freight = line_item_entry_words[i].replace(",", "");
                } else if (i == (index_of_uom_field + 3)) {
                    freight_amount = line_item_entry_words[i].replace(",", "");
                } else if (i == (index_of_uom_field + 4)) {
                    recoveries = line_item_entry_words[i].replace(",", "");
                } else if (i == (line_item_entry_words.length - 4)) {
                    total_excl_vat = line_item_entry_words[i].trim().replace(",", "");
                } else if (i == (line_item_entry_words.length - 3)) {
                    vat = line_item_entry_words[i].trim().replace(",", "");
                } else if (i == (line_item_entry_words.length - 2)) {
                    total_all_incl = line_item_entry_words[i].trim().replace(",", "");
                    if (total_all_incl.equalsIgnoreCase("0.00")) {
                        total_all_incl = line_item_entry_words[i + 1].trim().replace(",", "");
                        if (total_all_incl.length() == 0) {
                            total_all_incl = line_item_entry_words[i].trim().replace(",", "");
                        }

                    }
                }

            }

            //Put the line item entry values in an array list
            ArrayList<String> line_item_entry_values = new ArrayList();
            line_item_entry_values.add(line_item_desc);
            line_item_entry_values.add(line_item_qty);
            line_item_entry_values.add(unit_price_excl_discount);
            line_item_entry_values.add(total_value_excl_freight);
            line_item_entry_values.add(freight_amount);
            line_item_entry_values.add(recoveries);
            line_item_entry_values.add(total_excl_vat);
            line_item_entry_values.add(vat);
            line_item_entry_values.add(total_all_incl);

            //Put in line_items
            line_items.put(entry_index, line_item_entry_values);

        }//End of main loop

        return line_items;
    }

    HashMap<Integer, ArrayList<String>> process_portrait_line_items(String line_items_str) {
        HashMap<Integer, ArrayList<String>> line_items = new HashMap();

        //Explode the items string along \n characters
        String[] line_item_entries = line_items_str.split("\n");
        //Process each entry one by one
        int line_item_count = 0;
        for (int i = 0; i < line_item_entries.length; i++) {
            String line_item_entry = line_item_entries[i].trim();
            if (line_item_entry.length() > 0) {
                System.out.println("process_portrait_line_items(): line_item_entry number: " + i);
                System.out.println(line_item_entry);
                boolean contains_currency_symbol = line_item_entry.contains("USD");
                if (contains_currency_symbol == false) {
                    contains_currency_symbol = line_item_entry.contains("ZWD");
                }

                if (contains_currency_symbol == false) {
                    contains_currency_symbol = line_item_entry.contains("ZWL");
                }
                String line_item_desc = "",
                        line_item_qty = "",
                        unit_price_excl_discount = "",
                        total_value_excl_discount = "",
                        discount_amount = "",
                        discount_percentage = "",
                        total_excl_vat = "",
                        vat = "",
                        total_all_incl = "";

                if (contains_currency_symbol) {
                    //Break the entry along space bounderies
                    String[] line_item_entry_words = line_item_entry.split(" ");
                    //Print out the words
                    for (int w = 0; w < line_item_entry_words.length; w++) {
                        System.out.println("process_portrait_line_items(): word: " + w + ": " + line_item_entry_words[w]);

                        //Get the item description
                        if (w < (line_item_entry_words.length - 2)) {
                            line_item_desc = line_item_desc + " " + line_item_entry_words[w].trim();
                        } else if (w == (line_item_entry_words.length - 2)) {
                            total_all_incl = line_item_entry_words[w].trim();
                        }
                    }

                } else {
                    //Entry doesnt have price, set it as comment
                    line_item_desc = line_item_entry;
                    total_all_incl = "-";
                }

                //Put the line item entry values in an array list
                ArrayList<String> line_item_entry_values = new ArrayList();
                line_item_entry_values.add(line_item_desc);
                line_item_qty = "1.000";
                line_item_entry_values.add(line_item_qty);
                line_item_entry_values.add(total_all_incl);

                //Put in line_items
                line_items.put(line_item_count, line_item_entry_values);
                //Update line_item_count
                line_item_count++;
            }

        }
        return line_items;
    }

    public boolean generatePRN(String file_name, HashMap<String, HashMap<Integer, ArrayList<String>>> prn_data) {
        String command_str = null, line_items_str = "";
        ArrayList<String> line_item_commands = new ArrayList();
        String opening_line = "##DLRWF#" + file_name + "#\n";
        String receipt_start_line = "#*1#1#Triangle Limited#0.00#14.50#0.00#0.00#1#1#1#0#\n";
        //String receipt_start_line = "#*1#1#Triangle Limited#0.00#15#0.00#0.00#1#1#1#0#\n";
        String invoice_to_line = "#!invoice_to#customer_vat_number#\n";
        String line_items_begin_line = "#|Line Items:\n";
        String line_item_entry = "#^item_number#item_desc#item_price#item_qty#2#\n";
        String payment_line = "#$1#amount_paid#\n";

        String invoice_type = null, recepient = null, currency = null;
        for (Object key : prn_data.keySet()) {
            String field_name = (String) key;

            HashMap<Integer, ArrayList<String>> field_values_map = prn_data.get(key);
            for (int i = 0; i < field_values_map.size(); i++) {
                ArrayList<String> field_values_array = field_values_map.get(i);
                if (!field_name.equalsIgnoreCase("Line items")) {
                    String field_value = field_values_array.get(0);
                    if (field_name.equalsIgnoreCase("invoice_type")) {
                        invoice_type = field_value;
                    }
                    
                    if (field_name.equalsIgnoreCase("Currency")) {
                        currency = field_value;
                    }
                    //Create PRN command line 
                    if (field_name.equalsIgnoreCase("Invoice To")) {
                        if (invoice_type.contains("landscape")) {
                            invoice_to_line = invoice_to_line.replace("invoice_to", field_value);
                        } else if (invoice_type.contains("portrait")) {
                            //Explode the field value along \n characters
                            String[] invoice_to_words = field_value.split("\n");
                            field_value = invoice_to_words[0].replace("\n", "").trim();
                            invoice_to_line = invoice_to_line.replace("invoice_to", field_value);
                        }
                    } else if (field_name.equalsIgnoreCase("Customer VAT Number")) {
                        field_value = field_value.trim();
                        if (field_value.length() == 0 || field_value.equalsIgnoreCase(".")) {
                            field_value = "NONE";
                        }
                        invoice_to_line = invoice_to_line.replace("customer_vat_number", field_value);

                    } else if (field_name.equalsIgnoreCase("Grand Total")) {
                        payment_line = payment_line.replace("amount_paid", field_value.replace(",", "").replace("-", "").replaceAll("[a-zA-Z:]", "").trim());
                    } else if (field_name.equalsIgnoreCase("User Email")) {
                        recepient = field_value;
                    }
                } else {
                    //Process line items
                    System.out.println("field_values_map: " + i);
                    System.out.println(field_values_map);
                    for (int entry_index = 0; entry_index < field_values_map.size(); entry_index++) {
                        ArrayList<String> entry_values = field_values_map.get(entry_index);
                        String item_desc = "", item_qty = "", total_all_incl = "";
                        if (invoice_type.contains("landscape")) {
                            item_desc = entry_values.get(0).trim();
                            item_qty = entry_values.get(1).replace(",", "").replaceAll("[a-zA-Z]", "").trim();
                            if (item_qty != null && !item_qty.equalsIgnoreCase("")) {
                                //Pad the qty if its integer
                                float qty = Float.parseFloat(item_qty);
                                item_qty = String.format("%.3f", qty);
                            } else {
                                //Pad the qty if its integer
                                item_qty = "0.00";
                                item_qty = String.format("%.3f", item_qty);
                            }

                            //total_all_incl = entry_values.get(8).trim().replace(",", "");
                            //Now using unit price
                            total_all_incl = entry_values.get(2).trim().replace(",", "").replace("-", "");
                        } else if (invoice_type.contains("portrait")) {
                            item_desc = entry_values.get(0).trim();
                            item_qty = entry_values.get(1).trim();
                            total_all_incl = entry_values.get(2).trim().replace(",", "").replace("-", "");
                        }

                        //Check for empty or " item_desc
                        if (item_desc.equalsIgnoreCase("\"") || item_desc.equalsIgnoreCase("")) {
                            item_desc = "Item " + entry_index;
                        }

                        System.out.println(TAG + ", generatePRN(): total_all_incl: " + total_all_incl);
                        if (total_all_incl.matches(".*\\d.*")) {
                            System.out.println(TAG + ", generatePRN(): total_all_incl: is a valid price");
                        } else {
                            System.out.println(TAG + ", generatePRN(): total_all_incl: is not a valid price");
                        }
                        //Create PRN command line
                        //if (!total_all_incl.equalsIgnoreCase("-")) {
                        if (total_all_incl.matches(".*\\d.*")) {
                            String line_item = line_item_entry.replace("item_number", entry_index + "");
                            line_item = line_item.replace("item_desc", item_desc);
                            //total_all_incl = "72.12";//Temporary for testing only
                            line_item = line_item.replace("item_price", total_all_incl);
                            line_item = line_item.replace("item_qty", item_qty);
                            //Concat to line_items_str
                            line_items_str = line_items_str + line_item + "\n";
                            line_item_commands.add(line_item);
                        } else {
                            //Its a comment with no price
                            String line_items_comment_line = "#|" + item_desc + "\n";
                            //Concat to line_items_str
                            line_items_str = line_items_str + line_items_comment_line;
                            line_item_commands.add(line_items_comment_line);
                        }

                    }

                    break;
                }
            }
        }

        command_str = opening_line
                + receipt_start_line
                + invoice_to_line
                + line_items_begin_line
                + line_items_str
                + payment_line;
        //Diplay the commands
        System.out.print(command_str);
        //Write the commands to file
        ArrayList<String> command_lines = new ArrayList();
        command_lines.add(opening_line);
        command_lines.add(receipt_start_line);
        command_lines.add(invoice_to_line);
        command_lines.add(line_items_begin_line);
        for (int i = 0; i < line_item_commands.size(); i++) {
            command_lines.add(line_item_commands.get(i));
        }
        command_lines.add(payment_line);
        if (writePRNFile(file_name, currency, command_lines)) {
            return true;
        } else {
            return false;
        }

    }

    public boolean writePRNFile(String file_name, String currency, ArrayList<String> command_lines) {
        PrintWriter writer = null;
        //Generate prn folder path based on currency
        String prn_folder = null;
        if(currency.equalsIgnoreCase("ZWL")){
            prn_folder = Config.PRN_FILES_PATH;
        }else if(currency.equalsIgnoreCase("USD")){
            prn_folder = Config.USD_PRN_FILES_PATH;
        }
        try {
            writer = new PrintWriter(prn_folder + file_name + ".prn", "UTF-8");
            for (int i = 0; i < command_lines.size(); i++) {
                writer.println(command_lines.get(i));
            }
            writer.close();
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PRNGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PRNGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            writer.close();
        }

    }

    public boolean set_prn_status_and_recepient_in_db(String file_name, String recepient, int prn_status) {
        DbManager db_manager = new DbManager();
        if (db_manager.set_invoice_prn_created_status(file_name, recepient, prn_status)) {
            return true;
        } else {
            return false;
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package app;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONObject;

/**
 *
 * @author Tinashe
 */
public class Config {
    
    public static String SAP_INVOICES_PATH = "C:\\Projects\\Carl\\SAP INVOICES\\";
    public static String PRN_FILES_PATH = "C:\\Projects\\Carl\\PRN FILES\\";
    public static String HTML_RECEIPTS_PATH = "C:\\Projects\\Carl\\HTML RECEIPTS\\";
    public static String SIGNED_INVOICES_PATH = "C:\\Projects\\Carl\\SIGNED INVOICES\\";
    public static String SMTP_SERVER = null;
    public static String SMTP_SERVER_PORT = null;
    public static String SENDER_MAILBOX = null;
    public static String SENDER_MAILBOX_PASSWORD = null;
    public static String TEST_RECEPIENT = null;
    public static ArrayList<String> INVOICE_TYPES = null;
    public static HashMap<String, ArrayList<String>> INVOICE_TAGS = null;
    public static ArrayList<String> REQUIRED_DATA_VALUES = null;
    //public static HashMap<String, HashMap<String, String>> INVOICE_DATA_BOUNDERIES = null;
    public static JSONObject INVOICE_DATA_BOUNDERIES = null;
    public static HashMap<String, String> DATA_EXTRACTION_ERRORS = null,DATABASE_ERRORS = null;
    public static ArrayList<String> UNIT_OF_MEASUREMENTS = null;
    public static HashMap<String, String> CURRENT_MACHINE_INFO = null, SAVED_MACHINE_INFO = null;
    public static int LICENCE_MODE = 0;
    public static String ACTIVATION_SERVER = "http://mgi.co.zw/";
    public static String SALES_ADDRESS = "karlnyabvure@gmail.com", ADMIN_ADDRESS = null;
    public static String RECEIPT_START_LINE = "\"#*1#1#User#0.00#15#0.00#0.00#1#1#1#0#\\n\";";
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbutils;

import autoutils.BackgroundWorker;
import static autoutils.BackgroundWorker.print_queuee;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tinashe
 */
public class DbManager {

    String TAG = "DbManager";
    Connection conn = null;
    static String databaseURL = null;
    static Statement statement = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    String driver = "org.apache.derby.jdbc.EmbeddedDriver";

    String CREATE_SAP_INVOICES_TABLE = "CREATE TABLE sap_invoices ("
            + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
            + "file_name varchar(128),"
            + "recepient varchar(128),"
            + "submitted timestamp default current timestamp,"
            + "prn_status int default 0,"
            + "signed int default 0,"
            + "sent int default 0,"
            + "printed int default 0,"
            + "processed int default 0,"
            + "date_sent timestamp, "
            + "sending_progress varchar(30) default 'pending',"
            + "printing_progress varchar(30) default 'pending',"
            + "CONSTRAINT primary_key PRIMARY KEY (id)"
            + ")";
    //file_name,recepient,prn_status,signed

    String CREATE_SAP_INVOICES_DATA_TABLE = "CREATE TABLE sap_invoices_data ("
            + "invoice_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
            + "file_name varchar(128),"
            + "invoice_number varchar(128),"
            + "grand_total varchar(128),"
            + "invoice_to varchar(128),"
            + "signed int default 0,"
            + "printed int default 0,"
            + "signature varchar(128) default 'none',"
            + "submitted timestamp default current timestamp"
            + ")";

    //
    public static void main(String[] args) {
        DbManager db_manager = new DbManager();
        
        HashMap<String, ArrayList<String>> unsent_invoices_found = db_manager.get_unsent_invoices();
        if (unsent_invoices_found != null && unsent_invoices_found.size() > 0) {
            System.out.println("unsent_invoices_found:");
            System.out.println(unsent_invoices_found);
        } else {
            System.out.println("No unsent_invoices_found");
        }
        /*
        HashMap<String, String> invoice_data_values = db_manager.get_invoice_data_test("0920040660aaa");
        System.out.println("invoice_data_values:");
        System.out.println(invoice_data_values);

        System.out.println("All invoices data:");
        System.out.println(db_manager.get_all_invoice_data());

        /*
         HashMap<String, ArrayList<String>> results = db_manager.get_error_invoices();
         System.out.println("unsent invoices:");
         System.out.println(results);
         */
        /*
         //Insert test invoice data
         HashMap<String, String> invoice_data = new HashMap();
         invoice_data.put("file_name", "1234567.pdf");
         invoice_data.put("invoice_number", "12345678");
         invoice_data.put("grand_total", "1300.00");
         invoice_data.put("invoice_to", "13009276");

         db_manager.insert_invoice_data(invoice_data);

         if (db_manager.check_for_duplicate_invoice_by_data(invoice_data)) {
         System.out.println("Duplicate invoice found.");
         } else {
         System.out.println("Duplicate invoice not found.");
         }
        
        
         if (db_manager.insert_duplicate_signed_invoice_for_sending("12345678.pdf", "test@gmail.com")) {
         System.out.println("Duplicate invoice submitted for re-sending");
         }else{
         System.out.println("Failed to submit duplicate invoice for re-sending");
         }
        
        

         //Insert an invoice record
         /*
         if (db_manager.insert_invoice("test_invoice2", "tinashe@mgi.co.zw")) {
         System.out.println("Invoice inserted successfully");
         } else {
         System.out.println("Failed to inserted invoice.");
         }
         
         HashMap<String, ArrayList<String>> new_invoices_found = db_manager.get_error_invoices();
         if (new_invoices_found != null && new_invoices_found.size() > 0) {
         System.out.println("Error invoices found in db:");
         System.out.println(new_invoices_found);
         } else {
         System.out.println("No error invoices found in db.");
         }
         /*
         if(db_manager.set_invoice_prn_created_status("test_invoice", 1)){
         System.out.println("PRN created status set successfully");
         }else{
         System.out.println("Failed to set PRN created status"); 
         }
         
         HashMap<String, ArrayList<String>> unsent_invoices_found = db_manager.get_unsent_invoices();
         if (unsent_invoices_found != null && unsent_invoices_found.size() > 0) {
         System.out.println("Unsent invoices found in db:");
         System.out.println(unsent_invoices_found);
         } else {
         System.out.println("No unsent invoices found in db.");
         }
         /*
         if(db_manager.set_invoice_sent_status("test_invoice", 1)){
         System.out.println("Sent status set successfully");
         }else{
         System.out.println("Failed to set sent status"); 
         }
         
         HashMap<String, ArrayList<String>> sent_invoices_found = db_manager.get_sent_invoices();
         if (sent_invoices_found != null && sent_invoices_found.size() > 0) {
         System.out.println("Sent invoices found in db:");
         System.out.println(sent_invoices_found);
         } else {
         System.out.println("No sent invoices found in db.");
         }

         if (db_manager.close()) {
         System.out.println("Database closed successfully");
         } else {
         System.out.println("Failed to close database");
         }
         */
    }

    public DbManager() {
        //load_driver(driver);

        this.databaseURL = "jdbc:derby:systemdb;create=true";
        //Create or open db
        this.conn = open();
        if (conn != null) {
            //System.out.println("Database created or opened successfully");
            //Create tables
            createTables();
        } else {
            System.out.println("Failed to created or opened database");
        }
    }

    void load_driver(String driver) {
        try {
            Class.forName(driver);
        } catch (java.lang.ClassNotFoundException e) {

        }
    }

    public Connection open() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(databaseURL);
            //conn.setAutoCommit(false);
            statement = conn.createStatement();
            return conn;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public boolean close() {
        try {
            conn.close();
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
            return true;
        } catch (SQLException ex) {
            System.out.println("close(): " + ex.getMessage());
            if (ex.getSQLState().equals("XJ015")) {
                System.out.println("Derby shutdown normally");
                return true;
            } else {
                ex.printStackTrace();
                return false;
            }
        }
    }

    void createTables() {
        if (!doesTableExists("sap_invoices", conn)) {
            try {
                statement.execute(CREATE_SAP_INVOICES_TABLE);
                System.out.println("Created table sap_invoices.");
            } catch (SQLException ex) {
                Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //System.out.println("sap_invoices table already exists.");
        }

        if (!doesTableExists("sap_invoices_data", conn)) {
            try {
                statement.execute(CREATE_SAP_INVOICES_DATA_TABLE);
                System.out.println("Created table sap_invoices_data.");
            } catch (SQLException ex) {
                Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //System.out.println("sap_invoices table already exists.");
        }
    }

    private boolean doesTableExists(String tableName, Connection conn) {
        DatabaseMetaData meta;
        try {
            meta = conn.getMetaData();
            ResultSet result = meta.getTables(null, null, tableName.toUpperCase(), null);
            return result.next();
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public boolean insert_invoice(String file_name, String recepient) {
        Timestamp submitted = new Timestamp(System.currentTimeMillis());
        System.out.println("submitted: " + submitted);
        String sql_query = "INSERT INTO sap_invoices (file_name,recepient,submitted)"
                + " VALUES ('" + file_name + "','" + recepient + "','" + submitted + "')";

        try {
            statement.execute(sql_query);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean insert_duplicate_signed_invoice_for_sending(String file_name, String recepient) {
        Timestamp submitted = new Timestamp(System.currentTimeMillis());
        System.out.println("submitted: " + submitted);
        String sql_query = "INSERT INTO sap_invoices (file_name,recepient,prn_status,signed,processed,sent,submitted)"
                + " VALUES ('" + file_name + "','" + recepient + "',1,1,1,0,'" + submitted + "')";

        try {
            statement.execute(sql_query);
            //Update the flags
            String sql_update = "UPDATE sap_invoices SET prn_status = 1, signed = 1, processed = 1, sending_progress = 'pending' WHERE file_name = '" + file_name + "'";
            statement.executeUpdate(sql_update);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean insert_invoice_data(HashMap<String, String> invoice_data) {
        Timestamp submitted = new Timestamp(System.currentTimeMillis());
        System.out.println("submitted: " + submitted);
        String sql_query = "INSERT INTO sap_invoices_data (file_name,invoice_number,grand_total,invoice_to,submitted)"
                + " VALUES ('" + invoice_data.get("file_name") + "','" + invoice_data.get("invoice_number") + "','" + invoice_data.get("grand_total") + "','" + invoice_data.get("invoice_to") + "','" + submitted + "')";
        System.out.println("sql: ");
        System.out.println(sql_query);
        try {
            statement.execute(sql_query);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public HashMap<String, ArrayList<String>> get_new_invoices() {
        HashMap<String, ArrayList<String>> invoices_found = new HashMap();
        String sql = "SELECT * FROM sap_invoices WHERE prn_status = 0 ORDER BY id ASC";
        ResultSet result;
        try {
            result = statement.executeQuery(sql);
            while (result.next()) {
                ArrayList<String> row_values = new ArrayList();
                row_values.add(result.getString("id"));
                row_values.add(result.getString("file_name"));
                row_values.add(result.getString("recepient"));
                invoices_found.put(result.getString("file_name"), row_values);
                //System.out.println(result.getString("id") + " , " + result.getString("file_name") + " , " + result.getString("recepient") + " , " + result.getString("submitted"));
            }
            return invoices_found;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public HashMap<String, String> get_invoice_data(String file_name) {
        HashMap<String, String> invoices_found = new HashMap();
        String sql = "SELECT * FROM sap_invoices_data WHERE file_name = '" + file_name + "' ORDER BY invoice_id DESC";
        System.out.println(TAG + ", get_invoice_data(): sql: " + sql);
        ResultSet result;
        try {
            result = statement.executeQuery(sql);
            while (result.next()) {//invoice_number,grand_total,invoice_to,signed,signature
                ArrayList<String> row_values = new ArrayList();
                invoices_found.put("invoice_id", result.getString("invoice_id"));
                invoices_found.put("file_name", result.getString("file_name"));
                invoices_found.put("invoice_number", result.getString("invoice_number"));
                invoices_found.put("grand_total", result.getString("grand_total"));
                invoices_found.put("invoice_to", result.getString("invoice_to"));
                invoices_found.put("signed", result.getInt("signed") + "");
                invoices_found.put("signature", result.getString("signature"));
                invoices_found.put("submitted", result.getString("submitted"));
                //System.out.println(result.getString("id") + " , " + result.getString("file_name") + " , " + result.getString("recepient") + " , " + result.getString("submitted"));
            }
            return invoices_found;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public HashMap<String, String> get_invoice_data_test(String invoice_number) {
        HashMap<String, String> invoices_found = new HashMap();
        String sql = "SELECT * FROM sap_invoices_data WHERE invoice_number = '" + invoice_number + "' ORDER BY invoice_id DESC";
        System.out.println(TAG + ", get_invoice_data(): sql: " + sql);
        ResultSet result;
        try {
            result = statement.executeQuery(sql);
            while (result.next()) {//invoice_number,grand_total,invoice_to,signed,signature
                ArrayList<String> row_values = new ArrayList();
                invoices_found.put("invoice_id", result.getString("invoice_id"));
                invoices_found.put("file_name", result.getString("file_name"));
                invoices_found.put("invoice_number", result.getString("invoice_number"));
                invoices_found.put("grand_total", result.getString("grand_total"));
                invoices_found.put("invoice_to", result.getString("invoice_to"));
                invoices_found.put("signed", result.getInt("signed") + "");
                invoices_found.put("signature", result.getString("signature"));
                invoices_found.put("submitted", result.getString("submitted"));
                //System.out.println(result.getString("id") + " , " + result.getString("file_name") + " , " + result.getString("recepient") + " , " + result.getString("submitted"));
            }
            return invoices_found;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public HashMap<String, String> get_all_invoice_data() {
        HashMap<String, String> invoices_found = new HashMap();
        String sql = "SELECT * FROM sap_invoices_data ORDER BY invoice_id DESC";
        System.out.println(TAG + ", get_invoice_data(): sql: " + sql);
        ResultSet result;
        try {
            result = statement.executeQuery(sql);
            while (result.next()) {//invoice_number,grand_total,invoice_to,signed,signature
                ArrayList<String> row_values = new ArrayList();
                invoices_found.put("invoice_id", result.getString("invoice_id"));
                invoices_found.put("file_name", result.getString("file_name"));
                invoices_found.put("invoice_number", result.getString("invoice_number"));
                invoices_found.put("grand_total", result.getString("grand_total"));
                invoices_found.put("invoice_to", result.getString("invoice_to"));
                invoices_found.put("signed", result.getInt("signed") + "");
                invoices_found.put("signature", result.getString("signature"));
                invoices_found.put("submitted", result.getString("submitted"));
                //System.out.println(result.getString("id") + " , " + result.getString("file_name") + " , " + result.getString("recepient") + " , " + result.getString("submitted"));
            }
            return invoices_found;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public HashMap<String, HashMap<String, String>> get_all_invoice_data_new() {
        HashMap<String, HashMap<String, String>> invoices_found = new HashMap();
        String sql = "SELECT * FROM sap_invoices_data ORDER BY invoice_id DESC";
        System.out.println(TAG + ", get_invoice_data(): sql: " + sql);
        ResultSet result;
        try {
            result = statement.executeQuery(sql);
            while (result.next()) {//invoice_number,grand_total,invoice_to,signed,signature
                HashMap<String, String> row_values = new HashMap();
                String invoice_id = result.getString("invoice_id");
                row_values.put("invoice_id", result.getString("invoice_id"));
                row_values.put("file_name", result.getString("file_name"));
                row_values.put("invoice_number", result.getString("invoice_number"));
                row_values.put("grand_total", result.getString("grand_total"));
                row_values.put("invoice_to", result.getString("invoice_to"));
                row_values.put("signed", result.getInt("signed") + "");
                row_values.put("signature", result.getString("signature"));
                row_values.put("submitted", result.getString("submitted"));
                
                invoices_found.put(invoice_id, row_values);
                //System.out.println(result.getString("id") + " , " + result.getString("file_name") + " , " + result.getString("recepient") + " , " + result.getString("submitted"));
            }
            return invoices_found;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public HashMap<String, ArrayList<String>> get_unsent_invoices() {
        HashMap<String, ArrayList<String>> invoices_found = new HashMap();
        String sql = "SELECT * FROM sap_invoices WHERE prn_status = 1 AND signed = 1 AND sent = 0 ORDER BY id ASC";
        //String sql = "SELECT * FROM sap_invoices ORDER BY id ASC";
        ResultSet result;
        try {
            result = statement.executeQuery(sql);
            if (!result.isClosed()) {
                while (result.next()) {
                    ArrayList<String> row_values = new ArrayList();
                    String file_name = result.getString("file_name");
                    row_values.add(result.getString("id"));
                    row_values.add(file_name);
                    row_values.add(result.getString("recepient"));
                    if (!BackgroundWorker.outbox.containsKey(file_name)) {
                        BackgroundWorker.outbox.put(file_name, row_values);
                        invoices_found.put(file_name, row_values);
                    }

                    //System.out.println(result.getString("id") + " , " + result.getString("file_name") + " , " + result.getString("recepient") + " , " + result.getString("submitted"));
                }
                return BackgroundWorker.outbox;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {

        }

    }

    public HashMap<String, ArrayList<String>> get_unprinted_invoices() {
        HashMap<String, ArrayList<String>> invoices_found = new HashMap();
        String sql = "SELECT * FROM sap_invoices WHERE prn_status = 1 AND signed = 1 AND printed = 0 ORDER BY id ASC";
        //String sql = "SELECT * FROM sap_invoices ORDER BY id ASC";
        ResultSet result;
        try {
            result = statement.executeQuery(sql);
            if (!result.isClosed()) {
                while (result.next()) {
                    ArrayList<String> row_values = new ArrayList();
                    String file_name = result.getString("file_name");
                    row_values.add(result.getString("id"));
                    row_values.add(file_name);
                    row_values.add(result.getString("submitted"));
                    if (!BackgroundWorker.print_queuee.containsKey(file_name)) {
                        BackgroundWorker.print_queuee.put(file_name, row_values);
                        invoices_found.put(file_name, row_values);
                    }

                    //System.out.println(result.getString("id") + " , " + result.getString("file_name") + " , " + result.getString("recepient") + " , " + result.getString("submitted"));
                }
                return BackgroundWorker.print_queuee;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {

        }

    }

    public HashMap<String, ArrayList<String>> get_all_invoices() {
        HashMap<String, ArrayList<String>> invoices_found = new HashMap();
        String sql = "SELECT * FROM sap_invoices ORDER BY id DESC";
        ResultSet result;
        try {
            result = statement.executeQuery(sql);
            while (result.next()) {
                ArrayList<String> row_values = new ArrayList();
                row_values.add(result.getString("id"));
                row_values.add(result.getString("submitted"));
                row_values.add(result.getString("prn_status"));
                row_values.add(result.getString("signed"));
                row_values.add(result.getString("processed"));
                invoices_found.put(result.getString("file_name"), row_values);
                //System.out.println(result.getString("id") + " , " + result.getString("file_name") + " , " + result.getString("recepient") + " , " + result.getString("submitted"));
            }
            return invoices_found;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public HashMap<String, ArrayList<String>> get_error_invoices() {
        HashMap<String, ArrayList<String>> invoices_found = new HashMap();
        String sql = "SELECT * FROM sap_invoices WHERE (prn_status = 0 OR signed = 0 OR sent = 0) AND processed = 1 ORDER BY id DESC";
        ResultSet result;
        try {
            result = statement.executeQuery(sql);
            while (result.next()) {
                ArrayList<String> row_values = new ArrayList();
                row_values.add(result.getString("id"));//0
                row_values.add(result.getString("submitted"));//1
                row_values.add(result.getInt("prn_status") + "");//2
                row_values.add(result.getInt("signed") + "");//3
                row_values.add(result.getInt("sent") + "");//4
                row_values.add(result.getString("sending_progress"));//5
                row_values.add(result.getInt("processed") + "");//6 
                row_values.add(result.getString("recepient"));//7
                invoices_found.put(result.getString("file_name"), row_values);
                //System.out.println(result.getString("id") + " , " + result.getString("file_name") + " , " + result.getString("recepient") + " , " + result.getString("submitted"));
            }
            //System.out.println(TAG + ", get_error_invoices(): ");
            //System.out.println(invoices_found);
            return invoices_found;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public HashMap<String, ArrayList<String>> get_sent_invoices() {
        HashMap<String, ArrayList<String>> invoices_found = new HashMap();
        String sql = "SELECT * FROM sap_invoices WHERE prn_status = 1 AND sent = 1 ORDER BY id ASC";
        ResultSet result;
        try {
            result = statement.executeQuery(sql);
            while (result.next()) {
                ArrayList<String> row_values = new ArrayList();
                row_values.add(result.getString("id"));
                row_values.add(result.getString("file_name"));
                row_values.add(result.getString("recepient"));
                invoices_found.put(result.getString("file_name"), row_values);
                System.out.println(result.getString("id") + " , " + result.getString("file_name") + " , " + result.getString("recepient") + " , " + result.getString("sent"));
            }
            return invoices_found;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public boolean check_for_duplicate_invoice(String file_name) {
        String sql = "SELECT * FROM sap_invoices WHERE file_name = '" + file_name + "'";
        ResultSet result;
        try {
            int total_found = 0;
            result = statement.executeQuery(sql);
            while (result.next()) {
                total_found++;
            }
            if (total_found > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public HashMap<String, String> check_for_duplicate_invoice_by_data(HashMap<String, String> invoice_data) {
        String where_clause = "";
        int loop_count = 0;
        for (Object key : invoice_data.keySet()) {
            String column_name = (String) key;
            String column_value = (String) invoice_data.get(key);
            if (!column_name.equalsIgnoreCase("file_name") && !column_name.equalsIgnoreCase("recepient")) {
                System.out.println(TAG + ", check_for_duplicate_invoice_by_data(): column_name: " + column_name);
                System.out.println(TAG + ", check_for_duplicate_invoice_by_data(): column_value: " + column_value);
                if (loop_count == 0) {
                    where_clause = column_name + " = '" + column_value;
                } else {
                    where_clause = where_clause + "' AND " + column_name + " = '" + column_value;
                }
                System.out.println(TAG + ", check_for_duplicate_invoice_by_data(): where_clause @  loop_count: " + loop_count);
                System.out.println(where_clause);
                loop_count++;
            }
        }

        //Add final quote
        where_clause += "'";

        System.out.println("where_clause: ");
        System.out.println(where_clause);

        String sql = "SELECT * FROM sap_invoices_data WHERE " + where_clause + " ORDER BY invoice_id DESC FETCH FIRST 1 ROWS ONLY";
        System.out.println("sql: ");
        System.out.println(sql);

        ResultSet result;
        String existing_invoice_name = null;
        HashMap<String, String> existing_invoice_data = new HashMap();
        try {
            int total_found = 0;
            result = statement.executeQuery(sql);
            while (result.next()) {
                existing_invoice_name = result.getString("file_name");
                existing_invoice_data.put("filename", result.getString("file_name"));
                existing_invoice_data.put("signature", result.getString("signature"));
                total_found++;
            }
            if (existing_invoice_data.size() > 0) {
                return existing_invoice_data;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean set_invoice_recepient(String file_name, String recepient) {
        String sql = "UPDATE sap_invoices SET recepient = '" + recepient + "' WHERE file_name = '" + file_name + "'";
        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean set_invoice_prn_created_status(String file_name, int status) {
        String sql = "UPDATE sap_invoices SET prn_status = 1 WHERE file_name = '" + file_name + "'";
        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean set_invoice_prn_created_status(String file_name, String recepient, int status) {
        //String sql = "UPDATE sap_invoices SET prn_status = 1, recepient = '" + recepient + "' WHERE file_name = '" + file_name + "'";
        String sql = "UPDATE sap_invoices SET prn_status = 1 WHERE file_name = '" + file_name + "'";
        //Temporarily set invoice signed
        //String sql = "UPDATE sap_invoices SET prn_status = 1, signed = 1, recepient = '" + recepient + "' WHERE file_name = '" + file_name + "'";
        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean set_invoice_signed_status(String file_name, int status) {
        String sql = "UPDATE sap_invoices SET signed = 1 WHERE file_name = '" + file_name + "'";
        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean set_invoice_data_signed_status(String file_name, int status) {
        String sql = "UPDATE sap_invoices_data SET signed = 1 WHERE file_name = '" + file_name + "'";
        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean set_signature_on_invoice_data(String file_name, String signature) {
        String sql = "UPDATE sap_invoices_data SET signature = '" + signature + "' WHERE file_name = '" + file_name + "'";
        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean set_invoice_processed_status(String file_name, int status) {
        String sql = "UPDATE sap_invoices SET processed = 1 WHERE file_name = '" + file_name + "'";
        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean set_invoice_sent_status(String file_name, int status) {
        Timestamp date_sent = new Timestamp(System.currentTimeMillis());
        String sql = null;
        if (status == 1) {
            sql = "UPDATE sap_invoices SET sent = 1, date_sent = '" + date_sent + "', sending_progress = 'sent' WHERE file_name = '" + file_name + "'";
        } else {
            sql = "UPDATE sap_invoices SET sent = 0, sending_progress = 'pending' WHERE file_name = '" + file_name + "'";
        }
        System.out.println(TAG + ", set_invoice_sent_status(): " + sql);
        try {
            int affected_rows = statement.executeUpdate(sql);
            if(affected_rows > 0){
                System.out.println(TAG + ", set_invoice_sent_status(): total affected_rows" + affected_rows);
                return true;
            }else{
                return false;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean set_invoice_print_status(String file_name, int status) {
        Timestamp date_sent = new Timestamp(System.currentTimeMillis());
        String sql = null;
        if (status == 1) {
            sql = "UPDATE sap_invoices SET printed = 1, date_sent = '" + date_sent + "', printing_progress = 'printed' WHERE file_name = '" + file_name + "'";
        } else {
            sql = "UPDATE sap_invoices SET printed = 0, printing_progress = 'pending' WHERE file_name = '" + file_name + "'";
        }
        System.out.println(TAG + ", set_invoice_print_status(): " + sql);
        try {
            int affected_rows = statement.executeUpdate(sql);
            if(affected_rows > 0){
                System.out.println(TAG + ", set_invoice_print_status(): total affected_rows" + affected_rows);
                return true;
            }else{
                return false;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean reset_invoice_sent_status(String file_name, int status) {
        Timestamp date_sent = new Timestamp(System.currentTimeMillis());
        String sql = "UPDATE sap_invoices SET sent = 0, sending_progress = 'pending' WHERE file_name = '" + file_name + "'";

        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean reset_invoice_printed_status(String file_name, int status) {
        Timestamp date_sent = new Timestamp(System.currentTimeMillis());
        String sql = "UPDATE sap_invoices SET printed = 0 WHERE file_name = '" + file_name + "'";

        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean set_invoice_sending_status(String file_name, String status) {
        Timestamp date_sent = new Timestamp(System.currentTimeMillis());
        String sql = null;
        sql = "UPDATE sap_invoices SET sent = 0, sending_progress = '" + status + "' WHERE file_name = '" + file_name + "'";

        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean set_invoice_printing_status(String file_name, String status) {
        Timestamp date_sent = new Timestamp(System.currentTimeMillis());
        String sql = null;
        sql = "UPDATE sap_invoices SET printed = 0, printing_progress = '" + status + "' WHERE file_name = '" + file_name + "'";

        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean delete_error_invoices(HashMap<String, ArrayList<String>> error_invoices) {
        int total_deleted = 0;
        for (Object key : error_invoices.keySet()) {
            String file_name = (String) key;
            String sql = "DELETE FROM sap_invoices WHERE file_name = '" + file_name + "'";
            try {
                statement.executeUpdate(sql);
                total_deleted++;
            } catch (SQLException ex) {
                Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (total_deleted == error_invoices.size()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean delete_duplicate_unsigned_invoice(String file_name) {
        String sql = "DELETE FROM sap_invoices WHERE file_name = '" + file_name + "'";
        try {
            statement.executeUpdate(sql);
            System.out.println(TAG + ", delete_duplicate_unsigned_invoice() deleted unsigned duplicate invoice: " + file_name);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(TAG + ", delete_duplicate_unsigned_invoice() failed to delete unsigned duplicate invoice: " + file_name);
            return false;
        }
    }

    public boolean delete_empty_invoice(String invoice_name) {
        String sql = "DELETE FROM sap_invoices WHERE file_name = '" + invoice_name + "'";
        try {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}

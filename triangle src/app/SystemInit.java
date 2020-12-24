/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import dbutils.DbManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tinashe
 */
public class SystemInit {
    
    
    public static HashMap<String, String> get_system_info() {
        HashMap<String, String> machine_info = new HashMap();
        String OS = System.getProperty("os.name").toLowerCase();
        machine_info.put("OS_Type", OS);
        System.out.println("OS name: " + OS);
        String command = "wmic csproduct get UUID";
        StringBuffer output = new StringBuffer();

        Process SerNumProcess;
        try {
            SerNumProcess = Runtime.getRuntime().exec(command);
            BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));

            String line = "";
            while ((line = sNumReader.readLine()) != null) {
                output.append(line + "\n");
            }
            String MachineID = output.toString().substring(output.indexOf("\n"), output.length()).trim();;
            System.out.println("UUID: " + MachineID);
            machine_info.put("UUID", MachineID);

            //Get installation date
            LocalDate install_date = LocalDate.now();
            LocalDate expiry_date = LocalDate.now().plusDays(27);//27
            LocalDate reminder_date = expiry_date.minusDays(8);//8
            
            
            //Test params
            //LocalDate expiry_date = LocalDate.now().plus(1, DAYS);
            //LocalDate reminder_date = expiry_date.minusDays(10);
            

            machine_info.put("CURRENT_DATE", install_date.toString());
            machine_info.put("DATE_OF_EXPIRY", expiry_date.toString());
            machine_info.put("DATE_OF_REMINDER", reminder_date.toString());
            

            return machine_info;
        } catch (IOException ex) {
            Logger.getLogger(SystemInit.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static LocalDate string_to_localdate(String date_str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(Locale.US);  
        LocalDate converted_date = LocalDate.parse(date_str, formatter);
        return converted_date;
    }


    public static void read_unit_of_measurements() {
        Config.UNIT_OF_MEASUREMENTS = new ArrayList();
    }

    public static void load_data_extraction_errors() {
        Config.DATA_EXTRACTION_ERRORS = new HashMap();
        Config.DATA_EXTRACTION_ERRORS.put("Error #1", "Start tag index not found");
        Config.DATA_EXTRACTION_ERRORS.put("Error #2", "End tag index not found");
        Config.DATA_EXTRACTION_ERRORS.put("Error #3", "Tag mismatch, end_tag occurs before start_tag");
        Config.DATA_EXTRACTION_ERRORS.put("Error #4", "Failed to extract data from page text");
        Config.DATA_EXTRACTION_ERRORS.put("Error #5", "");

    }

    public static void load_database_errors() {
        Config.DATABASE_ERRORS = new HashMap();
        Config.DATABASE_ERRORS.put("Error #1", "Failed to save new SAP invoice.");
        Config.DATABASE_ERRORS.put("Error #2", "Failed to set PRN status for invoice.");
    }

    public static void load_required_data_fields() {
        Config.REQUIRED_DATA_VALUES = new ArrayList();
        Config.REQUIRED_DATA_VALUES.add("Invoice To");
        Config.REQUIRED_DATA_VALUES.add("Invoice Number");
        Config.REQUIRED_DATA_VALUES.add("Customer VAT Number");
        Config.REQUIRED_DATA_VALUES.add("Line items");
        Config.REQUIRED_DATA_VALUES.add("Grand Total");
        Config.REQUIRED_DATA_VALUES.add("User Email");
        Config.REQUIRED_DATA_VALUES.add("Currency");
    }

    public static boolean serialize_object_to_file(String filename, Object obj) {
        try {
            FileOutputStream fileOut
                    = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in: " + filename);
            return true;
        } catch (IOException i) {
            i.printStackTrace();
            return false;
        }
    }

    public static Object deserialize_object_from_file(String filename) {
        Object obj = null;
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            obj = in.readObject();
            in.close();
            fileIn.close();
            return obj;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println(filename + " not found");
            c.printStackTrace();
            return null;
        }
    }

    public static void initDB() {
        //Create db manager object
        DbManager db_manager = new DbManager();
        //Close the db
        //db_manager.close();
    }
}

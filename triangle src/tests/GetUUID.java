/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tinashe
 */
public class GetUUID {

    public static void main(String[] args) {
        /*
         HashMap<String, String> machine_info = read_machine_details();
         System.out.println("Machine info: " + machine_info);
        
         Date installation_date = new Date(Long.parseLong(machine_info.get("DATE_OF_INSTALLATION"))); 
         System.out.println("installation_date: " + installation_date);
         Date now_date = new Date(System.currentTimeMillis()); 
         System.out.println("current_date: " + now_date);
        
        
         try {
         Thread.sleep(10000);
         } catch (InterruptedException ex) {
         Logger.getLogger(GetUUID.class.getName()).log(Level.SEVERE, null, ex);
         }
        
         long install_date = Long.parseLong(machine_info.get("DATE_OF_INSTALLATION"));
         long current_date = System.currentTimeMillis();
         check_licence_duration(install_date, current_date);
         */

        //date_arithmetic();
        string_to_localdate("2020-03-05");
    }

    static void date_arithmetic() {
        LocalDate install_date = LocalDate.now();
        System.out.println("Activation date: " + install_date);

        LocalDate expiry_date = install_date.plusDays(30);
        System.out.println("Expiry date: " + expiry_date);
        LocalDate first_reminder_date = expiry_date.minusDays(10);
        System.out.println("Date to send email reminder: " + first_reminder_date);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            Logger.getLogger(GetUUID.class.getName()).log(Level.SEVERE, null, ex);
        }

        LocalDate current_date = LocalDate.now().plusDays(5);
        int result = expiry_date.compareTo(current_date);
        System.out.println("Compare result: " + result);
        long days = DAYS.between(current_date, expiry_date);
        if (result < 0) {
            System.out.println(days + " days after exiry date..");
        } else if (result > 0) {

            System.out.println(days + " days before exiry date..");
        } else if (result == 0) {
            System.out.println("Now is exiry date..");
        }
    }

    static void string_to_localdate(String date_str) {
        /*
         final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
         final LocalDate converted_date = (LocalDate) dtf.parse(date_str);
         */

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(Locale.US);  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
        LocalDate converted_date = LocalDate.parse(date_str, formatter);
        
        LocalDate current_date = LocalDate.now();

        long days = DAYS.between(current_date, converted_date);
        System.out.println(days + " days difference between: current_date: " + current_date + " & converted_date: " + converted_date);
    }

    static void check_licence_duration(long install_date, long current_date) {
        //in milliseconds
        long diff = current_date - install_date;

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        System.out.print(diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.");
    }

    static HashMap<String, String> read_machine_details() {
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

            //Get installation epoch or date
            long time_of_install = System.currentTimeMillis();

            machine_info.put("DATE_OF_INSTALLATION", time_of_install + "");

            return machine_info;
        } catch (IOException ex) {
            Logger.getLogger(GetUUID.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}

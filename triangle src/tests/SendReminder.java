/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import app.Config;
import app.SystemInit;
import emailutils.SendMail;
import java.time.LocalDate;

/**
 *
 * @author Tinashe
 */
public class SendReminder {
    static String TAG = "SendReminder";
    
    public static void main(String[] args) {
        LocalDate expiry_date = LocalDate.now().plusMonths(3);
                        
        //Send reminder
        String subject = "License Renewal Reminder";
        String body = "Your license subscription for this application expires on (" + expiry_date + ").\nPlease contact " + Config.SALES_ADDRESS + " to make arrangements for the renewal.\n\nKind Regards, Support.";
        String sending_status = SendMail.send_with_tls("tinashe@radisys.co.zw", subject, body, null, null);
        if (sending_status.equalsIgnoreCase("success")) {
            //time_lapsed = 0;
            System.out.println(TAG + ", License subscription reminder sent to: " + Config.ADMIN_ADDRESS);
        } else {
            System.out.println(TAG + ", Failed to send license subscription reminder to: " + Config.ADMIN_ADDRESS);
        }
    }

}

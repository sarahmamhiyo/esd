/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailutils;

import app.Config;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 *
 * @author Tinashe
 */
public class SendMail {
    /*
    public static void main(String[] args) {
        String to = "radsys@mgi.co.zw";
        String subject = "Signed Invoice: 0920009359";
        String body = "Find attached signed invoice.";
        String invoice_name = "0920009359.pdf";
        String file_path_name = Config.SIGNED_INVOICES_PATH + invoice_name;
        String sending_status = send_with_tls(to, subject, body, invoice_name, file_path_name);
        if(sending_status.equalsIgnoreCase("success")){
           System.out.println("Email sent successfully");
        }else{
            System.out.println(sending_status);
        }
    }
    */

    public static void send_with_ssl() {
        final String fromEmail = "tinashekujoka@gmail.com"; //requires valid gmail id
        final String password = "Kujoka56743201"; // correct password for gmail id
        final String toEmail = "radsys@mgi.co.zw"; // can be any email id 

        System.out.println("SSLEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", "465"); //SMTP Port

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getDefaultInstance(props, auth);
        System.out.println("Session created");
        EmailUtil.sendEmail(session, toEmail, "SSLEmail Testing Subject", "SSLEmail Testing Body");

        //EmailUtil.sendAttachmentEmail(session, toEmail, "SSLEmail Testing Subject with Attachment", "SSLEmail Testing Body with Attachment");

        //EmailUtil.sendImageEmail(session, toEmail, "SSLEmail Testing Subject with Image", "SSLEmail Testing Body with Image");
    }

    public static String send_with_tls(String toEmail, String subject, String body, String invoice_name, String file_path_name) {
        final String fromEmail = Config.SENDER_MAILBOX; //requires valid gmail id
        final String password = Config.SENDER_MAILBOX_PASSWORD; // correct password for gmail id
        //final String toEmail = "radsys@mgi.co.zw"; // can be any email id 

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", Config.SMTP_SERVER); //SMTP Host
        props.put("mail.smtp.port", Config.SMTP_SERVER_PORT); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        //EmailUtil.sendEmail(session, toEmail, subject, body);
        String status = null;
        if(invoice_name != null){
            //Send signed invoice
            status = EmailUtil.sendAttachmentEmail(session, toEmail, subject, body, invoice_name, file_path_name);
        }else{
            //Send subscription reminder
            status = EmailUtil.sendEmail(session, toEmail, subject, body);
        }
        //String status = EmailUtil.sendAttachmentEmail(session, toEmail, subject, body, invoice_name, file_path_name);
        return status;
    }

    public static void send_mail() {
        final String fromEmail = "tinashekujoka@gmail.com"; //requires valid gmail id
        final String password = "Kujoka56743201"; // correct password for gmail id
        final String toEmail = "radsys@mgi.co.zw"; // can be any email id 

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, toEmail, "TLSEmail Testing Subject", "TLSEmail Testing Body");
    }

}

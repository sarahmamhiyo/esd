/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailutils;

import app.Config;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Tinashe
 */
public class EmailUtil {

    static String TAG = "EmailUtil";
    
    //public static void main
    /**
     * Utility method to send simple HTML email
     *
     * @param session
     * @param toEmail
     * @param subject
     * @param body
     */
    public static String sendEmail(Session session, String toEmail, String subject, String body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(Config.SENDER_MAILBOX, "Invoice Processing System"));
            msg.setReplyTo(InternetAddress.parse(toEmail, false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            //System.out.println("Message is ready");
            Transport.send(msg);

            //System.out.println("EMail Sent Successfully!!");
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Utility method to send email with attachment
     *
     * @param session
     * @param toEmail
     * @param subject
     * @param body
     */
    public static String sendAttachmentEmail(Session session, String toEmail, String subject, String body, String invoice_name, String file_path_name) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(Config.SENDER_MAILBOX, "Invoice Processing System"));

            msg.setReplyTo(InternetAddress.parse(Config.SENDER_MAILBOX, false));

            msg.setSubject(subject, "UTF-8");

            msg.setSentDate(new Date());
            //Check if multiple recipients
            //Sample: mchiturumani@zimsugar.co.zw;sales@triangle.co.zw
            //toEmail = "tinashe@reciprocacy.com;tinashe@mgi.co.zw;tinashekujoka@gmail.com";
            //toEmail = "tinashe@reciprocacy.com";
            if(toEmail.contains(";")){
                String[] recepients = toEmail.split(";");
                System.out.println(TAG + ", sending to multiple recepients: " + recepients.length);
                for(int i=0;i<recepients.length;i++){
                    if(i==0){
                        System.out.println(TAG + ", recepient TO: " + recepients[i]);
                        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recepients[i], false));
                    }else{
                        System.out.println(TAG + ", recepient CC: " + recepients[i]);
                        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(recepients[i], false));
                    }
                }
            }else{
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            }
            

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(body);

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Second part is attachment
            messageBodyPart = new MimeBodyPart();
            //String filename = "abc.txt";
            DataSource source = new FileDataSource(file_path_name);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(invoice_name);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            msg.setContent(multipart);

            // Send message
            Transport.send(msg);
            //System.out.println("EMail Sent Successfully with attachment!!");
            return "Success";
        } catch (MessagingException e) {
            //e.printStackTrace();
            //System.out.println("MessagingException: " + e.getMessage());
            return e.getMessage();
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            //System.out.println("UnsupportedEncodingException: " + e.getMessage());
            return e.getMessage();
        }
    }
}

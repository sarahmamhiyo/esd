/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoicetypeutils;

import app.Config;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author Tinashe
 */
public class InvoiceSigner {
    /*
    public static void main(String[] args) {
        boolean sign_status = sign_invoice("Invoice_20200903_104144.pdf", "9D233618642BD757B193B8532D7BE5A2483BA120-TEST");
        if (sign_status) {
            System.out.println("Invoice signed successfully");
        } else {
            System.out.println("Failed to signed invoice");
        }
    }
    */
    

    public static boolean sign_invoice(String invoice_name, String signature) {
        System.out.println("sign_invoice(): invoice_name: " + invoice_name + " signature: " + signature);
        //Loading an existing document
        String path_name = Config.SAP_INVOICES_PATH + invoice_name;
        String output_path_name = Config.SIGNED_INVOICES_PATH + invoice_name.replace(".sen", ".pdf").replace(".dup", ".pdf");
        
        //Temp paths
        //String path_name = "C:\\Projects\\Carl\\SAP INVOICES\\" + invoice_name;
        //String output_path_name = "C:\\Projects\\Carl\\SIGNED INVOICES\\" + invoice_name;
        //signature = "ABDCSHTD56678292HDDK937484903HHFJDKDLD";
        
        File file = new File(path_name);
        PDDocument document;
        try {
            document = PDDocument.load(file);
            int page_count = document.getNumberOfPages();
            for (int page_number = 0; page_number < page_count; page_number++) {
                //Retrieving the pages of the document 
                PDPage page = document.getPage(page_number);
                PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
                //Begin the Content stream 
                contentStream.beginText();
                //Setting the font to the Content stream  
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                //Setting the position for the line 
                PDRectangle mediaBox = page.getMediaBox();
                boolean isLandscape = mediaBox.getWidth() > mediaBox.getHeight();
                float height = mediaBox.getHeight();
                float sig_vertical_pos = height - 10;
                if (isLandscape) {
                    //contentStream.newLineAtOffset(250, 20);//landscape
                    contentStream.newLineAtOffset(150, sig_vertical_pos);//landscape
                } else {
                    contentStream.newLineAtOffset(100, 20);//portrait
                }
                //Adding text in the form of string 
                contentStream.showText(signature);
                //Ending the content stream
                contentStream.endText();
                //Closing the content stream
                contentStream.close();
                
                //Stop signing other pages, after first page
                //break;
            }

            //Saving the document
            document.save(new File(output_path_name));
            //Closing the document
            document.close();

            return true;
        } catch (IOException ex) {
            Logger.getLogger(InvoiceSigner.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfutils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Tinashe
 */
public class PdfManager {

    public int get_number_of_pages(String file_path) {
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(file_path));
            int total_pages = document.getNumberOfPages();
            return total_pages;
        } catch (IOException ex) {
            Logger.getLogger(PdfManager.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        } finally {
            try {
                document.close();
            } catch (IOException ex) {
                Logger.getLogger(PdfManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String get_page_text(int page_number, String file_path) {
        PDDocument document = null;
        String pageText = null;
        try {
            document = PDDocument.load(new File(file_path));
            //PDPage page = document.getPage(page_number);
            PDFTextStripper reader = new PDFTextStripper();
            reader.setStartPage(page_number);
            reader.setEndPage(page_number);
            pageText = reader.getText(document);
            return pageText;
        } catch (IOException ex) {
            Logger.getLogger(PdfManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                document.close();
            } catch (IOException ex) {
                Logger.getLogger(PdfManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String get_page_orientation(String file_path) {
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(file_path));
            PDPage page = document.getPage(0);
            PDRectangle mediaBox = page.getMediaBox();
            boolean isLandscape = mediaBox.getWidth() > mediaBox.getHeight();
            if (isLandscape) {
                return "landscape";
            } else {
                return "portrait";
            }
        } catch (IOException ex) {
            return null;
        } finally {
            try {
                document.close();
            } catch (IOException ex) {
                Logger.getLogger(PdfManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoicetypeutils;

import app.Config;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.Orientation;
import org.apache.pdfbox.printing.PDFPageable;

/**
 *
 * @author Tinashe
 */
public class Printer {

    String TAG = "Printer";

    public static void main(String[] args) {
        //find_printer();

        //find_printer_for_doc_type();
        //get_supported_doc_types();
        //choose_printer();
        Printer printer = new Printer();
        String invoice_name = "Invoice_20200903_104144.pdf";
        /*
         if (printer.print_invoice(invoice_name)) {
         System.out.println("Printed successfully...");
         } else {
         System.out.println("Printint failed...");
         }
         

        
        try {
            print_pdf(invoice_name);
            
        } catch (PrintException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }

    public static void find_printer() {
        PrintService printService = PrintServiceLookup
                .lookupDefaultPrintService();
        System.out.println(printService.getName());
    }

    public static void find_printer_for_doc_type() {
        DocFlavor df = DocFlavor.URL.JPEG;
        AttributeSet attribute = new HashAttributeSet();
        attribute.add(OrientationRequested.LANDSCAPE);
        attribute.add(ColorSupported.SUPPORTED);
        PrintService[] services = PrintServiceLookup
                .lookupPrintServices(df, attribute);
        for (int i = 0; i < services.length; i++) {
            if (services[i].isDocFlavorSupported(df)) {
                System.out.println(services[i].getName());
            }
        }
    }

    public static void get_supported_doc_types() {
        PrintService ps0 = PrintServiceLookup.lookupDefaultPrintService();
        DocFlavor f[] = ps0.getSupportedDocFlavors();
        for (int i = 0; i < f.length; i++) {
            System.out.println("MIME Type:" + f[i].getMimeType());
            System.out.println("Media Subtype:" + f[i].getMediaSubtype());
            System.out.println("Media Type:" + f[i].getMediaType());
            System.out.println("--------------------------------------");
        }
    }

    public static void choose_printer() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
        PrintRequestAttributeSet attrib = new HashPrintRequestAttributeSet();
        PrintService selectedPrintService = ServiceUI.printDialog(null, 150, 150,
                printServices, defaultPrintService, null, attrib);

        if (selectedPrintService != null) {
            System.out.println("selected printer:"
                    + selectedPrintService.getName());
        } else {
            System.out.println("selection cancelled");
        }
    }

    public void print() {
        // Input the file
        FileInputStream textStream = null;
        try {
            textStream = new FileInputStream("file.TXT");
        } catch (FileNotFoundException ffne) {
        }
        if (textStream == null) {
            return;
        }
        // Set the document type
        DocFlavor myFormat = DocFlavor.INPUT_STREAM.PDF;
        // Create a Doc
        Doc myDoc = new SimpleDoc(textStream, myFormat, null);
        // Build a set of attributes
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(new Copies(5));
        //aset.add(MediaSize.);
        aset.add(Sides.DUPLEX);
        // discover the printers that can print the format according to the
        // instructions in the attribute set
        PrintService[] services
                = PrintServiceLookup.lookupPrintServices(myFormat, aset);
        // Create a print job from one of the print services
        if (services.length > 0) {
            DocPrintJob job = services[0].createPrintJob();
            try {
                job.print(myDoc, aset);
            } catch (PrintException pe) {

            }
        }
    }

    public boolean print_invoice(String file_name) {
        PrintService ps = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob job = ps.createPrintJob();
        job.addPrintJobListener(new PrintListener(file_name));
        FileInputStream fis;
        try {
            //Temporary folder path
            //Config.SIGNED_INVOICES_PATH = "C:\\Projects\\Carl\\SIGNED INVOICES\\";
            fis = new FileInputStream(Config.SIGNED_INVOICES_PATH + file_name);
            Doc doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            // Doc doc=new SimpleDoc(fis, DocFlavor.INPUT_STREAM.JPEG, null);
            PrintRequestAttributeSet attrib = new HashPrintRequestAttributeSet();
            attrib.add(new Copies(1));
            job.print(doc, attrib);

            fis.close();

            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (PrintException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public boolean print_pdf(String file_name) throws PrintException {

        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        FileInputStream fis;
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        try {
            fis = new FileInputStream(Config.SIGNED_INVOICES_PATH + file_name);
            //Temp testing 
            //String file_pathname = "C:\\Projects\\Carl\\SIGNED INVOICES\\" + file_name;
            //System.out.println("file_pathname: " + file_pathname);
            //fis = new FileInputStream(file_pathname);
            PDDocument pdf = PDDocument.load(fis);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(printService);
            job.setPageable(new PDFPageable(pdf, Orientation.PORTRAIT));
            job.print();
            pdf.close();

            return true;
        } catch (PrinterException e) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, e);
            //throw new PrintException("Printer exception", e);
            return false;
        } catch (IOException e) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, e);
            //throw new PrintException("Input exception", e);
            return false;
        }

    }

    class PrintListener implements PrintJobListener {

        String file_name = null;
        app.Logger log = new app.Logger();
        ArrayList<String> info_lines = null;

        public PrintListener(String file_name) {
            this.file_name = file_name;
        }

        @Override
        public void printDataTransferCompleted(PrintJobEvent pje) {
            System.out.println("data transfer complete");
            info_lines = new ArrayList();
            info_lines.add(TAG + ": data transfer complete for invoice (" + file_name + ")");
            log.writeLog(file_name, info_lines);
        }

        @Override
        public void printJobCompleted(PrintJobEvent pje) {
            System.out.println("print job completed successfully..");
            info_lines = new ArrayList();
            info_lines.add(TAG + ": print job completed successfully for invoice (" + file_name + ")");
            log.writeLog(file_name, info_lines);
        }

        @Override
        public void printJobFailed(PrintJobEvent pje) {
            System.out.println("print job failed..");
            info_lines = new ArrayList();
            info_lines.add(TAG + ": print job failed for invoice (" + file_name + ")");
            log.writeLog(file_name, info_lines);
        }

        @Override
        public void printJobCanceled(PrintJobEvent pje) {
            System.out.println("print job cancelled..");
            info_lines = new ArrayList();
            info_lines.add(TAG + ": print job cancelled for invoice (" + file_name + ")");
            log.writeLog(file_name, info_lines);
        }

        @Override
        public void printJobNoMoreEvents(PrintJobEvent pje) {
            System.out.println("received no more events ..");
        }

        @Override
        public void printJobRequiresAttention(PrintJobEvent pje) {
            System.out.println("print job requires attention..");
            info_lines = new ArrayList();
            info_lines.add(TAG + ": print job requires attention for invoice (" + file_name + ")");
            log.writeLog(file_name, info_lines);
        }

    }

}

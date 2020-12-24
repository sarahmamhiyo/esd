/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoicetypeutils;

import dbutils.DbManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Tinashe
 */
public class PrinterTester extends javax.swing.JPanel {

    /**
     * Creates new form PrinterTester
     */
    public PrinterTester() {
        initComponents();

        initTables();

        show_print_queue_btn.addActionListener(new OpenQueueListener());

        print_btn.addActionListener(new PrintInvoiceListener());
        
        show_printed_btn.addActionListener(new CheckPrintersListener());

        //create the thread pool executor service
        this.executor = Executors.newFixedThreadPool(10);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        show_print_queue_btn = new javax.swing.JButton();
        show_printed_btn = new javax.swing.JButton();
        outboxScrollpane = new javax.swing.JScrollPane();
        queueTable = new javax.swing.JTable();
        print_btn = new javax.swing.JButton();
        printing_status = new javax.swing.JLabel();
        db_update_status = new javax.swing.JLabel();

        show_print_queue_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        show_print_queue_btn.setText("Print Queue");

        show_printed_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        show_printed_btn.setText("Check PDF Support");

        outboxScrollpane.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        queueTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        outboxScrollpane.setViewportView(queueTable);

        print_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        print_btn.setText("Print Invoice");

        printing_status.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        printing_status.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        printing_status.setText("-");

        db_update_status.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        db_update_status.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        db_update_status.setText("-");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(printing_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(print_btn)
                    .addComponent(outboxScrollpane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(show_print_queue_btn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(show_printed_btn))
                    .addComponent(db_update_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(323, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(show_print_queue_btn)
                    .addComponent(show_printed_btn))
                .addGap(18, 18, 18)
                .addComponent(outboxScrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(print_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(printing_status)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(db_update_status)
                .addContainerGap(141, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JLabel db_update_status;
    private static javax.swing.JScrollPane outboxScrollpane;
    private static javax.swing.JButton print_btn;
    private static javax.swing.JLabel printing_status;
    private static javax.swing.JTable queueTable;
    private static javax.swing.JButton show_print_queue_btn;
    private static javax.swing.JButton show_printed_btn;
    // End of variables declaration//GEN-END:variables

    HashMap<String, ArrayList<String>> print_queue;
    DefaultTableModel queueTableModel = new DefaultTableModel(0, 3);
    String TAG = "PrinterTester";
    public static ExecutorService executor = null;

    void initTables() {
        queueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // add header of the table
        String header[] = new String[]{"Invoice Name", "Print Status", "Submitted"};
        queueTableModel.setColumnIdentifiers(header);
        queueTable.setModel(queueTableModel);
        resizeColumnWidth(queueTable);

    }

    void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            if (width > 300) {
                width = 300;
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    class OpenQueueListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //Get the pending invoices from db
            DbManager db_manager = new DbManager();
            //Check
            print_queue = db_manager.get_unprinted_invoices();
            if (print_queue != null && print_queue.size() > 0) {
                //Pending emails found, display in table
                printing_status.setText(print_queue.size() + " pending invoices found.");
                for (Object key : print_queue.keySet()) {
                    String invoice_name = (String) key;
                    ArrayList<String> invoice_data_array = print_queue.get(key);
                    String submitted = invoice_data_array.get(2);
                    String status = "pending";
                    queueTableModel.addRow(new String[]{invoice_name, status, submitted});
                    resizeColumnWidth(queueTable);
                }

            } else {
                //Pending emails not found
                printing_status.setText("No pending invoices found.");
                printing_status.setForeground(Color.black);
                db_update_status.setForeground(Color.black);
            }
        }

    }

    class PrintInvoiceListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (print_queue != null && print_queue.size() > 0) {
                int rowIndex = queueTable.getSelectedRow();
                //Get the field name selected
                String invoice_name = (String) queueTable.getValueAt(rowIndex, 0);
                System.out.println(TAG + ", invoice_name: " + invoice_name);
                if (invoice_name == null || invoice_name.length() == 0) {
                    System.out.println(TAG + ", Invoice: " + invoice_name + " is empty");
                }

                print_btn.setEnabled(false);
                printing_status.setText("Printing...please wait");

                //Submit the PrintInvoiceTask to executor
                Future<Boolean> printing_response = executor.submit(new PrintInvoiceTask(invoice_name));
                try {
                    //Block & check printing response
                    boolean sending_status_response = printing_response.get();
                    //Update the sent_status & progress in db
                    int print_status = 0;
                    if (sending_status_response) {
                        String msg = invoice_name + ", printed successifully";
                        System.out.println(TAG + ", invoice: " + invoice_name + ", printed successifully.");
                        printing_status.setText(msg);
                        printing_status.setForeground(Color.green);
                        print_status = 1;
                    } else {
                        String msg = "Failed to print " + invoice_name;
                        System.out.println(TAG + "Failed to print " + invoice_name);
                        printing_status.setText(msg);
                        printing_status.setForeground(Color.red);
                        print_status = 0;
                    }

                    //Update invoice printed flag in db
                    DbManager db_manager = new DbManager();
                    if (db_manager.set_invoice_print_status(invoice_name, print_status)) {
                        //Remove from out hashmap
                        print_queue.remove(invoice_name);
                        System.out.println(TAG + ", signed invoice: " + invoice_name + " print status updated successfully.");
                        //Delete the sending failure log if it had been created
                        app.Logger log = new app.Logger();
                        log.deleteLog(invoice_name);
                        String msg = invoice_name + " print status updated successfully.";
                        db_update_status.setText(msg);
                        db_update_status.setForeground(Color.green);
                    } else {
                        System.out.println(TAG + ", failed to update print status in db for: " + invoice_name);
                        String msg = invoice_name + " failed to update print status in db for: " + invoice_name;
                        db_update_status.setText(msg);
                        db_update_status.setForeground(Color.red);
                    }

                    print_btn.setEnabled(true);

                } catch (InterruptedException ex) {
                    Logger.getLogger(PrinterTester.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(PrinterTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                String msg = "Choose an invoice in above table first.";
                db_update_status.setText(msg);
                db_update_status.setForeground(Color.red);
            }
        }

    }

    class PrintInvoiceTask implements Callable<Boolean> {

        String TAG = "PrintInvoiceTask";
        String invoice_name = null;

        public PrintInvoiceTask(String invoice_name) {
            this.invoice_name = invoice_name;
        }

        @Override
        public Boolean call() throws Exception {
            if (invoice_name != null) {
                //Print invoice
                System.out.println(TAG + ", printing: " + invoice_name);
                Printer printer = new Printer();
                if (printer.print_pdf(invoice_name)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                System.out.println(TAG + ", error printing invoice, invoice_name is empty.");
                return false;
            }
        }

    }

    class CheckPrintersListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            checkIfPdfIsSupported();
        }

        void checkIfPdfIsSupported() {
            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            int count = 0;
            for (DocFlavor docFlavor : service.getSupportedDocFlavors()) {
                if (docFlavor.toString().contains("pdf")) {
                    count++;
                }
            }
            if (count == 0) {
                System.err.println("PDF not supported by printer: " + service.getName());
                db_update_status.setText("PDF not supported by printer: " + service.getName());
                db_update_status.setForeground(Color.red);
            } else {
                System.out.println("PDF is supported by printer: " + service.getName());
                db_update_status.setText("PDF is supported by printer: " + service.getName());
                db_update_status.setForeground(Color.green);
            }
        }

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Tinashe
 */
public class AppSettings extends javax.swing.JPanel {

    /**
     * Creates new form AppSettings
     */
    public AppSettings() {
        initComponents();

        sap_browse_btn.addActionListener(new BrowseToFolderListener());
        prn_browse_btn.addActionListener(new BrowseToFolderListener());
        usd_prn_browse_btn.addActionListener(new BrowseToFolderListener());
        receipts_browse_btn.addActionListener(new BrowseToFolderListener());
        signed_invoices_browse_btn.addActionListener(new BrowseToFolderListener());

        save_email_settings_btn.addActionListener(new SaveEmailSettingsListener());

        save_folder_settings_btn.addActionListener(new SaveFolderSettingsListener());

        loadExistingSettings();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        sap_invoices_path = new javax.swing.JTextField();
        sap_browse_btn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        prn_files_path = new javax.swing.JTextField();
        prn_browse_btn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        html_receipts_files_path = new javax.swing.JTextField();
        receipts_browse_btn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        signed_invoices_path = new javax.swing.JTextField();
        signed_invoices_browse_btn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        smtp_server_txtfield = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        smtp_port_txtfield = new javax.swing.JTextField();
        sender_mailbox_txtfield = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        password_txtfield = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        save_email_settings_btn = new javax.swing.JButton();
        save_folder_settings_btn = new javax.swing.JButton();
        folder_paths_status = new javax.swing.JLabel();
        email_settings_status = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        test_recepient_txt = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        admin_mailbox = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        support_mailbox = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        prn_files_path_usd = new javax.swing.JTextField();
        usd_prn_browse_btn = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Application Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 0, 11))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel1.setText("SAP Invoices Folder:");

        sap_invoices_path.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        sap_browse_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        sap_browse_btn.setText("Browse..");

        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel2.setText("PRN Files Folder (ZWL):");

        prn_files_path.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        prn_browse_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        prn_browse_btn.setText("Browse..");

        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel3.setText("Receipts Folder:");

        html_receipts_files_path.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        receipts_browse_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        receipts_browse_btn.setText("Browse..");

        jLabel4.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel4.setText("Signed Invoices Folder:");

        signed_invoices_path.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        signed_invoices_browse_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        signed_invoices_browse_btn.setText("Browse..");

        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel5.setText("Email Settings:");

        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel6.setText("SMTP Server:");

        smtp_server_txtfield.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel7.setText("SMTP Port:");

        smtp_port_txtfield.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        sender_mailbox_txtfield.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel8.setText("Sender Mailbox:");

        password_txtfield.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel9.setText("Mailbox Password:");

        save_email_settings_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        save_email_settings_btn.setText("Save Email Settings");

        save_folder_settings_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        save_folder_settings_btn.setText("Save Folder Settings");

        folder_paths_status.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        folder_paths_status.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        folder_paths_status.setText("-");

        email_settings_status.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        email_settings_status.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        email_settings_status.setText("-");

        jLabel10.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel10.setText("Test Recepient:");

        test_recepient_txt.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        test_recepient_txt.setText("0");

        jLabel11.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel11.setText("Admin Email:");

        admin_mailbox.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel12.setText("Support Email:");

        support_mailbox.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel13.setText("PRN Files Folder (USD):");

        prn_files_path_usd.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        usd_prn_browse_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        usd_prn_browse_btn.setText("Browse..");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sap_invoices_path, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sap_browse_btn))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(prn_files_path, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(prn_browse_btn))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(html_receipts_files_path, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(receipts_browse_btn))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(smtp_server_txtfield, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(smtp_port_txtfield, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sender_mailbox_txtfield, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(password_txtfield, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(test_recepient_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(admin_mailbox, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(support_mailbox, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(save_folder_settings_btn)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(signed_invoices_path, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(folder_paths_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(email_settings_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(save_email_settings_btn)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(signed_invoices_browse_btn))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(prn_files_path_usd, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usd_prn_browse_btn)))
                .addContainerGap(139, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sap_invoices_path, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sap_browse_btn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(prn_files_path, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prn_browse_btn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(prn_files_path_usd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usd_prn_browse_btn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(html_receipts_files_path, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(receipts_browse_btn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(signed_invoices_path, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(signed_invoices_browse_btn))
                .addGap(35, 35, 35)
                .addComponent(folder_paths_status)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(save_folder_settings_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(smtp_server_txtfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(smtp_port_txtfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(sender_mailbox_txtfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(password_txtfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(admin_mailbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(support_mailbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(test_recepient_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(email_settings_status)
                    .addComponent(save_email_settings_btn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextField admin_mailbox;
    private static javax.swing.JLabel email_settings_status;
    private static javax.swing.JLabel folder_paths_status;
    private static javax.swing.JTextField html_receipts_files_path;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private static javax.swing.JTextField password_txtfield;
    private static javax.swing.JButton prn_browse_btn;
    private static javax.swing.JTextField prn_files_path;
    private static javax.swing.JTextField prn_files_path_usd;
    private static javax.swing.JButton receipts_browse_btn;
    private static javax.swing.JButton sap_browse_btn;
    private static javax.swing.JTextField sap_invoices_path;
    private static javax.swing.JButton save_email_settings_btn;
    private static javax.swing.JButton save_folder_settings_btn;
    private static javax.swing.JTextField sender_mailbox_txtfield;
    private static javax.swing.JButton signed_invoices_browse_btn;
    private static javax.swing.JTextField signed_invoices_path;
    private static javax.swing.JTextField smtp_port_txtfield;
    private static javax.swing.JTextField smtp_server_txtfield;
    private static javax.swing.JTextField support_mailbox;
    private static javax.swing.JTextField test_recepient_txt;
    private static javax.swing.JButton usd_prn_browse_btn;
    // End of variables declaration//GEN-END:variables

    HashMap<String, String> folder_paths = null;

    void loadExistingSettings() {
        //Load the folder paths settings
        HashMap<String, String> folder_paths = (HashMap) SystemInit.deserialize_object_from_file("folder_paths.txt");
        if (folder_paths != null) {
            //Set the folder global variables
            Config.SAP_INVOICES_PATH = folder_paths.get("SAP_INVOICES_PATH");
            sap_invoices_path.setText(Config.SAP_INVOICES_PATH);
            Config.PRN_FILES_PATH = folder_paths.get("PRN_FILES_PATH");
            prn_files_path.setText(Config.PRN_FILES_PATH);
            Config.USD_PRN_FILES_PATH = folder_paths.get("USD_PRN_FILES_PATH");
            prn_files_path_usd.setText(Config.USD_PRN_FILES_PATH);
            Config.HTML_RECEIPTS_PATH = folder_paths.get("HTML_RECEIPTS_PATH");
            html_receipts_files_path.setText(Config.HTML_RECEIPTS_PATH);
            Config.SIGNED_INVOICES_PATH = folder_paths.get("SIGNED_INVOICES_PATH");
            signed_invoices_path.setText(Config.SIGNED_INVOICES_PATH);

            folder_paths_status.setText("Existing folder paths loaded successfully.");
        } else {
            System.out.println("Failed to load folder paths.");
        }

        //Load the email settings
        HashMap<String, String> email_settings = (HashMap) SystemInit.deserialize_object_from_file("email_settings.txt");
        if (email_settings != null) {
            //Set the email settings global variables
            Config.SMTP_SERVER = email_settings.get("SMTP_SERVER");
            smtp_server_txtfield.setText(Config.SMTP_SERVER);
            Config.SMTP_SERVER_PORT = email_settings.get("SMTP_SERVER_PORT");
            smtp_port_txtfield.setText(Config.SMTP_SERVER_PORT);
            Config.SENDER_MAILBOX = email_settings.get("SENDER_MAILBOX");
            sender_mailbox_txtfield.setText(Config.SENDER_MAILBOX);
            Config.SENDER_MAILBOX_PASSWORD = email_settings.get("SENDER_MAILBOX_PASSWORD");
            password_txtfield.setText(Config.SENDER_MAILBOX_PASSWORD);
            Config.TEST_RECEPIENT = email_settings.get("TEST_RECEPIENT");
            test_recepient_txt.setText(Config.TEST_RECEPIENT);
            Config.ADMIN_ADDRESS = email_settings.get("ADMIN_ADDRESS");
            admin_mailbox.setText(Config.ADMIN_ADDRESS);
            Config.SALES_ADDRESS = email_settings.get("SALES_ADDRESS");
            support_mailbox.setText(Config.SALES_ADDRESS);
            

        } else {
            System.out.println("Failed to load email settings.");
        }
    }

    class BrowseToFolderListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int mode = 0;
            String title = null;
            if (e.getSource() == sap_browse_btn) {
                mode = 0;
                title = "Choose SAP Invoices folder.";
            } else if (e.getSource() == prn_browse_btn) {
                mode = 1;
                title = "Choose ZWL PRN Files folder.";
            } else if (e.getSource() == receipts_browse_btn) {
                mode = 2;
                title = "Choose HTML Receipts folder.";
            } else if (e.getSource() == signed_invoices_browse_btn) {
                mode = 3;
                title = "Choose Signed Invoices folder.";
            } else if (e.getSource() == usd_prn_browse_btn) {
                mode = 4;
                title = "Choose USD PRN Files folder.";
            }

            JFileChooser jfc = new JFileChooser();
            jfc.setCurrentDirectory(new File("."));
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.setDialogTitle(title);
            jfc.setAcceptAllFileFilterUsed(false);

            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String selected_folder_path = jfc.getSelectedFile().getAbsolutePath();
                selected_folder_path = selected_folder_path.replace("\\", "\\\\") + "\\\\";

                if (folder_paths == null) {
                    folder_paths = new HashMap();
                }

                if (mode == 0) {
                    sap_invoices_path.setText(selected_folder_path);
                    folder_paths.put("SAP_INVOICES_PATH", selected_folder_path);
                } else if (mode == 1) {
                    prn_files_path.setText(selected_folder_path);
                    folder_paths.put("PRN_FILES_PATH", selected_folder_path);
                } else if (mode == 2) {
                    html_receipts_files_path.setText(selected_folder_path);
                    folder_paths.put("HTML_RECEIPTS_PATH", selected_folder_path);
                } else if (mode == 3) {
                    signed_invoices_path.setText(selected_folder_path);
                    folder_paths.put("SIGNED_INVOICES_PATH", selected_folder_path);
                } else if (mode == 4) {
                    prn_files_path_usd.setText(selected_folder_path);
                    folder_paths.put("USD_PRN_FILES_PATH", selected_folder_path);
                }
            }
        }

    }

    class SaveFolderSettingsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //Check if all the paths are set
            if (!folder_paths.containsKey("SAP_INVOICES_PATH")) {
                JOptionPane.showMessageDialog(
                        AppSettings.this,
                        "Set the SAP invoices folder path first!",
                        "Alert",
                        JOptionPane.WARNING_MESSAGE);

                sap_invoices_path.requestFocus();
            } else if (!folder_paths.containsKey("PRN_FILES_PATH")) {
                JOptionPane.showMessageDialog(
                        AppSettings.this,
                        "Set the PRN Files folder path first!",
                        "Alert",
                        JOptionPane.WARNING_MESSAGE);

                prn_files_path.requestFocus();
            } else if (!folder_paths.containsKey("HTML_RECEIPTS_PATH")) {
                JOptionPane.showMessageDialog(
                        AppSettings.this,
                        "Set the HTML Receipts folder path first!",
                        "Alert",
                        JOptionPane.WARNING_MESSAGE);

                html_receipts_files_path.requestFocus();
            } else if (!folder_paths.containsKey("SIGNED_INVOICES_PATH")) {
                JOptionPane.showMessageDialog(
                        AppSettings.this,
                        "Set the Signed Invoices folder path first!",
                        "Alert",
                        JOptionPane.WARNING_MESSAGE);

                signed_invoices_path.requestFocus();
            } else if (!folder_paths.containsKey("USD_PRN_FILES_PATH")) {
                JOptionPane.showMessageDialog(
                        AppSettings.this,
                        "Set the USD PRN Files folder path first!",
                        "Alert",
                        JOptionPane.WARNING_MESSAGE);

                prn_files_path_usd.requestFocus();
            } else {
                //Save the hashmap to file
                boolean save_status = SystemInit.serialize_object_to_file("folder_paths.txt", folder_paths);
                if (save_status) {
                    //Set the folder global variables
                    Config.SAP_INVOICES_PATH = folder_paths.get("SAP_INVOICES_PATH");
                    Config.PRN_FILES_PATH = folder_paths.get("PRN_FILES_PATH");
                    Config.USD_PRN_FILES_PATH = folder_paths.get("USD_PRN_FILES_PATH");
                    Config.HTML_RECEIPTS_PATH = folder_paths.get("HTML_RECEIPTS_PATH");
                    Config.SIGNED_INVOICES_PATH = folder_paths.get("SIGNED_INVOICES_PATH");
                    folder_paths_status.setText("Folder paths saved and set successfully.");
                } else {
                    JOptionPane.showMessageDialog(
                            AppSettings.this,
                            "Oops, failed to save folder paths!!",
                            "Alert",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        }

    }

    class SaveEmailSettingsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String smtp_server = smtp_server_txtfield.getText().toString();
            if (smtp_server != null && !smtp_server.equalsIgnoreCase("")) {
                String smtp_server_port = smtp_port_txtfield.getText().toString();
                if (smtp_server_port != null && !smtp_server_port.equalsIgnoreCase("")) {
                    String sender_mailbox = sender_mailbox_txtfield.getText().toString();
                    if (sender_mailbox != null && !sender_mailbox.equalsIgnoreCase("")) {
                        String password = password_txtfield.getText().toString();
                        if (password != null && !password.equalsIgnoreCase("")) {
                            //Get admin mail box 
                            String admin_email = admin_mailbox.getText().toString();
                            if (admin_email != null && !admin_email.equalsIgnoreCase("")) {
                                String support_email = support_mailbox.getText().toString();
                                if (support_email != null && !support_email.equalsIgnoreCase("")) {
                                    HashMap<String, String> email_settings = new HashMap();
                                    email_settings.put("SMTP_SERVER", smtp_server);
                                    email_settings.put("SMTP_SERVER_PORT", smtp_server_port);
                                    email_settings.put("SENDER_MAILBOX", sender_mailbox);
                                    email_settings.put("SENDER_MAILBOX_PASSWORD", password);
                                    email_settings.put("ADMIN_ADDRESS", admin_email);
                                    email_settings.put("SALES_ADDRESS", support_email);
                                    
                                    //Check for test recepient
                                    String test_recepient = test_recepient_txt.getText().toString();
                                    if (test_recepient != null && !test_recepient.equalsIgnoreCase("")) {
                                        email_settings.put("TEST_RECEPIENT", test_recepient);
                                    } else {
                                        email_settings.put("TEST_RECEPIENT", "0");
                                    }
                                    //Save the settings
                                    boolean save_status = SystemInit.serialize_object_to_file("email_settings.txt", email_settings);
                                    if (save_status) {
                                        Config.SMTP_SERVER = email_settings.get("SMTP_SERVER");
                                        Config.SMTP_SERVER_PORT = email_settings.get("SMTP_SERVER_PORT");
                                        Config.SENDER_MAILBOX = email_settings.get("SENDER_MAILBOX");
                                        Config.SENDER_MAILBOX_PASSWORD = email_settings.get("SENDER_MAILBOX_PASSWORD");
                                        Config.TEST_RECEPIENT = email_settings.get("TEST_RECEPIENT");
                                        Config.ADMIN_ADDRESS = email_settings.get("ADMIN_ADDRESS");
                                        Config.SALES_ADDRESS = email_settings.get("SALES_ADDRESS");
                                        

                                        email_settings_status.setText("Email settings saved and set successfully.");
                                    } else {
                                        JOptionPane.showMessageDialog(
                                                AppSettings.this,
                                                "Failed to save email settings!!",
                                                "Alert",
                                                JOptionPane.WARNING_MESSAGE);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(
                                            AppSettings.this,
                                            "Enter the Support's email address for license notifications!!",
                                            "Alert",
                                            JOptionPane.WARNING_MESSAGE);

                                    support_mailbox.requestFocus();
                                }
                            } else {
                                JOptionPane.showMessageDialog(
                                        AppSettings.this,
                                        "Enter the Admin's email address for license notifications!!",
                                        "Alert",
                                        JOptionPane.WARNING_MESSAGE);

                                admin_mailbox.requestFocus();
                            }
                        } else {
                            JOptionPane.showMessageDialog(
                                    AppSettings.this,
                                    "Enter the Sender Mailbox password first!!",
                                    "Alert",
                                    JOptionPane.WARNING_MESSAGE);

                            password_txtfield.requestFocus();
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                                AppSettings.this,
                                "Enter the Sender Mailbox first!!",
                                "Alert",
                                JOptionPane.WARNING_MESSAGE);

                        smtp_port_txtfield.requestFocus();
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            AppSettings.this,
                            "Enter the SMTP Server port first!!",
                            "Alert",
                            JOptionPane.WARNING_MESSAGE);

                    smtp_port_txtfield.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(
                        AppSettings.this,
                        "Enter the SMTP Server first!!",
                        "Alert",
                        JOptionPane.WARNING_MESSAGE);

                smtp_server_txtfield.requestFocus();
            }
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import autoutils.BackgroundWorker;
import invoicetypeutils.ManageDataExtractionBounderies;
import invoicetypeutils.ManageInvoiceTypes;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.MAXIMIZED_BOTH;
import static java.awt.Frame.NORMAL;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.json.simple.JSONObject;

/**
 *
 * @author Tinashe
 */
public class Main extends javax.swing.JFrame {

    String TAG = "Main";

    /**
     * Creates new form Main
     */
    public Main() {
        super("Invoice Processing System");
        System.out.println("creating instance");
        try {
            //System.out.println("setting look and feel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Unable to set LookAndFeel");
        }
        if (SystemTray.isSupported()) {
            //System.out.println("system tray supported");
            tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().getImage("user.png");
            ActionListener exitListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //System.out.println("Exiting....");
                    System.exit(0);
                }
            };
            PopupMenu popup = new PopupMenu();
            MenuItem defaultItem = new MenuItem("Exit");
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);
            defaultItem = new MenuItem("Open");
            defaultItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                    setExtendedState(JFrame.NORMAL);
                }
            });
            popup.add(defaultItem);
            trayIcon = new TrayIcon(image, "Invoice Processing System", popup);
            trayIcon.setImageAutoSize(true);
        } else {
            System.out.println("system tray not supported");
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        e.getWindow(),
                        "Do you want to shutdown the program?",
                        "Select An Option",
                        JOptionPane.YES_NO_CANCEL_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    //Shutdown the program
                    System.exit(0);
                } else if (option == JOptionPane.NO_OPTION) {
                    try {
                        tray.add(trayIcon);
                        setVisible(false);
                    } catch (AWTException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    //do nothing
                }

            }
        });

        addWindowStateListener(new WindowStateListener() {

            public void windowStateChanged(WindowEvent e) {
                if (e.getNewState() == ICONIFIED) {
                    try {
                        tray.add(trayIcon);
                        setVisible(false);
                        //System.out.println("added to SystemTray");
                    } catch (AWTException ex) {
                        System.out.println("unable to add to tray");
                    }
                }
                if (e.getNewState() == 7) {
                    try {
                        tray.add(trayIcon);
                        setVisible(false);
                        //System.out.println("added to SystemTray");
                    } catch (AWTException ex) {
                        System.out.println("unable to add to system tray");
                    }
                }
                if (e.getNewState() == MAXIMIZED_BOTH) {
                    tray.remove(trayIcon);
                    setVisible(true);
                    //System.out.println("Tray icon removed");
                }
                if (e.getNewState() == NORMAL) {
                    tray.remove(trayIcon);
                    setVisible(true);
                    //System.out.println("Tray icon removed");
                }
            }
        });
        setIconImage(Toolkit.getDefaultToolkit().getImage("user.png"));

        initComponents();

        check_licence();

        if (Config.LICENCE_MODE == 1) {
            //Put the app in auto mode
            run_in_auto_mode();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        main_content_pane = new javax.swing.JPanel();
        license_status = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        bounderiesMenuItem = new javax.swing.JMenuItem();
        running_mode = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        settingsMenuItem = new javax.swing.JMenuItem();
        manage_UOMS = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 700));

        license_status.setBackground(new java.awt.Color(255, 255, 255));
        license_status.setFont(new java.awt.Font("Trebuchet MS", 2, 8)); // NOI18N
        license_status.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        license_status.setText("-");

        javax.swing.GroupLayout main_content_paneLayout = new javax.swing.GroupLayout(main_content_pane);
        main_content_pane.setLayout(main_content_paneLayout);
        main_content_paneLayout.setHorizontalGroup(
            main_content_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, main_content_paneLayout.createSequentialGroup()
                .addContainerGap(462, Short.MAX_VALUE)
                .addComponent(license_status, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        main_content_paneLayout.setVerticalGroup(
            main_content_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, main_content_paneLayout.createSequentialGroup()
                .addContainerGap(454, Short.MAX_VALUE)
                .addComponent(license_status)
                .addContainerGap())
        );

        fileMenu.setText("File");
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jMenuItem1.setText("Invoice Types");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        bounderiesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        bounderiesMenuItem.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        bounderiesMenuItem.setText("Manage Data Bounderies");
        bounderiesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bounderiesMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(bounderiesMenuItem);

        running_mode.setText("Run Auto Mode");
        running_mode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                running_modeActionPerformed(evt);
            }
        });
        fileMenu.add(running_mode);

        jMenuBar.add(fileMenu);

        editMenu.setText("Edit");
        editMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMenuActionPerformed(evt);
            }
        });

        settingsMenuItem.setText("App Settings");
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(settingsMenuItem);

        manage_UOMS.setText("Manage UOMs");
        manage_UOMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_UOMSActionPerformed(evt);
            }
        });
        editMenu.add(manage_UOMS);

        jMenuBar.add(editMenu);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(main_content_pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(main_content_pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        main_content_pane.removeAll();
        main_content_pane.setLayout(new BorderLayout());
        main_content_pane.add(new ManageInvoiceTypes(), BorderLayout.CENTER);
        main_content_pane.validate();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed

    }//GEN-LAST:event_fileMenuActionPerformed

    private void bounderiesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bounderiesMenuItemActionPerformed
        main_content_pane.removeAll();
        main_content_pane.setLayout(new BorderLayout());
        main_content_pane.add(new ManageDataExtractionBounderies(), BorderLayout.CENTER);
        main_content_pane.validate();
    }//GEN-LAST:event_bounderiesMenuItemActionPerformed

    private void running_modeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_running_modeActionPerformed
        if (current_mode.equalsIgnoreCase("manual")) {
            run_in_auto_mode();
        } else if (current_mode.equalsIgnoreCase("auto")) {
            run_in_manual_mode(0);
        }

        /*
         
         */
    }//GEN-LAST:event_running_modeActionPerformed

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
        main_content_pane.removeAll();
        main_content_pane.setLayout(new BorderLayout());
        main_content_pane.add(new AppSettings(), BorderLayout.CENTER);
        main_content_pane.validate();
    }//GEN-LAST:event_settingsMenuItemActionPerformed

    private void manage_UOMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manage_UOMSActionPerformed
        main_content_pane.removeAll();
        main_content_pane.setLayout(new BorderLayout());
        main_content_pane.add(new UOMS(), BorderLayout.CENTER);
        main_content_pane.validate();
    }//GEN-LAST:event_manage_UOMSActionPerformed

    private void editMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editMenuActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JMenuItem bounderiesMenuItem;
    private static javax.swing.JMenu editMenu;
    private static javax.swing.JMenu fileMenu;
    private static javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuItem jMenuItem1;
    private static javax.swing.JLabel license_status;
    private static javax.swing.JPanel main_content_pane;
    private javax.swing.JMenuItem manage_UOMS;
    private static javax.swing.JMenuItem running_mode;
    private static javax.swing.JMenuItem settingsMenuItem;
    // End of variables declaration//GEN-END:variables

    TrayIcon trayIcon;
    SystemTray tray;
    static String current_mode = "manual";

    void run_in_auto_mode() {
        running_mode.setText("Run Manual Mode");
        current_mode = "auto";
        BackgroundWorker.stop_background_tasks = false;
        //Add the monitoring view
        main_content_pane.removeAll();
        main_content_pane.setLayout(new BorderLayout());
        main_content_pane.add(new AutoModeMonitor(), BorderLayout.CENTER);
        main_content_pane.validate();
        //Start the background task
        new BackgroundWorker();
    }

    public static void run_in_manual_mode(int mode) {
        running_mode.setText("Run Auto Mode");
        current_mode = "manual";
        BackgroundWorker.stop_background_tasks = true;
        BackgroundWorker.executor.shutdown();

        main_content_pane.removeAll();
        main_content_pane.setLayout(new BorderLayout());
        if(mode == 0){
            main_content_pane.add(new ManageDataExtractionBounderies(), BorderLayout.CENTER);
        }else if(mode == 1){//License expired
            main_content_pane.add(new LicensingMenu(), BorderLayout.CENTER);
        }
        
        main_content_pane.validate();
    }

    void check_licence() {
        //Get licensing info
        Config.CURRENT_MACHINE_INFO = SystemInit.get_system_info();
        if (Config.CURRENT_MACHINE_INFO != null && Config.CURRENT_MACHINE_INFO.size() > 0) {
            //Read the saved machine info
            Config.SAVED_MACHINE_INFO = (HashMap<String, String>) SystemInit.deserialize_object_from_file("aclin");
            if (Config.SAVED_MACHINE_INFO != null && Config.SAVED_MACHINE_INFO.size() > 0) {
                //Compare the parameters
                if (Config.CURRENT_MACHINE_INFO.get("OS_Type").equals(Config.SAVED_MACHINE_INFO.get("OS_Type"))) {
                    if (Config.CURRENT_MACHINE_INFO.get("UUID").equals(Config.SAVED_MACHINE_INFO.get("UUID"))) {
                        //Same machine detected, check the license expiry status
                        LocalDate current_date = SystemInit.string_to_localdate(Config.CURRENT_MACHINE_INFO.get("CURRENT_DATE"));
                        LocalDate expiry_date = SystemInit.string_to_localdate(Config.SAVED_MACHINE_INFO.get("DATE_OF_EXPIRY"));
                        LocalDate reminder_date = SystemInit.string_to_localdate(Config.SAVED_MACHINE_INFO.get("DATE_OF_REMINDER"));

                        int result = expiry_date.compareTo(current_date);
                        long days = DAYS.between(current_date, expiry_date);
                        if (result < 0) {
                            System.out.println(days + " days after exiry date..");
                            license_status.setText("Licence expired " + days + " days ago.");
                            license_status.setForeground(Color.red);
                        } else if (result > 0) {
                            System.out.println(days + " days before exiry date..");
                            license_status.setText("Licence expires in " + days + " days.");
                            license_status.setForeground(Color.black);
                        } else if (result == 0) {
                            System.out.println("Now is exiry date..");
                            license_status.setText("Licence expires in today.");
                            license_status.setForeground(Color.black);
                            
                        }
                        
                        if(Config.SAVED_MACHINE_INFO.get("Status").equalsIgnoreCase("registered active")){
                            //Run app in normal mode
                            Config.LICENCE_MODE = 1;
                        }else{
                            //App needs activation
                            Config.LICENCE_MODE = 0;
                        }
                        initializeApp();
                    } else {
                        //Different UUID detected from activated type i.e re-installation detected with same OS type
                        System.out.println("OS re-installation detected.");
                        //Set activation status
                        //Config.CURRENT_MACHINE_INFO.put("Status", "inactive");
                        //Read and save this machines' details
                        if (SystemInit.serialize_object_to_file("aclin", Config.CURRENT_MACHINE_INFO)) {
                            System.out.println("Re-installation licence info saved.");
                            System.out.println(Config.CURRENT_MACHINE_INFO);
                            //Assign the current info to the saved info map
                            Config.SAVED_MACHINE_INFO = Config.CURRENT_MACHINE_INFO;
                            //Show license verification/activation screen
                            Config.LICENCE_MODE = 2;
                            initializeApp();
                        } else {
                            System.out.println("Failed to save re-installation licence info.");
                            license_status.setText("Failed to save re-installation licence info..");
                            license_status.setForeground(Color.red);
                        }
                    }
                } else {
                    //Different OS detected from activated type i.e new installation detected
                    System.out.println("Different OS detected from activated type.");
                    //Set activation status
                    Config.CURRENT_MACHINE_INFO.put("Status", "inactive");
                    //Read and save this machines' details
                    if (SystemInit.serialize_object_to_file("aclin", Config.CURRENT_MACHINE_INFO)) {
                        System.out.println("Fresh installation licence info saved.");
                        System.out.println(Config.CURRENT_MACHINE_INFO);
                        //Assign the current info to the saved info map
                        Config.SAVED_MACHINE_INFO = Config.CURRENT_MACHINE_INFO;
                        //Show license verification/activation screen
                        Config.LICENCE_MODE = 3;
                        initializeApp();
                    } else {
                        System.out.println("Failed to save fresh installation licence info.");
                        license_status.setText("Failed to save fresh installation licence info.");
                        license_status.setForeground(Color.red);
                    }
                }
            } else {
                //Failed to read current machine info i.e new installation detected
                System.out.println("Failed to read saved machine info.");
                //Set activation status
                Config.CURRENT_MACHINE_INFO.put("Status", "inactive");
                //Read and save this machines' details
                if (SystemInit.serialize_object_to_file("aclin", Config.CURRENT_MACHINE_INFO)) {
                    System.out.println("New installation licence info saved.");
                    System.out.println(Config.CURRENT_MACHINE_INFO);
                    //Assign the current info to the saved info map
                    Config.SAVED_MACHINE_INFO = Config.CURRENT_MACHINE_INFO;
                    //Show license verification/activation screen
                    Config.LICENCE_MODE = 4;
                    initializeApp();
                } else {
                    System.out.println("Failed to save new installation licence info.");
                    license_status.setText("Failed to save new installation licence info.");
                    license_status.setForeground(Color.red);
                }
            }
        } else {
            //Failed to read current machine info
            System.out.println("Failed to read current machine info.");
            Config.LICENCE_MODE = 5;
            initializeApp();
        }
    }

    public static void initializeApp() {
        System.out.println("Config.LICENCE_MODE: " + Config.LICENCE_MODE);
        if (Config.LICENCE_MODE == 1) {
            //Load UOMs
            Config.UNIT_OF_MEASUREMENTS = (ArrayList<String>) SystemInit.deserialize_object_from_file("uoms.txt");

            //Load the folder paths settings
            HashMap<String, String> folder_paths = (HashMap) SystemInit.deserialize_object_from_file("folder_paths.txt");
            if (folder_paths != null) {
                //Set the folder global variables
                Config.SAP_INVOICES_PATH = folder_paths.get("SAP_INVOICES_PATH");
                Config.PRN_FILES_PATH = folder_paths.get("PRN_FILES_PATH");
                Config.HTML_RECEIPTS_PATH = folder_paths.get("HTML_RECEIPTS_PATH");
                Config.SIGNED_INVOICES_PATH = folder_paths.get("SIGNED_INVOICES_PATH");
            } else {
                System.out.println("Failed to load folder paths.");
            }

            //Load the email settings
            HashMap<String, String> email_settings = (HashMap) SystemInit.deserialize_object_from_file("email_settings.txt");
            if (email_settings != null) {
                //Set the email settings global variables
                Config.SMTP_SERVER = email_settings.get("SMTP_SERVER");
                Config.SMTP_SERVER_PORT = email_settings.get("SMTP_SERVER_PORT");
                Config.SENDER_MAILBOX = email_settings.get("SENDER_MAILBOX");
                Config.SENDER_MAILBOX_PASSWORD = email_settings.get("SENDER_MAILBOX_PASSWORD");
                Config.TEST_RECEPIENT = email_settings.get("TEST_RECEPIENT");
                Config.ADMIN_ADDRESS = email_settings.get("ADMIN_ADDRESS");
                Config.SALES_ADDRESS = email_settings.get("SALES_ADDRESS");
                
            } else {
                System.out.println("Failed to load email settings.");
            }
            //Get the existing invoice types 
            Config.INVOICE_TYPES = (ArrayList) SystemInit.deserialize_object_from_file("invoice_types");
            //Get the existing invoice types 
            Config.INVOICE_TAGS = (HashMap<String, ArrayList<String>>) SystemInit.deserialize_object_from_file("invoice_type_tags");
            //Load required data fields
            SystemInit.load_required_data_fields();
            //Get the existing invoice types 
            Config.INVOICE_DATA_BOUNDERIES = (JSONObject) SystemInit.deserialize_object_from_file("invoice_data_bounderies");
            //System.out.println(TAG + ": initializeApp(): Config.INVOICE_DATA_BOUNDERIES:");
            //System.out.println(Config.INVOICE_DATA_BOUNDERIES);
            //Load data extraction error msgs
            SystemInit.load_data_extraction_errors();

            //Initialize the database
            SystemInit.initDB();
            //Load database error msgs
            SystemInit.load_database_errors();

        } else {
            //Show licencing screen

            main_content_pane.removeAll();
            main_content_pane.setLayout(new BorderLayout());
            main_content_pane.add(new LicensingMenu(), BorderLayout.CENTER);
            main_content_pane.validate();
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Tinashe
 */
public class UOMS extends javax.swing.JPanel {

    /**
     * Creates new form UOMS
     */
    public UOMS() {
        initComponents();

        add_uom_btn.addActionListener(new AddNewUOMListener());
        save_UOMS_btn.addActionListener(new SaveUOMsListener());

        load_existing_uom_values();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        uom_txtfield = new javax.swing.JTextField();
        add_uom_btn = new javax.swing.JButton();
        jscrollpane = new javax.swing.JScrollPane();
        uoms_list = new javax.swing.JList();
        save_UOMS_btn = new javax.swing.JButton();
        remove_UOM_btn = new javax.swing.JButton();
        save_status = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Add/Remove Units Of Measurents", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 0, 11))); // NOI18N

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel1.setText("New UOM value:");

        uom_txtfield.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        add_uom_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        add_uom_btn.setText("Add UOM");

        jscrollpane.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N

        uoms_list.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jscrollpane.setViewportView(uoms_list);

        save_UOMS_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        save_UOMS_btn.setText("Save UOMs");

        remove_UOM_btn.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        remove_UOM_btn.setText("<<Remove UOM");

        save_status.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        save_status.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        save_status.setText("-");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jscrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(uom_txtfield))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(remove_UOM_btn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(save_UOMS_btn))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(add_uom_btn))
                    .addComponent(save_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(uom_txtfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(add_uom_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jscrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(save_UOMS_btn)
                    .addComponent(remove_UOM_btn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(save_status)
                .addContainerGap(119, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 281, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton add_uom_btn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private static javax.swing.JScrollPane jscrollpane;
    private static javax.swing.JButton remove_UOM_btn;
    private static javax.swing.JButton save_UOMS_btn;
    private static javax.swing.JLabel save_status;
    private static javax.swing.JTextField uom_txtfield;
    private static javax.swing.JList uoms_list;
    // End of variables declaration//GEN-END:variables
    DefaultListModel uomsListModel = new DefaultListModel();

    void load_existing_uom_values() {
        if (Config.UNIT_OF_MEASUREMENTS == null) {
            Config.UNIT_OF_MEASUREMENTS = (ArrayList<String>) SystemInit.deserialize_object_from_file("uoms.txt");
        }

        if (Config.UNIT_OF_MEASUREMENTS != null && Config.UNIT_OF_MEASUREMENTS.size() > 0) {
            for (int i = 0; i < Config.UNIT_OF_MEASUREMENTS.size(); i++) {
                uomsListModel.addElement(Config.UNIT_OF_MEASUREMENTS.get(i));
            }

            uoms_list.setModel(uomsListModel);
        }
    }

    class AddNewUOMListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String new_uom_value = (String) uom_txtfield.getText();
            if (new_uom_value != null && !new_uom_value.equalsIgnoreCase("")) {
                //Add to Config.UNIT_OF_MEASUREMENTS array
                if (Config.UNIT_OF_MEASUREMENTS == null) {
                    Config.UNIT_OF_MEASUREMENTS = new ArrayList();
                }
                Config.UNIT_OF_MEASUREMENTS.add(new_uom_value);
                //Display on uoms list
                add_to_uoms_list(new_uom_value);
                
                uom_txtfield.setText("");
                uom_txtfield.requestFocus();
            } else {
                JOptionPane.showMessageDialog(
                        UOMS.this,
                        "Enter new UOM value first!!",
                        "Alert",
                        JOptionPane.WARNING_MESSAGE);
                uom_txtfield.requestFocus();
            }
        }

        void add_to_uoms_list(String uom_value) {
            uomsListModel.addElement(uom_value);

            uoms_list.setModel(uomsListModel);
        }

    }

    class SaveUOMsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (Config.UNIT_OF_MEASUREMENTS != null && Config.UNIT_OF_MEASUREMENTS.size() > 0) {
                boolean save_oums_status = SystemInit.serialize_object_to_file("uoms.txt", Config.UNIT_OF_MEASUREMENTS);
                if (save_oums_status) {
                    save_status.setText("UOM values saved successfully.");
                } else {
                    JOptionPane.showMessageDialog(
                            UOMS.this,
                            "Failed to save UOM values!!",
                            "Alert",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(
                        UOMS.this,
                        "Specify UOM values first!!",
                        "Alert",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

    }
}

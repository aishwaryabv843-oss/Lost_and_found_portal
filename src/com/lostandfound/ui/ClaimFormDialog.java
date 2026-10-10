package com.lostandfound.ui;

import com.lostandfound.database.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClaimFormDialog - JDialog popup that captures the complete student claim verification details,
 * validates against duplicate claims, and saves it inside the MySQL database using JDBC.
 */
public class ClaimFormDialog extends JDialog {
    private StudentDashboard parent;
    private int itemId;
    private String itemTitle;
    
    // UI Components
    private JTextField txtStudentName;
    private JTextField txtUsn;
    private JTextField txtContact;
    private JComboBox<String> comboDept;
    private JTextArea txtProof;
    private JTextField txtDetails;
    private JLabel lblImageStatus;
    
    private File selectedProofFile = null;
    
    // Theme Colors
    private final Color primaryColor = new Color(37, 99, 235);    // Royal Blue
    private final Color secondaryColor = new Color(30, 58, 138);  // Navy
    private final Color backgroundColor = new Color(248, 250, 252); // Slate light

    public ClaimFormDialog(StudentDashboard parent, int itemId, String itemTitle) {
        super(parent, "File Ownership Verification Claim Match", true);
        this.parent = parent;
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        
        setSize(550, 600);
        setLocationRelativeTo(parent);
        setResizable(true);
        
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(backgroundColor);
        
        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout(5, 2));
        headerPanel.setBackground(secondaryColor);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel lblHeader = new JLabel("📁 Submit Ownership Claim Details", JLabel.LEFT);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Claiming Item: " + itemTitle + " (ID: #" + itemId + ")", JLabel.LEFT);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(191, 219, 254));
        headerPanel.add(lblHeader, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        rootPanel.add(headerPanel, BorderLayout.NORTH);
        
        // 2. Form Body
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;
        
        // Fields Initialization
        txtStudentName = new JTextField();
        txtUsn = new JTextField();
        txtContact = new JTextField();
        txtDetails = new JTextField();
        
        // Custom Area
        txtProof = new JTextArea(3, 30);
        txtProof.setLineWrap(true);
        txtProof.setWrapStyleWord(true);
        JScrollPane proofScroll = new JScrollPane(txtProof);
        
        // Dept Combo
        String[] depts = {
            "Computer Science & Eng",
            "Information Science & Eng",
            "Electronics & Communication",
            "Mechanical Engineering",
            "Civil Engineering"
        };
        comboDept = new JComboBox<>(depts);
        
        // Setup Prefills from session
        if (parent != null) {
            txtStudentName.setText(parent.getStudentName());
            txtUsn.setText(parent.getStudentUsn());
            String dept = parent.getStudentDept();
            if (dept != null) {
                boolean found = false;
                for (int i = 0; i < comboDept.getItemCount(); i++) {
                    String item = comboDept.getItemAt(i);
                    if (item.equalsIgnoreCase(dept) || item.toLowerCase().startsWith(dept.toLowerCase()) || dept.toLowerCase().startsWith(item.toLowerCase())) {
                        comboDept.setSelectedIndex(i);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    comboDept.addItem(dept);
                    comboDept.setSelectedItem(dept);
                }
            }
        }
        
        // Grid placement
        gbc.gridx = 0; gbc.gridy = 0;
        bodyPanel.add(createBoldLabel("Student Name *"), gbc);
        gbc.gridx = 1;
        bodyPanel.add(txtStudentName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        bodyPanel.add(createBoldLabel("University Seat No (USN) *"), gbc);
        gbc.gridx = 1;
        bodyPanel.add(txtUsn, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        bodyPanel.add(createBoldLabel("Contact Phone No *"), gbc);
        gbc.gridx = 1;
        bodyPanel.add(txtContact, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        bodyPanel.add(createBoldLabel("Department"), gbc);
        gbc.gridx = 1;
        bodyPanel.add(comboDept, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        bodyPanel.add(createBoldLabel("Proof of Ownership *"), gbc);
        gbc.gridx = 1;
        bodyPanel.add(proofScroll, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        bodyPanel.add(createBoldLabel("Additional Identifiers"), gbc);
        gbc.gridx = 1;
        bodyPanel.add(txtDetails, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        bodyPanel.add(createBoldLabel("Supporting ID/Proof File"), gbc);
        
        gbc.gridx = 1;
        JPanel uploadPanel = new JPanel(new BorderLayout(8, 2));
        uploadPanel.setBackground(Color.WHITE);
        JButton btnUpload = new JButton("📁 Browse File...");
        btnUpload.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnUpload.addActionListener(this::handleProofUpload);
        lblImageStatus = new JLabel("No image selected", JLabel.LEFT);
        lblImageStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblImageStatus.setForeground(Color.GRAY);
        uploadPanel.add(btnUpload, BorderLayout.WEST);
        uploadPanel.add(lblImageStatus, BorderLayout.CENTER);
        bodyPanel.add(uploadPanel, gbc);
        
        rootPanel.add(bodyPanel, BorderLayout.CENTER);
        
        // 3. Actions Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footerPanel.setBackground(backgroundColor);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        
        JButton btnSubmit = new JButton("Submit Claim");
        btnSubmit.setBackground(primaryColor);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSubmit.setPreferredSize(new Dimension(120, 32));
        btnSubmit.addActionListener(this::handleSubmit);
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setPreferredSize(new Dimension(80, 32));
        btnCancel.addActionListener(e -> dispose());
        
        footerPanel.add(btnSubmit);
        footerPanel.add(btnCancel);
        rootPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(rootPanel);
    }

    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(15, 23, 42));
        return label;
    }

    private void handleProofUpload(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Supporting ID/Purchase Receipt");
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedProofFile = chooser.getSelectedFile();
            lblImageStatus.setText(selectedProofFile.getName() + " (" + (selectedProofFile.length() / 1024) + " KB)");
            lblImageStatus.setForeground(new Color(16, 185, 129));
        }
    }

    private void handleSubmit(ActionEvent e) {
        String name = txtStudentName.getText().trim();
        String usn = txtUsn.getText().trim();
        String contact = txtContact.getText().trim();
        String dept = (String) comboDept.getSelectedItem();
        String proof = txtProof.getText().trim();
        String details = txtDetails.getText().trim();

        System.out.println("[DEBUG] Detected active student submission session USN: " + usn);

        if (name.isEmpty() || usn.isEmpty() || contact.isEmpty() || proof.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Please fill in all required (*) fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check duplicate claims
        String duplicateCheckSQL = "SELECT COUNT(*) FROM claims WHERE item_id = ? AND usn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPstmt = conn.prepareStatement(duplicateCheckSQL)) {
             
            checkPstmt.setInt(1, itemId);
            checkPstmt.setString(2, usn);
            
            try (ResultSet rs = checkPstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "⚠️ You have already submitted a claim for this item.", 
                        "Duplicate Claim Notice", 
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Insert claim into SQL database
        String insertSQL = "INSERT INTO claims (item_id, student_name, usn, contact, department, proof, claim_date, uploaded_proof, claim_status) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setInt(1, itemId);
            pstmt.setString(2, name);
            pstmt.setString(3, usn);
            pstmt.setString(4, contact);
            pstmt.setString(5, dept);
            pstmt.setString(6, proof);
            pstmt.setDate(7, new java.sql.Date(System.currentTimeMillis()));

            if (selectedProofFile != null) {
                FileInputStream fis = new FileInputStream(selectedProofFile);
                pstmt.setBinaryStream(8, fis, (int) selectedProofFile.length());
            } else {
                pstmt.setNull(8, java.sql.Types.BLOB);
            }

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[DEBUG] Claim notice successfully inserted into claims table for item ID: " + itemId);
                // Update lost_items table status to 'Claimed' for this item
                String updateItemSQL = "UPDATE lost_items SET status = 'Claimed' WHERE id = ?";
                try (PreparedStatement updatePstmt = conn.prepareStatement(updateItemSQL)) {
                    updatePstmt.setInt(1, itemId);
                    updatePstmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, 
                    "🎉 Claim Submitted Successfully", 
                    "Claim Match Registry", 
                    JOptionPane.INFORMATION_MESSAGE);
                parent.loadClaimsData(); // Refresh parent claims table
                dispose();
            }
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Database Write Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

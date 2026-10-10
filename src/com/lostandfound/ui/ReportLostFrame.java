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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ReportLostFrame - A modern, responsive, and fully editable Java Swing form
 * for reporting lost items on the College Lost & Found Portal.
 * Fully integrates with MySQL database via JDBC.
 */
public class ReportLostFrame extends JFrame {
    private StudentDashboard parentDashboard;
    
    // UI Form Fields
    private JTextFieldPlaceholder txtStudentName;
    private JTextFieldPlaceholder txtUsn;
    private JTextFieldPlaceholder txtContact;
    private JComboBox<String> comboDept;
    private JTextFieldPlaceholder txtItemName;
    private JTextAreaPlaceholder txtDescription;
    private JTextFieldPlaceholder txtPlaceLost;
    private JTextFieldPlaceholder txtDateLost;
    private JComboBox<String> comboDepositedTo;
    
    // Image Upload Attributes
    private File selectedImageFile = null;
    private JLabel lblImageStatus;
    
    // Theme Colors
    private final Color primaryColor = new Color(37, 99, 235);    // Royal Blue
    private final Color secondaryColor = new Color(30, 58, 138);  // Dark Navy Blue
    private final Color backgroundColor = new Color(248, 250, 252); // Slate light gray
    private final Color textColor = new Color(15, 23, 42);         // Slate dark text

    public ReportLostFrame(StudentDashboard parentDashboard) {
        this.parentDashboard = parentDashboard;
        
        setTitle("Report Lost Item Registry");
        setSize(650, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Root Panel
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(backgroundColor);
        
        // 1. HEADER SECTION
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);
        
        // 2. FORM BODY PANEL
        JPanel bodyPanel = createFormBodyPanel();
        JScrollPane scrollPane = new JScrollPane(bodyPanel);
        scrollPane.setBorder(null);
        rootPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 3. FOOTER ACTIONS ROW
        JPanel footerPanel = createFooterPanel();
        rootPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(rootPanel);
        
        // Prefill default logged-in student session info
        if (parentDashboard != null) {
            // Safe pre-fill but completely typeable!
            txtStudentName.setRealText(parentDashboard.getTitle().contains("Welcome") ? "" : "Aishu R. Kumar");
        }
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(secondaryColor);
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel lblTitle = new JLabel("🌐 FindMe Portal", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSubtitle = new JLabel("College Lost & Found Portal - New Lost Item Registry Notice", JLabel.LEFT);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(191, 219, 254));
        
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblSubtitle, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFormBodyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;
        
        // Initialize editable placeholders
        txtStudentName = new JTextFieldPlaceholder("Enter student full name");
        txtUsn = new JTextFieldPlaceholder("Enter 10-digit University Seat No (USN)");
        txtContact = new JTextFieldPlaceholder("Enter active mobile contact number");
        txtItemName = new JTextFieldPlaceholder("e.g., Red Redmi Note 13 phone");
        txtPlaceLost = new JTextFieldPlaceholder("e.g., Computer Science Lab-3, Desk 4");
        
        // Set date lost default value as current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtDateLost = new JTextFieldPlaceholder(sdf.format(new Date()));
        
        txtDescription = new JTextAreaPlaceholder("Describe physical marks, cases, stickers, or distinguishing details...", 4, 30);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setPreferredSize(new Dimension(300, 80));
        
        // Departments JComboBox
        String[] depts = {
            "Computer Science & Eng",
            "Information Science & Eng",
            "Electronics & Communication",
            "Electrical & Electronics",
            "Mechanical Engineering",
            "Civil Engineering",
            "Chemical Engineering"
        };
        comboDept = new JComboBox<>(depts);
        comboDept.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboDept.setPreferredSize(new Dimension(300, 32));
        comboDept.setBackground(Color.WHITE);

        // Custody Locations Dropdown
        String[] custodyLocations = {
            "Security Room",
            "College Office",
            "Lab Incharge"
        };
        comboDepositedTo = new JComboBox<>(custodyLocations);
        comboDepositedTo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboDepositedTo.setPreferredSize(new Dimension(300, 32));
        comboDepositedTo.setBackground(Color.WHITE);

        // Group 1: Student Details Header
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblSectionStudent = new JLabel("🎓 Student & Reporter Profile");
        lblSectionStudent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSectionStudent.setForeground(secondaryColor);
        panel.add(lblSectionStudent, gbc);
        
        // Student Name
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(createIconLabel("👤 Student Name", true), gbc);
        gbc.gridx = 1;
        panel.add(txtStudentName, gbc);
        
        // USN
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createIconLabel("🪪 Seat Number (USN)", true), gbc);
        gbc.gridx = 1;
        panel.add(txtUsn, gbc);
        
        // Contact No
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createIconLabel("📞 Contact Number", true), gbc);
        gbc.gridx = 1;
        panel.add(txtContact, gbc);
        
        // Department
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createIconLabel("🏢 Academic Department", true), gbc);
        gbc.gridx = 1;
        panel.add(comboDept, gbc);
        
        // Divider
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(Box.createVerticalStrut(10), gbc);

        // Group 2: Item Details Header
        gbc.gridy = 6;
        JLabel lblSectionItem = new JLabel("📦 Lost Belonging Information");
        lblSectionItem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSectionItem.setForeground(secondaryColor);
        panel.add(lblSectionItem, gbc);
        
        // Lost Item Name
        gbc.gridy = 7; gbc.gridwidth = 1;
        panel.add(createIconLabel("🔑 Lost Item Name", true), gbc);
        gbc.gridx = 1;
        panel.add(txtItemName, gbc);
        
        // Place Lost
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(createIconLabel("📍 Place Lost", true), gbc);
        gbc.gridx = 1;
        panel.add(txtPlaceLost, gbc);
        
        // Safe Custody deposited to
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(createIconLabel("🏢 Deposited Custody Location", true), gbc);
        gbc.gridx = 1;
        panel.add(comboDepositedTo, gbc);

        // Date Lost
        gbc.gridx = 0; gbc.gridy = 10;
        panel.add(createIconLabel("📅 Date Lost (YYYY-MM-DD)", true), gbc);
        gbc.gridx = 1;
        panel.add(txtDateLost, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 11;
        panel.add(createIconLabel("📝 Item Description", false), gbc);
        gbc.gridx = 1;
        panel.add(descScroll, gbc);

        // Image Selection Row
        gbc.gridx = 0; gbc.gridy = 12;
        panel.add(createIconLabel("📷 Item Photograph", false), gbc);
        
        gbc.gridx = 1;
        JPanel imageUploadPanel = new JPanel(new BorderLayout(10, 5));
        imageUploadPanel.setBackground(Color.WHITE);
        
        JButton btnUpload = new JButton("📁 Choose File...");
        btnUpload.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnUpload.addActionListener(this::handleImageUpload);
        
        lblImageStatus = new JLabel("No image file selected", JLabel.LEFT);
        lblImageStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblImageStatus.setForeground(Color.GRAY);
        
        imageUploadPanel.add(btnUpload, BorderLayout.WEST);
        imageUploadPanel.add(lblImageStatus, BorderLayout.CENTER);
        panel.add(imageUploadPanel, gbc);
        
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        
        RoundedButton btnSubmit = new RoundedButton("Submit Report");
        btnSubmit.setPreferredSize(new Dimension(140, 38));
        btnSubmit.setBackground(primaryColor);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.addActionListener(this::handleSubmit);
        
        RoundedButton btnClear = new RoundedButton("Clear Form");
        btnClear.setPreferredSize(new Dimension(120, 38));
        btnClear.setBackground(new Color(226, 232, 240));
        btnClear.setForeground(new Color(71, 85, 105));
        btnClear.addActionListener(e -> clearForm());
        
        RoundedButton btnBack = new RoundedButton("Back to Dashboard");
        btnBack.setPreferredSize(new Dimension(160, 38));
        btnBack.setBackground(new Color(15, 23, 42)); // Slate 900
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> handleBackToDashboard());
        
        panel.add(btnSubmit);
        panel.add(btnClear);
        panel.add(btnBack);
        return panel;
    }

    private JLabel createIconLabel(String text, boolean required) {
        String labelText = required ? text + " *" : text;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(textColor);
        return label;
    }

    private void handleImageUpload(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Item Photograph");
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            lblImageStatus.setText(selectedImageFile.getName() + " (" + (selectedImageFile.length() / 1024) + " KB)");
            lblImageStatus.setForeground(new Color(16, 185, 129)); // Successful green
        }
    }

    private void handleSubmit(ActionEvent e) {
        String name = txtStudentName.getRealText();
        String usn = txtUsn.getRealText();
        String contact = txtContact.getRealText();
        String dept = (String) comboDept.getSelectedItem();
        String itemName = txtItemName.getRealText();
        String place = txtPlaceLost.getRealText();
        String dateStr = txtDateLost.getRealText();
        String desc = txtDescription.getRealText();
        String depositedTo = (String) comboDepositedTo.getSelectedItem();

        // 1. Validation check for required fields
        if (name.isEmpty() || usn.isEmpty() || contact.isEmpty() || itemName.isEmpty() || place.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ DBMS Validation Error: Please fill in all required (*) fields!", 
                "Submission Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate date format YYYY-MM-DD
        java.sql.Date sqlDate;
        try {
            sqlDate = java.sql.Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Date Format Error: Please input a valid date in YYYY-MM-DD format!", 
                "Format Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. JDBC Connection and Database insert transaction
        String insertSQL = "INSERT INTO lost_items (student_name, usn, contact, department, title, description, image_blob, image_path, date_lost, location_lost, status, deposited_to) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending', ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
             
            pstmt.setString(1, name);
            pstmt.setString(2, usn);
            pstmt.setString(3, contact);
            pstmt.setString(4, dept);
            pstmt.setString(5, itemName);
            pstmt.setString(6, desc);

            // Set image BLOB binary stream
            if (selectedImageFile != null) {
                FileInputStream fis = new FileInputStream(selectedImageFile);
                pstmt.setBinaryStream(7, fis, (int) selectedImageFile.length());
                pstmt.setString(8, selectedImageFile.getAbsolutePath()); // Save absolute path in image_path
            } else {
                pstmt.setNull(7, java.sql.Types.BLOB);
                pstmt.setNull(8, java.sql.Types.VARCHAR);
            }

            pstmt.setDate(9, sqlDate);
            pstmt.setString(10, place);
            pstmt.setString(11, depositedTo);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, 
                    "🎉 Lost Item Report Submitted Successfully", 
                    "Registry Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                handleBackToDashboard();
            }
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "❌ Database Transaction Error: " + ex.getMessage(), 
                "Write Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        txtStudentName.clear();
        txtUsn.clear();
        txtContact.clear();
        comboDept.setSelectedIndex(0);
        txtItemName.clear();
        txtDescription.clear();
        txtPlaceLost.clear();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtDateLost.setRealText(sdf.format(new Date()));
        
        selectedImageFile = null;
        lblImageStatus.setText("No image file selected");
        lblImageStatus.setForeground(Color.GRAY);
    }

    private void handleBackToDashboard() {
        if (parentDashboard != null) {
            parentDashboard.setVisible(true);
        } else {
            // Safe fallback launcher
            new StudentDashboard("1RV24CS015", "Aishu R. Kumar", "Computer Science & Eng").setVisible(true);
        }
        this.dispose();
    }

    // ==========================================================================
    // CUSTOM HELPER INNER CLASSES FOR MODERN TEXT PLACEHOLDERS & ROUNDED BUTTONS
    // ==========================================================================
    
    private static class JTextFieldPlaceholder extends JTextField {
        private String placeholder;
        private boolean isPlaceholderActive = true;

        public JTextFieldPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            setForeground(Color.GRAY);
            setText(placeholder);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setPreferredSize(new Dimension(300, 32));
            
            addFocusListener(new java.awt.event.FocusListener() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (isPlaceholderActive) {
                        setText("");
                        setForeground(Color.BLACK);
                        isPlaceholderActive = false;
                    }
                }
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (getText().trim().isEmpty()) {
                        setText(placeholder);
                        setForeground(Color.GRAY);
                        isPlaceholderActive = true;
                    }
                }
            });
        }

        public String getRealText() {
            return isPlaceholderActive ? "" : getText().trim();
        }

        public void setRealText(String val) {
            if (val == null || val.trim().isEmpty()) {
                clear();
            } else {
                setText(val);
                setForeground(Color.BLACK);
                isPlaceholderActive = false;
            }
        }

        public void clear() {
            setText(placeholder);
            setForeground(Color.GRAY);
            isPlaceholderActive = true;
        }
    }

    private static class JTextAreaPlaceholder extends JTextArea {
        private String placeholder;
        private boolean isPlaceholderActive = true;

        public JTextAreaPlaceholder(String placeholder, int rows, int cols) {
            super(rows, cols);
            this.placeholder = placeholder;
            setForeground(Color.GRAY);
            setText(placeholder);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            addFocusListener(new java.awt.event.FocusListener() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (isPlaceholderActive) {
                        setText("");
                        setForeground(Color.BLACK);
                        isPlaceholderActive = false;
                    }
                }
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (getText().trim().isEmpty()) {
                        setText(placeholder);
                        setForeground(Color.GRAY);
                        isPlaceholderActive = true;
                    }
                }
            });
        }

        public String getRealText() {
            return isPlaceholderActive ? "" : getText().trim();
        }

        public void clear() {
            setText(placeholder);
            setForeground(Color.GRAY);
            isPlaceholderActive = true;
        }
    }

    private static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getLeading() - fm.getDescent()) / 2;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}

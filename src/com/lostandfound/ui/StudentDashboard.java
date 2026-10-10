package com.lostandfound.ui;

import com.lostandfound.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
 * Client Dashboard allowing students to search for belongings, report lost/found items with images, and track claims.
 */
public class StudentDashboard extends JFrame {
    private String studentUsn;
    private String studentName;
    private String studentDept;

    // View Table Models
    private DefaultTableModel searchTableModel;
    private DefaultTableModel claimsTableModel;

    // Image Upload helper attributes
    private File selectedImageFile = null;
    private JLabel lblImagePreview;

    public StudentDashboard(String usn, String name, String dept) {
        this.studentUsn = usn;
        this.studentName = name;
        this.studentDept = dept;

        setTitle("College Lost & Found Portal - Student Dashboard");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top student profile block
        JPanel profileHeader = createProfileHeader();
        add(profileHeader, BorderLayout.NORTH);

        // Main Tabbed Controller
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tabbedPane.addTab("🔍 Search Directory", createSearchPanel());
        tabbedPane.addTab("➕ Report Lost Item", createReportLostPanel());
        tabbedPane.addTab("📦 Report Found Item", createReportFoundPanel());
        tabbedPane.addTab("📋 Track My Claims", createClaimsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Fetch initial table listings
        loadSearchData("");
        loadClaimsData();
    }

    private JPanel createProfileHeader() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        panel.setBackground(new Color(30, 58, 138)); // Deep Navy Blue

        JLabel lblTitle = new JLabel("STUDENT PORTAL DASHBOARD", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblInfo = new JLabel("Welcome, " + studentName + " (" + studentUsn + ") | Dept: " + studentDept, JLabel.RIGHT);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(191, 219, 254)); // Soft light blue

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(lblInfo, BorderLayout.EAST);
        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Filter / Search Row
        JPanel searchRow = new JPanel(new BorderLayout(8, 8));
        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(450, 32));
        JButton btnSearch = new JButton("Search Keyword");
        btnSearch.setBackground(new Color(37, 99, 235));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> loadSearchData(txtSearch.getText().trim()));

        searchRow.add(new JLabel("Search Item name: "), BorderLayout.WEST);
        searchRow.add(txtSearch, BorderLayout.CENTER);
        searchRow.add(btnSearch, BorderLayout.EAST);

        panel.add(searchRow, BorderLayout.NORTH);

        // Search Results JTable
        String[] columns = {"ID", "Item Name", "Date Logged", "Location", "Reporter USN", "Contact Info", "Status", "Deposited To"};
        searchTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(searchTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Claim Ownership trigger button
        JButton btnClaim = new JButton("⭐ File Ownership Verification Claim Match");
        btnClaim.setBackground(new Color(16, 185, 129));
        btnClaim.setForeground(Color.WHITE);
        btnClaim.setPreferredSize(new Dimension(250, 38));
        btnClaim.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClaim.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item from the directory to claim!", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int itemId = (int) searchTableModel.getValueAt(selectedRow, 0);
            String itemTitle = (String) searchTableModel.getValueAt(selectedRow, 1);
            String reporter = (String) searchTableModel.getValueAt(selectedRow, 4);

            if (studentUsn.equalsIgnoreCase(reporter)) {
                JOptionPane.showMessageDialog(this, "You reported this item! You don't need to file an external claim match.", "DBMS Validation Check", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Launch the proper Claim Verification Dialog Popup!
            new ClaimFormDialog(StudentDashboard.this, itemId, itemTitle).setVisible(true);
        });

        panel.add(btnClaim, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createReportLostPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblIcon = new JLabel("📝", JLabel.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));

        JLabel lblTitle = new JLabel("Report Lost Item Notice", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(30, 58, 138));

        JLabel lblDesc = new JLabel("<html><center>Click below to open the fully interactive, responsive, and editable lost item registry form.<br>This allows you to input custom student profiles, upload proof images, and submit directly to the MySQL database.</center></html>", JLabel.CENTER);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(71, 85, 105));
        lblDesc.setPreferredSize(new Dimension(450, 60));

        JButton btnOpenForm = new JButton("🚀 Launch Report Form");
        btnOpenForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOpenForm.setBackground(new Color(37, 99, 235));
        btnOpenForm.setForeground(Color.WHITE);
        btnOpenForm.setPreferredSize(new Dimension(220, 42));
        btnOpenForm.setFocusPainted(false);
        btnOpenForm.addActionListener(e -> {
            new ReportLostFrame(this).setVisible(true);
            this.setVisible(false);
        });

        panel.add(lblIcon, gbc);
        panel.add(lblTitle, gbc);
        panel.add(lblDesc, gbc);
        panel.add(Box.createVerticalStrut(10), gbc);
        panel.add(btnOpenForm, gbc);

        return panel;
    }

    private JPanel createReportFoundPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JTextField txtTitle = new JTextField(20);
        JTextField txtLoc = new JTextField(20);
        
        // Custody Locations Selector dropdown
        String[] locations = {"Security Room", "College Office", "Lab Incharge"};
        JComboBox<String> comboSafe = new JComboBox<>(locations);
        comboSafe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboSafe.setBackground(Color.WHITE);

        addLabeledField(panel, "What item did you find?", txtTitle, gbc);
        addLabeledField(panel, "Where on campus did you spot it? (e.g. Canteen desk)", txtLoc, gbc);
        
        JLabel lblSafe = new JLabel("Current Safe custody location *");
        lblSafe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSafe.setForeground(new Color(71, 85, 105));
        panel.add(lblSafe, gbc);
        panel.add(comboSafe, gbc);

        JButton btnSubmit = new JButton("Register Found Item");
        btnSubmit.setBackground(new Color(16, 185, 129));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setPreferredSize(new Dimension(150, 36));
        btnSubmit.addActionListener(e -> {
            String title = txtTitle.getText().trim();
            String loc = txtLoc.getText().trim();
            String safe = (String) comboSafe.getSelectedItem();

            if (title.isEmpty() || loc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "DBMS constraint failed: Details are mandatory!", "Form Alert", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String insertSQL = "INSERT INTO lost_items (student_name, usn, contact, department, title, description, image_blob, image_path, date_lost, location_lost, status, deposited_to) "
                             + "VALUES (?, 'FINDER-STD', 'N/A', 'Student Body', ?, 'Found Item Reported', NULL, NULL, ?, ?, 'Pending', ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

                pstmt.setString(1, studentName);
                pstmt.setString(2, "[FOUND] " + title);
                pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                pstmt.setString(4, loc);
                pstmt.setString(5, safe);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Success: Found item registered under safe custody tracking!", "DB Saved", JOptionPane.INFORMATION_MESSAGE);
                    txtTitle.setText("");
                    txtLoc.setText("");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        panel.add(Box.createVerticalStrut(10), gbc);
        panel.add(btnSubmit, gbc);
        return panel;
    }

    private JPanel createClaimsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Verifiable Claims Log", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Claim ID", "Item ID", "Item Title", "Filing Date", "Verification Status"};
        claimsTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(claimsTableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh Verification Status");
        btnRefresh.addActionListener(e -> loadClaimsData());
        panel.add(btnRefresh, BorderLayout.SOUTH);

        return panel;
    }

    private void addLabeledField(JPanel container, String text, JTextField field, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        container.add(label, gbc);
        container.add(field, gbc);
    }

    private void loadSearchData(String keyword) {
        searchTableModel.setRowCount(0);
        String query = "SELECT * FROM lost_items WHERE status != 'Returned' AND title LIKE ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    searchTableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getDate("date_lost").toString(),
                            rs.getString("location_lost"),
                            rs.getString("usn"),
                            rs.getString("contact"),
                            rs.getString("status"),
                            rs.getString("deposited_to")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadClaimsData() {
        claimsTableModel.setRowCount(0);
        // Connects claims and lost_items together via JOIN relation to get descriptive parameters
        String query = "SELECT c.claim_id, c.item_id, l.title, c.claim_date, c.claim_status "
                     + "FROM claims c JOIN lost_items l ON c.item_id = l.id "
                     + "WHERE c.usn = ? ORDER BY c.claim_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentUsn);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    claimsTableModel.addRow(new Object[]{
                            rs.getInt("claim_id"),
                            rs.getInt("item_id"),
                            rs.getString("title"),
                            rs.getDate("claim_date").toString(),
                            rs.getString("claim_status")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void submitClaim(int itemId) {
        String insertSQL = "INSERT INTO claims (item_id, student_name, usn, department, contact, claim_date, verification_status) VALUES (?, ?, ?, ?, ?, ?, 'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setInt(1, itemId);
            pstmt.setString(2, studentName);
            pstmt.setString(3, studentUsn);
            pstmt.setString(4, studentDept);
            pstmt.setString(5, "Student Mobile"); // Fetch profile contact or input custom
            pstmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Match Ticket Created! Status set to 'Pending'. Please visit the CSE Department HOD with valid ID card proof.", "Claim Filed Successful", JOptionPane.INFORMATION_MESSAGE);
                loadClaimsData();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DBMS Error: Claim already exists or constraint violation! " + ex.getMessage(), "Claim Failed", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public String getStudentUsn() { return this.studentUsn; }
    public String getStudentName() { return this.studentName; }
    public String getStudentDept() { return this.studentDept; }
}

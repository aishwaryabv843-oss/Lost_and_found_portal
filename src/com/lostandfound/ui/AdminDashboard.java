package com.lostandfound.ui;

import com.lostandfound.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Admin Dashboard allowing supervisors to verify claims, perform transactional updates, and execute CRUD overrides.
 */
public class AdminDashboard extends JFrame {
    private DefaultTableModel claimsTableModel;
    private JLabel lblTotalLost;
    private JLabel lblPendingClaims;
    private JLabel lblReturnedCount;

    public AdminDashboard() {
        setTitle("College Lost & Found Portal - Admin Oversight Dashboard");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header
        JPanel header = createAdminHeader();
        add(header, BorderLayout.NORTH);

        // Center Area (Split Panel: Left is claims table, Right is operational buttons & stats)
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Stats strip
        JPanel statsStrip = createStatsStrip();
        mainContent.add(statsStrip, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.add(new JLabel("⏳ CLAIM VERIFICATION DIRECTORY", JLabel.LEFT), BorderLayout.NORTH);

        String[] columns = {"Claim ID", "Item ID", "Item Title", "Student Name", "USN", "Contact Mobile", "Claim Date", "Status"};
        claimsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(claimsTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(tablePanel, BorderLayout.CENTER);

        // Right Operational Controls Panel
        JPanel controlsPanel = createControlsPanel(table);
        mainContent.add(controlsPanel, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);

        // Initial Data Fetch
        refreshAdminData();
    }

    private JPanel createAdminHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        panel.setBackground(new Color(15, 23, 42)); // Modern Deep Charcoal

        JLabel lblTitle = new JLabel("🛡️ SUPERVISOR OVERSIGHT SYSTEM CONTROL", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSession = new JLabel("Role: System Admin (Active SQL Override Security)", JLabel.RIGHT);
        lblSession.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSession.setForeground(new Color(148, 163, 184));

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(lblSession, BorderLayout.EAST);
        return panel;
    }

    private JPanel createStatsStrip() {
        JPanel strip = new JPanel(new GridLayout(1, 3, 15, 10));

        lblTotalLost = createStatCard(strip, "Total Lost Logs", "0", new Color(37, 99, 235));
        lblPendingClaims = createStatCard(strip, "Active Claims", "0", new Color(245, 158, 11));
        lblReturnedCount = createStatCard(strip, "Successfully Returned", "0", new Color(16, 185, 129));

        return strip;
    }

    private JLabel createStatCard(JPanel container, String title, String val, Color barColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, barColor),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(Color.DARK_GRAY);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        container.add(card);
        return lblVal;
    }

    private JPanel createControlsPanel(JTable table) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(200, 300));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JButton btnApprove = new JButton("✓ Approve Claim");
        btnApprove.setBackground(new Color(37, 99, 235)); // Theme blue
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnApprove.setPreferredSize(new Dimension(180, 34));
        btnApprove.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a claim request to approve!", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int claimId = (int) claimsTableModel.getValueAt(selectedRow, 0);
            int itemId = (int) claimsTableModel.getValueAt(selectedRow, 1);
            String name = (String) claimsTableModel.getValueAt(selectedRow, 3);
            String usn = (String) claimsTableModel.getValueAt(selectedRow, 4);

            String notes = JOptionPane.showInputDialog(this, "Enter verification/identification notes for approval:", "Identity match verified via college ID card & photos.");
            if (notes == null) return; // User cancelled

            approveClaimWorkflow(claimId, itemId, notes);
        });

        JButton btnReject = new JButton("✗ Reject Claim");
        btnReject.setBackground(new Color(239, 68, 68));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReject.setPreferredSize(new Dimension(180, 34));
        btnReject.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a claim row!", "Alert", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int claimId = (int) claimsTableModel.getValueAt(selectedRow, 0);
            int itemId = (int) claimsTableModel.getValueAt(selectedRow, 1);
            String name = (String) claimsTableModel.getValueAt(selectedRow, 3);
            
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to REJECT this claim for student: " + name + "?\nThis sets Claim to 'Rejected' and reverts Item to 'Pending'.", "Reject Claim", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                rejectClaimWorkflow(claimId, itemId);
            }
        });

        JButton btnReturn = new JButton("🎁 Mark Item as Returned");
        btnReturn.setBackground(new Color(16, 185, 129)); // Emerald/green
        btnReturn.setForeground(Color.WHITE);
        btnReturn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReturn.setPreferredSize(new Dimension(180, 38));
        btnReturn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a claim request to mark as returned!", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int claimId = (int) claimsTableModel.getValueAt(selectedRow, 0);
            int itemId = (int) claimsTableModel.getValueAt(selectedRow, 1);
            String name = (String) claimsTableModel.getValueAt(selectedRow, 3);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to mark this claim for " + name + " as Returned?\nThis transitions Claim to 'Completed' and Lost Item to 'Returned'.", "Mark as Returned", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                returnClaimWorkflow(claimId, itemId);
            }
        });

        JButton btnDelete = new JButton("🗑️ Delete Invalid Claim (CRUD)");
        btnDelete.setBackground(new Color(71, 85, 105));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a claim row to delete permanently!", "Alert", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int claimId = (int) claimsTableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "WARNING: This runs a direct SQL DELETE statement and permanently removes the claim ID #" + claimId + " from logs. Proceed?", "DB SQL Delete Override", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteClaimFromDb(claimId);
            }
        });

        JButton btnRefresh = new JButton("🔄 Refresh Data Logs");
        btnRefresh.addActionListener(e -> refreshAdminData());

        panel.add(btnApprove, gbc);
        panel.add(btnReject, gbc);
        panel.add(btnReturn, gbc);
        panel.add(Box.createVerticalStrut(10), gbc);
        panel.add(btnDelete, gbc);
        panel.add(Box.createVerticalStrut(15), gbc);
        panel.add(btnRefresh, gbc);

        return panel;
    }

    private void refreshAdminData() {
        loadClaimsData();
        loadAggregatedStats();
    }

    private void loadClaimsData() {
        claimsTableModel.setRowCount(0);
        String query = "SELECT c.claim_id, c.item_id, l.title, c.student_name, c.usn, c.contact, c.claim_date, c.claim_status "
                     + "FROM claims c JOIN lost_items l ON c.item_id = l.id "
                     + "ORDER BY c.claim_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                claimsTableModel.addRow(new Object[]{
                        rs.getInt("claim_id"),
                        rs.getInt("item_id"),
                        rs.getString("title"),
                        rs.getString("student_name"),
                        rs.getString("usn"),
                        rs.getString("contact"),
                        rs.getDate("claim_date").toString(),
                        rs.getString("claim_status")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadAggregatedStats() {
        try (Connection conn = DBConnection.getConnection()) {
            // Stat 1: Total Lost
            String q1 = "SELECT COUNT(*) FROM lost_items";
            try (PreparedStatement pst = conn.prepareStatement(q1); ResultSet rs = pst.executeQuery()) {
                if (rs.next()) lblTotalLost.setText(String.valueOf(rs.getInt(1)));
            }

            // Stat 2: Active Claims
            String q2 = "SELECT COUNT(*) FROM claims WHERE claim_status = 'Pending'";
            try (PreparedStatement pst = conn.prepareStatement(q2); ResultSet rs = pst.executeQuery()) {
                if (rs.next()) lblPendingClaims.setText(String.valueOf(rs.getInt(1)));
            }

            // Stat 3: Returned Counts
            String q3 = "SELECT COUNT(*) FROM lost_items WHERE status = 'Returned'";
            try (PreparedStatement pst = conn.prepareStatement(q3); ResultSet rs = pst.executeQuery()) {
                if (rs.next()) lblReturnedCount.setText(String.valueOf(rs.getInt(1)));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Executes atomic MySQL transaction steps: Sets auto-commit off, updates the claim to Ready for Collection,
     * updates the lost item status to Ready for Collection, sets verification notes, and commits.
     */
    private void approveClaimWorkflow(int claimId, int itemId, String notes) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin ACID transaction boundary

            // Statement A: Update claim verification status to Ready for Collection
            String sqlClaim = "UPDATE claims SET claim_status = 'Ready for Collection' WHERE claim_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlClaim)) {
                pstmt.setInt(1, claimId);
                pstmt.executeUpdate();
            }

            // Statement B: Update lost item status to Ready for Collection and set verification_notes
            String sqlItem = "UPDATE lost_items SET status = 'Ready for Collection', verification_notes = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlItem)) {
                pstmt.setString(1, notes);
                pstmt.setInt(2, itemId);
                pstmt.executeUpdate();
            }

            conn.commit(); // Transaction ends - Safe database state write
            JOptionPane.showMessageDialog(this, "🎉 Claim successfully approved! Item is marked as 'Ready for Collection'.", "Claim Verified", JOptionPane.INFORMATION_MESSAGE);
            refreshAdminData();
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in case of runtime JDBC failures
                    JOptionPane.showMessageDialog(this, "SQL Transaction Failed. Rollback executed!", "DBMS Rollback Alert", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException rollEx) {
                    rollEx.printStackTrace();
                }
            }
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    private void rejectClaimWorkflow(int claimId, int itemId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlClaim = "UPDATE claims SET claim_status = 'Rejected' WHERE claim_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlClaim)) {
                pstmt.setInt(1, claimId);
                pstmt.executeUpdate();
            }

            String sqlItem = "UPDATE lost_items SET status = 'Pending' WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlItem)) {
                pstmt.setInt(1, itemId);
                pstmt.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Claim marked as Rejected and Item status reverted back to Pending!", "Claim Rejected", JOptionPane.INFORMATION_MESSAGE);
            refreshAdminData();
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollEx) { rollEx.printStackTrace(); }
            }
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    private void returnClaimWorkflow(int claimId, int itemId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlClaim = "UPDATE claims SET claim_status = 'Completed' WHERE claim_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlClaim)) {
                pstmt.setInt(1, claimId);
                pstmt.executeUpdate();
            }

            String sqlItem = "UPDATE lost_items SET status = 'Returned', returned_by_admin = 'System Admin', return_date = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlItem)) {
                pstmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
                pstmt.setInt(2, itemId);
                pstmt.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "🎉 SUCCESS: Item successfully returned to student and claim marked as Completed!", "Return Complete", JOptionPane.INFORMATION_MESSAGE);
            refreshAdminData();
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollEx) { rollEx.printStackTrace(); }
            }
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    private void deleteClaimFromDb(int claimId) {
        String deleteSQL = "DELETE FROM claims WHERE claim_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.setInt(1, claimId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "CRUD Delete Success: Claim ID #" + claimId + " permanently deleted from MySQL tables.", "Record Deleted", JOptionPane.INFORMATION_MESSAGE);
                refreshAdminData();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Exception: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

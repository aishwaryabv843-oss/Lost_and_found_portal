package com.lostandfound.ui;

import com.lostandfound.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Authentication JFrame supporting student and admin logins, plus account creation. Swaps views via CardLayout.
 */
public class LoginFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Login Form fields
    private JTextField txtLoginUser;
    private JPasswordField txtLoginPass;

    // Register Form fields
    private JTextField txtRegName;
    private JTextField txtRegUsn;
    private JTextField txtRegContact;
    private JTextField txtRegDept;
    private JPasswordField txtRegPass;

    public LoginFrame() {
        setTitle("College Lost & Found Portal - Authentication Gate");
        setSize(450, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Build panels
        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();

        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(new Color(248, 250, 252)); // Sleek Slate Gray

        // Header Label
        JLabel lblHeader = new JLabel("College Lost & Found Portal", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(new Color(30, 58, 138)); // Deep Navy
        panel.add(lblHeader, BorderLayout.NORTH);

        // Form Center Area
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(248, 250, 252));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel lblUser = new JLabel("Username (USN / Student ID)");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtLoginUser = new JTextField(20);
        txtLoginUser.setPreferredSize(new Dimension(300, 32));

        JLabel lblPass = new JLabel("Security Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtLoginPass = new JPasswordField(20);
        txtLoginPass.setPreferredSize(new Dimension(300, 32));

        centerPanel.add(lblUser, gbc);
        centerPanel.add(txtLoginUser, gbc);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(lblPass, gbc);
        centerPanel.add(txtLoginPass, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(248, 250, 252));

        JButton btnLogin = new JButton("Login →");
        btnLogin.setPreferredSize(new Dimension(100, 36));
        btnLogin.setBackground(new Color(37, 99, 235)); // Royal Blue
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.addActionListener(this::handleLogin);

        JButton btnGoRegister = new JButton("Create New Student Account");
        btnGoRegister.setBorderPainted(false);
        btnGoRegister.setContentAreaFilled(false);
        btnGoRegister.setForeground(new Color(37, 99, 235));
        btnGoRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoRegister.addActionListener(e -> cardLayout.show(mainPanel, "register"));

        bottomPanel.add(btnLogin, BorderLayout.NORTH);
        bottomPanel.add(btnGoRegister, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        panel.setBackground(new Color(248, 250, 252));

        JLabel lblHeader = new JLabel("Register Student Profile", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(new Color(30, 58, 138));
        panel.add(lblHeader, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(248, 250, 252));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        txtRegName = new JTextField();
        txtRegUsn = new JTextField();
        txtRegContact = new JTextField();
        txtRegDept = new JTextField();
        txtRegPass = new JPasswordField();

        Dimension dim = new Dimension(300, 30);
        txtRegName.setPreferredSize(dim);
        txtRegUsn.setPreferredSize(dim);
        txtRegContact.setPreferredSize(dim);
        txtRegDept.setPreferredSize(dim);
        txtRegPass.setPreferredSize(dim);

        addLabeledField(centerPanel, "Full Name", txtRegName, gbc);
        addLabeledField(centerPanel, "USN (Unique Student Seat Key)", txtRegUsn, gbc);
        addLabeledField(centerPanel, "Contact Number", txtRegContact, gbc);
        addLabeledField(centerPanel, "Department", txtRegDept, gbc);
        addLabeledField(centerPanel, "Choose Password", txtRegPass, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(248, 250, 252));

        JButton btnRegister = new JButton("Register Profile");
        btnRegister.setPreferredSize(new Dimension(100, 36));
        btnRegister.setBackground(new Color(16, 185, 129)); // Emerald Green
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setFocusPainted(false);
        btnRegister.addActionListener(this::handleRegistration);

        JButton btnGoLogin = new JButton("← Already registered? Back to Login");
        btnGoLogin.setBorderPainted(false);
        btnGoLogin.setContentAreaFilled(false);
        btnGoLogin.setForeground(new Color(37, 99, 235));
        btnGoLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoLogin.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        bottomPanel.add(btnRegister, BorderLayout.NORTH);
        bottomPanel.add(btnGoLogin, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void addLabeledField(JPanel container, String text, JTextField field, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(71, 85, 105));
        container.add(label, gbc);
        container.add(field, gbc);
    }

    private void handleLogin(ActionEvent e) {
        String username = txtLoginUser.getText().trim();
        String password = new String(txtLoginPass.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "DBMS Validation Error: Fields cannot be blank!", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Execute SQL Select to verify user credentials
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    String fullName = rs.getString("name");
                    String dept = rs.getString("department");

                    JOptionPane.showMessageDialog(this, "Welcome " + fullName + "! Login successful.", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Route user to appropriate Dashboard based on Role
                    if ("admin".equalsIgnoreCase(role)) {
                        new AdminDashboard().setVisible(true);
                    } else {
                        new StudentDashboard(username, fullName, dept).setVisible(true);
                    }
                    this.dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(this, "DBMS Alert: Incorrect Username or Password!", "Auth Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Connection Failure: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleRegistration(ActionEvent e) {
        String name = txtRegName.getText().trim();
        String usn = txtRegUsn.getText().trim();
        String contact = txtRegContact.getText().trim();
        String dept = txtRegDept.getText().trim();
        String password = new String(txtRegPass.getPassword()).trim();

        if (name.isEmpty() || usn.isEmpty() || contact.isEmpty() || dept.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Validation Error: Please fill in all fields!", "Registration Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Insert new student profile into DB
        String insertQuery = "INSERT INTO users (username, name, password, department, role) VALUES (?, ?, ?, ?, 'student')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, usn);
            pstmt.setString(2, name);
            pstmt.setString(3, password);
            pstmt.setString(4, dept);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Profile registered successfully inside MySQL! You can log in now.", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                txtLoginUser.setText(usn);
                txtLoginPass.setText("");
                cardLayout.show(mainPanel, "login");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) { // Duplicate key error code in MySQL
                JOptionPane.showMessageDialog(this, "DBMS Violation: A student profile with this USN already exists!", "Integrity Constraint Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            ex.printStackTrace();
        }
    }
}

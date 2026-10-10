package com.lostandfound.model;

/**
 * Model class representing a User entity
 */
public class User {
    private int id;
    private String username;
    private String name;
    private String password;
    private String department;
    private String role; // 'student' or 'admin'

    public User(int id, String username, String name, String password, String department, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.department = department;
        this.role = role;
    }

    public User(String username, String name, String password, String department, String role) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.department = department;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

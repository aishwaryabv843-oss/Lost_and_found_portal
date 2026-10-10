package com.lostandfound.model;

import java.util.Date;

/**
 * Model class representing a Lost Item record
 */
public class LostItem {
    private int id;
    private String studentName;
    private String usn;
    private String contact;
    private String department;
    private String title;
    private byte[] imageBlob; // Holds the image bytes for database upload
    private Date dateLost;
    private String locationLost;
    private String status; // 'Pending' or 'Returned'

    public LostItem(int id, String studentName, String usn, String contact, String department, 
                    String title, byte[] imageBlob, Date dateLost, String locationLost, String status) {
        this.id = id;
        this.studentName = studentName;
        this.usn = usn;
        this.contact = contact;
        this.department = department;
        this.title = title;
        this.imageBlob = imageBlob;
        this.dateLost = dateLost;
        this.locationLost = locationLost;
        this.status = status;
    }

    public LostItem(String studentName, String usn, String contact, String department, 
                    String title, byte[] imageBlob, Date dateLost, String locationLost, String status) {
        this.studentName = studentName;
        this.usn = usn;
        this.contact = contact;
        this.department = department;
        this.title = title;
        this.imageBlob = imageBlob;
        this.dateLost = dateLost;
        this.locationLost = locationLost;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getUsn() { return usn; }
    public void setUsn(String usn) { this.usn = usn; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public byte[] getImageBlob() { return imageBlob; }
    public void setImageBlob(byte[] imageBlob) { this.imageBlob = imageBlob; }

    public Date getDateLost() { return dateLost; }
    public void setDateLost(Date dateLost) { this.dateLost = dateLost; }

    public String getLocationLost() { return locationLost; }
    public void setLocationLost(String locationLost) { this.locationLost = locationLost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

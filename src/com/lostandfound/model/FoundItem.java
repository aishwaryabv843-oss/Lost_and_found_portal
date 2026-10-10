package com.lostandfound.model;

import java.util.Date;

/**
 * Model class representing a Found Item record
 */
public class FoundItem {
    private int id;
    private String title;
    private Date dateFound;
    private String locationFound;
    private String safeCustody; // safe point of recovery (e.g. Sports Room)
    private String reporterName;
    private String contact;
    private String status; // 'Found' or 'Returned'

    public FoundItem(int id, String title, Date dateFound, String locationFound, 
                     String safeCustody, String reporterName, String contact, String status) {
        this.id = id;
        this.title = title;
        this.dateFound = dateFound;
        this.locationFound = locationFound;
        this.safeCustody = safeCustody;
        this.reporterName = reporterName;
        this.contact = contact;
        this.status = status;
    }

    public FoundItem(String title, Date dateFound, String locationFound, 
                     String safeCustody, String reporterName, String contact, String status) {
        this.title = title;
        this.dateFound = dateFound;
        this.locationFound = locationFound;
        this.safeCustody = safeCustody;
        this.reporterName = reporterName;
        this.contact = contact;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getDateFound() { return dateFound; }
    public void setDateFound(Date dateFound) { this.dateFound = dateFound; }

    public String getLocationFound() { return locationFound; }
    public void setLocationFound(String locationFound) { this.locationFound = locationFound; }

    public String getSafeCustody() { return safeCustody; }
    public void setSafeCustody(String safeCustody) { this.safeCustody = safeCustody; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

package com.example.androidapp.models;

public class DocumentRecent extends Document{
    String dateRecent, userView;

    public DocumentRecent(String id, String name, String subject, String type, String url, String upLoadTimeStamp, String owner, String description, String logo, String dateRecent, String userView) {
        super(id, name, subject, type, url, upLoadTimeStamp, owner, description, logo);
        this.dateRecent = dateRecent;
        this.userView = userView;
    }

    public String getDateRecent() {
        return dateRecent;
    }

    public void setDateRecent(String dateRecent) {
        this.dateRecent = dateRecent;
    }

    public String getUserView() {
        return userView;
    }

    public void setUserView(String userView) {
        this.userView = userView;
    }
}

package com.example.androidapp.models;

public class Document {
    private String name, subject, type, url, upLoadTimeStamp, owner, description, logo;

    public Document(String name, String subject, String type, String url, String upLoadTimeStamp, String owner, String description, String logo) {
        this.name = name;
        this.subject = subject;
        this.type = type;
        this.url = url;
        this.upLoadTimeStamp = upLoadTimeStamp;
        this.owner = owner;
        this.description = description;
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpLoadTimeStamp() {
        return upLoadTimeStamp;
    }

    public void setUpLoadTimeStamp(String upLoadTimeStamp) {
        this.upLoadTimeStamp = upLoadTimeStamp;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}

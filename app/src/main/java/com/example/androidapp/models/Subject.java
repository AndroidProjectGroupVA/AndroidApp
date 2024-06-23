package com.example.androidapp.models;

public class Subject {
    private String id, name, upLoadTimeStamp,  logo;

    public Subject(String id, String name,String upLoadTimeStamp,String logo) {
        this.id = id;
        this.name = name;

        this.upLoadTimeStamp = upLoadTimeStamp;

        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getUpLoadTimeStamp() {
        return upLoadTimeStamp;
    }

    public void setUpLoadTimeStamp(String upLoadTimeStamp) {
        this.upLoadTimeStamp = upLoadTimeStamp;
    }



    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}

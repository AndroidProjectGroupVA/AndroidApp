package com.example.androidapp.models;

public class Subject {
    private String id, name, description,  logo;

    public Subject(String id, String name,String description,String logo) {
        this.id = id;
        this.name = name;

        this.description = description;

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

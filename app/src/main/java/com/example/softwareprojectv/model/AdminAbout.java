package com.example.softwareprojectv.model;

public class AdminAbout {
    private String name;
    private String email;
    private String designation;
    private String wing;
    private String key;
    private String imageUrl;

    public AdminAbout() {
        this.name = "";
        this.email = "";
        this.designation = "";
        this.key = "";
        this.imageUrl = "";
    }
    public AdminAbout(String name, String email, String designation, String imageUrl) {
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

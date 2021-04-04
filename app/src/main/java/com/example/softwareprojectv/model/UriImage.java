package com.example.softwareprojectv.model;

public class UriImage {
    private String imageName;
    private String url;

    public UriImage() {
    }
    public UriImage(String imageName, String url) {
        this.imageName = imageName;
        this.url = url;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

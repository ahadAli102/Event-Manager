package com.example.softwareprojectv.model;

import android.net.Uri;

public class Image {
    private String imageName;
    private Uri uri;

    public Image(String imageName, Uri uri) {
        this.imageName = imageName;
        this.uri = uri;
    }
    public Image() {
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}

package com.example.softwareprojectv.model;

import java.io.Serializable;

public class Program implements Serializable {
    private String key;
    private String title;
    private String programDescription;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String imageUrl;

    public Program() {
        this.key = "";
        this.title = "";
        this.programDescription = "";
        this.startDate = "";
        this.endDate = "";
        this.startTime = "";
        this.endTime = "";
        this.imageUrl = "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Program(String key, String title, String programDescription, String startDate, String endDate, String startTime, String endTime, String imageUrl) {
        this.key = key;
        this.title = title;
        this.programDescription = programDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imageUrl = imageUrl;
    }

    public String getProgramName() {
        return programDescription;
    }

    public void setProgramName(String programDescrition) {
        this.programDescription = programDescrition;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

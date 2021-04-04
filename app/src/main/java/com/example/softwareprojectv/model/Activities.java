package com.example.softwareprojectv.model;

import java.io.Serializable;
import java.util.List;

public class Activities implements Serializable {

    private String activityId;
    private String activityName;
    private String activityDescription;
    private String totalImage;
    private List<String> imageNameList;
    private List<String> imageLinkList;

    public Activities() {
        this.activityId = "";
        this.activityName = "";
        this.activityDescription = "";
        this.totalImage = "";
    }
    public Activities(String activityId, String activityName, String activityDescription, String totalImage) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.activityDescription = activityDescription;
        this.totalImage = totalImage;
    }

    public List<String> getImageNameList() {
        return imageNameList;
    }

    public void setImageNameList(List<String> imageNameList) {
        this.imageNameList = imageNameList;
    }

    public List<String> getImageLinkList() {
        return imageLinkList;
    }

    public void setImageLinkList(List<String> imageLinkList) {
        this.imageLinkList = imageLinkList;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public String getTotalImage() {
        return totalImage;
    }

    public void setTotalImage(String totalImage) {
        this.totalImage = totalImage;
    }
}

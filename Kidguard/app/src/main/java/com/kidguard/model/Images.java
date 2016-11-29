package com.kidguard.model;


public class Images {
    String imagePath;
    String sizeMB;
    String sizeKB;
    String dateTime;

    public String getSizeKB() {
        return sizeKB;
    }

    public void setSizeKB(String sizeKB) {
        this.sizeKB = sizeKB;
    }

    public String getSizeMB() {
        return sizeMB;
    }

    public void setSizeMB(String sizeMB) {
        this.sizeMB = sizeMB;
    }


    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}

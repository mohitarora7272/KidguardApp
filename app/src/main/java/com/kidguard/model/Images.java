package com.kidguard.model;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
@SuppressWarnings("all")
public class Images implements Serializable {
    private static final long serialVersionUID = -222864131214757024L;

    public static final String ID = "id";
    public static final String IMAGE_PATH = "imagePath";
    public static final String IMAGE_NAME = "imageName";
    public static final String IMAGE_SIZE_MB = "sizeMB";
    public static final String IMAGE_SIZE_KB = "sizeKB";
    public static final String IMAGE_DATE_TIME = "dateTime";
    public static final String IMAGE_DATE_TIMESTAMP = "dateTimeStamp";
    public static final String IMAGE_STATUS = "imageStatus";

    @DatabaseField(generatedId = true, columnName = ID)
    public int id;
    @DatabaseField(columnName = IMAGE_PATH)
    String imagePath;
    @DatabaseField(columnName = IMAGE_NAME)
    String imageName;
    @DatabaseField(columnName = IMAGE_SIZE_MB)
    String sizeMB;
    @DatabaseField(columnName = IMAGE_SIZE_KB)
    String sizeKB;
    @DatabaseField(columnName = IMAGE_DATE_TIME)
    String dateTime;
    @DatabaseField(columnName = IMAGE_DATE_TIMESTAMP)
    String dateTimeStamp;
    @DatabaseField(columnName = IMAGE_STATUS)
    String imageStatus;


    /* Default Constructor */
    public Images() {
    }

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

    public String getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(String imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getDateTimeStamp() {
        return dateTimeStamp;
    }

    public void setDateTimeStamp(String dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}

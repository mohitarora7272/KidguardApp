package com.kidguard.model;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Video implements Serializable {
    private static final long serialVersionUID = -222864131214757024L;

    public static final String ID = "id";
    public static final String VIDEO_NAME = "videoname";
    public static final String VIDEO_PATH = "videoPath";
    public static final String VIDEO_EXT = "videoExt";
    public static final String VIDEO_STATUS = "videoStatus";
    public static final String VIDEO_SIZE_KB = "videoSizeKB";
    public static final String VIDEO_SIZE_MB = "videoSizeMB";
    public static final String DATE_TIME = "date_time";
    public static final String DATE_TIME_STAMP = "date_time_stamp";


    @DatabaseField(columnName = VIDEO_NAME)
    private String videoname;
    @DatabaseField(columnName = VIDEO_PATH)
    private String videoPath;
    @DatabaseField(columnName = VIDEO_EXT)
    private String videoExt;
    @DatabaseField(columnName = VIDEO_STATUS)
    private String videoStatus;
    @DatabaseField(columnName = VIDEO_SIZE_KB)
    private String videoSizeKB;
    @DatabaseField(columnName = VIDEO_SIZE_MB)
    private String videoSizeMB;
    @DatabaseField(columnName = DATE_TIME)
    private String dateTime;
    @DatabaseField(columnName = DATE_TIME_STAMP)
    private String dateTimeStamp;

    // Default Constructor
    public Video() {
    }

    @DatabaseField(generatedId = true, columnName = ID)
    public int id;

    public String getVideoname() {
        return videoname;
    }

    public void setVideoname(String videoname) {
        this.videoname = videoname;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoExt() {
        return videoExt;
    }

    public void setVideoExt(String videoExt) {
        this.videoExt = videoExt;
    }

    public String getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(String videoStatus) {
        this.videoStatus = videoStatus;
    }

    public String getVideoSizeMB() {
        return videoSizeMB;
    }

    public void setVideoSizeMB(String videoSizeMB) {
        this.videoSizeMB = videoSizeMB;
    }

    public String getVideoSizeKB() {
        return videoSizeKB;
    }

    public void setVideoSizeKB(String videoSizeKB) {
        this.videoSizeKB = videoSizeKB;
    }

    public String getDate_time_stamp() {
        return dateTimeStamp;
    }

    public void setDate_time_stamp(String dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public String getDate_time() {
        return dateTime;
    }

    public void setDate_time(String dateTime) {
        this.dateTime = dateTime;
    }
}

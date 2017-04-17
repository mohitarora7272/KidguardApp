package com.kidguard.model;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Files implements Serializable {
    private static final long serialVersionUID = -222864131214757024L;

    public static final String ID = "id";
    public static final String FILE_NAME = "filename";
    public static final String FILE_PATH = "filePath";
    public static final String FILE_EXT = "fileExt";
    public static final String FILE_SIZE_KB = "fileSizeKB";
    public static final String FILE_SIZE_MB = "fileSizeMB";
    public static final String FILE_STATUS = "fileStatus";
    public static final String DATE_TIME = "date_time";
    public static final String DATE_TIME_STAMP = "date_time_stamp";

    @DatabaseField(generatedId = true, columnName = ID)
    public int id;
    @DatabaseField(columnName = FILE_NAME)
    String filename;
    @DatabaseField(columnName = FILE_PATH)
    String filePath;
    @DatabaseField(columnName = FILE_SIZE_KB)
    String fileSizeKB;
    @DatabaseField(columnName = FILE_SIZE_MB)
    String fileSizeMB;
    @DatabaseField(columnName = FILE_EXT)
    String fileExt;
    @DatabaseField(columnName = FILE_STATUS)
    String fileStatus;
    @DatabaseField(columnName = DATE_TIME)
    String dateTime;
    @DatabaseField(columnName = DATE_TIME_STAMP)
    String dateTimeStamp;

    // Default Constructor
    public Files() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFileSizeMB() {
        return fileSizeMB;
    }

    public void setFileSizeMB(String fileSizeMB) {
        this.fileSizeMB = fileSizeMB;
    }

    public String getFileSizeKB() {
        return fileSizeKB;
    }

    public void setFileSizeKB(String fileSizeKB) {
        this.fileSizeKB = fileSizeKB;
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

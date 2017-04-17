package com.kidguard.model;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class GoogleDrive implements Serializable {
    private static final long serialVersionUID = -222864131214757024L;

    public static final String ID = "id";
    public static final String FILE_ID = "fileId";
    public static final String FILE_TITLE = "fileTitle";
    public static final String FILE_DATE = "fileDate";
    public static final String FILE_SIZE = "fileSize";
    public static final String FILE_EXTENSION = "fileExtention";
    public static final String FILE_DOWNLOAD_URL = "fileDownloadUrl";

    @DatabaseField(generatedId = true, columnName = ID)
    public int id;
    @DatabaseField(columnName = FILE_ID)
    private String fileId;
    @DatabaseField(columnName = FILE_TITLE)
    private String fileTitle;
    @DatabaseField(columnName = FILE_DATE)
    private String fileDate;
    @DatabaseField(columnName = FILE_SIZE)
    private String fileSize;
    @DatabaseField(columnName = FILE_EXTENSION)
    private String fileExtention;
    @DatabaseField(columnName = FILE_DOWNLOAD_URL)
    private String fileDownloadUrl;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileDownloadUrl() {
        return fileDownloadUrl;
    }

    public void setFileDownloadUrl(String fileDownloadUrl) {
        this.fileDownloadUrl = fileDownloadUrl;
    }

    public String getFileExtention() {
        return fileExtention;
    }

    public void setFileExtention(String fileExtention) {
        this.fileExtention = fileExtention;
    }
}

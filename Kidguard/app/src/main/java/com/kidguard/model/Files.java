package com.kidguard.model;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Files implements Serializable {
    private static final long serialVersionUID = -222864131214757024L;

    public static final String ID = "id";
    public static final String FILE_NAME = "filename";
    public static final String FILE_PATH = "filePath";
    public static final String FILE_EXT = "fileExt";
    public static final String FILE_STATUS = "fileStatus";

    @DatabaseField(generatedId = true, columnName = ID)
    public int id;
    @DatabaseField(columnName = FILE_NAME)
    String filename;
    @DatabaseField(columnName = FILE_PATH)
    String filePath;
    @DatabaseField(columnName = FILE_EXT)
    String fileExt;
    @DatabaseField(columnName = FILE_STATUS)
    String fileStatus;

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

}

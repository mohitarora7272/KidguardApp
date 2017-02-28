package com.kidguard.model;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

@SuppressWarnings("all")
public class Sms implements Serializable {

    private static final long serialVersionUID = -222864131214757024L;

    public static final String ID = "id";
    public static final String SMS_ID = "sms_id";
    public static final String SMS_ADDRESS = "address";
    public static final String SMS_MESSAGE = "message";
    public static final String SMS_READ_STATE = "readState";
    public static final String SMS_DATE = "date";
    public static final String SMS_DATE_TIMESTAMP = "date_timestamp";
    public static final String SMS_FOLDER_NAME = "folderName";
    public static final String SMS_STATUS = "sms_status";
    public static final String SMS_FROM_ME = "from_me";


    @DatabaseField(generatedId = true, columnName = ID)
    public int id;

    @DatabaseField(columnName = SMS_ID)
    public String sms_id;

    @DatabaseField(columnName = SMS_ADDRESS)
    public String address;

    @DatabaseField(columnName = SMS_MESSAGE)
    public String message;

    @DatabaseField(columnName = SMS_READ_STATE)
    public String readState;

    @DatabaseField(columnName = SMS_DATE)
    public String date;

    @DatabaseField(columnName = SMS_FOLDER_NAME)
    public String folderName;

    @DatabaseField(columnName = SMS_STATUS)
    public String sms_status;

    @DatabaseField(columnName = SMS_DATE_TIMESTAMP)
    public String date_timestamp;

    @DatabaseField(columnName = SMS_FROM_ME)
    public String from_me;


    /* Default Constructor */
    public Sms() {
    }

    public String getSms_status() {
        return sms_status;
    }

    public void setSms_status(String sms_status) {
        this.sms_status = sms_status;
    }


    public String getSmsId() {
        return sms_id;
    }

    public String getAddress() {
        return address;
    }

    public String getMsg() {
        return message;
    }

    public String getReadState() {
        return readState;
    }

    public String getDate() {
        return date;
    }

    public String getFolderName() {
        return folderName;
    }


    public void setSmsId(String sms_id) {
        this.sms_id = sms_id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMsg(String message) {
        this.message = message;
    }

    public void setReadState(String readState) {
        this.readState = readState;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getDate_timestamp() {
        return date_timestamp;
    }

    public void setDate_timestamp(String date_timestamp) {
        this.date_timestamp = date_timestamp;
    }

    public String getFrom_me() {
        return from_me;
    }

    public void setFrom_me(String from_me) {
        this.from_me = from_me;
    }

}

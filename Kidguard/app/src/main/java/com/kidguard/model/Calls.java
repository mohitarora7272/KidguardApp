package com.kidguard.model;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class Calls implements Serializable {
    private static final long serialVersionUID = -222864131214757024L;

    public static final String ID = "id";
    public static final String CALLER_ID = "callerId";
    public static final String CALLER_NAME = "callerName";
    public static final String CALLER_PHONE_NO = "phNumber";
    public static final String CALLER_TYPE = "callType";
    public static final String CALLER_DATE = "callDate";
    public static final String CALLER_DURATION = "callDuration";
    public static final String CALLER_DIRECTORY = "dir";
    public static final String CALLER_DATE_TIME = "callDateTime";
    public static final String CALLER_STATUS = "callerStatus";

    @DatabaseField(generatedId = true, columnName = ID)
    public int id;
    @DatabaseField(columnName = CALLER_ID)
    private String callerId;
    @DatabaseField(columnName = CALLER_NAME)
    private String callerName;
    @DatabaseField(columnName = CALLER_PHONE_NO)
    private String phNumber;
    @DatabaseField(columnName = CALLER_TYPE)
    private String callType;
    @DatabaseField(columnName = CALLER_DATE)
    private String callDate;
    @DatabaseField(columnName = CALLER_DURATION)
    private String callDuration;
    @DatabaseField(columnName = CALLER_DIRECTORY)
    private String dir;
    @DatabaseField(columnName = CALLER_DATE_TIME)
    private String callDateTime;
    @DatabaseField(columnName = CALLER_STATUS)
    public String callerStatus;

    /* Default Constructor */
    public Calls() {
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }


    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallDateTime() {
        return callDateTime;
    }

    public void setCallDateTime(String callDateTime) {
        this.callDateTime = callDateTime;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getCallerStatus() {
        return callerStatus;
    }

    public void setCallerStatus(String callerStatus) {
        this.callerStatus = callerStatus;
    }


}

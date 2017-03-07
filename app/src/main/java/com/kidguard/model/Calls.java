package com.kidguard.model;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

@SuppressWarnings("all")
public class Calls implements Serializable {
    private static final long serialVersionUID = -222864131214757024L;

    public static final String ID = "id";
    public static final String CALLER_ID = "callerId";
    private static final String CALLER_NAME = "callerName";
    private static final String CALLER_PHONE_NO = "phNumber";
    private static final String CALLER_TYPE = "callType";
    private static final String CALLER_DATE_TIME_STAMP = "callDateTimeStamp";
    private static final String CALLER_DURATION = "callDuration";
    private static final String CALLER_DIRECTORY = "dir";
    public static final String CALLER_DATE_TIME = "callDateTime";
    public static final String CALLER_STATUS = "callerStatus";
    public static final String CALLER_FROM_ME = "callerFromMe";
    public static final String CALLER_ANSWERED = "callerAnswered";

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
    @DatabaseField(columnName = CALLER_DATE_TIME_STAMP)
    private String callDateTimeStamp;
    @DatabaseField(columnName = CALLER_DURATION)
    private String callDuration;
    @DatabaseField(columnName = CALLER_DIRECTORY)
    private String dir;
    @DatabaseField(columnName = CALLER_DATE_TIME)
    private String callDateTime;
    @DatabaseField(columnName = CALLER_STATUS)
    public String callerStatus;
    @DatabaseField(columnName = CALLER_FROM_ME)
    public String callerFromMe;
    @DatabaseField(columnName = CALLER_ANSWERED)
    public String callerAnswered;

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

    public String getCallDateTimeStamp() {
        return callDateTimeStamp;
    }

    public void setCallDateTimeStamp(String callDateTimeStamp) {
        this.callDateTimeStamp = callDateTimeStamp;
    }

    public String getCallerFromMe() {
        return callerFromMe;
    }

    public void setCallerFromMe(String callerFromMe) {
        this.callerFromMe = callerFromMe;
    }

    public String getCallerAnswered() {
        return callerAnswered;
    }

    public void setCallerAnswered(String callerAnswered) {
        this.callerAnswered = callerAnswered;
    }

}

package com.kidguard.model;

public class Apps {
    private String appName;
    private String packageName;
    private String appDateTime;
    private String appDateTimeStamp;


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppDateTime() {
        return appDateTime;
    }

    public void setAppDateTime(String appDateTime) {
        this.appDateTime = appDateTime;
    }

    public String getAppDateTimeStamp() {
        return appDateTimeStamp;
    }

    public void setAppDateTimeStamp(String appDateTimeStamp) {
        this.appDateTimeStamp = appDateTimeStamp;
    }
}

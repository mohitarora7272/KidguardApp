package com.kidguard.model;

@SuppressWarnings("all")
public class Apps {
    String appname;
    String packageName;
    String appDateTime;
    String appDateTimeStamp;


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
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

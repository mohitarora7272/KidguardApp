package com.kidguard.model;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

@SuppressWarnings("all")
public class Contacts implements Serializable {

    private static final long serialVersionUID = -222864131214757024L;

    public static final String ID = "id";
    public static final String CONTACT_ID = "contact_id";
    public static final String CONTACT_NAME = "contactName";
    public static final String CONTACT_PHONE_NO = "contactPhoneNo";
    public static final String CONTACT_STATUS = "contactStatus";
    public static final String CONTACT_DATE = "contactDate";
    public static final String CONTACT_DATE_TIMESTAMP = "contactDateTimeStamp";


    @DatabaseField(generatedId = true, columnName = ID)
    public int id;
    @DatabaseField(columnName = CONTACT_NAME)
    private String contactName;
    @DatabaseField(columnName = CONTACT_PHONE_NO)
    private String contactPhoneNo;
    @DatabaseField(columnName = CONTACT_STATUS)
    private String contactStatus;
    @DatabaseField(columnName = CONTACT_ID)
    private String contact_id;
    @DatabaseField(columnName = CONTACT_DATE)
    private String contactDate;
    @DatabaseField(columnName = CONTACT_DATE_TIMESTAMP)
    private String contactDateTimeStamp;

    /* Default Constructor */
    public Contacts() {
    }

    public String getContactsPhoneNo() {
        return contactPhoneNo;
    }

    public void setContactsPhoneNo(String contactPhoneNo) {
        this.contactPhoneNo = contactPhoneNo;
    }

    public String getContactsName() {
        return contactName;
    }

    public void setContactsName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(String contactStatus) {
        this.contactStatus = contactStatus;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getContactDate() {
        return contactDate;
    }

    public void setContactDate(String contactDate) {
        this.contactDate = contactDate;
    }

    public String getContactDateTimeStamp() {
        return contactDateTimeStamp;
    }

    public void setContactDateTimeStamp(String contactDateTimeStamp) {
        this.contactDateTimeStamp = contactDateTimeStamp;
    }

}

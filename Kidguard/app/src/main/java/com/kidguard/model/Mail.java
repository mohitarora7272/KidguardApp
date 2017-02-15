package com.kidguard.model;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

@SuppressWarnings("all")
public class Mail implements Serializable {
    private static final long serialVersionUID = -222864131214757024L;

    public static final String ID = "id";
    public static final String MAIL_ID = "mail_id";
    public static final String MAIL_SNIPPET = "snippet";
    public static final String MAIL_DATE = "date";
    public static final String MAIL_SUBJECT = "subject";
    public static final String MAIL_FROM = "from";
    public static final String MAIL_DATE_TIMESTAMP = "date_timestamp";


    @DatabaseField(generatedId = true, columnName = ID)
    public int id;
    @DatabaseField(columnName = MAIL_ID)
    private String mail_id;
    @DatabaseField(columnName = MAIL_SNIPPET)
    private String snippet;
    @DatabaseField(columnName = MAIL_DATE)
    private String date;
    @DatabaseField(columnName = MAIL_SUBJECT)
    private String subject;
    @DatabaseField(columnName = MAIL_FROM)
    private String from;
    @DatabaseField(columnName = MAIL_DATE_TIMESTAMP)
    private String date_timestamp;


    /* Default Constructor */
    public Mail() {
    }

    public String getMail_id() {
        return mail_id;
    }

    public void setMail_id(String mail_id) {
        this.mail_id = mail_id;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate_timestamp() {
        return date_timestamp;
    }

    public void setDate_timestamp(String date_timestamp) {
        this.date_timestamp = date_timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}

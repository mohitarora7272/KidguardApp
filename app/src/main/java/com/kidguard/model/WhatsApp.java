package com.kidguard.model;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class WhatsApp implements Serializable {

    private static final long serialVersionUID = -222864131214757024L;
    public static final String ID = "id";
    public static final String WA_MESSAGE_TITLE = "wa_message_title";
    public static final String WA_MESSAGE_TEXT = "wa_message_text";
    public static final String WA_MESSAGE_TIME = "wa_message_time";
    public static final String WA_MESSAGE_TYPE = "wa_message_type";

    @DatabaseField(generatedId = true, columnName = ID)
    public int id;
    @DatabaseField(columnName = WA_MESSAGE_TITLE)
    public String wa_message_title;
    @DatabaseField(columnName = WA_MESSAGE_TEXT)
    public String wa_message_text;
    @DatabaseField(columnName = WA_MESSAGE_TIME)
    public String wa_message_time;
    @DatabaseField(columnName = WA_MESSAGE_TYPE)
    public String wa_message_type;

    // Default Constructor
    public WhatsApp() {
    }

    public String getWa_message_title() {
        return wa_message_title;
    }

    public void setWa_message_title(String wa_message_title) {
        this.wa_message_title = wa_message_title;
    }

    public String getWa_message_text() {
        return wa_message_text;
    }

    public void setWa_message_text(String wa_message_text) {
        this.wa_message_text = wa_message_text;
    }

    public String getWa_message_time() {
        return wa_message_time;
    }

    public void setWa_message_time(String wa_message_time) {
        this.wa_message_time = wa_message_time;
    }

    public String getWa_message_type() {
        return wa_message_type;
    }

    public void setWa_message_type(String wa_message_type) {
        this.wa_message_type = wa_message_type;
    }
}
package com.kidguard.interfaces;


import com.google.api.services.gmail.GmailScopes;

public interface Constant {

    int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    int REQUEST_CODE_ENABLE_ADMIN = 1;
    int TIME_INTERVAL_FOR_DATA = 1000 * 60;
    int TIME_INTERVAL_FOR_CHECK = 1000;
    int TIME_DELAY_FOR_CHECK = 1000;

    String TAG_SMS = "SMS";
    String TAG_CONTACTS = "CONTACTS";
    String TAG_CALLS = "CALLS";
    String TAG_FILES = "FILES";
    String TAG_LIST_APPS = "APPS_LIST";
    String TAG_IMAGES = "IMAGES";

    String BROADCAST = "com.kidguard.android.action.broadcast";
    String SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    String MY_PREFERENCE = "KidGuardPreference";
    String KEY_ACCOUNT = "account";
    String KEY_IS_ACTIVE = "active";
    String KEY_ACCOUNT_NAME = "accountName";


    String PACKAGE_NAME = "com.google.android.youtube";

    String TABLE_NAME_SMS = "table_sms";

    int REQUEST_ACCOUNT_PICKER = 1000;
    int REQUEST_AUTHORIZATION = 1001;
    int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    String[] SCOPES = {GmailScopes.GMAIL_READONLY};


}

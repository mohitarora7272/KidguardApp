package com.kidguard.interfaces;


import android.net.Uri;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.gmail.GmailScopes;

@SuppressWarnings("all")
public interface Constant {

    int _100 = 100;
    int _200 = 200;
    int _300 = 300;
    int _400 = 400;
    int _500 = 500;
    int DATABASE_VERSION = 1;
    int RESPONSE_CODE = 200;
    int REQUEST_CODE_ENABLE_ADMIN = 1;
    int TIME_INTERVAL_FOR_CHECK = 1000;
    int TIME_DELAY_FOR_CHECK = 1000;
    int REQUEST_ACCOUNT_PICKER = 1000;
    int REQUEST_AUTHORIZATION = 1001;
    int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    int NOTIFICATION_ID = 100;
    int NOTIFICATION_ID_BIG_IMAGE = 101;

    String TAG_LOGIN = "Login";
    String TAG_SMS = "Sms";
    String TAG_CONTACTS = "Contact";
    String TAG_CALLS = "Call";
    String TAG_FILES = "File";
    String TAG_LIST_APPS = "App";
    String TAG_IMAGES = "Image";
    String TAG_CAMERA = "Camera";
    String TAG_VIDEOS = "Video";
    String TAG_EMAIL = "Email";
    String TAG_GOOGLE_DRIVE = "Google_Drive";
    String TAG_BROWSER_HISTORY = "BrowserHistory";
    String TAG_LOCATION = "Locations";
    String TAG_BLOCK_APP = "BlockApp";

    String MY_PREFERENCE = "KidGuardPreference";
    String KEY_IS_ACTIVE = "active";
    String KEY_ACCOUNT_NAME = "accountName";
    String KEY_REGISTRATION_ID = "regId";
    String KEY_EMAIL = "email";
    String KEY_LATITUDE = "latitude";
    String KEY_LONGITUDE = "longitude";
    String KEY_DRIVE_ID = "drive_Id";

    String PACKAGE_NAME = "com.google.android.youtube";
    String BROADCAST = "com.kidguard.android.action.broadcast";
    String SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    String DATABASE_NAME = "Kidguard.db";
    String TRUE = "true";
    String FALSE = "false";
    String ZERO = "0";
    String KEY_COUNT = "count";
    String KEY_DATE_FROM = "dateFrom";
    String KEY_DATE_TO = "dateTo";
    String KEY_SUBJECT = "subject";
    String KEY_TAG = "tag";
    String KEY_SIZE = "size";
    String KEY_TOKEN = "token";
    String KEY_PACKAGE_NAME = "packageName";
    String DRIVE_NAME = "KidguardDrive";
    String DRIVE_DB_NAME = "KidguardDatabase";
    String REGISTRATION_COMPLETE = "registrationComplete";
    String PUSH_NOTIFICATION = "pushNotification";
    String TOPIC_GLOBAL = "global";
    String TOKEN = "token";
    String SENDER_ID = "969615957584";

    String API_TOKEN = "c0u0NAZSCsMaahh4Z74J7haYJGSXWs7MoP0WfAXMI1EWpKER9Spqi2SG2ORD";
    String MULTIPART_FORM_DATA = "multipart/form-data";

    //String ROOT = "http://192.168.0.175:3000/";
    String ROOT = "http://192.168.0.10:3000/";
    String APPENDED_URL = "api/";
    String LOGIN = "login/";
    String CONTACT = "save_mobile_contact/";
    String SMS = "save_sms/";
    String CALL = "save_call/";
    String APP = "save_app/";
    String EMAIL = "save_email/";
    String IMAGE = "save_image/";
    String BROWSER_HISTORY = "save_browser_history/";
    String LOCATION = "save_location/";
    String VIDEO = "save_video/";
    String FILE = "save_file/";
    String DRIVE = "save_drive/";

    String[] SCOPES_GMAIL = {GmailScopes.GMAIL_READONLY};
    String[] SCOPES_GOOGLE_DRIVE = {DriveScopes.DRIVE};

    boolean SUCCESS = true;

    // For Default Browser History
    Uri BOOKMARKS_URI = Uri.parse("content://browser/bookmarks");
    String[] HISTORY_PROJECTION = new String[]{
            "_id", // 0
            "url", // 1
            "visits", // 2
            "date", // 3
            "bookmark", // 4
            "title", // 5
            "favicon", // 6
            "thumbnail", // 7
            "touch_icon", // 8
            "user_entered", // 9
    };

    int HISTORY_PROJECTION_ID_INDEX = 0;
    int HISTORY_PROJECTION_URL_INDEX = 1;
    int HISTORY_PROJECTION_VISITS_INDEX = 2;
    int HISTORY_PROJECTION_DATE_INDEX = 3;
    int HISTORY_PROJECTION_TITLE_INDEX = 5;
}

package com.kidguard.interfaces;


import android.net.Uri;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.gmail.GmailScopes;
import com.kidguard.BuildConfig;

@SuppressWarnings("all")
public interface Constant {

    int _100 = 100;
    int _200 = 200;
    int _300 = 300;
    int _400 = 400;
    int _500 = 500;

    int DATABASE_VERSION                 = 1;
    int REQUEST_CODE_ENABLE_ADMIN        = 1;
    int RESPONSE_CODE                    = 200;
    int TIME_INTERVAL_FOR_CHECK          = 1000;
    int TIME_DELAY_FOR_CHECK             = 1000;
    int REQUEST_ACCOUNT_PICKER           = 1000;
    int REQUEST_AUTHORIZATION            = 1001;
    int REQUEST_GOOGLE_PLAY_SERVICES     = 1002;
    int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    int REQUEST_PERMISSION_READ_CONTACTS  = 1000;
    int REQUEST_PERMISSION_SMS            = 1001;
    int REQUEST_PERMISSION_STORAGE        = 1002;
    int REQUEST_PERMISSION_CALL           = 1003;
    int REQUEST_PERMISSION_LOCATION       = 1004;
    int REQUEST_PERMISSION_GET_ACCOUNTS   = 1005;

    String TAG_SMS             = "Sms";
    String TAG_LOGIN           = "Login";
    String TAG_CALLS           = "Call";
    String TAG_FILES           = "File";
    String TAG_IMAGES          = "Image";
    String TAG_CAMERA          = "Camera";
    String TAG_VIDEOS          = "Video";
    String TAG_EMAIL           = "Email";
    String TAG_WIFI            = "Wifi";
    String TAG_LIST_APPS       = "App";
    String TAG_CONTACTS        = "Contact";
    String TAG_LOCATION        = "Locations";
    String TAG_BLOCK_APP       = "BlockApp";
    String TAG_GOOGLE_DRIVE    = "Drive";
    String TAG_BROWSER_HISTORY = "BrowserHistory";
    String TAG_SYNC_PROCESS    = "SyncProcess";
    String TAG_UNINSTALL       = "Uninstall";

    String DATABASE_NAME       = "Kidguard.db";
    String FALSE               = "false";
    String ZERO                = "0";
    String KEY_COUNT           = "count";
    String KEY_DATE_FROM       = "dateFrom";
    String KEY_DATE_TO         = "dateTo";
    String KEY_SUBJECT         = "subject";
    String KEY_TAG             = "tag";
    String KEY_SIZE            = "size";
    String KEY_PACKAGE_NAME    = "packageName";
    String DRIVE_NAME          = ".KidguardDrive";
    String DRIVE_DB_NAME       = "KidguardDatabase";
    String SENDER_ID           = "969615957584";
    String CUSTOM              = "custom";
    String DATA                = "data";
    String TAGGING             = "tag";
    String MULTIPART_FORM_DATA = "multipart/form-data";
    String PACKAGE             = "package:";

    String ROOT                = BuildConfig.API_URL;
    String APPENDED_URL        = "api/";
    String APPENDED_URL_LOGIN  = "api/auth/";
    String LOGIN               = "login/";
    String SMS                 = "save_sms/";
    String CALL                = "save_call/";
    String APP                 = "save_app/";
    String EMAIL               = "save_email/";
    String IMAGE               = "save_image/";
    String VIDEO               = "save_video/";
    String FILE                = "save_file/";
    String DRIVE               = "save_drive/";
    String LOCATION            = "save_location/";
    String CONTACT             = "save_mobile_contact/";
    String BROWSER_HISTORY     = "save_browser_history/";
    String DEVICE_SYNC         = "sync_process/";
    String VALIDATE_DEVICE     = "validate_device/";

    String MY_PREFERENCE               = "KidGuardPreference";
    String KEY_IS_ACTIVE               = "active";
    String KEY_ACCOUNT_NAME            = "accountName";
    String KEY_REGISTRATION_ID         = "regId";
    String KEY_ACCESSTOKEN             = "access_token";
    String KEY_ID                      = "id";
    String KEY_LATITUDE                = "latitude";
    String KEY_LONGITUDE               = "longitude";
    String KEY_DRIVE_ID                = "drive_Id";
    String KEY_AGAIN                   = "again_try";
    String KEY_ACTIVE_SUBSCRIBER       = "active_subscriber";
    String KEY_MAC_ADDRESS             = "mac_address";

    String[] SCOPES_GMAIL        = {GmailScopes.GMAIL_READONLY, DriveScopes.DRIVE};
    String[] SCOPES_GOOGLE_DRIVE = {DriveScopes.DRIVE};

    // For Default Browser History
    Uri BOOKMARKS_URI           = Uri.parse("content://browser/bookmarks");
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

    int HISTORY_PROJECTION_ID_INDEX     = 0;
    int HISTORY_PROJECTION_URL_INDEX    = 1;
    int HISTORY_PROJECTION_VISITS_INDEX = 2;
    int HISTORY_PROJECTION_DATE_INDEX   = 3;
    int HISTORY_PROJECTION_TITLE_INDEX  = 5;

}

package com.kidguard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.kidguard.interfaces.Constant;
import com.kidguard.preference.Preference;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.services.GoogleAccountService;
import com.kidguard.services.LocationService;
import com.kidguard.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class GCMIntentService extends GCMBaseIntentService implements Constant {
    private static final String TAG = GCMIntentService.class.getSimpleName();

    // Use your PROJECT ID from Google API into SENDER_ID
    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.e(TAG, "registrationId>>>>" + registrationId);
        // Saving registrationId to shared preferences
        Preference.storeRegIdInPref(this, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
    }

    @Override
    protected void onMessage(Context context, Intent data) {
        // Message from PHP server
        String message = data.getStringExtra(CUSTOM);

        try {
            Log.e(TAG, "Message??" + message);
            JSONObject json = new JSONObject(message);
            JSONObject jsonData = json.getJSONObject(DATA);
            String tag = jsonData.getString(TAGGING);
            passServiceIntent(tag, "", "", "", "", "");
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    // Pass Service Intent
    private void passServiceIntent(String tag, String count, String dateFrom, String dateTo, String size, String subject) {
        if (BackgroundDataService.getInstance() != null) {
            stopService(new Intent(this, BackgroundDataService.class));
        }

        if (GoogleAccountService.getInstance() != null) {
            stopService(new Intent(this, GoogleAccountService.class));
        }

        // Check and Delete And Refresh Gallery Google Drive Folder in Sd Card
        File toFiles = new java.io.File(Environment.getExternalStorageDirectory().toString() + java.io.File.separator + DRIVE_NAME);
        Utilities.deleteDirectory(toFiles);
        Utilities.refreshAndroidGallery(this, Uri.fromFile(toFiles));

        if (tag.equals(TAG_LOCATION)) {
            if (LocationService.getInstance() != null) {
                stopService(new Intent(this, LocationService.class));
            }
            startService(new Intent(this, LocationService.class).putExtra(KEY_TAG, tag));
        } else {
            Log.e(TAG, "Tag_In_Notification>>>" + " " + tag);
            startService(new Intent(this, BackgroundDataService.class).putExtra(KEY_TAG, tag).putExtra(KEY_COUNT, count).putExtra(KEY_DATE_FROM, dateFrom).putExtra(KEY_DATE_TO, dateTo).putExtra(KEY_SIZE, size).putExtra(KEY_SUBJECT, subject));
        }
    }

    @Override
    protected void onError(Context arg0, String errorId) {
        Log.e(TAG, "onError: errorId=" + errorId);
    }
}
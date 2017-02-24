package com.kidguard;

import android.*;
import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kidguard.interfaces.Constant;
import com.kidguard.preference.Preference;
import com.kidguard.receivers.BlockAppReceiver;
import com.kidguard.receivers.LocationReceiver;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.services.GoogleAccountService;
import com.kidguard.utilities.Utilities;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

@SuppressWarnings("all")
public class MainActivity extends AppCompatActivity implements Constant, EasyPermissions.PermissionCallbacks {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static MainActivity mActivity;

    public static MainActivity getInstance() {
        return mActivity;
    }

    private static LocationReceiver mLocationReceiver;

    /* onCrate */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* Retrieve the useful instance variables */
        mActivity = MainActivity.this;

    }

    /* onResume */
    protected void onResume() {
        super.onResume();

        if (!Utilities.isNetworkAvailable(this)) {
            Toast.makeText(MainActivity.this, getString(R.string.internet_error),
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!Utilities.checkPlayServices(this)) {
            return;
        }

        /* Check Is Admin Active Or Not */
        if (!Preference.getIsAdminActive(this)) {

            if (BackgroundDataService.getInstance() == null) {

                Utilities.startServices(this, BackgroundDataService.class);
                return;

            } else if (BackgroundDataService.getInstance() != null) {

                stopService(new Intent(this, BackgroundDataService.class));
                Utilities.startServices(this, BackgroundDataService.class);
                return;
            }
            return;
        }


        /*Intent myIntent = new Intent(this, BackgroundDataService.class);
        myIntent.putExtra(KEY_TAG, TAG_SMS);
        myIntent.putExtra(KEY_COUNT, "");
        myIntent.putExtra(KEY_DATE_FROM, "");
        myIntent.putExtra(KEY_DATE_TO, "");
        myIntent.putExtra(KEY_SIZE, "");
        myIntent.putExtra(KEY_SUBJECT, "");
        startService(myIntent);*/


        /* Check Google Account Is Enable Or Not */
        if (Preference.getAccountName(this) == null) {

            if (GoogleAccountService.getInstance() == null) {

                Utilities.startServices(this, GoogleAccountService.class);
                return;

            } else if (GoogleAccountService.getInstance() != null) {

                stopService(new Intent(this, GoogleAccountService.class));
                Utilities.startServices(this, GoogleAccountService.class);
                return;
            }
            return;
        }
        if (Build.VERSION.SDK_INT < 23) {
            //Get email access permission to user
            Utilities.startBackgroundService(MainActivity.this);
        } else {
            checkAndRequestPermissions();
        }

        //Check For Notification Access
        checkNotificationAccess();
    }

    /* Check Notification Access */
    private void checkNotificationAccess() {

        if (android.provider.Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners") == null
                || android.provider.Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners").equals("")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                boolean weHaveNotificationListenerPermission = false;
                for (String service : NotificationManagerCompat.getEnabledListenerPackages(this)) {
                    if (service.equals(getPackageName()))
                        weHaveNotificationListenerPermission = true;
                }

                if (!weHaveNotificationListenerPermission) {
                    //ask for permission
                    Intent intent = new Intent(SETTINGS);
                    startActivity(intent);
                    return;
                }
            }
        }

        /* Request Usage State Permission For Application Is In ForGround Or Not */
        requestUsageStatsPermission();

    }

    // Request Usage Stats Permission on RunTime
    private void requestUsageStatsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {

            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

        } else {

            /* Start Services */
            startServices();
        }
    }

    /* Has Usage Stats Permission */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        @SuppressWarnings("FieldCanBeLocal")
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    /* Start Background Services */
    private void startServices() {
        if (!Utilities.isGpsEnabled(this)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return;
        }

        /* Start Background Receivers Here */
        try {

            startBackgroundReceiver();

            registerLocationReceiver();

        } catch (Exception e) {
            Log.e(TAG, "Exception??" + e.getMessage());
        }

    }

    /* Start Background Check Receiver */
    private void startBackgroundReceiver() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion <= 21) {
            //Log.e("run", "<21");
            Intent alarm = new Intent(this, BlockAppReceiver.class);

            boolean alarmRunning = (PendingIntent.getBroadcast(this, 0,
                    alarm, PendingIntent.FLAG_NO_CREATE) != null);
            if (!alarmRunning) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        TIME_DELAY_FOR_CHECK, TIME_INTERVAL_FOR_CHECK, pendingIntent);
            }
        }
    }

    /* Register Locations Receiver */
    private void registerLocationReceiver() {

        // Initialize Receiver
        mLocationReceiver = new LocationReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST);
        registerReceiver(mLocationReceiver, intentFilter);

        passIntentReceiver();

    }

    /* Pass Intent Receiver */
    private void passIntentReceiver() {
        Intent intent = new Intent(BROADCAST);
        Bundle extras = new Bundle();
        extras.putString("send_data", "test");
        intent.putExtras(extras);
        sendBroadcast(intent);

        if (BackgroundDataService.getInstance() != null) {
            stopService(new Intent(this, BackgroundDataService.class));
        }

        if (GoogleAccountService.getInstance() != null) {
            stopService(new Intent(this, GoogleAccountService.class));
        }
    }

    /* OnActivity Result Call */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ACCOUNT_PICKER:
                    if (data != null &&
                            data.getExtras() != null) {
                        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        Log.e(TAG, "AccName??" + accountName);
                        if (accountName != null) {
                            Preference.setAccountName(this, accountName);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /* onDestroy */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Utilities.isNetworkAvailable(this) && Utilities.isGpsEnabled(this)) {

            if (mLocationReceiver != null) {
                Log.e(TAG, "onDestroy");
                unregisterReceiver(mLocationReceiver);
            }

            if (BackgroundDataService.getInstance() != null) {
                Log.e(TAG, "Stop BackgroundDataService");
                stopService(new Intent(this, BackgroundDataService.class));
            }

            if (GoogleAccountService.getInstance() != null) {
                Log.e(TAG, "Stop GoogleAccountService");
                stopService(new Intent(this, GoogleAccountService.class));
            }
        }

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Get email access permission to user
                    Utilities.startBackgroundService(MainActivity.this);
                } else {
                    Utilities.startBackgroundService(MainActivity.this);
                }
                break;
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private void checkAndRequestPermissions() {
        int SMSPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);

        int storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int callPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG);

        int finelocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int coarselocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (SMSPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (callPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }
        if (finelocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coarselocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }
}

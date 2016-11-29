package com.kidguard;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.kidguard.interfaces.Constant;
import com.kidguard.preference.Preference;
import com.kidguard.receivers.BackgroundCheckReceiver;
import com.kidguard.receivers.LocationReceiver;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.services.GoogleAccountService;
import com.kidguard.utilities.Utilities;

public class MainActivity extends AppCompatActivity implements Constant {

    private static final String TAG = "MainActivity";
    private static MainActivity mActivity;
    private LocationReceiver mLocationReceiver;

    public static MainActivity getInstance() {
        return mActivity;
    }

    /* onCreate */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Retrieve the useful instance variables */
        mActivity = MainActivity.this;
    }

    /* onResume */
    @Override
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

                startBackGroundDataServices();
                return;

            } else if (BackgroundDataService.getInstance() != null) {

                stopService(new Intent(this, BackgroundDataService.class));
                startBackGroundDataServices();
                return;
            }

            return;
        }

        /* Check Google Account Is Enable Or Not */
        if (Preference.getAccountName(this) == null) {

            if (GoogleAccountService.getInstance() == null) {

                startGoogleAccountServices();
                return;

            } else if (GoogleAccountService.getInstance() != null) {

                stopService(new Intent(this, GoogleAccountService.class));
                startGoogleAccountServices();
                return;
            }

            return;
        }


        /* Check For Notification Access */
        checkNotificationAccess();

    }

    /* StartBackGroundDataServices */
    private void startBackGroundDataServices() {
        Intent myIntent = new Intent(this, BackgroundDataService.class);
        startService(myIntent);
    }

    /* StartGoogleAccountServices */
    private void startGoogleAccountServices() {
        Intent myIntent = new Intent(this, GoogleAccountService.class);
        startService(myIntent);
    }


    /* Check Notification Access */
    private void checkNotificationAccess() {
        if (android.provider.Settings.Secure.getString(this.getContentResolver(),
                "enabled_notification_listeners") == null
                || android.provider.Settings.Secure.getString(MainActivity.this.getContentResolver(),
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

        } else {

            /* Start Services */
            startServices();
        }
    }

    /* hasUsageStatsPermission */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    /* Start Background Services */
    private void startServices() {

        if (!Utilities.isGpsEnabled(this)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return;
        }

        /* Start Background Receivers Here */
        try {

            startBackgroundReceiver();

            registerLocationReceiver();

        } catch (Exception e) {
            Log.e("Exception", "MainActivity??" + e.getMessage());
        }
    }

    /* Start Background Receiver */
    private void startBackgroundReceiver() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 21) {
            //Log.e("run", "<21");
            Intent alarm = new Intent(MainActivity.this, BackgroundCheckReceiver.class);

            boolean alarmRunning = (PendingIntent.getBroadcast(MainActivity.this, 0,
                    alarm, PendingIntent.FLAG_NO_CREATE) != null);
            if (alarmRunning == false) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarm, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(MainActivity.this.ALARM_SERVICE);
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        TIME_DELAY_FOR_CHECK, TIME_INTERVAL_FOR_CHECK, pendingIntent);
            }
        }

    }

    /* Register Location Receiver */
    private void registerLocationReceiver() {

        // Initialize Receiver
        mLocationReceiver = new LocationReceiver();
        IntentFilter intentFilter = new IntentFilter(BROADCAST);
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
                    if (resultCode == RESULT_OK && data != null &&
                            data.getExtras() != null) {
                        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                        if (accountName != null) {
                            Preference.setAccountName(this, accountName);

                            if (GoogleAccountService.getInstance() != null) {
                                stopService(new Intent(this, GoogleAccountService.class));
                                startGoogleAccountServices();
                            }
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
                Log.e("onDestroy", "onDestroy");
                unregisterReceiver(mLocationReceiver);
            }

            if (BackgroundDataService.getInstance() != null) {
                Log.e("onDestroy", "Stop");
                stopService(new Intent(this, BackgroundDataService.class));
            }

            if (GoogleAccountService.getInstance() != null) {
                Log.e("onDestroy", "Stop2");
                stopService(new Intent(this, GoogleAccountService.class));
            }
        }

        finish();
    }

}

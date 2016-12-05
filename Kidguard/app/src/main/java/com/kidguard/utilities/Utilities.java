package com.kidguard.utilities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.kidguard.R;
import com.kidguard.interfaces.Constant;
import com.kidguard.receivers.BackgroundCheckReceiver;
import com.kidguard.receivers.LocationReceiver;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.services.GoogleAccountService;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Utilities implements Constant {
    public static LocationReceiver mLocationReceiver;

    /* Start Services */
    public static void startServices(Context ctx, Class nextClassName) {
        Intent myIntent = new Intent(ctx, nextClassName);
        ctx.startService(myIntent);
    }

    /* Check Google Play Service is Install Or Not */
    public static boolean checkPlayServices(final Context ctx) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(ctx);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(((Activity) ctx), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                ((Activity) ctx).finish();
            }
            return false;
        }
        return true;
    }

    /* is GPS Enable */
    public static boolean isGpsEnabled(Context context) {

        if (PackageUtil.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            return false;
        }
    }

    /* Package Util */
    public static class PackageUtil {

        public static boolean checkPermission(Context context, String accessFineLocation) {

            int res = context.checkCallingOrSelfPermission(accessFineLocation);
            return (res == PackageManager.PERMISSION_GRANTED);

        }

    }

    /* Check Internet Available Or Not */
    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /* Show Alert Dialog */
    public static void showAlertDialog(Context ctx, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.encryption_not_supported_ok, null);
        builder.show();
    }

    /* Disable Registered Receiver in Manifest */
    public static void disableReceiver(Context ctx, Class classname) {
        PackageManager pm = ctx.getPackageManager();
        ComponentName component = new ComponentName(ctx, classname);
        pm.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    /* Application Forcefully Stop With This Home Intent */
    public static void stopAppIntent(Context ctx) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(homeIntent);
    }

    /* Get Device Name */
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    /*capitalize Name*/
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /* Check Notification Access */
    public static void checkNotificationAccess(Context ctx) {

        if (android.provider.Settings.Secure.getString(ctx.getContentResolver(),
                "enabled_notification_listeners") == null
                || android.provider.Settings.Secure.getString(ctx.getContentResolver(),
                "enabled_notification_listeners").equals("")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                boolean weHaveNotificationListenerPermission = false;
                for (String service : NotificationManagerCompat.getEnabledListenerPackages(ctx)) {
                    if (service.equals(ctx.getPackageName()))
                        weHaveNotificationListenerPermission = true;
                }

                if (!weHaveNotificationListenerPermission) {
                    //ask for permission
                    Intent intent = new Intent(SETTINGS);
                    ctx.startActivity(intent);
                    return;
                }
            }
        }

        /* Request Usage State Permission For Application Is In ForGround Or Not */
        requestUsageStatsPermission(ctx);

    }

    // Request Usage Stats Permission on RunTime
    public static void requestUsageStatsPermission(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(ctx)) {
            ctx.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

        } else {

            /* Start Services */
            startServices(ctx);
        }
    }

    /* hasUsageStatsPermission */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    /* Start Background Services */
    public static void startServices(Context ctx) {

        if (!Utilities.isGpsEnabled(ctx)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            ctx.startActivity(intent);
            return;
        }

        /* Start Background Receivers Here */
        try {

            startBackgroundReceiver(ctx);

            registerLocationReceiver(ctx);

        } catch (Exception e) {
            Log.e("Exception", "MainActivity??" + e.getMessage());
        }
    }

    /* Start Background Check Receiver */
    public static void startBackgroundReceiver(Context ctx) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion < 21) {
            Log.e("run", "<21");
            Intent alarm = new Intent(ctx, BackgroundCheckReceiver.class);

            boolean alarmRunning = (PendingIntent.getBroadcast(ctx, 0,
                    alarm, PendingIntent.FLAG_NO_CREATE) != null);
            if (alarmRunning == false) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, alarm, 0);
                AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ctx.ALARM_SERVICE);
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        TIME_DELAY_FOR_CHECK, TIME_INTERVAL_FOR_CHECK, pendingIntent);
            }
        }

    }

    /* Register Location Receiver */
    public static void registerLocationReceiver(Context ctx) {

        // Initialize Receiver
        mLocationReceiver = new LocationReceiver();
        IntentFilter intentFilter = new IntentFilter(BROADCAST);
        ctx.registerReceiver(mLocationReceiver, intentFilter);

        passIntentReceiver(ctx);

    }

    /* Pass Intent Receiver */
    public static void passIntentReceiver(Context ctx) {
        Intent intent = new Intent(BROADCAST);
        Bundle extras = new Bundle();
        extras.putString("send_data", "test");
        intent.putExtras(extras);
        ctx.sendBroadcast(intent);


        if (BackgroundDataService.getInstance() != null) {
            ctx.stopService(new Intent(ctx, BackgroundDataService.class));
        }

        if (GoogleAccountService.getInstance() != null) {
            ctx.stopService(new Intent(ctx, GoogleAccountService.class));
        }

    }

    /* Get Location Receiver */
    public static LocationReceiver getLocationReceiver() {
        return mLocationReceiver;
    }

    /* Change Date Format */
    public static String changeDateFormat(String date) {
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        Date dates = new Date(Long.parseLong(date));
        String reportDate = format.format(dates);
        return reportDate;
    }

    /* Change Date Format */
    public static String changeDateToString(Date date) {
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        String reportDate = format.format(date);
        return reportDate;
    }

    /* Get Incremented Date*/
    public static String getIncrementDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);  // number of days to add
        String dt = sdf.format(c.getTime());  // dt is now the new date
        return dt;
    }

    /* Get Decrement Date*/
    public static String getDecrementDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, -1);
        String dt = sdf.format(c.getTime());
        return dt;
    }
    /*
    * Get the extension of a file.
    */
    public static String getExtension(String file) {
        String ext = null;
        int i = file.lastIndexOf('.');

        if (i > 0 && i < file.length() - 1) {
            ext = file.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}

package com.kidguard.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.kidguard.R;

import static com.kidguard.interfaces.Constant.PLAY_SERVICES_RESOLUTION_REQUEST;


public class Utilities {

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
}

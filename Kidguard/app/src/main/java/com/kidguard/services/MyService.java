package com.kidguard.services;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kidguard.MainActivity;
import com.kidguard.R;
import com.rvalerio.fgchecker.AppChecker;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Apps;
import com.kidguard.model.Calls;
import com.kidguard.model.Contacts;
import com.kidguard.model.Files;
import com.kidguard.model.Sms;
import com.kidguard.utilities.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyService extends Service implements LocationListener, Constant {

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double fusedLatitude = 0.0;
    private double fusedLongitude = 0.0;
    private static MyService mContext;
    private Intent intent;
    private Location location;
    private ArrayList<Sms> lstSms;
    private ArrayList<Contacts> lstContacts;
    private ArrayList<Calls> lstCalls;
    private ArrayList<File> fileList;
    private ArrayList<Files> lstfiles;
    private ArrayList<Apps> lstApps;
    private String finalJSON;
    private StringBuffer sbAppend;

    // HomeActivity Instance
    public static MyService getInstance() {
        return mContext;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = MyService.this;

        sbAppend = new StringBuffer();

        /* Get Notification Message Using LocalBroadcastManager */
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

        // Stop Applications Using Package Name
        stopApplications("com.instagram.android");

        // Read SMS
        if (Utilities.PackageUtil.checkPermission(mContext, Manifest.permission.READ_SMS)) {
            getAllSms(mContext);
        }

        // Read Contacts
        if (Utilities.PackageUtil.checkPermission(mContext, Manifest.permission.READ_CONTACTS)) {
            readContacts();
        }

        // Read CALL Details
        if (Utilities.PackageUtil.checkPermission(mContext, Manifest.permission.READ_CALL_LOG)) {
            getCallDetails(mContext);
        }

        // List Of installed Apps
        getListApps();

        /* Fused Location Request */
        startFusedLocation();
        registerRequestUpdate(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        this.intent = intent;
    }

    /* Start Fused Location */
    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                        }

                        @Override
                        public void onConnectionSuspended(int cause) {
                        }

                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                        }
                    }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    /* Stop Fusedd Location */
    public void stopFusedLocation() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            if (intent != null) {
                stopService(intent);
            }
        }
    }

    public void registerRequestUpdate(final LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // every second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isGoogleApiClientConnected()) {
                        mGoogleApiClient.connect();
                    }
                    registerRequestUpdate(listener);
                }
            }
        }, 1000);
    }

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d("Latitude", "Latitude??" + location.getLatitude());
        Log.d("Longitude", "Longitude??" + location.getLongitude());
        generateNotification("Latitude =" + String.valueOf(location.getLatitude() + " " + "Longitude=" + String.valueOf(location.getLongitude())));
        setFusedLatitude(location.getLatitude());
        setFusedLongitude(location.getLongitude());
    }

    public void setFusedLatitude(double lat) {
        fusedLatitude = lat;
    }

    public void setFusedLongitude(double lon) {
        fusedLongitude = lon;
    }

    public double getFusedLatitude() {
        return fusedLatitude;
    }

    public double getFusedLongitude() {
        return fusedLongitude;
    }

    public Location getLocation() {
        return location;
    }

    // Read Phone Contacts
    public void readContacts() {
        lstContacts = new ArrayList<Contacts>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
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
                    // get email and type
                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        String email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        System.out.println("Email " + email + " Email Type : " + emailType);
                    }
                    emailCur.close();
                    // Get note.......
                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    if (noteCur.moveToFirst()) {
                        String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        System.out.println("Note " + note);
                    }
                    noteCur.close();
                    //Get Postal Address....
                    String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] addrWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, null, null, null);
                    while (addrCur.moveToNext()) {
                        String poBox = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                        String street = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        String city = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                        String state = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                        String postalCode = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                        String country = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                        String type = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                        // Do something with these....
                    }
                    addrCur.close();
                    // Get Instant Messenger.........
                    String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] imWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                    Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, imWhere, imWhereParams, null);
                    if (imCur.moveToFirst()) {
                        String imName = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                        String imType;
                        imType = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                    }
                    imCur.close();
                    // Get Organizations.........
                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, orgWhere, orgWhereParams, null);
                    if (orgCur.moveToFirst()) {
                        String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                    }
                    orgCur.close();
                }
            }
        }

        convertToJSONFormat(TAG_CONTACTS, 2);
    }

    // Get All SMS
    public List<Sms> getAllSms(Context ctx) {
        lstSms = new ArrayList<Sms>();
        Sms objSms;
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ((Activity) ctx).startManagingCursor(c);
        }

        int totalSMS = c.getCount();
        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new Sms();
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

        return lstSms;
    }

    // Get Call Details
    private String getCallDetails(Context ctx) {
        lstCalls = new ArrayList<Calls>();
        StringBuffer sb = new StringBuffer();
        ContentResolver cr = this.getContentResolver();
        Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);

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
        convertToJSONFormat(TAG_LIST_APPS, 5);
    }

    // Stop Application
    private void stopApplications(String packageName) {
        AppChecker appChecker = new AppChecker();
        appChecker
                .when(packageName, new AppChecker.Listener() {
                    @Override
                    public void onForeground(String packageName) {

                        stopAppIntent();
                    }

                }).timeout(1000)
                .start(mContext);


    }

    /* Application Forcefully Stop With This Home Intent */
    private void stopAppIntent() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
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
        }

        if (count == 5) {
            finalJSON = "{" + sbAppend + "}";
            Log.d("JSON", "FINAL??" + finalJSON);
            generateFile(finalJSON);

        }
    }

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
        Toast.makeText(mContext, "File generate successfully", Toast.LENGTH_SHORT).show();
    }

    /* Broadcast Receover For Notification Received */
    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            Log.d("package", "package??" + pack);
            Log.d("title", "title??" + title);
            Log.d("text", "text??" + text);

        }
    };

    private void generateNotification(String s) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("data")
                        .setContentText(String.valueOf(s))
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                        .setContentIntent(pIntent)
                        .setDefaults(0)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(String.valueOf(s)));
        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, builder.build());

    }


}

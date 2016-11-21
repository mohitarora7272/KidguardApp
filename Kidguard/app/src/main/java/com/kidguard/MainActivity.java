package com.kidguard;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.kidguard.interfaces.Constant;
import com.kidguard.receivers.MyReceiver;
import com.kidguard.utilities.Utilities;

public class MainActivity extends AppCompatActivity implements Constant {
    private static final String TAG = "MainActivity";
    // Interaction with the DevicePolicyManager
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;
    private MainActivity mActivity;
    protected boolean mAdminActive;
    public static final String BROADCAST = "com.kidguard.android.action.broadcast";
    private MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Prepare to work with the DPM
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(this, DeviceAdminSampleReceiver.class);
        myReceiver = new MyReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Utilities.isNetworkAvailable(this)) {
            Toast.makeText(MainActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
            finish();
        } else {
            // Retrieve the useful instance variables
            mActivity = MainActivity.this;
            mDPM = mActivity.mDPM;
            mDeviceAdminSample = mActivity.mDeviceAdminSample;
            mAdminActive = mActivity.isActiveAdmin();
            Log.d("active", "active??" + mAdminActive);
            if (!mAdminActive) {
                getDeviceAdminPermission(REQUEST_CODE_ENABLE_ADMIN);
                return;
            } else {
                mDPM.setCameraDisabled(mDeviceAdminSample, false);
                startServices();
                checkNotificationAccess();
                return;
            }
        }

    }

    /**
     * Helper to determine if we are an active admin
     */
    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

    // Get Device Admin Permissions
    private void getDeviceAdminPermission(int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_ENABLE_ADMIN:
                // Launch the activity to have the user enable our admin.
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.add_admin_extra_app_text));
                startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                break;
            case REQUEST_CODE_ENABLE_PROFILE:
                Intent intentP = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);
                intentP.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME, getPackageName());
                if (intentP.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intentP, REQUEST_CODE_ENABLE_PROFILE);
                }
                break;
            case REQUEST_CODE_START_ENCRYPTION:
                // Check to see if encryption is even supported on this device (it's optional).
                if (mDPM.getStorageEncryptionStatus() ==
                        DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
                    Utilities.showAlertDialog(this, R.string.encryption_not_supported);
                    return;
                } else if (mDPM.getStorageEncryptionStatus() ==
                        DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE) {
                    Utilities.showAlertDialog(this, R.string.encryption_inactive);
                    return;
                } else if (mDPM.getStorageEncryptionStatus() ==
                        DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE) {
                    Utilities.showAlertDialog(this, R.string.encryption_active);
                    return;
                }
                // Launch the activity to activate encryption.  May or may not return!
                Intent intentE = new Intent(DevicePolicyManager.ACTION_START_ENCRYPTION);
                startActivityForResult(intentE, REQUEST_CODE_START_ENCRYPTION);
                break;
        }
    }

    /* Check Notification Access */
    private void checkNotificationAccess() {
        if (android.provider.Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners") == null
                || android.provider.Settings.Secure.getString(MainActivity.this.getContentResolver(), "enabled_notification_listeners").equals("")) {

            Intent intent = new Intent(
                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivityForResult(intent, REQUEST_CODE_NOTIFICATION);
            return;
        }
        /* Request Usage State Permission For Application Is In ForGround Or Not */
        requestUsageStatsPermission();
    }

    private void startServices() {
        if (!Utilities.isGpsEnabled(this)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_CODE_GPS);
            return;
        }
        registerReceiverActivity();
    }

    /* RegisterReceiver */
    private void registerReceiverActivity() {
        IntentFilter intentFilter = new IntentFilter(BROADCAST);
        registerReceiver(myReceiver, intentFilter);
        Intent intent = new Intent(BROADCAST);
        Bundle extras = new Bundle();
        extras.putString("send_data", "test");
        intent.putExtras(extras);
        sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GPS:
                    Log.e("GPS", "ENABLE");
                    break;

                case REQUEST_CODE_ENABLE_ADMIN:
                    Log.e("ADMIN", "ENABLE");
                    mDPM.setCameraDisabled(mDeviceAdminSample, true);
                    //hideApplicationIcon();
                    startServices();
                    checkNotificationAccess();
                    break;

                case REQUEST_CODE_ENABLE_PROFILE:
                    Log.e("PROFILE", "ENABLE");
                    break;

                case REQUEST_CODE_START_ENCRYPTION:
                    Log.e("ENCRYPTION", "ENCRYPTION");
                    break;

                case REQUEST_CODE_NOTIFICATION:
                    Log.e("NOTIFICATION", "NOTIFICATION");
                    checkNotificationAccess();
                    break;
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
            showToast(context, context.getString(R.string.admin_receiver_status_enabled));
        }

        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            return context.getString(R.string.admin_receiver_status_disable_warning);
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
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

    // Request Usage Stats Permission on RunTime
    void requestUsageStatsPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Utilities.isNetworkAvailable(this) && Utilities.isGpsEnabled(this)) {
            if (myReceiver != null) {
                unregisterReceiver(myReceiver);
            }
        }

        finish();
    }

}

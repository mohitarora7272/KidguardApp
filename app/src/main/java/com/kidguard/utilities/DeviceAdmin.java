package com.kidguard.utilities;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kidguard.R;
import com.kidguard.UninstallActivity;
import com.kidguard.preference.Preference;

public class DeviceAdmin {
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;
    private Context ctx;

    // DeviceAdmin Constructor Call
    public DeviceAdmin(Context ctx) {
        this.ctx = ctx;
        initializeAdmin();
    }

    // Initializing Administrator
    private void initializeAdmin() {
        // Prepare to work with the DPM
        mDPM = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(ctx, DeviceAdminSampleReceiver.class);
    }

    // Check Device Administrator Is Active Or Not
    public void openAdminScreen() {
        // Launch the activity to have the user enable our admin.
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, ctx.getString(R.string.add_admin_extra_app_text));
        ctx.startActivity(intent);
    }

    // Helper to determine if we are an active admin
    public boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
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
            if (intent.getAction().equals(ACTION_DEVICE_ADMIN_DISABLE_REQUESTED)) {
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

            Intent in = new Intent(context, UninstallActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
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
            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            long expr = dpm.getPasswordExpiration(new ComponentName(context, DeviceAdminSampleReceiver.class));
            long delta = expr - System.currentTimeMillis();
            boolean expired = delta < 0L;
            String message = context.getString(expired ? R.string.expiration_status_past : R.string.expiration_status_future);
            showToast(context, message);
        }
    }

    // Method To Camera Disable Enable
    public void cameraDisableEnable() {
        if (mDPM.getCameraDisabled(mDeviceAdminSample)) {
            mDPM.setCameraDisabled(mDeviceAdminSample, false);
        } else {
            mDPM.setCameraDisabled(mDeviceAdminSample, true);
        }
    }
}
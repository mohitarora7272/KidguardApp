package com.kidguard;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.kidguard.interfaces.Constant;
import com.kidguard.preference.Preference;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.services.GoogleAccountService;
import com.kidguard.utilities.Utilities;

public class MainActivity extends AppCompatActivity implements Constant {

    private static final String TAG = "MainActivity";
    private static MainActivity mActivity;

    public static MainActivity getInstance() {
        return mActivity;
    }

    /* onCreate */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Retrieve the useful instance variables */
        mActivity = MainActivity.this;
    }

    /* onResume */
    @Override
    protected void onResume() {
        super.onResume();

        if (!Utilities.isNetworkAvailable(this)) {
            Toast.makeText(MainActivity.this, getString(R.string.internet_error),
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!Utilities.checkPlayServices(this)) {
            return;
        }

        /* Check Is Admin Active Or Not */
        if (!Preference.getIsAdminActive(this)) {

            if (BackgroundDataService.getInstance() == null) {


                Utilities.startServices(this, BackgroundDataService.class);
                return;

            } else if (BackgroundDataService.getInstance() != null) {

                stopService(new Intent(this, BackgroundDataService.class));
                Utilities.startServices(this, BackgroundDataService.class);
                return;
            }

            return;
        }

        Utilities.startServices(this, BackgroundDataService.class);

        /* Check Google Account Is Enable Or Not */
        if (Preference.getAccountName(this) == null) {

            if (GoogleAccountService.getInstance() == null) {

                Utilities.startServices(this, GoogleAccountService.class);
                return;

            } else if (GoogleAccountService.getInstance() != null) {

                stopService(new Intent(this, GoogleAccountService.class));
                Utilities.startServices(this, GoogleAccountService.class);
                return;
            }

            return;
        }

        /* Check For Notification Access */
        Utilities.checkNotificationAccess(this);
    }


    /* OnActivity Result Call */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ACCOUNT_PICKER:
                    if (resultCode == RESULT_OK && data != null &&
                            data.getExtras() != null) {
                        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        Log.e("AccName", "AccName??" + accountName);
                        if (accountName != null) {
                            Preference.setAccountName(this, accountName);

//                            if (GoogleAccountService.getInstance() != null) {
//                                stopService(new Intent(this, GoogleAccountService.class));
//                                Utilities.startServices(this, GoogleAccountService.class);
//                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /* onDestroy */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Utilities.isNetworkAvailable(this) && Utilities.isGpsEnabled(this)) {

            if (Utilities.getLocationReceiver() != null) {
                Log.e("onDestroy", "onDestroy");
                unregisterReceiver(Utilities.getLocationReceiver());
            }

            if (BackgroundDataService.getInstance() != null) {
                Log.e("onDestroy", "Stop");
                stopService(new Intent(this, BackgroundDataService.class));
            }

            if (GoogleAccountService.getInstance() != null) {
                Log.e("onDestroy", "Stop2");
                stopService(new Intent(this, GoogleAccountService.class));
            }
        }

        finish();
    }

}

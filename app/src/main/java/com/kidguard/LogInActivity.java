package com.kidguard;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kidguard.exceptions.APIError;
import com.kidguard.interfaces.Constant;
import com.kidguard.interfaces.RestClient;
import com.kidguard.pojo.LogInPOJO;
import com.kidguard.preference.Preference;
import com.kidguard.utilities.ApiClient;
import com.kidguard.utilities.ErrorUtils;
import com.kidguard.utilities.Utilities;

import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity implements Constant, View.OnClickListener, EasyPermissions.PermissionCallbacks, Callback<LogInPOJO> {
    private static final String TAG = LogInActivity.class.getSimpleName();

    private CoordinatorLayout coordinatorLayout;
    private EditText edt_Email;
    private EditText edt_DeviceCode;
    private ProgressDialog progressDialog;
    private String macAddress;
    private ApiClient apiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBar();
        passNextActivityIntent();
        setContentView(R.layout.activity_login);
        initializeView();
    }

    // Initialize View
    private void initializeView() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        edt_Email = (EditText) findViewById(R.id.edt_Email);
        edt_DeviceCode = (EditText) findViewById(R.id.edt_DeviceCode);
        Button btn_SignIn = (Button) findViewById(R.id.btn_SignIn);
        btn_SignIn.setOnClickListener(this);
        Button btn_SignUp = (Button) findViewById(R.id.btn_SignUp);
        btn_SignUp.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        // Get Mac Address
        if (Build.VERSION.SDK_INT < 23) {
            macAddress = Utilities.getMacAddressBelowMarshmallow(this);
        } else {
            macAddress = Utilities.getMacAddressOnMarshmallow();
            checkPermissionReadContacts();
        }

        // Set Mac Address into Preference
        Preference.setMacAddress(this, macAddress);
    }

    // Set ActionBar Hide
    private void setActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_SignIn:
                signIn();
                break;
            case R.id.btn_SignUp:
                signUp();
                break;
            default:
                break;
        }
    }

    // Sign Up
    private void signUp() {
        if (Utilities.isNetworkAvailable(this)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SIGN_UP_URL)));
        } else {
            Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.internet_error));
        }
    }

    // Sign In
    private void signIn() {
        if (Utilities.isNetworkAvailable(this)) {
            if (Utilities.isEmpty(edt_Email)) {
                Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.enter_email));
                return;
            }

            if (!Utilities.isValidEmail(edt_Email.getText().toString())) {
                Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.enter_valid_email));
                return;
            }

            if (Utilities.isEmpty(edt_DeviceCode)) {
                Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.enter_deviceCode));
                return;
            }

            Utilities.showProgressDialog(this, progressDialog);
            getLogin();

        } else {
            Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.internet_error));
        }
    }

    // Get Login
    private void getLogin() {
        apiClient = new ApiClient(TAG_LOGIN);
        RestClient restClientAPI = apiClient.getClient();

        Call<LogInPOJO> call = restClientAPI.logInRequest(edt_Email.getText().toString(), edt_DeviceCode.getText().toString(), Preference.getRegIdInPref(this), macAddress, BuildConfig.VERSION_NAME, String.valueOf(BuildConfig.VERSION_CODE), Utilities.getDeviceVersion(), Utilities.getDeviceModel(), Utilities.getDeviceManufacture());
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<LogInPOJO> call, Response<LogInPOJO> response) {
        passLogInResponse(response);
    }

    @Override
    public void onFailure(Call<LogInPOJO> call, Throwable t) {
        Log.e(TAG, "onFailure?? " + t.getMessage());
        Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.failed_to_connect_with_server));
        logInResponseFailure();
    }

    // Pass LogIn Response
    private void passLogInResponse(Response<LogInPOJO> response) {
        LogInPOJO logIn = response.body();
        int code = response.code();
        Log.e(TAG, "code>>>>" + code);
        if (response.isSuccessful()) {
            Utilities.dismissProgressDialog(progressDialog);
            if (logIn.getStatus() == RESPONSE_CODE) {
                Preference.setAccessToken(this, logIn.getUser().getAccessToken());
                Preference.setID(this, String.valueOf(logIn.getUser().getId()));
                Preference.setActiveSubscriber(this, "true");
                passNextActivityIntent();
            }
        } else {
            if (Preference.getAgainTry(this) != null) {
                getLogin();
                Preference.setAgainTry(this, null);
            }

            try {
                APIError error = ErrorUtils.parseError(response, apiClient.getRetrofit());
                Log.e(TAG, "login failed error>>" + error.message());
                Utilities.showSnackBar(this, coordinatorLayout, error.message());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "parse response error>>" + e.getMessage());
                Utilities.showSnackBar(this, coordinatorLayout, e.getMessage());
            } finally {
                Utilities.dismissProgressDialog(progressDialog);
            }
        }
    }

    // LogIn Response Failure
    private void logInResponseFailure() {
        Utilities.dismissProgressDialog(progressDialog);
    }

    // Pass Next Activity Intent
    private void passNextActivityIntent() {
        if (Preference.getID(this) != null && !Preference.getID(this).isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Check Permission Read Contacts
    private void checkPermissionReadContacts() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.contactMsg), REQUEST_PERMISSION_READ_CONTACTS, Manifest.permission.READ_CONTACTS);
        }
    }

    // Check Permission Read Sms
    private void checkPermissionReadSms() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_SMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.smsMsg), REQUEST_PERMISSION_SMS, Manifest.permission.READ_SMS);
        }
    }

    // Check Permission Storage
    private void checkPermissionStorage() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE) && !EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, getString(R.string.storageMsg), REQUEST_PERMISSION_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    // Check Permission Call
    private void checkPermissionCall() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_CALL_LOG)) {
            EasyPermissions.requestPermissions(this, getString(R.string.callMsg), REQUEST_PERMISSION_CALL, Manifest.permission.READ_CALL_LOG);
        }
    }

    // Check Permission Location
    private void checkPermissionLocation() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION) && !EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            EasyPermissions.requestPermissions(this, getString(R.string.locationMsg), REQUEST_PERMISSION_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    // Check Permission Get Account
    private void checkPermissionGetAccount() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.getAccountMsg), REQUEST_PERMISSION_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
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

    // Permissions Request Result Callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        switch (requestCode) {
            case REQUEST_PERMISSION_READ_CONTACTS:
                checkPermissionReadSms();
                break;
            case REQUEST_PERMISSION_SMS:
                checkPermissionStorage();
                break;
            case REQUEST_PERMISSION_STORAGE:
                checkPermissionCall();
                break;
            case REQUEST_PERMISSION_CALL:
                checkPermissionLocation();
                break;
            case REQUEST_PERMISSION_LOCATION:
                checkPermissionGetAccount();
                break;
            case REQUEST_PERMISSION_GET_ACCOUNTS:
                break;
            default:
                break;
        }
    }
}
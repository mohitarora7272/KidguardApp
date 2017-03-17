package com.kidguard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("all")
public class LogInActivity extends AppCompatActivity implements Constant, View.OnClickListener {
    private static final String TAG = LogInActivity.class.getSimpleName();

    private static LogInActivity mActivity;
    private CoordinatorLayout coordinatorLayout;
    private EditText edt_Email;
    private EditText edt_DeviceCode;
    private Button btn_SignIn;
    private ProgressDialog progressDialog;
    private String macAddress;
    private ApiClient apiClient;

    public static LogInActivity getInstance() {
        return mActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passNextActivityIntent();
        setContentView(R.layout.activity_login);
        iniView();
    }

    /* Initialize View */
    private void iniView() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        edt_Email = (EditText) findViewById(R.id.edt_Email);
        edt_DeviceCode = (EditText) findViewById(R.id.edt_DeviceCode);
        btn_SignIn = (Button) findViewById(R.id.btn_SignIn);
        btn_SignIn.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        if (Build.VERSION.SDK_INT < 23) {
            macAddress = Utilities.getMacAddressBelowMarshmallow(this);
        } else {
            macAddress = Utilities.getMacAddressOnMarshmallow();
        }

        //macAddress = "64:00:6a:3e:28:fc";

         /* Set Mac Address */
        Preference.setMacAddress(this, macAddress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_SignIn:
                signIn();
                break;

            default:

                break;
        }
    }

    private void signIn() {
        if (Utilities.isNetworkAvailable(getApplicationContext())) {

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
            Utilities.showSnackBar(this, coordinatorLayout,
                    getString(R.string.internet_error));
        }
    }

    // 64:00:6a:3e:28:fc
    private void getLogin() {
        apiClient = new ApiClient(TAG_LOGIN);
        RestClient restClientAPI = apiClient.getClient();

        Call<LogInPOJO> call = restClientAPI.logInRequest(edt_Email.getText().toString(), edt_DeviceCode.getText().toString(),
                Preference.getRegIdInPref(this), macAddress, BuildConfig.VERSION_NAME, String.valueOf(BuildConfig.VERSION_CODE),
                Utilities.getDeviceVersion(), Utilities.getDeviceModel(), Utilities.getDeviceManufacture());

        Callback<LogInPOJO> callback = new Callback<LogInPOJO>() {

            @Override
            public void onResponse(Call<LogInPOJO> call, Response<LogInPOJO> response) {
                passLogInResponse(response);
            }

            @Override
            public void onFailure(Call<LogInPOJO> call, Throwable t) {
                Log.e(TAG, "onFailure?? " + t.getMessage());
                Utilities.showSnackBar(LogInActivity.this, coordinatorLayout, getString(R.string.failed_to_connect_with_server));
                logInResponseFailure();
            }
        };

        call.enqueue(callback);
    }

    /* Pass LogIn Response */
    public void passLogInResponse(Response<LogInPOJO> response) {
        LogInPOJO logIn = response.body();
        int code = response.code();
        Log.e("code", "code>>>>" + code);
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

                Log.e("login failed", "error???>>" + error.message());
                Utilities.showSnackBar(this, coordinatorLayout, error.message());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("parse response error", "error???>>" + e.getMessage());
                Utilities.showSnackBar(this, coordinatorLayout, e.getMessage());
            } finally {
                Utilities.dismissProgressDialog(progressDialog);
            }
        }
    }

    /* LogIn Response Failure */
    public void logInResponseFailure() {
        Utilities.dismissProgressDialog(progressDialog);
    }

    /* Pass Next Activity Intent */
    public void passNextActivityIntent() {
        if (Preference.getID(this) != null && !Preference.getID(this).isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
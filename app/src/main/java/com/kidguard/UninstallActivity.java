package com.kidguard;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kidguard.exceptions.APIError;
import com.kidguard.interfaces.Constant;
import com.kidguard.interfaces.RestClient;
import com.kidguard.pojo.ApiResponsePOJO;
import com.kidguard.preference.Preference;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.utilities.ApiClient;
import com.kidguard.utilities.ErrorUtils;
import com.kidguard.utilities.Utilities;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("all")
public class UninstallActivity extends AppCompatActivity implements Constant, View.OnClickListener, Callback<ApiResponsePOJO> {
    private static final String TAG = UninstallActivity.class.getSimpleName();
    private static UninstallActivity mActivity;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog progressDialog;
    private EditText edt_DeviceCode;
    private EditText edt_Email;
    private Button btn_Uninstall;
    private Button btn_Activate;
    private ApiClient apiClient;
    private String macAddress;

    public static UninstallActivity getInstance() {
        return mActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uninstall);
        mActivity = UninstallActivity.this;
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        edt_DeviceCode = (EditText) findViewById(R.id.edt_DeviceCode);
        edt_Email = (EditText) findViewById(R.id.edt_Email);
        btn_Uninstall = (Button) findViewById(R.id.btn_Uninstall);
        btn_Uninstall.setOnClickListener(this);
        btn_Activate = (Button) findViewById(R.id.btn_Activate);
        btn_Activate.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        /* Getting Mac Address */
        if (Preference.getMacAddress(this) != null && !Preference.getMacAddress(this).isEmpty()) {
            macAddress = Preference.getMacAddress(this);
        } else {
            if (Build.VERSION.SDK_INT < 23) {
                macAddress = Utilities.getMacAddressBelowMarshmallow(this);
            } else {
                macAddress = Utilities.getMacAddressOnMarshmallow();
            }
        }

        //macAddress = "64:00:6a:3e:28:fc";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Uninstall:
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

                    getUninstall();

                } else {
                    Utilities.showSnackBar(this, coordinatorLayout,
                            getString(R.string.internet_error));
                }
                break;

            case R.id.btn_Activate:
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
                }
                break;

            default:
                break;
        }

    }

    /* Get Uninstall */
    private void getUninstall() {
        apiClient = new ApiClient(TAG_UNINSTALL);
        RestClient restClientAPI = apiClient.getClient();

        Call<ApiResponsePOJO> call = restClientAPI.uninstallRequest(edt_Email.getText().toString(),
                edt_DeviceCode.getText().toString(), macAddress, Preference.getAccessToken(this));
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
        ApiResponsePOJO apiResponse = response.body();
        int code = response.code();
        Log.e("code", "code>>>>" + code);
        if (response.isSuccessful()) {
            Utilities.dismissProgressDialog(progressDialog);
            if (apiResponse.getStatus() == RESPONSE_CODE) {
                uninstallApp();
            }
        } else {

            try {
                APIError error = ErrorUtils.parseError(response, apiClient.getRetrofit());

                Log.e("Uninstall failed", "error???>>" + error.message());
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

    @Override
    public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
        Log.e(TAG, "onFailure?? " + t.getMessage());
        Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.failed_to_connect_with_server));
        Utilities.dismissProgressDialog(progressDialog);
    }

    /* Uninstall App */
    public void uninstallApp() {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse(PACKAGE + getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showToast(this, getString(R.string.activate_message));
        Utilities.startUninstallActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Preference.getIsAdminActive(this)) {
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utilities.startUninstallActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utilities.startUninstallActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utilities.startUninstallActivity(this);
    }

    /* show Message in toast */
    void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
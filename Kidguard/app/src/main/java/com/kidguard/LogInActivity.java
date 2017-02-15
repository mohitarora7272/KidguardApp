package com.kidguard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kidguard.interfaces.Constant;
import com.kidguard.pojo.LogInPOJO;
import com.kidguard.preference.Preference;
import com.kidguard.services.RestClientService;
import com.kidguard.utilities.Utilities;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("all")
public class LogInActivity extends AppCompatActivity implements Constant, View.OnClickListener,
        Callback<LogInPOJO> {
    private static final String TAG = LogInActivity.class.getSimpleName();

    private static LogInActivity mActivity;
    private CoordinatorLayout coordinatorLayout;
    private EditText edt_Email;
    private EditText edt_Password;
    @SuppressWarnings("FieldCanBeLocal")
    private Button btn_SignIn;
    private ProgressDialog progressDialog;

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

    private void iniView() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        edt_Email = (EditText) findViewById(R.id.edt_Email);
        edt_Password = (EditText) findViewById(R.id.edt_Password);
        btn_SignIn = (Button) findViewById(R.id.btn_SignIn);
        btn_SignIn.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_SignIn:

                if (Utilities.isNetworkAvailable(getApplicationContext())) {

                    if (Utilities.isEmpty(edt_Email)) {
                        Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.enter_email));
                        return;
                    }

                    if (!Utilities.isValidEmail(edt_Email.getText().toString())) {
                        Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.enter_valid_email));
                        return;
                    }

                    if (Utilities.isEmpty(edt_Password)) {
                        Utilities.showSnackBar(this, coordinatorLayout, getString(R.string.enter_password));
                        return;
                    }

                    Utilities.showProgressDialog(this, progressDialog);

                    new RestClientService(TAG_LOGIN,
                            edt_Email.getText().toString(),
                            edt_Password.getText().toString(),
                            Preference.getRegIdInPref(this));

                } else {
                    Utilities.showSnackBar(this, coordinatorLayout,
                            getString(R.string.internet_error));
                }
                break;

            default:

                break;
        }
    }

    /* Get Response From LogIn */
    @Override
    public void onResponse(Call<LogInPOJO> call, Response<LogInPOJO> response) {

        LogInPOJO logIn = response.body();

        int code = response.code();
        if (code == RESPONSE_CODE) {

            Utilities.dismissProgressDialog(progressDialog);

            if (logIn.getSuccess() == SUCCESS) {

                Preference.setEmail(this, edt_Email.getText().toString());
                passNextActivityIntent();

            } else {
                Utilities.showSnackBar(LogInActivity.this, coordinatorLayout,
                        String.valueOf(getString(R.string.internet_error)));
            }

        } else {

            Utilities.dismissProgressDialog(progressDialog);
        }
    }

    @Override
    public void onFailure(Call<LogInPOJO> call, Throwable t) {
        Utilities.dismissProgressDialog(progressDialog);
        Log.e(TAG, "onFailure?? " + t.getMessage());
        Utilities.showSnackBar(this, coordinatorLayout,
                String.valueOf(getString(R.string.failed_to_connect_with_server)));
    }

    /* Pass Next Activity Intent */
    private void passNextActivityIntent() {
        if (Preference.getEmail(this) != null && !Preference.getEmail(this).equals("")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

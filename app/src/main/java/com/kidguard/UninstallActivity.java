package com.kidguard;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kidguard.interfaces.Constant;
import com.kidguard.preference.Preference;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.utilities.Utilities;

@SuppressWarnings("all")
public class UninstallActivity extends AppCompatActivity implements Constant, View.OnClickListener {
    private static UninstallActivity mActivity;
    private EditText edt_DeviceCode;
    private EditText edt_Email;
    private Button btn_Uninstall;
    private Button btn_Activate;

    public static UninstallActivity getInstance() {
        return mActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uninstall);
        mActivity = UninstallActivity.this;
        edt_DeviceCode = (EditText) findViewById(R.id.edt_DeviceCode);
        edt_Email = (EditText) findViewById(R.id.edt_Email);
        btn_Uninstall = (Button) findViewById(R.id.btn_Uninstall);
        btn_Uninstall.setOnClickListener(this);
        btn_Activate = (Button) findViewById(R.id.btn_Activate);
        btn_Activate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Uninstall:
                //Utilities.UninstallApp(this);
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

    void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

package com.kidguard.preference;


import android.content.Context;
import android.content.SharedPreferences;

import com.kidguard.interfaces.Constant;

public class Preference implements Constant {

    static SharedPreferences sharedpreferences;
    static SharedPreferences.Editor editor;
    Context context;


    public static void setAccountEnable(Context ctx, Boolean account) {
        sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putBoolean(KEY_ACCOUNT, account);
        editor.commit();
    }

    public static Boolean getAccountEnable(Context ctx) {
        sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        Boolean account = sharedpreferences.getBoolean(KEY_ACCOUNT, false);
        return account;
    }

    public static void setIsAdminActive(Context ctx, Boolean is_register) {
        sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putBoolean(KEY_IS_ACTIVE, is_register);
        editor.commit();
    }

    public static Boolean getIsAdminActive(Context ctx) {
        sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        Boolean is_register = sharedpreferences.getBoolean(KEY_IS_ACTIVE, false);
        return is_register;
    }


    public static void setAccountName(Context ctx, String account) {
        sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString(KEY_ACCOUNT_NAME, account);
        editor.commit();
    }

    public static String getAccountName(Context ctx) {
        sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        String account = sharedpreferences.getString(KEY_ACCOUNT_NAME, null);
        return account;
    }
}

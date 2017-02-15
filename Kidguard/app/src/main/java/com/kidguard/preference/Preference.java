package com.kidguard.preference;


import android.content.Context;
import android.content.SharedPreferences;

import com.kidguard.interfaces.Constant;

@SuppressWarnings("all")
public class Preference implements Constant {

    public static void setIsAdminActive(Context ctx, Boolean is_register) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(KEY_IS_ACTIVE, is_register).commit();
    }

    public static Boolean getIsAdminActive(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        Boolean is_register = sharedpreferences.getBoolean(KEY_IS_ACTIVE, false);
        return is_register;
    }

    public static void setAccountName(Context ctx, String account) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_ACCOUNT_NAME, account).commit();
    }

    public static String getAccountName(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        String account = sharedpreferences.getString(KEY_ACCOUNT_NAME, null);
        return account;
    }

    public static void storeRegIdInPref(Context ctx, String token) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_REGISTRATION_ID, token).commit();
    }

    public static String getRegIdInPref(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        String regId = sharedpreferences.getString(KEY_REGISTRATION_ID, null);
        return regId;
    }

    public static void setEmail(Context ctx, String email) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_EMAIL, email).commit();
    }

    public static String getEmail(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        String email = sharedpreferences.getString(KEY_EMAIL, null);
        return email;
    }

    public static void setLatLong(Context ctx, double latitude, double longitude) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_LATITUDE, String.valueOf(latitude)).putString(KEY_LONGITUDE, String.valueOf(longitude)).commit();
    }

    public static String getLatitude(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_LATITUDE, null);
    }

    public static String getLongitude(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_LONGITUDE, null);
    }

    public static void setDriveId(Context ctx, int id) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(KEY_DRIVE_ID, id).commit();
    }

    public static int getDriveId(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(KEY_DRIVE_ID, 1);
    }
}

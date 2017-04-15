package com.kidguard.preference;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.kidguard.interfaces.Constant;

public class Preference implements Constant {

    public static void setIsAdminActive(Context ctx, Boolean is_register) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(KEY_IS_ACTIVE, is_register).apply();
    }

    @NonNull
    public static Boolean getIsAdminActive(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(KEY_IS_ACTIVE, false);
    }

    public static void setAccountName(Context ctx, String account) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_ACCOUNT_NAME, account).apply();
    }

    public static String getAccountName(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_ACCOUNT_NAME, null);
    }

    public static void storeRegIdInPref(Context ctx, String token) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_REGISTRATION_ID, token).apply();
    }

    public static String getRegIdInPref(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_REGISTRATION_ID, null);
    }

    public static void setAccessToken(Context ctx, String token) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_ACCESSTOKEN, token).apply();
    }

    public static String getAccessToken(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_ACCESSTOKEN, null);
    }

    public static void setAgainTry(Context ctx, String again) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_AGAIN, again).apply();
    }

    public static String getAgainTry(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_AGAIN, null);
    }

    public static void setID(Context ctx, String id) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_ID, id).apply();
    }

    public static String getID(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_ID, null);
    }

    public static void setLatLong(Context ctx, double latitude, double longitude) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_LATITUDE, String.valueOf(latitude)).putString(KEY_LONGITUDE, String.valueOf(longitude)).apply();
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
        editor.putInt(KEY_DRIVE_ID, id).apply();
    }

    public static int getDriveId(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(KEY_DRIVE_ID, 1);
    }

    public static void setActiveSubscriber(Context ctx, String is_Active) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_ACTIVE_SUBSCRIBER, is_Active).apply();
    }

    public static String getActiveSubscriber(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_ACTIVE_SUBSCRIBER, null);
    }

    public static void setMacAddress(Context ctx, String macAddress) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_MAC_ADDRESS, macAddress).apply();
    }

    public static String getMacAddress(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_MAC_ADDRESS, null);
    }
}
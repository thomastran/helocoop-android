package com.novahub.voipcall.sharepreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by samnguyen on 11/01/2016.
 */
public class SharePreferences {

    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String ACTIVATE_CODE = "ACTIVATE_CODE";
    public static final String NAME_OF_USER = "NAME_OF_USER";
    public static final String EMAIL_OF_USER = "EMAIL_OF_USER";
    public static final String HOME_CITY = "HOME_CITY";
    public static final String TOKEN = "TOKEN";
    public static final String INSTANCE_ID = "INSTANCE_ID";
    public static final String IS_REQUESTED_CODE = "IS_REQUESTED_CODE";
    public static final String IS_ACTIVATED_CODE = "IS_ACTIVATED_CODE";
    public static final String IS_UPDATED_INFO = "IS_UPDATED_INFO";
    public static final String ON_SAMARITANS = "IS_ON_SAMARITANS";
    public static final String EMPTY = " ";

    public static void saveData(Context context, String typeOfAction, String data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(typeOfAction, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(typeOfAction, data);
        editor.commit();
    }

    public static void saveData(Context context, String typeOfAction, boolean data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(typeOfAction, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(typeOfAction, data);
        editor.commit();
    }

    public static String getData(Context context, String typeOfAction) {
        SharedPreferences sharePreferences = context.getSharedPreferences(typeOfAction, Context.MODE_PRIVATE);
        return sharePreferences.getString(typeOfAction, EMPTY);
    }

    public static boolean getDataBoolean(Context context, String typeOfAction) {
        SharedPreferences sharePreferences = context.getSharedPreferences(typeOfAction, Context.MODE_PRIVATE);
        return sharePreferences.getBoolean(typeOfAction, false);
    }

    public static void setDoneAction(Context context, String typeOfAction) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(typeOfAction, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(typeOfAction, true);
        editor.commit();
    }

    public static boolean isDoneAction(Context context, String typeOfAction) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(typeOfAction, Context.MODE_PRIVATE);
        return sharedPreferences.contains(typeOfAction);

    }

    public static int checkDoneAction(boolean isRequestedCode, boolean isActivatedCode, boolean isUpdatedInfo) {

        if (!isRequestedCode && !isActivatedCode && !isUpdatedInfo) {
            return 1;
        } else {
            if(isRequestedCode && !isActivatedCode && !isUpdatedInfo) {
                return 2;
            } else {
                if(isRequestedCode && isActivatedCode && !isUpdatedInfo) {
                    return 3;
                } else {
                    return 4;
                }
            }
        }
    }




}

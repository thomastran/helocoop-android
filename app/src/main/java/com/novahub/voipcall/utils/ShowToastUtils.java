package com.novahub.voipcall.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by samnguyen on 18/02/2016.
 */
public class ShowToastUtils {
    public static void showMessage(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}

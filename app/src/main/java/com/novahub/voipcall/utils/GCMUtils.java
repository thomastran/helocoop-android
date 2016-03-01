package com.novahub.voipcall.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.novahub.voipcall.R;
import com.novahub.voipcall.services.RegistrationIntentService;
import com.novahub.voipcall.services.UpdateInstanceIdService;
import com.novahub.voipcall.sharepreferences.SharePreferences;

/**
 * Created by samnguyen on 18/02/2016.
 */
public class GCMUtils {
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String TAG = "GCMUtils";

    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public static void requestGcmInstanceId(Activity activity) {
        Intent intent = new Intent(activity, RegistrationIntentService.class);
        activity.startService(intent);
    }

    public static boolean isExistedGcmInstanceId(Context context) {
        if (SharePreferences.getData(context, SharePreferences.INSTANCE_ID) == SharePreferences.EMPTY)
            return false;
        else
            return true;
    }

    public static void checkGCMInstanceId(Context context, Activity activity) {
        if (!isExistedGcmInstanceId(context)) {
            if (NetworkUtil.isOnline(context)) {
                if (checkPlayServices(activity)) {
                    requestGcmInstanceId(activity);
                }
            } else {
                ShowToastUtils.showMessage(activity, activity.getString(R.string.turn_on_the_internet));
            }
        }
    }

    public static void startServiceUpdateInstanceId(Context context) {
        Intent intent = new Intent(context, UpdateInstanceIdService.class);
        context.startService(intent);
    }
}

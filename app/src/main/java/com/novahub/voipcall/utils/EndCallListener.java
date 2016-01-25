package com.novahub.voipcall.utils;

import android.app.Activity;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.novahub.voipcall.activity.MakingCallConferenceActivity;
import com.novahub.voipcall.activity.ShowResultsActivity;

/**
 * Created by samnguyen on 25/01/2016.
 */
public class EndCallListener extends PhoneStateListener {
    private static String LOG_TAG = "EndCallListener";
    private Activity activity;
    public EndCallListener(Activity activity) {
        this.activity = activity;
    }
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (Asset.distanceList != null) {
            Intent intent = new Intent(activity, ShowResultsActivity.class);
            intent.putExtra(Asset.FROM_INCOMING_CALL, Asset.FROM_INCOMING_CALL);
            activity.startActivity(intent);
            activity.finish();
        }
        if(TelephonyManager.CALL_STATE_RINGING == state) {
            Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);

        }
        if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.

            Log.i(LOG_TAG, "OFFHOOK");


        }
        if(TelephonyManager.CALL_STATE_IDLE == state) {
            //when this state occurs, and your flag is set, restart your app
            Log.i(LOG_TAG, "IDLE");
        }
    }
}

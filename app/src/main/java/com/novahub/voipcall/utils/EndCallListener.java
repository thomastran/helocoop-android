package com.novahub.voipcall.utils;

import android.app.Activity;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.novahub.voipcall.activity.IncomingGcmRequestActivity;
import com.novahub.voipcall.activity.ShowResultsActivity;

/**
 * Created by samnguyen on 25/01/2016.
 */
public class EndCallListener extends PhoneStateListener {
    private static String LOG_TAG = "EndCallListener";
    private Activity activity;
    private boolean isChecked;
    public EndCallListener(Activity activity, boolean isChecked) {
        this.activity = activity;
        this.isChecked = isChecked;
    }
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        if (Asset.listOfGoodSamaritans != null) {
            Intent intent = new Intent(activity, ShowResultsActivity.class);
            intent.putExtra(Asset.FROM_INCOMING_CALL, Asset.FROM_INCOMING_CALL);
            activity.startActivity(intent);
            activity.finish();
        }

        if (isChecked) {
            if (TempDataUtils.isExistedDataOfCallerInfo()) {
                Intent intent = new Intent(activity, IncomingGcmRequestActivity.class);
                intent.putExtra(Asset.IS_FROM_SERVER, false);
                activity.startActivity(intent);
                activity.finish();
            }
        }

        if(TelephonyManager.CALL_STATE_RINGING == state) {
            Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            Asset.isRinging = true;
        }
        if(TelephonyManager.CALL_STATE_IDLE == state) {
            //when this state occurs, and your flag is set, restart your app
            if (isChecked & Asset.isRinging) {
                if (Asset.listOfCallerAndSamaritans != null) {
                    Intent intent = new Intent(activity, ShowResultsActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }

            }
        }
        if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
            Log.i(LOG_TAG, "OFFHOOK");
        }

    }
}

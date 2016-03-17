package com.novahub.voipcall.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.novahub.voipcall.services.UpdateLocationService;

/**
 * Created by samnguyen on 17/03/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";



    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, UpdateLocationService.class);
        context.startService(i);
    }
}
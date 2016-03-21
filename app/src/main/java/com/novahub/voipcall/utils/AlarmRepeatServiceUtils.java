package com.novahub.voipcall.utils;

/**
 * Created by samnguyen on 18/03/2016.
 */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.novahub.voipcall.receiver.AlarmReceiver;


/**
 * Created by samnguyen on 17/03/2016.
 */
public class AlarmRepeatServiceUtils {


    public static void updateLocationService(Context activity) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(activity, AlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(activity, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, Asset.TIMEREPEATEALARM, pIntent);
    }


    public static void cancelUpdateLocationService(Context activity) {
        Intent intent = new Intent(activity, AlarmReceiver.class);


        final PendingIntent pIntent = PendingIntent.getBroadcast(activity, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarm = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);


        alarm.cancel(pIntent);




    }
}
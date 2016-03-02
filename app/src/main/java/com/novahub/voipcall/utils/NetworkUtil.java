package com.novahub.voipcall.utils;

/**
 * Created by samnguyen on 18/01/2016.
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Dong on 7/24/2015.
 */
public class NetworkUtil {

    private static int TYPE_WIFI = 1;
    private static int TYPE_MOBILE = 2;
    private static int TYPE_NOT_CONNECTED = 0;


    public static boolean isOnline(Context context) {
        return getConnectivityStatus(context) > 0;
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public String getConnectivityStatusString(Context context) {

        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "WIFI";
        } else if (conn == TYPE_MOBILE) {
            status = "MOBILE";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "NOT CONNECTED";
        }
        return status;
    }
}

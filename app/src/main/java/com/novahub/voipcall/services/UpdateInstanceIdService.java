package com.novahub.voipcall.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.novahub.voipcall.apiendpoint.EndPointInterface;
import com.novahub.voipcall.locationtracker.GPSTracker;
import com.novahub.voipcall.model.Location;
import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.sharepreferences.SharePreferences;
import com.novahub.voipcall.utils.Url;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by samnguyen on 01/03/2016.
 */
public class UpdateInstanceIdService extends Service {
    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    private String token;
    private String instanceId;
    // Define how the handler will process messages
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(Message message) {
            // ...
            // When needed, stop the service with
            // stopSelf();
        }
    }

    // Fires when a service is first initialized
    public void onCreate() {
        super.onCreate();
        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("MyCustomService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());
    }

    // Fires when a service is started up
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        token = SharePreferences.getData(getApplicationContext(), SharePreferences.TOKEN);
        instanceId = SharePreferences.getData(getApplicationContext(), SharePreferences.INSTANCE_ID);
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Url.BASE_URL)
                            .setLogLevel(RestAdapter.LogLevel.FULL)
                            .build();
                Response response;
                EndPointInterface apiService = restAdapter.create(EndPointInterface.class);
                Boolean success = false;
                try {
                    response = apiService.updateInstanceId(token, instanceId);
                    success = response.isSuccess();
                    if (success) {
                        SharePreferences.setDoneAction(getApplicationContext(), SharePreferences.IS_UPDATED_INSTANCE_ID);
                    }
                } catch (RetrofitError retrofitError) {

                }
                stopSelf();
            }
        });

        return START_STICKY;
    }

    // Defines the shutdown sequence
    @Override
    public void onDestroy() {
        // Cleanup service before destruction
        mHandlerThread.quit();
    }

    // Binding is another way to communicate between service and activity
    // Not needed here, local broadcasts will be used instead
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
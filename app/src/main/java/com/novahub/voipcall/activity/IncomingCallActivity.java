package com.novahub.voipcall.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.novahub.voipcall.R;
import com.novahub.voipcall.model.SamaritanNeedHelp;
import com.novahub.voipcall.utils.Asset;
import com.novahub.voipcall.utils.Data;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import android.support.v4.app.Fragment;

public class IncomingCallActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private Button buttonAccept;
    private Button buttonReject;
    private GoogleMap googleMap;
    private TextView textViewStatus;
    private TextView textViewTitle;
    private TextView textViewCount;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeComponents();
        initializeData(Data.samaritanNeedHelp);
        startCountingTime();
//        acceptCall(getApplicationContext());

    }

    private void initializeComponents() {
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("Incoming Call From Samaritan");
        textViewCount = (TextView) findViewById(R.id.textViewCount);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        buttonAccept = (Button) findViewById(R.id.buttonAccept);
        buttonAccept.setOnClickListener(this);
        buttonReject = (Button) findViewById(R.id.buttonReject);
        buttonReject.setOnClickListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeData(SamaritanNeedHelp samaritanNeedHelp) {
        textViewStatus.setText(samaritanNeedHelp.getName() + " Need Your Help !");
    }

    private void acceptCall(Context context){
        Intent intent = new Intent(context, AcceptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    private void disconnectCall(){
        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAccept:
                acceptCall(getApplicationContext());
                buttonReject.setText("End");
                buttonAccept.setEnabled(false);
                startCountingTime();
                break;
            case R.id.buttonReject:
                disconnectCall();
                buttonAccept.setEnabled(false);
                buttonReject.setEnabled(false);
                stopCountingTime();
//                startActivityRating();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(Data.samaritanNeedHelp.getLatitude(), Data.samaritanNeedHelp.getLongitude());
        this.googleMap.addMarker(new MarkerOptions().position(sydney).title("Location"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

    }

    private void startActivityRating() {
        if (Asset.distanceListRates != null) {
            Intent intent = new Intent(IncomingCallActivity.this, ShowResultsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void startCountingTime() {

        timer=new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            int hour = 0;
            int minute = 0;
            int second = 0;
            String hourText;
            String minuteText;
            String secondText;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (second > 0 && (second % 60 == 0)) {

                            minute++;
                            second = 0;
                        }
                        if (minute > 0 && (minute % 60 == 0)) {

                            hour++;
                            minute = 0;
                        }

                        if (hour < 10)
                            hourText = "0" + hour;
                        else
                            hourText = "" + hour;

                        if (minute < 10)
                            minuteText = "0" + minute;
                        else
                            minuteText = "" + minute;

                        if (second < 10)
                            secondText = "0" + second;
                        else
                            secondText = "" + second;
                        textViewCount.setText(hourText + ":" + minuteText + ":" + secondText);
                        second++;
                    }
                });
            }
        }, 1000, 1000);

    }

    private void stopCountingTime() {
        if(timer != null)
            timer.cancel();
    }

}

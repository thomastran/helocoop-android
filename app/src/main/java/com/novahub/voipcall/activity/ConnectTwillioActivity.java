package com.novahub.voipcall.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.novahub.voipcall.R;
import com.novahub.voipcall.adapter.ConnectedPeopleAdapter;
import com.novahub.voipcall.apiendpoint.EndPointInterface;
import com.novahub.voipcall.model.Distance;
import com.novahub.voipcall.model.Rate;
import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.model.WrapperRate;
import com.novahub.voipcall.sharepreferences.SharePreferences;
import com.novahub.voipcall.twilio.TwillioPhone;
import com.novahub.voipcall.utils.Asset;
import com.novahub.voipcall.utils.ShowToastUtils;
import com.novahub.voipcall.utils.Url;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class ConnectTwillioActivity extends AppCompatActivity implements TwillioPhone.LoginListener, TwillioPhone.BasicConnectionListener, TwillioPhone.BasicDeviceListener, View.OnClickListener, OnMapReadyCallback {
    private static final Handler handler = new Handler();
    private TwillioPhone twillioPhone;
    private boolean isMuted;
    private boolean isSpeaker;
    private AlertDialog incomingConferenceAlert;
    private Button buttonRed;
    private Button buttonGreen;

    private RecyclerView recyclerViewList;
    private RecyclerView.LayoutManager layoutManager;
    private ConnectedPeopleAdapter connectedPeopleAdapter;
    private List<Rate> rateList;
    private boolean isRated = false;
    private List<Distance> distanceList;
    private TextView textViewCount;
    private TextView textViewTitle;
    private Timer timer;
    private GoogleMap googleMap;
    private String nameCaller;
    private String addressCaller;
    private String descriptionCaller;
    private float latitude;
    private float longitude;
    final String ADDRESS = "Address : ";
    final String DESCRIPTION = "Description : ";
    private MediaPlayer mPlayer;

    private final int INCOMING_CALL = 0;
    private final int ACCEPTED_CALL = 1;
    private final int REJECTED_CALL = 2;
    private final int FINISHED_CALL = 3;
    private int callStatus;

    private LinearLayout linearLayoutListRate;
    private LinearLayout linearLayoutMap;
    private TextView textViewNameOfCaller;
    private TextView textViewDescription;

    private boolean isFromCaller;
    private boolean statusForTwillioConnecttion = true;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_twillio);
        intilizeTwillioPhone();
//        loginTwillioPhone();
        initializeComponents();
        getBundle();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        connectTwillio();
    }

    private void connectTwillio() {
        if (!twillioPhone.isConnected()) {
            Map<String, String> params = new HashMap<String, String>();
            final String name_room = "name_room";
            final String token = "token";
            final String is_from_caller = "is_from_caller";
            String token_local = SharePreferences.getData(getApplicationContext(), SharePreferences.TOKEN);
            params.put(name_room, Asset.nameOfConferenceRoom);
            params.put(token, token_local);
            params.put(is_from_caller, "false");
            twillioPhone.connect(params);
            twillioPhone.setSpeakerEnabled(true);
            callStatus = ACCEPTED_CALL;
        }
    }

    private void getBundle() {

        nameCaller = getIntent().getStringExtra(Asset.GCM_NAME_CALLER);
        addressCaller = getIntent().getStringExtra(Asset.GCM_ADDRESS_CALLER);
        descriptionCaller = getIntent().getStringExtra(Asset.GCM_DESCRIPTION_CALLER);
        latitude = Float.parseFloat(getIntent().getStringExtra(Asset.LATITUDE));
        longitude = Float.parseFloat(getIntent().getStringExtra(Asset.LONGITUDE));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        callStatus = INCOMING_CALL;
        startMusicRinging();
        textViewNameOfCaller.setText(nameCaller + " need your help !");
        textViewDescription.setText(ADDRESS + identifyLocation(latitude, longitude) + ", " + DESCRIPTION + descriptionCaller);
    }

    private void startMusicRinging() {
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ringtone);
        mPlayer.start();
    }

    private void stopMusicRinging() {
        mPlayer.stop();
    }

    private void countingTime() {
        final long second = 1000;
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
        }, second, second);
    }

    private void initializeComponents() {
        buttonRed = (Button) findViewById(R.id.buttonRed);
        buttonRed.setOnClickListener(this);
        buttonGreen = (Button) findViewById(R.id.buttonGreen);
        buttonGreen.setOnClickListener(this);
        textViewCount = (TextView) findViewById(R.id.textViewCount);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        recyclerViewList = (RecyclerView) findViewById(R.id.recyclerViewList);
        linearLayoutListRate = (LinearLayout) findViewById(R.id.linearLayoutListRate);
        linearLayoutMap = (LinearLayout) findViewById(R.id.linearLayoutMap);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewNameOfCaller = (TextView) findViewById(R.id.textViewNameOfCaller);
        progressDialog = new ProgressDialog(ConnectTwillioActivity.this);
        progressDialog.setMessage(getString(R.string.progress_message_connect_twillio_room));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setHasFixedSize(true);
        distanceList = new ArrayList<>();
        distanceList.addAll(Asset.listOfCallerAndSamaritans);
        connectedPeopleAdapter = new ConnectedPeopleAdapter(distanceList);
        recyclerViewList.setAdapter(connectedPeopleAdapter);

        String toBe = "are ";
        if(distanceList.size() == 1) {
            toBe = "is ";
        }

        String message = "There " + toBe + distanceList.size() + " good Samaritan(s)";
        textViewTitle.setText(message);

        rateList = new ArrayList<>();

        // Intent from here to check
        for (int i = 0; i < distanceList.size(); i++) {
            rateList.add(new Rate(null, distanceList.get(i).getToken()));
        }
        connectedPeopleAdapter.setOnItemClickListener(new ConnectedPeopleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String estimation, int position) {
                isRated = true;
                rateList.get(position).setRateStatus(estimation);
            }
        });

    }

    private void intilizeTwillioPhone() {
        twillioPhone = TwillioPhone.getInstance(ConnectTwillioActivity.this);
        twillioPhone.setListeners(this, this, this);
    }

    private void loginTwillioPhone() {
        String token = SharePreferences.getData(getApplicationContext(), SharePreferences.TOKEN);
        twillioPhone.login(token, true, true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(latitude, longitude);
        this.googleMap.addMarker(new MarkerOptions().position(sydney).title(nameCaller).snippet(descriptionCaller)).showInfoWindow();
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

    }

    private String identifyLocation(float latitude, float longitude) {
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String city = "Unknown";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && addresses.size() >= 1)
            {
                if (addresses.get(0).getLocality() != null)
                {
                    city = addresses.get(0).getLocality();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return city;
    }
    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        if (twillioPhone.handleIncomingIntent(getIntent())) {
//            showIncomingConferenceAlert();
//            addStatusMessage(R.string.got_incoming);
//            syncMainButton();
//
//        } else {
//            twillioPhone.ignoreIncomingConnection();
//
//        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (twillioPhone != null) {
            twillioPhone.setListeners(null, null, null);
            twillioPhone = null;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Toast.makeText(ConnectTwillioActivity.this, getString(R.string.toast_rate_for_samaritan), Toast.LENGTH_SHORT).show();
//        exitTwillio();

    }

    private void exitTwillio() {
        if (twillioPhone != null) {
            twillioPhone.shutDownTwillio();
            twillioPhone.setListeners(null, null, null);
            twillioPhone = null;
        }

//        int pid = android.os.Process.myPid();
//        android.os.Process.killProcess(pid);
    }

    private void addStatusMessage(final String message)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("============>", message);
            }
        });
    }

    private void addStatusMessage(int stringId)
    {
        addStatusMessage(getString(stringId));
    }

    private void syncMainButton()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (twillioPhone.isConnected()) {
                    switch (twillioPhone.getConnectionState()) {
                        case DISCONNECTED:
                            Log.d("============>", "Disconnect");
                            break;
                        case CONNECTED:
                            Log.d("============>", "Connected");
                            break;
                        default:
                            Log.d("============>", "Nothing");
                            break;
                    }
                } else if (twillioPhone.hasPendingConnection())
                    Log.d("============>", "Calling");
                else
                    Log.d("============>", "Idle");
            }
        });
    }



    private void showIncomingConferenceAlert() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (incomingConferenceAlert == null) {
                    incomingConferenceAlert = new AlertDialog.Builder(ConnectTwillioActivity.this)
                            .setTitle(R.string.incoming_call)
                            .setMessage(R.string.incoming_call_message)
                            .setPositiveButton(R.string.answer, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    twillioPhone.ignoreIncomingConnection();
//                                    Map<String, String> params = new HashMap<String, String>();
//                                    params.put(Asset.Twillio_Conference, Asset.Twillio_Room);
//                                    twillioPhone.connect(params);
//                                    incomingConferenceAlert = null;
                                    twillioPhone.acceptConnection();
                                    incomingConferenceAlert = null;
                                }
                            })
                            .setNegativeButton(R.string.ignore, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    twillioPhone.ignoreIncomingConnection();
                                    incomingConferenceAlert = null;
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    twillioPhone.ignoreIncomingConnection();
                                }
                            })
                            .create();

                    incomingConferenceAlert.show();
                }
            }
        });
    }

    private void hideIncomingAlert()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (incomingConferenceAlert != null) {
                    incomingConferenceAlert.dismiss();
                    incomingConferenceAlert = null;
                }
            }
        });
    }

    @Override
    public void onLoginStarted()
    {
        addStatusMessage("Log in with account ");
    }

    @Override
    public void onLoginFinished()
    {
        addStatusMessage(twillioPhone.canMakeOutgoing() ? R.string.outgoing_ok : R.string.no_outgoing_capability);
        addStatusMessage(twillioPhone.canAcceptIncoming() ? R.string.incoming_ok : R.string.no_incoming_capability);
        syncMainButton();
    }

    @Override
    public void onLoginError(Exception error)
    {
        if (error != null)
            addStatusMessage(String.format(getString(R.string.login_error_fmt), error.getLocalizedMessage()));
        else
            addStatusMessage(R.string.login_error_unknown);
        syncMainButton();
    }

    @Override
    public void onIncomingConnectionDisconnected()
    {
        Log.d("====>", "Pending incoming connection disconnected");
        hideIncomingAlert();
        addStatusMessage(R.string.incoming_disconnected);
        syncMainButton();
    }

    @Override
    public void onConnectionConnecting()
    {
        addStatusMessage(R.string.attempting_to_connect);
        syncMainButton();
    }

    @Override
    public void onConnectionConnected() {
        addStatusMessage(R.string.connected);
        syncMainButton();
    }

    @Override
    public void onConnectionFailedConnecting(Exception error)
    {
        if (error != null)
            addStatusMessage(String.format(getString(R.string.couldnt_establish_outgoing_fmt), error.getLocalizedMessage()));
        else
            addStatusMessage(R.string.couldnt_establish_outgoing);
    }

    @Override
    public void onConnectionDisconnecting()
    {
        addStatusMessage(R.string.disconnect_attempt);
        syncMainButton();
    }

    @Override
    public void onConnectionDisconnected()
    {
        if (timer != null)
            timer.cancel();
        addStatusMessage(R.string.disconnected);
        syncMainButton();
        handler.post(new Runnable() {
            @Override
            public void run() {
                buttonRed.setVisibility(View.GONE);
                linearLayoutMap.setVisibility(View.GONE);
                linearLayoutListRate.setVisibility(View.VISIBLE);
                callStatus = FINISHED_CALL;
                buttonGreen.setVisibility(View.VISIBLE);
                ShowToastUtils.showMessage(ConnectTwillioActivity.this, getString(R.string.toast_finish_the_twillio_call));
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
    }

    @Override
    public void onConnectionFailed(Exception error)
    {
        if (error != null)
            addStatusMessage(String.format(getString(R.string.connection_error_fmt), error.getLocalizedMessage()));
        else
            addStatusMessage(R.string.connection_error);
        syncMainButton();
    }

    @Override
    public void onDeviceStartedListening()
    {
        if (statusForTwillioConnecttion) {
            connectTwillio();
            countingTime();
            statusForTwillioConnecttion = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textViewCount.setText(getString(R.string.alert_connect_success_twillio));
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    ShowToastUtils.showMessage(ConnectTwillioActivity.this, getString(R.string.alert_connect_success_twillio));

                }
            });

        }

        addStatusMessage(R.string.device_listening);
    }

    @Override
    public void onDeviceStoppedListening(Exception error)
    {
        if (error != null)
            addStatusMessage(String.format(getString(R.string.device_listening_error_fmt), error.getLocalizedMessage()));
        else
            addStatusMessage(R.string.device_not_listening);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRed:
                switch (callStatus) {
                    case INCOMING_CALL:
                        stopMusicRinging();
                        Intent intent = new Intent(ConnectTwillioActivity.this, MakingCallConferenceActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case ACCEPTED_CALL:
                        twillioPhone.disconnect();
                        callStatus = FINISHED_CALL;
                        buttonRed.setVisibility(View.GONE);
                        buttonGreen.setText(getString(R.string.button_rate));
                        break;
                }
                break;
            case R.id.buttonGreen:
                switch (callStatus) {
                    case INCOMING_CALL:
                        progressDialog.show();
                        stopMusicRinging();
                        loginTwillioPhone();
                        buttonGreen.setText(getString(R.string.button_rate));
                        buttonGreen.setVisibility(View.GONE);
                        buttonRed.setText(getString(R.string.button_end_call));
                        break;
                    case FINISHED_CALL:
                        if (isRated) {
                            Asset.listOfGoodSamaritans = null;
                            String token = SharePreferences.getData(getApplicationContext(), SharePreferences.TOKEN);
                            for (int i = 0; i < rateList.size(); i++) {
                                if (rateList.get(i).getRateStatus() == null)
                                    rateList.remove(i);
                            }
                            Asset.wrapperRate = new WrapperRate(token, Asset.nameOfConferenceRoom, rateList);
                            RateAsyncTask rateAsyncTask = new RateAsyncTask(Asset.wrapperRate);
                            rateAsyncTask.execute();
                        } else {
                            Toast.makeText(ConnectTwillioActivity.this,
                                    getString(R.string.rate_toast), Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                break;
        }
    }

    private class RateAsyncTask extends AsyncTask<String, Boolean, Boolean> {

        private ProgressDialog progressDialog;
        private WrapperRate wrapperRate;
        public RateAsyncTask(WrapperRate wrapperRate) {
            this.wrapperRate = wrapperRate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(ConnectTwillioActivity.this);
            this.progressDialog.setMessage(getString(R.string.rating));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
            if(result) {
                Intent intent = new Intent(ConnectTwillioActivity.this, MakingCallConferenceActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ConnectTwillioActivity.this,
                        getString(R.string.make_conference_call_unsuccess), Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected Boolean doInBackground(String... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Url.BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            Response response;
            EndPointInterface apiService =
                    restAdapter.create(EndPointInterface.class);
            Boolean success = false;
            try {
                response = apiService.rate(wrapperRate);
                success = response.isSuccess();
            } catch (RetrofitError retrofitError) {

            }
            return success;
        }
    }
}

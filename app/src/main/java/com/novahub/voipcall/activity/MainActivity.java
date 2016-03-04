package com.novahub.voipcall.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.novahub.voipcall.R;
import com.novahub.voipcall.adapter.ChooseClientAdapter;
import com.novahub.voipcall.apiendpoint.EndPointInterface;
import com.novahub.voipcall.model.ClientToCall;
import com.novahub.voipcall.sharepreferences.SharePreferences;
import com.novahub.voipcall.twilio.BasicPhone;
import com.novahub.voipcall.utils.Asset;
import com.novahub.voipcall.utils.Url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;
import retrofit.RetrofitError;


public class MainActivity extends AppCompatActivity implements BasicPhone.LoginListener, BasicPhone.BasicConnectionListener, BasicPhone.BasicDeviceListener, View.OnClickListener ,CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener{

    private static final Handler handler = new Handler();

    private BasicPhone phone;

    private AlertDialog incomingConferenceAlert;

    private String currentClient;

    private RecyclerView recyclerViewList;

    private RecyclerView.LayoutManager layoutManager;

    private ChooseClientAdapter chooseClientAdapter;

    private List<ClientToCall> clientToCallList;

    private StringBuilder stringBuilder;

    private ImageView imageViewCall;

    private Toolbar toolbar;

    private LinearLayout linearLayoutMain;

    private LinearLayout linearLayoutMakeCall;

    private TextView textViewCurrentUser;

    private ImageView imageViewBack;

    private ImageView imageViewMuted;

    private ImageView imageViewSpeaker;

    private Button buttonDisconnect;

    private TextView textViewCoutTime;

    private TextView textViewConnectAlert;

    private Timer timer;

    private boolean isMuted;

    private boolean isSpeaker;

    private SharedPreferences sharedPreferences;

    private MediaPlayer mPlayer;

    private boolean isMakingCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeBasicPhone();

        initializeComponents();

        // this will prevent an Android device from going to sleep
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        checkIntentComing();

    }

    private void initializeBasicPhone() {

        phone = BasicPhone.getInstance(MainActivity.this);

        phone.setListeners(this, this, this);
    }

    private void checkIntentComing() {

        if(getIntent().getStringExtra(Asset.CURRENT_CONTACT) != null) {

            currentClient = SharePreferences.getData(getApplicationContext(), SharePreferences.TOKEN);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Asset.Current_Client, currentClient);

            editor.commit();

            phone.login(currentClient, true, true);

        } else {

            linearLayoutMain.setVisibility(View.GONE);

            linearLayoutMakeCall.setVisibility(View.VISIBLE);

            textViewCurrentUser.setText("Connecting to confference call ...");

            currentClient = sharedPreferences.getString(Asset.Current_Client, "");
        }

    }

    private void initializeComponents() {

        sharedPreferences = getSharedPreferences(Asset.VIOP_CALL, Context.MODE_PRIVATE);

        isMuted = false;

        isSpeaker = false;

        isMakingCall = false;

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        imageViewCall = (ImageView) findViewById(R.id.imageViewCall);

        imageViewCall.setOnClickListener(this);

        textViewCurrentUser = (TextView) findViewById(R.id.textViewCurrentUser);

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        imageViewBack.setOnClickListener(this);

        imageViewMuted = (ImageView) findViewById(R.id.imageViewMuted);

        imageViewMuted.setOnClickListener(this);

        imageViewSpeaker = (ImageView) findViewById(R.id.imageViewSpeaker);

        imageViewSpeaker.setOnClickListener(this);

        textViewConnectAlert = (TextView) findViewById(R.id.textViewConnectAlert);

        buttonDisconnect = (Button) findViewById(R.id.buttonDisconnect);

        buttonDisconnect.setOnClickListener(this);

        textViewCoutTime = (TextView) findViewById(R.id.textViewCoutTime);

        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);

        linearLayoutMakeCall = (LinearLayout) findViewById(R.id.linearLayoutMakeCall);

        recyclerViewList = (RecyclerView) findViewById(R.id.recyclerViewList);

        layoutManager = new LinearLayoutManager(MainActivity.this);

        recyclerViewList.setLayoutManager(layoutManager);

        recyclerViewList.setHasFixedSize(true);

        stringBuilder = new StringBuilder();

        initializeListContact();

        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ringtone);
    }

    private void initializeListContact() {

        clientToCallList = new ArrayList<>();

        for(int i = 0; i < 11; i++) {

            clientToCallList.add(new ClientToCall(false, "Contact" + i));

        }

        chooseClientAdapter = new ChooseClientAdapter(clientToCallList);

        recyclerViewList.setAdapter(chooseClientAdapter);

        chooseClientAdapter.setOnItemClickListener(new ChooseClientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (clientToCallList.get(position).isCalled()) {

                    clientToCallList.get(position).setIsCalled(false);

                } else {

                    clientToCallList.get(position).setIsCalled(true);

                }

            }
        });

    }



    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {

            case R.id.imageViewCall :
                connectToTwllio();
                break;

            case R.id.imageViewBack :
                moveBack();
                break;

            case R.id.buttonDisconnect :
                isMakingCall = false;
                phone.disconnect();
                if(timer != null)
                    timer.cancel();
                textViewConnectAlert.setText("Connecting");
                textViewCoutTime.setText("00:00:00");
                linearLayoutMakeCall.setVisibility(View.GONE);
                linearLayoutMain.setVisibility(View.VISIBLE);
                textViewCurrentUser.setText("You are in account " + currentClient);
                break;
            case R.id.imageViewMuted :
                isMuted = !isMuted;
                phone.setCallMuted(isMuted);
                break;

            case R.id.imageViewSpeaker :
                isSpeaker = !isSpeaker;
                phone.setSpeakerEnabled(isSpeaker);
                break;
        }

    }

    private void setCountDownTimerToHideAlert() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        Log.d("====>", millisUntilFinished / 1000 + "");
                    }

                    public void onFinish() {
                        if(incomingConferenceAlert != null) {
                            if(incomingConferenceAlert.isShowing()) {
                                hideIncomingAlert();
                                phone.ignoreIncomingConnection();
                                mPlayer.stop();
                                exchangeViewCallAndConnected(true);
                            }
                        }

                    }
                }.start();
            }
        });


    }

    private void setCountDownTimer() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        Log.d("====>", millisUntilFinished / 1000 + "");
                    }

                    public void onFinish() {
                        GetStatusAsyncTask getStatusAsyncTask = new GetStatusAsyncTask(stringBuilder.toString());
                        getStatusAsyncTask.execute();
                    }
                }.start();
            }
        });


    }

    private void countingTime() {

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
                        textViewCoutTime.setText(hourText + ":" + minuteText + ":" + secondText);
                        second++;
                    }
                });
            }
        }, 1000, 1000);

    }

    private void moveBack() {

        exitTwillio();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);

        startActivity(intent);

        finish();

    }

    private void connectToTwllio() {

        if (!phone.isConnected()) {
            isMakingCall = true;
            textViewConnectAlert.setText("Connecting...");
            Map<String, String> params = new HashMap<String, String>();
            params.put(Asset.Twillio_Conference, Asset.Twillio_Room);
            stringBuilder = null;
            stringBuilder = new StringBuilder();
            for(int i = 0; i < clientToCallList.size(); i++) {
                if(clientToCallList.get(i).isCalled()) {

                    stringBuilder.append(clientToCallList.get(i).getName() + " ");
                }
            }
            params.put(Asset.Twillio_People, stringBuilder.toString());
            phone.connect(params);
            linearLayoutMain.setVisibility(View.GONE);
            linearLayoutMakeCall.setVisibility(View.VISIBLE);
        }

    }

    private void exchangeViewCallAndConnected(boolean waiting) {

        if(waiting) {
            linearLayoutMain.setVisibility(View.VISIBLE);
            linearLayoutMakeCall.setVisibility(View.GONE);
        } else {
            linearLayoutMain.setVisibility(View.GONE);
            linearLayoutMakeCall.setVisibility(View.VISIBLE);
        }
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
        if (phone.handleIncomingIntent(getIntent())) {
            mPlayer.start();
            showIncomingConferenceAlert();
            addStatusMessage(R.string.got_incoming);
            syncMainButton();
            setCountDownTimerToHideAlert();

        } else {
            linearLayoutMain.setVisibility(View.VISIBLE);
            linearLayoutMakeCall.setVisibility(View.GONE);
            textViewCurrentUser.setText("You are in " + currentClient);
            phone.ignoreIncomingConnection();

        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (phone != null) {
            phone.setListeners(null, null, null);
            phone = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        exitTwillio();

    }

    private void exitTwillio() {
        if (phone != null) {
            phone.shutDownTwillio();
            phone.setListeners(null, null, null);
            phone = null;
        }

        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
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
                if (phone.isConnected()) {
                    switch (phone.getConnectionState()) {
                        case DISCONNECTED:
                            Log.d("============>", "Disconnect");
                            break;
                        case CONNECTED:
                            Log.d("============>", "Connected");
                            textViewConnectAlert.setText(R.string.connected);
                            break;
                        default:
                            Log.d("============>", "Nothing");
                            break;
                    }
                } else if (phone.hasPendingConnection())
                    Log.d("============>", "Calling");
                else
                    Log.d("============>", "Idle");
            }
        });
    }



    private void showIncomingConferenceAlert() {

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (incomingConferenceAlert == null) {
                    incomingConferenceAlert = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.incoming_call)
                            .setMessage(R.string.incoming_call_message)
                            .setPositiveButton(R.string.answer, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    phone.ignoreIncomingConnection();
                                    mPlayer.stop();
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put(Asset.Twillio_Conference, Asset.Twillio_Room);
                                    phone.connect(params);
                                    incomingConferenceAlert = null;
                                }
                            })
                            .setNegativeButton(R.string.ignore, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    phone.ignoreIncomingConnection();
                                    mPlayer.stop();
                                    incomingConferenceAlert = null;
                                    linearLayoutMakeCall.setVisibility(View.GONE);
                                    linearLayoutMain.setVisibility(View.VISIBLE);
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener()
                            {
                                @Override
                                public void onCancel(DialogInterface dialog)
                                {
                                    phone.ignoreIncomingConnection();
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
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
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
        addStatusMessage("Log in with account " + currentClient);
    }

    @Override
    public void onLoginFinished()
    {
        addStatusMessage(phone.canMakeOutgoing() ? R.string.outgoing_ok : R.string.no_outgoing_capability);
        addStatusMessage(phone.canAcceptIncoming() ? R.string.incoming_ok : R.string.no_incoming_capability);
        syncMainButton();
        textViewCurrentUser.setText("You are in account " + currentClient);
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
        countingTime();
        // Here well do count down timer
        if(isMakingCall)
            setCountDownTimer();

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
        addStatusMessage(R.string.disconnected);
        syncMainButton();
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
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
//        if (button.getId() == R.id.speaker_toggle) {
//            phone.setSpeakerEnabled(isChecked);
//        } else if (button.getId() == R.id.mute_toggle){
//            phone.setCallMuted(isChecked);
//        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

    }

    private class GetStatusAsyncTask extends AsyncTask<String, Void, Boolean> {

        private String people;

        public GetStatusAsyncTask(String people) {

            this.people = people;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Boolean isSomeOneJoined) {
            super.onPostExecute(isSomeOneJoined);

            if(isSomeOneJoined) {
                Toast.makeText(MainActivity.this, "The others has joined", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "No one has joined", Toast.LENGTH_LONG);
                buttonDisconnect.performClick();
            }

        }

        @Override
        protected Boolean doInBackground(String... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Url.BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            com.novahub.voipcall.model.Status status = null;

            EndPointInterface apiService =
                    restAdapter.create(EndPointInterface.class);
            boolean isSomeOneJoined = true;
            try {
                status = apiService.getStatusAfterCall(people);

                isSomeOneJoined = status.isStatus();

                Log.d("======>", isSomeOneJoined + "");

            } catch (RetrofitError retrofitError) {

            }

            return isSomeOneJoined;
        }
    }



}


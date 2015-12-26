package com.novahub.voipcall.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.novahub.voipcall.R;
import com.novahub.voipcall.adapter.ChooseClientAdapter;
import com.novahub.voipcall.twilio.BasicPhone;
import com.novahub.voipcall.utils.Asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements BasicPhone.LoginListener, BasicPhone.BasicConnectionListener, BasicPhone.BasicDeviceListener, View.OnClickListener ,CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener{

    private static final Handler handler = new Handler();

    private BasicPhone phone;

    private AlertDialog incomingAlert;

    private AlertDialog incomingConferenceAlert;

    private String toClient;

    private String currentClient;

    private RecyclerView recyclerViewList;

    private RecyclerView.LayoutManager layoutManager;

    private ChooseClientAdapter chooseClientAdapter;

    private List<String> listContact;

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

    private int temp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeComponents();

        if(getIntent().getStringExtra(Asset.CURRENT_CONTACT) != null) {

            currentClient = getIntent().getStringExtra(Asset.CURRENT_CONTACT);

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageViewCall = (ImageView) findViewById(R.id.imageViewCall);

        imageViewCall.setOnClickListener(this);

        textViewCurrentUser = (TextView) findViewById(R.id.textViewCurrentUser);

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        imageViewBack.setOnClickListener(this);

        toClient = "Contact1";

        imageViewMuted = (ImageView) findViewById(R.id.imageViewMuted);

        imageViewMuted.setOnClickListener(this);

        imageViewSpeaker = (ImageView) findViewById(R.id.imageViewSpeaker);

        imageViewSpeaker.setOnClickListener(this);

        textViewConnectAlert = (TextView) findViewById(R.id.textViewConnectAlert);

        buttonDisconnect = (Button) findViewById(R.id.buttonDisconnect);

        buttonDisconnect.setOnClickListener(this);

        textViewCoutTime = (TextView) findViewById(R.id.textViewCoutTime);

        phone = BasicPhone.getInstance(MainActivity.this);

        phone.setListeners(this, this, this);

        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);

        linearLayoutMakeCall = (LinearLayout) findViewById(R.id.linearLayoutMakeCall);

        recyclerViewList = (RecyclerView) findViewById(R.id.recyclerViewList);

        layoutManager = new LinearLayoutManager(MainActivity.this);

        recyclerViewList.setLayoutManager(layoutManager);

        recyclerViewList.setHasFixedSize(true);

        stringBuilder = new StringBuilder();

        initializeListContact();
    }

    private void initializeListContact() {

        listContact = new ArrayList<>();

        for(int i = 0; i < 11; i++) {

            listContact.add("Contact" + i);

        }

        chooseClientAdapter = new ChooseClientAdapter(listContact);

        recyclerViewList.setAdapter(chooseClientAdapter);

        chooseClientAdapter.setOnItemClickListener(new ChooseClientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("===============>", listContact.get(position));
                stringBuilder.append(listContact.get(position) + " ");
                Log.d("===========>", stringBuilder.toString());
            }
        });

    }



    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {

            case R.id.main_button :
                connectToTwllio();
                break;

            case R.id.imageViewCall :
                connectToTwllio();
                break;

            case R.id.imageViewBack :
                moveBack();
                break;

            case R.id.buttonDisconnect :
                phone.disconnect();
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
                        if(second > 0 && (second % 60 == 0) ) {

                            minute ++;
                            second = 0;
                        }
                        if(minute > 0 && (minute % 60 == 0)) {

                            hour ++;
                            minute = 0;
                        }

                        if(hour < 10)
                            hourText = "0" + hour;
                        else
                            hourText = "" + hour;

                        if (minute < 10)
                            minuteText = "0" + minute;
                        else
                            minuteText = "" + minute;

                        if(second < 10)
                            secondText = "0" + second;
                        else
                            secondText = "" + second;
                        textViewCoutTime.setText(hourText + ":" + minuteText + ":" +  secondText);
                        second++;
                    }
                });
            }
        }, 1000, 1000);

    }

    private void moveBack() {

        phone = null;

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);

        startActivity(intent);

        finish();
    }

    private void connectToTwllio() {

        if (!phone.isConnected()) {
            textViewConnectAlert.setText("Connecting...");
            Map<String, String> params = new HashMap<String, String>();
            params.put(Asset.Twillio_Conference, Asset.Twillio_Room);
            Log.d("===========>", stringBuilder.toString());
            params.put(Asset.Twillio_People, stringBuilder.toString());
            phone.connect(params);

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
//            showIncomingAlert();
            showIncomingConferenceAlert();
            addStatusMessage(R.string.got_incoming);
            syncMainButton();

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

    private void showIncomingAlert()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (incomingAlert == null) {
                    incomingAlert = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.incoming_call)
                            .setMessage(R.string.incoming_call_message)
                            .setPositiveButton(R.string.answer, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    phone.acceptConnection();
                                    incomingAlert = null;

                                }
                            })
                            .setNegativeButton(R.string.ignore, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    phone.ignoreIncomingConnection();
                                    incomingAlert = null;
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

                    incomingAlert.show();
                }
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

                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("Conference", "Myroom");
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
                if (incomingAlert != null) {
                    incomingAlert.dismiss();
                    incomingAlert = null;
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
//        temp++;
//        if(temp > 1) {
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    buttonDisconnect.performClick();
//                }
//            });
//        }
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
        if (button.getId() == R.id.speaker_toggle) {
            phone.setSpeakerEnabled(isChecked);
        } else if (button.getId() == R.id.mute_toggle){
            phone.setCallMuted(isChecked);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

    }
}


package com.novahub.voipcall.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.novahub.voipcall.R;
import com.novahub.voipcall.twilio.BasicPhone;

import java.util.HashMap;
import java.util.Map;

public class IncomingCallActivity extends AppCompatActivity implements BasicPhone.LoginListener, BasicPhone.BasicConnectionListener, BasicPhone.BasicDeviceListener, View.OnClickListener ,CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener{

    private static final Handler handler = new Handler();

    private BasicPhone phone;

    private ImageButton mainButton;

    private ToggleButton speakerButton;

    private ToggleButton muteButton;

    private AlertDialog incomingAlert;

    private AlertDialog incomingConferenceAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        initializeComponents();
    }

    private void initializeComponents() {

        mainButton = (ImageButton)findViewById(R.id.main_button);

        mainButton.setOnClickListener(this);

        speakerButton = (ToggleButton)findViewById(R.id.speaker_toggle);

        speakerButton.setOnCheckedChangeListener(this);

        muteButton = (ToggleButton)findViewById(R.id.mute_toggle);

        muteButton.setOnCheckedChangeListener(this);

        phone = BasicPhone.getInstance(getApplicationContext());

        phone.setListeners(this, this, this);

        if (phone.handleIncomingIntent(getIntent())) {

            showIncomingConferenceAlert();

            syncMainButton();
        }

    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.main_button) {
            phone.disconnect();
        }
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
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



    private void syncMainButton()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (phone.isConnected()) {
                    switch (phone.getConnectionState()) {
                        case DISCONNECTED:
                            mainButton.setImageResource(R.drawable.idle);
                            break;
                        case CONNECTED:
                            mainButton.setImageResource(R.drawable.inprogress);
                            break;
                        default:
                            mainButton.setImageResource(R.drawable.dialing);
                            break;
                    }
                } else if (phone.hasPendingConnection())
                    mainButton.setImageResource(R.drawable.dialing);
                else
                    mainButton.setImageResource(R.drawable.idle);
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
                    incomingAlert = new AlertDialog.Builder(IncomingCallActivity.this)
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
                                    onBackPressed();
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
                    incomingConferenceAlert = new AlertDialog.Builder(IncomingCallActivity.this)
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

    }

    @Override
    public void onLoginFinished()
    {
        syncMainButton();
    }

    @Override
    public void onLoginError(Exception error)
    {

        syncMainButton();
    }

    @Override
    public void onIncomingConnectionDisconnected()
    {
        hideIncomingAlert();
        syncMainButton();
    }

    @Override
    public void onConnectionConnecting()
    {
        syncMainButton();
    }

    @Override
    public void onConnectionConnected()
    {
        syncMainButton();
    }

    @Override
    public void onConnectionFailedConnecting(Exception error)
    {

    }

    @Override
    public void onConnectionDisconnecting()
    {
        syncMainButton();
    }

    @Override
    public void onConnectionDisconnected()
    {
        syncMainButton();
    }

    @Override
    public void onConnectionFailed(Exception error)
    {

        syncMainButton();
    }

    @Override
    public void onDeviceStartedListening()
    {
    }

    @Override
    public void onDeviceStoppedListening(Exception error)
    {

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

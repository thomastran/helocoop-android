package com.novahub.voipcall;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.novahub.voipcall.twilio.BasicPhone;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BasicPhone.LoginListener, BasicPhone.BasicConnectionListener, BasicPhone.BasicDeviceListener, View.OnClickListener ,CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener{

    private RadioGroup radioGroupSetCurrentClient;

    private RadioGroup radioGroupToClient;

    private static final Handler handler = new Handler();

    private BasicPhone phone;

    private ImageButton mainButton;

    private ToggleButton speakerButton;

    private ToggleButton muteButton;

    private AlertDialog incomingAlert;

    private String toClient;

    private String currentClient;

    private EditText logTextBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeComponents();

        initializeOnChangeRadioButton();

        setDefaultClient();
    }

    private void setDefaultClient() {

        radioGroupSetCurrentClient.check(R.id.radioButton1);
    }

    private void initializeOnChangeRadioButton() {

        radioGroupSetCurrentClient.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                switch (checkedId) {

                    case R.id.radioButton1:
                        Toast.makeText(getApplicationContext(), "choice: radioButton1",
                                Toast.LENGTH_SHORT).show();
                        currentClient = "Contact1";
                        phone.login("Contact1", true, true);

                        break;

                    case R.id.radioButton2:
                        Toast.makeText(getApplicationContext(), "choice: radioButton2",
                                Toast.LENGTH_SHORT).show();
                        currentClient = "Contact2";
                        phone.login("Contact2", true, true);

                        break;

                    case R.id.radioButton3:
                        Toast.makeText(getApplicationContext(), "choice: radioButton3",
                                Toast.LENGTH_SHORT).show();
                        currentClient = "Contact3";
                        phone.login("Contact3", true, true);
                        break;

                    case R.id.radioButton4:
                        Toast.makeText(getApplicationContext(), "choice: radioButton4",
                                Toast.LENGTH_SHORT).show();
                        currentClient = "Contact4";
                        phone.login("Contact4", true, true);
                        break;

                    case R.id.radioButton5:
                        Toast.makeText(getApplicationContext(), "choice: radioButton5",
                                Toast.LENGTH_SHORT).show();
                        currentClient = "Contact5";
                        phone.login("Contact5", true, true);
                        break;

                }

            }

        });



        radioGroupToClient.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                switch (checkedId) {

                    case R.id.radioButton1Client:
                        Toast.makeText(getApplicationContext(), "choice: Contact1",
                                Toast.LENGTH_SHORT).show();
                        toClient = "Contact1";
                        break;

                    case R.id.radioButton2Client:
                        Toast.makeText(getApplicationContext(), "choice: Contact2",
                                Toast.LENGTH_SHORT).show();
                        toClient = "Contact2";
                        break;

                    case R.id.radioButton3Client:
                        Toast.makeText(getApplicationContext(), "choice: Contact3",
                                Toast.LENGTH_SHORT).show();
                        toClient = "Contact3";
                        break;

                    case R.id.radioButton4Client:
                        Toast.makeText(getApplicationContext(), "choice: Contact4",
                                Toast.LENGTH_SHORT).show();
                        toClient = "Contact4";
                        break;

                    case R.id.radioButton5Client:
                        Toast.makeText(getApplicationContext(), "choice: Contact5",
                                Toast.LENGTH_SHORT).show();
                        toClient = "Contact5";
                        break;

                }
            }

        });
    }

    private void initializeComponents() {

        toClient = "Contact1";

        currentClient = "Contact1";

        radioGroupSetCurrentClient = (RadioGroup) findViewById(R.id.radioGroupSetCurrentClient);

        radioGroupToClient = (RadioGroup) findViewById(R.id.radioGroupToClient);

        logTextBox = (EditText)findViewById(R.id.log_text_box);

        mainButton = (ImageButton)findViewById(R.id.main_button);

        mainButton.setOnClickListener(this);

        speakerButton = (ToggleButton)findViewById(R.id.speaker_toggle);

        speakerButton.setOnCheckedChangeListener(this);

        muteButton = (ToggleButton)findViewById(R.id.mute_toggle);

        muteButton.setOnCheckedChangeListener(this);

        phone = BasicPhone.getInstance(getApplicationContext());

        phone.setListeners(this, this, this);

    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.main_button) {
            if (!phone.isConnected()) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("To", toClient);
                phone.connect(params);
            }
            else
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
    public void onResume()
    {
        super.onResume();
        if (phone.handleIncomingIntent(getIntent())) {
            showIncomingAlert();
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
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                logTextBox.append('-' + message + '\n');
            }
        });
    }

    private void addStatusMessage(int stringId)
    {
        addStatusMessage(getString(stringId));
    }

    private void syncMainButton()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
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
        logTextBox.setText("");
        addStatusMessage("Log in with account " + currentClient);
    }

    @Override
    public void onLoginFinished()
    {
        addStatusMessage(phone.canMakeOutgoing() ? R.string.outgoing_ok : R.string.no_outgoing_capability);
        addStatusMessage(phone.canAcceptIncoming() ? R.string.incoming_ok : R.string.no_incoming_capability);
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
    public void onConnectionConnected()
    {
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


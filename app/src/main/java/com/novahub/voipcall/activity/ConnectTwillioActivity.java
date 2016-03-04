package com.novahub.voipcall.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.novahub.voipcall.utils.Url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class ConnectTwillioActivity extends AppCompatActivity implements TwillioPhone.LoginListener, TwillioPhone.BasicConnectionListener, TwillioPhone.BasicDeviceListener, View.OnClickListener {
    private static final Handler handler = new Handler();
    private TwillioPhone twillioPhone;
    private boolean isMuted;
    private boolean isSpeaker;
    private AlertDialog incomingConferenceAlert;
    private Button buttonEnd;
    private Button buttonRate;

    private RecyclerView recyclerViewList;
    private RecyclerView.LayoutManager layoutManager;
    private ConnectedPeopleAdapter connectedPeopleAdapter;
    private List<Rate> rateList;
    private boolean isRated = false;
    private List<Distance> distanceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_twillio);
        intilizeTwillioPhone();
        loginTwillioPhone();
        initializeComponents();
//        connectTwillio();
    }

    private void connectTwillio() {
        if (!twillioPhone.isConnected()) {
            Map<String, String> params = new HashMap<String, String>();
            final String name_room = "name_room";
            final String token = "token";
            String token_local = SharePreferences.getData(getApplicationContext(), SharePreferences.TOKEN);
            params.put("name_room", Asset.nameOfConferenceRoom);
            params.put("token", token_local);
            params.put(Asset.Twillio_Conference, Asset.Twillio_Room);
            twillioPhone.connect(params);
            twillioPhone.setSpeakerEnabled(true);
        }
    }

    private void initializeComponents() {
        buttonEnd = (Button) findViewById(R.id.buttonEnd);
        buttonEnd.setOnClickListener(this);
        buttonRate = (Button) findViewById(R.id.buttonRate);
        buttonRate.setOnClickListener(this);

        recyclerViewList = (RecyclerView) findViewById(R.id.recyclerViewList);
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
//        textViewTitle.setText(message);

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
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (twillioPhone.handleIncomingIntent(getIntent())) {
            showIncomingConferenceAlert();
            addStatusMessage(R.string.got_incoming);
            syncMainButton();

        } else {
            twillioPhone.ignoreIncomingConnection();

        }
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
        super.onBackPressed();

        exitTwillio();

    }

    private void exitTwillio() {
        if (twillioPhone != null) {
            twillioPhone.shutDownTwillio();
            twillioPhone.setListeners(null, null, null);
            twillioPhone = null;
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
                                    twillioPhone.ignoreIncomingConnection();
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put(Asset.Twillio_Conference, Asset.Twillio_Room);
                                    twillioPhone.connect(params);
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
        connectTwillio();
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
            case R.id.buttonEnd:
                twillioPhone.disconnect();
                break;
            case R.id.buttonRate:
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
//                connectTwillio();

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

package com.novahub.voipcall.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.novahub.voipcall.R;
import com.novahub.voipcall.adapter.ConnectedPeopleAdapter;
import com.novahub.voipcall.apiendpoint.EndPointInterface;
import com.novahub.voipcall.locationtracker.GPSTracker;
import com.novahub.voipcall.model.Distance;
import com.novahub.voipcall.model.Location;
import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.services.UpdateLocationService;
import com.novahub.voipcall.sharepreferences.SharePreferences;
import com.novahub.voipcall.utils.Asset;
import com.novahub.voipcall.utils.EndCallListener;
import com.novahub.voipcall.utils.NetworkUtil;
import com.novahub.voipcall.utils.Url;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class MakingCallConferenceActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MakingCallConferenceActivity";
    private TextView textViewTitle;
    private LinearLayout linearLayoutMainButton;
    private Switch switchOnOff;
    private TextView textViewAction;
    private boolean isRegistered = true;
    private LinearLayout linearLayoutStatus;
    private static final int statusOffSamaritan = Color.parseColor("#db3126");
    private static final int statusOnSamaritan = Color.parseColor("#4cae4e");
    private List<Distance> distanceListMain;
    private LinearLayout linearLayoutShowConnectedPeople;
    private LinearLayout linearLayoutMain;
    private RecyclerView recyclerViewList;
    private RecyclerView.LayoutManager layoutManager;
    private ConnectedPeopleAdapter connectedPeopleAdapter;
    private boolean isShowingDialog;
    private TextView textViewNumberFound;
    private TextView textViewWait;
    private boolean flagSwitchDoNothing;
    private LinearLayout linearLayoutChangeInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_call_conference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initilizeComponents();
        checkSamaritan();
        if(!NetworkUtil.isOnline(getApplicationContext())) {
            showAlert("Internet Available", "Please turn on the internet");
        }
        startServiceUpdateLocation();
        checkActionsHaveDone(getApplicationContext());
        checkIntentFromIncomingCall();
    }
    private void checkIntentFromIncomingCall() {
        if (getIntent().getStringExtra(Asset.FROM_INCOMING_CALL) == null) {
            listenForEndCall();
        }
    }


    private void startServiceUpdateLocation() {
        if (NetworkUtil.isOnline(getApplicationContext()) &&
                SharePreferences.isDoneAction(getApplicationContext(), SharePreferences.TOKEN)) {
            Intent intent = new Intent(MakingCallConferenceActivity.this, UpdateLocationService.class);
            startService(intent);
        }
    }

    private void checkSamaritan() {
        flagSwitchDoNothing = true;
        if(SharePreferences.getDataBoolean(getApplicationContext(), SharePreferences.ON_SAMARITANS)) {
            setStatusSamaritan(true, getString(R.string.on), statusOnSamaritan);
        } else {
            setStatusSamaritan(false, getString(R.string.off), statusOffSamaritan);
        }
    }

    private void setStatusSamaritan(boolean isChecked, String text, int color) {
        switchOnOff.setChecked(isChecked);
        switchOnOff.setText(text);
        linearLayoutStatus.setBackgroundColor(color);
    }

    private void listenForEndCall() {
        EndCallListener callListener = new EndCallListener(MakingCallConferenceActivity.this);
        TelephonyManager mTM = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void checkActionsHaveDone(Context context) {

        boolean isRequestedCode =
                SharePreferences.isDoneAction(context, SharePreferences.IS_REQUESTED_CODE);

        boolean isActivatedCode =
                SharePreferences.isDoneAction(context, SharePreferences.IS_ACTIVATED_CODE);

        boolean isUpdatedInfo =
                SharePreferences.isDoneAction(context, SharePreferences.IS_UPDATED_INFO);

        int whatAtionsHaveDone =
                SharePreferences.checkDoneAction(isRequestedCode, isActivatedCode, isUpdatedInfo);

        switch (whatAtionsHaveDone) {
            case 1: // Just fill phone number get the code successfully
                switchOnOff.setEnabled(false);
                linearLayoutStatus.setBackgroundColor(statusOffSamaritan);
                textViewAction.setText(getString(R.string.register));
                isRegistered = false;
                break;
            case 2:
                Intent intentActivateCode = new Intent(context, ActivateActivity.class);
                intentActivateCode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentActivateCode);
                finish();
                break;
            case 3:
                Intent intentUpdateInfo = new Intent(context, GetInfoActivity.class);
                intentUpdateInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentUpdateInfo);
                finish();
                break;
            case 4:
                textViewAction.setText(getString(R.string.help));
                GPSTracker gps = new GPSTracker(MakingCallConferenceActivity.this);
                if (!gps.canGetLocation()) {
                    if(!isShowingDialog){
                        showAlert("GSP Setting", "Did you turn on Location Indentification");
                        gps.showSettingsAlert();
                    }
                }
                break;
        }
    }

    private void initilizeComponents() {
        flagSwitchDoNothing = false;
        isShowingDialog = false;
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText(getString(R.string.app_name));

        linearLayoutMainButton = (LinearLayout) findViewById(R.id.linearLayoutMainButton);
        linearLayoutMainButton.setOnClickListener(this);

        linearLayoutShowConnectedPeople = (LinearLayout) findViewById(R.id.linearLayoutShowConnectedPeople);
        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);

        linearLayoutStatus = (LinearLayout) findViewById(R.id.linearLayoutStatus);
        linearLayoutChangeInfo = (LinearLayout) findViewById(R.id.linearLayoutChangeInfo);
        linearLayoutChangeInfo.setOnClickListener(this);

        textViewNumberFound =  (TextView) findViewById(R.id.textViewNumberFound);
        textViewWait = (TextView) findViewById(R.id.textViewWait);

        switchOnOff = (Switch) findViewById(R.id.switchOnOff);
        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String token = SharePreferences.getData(getApplicationContext(),
                        SharePreferences.TOKEN);
                if (!flagSwitchDoNothing) {
                    if (NetworkUtil.isOnline(getApplicationContext())) {
                        if (isChecked) {
                            String message = getString(R.string.update_location);
                            if (SharePreferences.getDataBoolean(getApplicationContext(), SharePreferences.ON_SAMARITANS)) {
                                message = getString(R.string.update_location_without_turn_on);
                            }
                            Location location = getLocation();
                            if (location != null) {

                                TurnOnSamaritanAsyncTask turnOnSamaritanAsyncTask = new
                                        TurnOnSamaritanAsyncTask(location.getLatitude(),
                                        location.getLongtitude(), token, message);

                                turnOnSamaritanAsyncTask.execute();

                            } else {
                                flagSwitchDoNothing =true;
                                if(SharePreferences.getDataBoolean(getApplicationContext(), SharePreferences.ON_SAMARITANS)) {
                                    switchOnOff.setChecked(true);
                                } else {
                                    switchOnOff.setChecked(false);
                                }

                                isShowingDialog = true;
                                showAlert("GPS Setting", "Did you turn on Location Identification");
                                GPSTracker gps = new GPSTracker(MakingCallConferenceActivity.this);
                                gps.showSettingsAlert();
                            }
                        } else {

                            TurnOffSamaritanAsyncTask turnOffSamaritanAsyncTask = new
                                    TurnOffSamaritanAsyncTask(token);

                            turnOffSamaritanAsyncTask.execute();
                        }
                    } else {
                        Toast.makeText(MakingCallConferenceActivity.this,
                                getString(R.string.turn_on_the_internet),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    flagSwitchDoNothing = false;
                }
            }
        });

        textViewAction = (TextView) findViewById(R.id.textViewAction);
        distanceListMain = new ArrayList<>();
        recyclerViewList = (RecyclerView) findViewById(R.id.recyclerViewList);
        layoutManager = new LinearLayoutManager(MakingCallConferenceActivity.this);
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setHasFixedSize(true);

    }

    public void showAlert(String title, String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MakingCallConferenceActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        alertDialog.setCancelable(false);

        // On pressing Settings button
        alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
                Intent intent = new Intent(MakingCallConferenceActivity.this, MakingCallConferenceActivity.class);
                startActivity(intent);
                finish();
            }
        });

        alertDialog.show();
    }

    private Location getLocation() {
        GPSTracker gps = new GPSTracker(MakingCallConferenceActivity.this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            // \n is for new line
            Log.d("Lat", latitude + "");
            Log.d("Long", longitude + "");
            Location location = new Location((float)latitude, (float) longitude);
            return location;
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            switchOnOff.setChecked(true);
//            gps.showSettingsAlert();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayoutMainButton:
                if (isRegistered) {
                    if (NetworkUtil.isOnline(getApplicationContext())) {
                        makeConferenceCall();
                    } else {
                        Toast.makeText(MakingCallConferenceActivity.this,
                                getString(R.string.turn_on_the_internet), Toast.LENGTH_LONG).show();
                    }
                } else {
                    startGetPhoneNumberActivity();
                }
                break;
            case R.id.linearLayoutChangeInfo:
                Intent intent = new Intent(MakingCallConferenceActivity.this, GetInfoActivity.class);
                intent.putExtra(Asset.IS_CHANGED_INFO, true);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void makeConferenceCall() {
        String token = SharePreferences.getData(getApplicationContext(),
                SharePreferences.TOKEN);
        MakeConferenceCallAsyncTask makeConferenceCallAsyncTask = new
                MakeConferenceCallAsyncTask(token);
        makeConferenceCallAsyncTask.execute();

    }

    @SuppressLint("LongLogTag")
    private void testSharePreference() {
        Log.d(TAG, SharePreferences.getData(getApplicationContext(), SharePreferences.PHONE_NUMBER));
        Log.d(TAG, SharePreferences.getData(getApplicationContext(), SharePreferences.TOKEN));
        Log.d(TAG, SharePreferences.getData(getApplicationContext(), SharePreferences.INSTANCE_ID));
        Log.d(TAG, SharePreferences.getData(getApplicationContext(), SharePreferences.ACTIVATE_CODE));
    }

    private void startConferenceCallActivity() {
        Intent intent = new Intent(MakingCallConferenceActivity.this, ConferenceCallActivity.class);
        startActivity(intent);
        finish();
    }

    private void startGetPhoneNumberActivity() {
        Intent intent = new Intent(MakingCallConferenceActivity.this, GetPhoneNumberActivity.class);
        startActivity(intent);
        finish();
    }

    private class TurnOnSamaritanAsyncTask extends AsyncTask<String, Boolean, Boolean> {

        private ProgressDialog progressDialog;
        private float latitude;
        private float longitude;
        private String token;
        private String message;

        public TurnOnSamaritanAsyncTask(float latitude, float longitude, String token, String message) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.token = token;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(MakingCallConferenceActivity.this);
            this.progressDialog.setMessage(message);
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                SharePreferences.saveData(getApplicationContext(),
                        SharePreferences.ON_SAMARITANS, true);
//                Toast.makeText(MakingCallConferenceActivity.this,
//                        getString(R.string.update_location_success),
//                        Toast.LENGTH_LONG).show();
                switchOnOff.setText(getString(R.string.on));
                linearLayoutStatus.setBackgroundColor(statusOnSamaritan);

            } else {
                Toast.makeText(MakingCallConferenceActivity.this,
                        getString(R.string.update_location_unsuccess),
                        Toast.LENGTH_LONG).show();
                switchOnOff.setText(getString(R.string.off));
                linearLayoutStatus.setBackgroundColor(statusOffSamaritan);
            }
            if (!MakingCallConferenceActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                if(this.progressDialog != null && this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                }
            }

        }

        @Override
        protected Boolean doInBackground(String... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Url.BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            Response response;
            EndPointInterface apiService = restAdapter.create(EndPointInterface.class);
            Boolean success = false;
            try {
                response = apiService.updateLocation(this.latitude, this.longitude, this.token);
                success = response.isSuccess();
            } catch (RetrofitError retrofitError) {

            }
            return success;
        }
    }

    private class TurnOffSamaritanAsyncTask extends AsyncTask<String, Boolean, Boolean> {

        private ProgressDialog progressDialog;
        private String token;
        public TurnOffSamaritanAsyncTask(String token) {
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(MakingCallConferenceActivity.this);
            this.progressDialog.setMessage(getString(R.string.turn_off_samaritan));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                SharePreferences.saveData(getApplicationContext(),
                        SharePreferences.ON_SAMARITANS, false);
                Toast.makeText(MakingCallConferenceActivity.this,
                        getString(R.string.turn_off_samaritan_success), Toast.LENGTH_LONG).show();
                switchOnOff.setText(getString(R.string.off));
                linearLayoutStatus.setBackgroundColor(statusOffSamaritan);
            } else {
                Toast.makeText(MakingCallConferenceActivity.this,
                        getString(R.string.turn_off_samaritan_unsuccess), Toast.LENGTH_LONG).show();
                switchOnOff.setText(getString(R.string.on));
                linearLayoutStatus.setBackgroundColor(statusOnSamaritan);
            }
            if(this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
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
                response = apiService.turnOffSamaritan(this.token);
                success = response.isSuccess();

            } catch (RetrofitError retrofitError) {

            }
            return success;
        }
    }

    private class MakeConferenceCallAsyncTask extends AsyncTask<String, Boolean, Boolean> {

        private ProgressDialog progressDialog;
        private String token;
        private List<Distance> distanceList;
        public MakeConferenceCallAsyncTask(String token) {
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.distanceList = new ArrayList<>();
            this.progressDialog = new ProgressDialog(MakingCallConferenceActivity.this);
            this.progressDialog.setMessage(getString(R.string.make_conference_call));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
//                Toast.makeText(MakingCallConferenceActivity.this,
//                        getString(R.string.make_conference_call_success), Toast.LENGTH_LONG).show();
//                this.distanceList.remove(0);
                String messageDisplay = "";
                Asset.distanceList = new ArrayList<>();
                Asset.distanceList.addAll(distanceList);
                switch (this.distanceList.size()) {
                    case 0:
                        messageDisplay = getString(R.string.found_zero);
                        textViewWait.setText(messageDisplay);
                        break;
                    case 1:
                        messageDisplay = getString(R.string.found_one);
                        textViewNumberFound.setText(messageDisplay);
                        break;
                    case 2:
                        messageDisplay = getString(R.string.found_two);
                        textViewNumberFound.setText(messageDisplay);
                        break;
                }
                connectedPeopleAdapter = new ConnectedPeopleAdapter(this.distanceList);
                recyclerViewList.setAdapter(connectedPeopleAdapter);
                linearLayoutMain.setVisibility(View.GONE);
                linearLayoutShowConnectedPeople.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(MakingCallConferenceActivity.this,
                        getString(R.string.make_conference_call_unsuccess), Toast.LENGTH_LONG).show();
            }
            if(this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
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
            String nameRoom = Long.toHexString(Double.doubleToLongBits(Math.random()));
            try {
                response = apiService.makeConferenceCall(this.token, nameRoom);
                this.distanceList.addAll(response.getDistanceList());
                success = response.isSuccess();

            } catch (RetrofitError retrofitError) {

            }
            return success;
        }
    }

}

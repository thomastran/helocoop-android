package com.novahub.voipcall.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.novahub.voipcall.R;
import com.novahub.voipcall.apiendpoint.EndPointInterface;
import com.novahub.voipcall.locationtracker.GPSTracker;
import com.novahub.voipcall.model.Location;
import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.sharepreferences.SharePreferences;
import com.novahub.voipcall.utils.Url;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class MakingCallConferenceActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MakingCallConferenceActivity";
    private TextView textViewTitle;

    private LinearLayout linearLayoutMainButton;
    private Switch switchOnOff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_call_conference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initilizeComponents();
        if(SharePreferences.getDataBoolean(getApplicationContext(), SharePreferences.ON_SAMARITANS)) {
            switchOnOff.setChecked(true);
        } else {
            switchOnOff.setChecked(false);
        }
        testSharePreference();
    }

    private void initilizeComponents() {

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("Making Call");

        linearLayoutMainButton = (LinearLayout) findViewById(R.id.linearLayoutMainButton);
        linearLayoutMainButton.setOnClickListener(this);

        switchOnOff = (Switch) findViewById(R.id.switchOnOff);
        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String token = SharePreferences.getData(getApplicationContext(), SharePreferences.TOKEN);
                if (isChecked) {
                    Location location = getLocation();
                    if(location != null) {

                        TurnOnSamaritanAsyncTask turnOnSamaritanAsyncTask = new TurnOnSamaritanAsyncTask(location.getLatitude(), location.getLongtitude(), token);
                        turnOnSamaritanAsyncTask.execute();
                    }

                } else {
                    TurnOffSamaritanAsyncTask turnOffSamaritanAsyncTask = new TurnOffSamaritanAsyncTask(token);
                    turnOffSamaritanAsyncTask.execute();
                }

            }
        });
    }

    private Location getLocation() {
        GPSTracker gps = new GPSTracker(MakingCallConferenceActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            String address = "Unkown";

            String city = "Unkown";

            String state = "Unkown";

            String country = "Unkown";

            String postalCode = "Unkown";

            String knownName = "Unkown";
            // \n is for new line
            Log.d("Lat", latitude + "");
            Log.d("Long", longitude + "");

            Geocoder geocoder;

            List<Address> addresses;

            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                if (addresses != null && addresses.size() >= 1)
                {
                    if (addresses.get(0).getAddressLine(0) != null)
                    {
                        address = addresses.get(0).getAddressLine(0);
                    }

                    if (addresses.get(0).getLocality() != null)
                    {
                        city = addresses.get(0).getLocality();
                    }

                    if (addresses.get(0).getAdminArea() != null)
                    {
                        state = addresses.get(0).getAdminArea();
                    }

                    if (addresses.get(0).getCountryName() != null)
                    {
                        country = addresses.get(0).getCountryName();
                    }
                    // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    if (addresses.get(0).getPostalCode() != null)
                    {
                        postalCode = addresses.get(0).getPostalCode();
                    }

                    if (addresses.get(0).getFeatureName() != null)
                    {
                        knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

            String currentDateandTime = sdf.format(new Date());

            String addressDetail = "Adress : " + address + ", City : " + city + ", State : " + state + ", Country : " + country + ", PostalCode : " + postalCode;

            Location location = new Location((float)latitude, (float) longitude, addressDetail, currentDateandTime);

            return location;

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            switchOnOff.setChecked(false);
            gps.showSettingsAlert();
        }

        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayoutMainButton:
                startConferenceCallActivity();
                break;
        }
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

    private class TurnOnSamaritanAsyncTask extends AsyncTask<String, Boolean, Boolean> {

        private ProgressDialog progressDialog;

        private float latitude;

        private float longitude;

        private String token;

        public TurnOnSamaritanAsyncTask(float latitude, float longitude, String token) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(MakingCallConferenceActivity.this);
            this.progressDialog.setMessage(getString(R.string.request_code));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                SharePreferences.saveData(getApplicationContext(), SharePreferences.ON_SAMARITANS, true);
            } else {

                Toast.makeText(MakingCallConferenceActivity.this, getString(R.string.request_code_unsuccess), Toast.LENGTH_LONG).show();
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

            Response response = null;

            EndPointInterface apiService =
                    restAdapter.create(EndPointInterface.class);
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
            this.progressDialog.setMessage(getString(R.string.request_code));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                SharePreferences.saveData(getApplicationContext(), SharePreferences.ON_SAMARITANS, false);
            } else {

                Toast.makeText(MakingCallConferenceActivity.this, getString(R.string.request_code_unsuccess), Toast.LENGTH_LONG).show();
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

            Response response = null;

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

}

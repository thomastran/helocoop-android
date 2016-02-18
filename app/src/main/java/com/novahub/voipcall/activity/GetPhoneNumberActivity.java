package com.novahub.voipcall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.novahub.voipcall.R;
import com.novahub.voipcall.apiendpoint.EndPointInterface;
import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.services.RegistrationIntentService;
import com.novahub.voipcall.sharepreferences.SharePreferences;
import com.novahub.voipcall.utils.NetworkUtil;
import com.novahub.voipcall.utils.StringUtils;
import com.novahub.voipcall.utils.Url;

import io.fabric.sdk.android.Fabric;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class GetPhoneNumberActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextPhoneNumber;
    private Button buttonRegister;
    private String phoneNumber;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_get_phone_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initilizeComponents();
        requestGcmInstanceId();
        checkActionsHaveDone(getApplicationContext());
    }
    private void requestGcmInstanceId() {
        if(SharePreferences.getData(getApplicationContext(), SharePreferences.INSTANCE_ID) == SharePreferences.EMPTY) {
            if (NetworkUtil.isOnline(getApplicationContext())) {
                if (checkPlayServices()) {
                    // Start IntentService to register this application with GCM.
                    Intent intent = new Intent(this, RegistrationIntentService.class);
                    startService(intent);
                }
            } else {
                Toast.makeText(GetPhoneNumberActivity.this, getString(R.string.turn_on_the_internet),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }



    private void initilizeComponents() {
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);
    }

    private void checkActionsHaveDone(Context context) {

        boolean isRequestedCode = SharePreferences.isDoneAction(context, SharePreferences.IS_REQUESTED_CODE);
        boolean isActivatedCode = SharePreferences.isDoneAction(context, SharePreferences.IS_ACTIVATED_CODE);
        boolean isUpdatedInfo = SharePreferences.isDoneAction(context, SharePreferences.IS_UPDATED_INFO);

        int whatAtionsHaveDone = SharePreferences.checkDoneAction(isRequestedCode, isActivatedCode, isUpdatedInfo);

        switch (whatAtionsHaveDone) {
            case 1: // Just fill phone number get the code successfully
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
                Intent intentMakingCallConference = new Intent(context, MakingCallConferenceActivity.class);
                intentMakingCallConference.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentMakingCallConference);
                finish();
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRegister:
                doRequest();
                break;
        }
    }
    private void doRequest() {
        phoneNumber = editTextPhoneNumber.getText().toString();
        if(!StringUtils.isEmpty(phoneNumber)) {
            if(NetworkUtil.isOnline(getApplicationContext())) {
                requestCode(phoneNumber);
            } else {
                Toast.makeText(GetPhoneNumberActivity.this, getString(R.string.turn_on_the_internet), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(GetPhoneNumberActivity.this, getString(R.string.fill_alert), Toast.LENGTH_LONG).show();
        }

    }

    private void requestCode(String phoneNumber) {
        RequestCodeAsyncTask requestCodeAsyncTask = new RequestCodeAsyncTask(phoneNumber);
        requestCodeAsyncTask.execute();
    }

    private void startActivateActivity() {
        Intent intent = new Intent(GetPhoneNumberActivity.this, ActivateActivity.class);
        startActivity(intent);
        finish();
    }

    private class RequestCodeAsyncTask extends AsyncTask<String, Boolean, Boolean> {
        private ProgressDialog progressDialog;
        private String phoneNumber;
        public RequestCodeAsyncTask(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(GetPhoneNumberActivity.this);
            this.progressDialog.setMessage(getString(R.string.request_code));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                SharePreferences.setDoneAction(GetPhoneNumberActivity.this, SharePreferences.IS_REQUESTED_CODE);
                SharePreferences.saveData(GetPhoneNumberActivity.this, SharePreferences.PHONE_NUMBER, phoneNumber);
                Toast.makeText(GetPhoneNumberActivity.this, getString(R.string.request_code_success), Toast.LENGTH_LONG).show();
                startActivateActivity();
            } else {
                if(this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                }
                Toast.makeText(GetPhoneNumberActivity.this, getString(R.string.request_code_unsuccess), Toast.LENGTH_LONG).show();
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
                response = apiService.requestActivateCode(phoneNumber);
                success = response.isSuccess();
            } catch (RetrofitError retrofitError) {

            }
            return success;
        }
    }


}

package com.novahub.voipcall.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.novahub.voipcall.R;
import com.novahub.voipcall.apiendpoint.EndPointInterface;
import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.services.QuickstartPreferences;
import com.novahub.voipcall.services.RegistrationIntentService;
import com.novahub.voipcall.sharepreferences.SharePreferences;
import com.novahub.voipcall.utils.GCMUtils;
import com.novahub.voipcall.utils.NetworkUtil;
import com.novahub.voipcall.utils.StringUtils;
import com.novahub.voipcall.utils.Url;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class ActivateActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewTitle;
    private EditText editTextActivateCode;
    private Button buttonActivate;
    private String activateCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeComponents();
        //After get the activate code from server, this will help us to get InstanceId To send GCM
        GCMUtils.checkGCMInstanceId(getApplicationContext(), ActivateActivity.this);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(editTextActivateCode.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);    }
    private void initializeComponents() {
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText(getString(R.string.activate));
        editTextActivateCode = (EditText)  findViewById(R.id.editTextActivateCode);
        buttonActivate = (Button) findViewById(R.id.buttonActivate);
        buttonActivate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonActivate:
                doRequest();
                break;
        }
    }

    private void doRequest()  {
        activateCode = editTextActivateCode.getText().toString();
        if(!StringUtils.isEmpty(activateCode)) {
            if (NetworkUtil.isOnline(getApplicationContext())) {
                verifyCode(activateCode);
            } else {
                Toast.makeText(ActivateActivity.this, getString(R.string.turn_on_the_internet),
                        Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(ActivateActivity.this, getString(R.string.fill_alert),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void verifyCode(String activateCode) {
        String phoneNumber = SharePreferences.getData(ActivateActivity.this, SharePreferences.PHONE_NUMBER);
        String instanceId = SharePreferences.getData(ActivateActivity.this, SharePreferences.INSTANCE_ID);

        ActivateCodeAsyncTask activateCodeAsyncTask =
                new ActivateCodeAsyncTask(activateCode, phoneNumber, instanceId);

        activateCodeAsyncTask.execute();
    }

    private void startGetInfoActivity() {
        Intent intent = new Intent(ActivateActivity.this, GetInfoActivity.class);
        startActivity(intent);
        finish();
    }

    private class ActivateCodeAsyncTask extends AsyncTask<String, Boolean, Boolean> {

        private ProgressDialog progressDialog;
        private String activeCode;
        private String phoneNumber;
        private String instanceId;

        public ActivateCodeAsyncTask(String activeCode, String phoneNumber, String instanceId) {
            this.activeCode = activeCode;
            this.phoneNumber = phoneNumber;
            this.instanceId = instanceId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(ActivateActivity.this);
            this.progressDialog.setMessage(getString(R.string.activate_code));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                SharePreferences.setDoneAction(ActivateActivity.this, SharePreferences.IS_ACTIVATED_CODE);
                Toast.makeText(ActivateActivity.this, getString(R.string.activate_code_success), Toast.LENGTH_LONG).show();
                startGetInfoActivity();
            } else {
                if(this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                }
                Toast.makeText(ActivateActivity.this, getString(R.string.activate_code_success), Toast.LENGTH_LONG).show();
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
                response = apiService.verifyActivateCode(this.phoneNumber, this.activeCode, this.instanceId);
                success = response.isSuccess();

            } catch (RetrofitError retrofitError) {

            }
            return success;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}


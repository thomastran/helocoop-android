package com.novahub.voipcall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.novahub.voipcall.R;
import com.novahub.voipcall.apiendpoint.EndPointInterface;
import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.model.Token;
import com.novahub.voipcall.utils.StringUtils;

import io.fabric.sdk.android.Fabric;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class GetPhoneNumberActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextPhoneNumber;
    private Button buttonRegister;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_get_phone_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initilizeComponents();
    }

    private void initilizeComponents() {

        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRegister:
                startActivateActivity();
                break;
        }
    }

    private void registerNewPhoneNumber(String phoneNumber) {

        RegisterNewPhoneNumberAsyncTask registerNewPhoneNumberAsyncTask = new RegisterNewPhoneNumberAsyncTask(phoneNumber);
        registerNewPhoneNumberAsyncTask.execute();
    }

    private void startActivateActivity() {

        Intent intent = new Intent(GetPhoneNumberActivity.this, ActivateActivity.class);

        startActivity(intent);

        finish();
    }

    private class RegisterNewPhoneNumberAsyncTask extends AsyncTask<String, Boolean, Boolean> {

        private ProgressDialog progressDialog;

        private String phoneNumber;

        public RegisterNewPhoneNumberAsyncTask(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(GetPhoneNumberActivity.this);
            this.progressDialog.setMessage("You are log in with user name ");
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if( this.progressDialog.isShowing() ) {
                this.progressDialog.dismiss();
            }

            if(result) {
                Toast.makeText(GetPhoneNumberActivity.this, "Register successfully !", Toast.LENGTH_LONG).show();
                startActivateActivity();

            } else {
                Toast.makeText(GetPhoneNumberActivity.this, "Register unsuccessfully !", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected Boolean doInBackground(String... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(params[0])
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            Response response = null;

            EndPointInterface apiService =
                    restAdapter.create(EndPointInterface.class);
            Boolean success = false;
            try {
                response = apiService.registerNewPhoneNumber(phoneNumber);
                success = response.isSuccess();

            } catch (RetrofitError retrofitError) {

            }

            return success;
        }
    }


}

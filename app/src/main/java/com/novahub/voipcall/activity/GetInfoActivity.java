package com.novahub.voipcall.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.novahub.voipcall.R;
import com.novahub.voipcall.apiendpoint.EndPointInterface;
import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.sharepreferences.SharePreferences;
import com.novahub.voipcall.utils.NetworkUtil;
import com.novahub.voipcall.utils.StringUtils;
import com.novahub.voipcall.utils.Url;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class GetInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewTitle;
    private EditText editTextEmail;
    private EditText editTextName;
    private EditText editTextAddress;
    private EditText editTextDescription;
    private Button buttonUpdateInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeComponents();
    }

    private void initializeComponents() {
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText(getString(R.string.confirm_info));
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        buttonUpdateInfo = (Button) findViewById(R.id.buttonUpdateInfo);
        buttonUpdateInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonUpdateInfo:
                doRequest();
                break;
        }
    }

    private void doRequest() {
        String emailUser = editTextEmail.getText().toString();
        String nameUser = editTextName.getText().toString();
        String homeCity = editTextAddress.getText().toString();
        String description = editTextDescription.getText().toString();
        String phoneNumber = SharePreferences.getData(GetInfoActivity.this, SharePreferences.PHONE_NUMBER);
        if(!StringUtils.isEmpty(emailUser) &&
                !StringUtils.isEmpty(nameUser) &&
                !StringUtils.isEmpty(homeCity) &&
                !StringUtils.isEmpty(description)) {

            if (NetworkUtil.isOnline(getApplicationContext())) {

                UpdateInfoAsyncTask updateInfoAsyncTask = new UpdateInfoAsyncTask(emailUser,
                        phoneNumber, homeCity, nameUser, description);

                updateInfoAsyncTask.execute();

            } else {
                Toast.makeText(GetInfoActivity.this, getString(R.string.turn_on_the_internet), Toast.LENGTH_LONG).show();
            }


        } else {
            Toast.makeText(GetInfoActivity.this, getString(R.string.fill_alert), Toast.LENGTH_LONG).show();
        }
    }

    private void startMakingCallActivity() {
        Intent intent = new Intent(GetInfoActivity.this, MakingCallConferenceActivity.class);
        startActivity(intent);
        finish();
    }

    private class UpdateInfoAsyncTask extends AsyncTask<String, Response, Response> {

        private ProgressDialog progressDialog;
        private String email;
        private String phoneNumber;
        private String address;
        private String name;
        private String description;

        public UpdateInfoAsyncTask(String email, String phoneNumber,
                                   String address, String name, String description) {

            this.email = email;
            this.phoneNumber = phoneNumber;
            this.address = address;
            this.name = name;
            this.description = description;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(GetInfoActivity.this);
            this.progressDialog.setMessage(getString(R.string.request_code));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Response result) {
            super.onPostExecute(result);
            if(result.isSuccess()) {
                SharePreferences.setDoneAction(GetInfoActivity.this, SharePreferences.IS_UPDATED_INFO);
                SharePreferences.saveData(GetInfoActivity.this, SharePreferences.TOKEN, result.getToken());
                Toast.makeText(GetInfoActivity.this, getString(R.string.update_success), Toast.LENGTH_LONG).show();
                startMakingCallActivity();
            } else {
                if(this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                }
                Toast.makeText(GetInfoActivity.this, getString(R.string.update_unsuccess), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Response doInBackground(String... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Url.BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            Response response = null;
            EndPointInterface apiService =
                    restAdapter.create(EndPointInterface.class);
            try {
                response = apiService.updateInfo(this.phoneNumber, this.email, this.address, this.name, this.description);

            } catch (RetrofitError retrofitError) {

            }
            return response;
        }
    }
}

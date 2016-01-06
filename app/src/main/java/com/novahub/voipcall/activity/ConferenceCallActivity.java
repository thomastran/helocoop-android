package com.novahub.voipcall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.novahub.voipcall.R;

public class ConferenceCallActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewTitle;

    private Button buttonDisconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_call);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeComponents();

    }

    private void initializeComponents() {
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("You are in conference call");

        buttonDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        buttonDisconnect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDisconnect:
                startRatingActivity();
                break;
        }
    }

    private void startRatingActivity() {

        Intent intent = new Intent(ConferenceCallActivity.this, RatingActivity.class);

        startActivity(intent);

        finish();
    }
}

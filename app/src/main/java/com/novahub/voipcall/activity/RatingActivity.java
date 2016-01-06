package com.novahub.voipcall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.novahub.voipcall.R;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewTitle;

    private Button buttonSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeComponents();
    }

    private void initializeComponents() {

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("RATE YOUR EXPERIENCE");

        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSubmit:
                startConferenceCallActivity();
                break;
        }
    }


    private void startConferenceCallActivity() {

        Intent intent = new Intent(RatingActivity.this, MakingCallConferenceActivity.class);

        startActivity(intent);

        finish();
    }
}

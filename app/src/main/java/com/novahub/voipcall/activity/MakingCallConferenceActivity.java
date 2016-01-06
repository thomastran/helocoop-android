package com.novahub.voipcall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novahub.voipcall.R;

public class MakingCallConferenceActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView textViewTitle;

    private LinearLayout linearLayoutMainButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_call_conference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initilizeComponents();
    }

    private void initilizeComponents() {

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("Making Call");

        linearLayoutMainButton = (LinearLayout) findViewById(R.id.linearLayoutMainButton);
        linearLayoutMainButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayoutMainButton:
                startConferenceCallActivity();
                break;
        }
    }

    private void startConferenceCallActivity() {

        Intent intent = new Intent(MakingCallConferenceActivity.this, ConferenceCallActivity.class);

        startActivity(intent);

        finish();
    }
}

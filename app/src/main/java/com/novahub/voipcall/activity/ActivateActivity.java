package com.novahub.voipcall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.novahub.voipcall.R;

public class ActivateActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewTitle;

    private EditText editTextActivateCode;

    private Button buttonActivate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeComponents();
    }
    private void initializeComponents() {

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("Activate");

        editTextActivateCode = (EditText)  findViewById(R.id.editTextActivateCode);

        buttonActivate = (Button) findViewById(R.id.buttonActivate);
        buttonActivate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonActivate:
                startGetInfoActivity();
                break;
        }
    }

    private void startGetInfoActivity() {

        Intent intent = new Intent(ActivateActivity.this, GetInfoActivity.class);

        startActivity(intent);

        finish();
    }
}

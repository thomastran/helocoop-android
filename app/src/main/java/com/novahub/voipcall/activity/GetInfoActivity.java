package com.novahub.voipcall.activity;

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

public class GetInfoActivity extends AppCompatActivity {

    private TextView textViewTitle;

    private EditText editTextEmail;

    private EditText editTextName;

    private EditText editTextAddress;

    private Button buttonRegister;
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

        textViewTitle.setText("Confirm Information");

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        editTextName = (EditText) findViewById(R.id.editTextName);

        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
    }

}

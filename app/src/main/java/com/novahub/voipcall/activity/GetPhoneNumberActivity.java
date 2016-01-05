package com.novahub.voipcall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.novahub.voipcall.R;

public class GetPhoneNumberActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextPhoneNumber;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void startActivateActivity() {

        Intent intent = new Intent(GetPhoneNumberActivity.this, ActivateActivity.class);

        startActivity(intent);

        finish();
    }
}

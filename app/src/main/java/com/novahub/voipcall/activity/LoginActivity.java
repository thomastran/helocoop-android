package com.novahub.voipcall.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.novahub.voipcall.R;
import com.novahub.voipcall.utils.Asset;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private RadioGroup radioGroupSetCurrentClient;

    private String currentClient;

    private EditText editText;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeComponents();
        initializeOnChangeRadioButton();
    }

    private void initializeComponents() {
        radioGroupSetCurrentClient = (RadioGroup) findViewById(R.id.radioGroupSetCurrentClient);

        editText = (EditText) findViewById(R.id.editText);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(this);

    }
    // After choose the name for account !
    private void startMainActivity(String currentClient) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Asset.CURRENT_CONTACT, currentClient);
        startActivity(intent);
        finish();
    }

    private void initializeOnChangeRadioButton() {

        radioGroupSetCurrentClient.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                switch (checkedId) {

                    case R.id.radioButton1:
                        Toast.makeText(getApplicationContext(), "You are in : Contact 1",
                                Toast.LENGTH_SHORT).show();
                        currentClient = "Contact1";
                        break;

                    case R.id.radioButton2:
                        Toast.makeText(getApplicationContext(), "You are in : Contact 2",
                                Toast.LENGTH_SHORT).show();
                        currentClient = "Contact2";
                        break;

                    case R.id.radioButton3:
                        Toast.makeText(getApplicationContext(), "You are in : Contact 3",
                                Toast.LENGTH_SHORT).show();
                        currentClient = "Contact3";
                        break;

                    case R.id.radioButton4:
                        Toast.makeText(getApplicationContext(), "You are in : Contact 4",
                                Toast.LENGTH_SHORT).show();
                        currentClient = "Contact4";
                        break;

                    case R.id.radioButton5:
                        Toast.makeText(getApplicationContext(), "You are in : Contact 5",
                                Toast.LENGTH_SHORT).show();
                        currentClient = "Contact5";
                        break;

                }

                startMainActivity(currentClient);

            }

        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button :
                currentClient = editText.getText().toString();

                if(currentClient == null || currentClient.equals("")) {

                    Toast.makeText(getApplicationContext(), "Please fill the user name", Toast.LENGTH_LONG);
                } else {
                    startMainActivity(currentClient);
                }
                break;
        }
    }
}

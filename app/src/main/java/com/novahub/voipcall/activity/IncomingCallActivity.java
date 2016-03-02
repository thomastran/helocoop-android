package com.novahub.voipcall.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.novahub.voipcall.R;
import com.novahub.voipcall.twilio.BasicPhone;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IncomingCallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ringtone);
        mPlayer.start();

    }

    private void music() {

//        try {
//            mPlayer.prepare();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


}

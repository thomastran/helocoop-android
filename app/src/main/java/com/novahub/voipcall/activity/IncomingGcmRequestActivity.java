package com.novahub.voipcall.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.novahub.voipcall.R;
import com.novahub.voipcall.utils.Asset;

public class IncomingGcmRequestActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {
    private TextView textViewCallerName;
    private TextView textViewAddress;
    private TextView textViewDescription;
    private TextView textViewTitle;
    private GoogleMap googleMap;
    private String nameCaller;
    private String addressCaller;
    private String descriptionCaller;
    private float latitude;
    private float longitude;
    final String ADDRESS = "Address : ";
    final String DESCRIPTION = "Description : ";
    private Button buttonAccept;
    private Button buttonReject;
    private MediaPlayer mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_gcm_request);
        initializeComponents();
        getBundle();
    }
    private void getBundle() {

        nameCaller = getIntent().getStringExtra(Asset.GCM_NAME_CALLER);
        addressCaller = getIntent().getStringExtra(Asset.GCM_ADDRESS_CALLER);
        descriptionCaller = getIntent().getStringExtra(Asset.GCM_DESCRIPTION_CALLER);
        latitude = Float.parseFloat(getIntent().getStringExtra(Asset.LATITUDE));
        longitude = Float.parseFloat(getIntent().getStringExtra(Asset.LONGITUDE));

        textViewTitle.setText(nameCaller + " need help");
        textViewCallerName.setText(nameCaller);
        textViewAddress.setText(ADDRESS + addressCaller);
        textViewDescription.setText(DESCRIPTION + descriptionCaller);
        buttonAccept = (Button) findViewById(R.id.buttonAccept);
        buttonAccept.setOnClickListener(this);
        buttonReject = (Button) findViewById(R.id.buttonReject);
        buttonReject.setOnClickListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private void initializeComponents() {
        textViewCallerName = (TextView) findViewById(R.id.textViewCallerName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("Samaritan need help");
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ringtone);
        mPlayer.start();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(IncomingGcmRequestActivity.this, MakingCallConferenceActivity.class);
//        startActivity(intent);
//        finish();
        Log.d("===>", "Do nothing");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAccept:
                mPlayer.stop();
                Intent intent = new Intent(IncomingGcmRequestActivity.this, ConnectTwillioActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.buttonReject:
                mPlayer.stop();
                Intent intentMakingCallConferenceActivity = new Intent(IncomingGcmRequestActivity.this, MakingCallConferenceActivity.class);
                startActivity(intentMakingCallConferenceActivity);
                finish();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(latitude, longitude);
        this.googleMap.addMarker(new MarkerOptions().position(sydney).title("Location"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

    }
}

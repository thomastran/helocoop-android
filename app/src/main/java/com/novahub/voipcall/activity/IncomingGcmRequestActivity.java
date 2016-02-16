package com.novahub.voipcall.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
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
import com.novahub.voipcall.utils.EndCallListener;

public class IncomingGcmRequestActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {
    private TextView textViewCallerName;
    private TextView textViewAddress;
    private TextView textViewDescription;
    private LinearLayout linearLayoutBack;
    private TextView textViewTitle;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_gcm_request);
        initializeComponents();
        getBundle();
    }
    private void getBundle() {
        final String ADDRESS = "Address : ";
        final String DESCRIPTION = "Description : ";
        boolean isFromServer = getIntent().getBooleanExtra(Asset.IS_FROM_SERVER, true);
        if (isFromServer) {
            String nameCaller = getIntent().getStringExtra(Asset.GCM_NAME_CALLER);
            String addressCaller = getIntent().getStringExtra(Asset.GCM_ADDRESS_CALLER);
            String descriptionCaller = getIntent().getStringExtra(Asset.GCM_DESCRIPTION_CALLER);
            String latitude = getIntent().getStringExtra(Asset.LATITUDE);
            String longitude = getIntent().getStringExtra(Asset.LONGITUDE);
            String gcm_users = getIntent().getStringExtra(Asset.GCM_USERS);
            textViewTitle.setText(nameCaller + " need help");
            Asset.nameOfCaller = nameCaller;
            Asset.addressOfCaller = addressCaller;
            Asset.descriptionOfCaller = descriptionCaller;
            Asset.latitude = Float.parseFloat(latitude);
            Asset.longitude =  Float.parseFloat(longitude);
            textViewCallerName.setText(nameCaller);
            textViewAddress.setText(ADDRESS + addressCaller);
            textViewDescription.setText(DESCRIPTION + descriptionCaller);
            listenForEndCall();
        } else {
            textViewTitle.setText(Asset.nameOfCaller + " need your help");
            textViewCallerName.setText(Asset.nameOfCaller);
            textViewAddress.setText(ADDRESS + Asset.addressOfCaller);
            textViewDescription.setText(DESCRIPTION + Asset.descriptionOfCaller);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private void initializeComponents() {
        textViewCallerName = (TextView) findViewById(R.id.textViewCallerName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        linearLayoutBack = (LinearLayout) findViewById(R.id.linearLayoutBack);
        linearLayoutBack.setOnClickListener(this);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("Samaritan need help");

    }
    private void listenForEndCall() {
        EndCallListener callListener = new EndCallListener(IncomingGcmRequestActivity.this, true);
        TelephonyManager mTM = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(IncomingGcmRequestActivity.this, MakingCallConferenceActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayoutBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(Asset.latitude, Asset.longitude);
        this.googleMap.addMarker(new MarkerOptions().position(sydney).title("Location"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

    }
}

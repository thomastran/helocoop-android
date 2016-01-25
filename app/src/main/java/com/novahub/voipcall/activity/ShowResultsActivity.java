package com.novahub.voipcall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novahub.voipcall.R;
import com.novahub.voipcall.adapter.ConnectedPeopleAdapter;
import com.novahub.voipcall.utils.Asset;

public class ShowResultsActivity extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView recyclerViewList;
    private RecyclerView.LayoutManager layoutManager;
    private ConnectedPeopleAdapter connectedPeopleAdapter;
    private LinearLayout linearLayoutBack;
    private TextView textViewTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayoutBack = (LinearLayout) findViewById(R.id.linearLayoutBack);
        linearLayoutBack.setOnClickListener(this);
        recyclerViewList = (RecyclerView) findViewById(R.id.recyclerViewList);
        layoutManager = new LinearLayoutManager(ShowResultsActivity.this);
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setHasFixedSize(true);
        connectedPeopleAdapter = new ConnectedPeopleAdapter(Asset.distanceList);
        recyclerViewList.setAdapter(connectedPeopleAdapter);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        String toBe = "are ";
        if(Asset.distanceList.size() == 1) {
            toBe = "is ";
        }
        String message = "There " + toBe + Asset.distanceList.size() + " good Samaritan(s)";
        textViewTitle.setText(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayoutBack:
                Asset.distanceList = null;
                Intent intent = new Intent(ShowResultsActivity.this, MakingCallConferenceActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}

package com.novahub.voipcall.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.novahub.voipcall.R;
import com.novahub.voipcall.adapter.ChooseClientToLoginAdapter;
import com.novahub.voipcall.utils.Asset;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private String currentClient;

    private RecyclerView recyclerViewList;

    private RecyclerView.LayoutManager layoutManager;

    private List<String> listContact;

    private ChooseClientToLoginAdapter chooseClientToLoginAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeComponents();
    }

    private void initializeComponents() {

        recyclerViewList = (RecyclerView) findViewById(R.id.recyclerViewList);

        layoutManager = new LinearLayoutManager(LoginActivity.this);

        recyclerViewList.setLayoutManager(layoutManager);

        recyclerViewList.setHasFixedSize(true);

        listContact = new ArrayList<>();

        for(int i = 0; i < 11; i++) {

            listContact.add("Contact" + i);

        }

        chooseClientToLoginAdapter = new ChooseClientToLoginAdapter(listContact);

        recyclerViewList.setAdapter(chooseClientToLoginAdapter);

        chooseClientToLoginAdapter.setOnItemClickListener(new ChooseClientToLoginAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("===============>", listContact.get(position));
                startMainActivity(listContact.get(position));
            }
        });




    }
    // After choose the name for account !
    private void startMainActivity(String currentClient) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Asset.CURRENT_CONTACT, currentClient);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
    }
}


package com.novahub.voipcall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novahub.voipcall.R;
import com.novahub.voipcall.adapter.ConnectedPeopleAdapter;
import com.novahub.voipcall.apiendpoint.EndPointInterface;
import com.novahub.voipcall.model.Distance;
import com.novahub.voipcall.model.Rate;
import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.model.WrapperRate;
import com.novahub.voipcall.sharepreferences.SharePreferences;
import com.novahub.voipcall.utils.Asset;
import com.novahub.voipcall.utils.FlagHelpCoop;
import com.novahub.voipcall.utils.TempDataUtils;
import com.novahub.voipcall.utils.Url;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class MadeSuccessCallActivity extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView recyclerViewList;
    private RecyclerView.LayoutManager layoutManager;
    private ConnectedPeopleAdapter connectedPeopleAdapter;
//    private LinearLayout linearLayoutBack;
    private TextView textViewTitle;
    private Button buttonRate;
    private List<Rate> rateList;
    private boolean isRated = false;
    private List<Distance> distanceList;
    private TextView textViewCount;
    private Timer timer;
    private Button buttonEndCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_made_success_call);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewCount = (TextView) findViewById(R.id.textViewCount);
        recyclerViewList = (RecyclerView) findViewById(R.id.recyclerViewList);
        layoutManager = new LinearLayoutManager(MadeSuccessCallActivity.this);
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setHasFixedSize(true);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        buttonRate = (Button) findViewById(R.id.buttonRate);
        buttonRate.setOnClickListener(this);
        buttonEndCall = (Button) findViewById(R.id.buttonEndCall);
        buttonEndCall.setOnClickListener(this);
        distanceList = new ArrayList<>();
        if (Asset.distanceList != null) {
            distanceList.addAll(Asset.distanceList);
        }
        TempDataUtils.resetData();
        connectedPeopleAdapter = new ConnectedPeopleAdapter(distanceList);
        recyclerViewList.setAdapter(connectedPeopleAdapter);
        String toBe = "are ";
        if(distanceList.size() == 1) {
            toBe = "is ";
        }

        String message = "There " + toBe + distanceList.size() + " good Samaritan(s)";
        textViewTitle.setText(message);

        rateList = new ArrayList<>();

        // Intent from here to check
        for (int i = 0; i < distanceList.size(); i++) {
            rateList.add(new Rate(null, distanceList.get(i).getToken()));
        }
        connectedPeopleAdapter.setOnItemClickListener(new ConnectedPeopleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String estimation, int position) {
                isRated = true;
                rateList.get(position).setRateStatus(estimation);
            }
        });
        acceptCall(getApplicationContext());
        startCountingTime();
    }

    private void acceptCall(Context context){
        Intent intent = new Intent(context, AcceptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonEndCall:
                disconnectCall();
                stopCountingTime();
                break;
            case R.id.buttonRate:
                if (isRated) {
                    Asset.distanceList = null;
                    String token = SharePreferences.getData(MadeSuccessCallActivity.this, SharePreferences.TOKEN);
                    for (int i = 0; i < rateList.size(); i++) {
                        if(rateList.get(i).getRateStatus() == null)
                            rateList.remove(i);
                    }
                    Asset.wrapperRate = new WrapperRate(token, Asset.nameRoom, rateList);
                    RateAsyncTask rateAsyncTask = new RateAsyncTask(Asset.wrapperRate);
                    rateAsyncTask.execute();
                } else {
                    Toast.makeText(MadeSuccessCallActivity.this,
                            getString(R.string.rate_toast), Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private class RateAsyncTask extends AsyncTask<String, Boolean, Boolean> {

        private ProgressDialog progressDialog;
        private WrapperRate wrapperRate;
        public RateAsyncTask(WrapperRate wrapperRate) {
            this.wrapperRate = wrapperRate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(MadeSuccessCallActivity.this);
            this.progressDialog.setMessage(getString(R.string.rating));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
            if(result) {
                Intent intent = new Intent(MadeSuccessCallActivity.this, MakingCallConferenceActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MadeSuccessCallActivity.this,
                        getString(R.string.make_conference_call_unsuccess), Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected Boolean doInBackground(String... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Url.BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            Response response;
            EndPointInterface apiService =
                    restAdapter.create(EndPointInterface.class);
            Boolean success = false;
            try {
                response = apiService.rate(wrapperRate);
                success = response.isSuccess();
            } catch (RetrofitError retrofitError) {

            }
            return success;
        }
    }

    private void disconnectCall(){
        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCountingTime() {

        timer=new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            int hour = 0;
            int minute = 0;
            int second = 0;
            String hourText;
            String minuteText;
            String secondText;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (second > 0 && (second % 60 == 0)) {

                            minute++;
                            second = 0;
                        }
                        if (minute > 0 && (minute % 60 == 0)) {

                            hour++;
                            minute = 0;
                        }

                        if (hour < 10)
                            hourText = "0" + hour;
                        else
                            hourText = "" + hour;

                        if (minute < 10)
                            minuteText = "0" + minute;
                        else
                            minuteText = "" + minute;

                        if (second < 10)
                            secondText = "0" + second;
                        else
                            secondText = "" + second;
                        textViewCount.setText(hourText + ":" + minuteText + ":" + secondText);
                        second++;

                        if (FlagHelpCoop.isCheckedToCount) {
                            stopCountingTime();
                            FlagHelpCoop.isCheckedToCount = false;
                        }
                    }
                });
            }
        }, 1000, 1000);

    }

    private void stopCountingTime() {
        if(timer != null)
            timer.cancel();
    }



}

package com.novahub.voipcall.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.novahub.voipcall.R;
import com.novahub.voipcall.activity.SubmitRateActivity;
import com.novahub.voipcall.adapter.ConnectedPeopleAdapter;
import com.novahub.voipcall.model.Distance;
import com.novahub.voipcall.model.Rate;
import com.novahub.voipcall.model.WrapperRate;
import com.novahub.voipcall.sharepreferences.SharePreferences;
import com.novahub.voipcall.utils.Asset;
import com.novahub.voipcall.utils.FlagHelpCoop;
import com.novahub.voipcall.utils.PhoneUtils;
import com.novahub.voipcall.utils.TempDataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.internal.zzip.runOnUiThread;

public class MyPhoneBroadcastReceiver extends BroadcastReceiver {
    public static final long timeAfter = 100;
    private RecyclerView recyclerViewList;
    private RecyclerView.LayoutManager layoutManager;
    private ConnectedPeopleAdapter connectedPeopleAdapter;
    private List<Rate> rateList;
    private boolean isRated = false;
    private List<Distance> distanceList;
    private Timer timer;
    private boolean isAccepted = false;
    private TextView textViewCount;
    public MyPhoneBroadcastReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // TODO: When Samaritan receives an incoming call from Caller
        if (FlagHelpCoop.isReceivedIncomingCallFromSamaritan) {
            FlagHelpCoop.isReceivedIncomingCallFromSamaritan = false;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    initilizeViewForGoodSamaritan(context, Asset.listOfCallerAndSamaritans);
                }
            }, timeAfter);
        }
        if (FlagHelpCoop.isMadeSuccessCall) {
            FlagHelpCoop.isMadeSuccessCall = false;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    initilizeViewForCaller(context, Asset.listOfGoodSamaritans);
                }
            }, timeAfter);
        }
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                FlagHelpCoop.isCheckedToCount = false;
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                FlagHelpCoop.isCheckedToCount = true;
            }

        }
    }

    // TODO: This function looks stupid, but no way to do it, because it's just a system view
    private void initilizeViewForCaller(final Context context, List<Distance> dataListRate) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        final WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        final View myView = inflater.inflate(R.layout.receive_incoming_samaritan
                , null);
        textViewCount = (TextView) myView.findViewById(R.id.textViewCount);

        final Button buttonAccept = (Button) myView.findViewById(R.id.buttonAccept);
        final Button buttonReject = (Button) myView.findViewById(R.id.buttonReject);
        final TextView textViewAlert = (TextView) myView.findViewById(R.id.textViewAlert);
        TextView textViewTitle = (TextView) myView.findViewById(R.id.textViewTitle);

        textViewAlert.setVisibility(View.GONE);
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtils.disconnectCall();
                if (timer != null)
                    timer.cancel();
                buttonReject.setEnabled(false);
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtils.acceptCall(context);
                isAccepted = true;
                buttonAccept.setEnabled(false);
                buttonReject.setText("End");
                recyclerViewList.setVisibility(View.VISIBLE);
                textViewAlert.setVisibility(View.GONE);
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
                                    if (timer != null)
                                        timer.cancel();
                                    FlagHelpCoop.isCheckedToCount = false;
                                }
                            }
                        });
                    }
                }, 1000, 1000);
            }
        });


        textViewTitle.setText("Samaritan Need Help");

        recyclerViewList = (RecyclerView) myView.findViewById(R.id.recyclerViewList);
        recyclerViewList.setVisibility(View.VISIBLE);
        layoutManager = new LinearLayoutManager(context);
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setHasFixedSize(true);
        distanceList = new ArrayList<>();
        distanceList.addAll(dataListRate);
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

        Button buttonRate = (Button) myView.findViewById(R.id.buttonRate);
        buttonRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRated) {
                    Asset.listOfGoodSamaritans = null;
                    String token = SharePreferences.getData(context, SharePreferences.TOKEN);
                    for (int i = 0; i < rateList.size(); i++) {
                        if (rateList.get(i).getRateStatus() == null)
                            rateList.remove(i);
                    }
                    Asset.wrapperRate = new WrapperRate(token, Asset.nameOfConferenceRoom, rateList);
                    wm.removeViewImmediate(myView);
                    Intent intent = new Intent(context, SubmitRateActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context,
                            context.getString(R.string.rate_toast), Toast.LENGTH_LONG).show();
                }
            }
        });
        // Add layout to window manager
        wm.addView(myView, params);
    }
    // TODO: This function looks stupid, but no way to do it, because it's just a system view
    private void initilizeViewForGoodSamaritan(final Context context, List<Distance> dataListRate) {

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        final WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        final View myView = inflater.inflate(R.layout.receive_incoming_samaritan
                , null);
        textViewCount = (TextView) myView.findViewById(R.id.textViewCount);

        final Button buttonAccept = (Button) myView.findViewById(R.id.buttonAccept);
        final Button buttonReject = (Button) myView.findViewById(R.id.buttonReject);
        final TextView textViewAlert = (TextView) myView.findViewById(R.id.textViewAlert);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtils.acceptCall(context);
                isAccepted = true;
                buttonAccept.setEnabled(false);
                buttonReject.setText("End");
                recyclerViewList.setVisibility(View.VISIBLE);
                textViewAlert.setVisibility(View.GONE);
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
                            }
                        });
                    }
                }, 1000, 1000);
            }
        });
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtils.disconnectCall();
                if(timer != null)
                    timer.cancel();
                buttonReject.setEnabled(false);
                if (!isAccepted) {
                    wm.removeViewImmediate(myView);
                }
            }
        });

        TextView textViewTitle = (TextView) myView.findViewById(R.id.textViewTitle);
        textViewTitle.setText("Samaritan Need Help");

        recyclerViewList = (RecyclerView) myView.findViewById(R.id.recyclerViewList);
        layoutManager = new LinearLayoutManager(context);
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setHasFixedSize(true);
        distanceList = new ArrayList<>();
        distanceList.addAll(dataListRate);
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

        Button buttonRate = (Button) myView.findViewById(R.id.buttonRate);
        buttonRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRated) {
                    Asset.listOfGoodSamaritans = null;
                    String token = SharePreferences.getData(context, SharePreferences.TOKEN);
                    for (int i = 0; i < rateList.size(); i++) {
                        if (rateList.get(i).getRateStatus() == null)
                            rateList.remove(i);
                    }
                    Asset.wrapperRate = new WrapperRate(token, Asset.nameOfConferenceRoom, rateList);
                    wm.removeViewImmediate(myView);
                    Intent intent = new Intent(context, SubmitRateActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context,
                            context.getString(R.string.rate_toast), Toast.LENGTH_LONG).show();
                }
            }
        });
        // Add layout to window manager
        wm.addView(myView, params);

    }


}

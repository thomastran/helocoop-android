/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.novahub.voipcall.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.novahub.voipcall.R;
import com.novahub.voipcall.activity.GetPhoneNumberActivity;
import com.novahub.voipcall.activity.IncomingGcmRequestActivity;
import com.novahub.voipcall.activity.MainActivity;
import com.novahub.voipcall.model.Distance;
import com.novahub.voipcall.utils.Asset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("=========>", data.toString());
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + data.toString());

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        startIncomingGcmActivity(data);


    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, GetPhoneNumberActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void startIncomingGcmActivity(Bundle data) {
        if (data != null) {
            String nameOfInitialUser = "";
            String addressOfInitialUser = "";
            String descriptionOfInitialUser = "";
            String latitude = "";
            String longitude = "";
            String gcm_intial_user = data.getString(Asset.GCM_INITIAL_USER);
            String gcm_users = data.getString(Asset.GCM_USERS);
            String gcm_name_room = data.getString(Asset.GCM_NAME_ROOM);
            Log.d("===============>", gcm_name_room);
            Asset.distanceListRates = new ArrayList<>();
            Asset.distanceListRates.addAll(convertData(gcm_intial_user, gcm_users));
            Asset.nameRoom = gcm_name_room;
            try {
                JSONObject jsonObj = new JSONObject(gcm_intial_user);
                nameOfInitialUser = jsonObj.getString(Asset.GCM_NAME_CALLER);
                addressOfInitialUser = jsonObj.getString(Asset.GCM_ADDRESS_CALLER);
                descriptionOfInitialUser = jsonObj.getString(Asset.GCM_DESCRIPTION_CALLER);
                latitude = jsonObj.getString(Asset.LATITUDE);
                longitude = jsonObj.getString(Asset.LONGITUDE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(getApplicationContext(), IncomingGcmRequestActivity.class);
            intent.putExtra(Asset.IS_FROM_SERVER, true);
            intent.putExtra(Asset.GCM_NAME_CALLER, nameOfInitialUser);
            intent.putExtra(Asset.GCM_ADDRESS_CALLER, addressOfInitialUser);
            intent.putExtra(Asset.GCM_DESCRIPTION_CALLER, descriptionOfInitialUser);
            intent.putExtra(Asset.LATITUDE, latitude);
            intent.putExtra(Asset.LONGITUDE, longitude);
            intent.putExtra(Asset.GCM_USERS, gcm_users);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    private List<Distance> convertData(String initialUser, String gcm_users) {
        List<Distance> distanceList = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(initialUser);
            String nameOfInitialUser = jsonObj.getString(Asset.GCM_NAME_CALLER);
            String addressOfInitialUser = jsonObj.getString(Asset.GCM_ADDRESS_CALLER);
            String descriptionOfInitialUser = jsonObj.getString(Asset.GCM_DESCRIPTION_CALLER);
            String token = jsonObj.getString(Asset.GCM_TOKEN);
            distanceList.add(new Distance(descriptionOfInitialUser, nameOfInitialUser, addressOfInitialUser, token));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonArray = new JSONArray(gcm_users);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String nameOfInitialUser = jsonObject.getString(Asset.GCM_NAME_CALLER);
                String addressOfInitialUser = jsonObject.getString(Asset.GCM_ADDRESS_CALLER);
                String descriptionOfInitialUser = jsonObject.getString(Asset.GCM_DESCRIPTION_CALLER);
                String token = jsonObject.getString(Asset.GCM_TOKEN);
                distanceList.add(new Distance(descriptionOfInitialUser, nameOfInitialUser, addressOfInitialUser, token));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return distanceList;

    }
}

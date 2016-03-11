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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.novahub.voipcall.activity.ConnectTwillioActivity;
import com.novahub.voipcall.activity.IncomingGcmRequestActivity;
import com.novahub.voipcall.model.Distance;
import com.novahub.voipcall.utils.Asset;
import com.novahub.voipcall.utils.FlagHelpCoop;

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
        startIncomingGcmActivity(data);
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
            Asset.listOfCallerAndSamaritans = new ArrayList<>();
            Asset.listOfCallerAndSamaritans.addAll(convertData(gcm_intial_user, gcm_users));
            Asset.nameOfConferenceRoom = gcm_name_room;
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
            Intent intent = new Intent(getApplicationContext(), ConnectTwillioActivity.class);
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

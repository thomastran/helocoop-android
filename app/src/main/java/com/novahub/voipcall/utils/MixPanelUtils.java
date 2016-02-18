package com.novahub.voipcall.utils;

import android.app.Activity;
import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by samnguyen on 17/02/2016.
 */
public class MixPanelUtils {

    public static void pushData(Activity context, String content, JSONObject jsonObject) {
        MixpanelAPI mixpanel =
                MixpanelAPI.getInstance(context, Asset.projectToken);

        mixpanel.track(content, jsonObject);
    }
}

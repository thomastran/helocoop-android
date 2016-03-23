package com.novahub.voipcall.utils;

import com.novahub.voipcall.model.Distance;
import com.novahub.voipcall.model.WrapperRate;

import java.util.List;

/**
 * Created by samnguyen on 18/12/2015.
 */
public class Asset {
    public static List<Distance> listOfGoodSamaritans;

    public static List<Distance> listOfCallerAndSamaritans;

    public static WrapperRate wrapperRate;

    public static String nameOfConferenceRoom;

    public static final String IS_CHANGED_INFO = "IS_CHANGED_INFO";

    public static final String GCM_TOKEN = "token";

    public static final String GCM_NAME_CALLER = "name";

    public static final String GCM_ADDRESS_CALLER = "address";

    public static final String GCM_DESCRIPTION_CALLER = "description";

    public static final String LATITUDE =  "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String GCM_INITIAL_USER = "gcm_initial_user";

    public static final String GCM_USERS = "gcm_users";

    public static final String GCM_NAME_ROOM = "gcm_name_room";

    public static final String FROM_CALLER = "FROM_CALLER";

    public static final long TIMEREPEATEALARM = 60*60*1000;
}

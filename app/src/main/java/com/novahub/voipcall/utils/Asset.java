package com.novahub.voipcall.utils;

import com.novahub.voipcall.model.Distance;
import com.novahub.voipcall.model.WrapperRate;

import java.util.List;

/**
 * Created by samnguyen on 18/12/2015.
 */
public class Asset {

    // If you are the caller this @listOfGoodSamaritans will store all good samaritans
    public static List<Distance> listOfGoodSamaritans;

    // If you are good samaritans, you will receive this @listOfCallerAndSamaritans
    public static List<Distance> listOfCallerAndSamaritans;

    // Rating data to sent to server
    public static WrapperRate wrapperRate;

    // After you made a successfull call, you will recieve a @nameOfConferenceRoom
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

    public static final String paramsNameRoom = "name_room";

    public static final String paramsToken = "token";

    public static final String paramsIsFromCaller = "is_from_caller";
}

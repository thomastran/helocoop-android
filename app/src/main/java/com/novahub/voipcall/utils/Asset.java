package com.novahub.voipcall.utils;

import com.novahub.voipcall.model.Distance;
import com.novahub.voipcall.model.WrapperRate;

import java.util.List;

/**
 * Created by samnguyen on 18/12/2015.
 */
public class Asset {

    public static final String CONTACT1 = "Contact1 ";

    public static final String CONTACT2 = "Contact2 ";

    public static final String CONTACT3 = "Contact3 ";

    public static final String CONTACT4 = "Contact4 ";

    public static final String CONTACT5 = "Contact5 ";

    public static final String CURRENT_CONTACT = "Current_Contact";

    public static final String Twillio_Conference = "Conference";

    public static final String Twillio_Room = "Myroom";

    public static final String Twillio_People = "People";

    public static final String VIOP_CALL = "VIOPCALL";

    public static final String Current_Client = "CurrentClient";

    public static final String FROM_INCOMING_CALL = "FROM_INCOMING_CALL";

    public static List<Distance> distanceList;

    public static List<Distance> distanceListRates;

    public static WrapperRate wrapperRate;

    public static String nameRoom;

    public static String nameOfCaller = null;

    public static String addressOfCaller = null;

    public static String descriptionOfCaller = null;

    public static float latitude;

    public static float longitude;

    public static final String IS_FROM_SERVER = "IS_FROM_SERVER";

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

    public static boolean isRinging = false;

    public static String projectToken = "287de2ed49bf63670af7ec0d3c21f7b2";
}

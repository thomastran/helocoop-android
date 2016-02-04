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

    public static WrapperRate wrapperRate;

    public static String nameRoom;

    public static String nameOfCaller = null;

    public static String addressOfCaller = null;

    public static String descriptionOfCaller = null;

    public static float latitude;

    public static float longitude;

    public static final String IS_FROM_SERVER = "IS_FROM_SERVER";

    public static final String IS_CHANGED_INFO = "IS_CHANGED_INFO";

    public static final String GCM_NAME_CALLER = "gcm_name_caller";

    public static final String GCM_ADDRESS_CALLER = "gcm_address_caller";

    public static final String GCM_DESCRIPTION_CALLER = "gcm_description_caller";

    public static final String LATITUDE =  "latitude";

    public static final String LONGITUDE = "longitude";
}

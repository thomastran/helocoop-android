package com.novahub.voipcall.utils;

/**
 * Created by samnguyen on 18/02/2016.
 */
public class TempDataUtils {
    public static void resetData() {
        Asset.listOfGoodSamaritans = null;
        Asset.listOfCallerAndSamaritans = null;
    }

}

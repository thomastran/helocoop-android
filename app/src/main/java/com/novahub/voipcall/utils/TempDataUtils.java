package com.novahub.voipcall.utils;

/**
 * Created by samnguyen on 18/02/2016.
 */
public class TempDataUtils {
    public static void resetData() {
        Asset.listOfGoodSamaritans = null;
        Asset.listOfCallerAndSamaritans = null;
        Asset.isRinging = false;
        Asset.nameOfCaller = null;
        Asset.addressOfCaller = null;
        Asset.descriptionOfCaller = null;
    }

    public static boolean isExistedDataOfCallerInfo() {
        if (Asset.nameOfCaller != null & Asset.addressOfCaller != null & Asset.descriptionOfCaller != null)
            return true;
        else
            return false;
    }
}

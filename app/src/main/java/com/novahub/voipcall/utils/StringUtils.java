package com.novahub.voipcall.utils;

/**
 * Created by samnguyen on 06/01/2016.
 */
public class StringUtils {

    public static boolean isEmpty(String str) {

        return str != null ? str.equals("") : true;
    }

    public static boolean equals(String str1, String str2) {

        return str1 == null ? str2 == null : str2 == null ? false : str1.equals(str2);
    }

    public static boolean isAnyEmpty(String... strs) {
        boolean result = false;
        for (String str : strs) {
            if (isEmpty(str)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static String nullToBlank(String str) {

        return str == null ? "" : str;
    }

}

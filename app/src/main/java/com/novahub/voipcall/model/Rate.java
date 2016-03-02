package com.novahub.voipcall.model;

/**
 * Created by samnguyen on 01/02/2016.
 */
public class Rate {
    private String token;
    private String rateStatus;

    public Rate(String rateStatus, String token) {
        this.rateStatus = rateStatus;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRateStatus() {
        return rateStatus;
    }

    public void setRateStatus(String rateStatus) {
        this.rateStatus = rateStatus;
    }
}

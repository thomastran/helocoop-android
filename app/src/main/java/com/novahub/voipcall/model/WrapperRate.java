package com.novahub.voipcall.model;

import java.util.List;

/**
 * Created by samnguyen on 01/02/2016.
 */
public class WrapperRate {
    private String token;
    private String nameRoom;
    private List<Rate> rateList;

    public WrapperRate(String token, String nameRoom, List<Rate> rateList) {
        this.token = token;
        this.nameRoom = nameRoom;
        this.rateList = rateList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public List<Rate> getRateList() {
        return rateList;
    }

    public void setRateList(List<Rate> rateList) {
        this.rateList = rateList;
    }
}

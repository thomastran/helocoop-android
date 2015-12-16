package com.novahub.voipcall.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by samnguyen on 16/12/2015.
 */
public class Token {

    @SerializedName("token")
    private String token;

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

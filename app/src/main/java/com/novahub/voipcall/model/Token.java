package com.novahub.voipcall.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by samnguyen on 16/12/2015.
 */
public class Token {

    @SerializedName("token")
    private String token;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public Token(String token) {
        this.token = token;
    }

    public Token(String token, boolean success, String message) {
        this.token = token;
        this.success = success;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

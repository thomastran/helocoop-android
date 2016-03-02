package com.novahub.voipcall.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by samnguyen on 06/01/2016.
 */
public class Response {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("distances")
    private List<Distance> distanceList;

    public Response(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public Response(boolean success, String message, String token, List<Distance> distanceList) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.distanceList = distanceList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
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

    public List<Distance> getDistanceList() {
        return distanceList;
    }

    public void setDistanceList(List<Distance> distanceList) {
        this.distanceList = distanceList;
    }
}

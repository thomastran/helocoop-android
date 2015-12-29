package com.novahub.voipcall.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by samnguyen on 29/12/2015.
 */
public class Status {
    @SerializedName("status")
    private boolean status;

    public Status(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

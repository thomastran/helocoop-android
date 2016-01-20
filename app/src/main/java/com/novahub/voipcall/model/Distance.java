package com.novahub.voipcall.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by samnguyen on 19/01/2016.
 */
public class Distance {
    @SerializedName("mile")
    private float mile;

    @SerializedName("token")
    private String token;

    @SerializedName("instance_id")
    private String instanceId;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("address")
    private String address;

    public Distance(float mile, String token, String instanceId, String phoneNumber, String name, String description, String address) {
        this.mile = mile;
        this.token = token;
        this.instanceId = instanceId;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public Distance(float mile, String token, String instanceId, String phoneNumber) {
        this.mile = mile;
        this.token = token;
        this.instanceId = instanceId;
        this.phoneNumber = phoneNumber;
    }

    public float getMile() {
        return mile;
    }

    public void setMile(float mile) {
        this.mile = mile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

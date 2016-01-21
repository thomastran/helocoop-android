package com.novahub.voipcall.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by samnguyen on 19/01/2016.
 */
public class Distance {
    @SerializedName("mile")
    private float mile;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("address")
    private String address;

    public Distance(float mile, String phoneNumber, String name, String description, String address) {
        this.mile = mile;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public Distance(float mile, String phoneNumber) {
        this.mile = mile;
        this.phoneNumber = phoneNumber;
    }

    public float getMile() {
        return mile;
    }

    public void setMile(float mile) {
        this.mile = mile;
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

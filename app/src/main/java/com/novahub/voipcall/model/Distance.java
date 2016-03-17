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

    @SerializedName("token")
    private String token;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    public Distance(float mile, String phoneNumber, String description, String name, String address, String token) {
        this.mile = mile;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.name = name;
        this.address = address;
        this.token = token;
    }

    public Distance(String description, String name, String address, String token) {
        this.description = description;
        this.name = name;
        this.address = address;
        this.token = token;
    }

    public Distance(String description, String name, String address, String token, String latitude, String longitude) {
        this.description = description;
        this.name = name;
        this.address = address;
        this.token = token;
        this.latitude = latitude;
        this.longitude = longitude;
    }



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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}

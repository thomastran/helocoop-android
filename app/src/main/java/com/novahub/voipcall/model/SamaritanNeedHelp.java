package com.novahub.voipcall.model;

/**
 * Created by samnguyen on 23/02/2016.
 */
public class SamaritanNeedHelp {

    private String name;
    private String address;
    private String description;
    private String token;
    private float latitude;
    private float longitude;

    public SamaritanNeedHelp(String name, String address, String description, float latitude, float longitude) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SamaritanNeedHelp(String name, String address, String description, String token, float latitude, float longitude) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.token = token;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

package com.novahub.voipcall.model;

/**
 * Created by samnguyen on 11/01/2016.
 */
public class Location {

    private float latitude;

    private float longtitude;

    private String address;

    private String dateCreated;

    public Location(float latitude, float longtitude, String dateCreated, String address) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.dateCreated = dateCreated;
        this.address = address;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(float longtitude) {
        this.longtitude = longtitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}

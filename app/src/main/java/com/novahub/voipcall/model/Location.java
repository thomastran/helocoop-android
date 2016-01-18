package com.novahub.voipcall.model;

/**
 * Created by samnguyen on 11/01/2016.
 */
public class Location {

    private float latitude;

    private float longtitude;

    public Location(float latitude, float longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
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


}

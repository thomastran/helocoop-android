package com.novahub.voipcall.model;

/**
 * Created by samnguyen on 28/12/2015.
 */
public class ClientToCall {

    private boolean isCalled;

    private String name;

    public ClientToCall(boolean isCalled, String name) {
        this.isCalled = isCalled;
        this.name = name;
    }

    public boolean isCalled() {
        return isCalled;
    }

    public void setIsCalled(boolean isCalled) {
        this.isCalled = isCalled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

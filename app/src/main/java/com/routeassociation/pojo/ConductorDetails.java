package com.routeassociation.pojo;

/**
 * Created by techlead on 19/7/18.
 */

public class ConductorDetails {
    private int maidId;
    private String maidName;
    private String maidAddress;

    @Override
    public String toString() {
        return maidName;
    }

    public int getMaidId() {
        return maidId;
    }

    public void setMaidId(int maidId) {
        this.maidId = maidId;
    }

    public String getMaidName() {
        return maidName;
    }

    public void setMaidName(String maidName) {
        this.maidName = maidName;
    }

    public String getMaidAddress() {
        return maidAddress;
    }

    public void setMaidAddress(String maidAddress) {
        this.maidAddress = maidAddress;
    }

    public String getMaidContact() {
        return maidContact;
    }

    public void setMaidContact(String maidContact) {
        this.maidContact = maidContact;
    }

    private String maidContact;

}

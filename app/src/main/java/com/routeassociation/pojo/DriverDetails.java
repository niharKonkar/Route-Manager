package com.routeassociation.pojo;

/**
 * Created by techlead on 2/4/18.
 */

public class DriverDetails {
    private int driverId;
    private String driverName;

    @Override
    public String toString() {
        return driverName ;
    }

    private String driverAddress;
    private String driverContact;

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverAddress() {
        return driverAddress;
    }

    public void setDriverAddress(String driverAddress) {
        this.driverAddress = driverAddress;
    }

    public String getDriverContact() {
        return driverContact;
    }

    public void setDriverContact(String driverContact) {
        this.driverContact = driverContact;
    }
}

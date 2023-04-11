package com.routeassociation.pojo;

/**
 * Created by neha on 13/4/17.
 */

public class VehicleDetails {
    public int getVehId() {
        return vehId;
    }

    public void setVehId(int vehId) {
        this.vehId = vehId;
    }

    @Override
    public String toString() {
        return
                vehName ;
    }

    public String getVehName() {
        return vehName;
    }

    public void setVehName(String vehName) {
        this.vehName = vehName;
    }

    private int vehId;
    private String vehName;
    public int getVehIdbyName(String vehName)
    {

        return vehId;
    }
}

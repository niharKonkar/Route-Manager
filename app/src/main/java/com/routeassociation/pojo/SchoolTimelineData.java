package com.routeassociation.pojo;

import java.util.ArrayList;

/**
 * Created by sonal on 30/1/18.
 */

public class SchoolTimelineData {

    private  int vehId,gegId;
    private ArrayList<RouteDetails> routeDetailsArrayList;

    public ArrayList<RouteDetails> getRouteDetailsArrayList() {
        return routeDetailsArrayList;
    }

    public void setRouteDetailsArrayList(ArrayList<RouteDetails> routeDetailsArrayList) {
        this.routeDetailsArrayList = routeDetailsArrayList;
    }

    public int getVehId() {
        return vehId;
    }

    public void setVehId(int vehId) {
        this.vehId = vehId;
    }

    public int getGegId() {
        return gegId;
    }

    public void setGegId(int gegId) {
        this.gegId = gegId;
    }

    public String getSbeEvent() {
        return sbeEvent;
    }

    public void setSbeEvent(String sbeEvent) {
        this.sbeEvent = sbeEvent;
    }

    public String getSbeEventType() {
        return sbeEventType;
    }

    public void setSbeEventType(String sbeEventType) {
        this.sbeEventType = sbeEventType;
    }

    public String getSbeTimestamp() {
        return sbeTimestamp;
    }

    public void setSbeTimestamp(String sbeTimestamp) {
        this.sbeTimestamp = sbeTimestamp;
    }

    private String sbeEvent,sbeEventType,sbeTimestamp;

}

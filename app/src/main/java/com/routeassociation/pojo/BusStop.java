package com.routeassociation.pojo;

import androidx.annotation.NonNull;

/**
 * Created by neha on 13/4/17.
 */

public class BusStop {

    private String busStopName;
    private double busStopLat;
    private double busStopLng;
    private double distance;


    public String getBusStopName() {
        return busStopName;
    }

    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    public double getBusStopLat() {
        return busStopLat;
    }

    public void setBusStopLat(double busStopLat) {
        this.busStopLat = busStopLat;
    }

    public double getBusStopLng() {
        return busStopLng;
    }

    public void setBusStopLng(double busStopLng) {
        this.busStopLng = busStopLng;
    }

    public double getDistance() {
        return distance;
    }

    @NonNull
    @Override
    public String toString() {
        return this.busStopName;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}

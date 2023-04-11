package com.routeassociation.pojo;

import java.io.Serializable;

/**
 * Created by techlead on 18/05/17.
 */
public class EtaData implements Serializable {

    private String distance;
    private String time;

    public EtaData() {

    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

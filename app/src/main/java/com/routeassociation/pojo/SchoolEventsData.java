package com.routeassociation.pojo;

/**
 * Created by techlead on 28/12/17.
 */

public class SchoolEventsData {
    @Override
    public String toString() {
        return eventName;
    }

    private String eventName,eventTimestamp,eventAlertFlag;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(String eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getEventAlertFlag() {
        return eventAlertFlag;
    }

    public void setEventAlertFlag(String eventAlertFlag) {
        this.eventAlertFlag = eventAlertFlag;
    }
}

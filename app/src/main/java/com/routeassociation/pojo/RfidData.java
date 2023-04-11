package com.routeassociation.pojo;

/**
 * Created by neha on 27/4/17.
 */

public class RfidData {
    private String rasName,rasCard,rtnViolation,rtnTimestamp;

    public String getRasName() {
        return rasName;
    }

    public void setRasName(String rasName) {
        this.rasName = rasName;
    }

    public String getRasCard() {
        return rasCard;
    }

    public void setRasCard(String rasCard) {
        this.rasCard = rasCard;
    }

    public String getRtnViolation() {
        return rtnViolation;
    }

    public void setRtnViolation(String rtnViolation) {
        this.rtnViolation = rtnViolation;
    }

    public String getRtnTimestamp() {
        return rtnTimestamp;
    }

    public void setRtnTimestamp(String rtnTimestamp) {
        this.rtnTimestamp = rtnTimestamp;
    }
}

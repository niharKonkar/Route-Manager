package com.routeassociation.pojo;

public class IncidentCategoryDetails {

    private int icaId;
    private String icsName;

    @Override
    public String toString() {
        return icsName;
    }

    public int getIcaId() {
        return icaId;
    }

    public void setIcaId(int icaId) {
        this.icaId = icaId;
    }

    public String getIcsName() {
        return icsName;
    }

    public void setIcsName(String icsName) {
        this.icsName = icsName;
    }
}

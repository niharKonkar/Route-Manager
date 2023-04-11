package com.routeassociation.pojo;

public class RouteDetailsInc {
    private int routeId;
    private String routeName;

    @Override
    public String toString() {
        return routeName;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}

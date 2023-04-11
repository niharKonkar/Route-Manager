package com.routeassociation.pojo;


public class RouteDetails {
    private int routeId,originId,vehId;
    private String routeName;
    private String destination;
    private String conductorNumber;
    private String conductorName;
    private String driverNumber;
    private String driverName;
    private String dropDriverName;
    private String dropDiverNumber;
    private String dropConductorName;
    private boolean selected;
    private String routeDesc;
    private String pickupTeacherCount,dropTeacherCount,pickupStudentCount,dropStudentCount,busCapacity;

    public String getPickupTeacherCount() {
        return pickupTeacherCount;
    }

    public void setPickupTeacherCount(String pickupTeacherCount) {
        this.pickupTeacherCount = pickupTeacherCount;
    }

    public String getDropTeacherCount() {
        return dropTeacherCount;
    }

    public void setDropTeacherCount(String dropTeacherCount) {
        this.dropTeacherCount = dropTeacherCount;
    }

    public String getPickupStudentCount() {
        return pickupStudentCount;
    }

    public void setPickupStudentCount(String pickupStudentCount) {
        this.pickupStudentCount = pickupStudentCount;
    }

    public String getDropStudentCount() {
        return dropStudentCount;
    }

    public void setDropStudentCount(String dropStudentCount) {
        this.dropStudentCount = dropStudentCount;
    }

    public String getBusCapacity() {
        return busCapacity;
    }

    public void setBusCapacity(String busCapacity) {
        this.busCapacity = busCapacity;
    }

    public String getRouteDesc() {
        return routeDesc;
    }

    public void setRouteDesc(String routeDesc) {
        this.routeDesc = routeDesc;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDropConductorName() {
        return dropConductorName;
    }

    public void setDropConductorName(String dropConductorName) {
        this.dropConductorName = dropConductorName;
    }

    public String getDropConductorNumber() {
        return dropConductorNumber;
    }

    public void setDropConductorNumber(String dropConductorNumber) {
        this.dropConductorNumber = dropConductorNumber;
    }

    private String dropConductorNumber;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    private String driverId;

    public String getDropDriverName() {
        return dropDriverName;
    }

    public void setDropDriverName(String dropDriverName) {
        this.dropDriverName = dropDriverName;
    }

    public String getDropDiverNumber() {
        return dropDiverNumber;
    }

    public void setDropDiverNumber(String dropDiverNumber) {
        this.dropDiverNumber = dropDiverNumber;
    }

    public String getDropDriverId() {
        return dropDriverId;
    }

    public void setDropDriverId(String dropDriverId) {
        this.dropDriverId = dropDriverId;
    }

    private String dropDriverId;

    public String getConductorNumber() {
        return conductorNumber;
    }

    public void setConductorNumber(String conductorNumber) {
        this.conductorNumber = conductorNumber;
    }

    public String getConductorName() {
        return conductorName;
    }

    public void setConductorName(String conductorName) {
        this.conductorName = conductorName;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }



    public String getShowRfidFlag() {
        return showRfidFlag;
    }

    public void setShowRfidFlag(String showRfidFlag) {
        this.showRfidFlag = showRfidFlag;
    }

    private String showRfidFlag;

    @Override
    public String toString() {
        return  vehNumber;
    }

    private String vehNumber;

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getOriginId() {
        return originId;
    }

    public void setOriginId(int originId) {
        this.originId = originId;
    }

    public int getVehId() {
        return vehId;
    }

    public void setVehId(int vehId) {
        this.vehId = vehId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getVehNumber() {
        return vehNumber;
    }

    public void setVehNumber(String vehNumber) {
        this.vehNumber = vehNumber;
    }
}


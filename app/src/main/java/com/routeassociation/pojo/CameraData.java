package com.routeassociation.pojo;

import java.util.ArrayList;
import java.util.List;

public class CameraData {
    private String cameraClass;
    private Integer count;
    private List<String> cameraList;

    public CameraData() {
        cameraList = new ArrayList<>();
    }

    public String getCameraClass() {
        return cameraClass;
    }

    public void setCameraClass(String cameraClass) {
        this.cameraClass = cameraClass;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getCameraList() {
        return cameraList;
    }

    public void setCameraList(List<String> cameraList) {
        this.cameraList = cameraList;
    }
}

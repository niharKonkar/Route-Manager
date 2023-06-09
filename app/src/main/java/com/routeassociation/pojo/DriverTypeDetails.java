package com.routeassociation.pojo;

/**
 * Created by techlead on 2/4/18.
 */

public class DriverTypeDetails {
    private int typeId;
    private String typeName;
    private String typeCode;

    public DriverTypeDetails(int typeId, String typeName, String typeCode) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.typeCode = typeCode;
    }

    @Override
    public String toString() {
        return typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}

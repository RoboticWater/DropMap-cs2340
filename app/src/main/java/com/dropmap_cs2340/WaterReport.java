package com.dropmap_cs2340;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by arsenelakpa on 2/23/17.
 * Class containing water data
 */

public class WaterReport {
    private String reportName;
    private String id;
    private Double x;
    private Double y;
    @Nullable
    private WaterType type;
    @Nullable
    private WaterCondition condition;

    public WaterReport() {}

    public WaterReport(String _id, String _reportName, double lat, double lon, WaterType _type,
                       WaterCondition _condition) {
        id = _id;
        reportName = _reportName;
        x = lat;
        y = lon;
        type = _type;
        condition = _condition;
    }

    public WaterReport(String _id, String _reportName, double lat, double lon, String _type,
                       String _condition) {
        this(_id, _reportName, lat, lon, WaterType.valueOf(_type),
                WaterCondition.valueOf(_condition));
    }

    @Override
    public String toString() {
        return type.toString() + "," + condition.toString();
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public LatLng loc() {
        return new LatLng(x, y);
    }

    public String getType() {
        if (type == null) {
            return null;
        } else {
            return type.name();
        }
    }

    public void setType(String _type) {
        if (_type == null) {
            type = null;
        } else {
            type = WaterType.valueOf(_type);
        }
    }

    public String getCondition() {
        if (condition == null) {
            return null;
        } else {
            return condition.name();
        }
    }

    public void setCondition(String _condition) {
        if (_condition == null) {
            condition = null;
        } else {
            condition = WaterCondition.valueOf(_condition);
        }
    }
}

package com.dropmap_cs2340;
import android.location.Location;

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
    private WaterType type;
    private WaterCondition condition;

    public WaterReport(String _reportName, double lat, double lon, WaterType _type, WaterCondition _condition) {
        reportName = _reportName;
        x = lat;
        y = lon;
        type = _type;
        condition = _condition;
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

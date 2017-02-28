package com.dropmap_cs2340;
import android.location.Location;

/**
 * Created by arsenelakpa on 2/23/17.
 */

public class WaterReport {
    private String reportName;
    private String id;
    private Location source;
    private WaterType type;
    private WaterCondition condition;

    public WaterReport(String _reportName, Location _source, WaterType _type, WaterCondition _condition) {
        reportName = _reportName;
        source = _source;
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

    public Location getSource() {
        return source;
    }

    public void setSource(Location source) {
        this.source = source;
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

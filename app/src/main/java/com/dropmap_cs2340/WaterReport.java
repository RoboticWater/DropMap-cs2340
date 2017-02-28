package com.dropmap_cs2340;
import android.location.Location;

/**
 * Created by arsenelakpa on 2/23/17.
 */

public class WaterReport {
    private String reportName;
    private Location source;
    private WaterType type;
    private WaterCondition condition;

    public WaterReport(String _reportName, Location _source, WaterType _type, WaterCondition _condition) {
        reportName = _reportName;
        source = _source;
        type = _type;
        condition = _condition;
    }



}

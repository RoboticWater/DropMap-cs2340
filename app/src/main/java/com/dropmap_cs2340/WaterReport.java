package com.dropmap_cs2340;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by arsenelakpa on 2/23/17.
 * Class containing water data
 */

class WaterReport {
    private String reportName;
    private String user;
    private String id;
    private double x;
    private double y;
    @Nullable
    private WaterType type;
    @Nullable
    private WaterCondition condition;
    private double virusPPM;
    private double contaminantPPM;

    /**
     * Creates empty water report for Firebase
     */
    WaterReport() {
        virusPPM = -1;
        contaminantPPM = -1;
    }

    @Override
    public String toString() {
        return (type == null ? "" : type.toString()) + "," +
                (condition == null ? "" : condition.toString());
    }

    String getReportName() {
        return reportName;
    }

    void setReportName(String reportName) {
        this.reportName = reportName;
    }

    String getUser() {
        return user;
    }

    void setUser(String user) {
        this.user = user;
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    Double getX() {
        return x;
    }


    void setX(double x) {
        this.x = x;
    }

    Double getY() {
        return y;
    }

    void setY(double y) {
        this.y = y;
    }

    LatLng loc() {
        return new LatLng(x, y);
    }

    String getType() {
        if (type == null) {
            return null;
        } else {
            return type.name();
        }
    }

    void setType(@Nullable String _type) {
        if (_type == null) {
            type = null;
        } else {
            type = WaterType.valueOf(_type);
        }
    }

    String getCondition() {
        if (condition == null) {
            return null;
        } else {
            return condition.name();
        }
    }

    void setCondition(@Nullable String _condition) {
        if (_condition == null) {
            condition = null;
        } else {
            condition = WaterCondition.valueOf(_condition);
        }
    }

    public double getVirusPPM() {
        return virusPPM;
    }

    public void setVirusPPM(double virusPPM) {
        this.virusPPM = virusPPM;
    }

    public double getContaminantPPM() {
        return contaminantPPM;
    }

    public void setContaminantPPM(double contaminantPPM) {
        this.contaminantPPM = contaminantPPM;
    }

    public boolean formatPurity() {
        return !((-1 == contaminantPPM) && (-1 == virusPPM));
    }
}

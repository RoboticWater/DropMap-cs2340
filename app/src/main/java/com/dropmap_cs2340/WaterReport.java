package com.dropmap_cs2340;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by arsenelakpa on 2/23/17.
 * Class containing water data
 */

@SuppressWarnings("ConstructorWithTooManyParameters")
public class WaterReport {
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
    public WaterReport() {}

    /**
     * Creates water report
     * @param _id             unique id of report
     * @param _reportName     name of report
     * @param lat             latitude location
     * @param lon             longitude location
     * @param _type           kind of water source this is
     * @param _condition      condition of water
     * @param _virusPPM       parts per millions of virus
     * @param _contaminantPPM parts per millions of contaminant
     */
    WaterReport(String _id, String _reportName, String _user, double lat, double lon,
                @Nullable WaterType _type, @Nullable WaterCondition _condition,
                double _virusPPM, double _contaminantPPM) {
        id = _id;
        reportName = _reportName;
        user = _user;
        x = lat;
        y = lon;
        type = _type;
        condition = _condition;
        virusPPM = _virusPPM;
        contaminantPPM = _contaminantPPM;
    }

    /**
     * Creates water report
     * @param _id           unique id of report
     * @param _reportName   name of report
     * @param lat           latitude location
     * @param lon           longitude location
     * @param _type         kind of water source this is in string form
     * @param _condition    condition of water in string form
     * @param _virusPPM       parts per millions of virus
     * @param _contaminantPPM parts per millions of contaminant
     */
    WaterReport(String _id, String _reportName, String _user, double lat, double lon, String _type,
                       String _condition, double _virusPPM, double _contaminantPPM) {
        this(_id, _reportName, _user, lat, lon, WaterType.valueOf(_type),
                WaterCondition.valueOf(_condition), _virusPPM, _contaminantPPM);
    }

    /**
     * Creates simple water report
     * @param _id           unique id of report
     * @param _reportName   name of report
     * @param lat           latitude location
     * @param lon           longitude location
     * @param _type         kind of water source this is in string form
     * @param _condition    condition of water in string form
     */
    WaterReport(String _id, String _reportName, String _user, double lat, double lon, String _type,
                String _condition) {
        this(_id, _reportName, _user, lat, lon, WaterType.valueOf(_type),
                WaterCondition.valueOf(_condition),-1, -1);
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

    void setX(Double x) {
        this.x = x;
    }

    Double getY() {
        return y;
    }

    void setY(Double y) {
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

    public void setVirusPPM(double _virusPPM) {
        virusPPM = _virusPPM;
    }

    public double getContaminantPPM() {
        return contaminantPPM;
    }

    public void setContaminantPPM(double _contaminantPPM) {
        contaminantPPM = _contaminantPPM;
    }

    public int getFormat() {
        if ((virusPPM == -1) && (contaminantPPM == -1)) {
            return 1;
        } else {
            return 2;
        }
    }
}

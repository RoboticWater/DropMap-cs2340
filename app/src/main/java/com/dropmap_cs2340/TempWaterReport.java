package com.dropmap_cs2340;

import java.util.Date;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by johnbritti on 2/24/2017.
 * Temporary water report
 */

public class TempWaterReport {
    private Date date;
    private String id;
    private String username;
    private String uid;
    private Double x;
    private Double y;
    private String type;
    private String condition;

    public TempWaterReport() {}

    public TempWaterReport(String id, Date date, String username, String uid, Double x, Double y, String type, String condition) {
        this.id = id;
        this.date = date;
        this.username = username;
        this.uid = uid;
        this.x = x;
        this.y = y;
        this.type = type;
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public String toString() {
        return date.toString() + "," + username + "," + type + "," + condition;
    }
}

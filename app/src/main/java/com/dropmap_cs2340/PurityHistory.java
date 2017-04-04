package com.dropmap_cs2340;

/**
 * Created by johnbritti on 4/4/17.
 */

public class PurityHistory {
    private double virusPPM;
    private double contaminatePPM;
    PurityHistory(){};
    PurityHistory(double vP, double cP) {
        virusPPM = vP;
        contaminatePPM = cP;
    }

    public double getVirusPPM() {
        return virusPPM;
    }

    public void setVirusPPM(double virusPPM) {
        this.virusPPM = virusPPM;
    }

    public double getContaminatePPM() {
        return contaminatePPM;
    }

    public void setContaminatePPM(double contaminatePPM) {
        this.contaminatePPM = contaminatePPM;
    }
}

package com.dropmap_cs2340;

import java.io.Serializable;

/**
 * Created by arsenelakpa on 2/23/17.
 * Describes the conditions of water sources
 */
public enum WaterCondition implements Serializable {
    Waste, TreatableClear, TreatableMuddy, Potable;

    /**
     * Returns array of enum items
     * @return array of enum names as strings
     */
    public static String[] names() {
        WaterCondition[] states = values();
        String[] names = new String[states.length];

        for (int i = 0; i < states.length; i++) {
            names[i] = states[i].name();
        }

        return names;
    }
}
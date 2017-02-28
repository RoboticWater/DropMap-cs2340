package com.dropmap_cs2340;

import java.io.Serializable;

/**
 * Created by arsenelakpa on 2/23/17.
 */

public enum WaterType implements Serializable {
    Bottled, Well, Stream, Lake, Spring, Other;

    public static String[] names() {
        WaterType[] states = values();
        String[] names = new String[states.length];

        for (int i = 0; i < states.length; i++) {
            names[i] = states[i].name();
        }

        return names;
    }
}
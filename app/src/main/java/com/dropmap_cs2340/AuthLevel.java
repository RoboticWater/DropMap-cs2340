package com.dropmap_cs2340;

import java.io.Serializable;

/**
 * Created by johnbritti on 2/9/17.
 * Enum of the different kinds of users that can interact with the app
 */

public enum AuthLevel implements Serializable {
    User,
    Worker,
    Manager,
    Administrator;

    /**
     * Gets list of enum fields
     * @return string array of enum fields
     */
    public static String[] names() {
        AuthLevel[] states = values();
        String[] names = new String[states.length];

        for (int i = 0; i < states.length; i++) {
            names[i] = states[i].name();
        }

        return names;
    }
}

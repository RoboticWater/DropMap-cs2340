package com.example.johnbritti.dropmap_cs2340;

import java.io.Serializable;

/**
 * Created by johnbritti on 2/9/17.
 */

public enum AuthLevel implements Serializable {
    USER ("User"),
    WORKER ("Worker"),
    MANAGER ("Manager"),
    ADMINISTRATOR ("Administrator");

    private final String name;

    AuthLevel(String name) {
        this.name = name;
    }

    public boolean equals(String o) {
        return name.equals(o);
    }

    @Override
    public String toString() {
        return name;
    }

    public static String[] names() {
        AuthLevel[] states = values();
        String[] names = new String[states.length];

        for (int i = 0; i < states.length; i++) {
            names[i] = states[i].name;
        }

        return names;
    }
}

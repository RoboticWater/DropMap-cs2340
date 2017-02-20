package com.dropmap_cs2340;
import com.firebase.client.Firebase;

/**
 * Created by johnbritti on 2/19/2017.
 * Sets up Firebase connection
 */
public class DropMap extends android.app.Application {
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}

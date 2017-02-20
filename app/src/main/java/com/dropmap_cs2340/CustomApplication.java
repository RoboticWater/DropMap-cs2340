package com.dropmap_cs2340;
import com.firebase.client.Firebase;

/**
 * Created by john britti on 2/19/2017.
 */
import com.firebase.client.Firebase;
public class CustomApplication extends android.app.Application {
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}

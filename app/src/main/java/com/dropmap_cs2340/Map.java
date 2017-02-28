package com.dropmap_cs2340;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Map";

    /**
     * Firebase Hooks
     * Communicates with Firebase authentication and database services
     */
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;

    private Calendar calendar;


    /**
     * UI Hooks
     */
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        auth     = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        };

        calendar = Calendar.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
//        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng loc) {
//                //TODO put this in linked EDIT MAP MARKER activity
//                DatabaseReference reports = database.getReference("waterReports");
//                DatabaseReference childRef = reports.push();
//                TempWaterReport wr = new TempWaterReport(childRef.getKey(), calendar.getTime(), user.getDisplayName(), user.getUid(), loc.latitude, loc.longitude, "Water", "Pretty Good");
//                childRef.setValue(wr);
//                map.addMarker(new MarkerOptions().position(loc).title("Oh shit boi whuddup"));
//                map.moveCamera(CameraUpdateFactory.newLatLng(loc));
//            }
//        });

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.map_marker, null);
                String[] contents = marker.getSnippet().split(",");
                TextView title = (TextView) v.findViewById(R.id.title);
                TextView name = (TextView) v.findViewById(R.id.text_user);
                TextView date = (TextView) v.findViewById(R.id.text_date);
                TextView type = (TextView) v.findViewById(R.id.text_type);
                TextView condition = (TextView) v.findViewById(R.id.text_condition);

                title.setText(marker.getTitle());

                date.setText(contents[0]);
                name.setText(contents[1]);
                type.setText(contents[2]);
                condition.setText(contents[3]);
                return v;
            }
        });

        database.getReference().child("waterReports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            WaterReport wr = snapshot.getValue(WaterReport.class);
                            map.addMarker(new MarkerOptions().position(wr.loc())
                                    .title("Oh shit boi whuddup")
                                    .snippet(wr.toString()));
                            map.moveCamera(CameraUpdateFactory.newLatLng(wr.loc()));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}

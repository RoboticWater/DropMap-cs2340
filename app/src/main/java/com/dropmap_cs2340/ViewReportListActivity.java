package com.dropmap_cs2340;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Austin on 2/23/2017.
 * View all water availability reports as a list and show details
 */

public class ViewReportListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String rid;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    private static String TAG = "ReportList";

    /**
     * Widgets for info
     */
    private Spinner reportSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report_list);

        auth     = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        Log.d("ViewReport", database.toString());
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

        reportSpinner = (Spinner) findViewById(R.id.reports);
        database.getReference().child("waterReports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final List<String> ids = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            WaterReport wr = snapshot.getValue(WaterReport.class);
                            ids.add(wr.getId());
                        }

                        ArrayAdapter<String> reportAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ids);
                        reportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        reportSpinner.setAdapter(reportAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    protected void onStart(){
        super.onStart();
        auth.addAuthStateListener(authListener);
        user = auth.getCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    /**
     * Event handler for when view button is pressed
     * @param view the button
     */
    public void onViewPressed(View view) {
        Intent i = new Intent(getApplicationContext(), ViewWaterReport.class);
        Log.d(TAG, "Sending: " + reportSpinner.getSelectedItem());
        i.putExtra("report_id", (String) reportSpinner.getSelectedItem());
        startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Selected: " + parent.getItemAtPosition(position));
        rid = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        rid = "No Report";
    }
}

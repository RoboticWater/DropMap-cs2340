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

        Button viewReportButton = (Button) findViewById(R.id.viewReportButton);
        viewReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewWaterReport.class);
                intent.putExtra("report_id", rid);
                startActivity(intent);
            }
        });

        reportSpinner = (Spinner) findViewById(R.id.reports);

        ArrayAdapter<String> reportAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, reportList());
        reportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportSpinner.setAdapter(reportAdapter);

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
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Event handler for when view button is pressed
     * @param view the button
     */
    public void onViewPressed(View view) {
        startActivity(new Intent(getApplicationContext(), ViewWaterReport.class, reportSpinner.getSelectedItem())));
    }


    /**
     * Create a List of report names
     */
    public List<String> reportList(){
        final ArrayList<String> out = new ArrayList<String>();
        database.getReference().child("waterReports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            WaterReport wr = snapshot.getValue(WaterReport.class);
                            out.add(wr.getReportID());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        return out;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        rid = parent.getItemAtPosition(position).getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        rid = "No Report";
    }
}

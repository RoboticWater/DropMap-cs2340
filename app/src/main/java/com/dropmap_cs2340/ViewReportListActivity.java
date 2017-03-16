package com.dropmap_cs2340;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

@SuppressWarnings("ChainedMethodCall")
public class ViewReportListActivity extends AppCompatActivity {
    private final String TAG = "ReportList";
    private List<WaterReport> waterReports;

    /**
     * Firebase Hooks
     * Communicates with Firebase authentication and database services
     */
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;

    /**
     * UI Hooks
     */
    private LinearLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report_list);
        mainView = (LinearLayout) findViewById(R.id.main_layout);

        waterReports = new ArrayList<>();

        auth     = FirebaseAuth.getInstance();
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("waterReports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final WaterReport wr = snapshot.getValue(WaterReport.class);
                            Button t = new Button(getApplicationContext());
                            t.setText(wr.getId());
                            t.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openReport(wr.getId());
                                }
                            });
                            mainView.addView(t);
                            waterReports.add(wr);
                        }
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
     * Opens view water report screen with specified water report
     * @param rid  id of water report to fetch from database
     */
    private void openReport(String rid) {
        Intent i = new Intent(getApplicationContext(), ViewWaterReport.class);
        i.putExtra("report_id", rid);
        startActivity(i);
//        finish();
    }

    /**
     * Resets view and adds in reports matching the filter selected
     * Filters- 0:all, 1:source, 2:purity
     * @param type the int representation of the filter
     */
    private void filterReports(int type) {
        mainView.removeAllViews();
        for (final WaterReport wr : waterReports) {
            if ((type == 0) || (type == wr.format())) {
                Button t = new Button(getApplicationContext());
                t.setText(wr.getId());
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openReport(wr.getId());
                    }
                });
                mainView.addView(t);
            }
        }
    }

    /**
     * Event handler for when all reports radio button is selected
     * @param view current view
     */
    public void onAllClicked(View view) {
        filterReports(0);
    }

    /**
     * Event handler for when source reports radio button is selected
     * @param view current view
     */
    public void onSourceClicked(View view) {
        filterReports(1);
    }

    /**
     * Event handler for when purity reports radio button is selected
     * @param view current view
     */
    public void onPurityClicked(View view) {
        filterReports(2);
    }
}

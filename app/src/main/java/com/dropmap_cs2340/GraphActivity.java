package com.dropmap_cs2340;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private List<WaterReport> waterReports;
    private WaterReport selectedReport;


    /**
     * Firebase Hooks
     * Communicates with Firebase authentication and database services
     */
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        waterReports = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("waterReports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final WaterReport wr = snapshot.getValue(WaterReport.class);
                            waterReports.add(wr);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        for (WaterReport w : waterReports) {
            if (w.toString().equals(HistoryForm.getSelectedReport())) {
                selectedReport = w;
            }
        }
        BarChart barChart = (BarChart) findViewById(R.id.graph);

        List<Entry> entries = null;

        //LineDataSet dataset = new LineDataSet(entries, "# of Reports");

        Log.d("APP", "Made dataset with : " );

        //LineData data = new LineData(dataset);
//        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
//
//        dataset.setDrawFilled(true);

       // lineChart.setData(data);
        barChart.animateY(5000);
        if (HistoryForm.getType().equals("virus")) {
            barChart.getDescription().setText("Average Virus PPM per Month");
        } else {
            barChart.getDescription().setText("Average Contaminant PPM per Month");
        }


    }

    private List<Entry> convertDataSetToEntry(WaterReport report) {
        List<Entry> entries = new ArrayList<>();

        entries.add(new Entry(0, (float) report.getContaminantPPM()));

        return entries;
    }
}


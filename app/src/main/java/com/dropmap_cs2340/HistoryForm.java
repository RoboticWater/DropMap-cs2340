package com.dropmap_cs2340;

import android.content.Intent;
import android.icu.util.Calendar;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import java.util.ArrayList;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryForm extends AppCompatActivity {

    private ArrayList<String> years = new ArrayList<>();
    private int curYear = Calendar.getInstance().get(Calendar.YEAR);
    private ArrayList<String> waterReportStrings = new ArrayList<String>();
    private ArrayList<WaterReport> waterReports = new ArrayList<WaterReport>();
    private static String type;
    private static WaterReport selectedReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_form);


        //Submission Form
        FloatingActionButton submitFab = (FloatingActionButton) findViewById(R.id.submit_fab);
        Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        Spinner locSpinnner = (Spinner) findViewById(R.id.locSpinner);
        final RadioButton virus = (RadioButton) findViewById(R.id.VirusButton);
        final RadioButton contaminant = (RadioButton) findViewById(R.id.ConButton);

        virus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contaminant.setChecked(false);
                type = "virus";
            }
        });

        contaminant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virus.setChecked(false);
                type = "contaminant";
            }
        });


        submitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GraphActivity.class));
            }
        });

        for (int i = 1980; i <= curYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter1);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("waterReports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final WaterReport wr = snapshot.getValue(WaterReport.class);
                            waterReportStrings.add(wr.toString());
                            waterReports.add(wr);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, waterReportStrings);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locSpinnner.setAdapter(adapter2);
        String report = (String) locSpinnner.getSelectedItem();

        //Graph





    }

    public static String getType() {
        return type;
    }

}

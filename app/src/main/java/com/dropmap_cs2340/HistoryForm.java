package com.dropmap_cs2340;

import android.content.Intent;
import android.icu.util.Calendar;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
    private int selectedYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_form);


        //Submission Form
        FloatingActionButton submitFab = (FloatingActionButton) findViewById(R.id.submit_fab);
        Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        final Spinner locSpinner = (Spinner) findViewById(R.id.locSpinner);
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




        for (int i = 1980; i <= curYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter1);
        String yearSpinnerSelectedItem = (String) yearSpinner.getSelectedItem();
        selectedYear = Integer.parseInt(yearSpinnerSelectedItem);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("waterReports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final WaterReport wr = snapshot.getValue(WaterReport.class);
                            waterReportStrings.add(wr.getReportName());
                            waterReports.add(wr);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, waterReportStrings);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locSpinner.setAdapter(adapter2);

        submitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String report = (String) locSpinner.getSelectedItem();
                for (WaterReport wr : waterReports) {
                    if (wr.getReportName().equals(report)) {
                        System.out.println("It worked");
                        selectedReport = wr;
                    }
                }
                onGraphReady();
            }
        });


    }

    protected void onGraphReady() {
        BarChart barChart = (BarChart) findViewById(R.id.graph);
        List<BarEntry> entries = new ArrayList<>();
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        xAxis.setValueFormatter(new MyXAxisValueFormatter(months));

        YAxis yAxis = barChart.getAxisLeft();
        barChart.animateY(5000);


        if (HistoryForm.getType().equals("virus")) {
            barChart.getDescription().setText("Average Virus PPM per Month");
            entries.add(new BarEntry(0, 300));
            for (int i = 1; i < 12; i++) {
                entries.add(new BarEntry(i, (float) (300 * Math.random())));
            }

        } else {
            barChart.getDescription().setText("Average Contaminant PPM per Month");
            entries.add(new BarEntry(0, 300));
            for (int i = 1; i < 12; i++) {
                entries.add(new BarEntry(i, (float) (300 * Math.random())));
            }
        }
        BarDataSet dataSet = new BarDataSet(entries, "# of PPM");
        BarData data = new BarData(dataSet);
        barChart.setData(data);

    }
    public static String getType() {
        return type;
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }

//        /** this is only needed if numbers are returned, else return 0 */
//        @Override
//        public int getDecimalDigits() { return 0; }
    }


}

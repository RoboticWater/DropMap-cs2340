package com.dropmap_cs2340;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Displays the history PPM of a location
 */
public class HistoryForm extends AppCompatActivity {

    public static final int GRAPH_ANIMATE_MILLIS = 1000;
    public static final int THE_BEGINNING_OF_TIME = 1970;
    public static final double LOCATION_DEVIATION = 100;

    private RadioButton          contaminantButton;
    private RadioButton          virusButton;
    private EditText             xEdit;
    private EditText             yEdit;

    private List<WaterReport> waterReports;
    private List<String> reportNames;
    private String type = "virus";
    private int currentYear = -1;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_form);

        virusButton       = (RadioButton) findViewById(R.id.buttonVirus);
        contaminantButton = (RadioButton) findViewById(R.id.buttonContaminant);
        xEdit             = (EditText) findViewById(R.id.editLat);
        yEdit             = (EditText) findViewById(R.id.editLon);
        AutoCompleteTextView yearInput = (AutoCompleteTextView) findViewById(R.id.inputYear);

        ArrayList<String> years = new ArrayList<>();
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = THE_BEGINNING_OF_TIME; i <= currentYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearInput.setAdapter(yearAdapter);

        waterReports = new ArrayList<>();
        reportNames = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        database.getReference().child("waterReports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final WaterReport wr = snapshot.getValue(WaterReport.class);
                            reportNames.add(wr.getReportName());
                            waterReports.add(wr);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        yearInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                currentYear = Integer.parseInt(s.toString());
                if ((currentYear >= THE_BEGINNING_OF_TIME) && !isLocationEmpty())  {
                    onGraphReady();
                }
            }
        });

        xEdit.addTextChangedListener(new LocationTextWatcher());
        yEdit.addTextChangedListener(new LocationTextWatcher());
    }

    private class LocationTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            if ((currentYear >= THE_BEGINNING_OF_TIME) && !isLocationEmpty()) {
                onGraphReady();
            }
        }
    }

    protected void onGraphReady() {
        final ScatterChart chart = (ScatterChart) findViewById(R.id.graph);
        final List<Entry> entries = new ArrayList<>();
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
                "Nov", "Dec"};
        xAxis.setValueFormatter(new MyXAxisValueFormatter(months));
        chart.animateY(GRAPH_ANIMATE_MILLIS);

        for (WaterReport wr : waterReports) {
            if (dist(Double.parseDouble(xEdit.getText().toString()),
                    Double.parseDouble(yEdit.getText().toString()),
                    wr.getX(), wr.getY()) > LOCATION_DEVIATION) {
                continue;
            }
            database.getReference().child("reportHistory").child(wr.getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if ("virus".equals(type)) {
                                chart.getDescription().setText("Average Virus PPM per Month");
                            } else {
                                chart.getDescription().setText("Average Contaminant PPM per Month");
                            }
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                PurityHistory pr = snapshot.getValue(PurityHistory.class);
                                long date = Long.parseLong(snapshot.getKey());

                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(date);
                                int month = c.get(Calendar.MONTH);
                                int year = c.get(Calendar.YEAR);
                                if (year == currentYear) {
                                    if ("virus".equals(type)) {
                                        entries.add(new BarEntry(month,
                                                (float) pr.getVirusPPM()));
                                    } else {
                                        entries.add(new BarEntry(month,
                                                (float) pr.getContaminatePPM()));
                                    }
                                }
                            }
                            if (!entries.isEmpty()) {
                                ScatterDataSet dataSet = new ScatterDataSet(entries, "# of PPM");
                                ScatterData data = new ScatterData(dataSet);
                                chart.setData(data);
                            } else {
                                chart.clear();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }

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
    }

    /**
     * Selects virus data to for graph, deselects contaminant radio button, and updates graph
     * @param v view
     */
    public void onVirusClicked(View v) {
        contaminantButton.setChecked(false);
        type = "virus";
        if ((currentYear >= THE_BEGINNING_OF_TIME) && !isLocationEmpty()) {
            onGraphReady();
        }
    }

    /**
     * Selects contaminant data to for graph, deselects virus radio button, and updates graph
     * @param v view
     */
    public void onContaminantClicked(View v) {
        virusButton.setChecked(false);
        type = "contaminant";
        if ((currentYear != -1) && !isLocationEmpty()) {
            onGraphReady();
        }
    }

    /**
     * Determines if location inputs are empty
     * @return boolean whether or not one or more location inputs are empty
     */
    public boolean isLocationEmpty() {
        return !isInteger(xEdit) || !isInteger(yEdit);
    }

    /**
     * whether the string input is an integer or not
     * @return whether the string input is an integer or not
     */
    public boolean isInteger(EditText edit) {
        try {
            int num = Integer.parseInt(edit.getText().toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * returns distance between to points
     * @param x0 first x coordinate
     * @param y0 first y coordinate
     * @param x1 second x coordinate
     * @param y1 second y coordinate
     * @return distance between two points
     */
    public double dist(double x0, double y0, double x1, double y1) {
        return Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1- y0, 2));
    }
}

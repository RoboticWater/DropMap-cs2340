package com.dropmap_cs2340;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryForm extends AppCompatActivity {

    public static final int GRAPH_ANIMATE_MILLIS = 5000;
    public static final int THE_BEGINNING_OF_TIME = 1970;
    public static final int MAX_VIRUS_PPM = 300;
    public static final int MAX_CONTAMINANT_PPM = 300;

    private RadioButton          contaminantButton;
    private RadioButton          virusButton;
    private Spinner              reportSpinner;
    private AutoCompleteTextView yearInput;

    private List<WaterReport> waterReports;
    private ArrayList<String> reportNames;
    private String type = "virus";
    private WaterReport selectedReport;
    private int currentYear = -1;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_form);

        virusButton       = (RadioButton) findViewById(R.id.buttonVirus);
        contaminantButton = (RadioButton) findViewById(R.id.buttonContaminant);
        reportSpinner     = (Spinner)     findViewById(R.id.reportSpinner);
        yearInput         = (AutoCompleteTextView) findViewById(R.id.inputYear);

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
                        makeSpinner();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void makeSpinner() {
        ArrayAdapter<String> reportAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, reportNames);
        reportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportSpinner.setAdapter(reportAdapter);

        reportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                String report = (String) reportSpinner.getSelectedItem();
                for (WaterReport wr : waterReports) {
                    if (wr.getReportName().equals(report)) {
                        selectedReport = wr;
                    }
                }
                if ((currentYear != -1) && (reportSpinner.getSelectedItem() != null)) {
                    onGraphReady();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    protected void onGraphReady() {
        final ScatterChart chart = (ScatterChart) findViewById(R.id.graph);
        final List<Entry> entries = new ArrayList<>();
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        xAxis.setValueFormatter(new MyXAxisValueFormatter(months));

        YAxis yAxis = chart.getAxisLeft();
        chart.animateY(GRAPH_ANIMATE_MILLIS);

        database.getReference().child("reportHistory").child(selectedReport.getId())
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
                            if (year != currentYear) {
                                continue;
                            }
                            if ("virus".equals(type)) {
                                entries.add(new BarEntry(month, (float) pr.getVirusPPM()));
                            } else {
                                entries.add(new BarEntry(month, (float) pr.getContaminatePPM()));
                            }
                        }
                        ScatterDataSet dataSet = new ScatterDataSet(entries, "# of PPM");
                        ScatterData data = new ScatterData(dataSet);
                        chart.setData(data);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


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
        if ((currentYear != -1) && (reportSpinner.getSelectedItem() != null)) {
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
        if ((currentYear != -1) && (reportSpinner.getSelectedItem() != null)) {
            onGraphReady();
        }
    }
}

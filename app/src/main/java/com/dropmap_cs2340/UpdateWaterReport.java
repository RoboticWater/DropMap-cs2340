package com.dropmap_cs2340;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Water report viewing screen
 */
public class UpdateWaterReport extends AppCompatActivity {

    private final String TAG = "UpdateWaterReport";

    /**
     * Firebase Hooks
     */
    private FirebaseAuth     auth;
    private FirebaseUser     user;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authListener;

    /**
     * UI Hooks
     */
    private TextView idText;
    private TextView sourceText;
    private TextView typeText;
    private TextView conditionText;
    private EditText virusValue;
    private EditText contaminantValue;

    private String rid;

    private WaterReport report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_water_report);

        rid = getIntent().getStringExtra("report_id");

        auth     = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        };

        idText  = (TextView) findViewById(R.id.id_text);
        sourceText = (TextView) findViewById(R.id.source_text);
        typeText  = (TextView) findViewById(R.id.type_text);
        conditionText = (TextView) findViewById(R.id.condition_text);
        virusValue = (EditText) findViewById(R.id.virusValue);
        contaminantValue = (EditText) findViewById(R.id.contaminantValue);

        if (rid != null) {
            databaseStuff();
        }


        FloatingActionButton saveFab = (FloatingActionButton) findViewById(R.id.save_fab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent i = new Intent(getApplicationContext(), EditWaterReport.class);
                i.putExtra("report_id", rid);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void databaseStuff() {

        Log.d("ReportView", rid);
        database.getReference().child("waterReports").child(rid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        report = dataSnapshot.getValue(WaterReport.class);
                        rid = report.getId();
                        idText.setText(report.getId());
                        typeText.setText(report.getType());
                        conditionText.setText(report.getCondition());
                        sourceText.setText(report.getX() + ", " + report.getY());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    /**
     * If the input data is valid, updates Firebase data and Auth data
     * (condition)
     */
    private void saveChanges() {
        if (!validateForm()) {
            return;
        }
        DatabaseReference ref;
        DatabaseReference reports = database.getReference("waterReports");
        ref = reports.child(rid);
        String v = virusValue.getText().toString();
        String c = contaminantValue.getText().toString();
        report.setVirusPPM(Double.parseDouble(v));
        report.setContaminantPPM(Double.parseDouble(c));
        ref.setValue(report);
        ref = database.getReference("reportHistory").child(rid)
                .child(Long.toString(System.currentTimeMillis()));
        ref.setValue(new PurityHistory(Double.parseDouble(v), Double.parseDouble(c)));
        finish();
    }

    /**
     * Checks input for valid entries
     * @return whether or not any input is invalid
     */
    private boolean validateForm() {
        boolean valid = true;

        String virus = virusValue.getText().toString();
        String contaminant = contaminantValue.getText().toString();
        Log.d("EditProfile", virus + ", " + contaminant);

        if (TextUtils.isEmpty(virus)) {
            virusValue.setError("This field is required");
            valid = false;
        } else {
            virusValue.setError(null);
        }

        if (TextUtils.isEmpty(contaminant)) {
            contaminantValue.setError("This field is required");
            valid = false;
        } else {
            contaminantValue.setError(null);
        }

        return valid;
    }
}

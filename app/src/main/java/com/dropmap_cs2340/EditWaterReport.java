package com.dropmap_cs2340;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

/**
 * Screen for creating and editing water report forms
 */
public class EditWaterReport extends AppCompatActivity {
    private static final String TAG = "EditWaterReport";

    /**
     * Hooks to UI objects
     */
    private EditText nameEdit;
    private EditText xEdit;
    private EditText yEdit;
    private Spinner  typeSpinner;
    private Spinner  conditionSpinner;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private Location loc;

    /**
     * Firebase Hooks
     * Communicates with Firebase authentication and database services
     */
    private FirebaseAuth     auth;
    private FirebaseUser     user;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authListener;

    private String rid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loc = location;
                Log.d("OoLocationChange", loc.toString());
                xEdit.setText(String.format(Locale.getDefault(), "%f", loc.getLatitude()));
                yEdit.setText(String.format(Locale.getDefault(), "%f", loc.getLongitude()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.v(TAG, "Status changed: " + s);
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.e(TAG, "PROVIDER DISABLED: " + s);
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.e(TAG, "PROVIDER DISABLED: " + s);
            }
        };

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
        setContentView(R.layout.activity_edit_water_report);

        nameEdit = (EditText) findViewById(R.id.input_name);
        xEdit    = (EditText) findViewById(R.id.latEdit);
        yEdit    = (EditText) findViewById(R.id.longEdit);
        typeSpinner      = (Spinner) findViewById(R.id.typeSpinner);
        conditionSpinner = (Spinner) findViewById(R.id.conditionSpinner);


        FloatingActionButton saveFab = (FloatingActionButton) findViewById(R.id.save_fab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, WaterType.names());
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, WaterCondition.names());
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(adapter2);

        rid = getIntent().getStringExtra("report_id");
        if (rid != null) {
            setTitle(R.string.edit_water_report);
            saveFab.setVisibility(View.GONE);
            database.getReference().child("waterReports").child(rid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            WaterReport wr = dataSnapshot.getValue(WaterReport.class);
                            nameEdit.setText(wr.getReportName());
                            xEdit.setText(String.format(Locale.getDefault(), "%f", wr.getX()));
                            yEdit.setText(String.format(Locale.getDefault(), "%f", wr.getY()));
                            typeSpinner.setSelection(WaterType.valueOf(wr.getType()).ordinal());
                            conditionSpinner.setSelection(WaterCondition.valueOf(wr.getCondition())
                                    .ordinal());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting permission");
                ActivityCompat.requestPermissions(EditWaterReport.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
                    locationListener);
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (loc != null) {
            xEdit.setText(String.format(Locale.getDefault(), "%f", loc.getLatitude()));
            yEdit.setText(String.format(Locale.getDefault(), "%f", loc.getLongitude()));
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        rid = getIntent().getStringExtra("report_id");
        if (rid != null) {
            getMenuInflater().inflate(R.menu.menu_edit_report, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveChanges();
                return true;
            case R.id.action_discard:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if ((grantResults.length > 0)
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    ContextCompat.checkSelfPermission(getApplicationContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
                            locationListener);
                }
            }
        }
    }

    /**
     * If the input data is valid, updates Firebase data and Auth data
     * (latitude, longitude, condition)
     */
    private void saveChanges() {
        if (!validateForm()) {
            return;
        }
        DatabaseReference ref;
        DatabaseReference reports = database.getReference("waterReports");
        if (rid == null) {
            ref = reports.push();
            WaterReport wr = new WaterReport();

            wr.setId(ref.getKey());
            wr.setReportName(nameEdit.getText().toString());
            wr.setUser(user.getUid());
            wr.setX(Double.parseDouble(xEdit.getText().toString()));
            wr.setY(Double.parseDouble(yEdit.getText().toString()));
            wr.setType((String) typeSpinner.getSelectedItem());
            wr.setCondition((String) conditionSpinner.getSelectedItem());

            ref.setValue(wr);
        } else {
            ref = reports.child(rid);
            ref.child("x").setValue(xEdit.getText().toString());
            ref.child("y").setValue(yEdit.getText().toString());
            ref.child("type").setValue(typeSpinner.getSelectedItem());
            ref.child("condition").setValue(conditionSpinner.getSelectedItem());
        }
        finish();
    }

    /**
     * Checks input for valid entries
     * @return whether or not any input is invalid
     */
    private boolean validateForm() {
        boolean valid = true;

        String lat = xEdit.getText().toString();
        String lon = yEdit.getText().toString();
        Log.d("EditProfile", lat + ", " + lon);

        if (TextUtils.isEmpty(lat)) {
            xEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            xEdit.setError(null);
        }

        if (TextUtils.isEmpty(lon)) {
            yEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            yEdit.setError(null);
        }

        return valid;
    }
}

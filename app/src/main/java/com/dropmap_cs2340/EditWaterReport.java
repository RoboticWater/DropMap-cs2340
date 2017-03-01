package com.dropmap_cs2340;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditWaterReport extends AppCompatActivity {
    private static final String TAG = "EditWaterReport";

    /**
     * Hooks to UI objects
     */
    private EditText x;
    private EditText y;
    private Spinner typeSpinner;
    private Spinner conditionSpinner;

    /**
     * Firebase Hooks
     * Communicates with Firebase authentication and database services
     */
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        x    = (EditText) findViewById(R.id.latEdit);
        y   = (EditText) findViewById(R.id.longEdit);
        typeSpinner    = (Spinner) findViewById(R.id.typeSpinner);
        conditionSpinner    = (Spinner) findViewById(R.id.conditionSpinner);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, WaterType.names());
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, WaterCondition.names());
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(adapter2);

        String rid = getIntent().getStringExtra("report_Id");
        DatabaseReference mRef = database.getReference("waterReports").child(rid);
        mRef.child("x").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                x.setText(data);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("y").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                y.setText(data);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                typeSpinner.setSelection(WaterType.valueOf(data).ordinal());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("condition").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                conditionSpinner.setSelection(WaterCondition.valueOf(data).ordinal());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
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



    /**
     * If the input data is valid, updates Firebase data and Auth data (latitude, longitude, condition)
     */
    private void saveChanges() {
        String rid = getIntent().getStringExtra("report_Id");
        final DatabaseReference mRef = database.getReference("waterReport").child(rid);
        mRef.child("x").setValue(x.getText().toString());
        mRef.child("y").setValue(y.getText().toString());
        mRef.child("type").setValue(typeSpinner.getSelectedItem());
        mRef.child("condition").setValue(conditionSpinner.getSelectedItem());

        if (user != null) {
            mRef.child("x").setValue(x);
            mRef.child("y").setValue(y);
            finish();
        }
    }

    /**
     * Checks input for valid entries
     * @return whether or not any input is invalid
     */
    private boolean validateForm() {
        //TODO ensure correct validation criteria
        boolean valid = true;

        String lat = x.getText().toString();
        String lon = y.getText().toString();

        if (TextUtils.isEmpty(lat)) {
            x.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            x.setError(null);
        }

        if (TextUtils.isEmpty(lon)) {
            y.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            y.setError(null);
        }

        return valid;
    }
}

package com.dropmap_cs2340;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The form for editing profiles
 */
public class EditProfile extends AppCompatActivity {
    /**
     * Tag for Firebase logging
     */
    private static final String TAG = "EditProfile";

    /**
     * Firebase Hooks
     * Communicates with Firebase authentication and database services
     */
    private FirebaseAuth     auth;
    private FirebaseDatabase database;
    private FirebaseUser     user;
    private FirebaseAuth.AuthStateListener authListener;

    /**
     * UI Hooks
     * Links code to volatile UI elements
     */
    private EditText userEdit;
    private EditText emailEdit;
    private EditText curPassEdit;
    private EditText newPassEdit;
    private Spinner  authLevelSpinner;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        userEdit    = (EditText) findViewById(R.id.user_edit);
        emailEdit   = (EditText) findViewById(R.id.email_edit);
        curPassEdit = (EditText) findViewById(R.id.cur_pass_edit);
        newPassEdit = (EditText) findViewById(R.id.pass_edit);
        authLevelSpinner = (Spinner) findViewById(R.id.auth_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, AuthLevel.names());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        authLevelSpinner.setAdapter(adapter);

        user = auth.getCurrentUser();
        DatabaseReference mRef = database.getReference("users").child(user.getUid());
        mRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                userEdit.setText(data);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                emailEdit.setText(data);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("authLevel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                authLevelSpinner.setSelection(AuthLevel.valueOf(data).ordinal());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Retrieves relevant data from Firebase and sets relevant views
     */
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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

    /**
     * If the input data is valid, updates Firebase data and Auth data (email/password)
     */
    private void saveChanges() {
        if (!validateForm()) return;
        final DatabaseReference mRef = database.getReference("users").child(user.getUid());
        mRef.child("name").setValue(userEdit.getText().toString());
        mRef.child("authLevel").setValue(authLevelSpinner.getSelectedItem());

        final String email = emailEdit.getText().toString();
        final String password = newPassEdit.getText().toString();
        if (TextUtils.isEmpty(password)) {
            finish();
        } else {
            showProgressDialog();
            if (user != null) {
                Log.d(TAG, email + " " + password);
                user.updateEmail(email);
                user.updatePassword(password);
                mRef.child("email").setValue(email);
                mRef.child("password").setValue(password);
                hideProgressDialog();
                finish();
            } else {
            user.reauthenticate(EmailAuthProvider.getCredential(email, password))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.updateEmail(email);
                            user.updatePassword(password);
                            mRef.child("email").setValue(email);
                            mRef.child("password").setValue(password);
                            hideProgressDialog();
                            finish();
                        }
                    });
            }
        }
    }

    /**
     * Ensures all input data is valid otherwise flashes an error and stops update
     * @return whether or not any input is invalid
     */
    private boolean validateForm() {
        //TODO ensure correct validation criteria
        boolean valid = true;

        String name = userEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String curpass = curPassEdit.getText().toString();
        String newpass = newPassEdit.getText().toString();

        if (TextUtils.isEmpty(name)) {
            userEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            userEdit.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            emailEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            emailEdit.setError(null);
        }

        if (TextUtils.isEmpty(curpass) && !TextUtils.isEmpty(newpass)) {
            curPassEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            curPassEdit.setError(null);
        }

        return valid;
    }

    /**
     * Shows saving popup
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.saving));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    /**
     * Hides saving popup
     */
    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

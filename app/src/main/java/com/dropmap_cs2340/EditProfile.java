package com.dropmap_cs2340;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "Firebase";

    //Firebase bits
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    //UI hooks
    private EditText mUserEdit;
    private EditText mEmailEdit;
    private EditText mCurrentPassEdit;
    private EditText mNewPassEdit;
    private Spinner mAuthLevelSpinner;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mUserEdit = (EditText) findViewById(R.id.edit_user);
        mEmailEdit = (EditText) findViewById(R.id.edit_email);
        mCurrentPassEdit = (EditText) findViewById(R.id.edit_current_password);
        mNewPassEdit = (EditText) findViewById(R.id.edit_password);
        mAuthLevelSpinner = (Spinner) findViewById(R.id.spinner_auth_level);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, AuthLevel.names());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAuthLevelSpinner.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
                //TODO switch to activity?
                startActivity(new Intent(EditProfile.this, Profile.class));
                //TODO check what finish does and add it to necessary activities if it does what I think it does
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference mRef = database.getReference("users").child(uid);
        //TODO make this shit happen only once, probably...
        mRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                mUserEdit.setText(data);
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
                mEmailEdit.setText(data);
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
                mAuthLevelSpinner.setSelection(AuthLevel.valueOf(data).ordinal());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void saveChanges() {
        if (!validateForm()) return;
        String uid = mAuth.getCurrentUser().getUid();
        final DatabaseReference mRef = database.getReference("users").child(uid);
        mRef.child("name").setValue(mUserEdit.getText().toString());
        mRef.child("authLevel").setValue(mAuthLevelSpinner.getSelectedItem());

        final String email = mEmailEdit.getText().toString();
        final String password = mNewPassEdit.getText().toString();
        if (password == null || password.length() == 0) return;
        showProgressDialog();
        final FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, email + " " + password);
        user.reauthenticate(EmailAuthProvider.getCredential(email, password))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user.updateEmail(email);
                user.updatePassword(password);
                mRef.child("email").setValue(email);
                mRef.child("password").setValue(password);
            }
        });
    }

    private boolean validateForm() {
        //TODO ensure correct validation criteria
        boolean valid = true;

        String name = mUserEdit.getText().toString();
        String email = mEmailEdit.getText().toString();
        String curpass = mCurrentPassEdit.getText().toString();
        String newpass = mNewPassEdit.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mUserEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            mUserEdit.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            mEmailEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            mEmailEdit.setError(null);
        }

        if (TextUtils.isEmpty(curpass) && !TextUtils.isEmpty(newpass)) {
            mCurrentPassEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            mCurrentPassEdit.setError(null);
        }

        return valid;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}

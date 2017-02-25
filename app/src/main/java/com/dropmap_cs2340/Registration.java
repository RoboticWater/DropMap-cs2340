package com.dropmap_cs2340;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//TODO add offline registration

public class Registration extends AppCompatActivity {

    private static final String TAG = "Registration";

    /**
     * Firebase Hooks
     */
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    /**
     * UI Hooks
     */
    private EditText userEdit;
    private EditText emailEdit;
    private EditText passEdit;
    private Spinner  authSpinner;
    private ProgressDialog progressDialog;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        userEdit    = (EditText) findViewById(R.id.user_edit);
        emailEdit   = (EditText) findViewById(R.id.input_email);
        passEdit    = (EditText) findViewById(R.id.input_password);
        authSpinner = (Spinner)  findViewById(R.id.auth_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, AuthLevel.names());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        authSpinner.setAdapter(adapter);


        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, Login.class));
                finish();
            }
        });
    }

    /**
     * Creates User object with data from the input fields
     */
    private void setUpUser() {
        user = new User();
        user.setName(userEdit.getText().toString());
        user.setEmail(emailEdit.getText().toString());
        user.setPassword(passEdit.getText().toString());
        user.setAuthLevel((String) authSpinner.getSelectedItem());
    }

    /**
     * Creates a new account when "sign up" is pressed
     * @param view current view
     */
    public void onSignUpClicked(View view) {
        createNewAccount(emailEdit.getText().toString(), passEdit.getText().toString());
        showProgressDialog();
    }

    /**
     * Creates a new account with Firebase
     * @param email     user's email
     * @param password  user's password
     */
    private void createNewAccount(String email, String password) {
        if (!validateForm()) return;
        setUpUser();
        Log.d(TAG, "createNewAccount:" + email);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        if (!task.isSuccessful()) {
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            onAuthenticationSuccess(task.getResult().getUser());
                        }
                    }
                });

    }

    /**
     * Signs out any signed in user and signs in user in once
     * they've been created, then plops them onto the main screen
     * @param mUser The user that was just created
     */
    private void onAuthenticationSuccess(FirebaseUser mUser) {
        saveNewUser(mUser.getUid(), user.getName(), user.getEmail(), user.getPassword(), AuthLevel.valueOf((String) authSpinner.getSelectedItem()));
        signOut();
        showProgressDialog();
        auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    /**
     * Creates new User object and passes it to Firebase's database
     * @param userId    user's id
     * @param name      user's username
     * @param email     user's email
     * @param password  user's password
     * @param authLevel user's authentication level
     */
    private void saveNewUser(String userId, String name, String email, String password, AuthLevel authLevel) {
        User user = new User(userId, email, name, password, authLevel);
        DatabaseReference mRef = database.getReference("users");
        mRef.child(userId).setValue(user);
    }

    /**
     * Signs out with Firebase
     */
    private void signOut() {
        auth.signOut();
    }

    private boolean validateForm() {
        boolean valid = true;

        String userEmail = emailEdit.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            emailEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            emailEdit.setError(null);
        }

        String userPassword = passEdit.getText().toString();
        if (TextUtils.isEmpty(userPassword)) {
            passEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            passEdit.setError(null);
        }

        return valid;
    }

    /**
     * Shows registering popup
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.registering));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    /**
     * Hides registering popup
     */
    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

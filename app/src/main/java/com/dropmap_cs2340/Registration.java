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

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Registration extends AppCompatActivity {

    private static final String TAG = "DropMap";

    //Firebase bits
    private Firebase mRef = new Firebase("https://dropmapdb.firebaseio.com/");
    private FirebaseAuth mAuth;

    //Android interface references
    private EditText mUserView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private Spinner mAuthLevelSpinner;
    private ProgressDialog mProgressDialog;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        mUserView = (EditText) findViewById(R.id.user_input);
        mEmailView = (EditText) findViewById(R.id.email_input);
        mPasswordView = (EditText) findViewById(R.id.password_input);
        mAuthLevelSpinner = (Spinner) findViewById(R.id.auth_level_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, AuthLevel.names());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAuthLevelSpinner.setAdapter(adapter);

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignUpClicked();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, LoginScreen.class));
            }
        });
    }

    protected void setUpUser() {
        user = new User();
        user.setUsername(mUserView.getText().toString());
        user.setEmail(mEmailView.getText().toString());
        user.setAuthLevel((String) mAuthLevelSpinner.getSelectedItem());
        user.setPassword(mPasswordView.getText().toString());
    }

    public void onSignUpClicked() {
        createNewAccount(mEmailView.getText().toString(), mPasswordView.getText().toString());
        showProgressDialog();
    }

    private void createNewAccount(String email, String password) {
        Log.d(TAG, "createNewAccount:" + email);
        if (!validateForm()) {
            return;
        }
        //This method sets up a new User by fetching the user entered details.
        setUpUser();
        //This method  method  takes in an email address and password, validates them and then creates a new user
        // with the createUserWithEmailAndPassword method.
        // If the new account was created, the user is also signed in, and the AuthStateListener runs the onAuthStateChanged callback.
        // In the callback, you can use the getCurrentUser method to get the user's account data.

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            onAuthenticationSuccess(task.getResult().getUser());
                        }


                    }
                });

    }

    private void onAuthenticationSuccess(FirebaseUser mUser) {
        // Write new user
        saveNewUser(mUser.getUid(), user.getUsername(), user.getEmail(), user.getPassword(), AuthLevel.valueOf((String) mAuthLevelSpinner.getSelectedItem()));
        signOut();
        // Go to LoginActivity
        startActivity(new Intent(Registration.this, LoginScreen.class));
        finish();
    }

    private void saveNewUser(String userId, String username, String email, String password, AuthLevel authLevel) {
        User user = new User(userId, email, username, password, authLevel);
        mRef.child("users").child(userId).setValue(user);
    }

    private void signOut() {
        mAuth.signOut();
    }

    private boolean validateForm() {
        boolean valid = true;

        String userEmail = mEmailView.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            mEmailView.setError(null);
        }

        String userPassword = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(userPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            mPasswordView.setError(null);
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

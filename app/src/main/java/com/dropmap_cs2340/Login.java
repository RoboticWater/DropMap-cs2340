package com.dropmap_cs2340;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity {

    private static final String TAG = "Login";

    /**
     * Firebase Hooks
     */
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;

    /**
     * UI Hooks
     */
    private EditText emailEdit;
    private EditText passEdit;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = auth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        emailEdit = (EditText) findViewById(R.id.email_edit);
        passEdit  = (EditText) findViewById(R.id.pass_edit);
    }

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

    /**
     * Logs user in with input data
     * @param view current view
     */
    public void onLoginClicked(View view) {
        signIn(emailEdit.getText().toString(), passEdit.getText().toString());
    }

    /**
     * Switches to registration screen
     * @param view current view
     */
    public void onRegisterClicked(View view) {
        startActivity(new Intent(Login.this, Registration.class));
    }

    /**
     * If the input is valid, sign the user in with Firebase and send them to the main screen
     * @param email     user's email
     * @param password  user's password
     */
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    /**
     * Ensures the input data is valid
     * @return whether all the input data is valid
     */
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
     * Shows loading popup
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    /**
     * hides loading popup
     */
    private void hideProgressDialog() {
        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}


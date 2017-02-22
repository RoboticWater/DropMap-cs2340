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

    private static final String TAG = "Firebase";

    //Firebase bits
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

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



        mUserView = (EditText) findViewById(R.id.input_user);
        mEmailView = (EditText) findViewById(R.id.input_email);
        mPasswordView = (EditText) findViewById(R.id.input_password);
        mAuthLevelSpinner = (Spinner) findViewById(R.id.spinner_auth_level);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, AuthLevel.names());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAuthLevelSpinner.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, Login.class));
            }
        });
    }

    public void onStop() {
        super.onStop();
    }

    protected void setUpUser() {
        user = new User();
        user.setName(mUserView.getText().toString());
        user.setEmail(mEmailView.getText().toString());
        user.setAuthLevel((String) mAuthLevelSpinner.getSelectedItem());
        user.setPassword(mPasswordView.getText().toString());
    }

    public void onSignUpClicked(View view) {
        createNewAccount(mEmailView.getText().toString(), mPasswordView.getText().toString());
        showProgressDialog();
    }

    private void createNewAccount(String email, String password) {
        if (!validateForm()) return;
        setUpUser();
        Log.d(TAG, "createNewAccount:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
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

    private void onAuthenticationSuccess(FirebaseUser mUser) {
        saveNewUser(mUser.getUid(), user.getName(), user.getEmail(), user.getPassword(), AuthLevel.valueOf((String) mAuthLevelSpinner.getSelectedItem()));
        signOut();
        //TODO make sure this works
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            String uid = mAuth.getCurrentUser().getUid();
                            intent.putExtra("user_id", uid);
                            startActivity(intent);
                            finish();
                        }

                        hideProgressDialog();
                    }
                });
        startActivity(new Intent(Registration.this, MainActivity.class));
        finish();
    }

    private void saveNewUser(String userId, String name, String email, String password, AuthLevel authLevel) {
        User user = new User(userId, email, name, password, authLevel);
        DatabaseReference mRef = database.getReference("users");
        mRef.child(userId).setValue(user);
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

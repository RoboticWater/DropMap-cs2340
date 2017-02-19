package com.dropmap_cs2340;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private UserRegisterTask mAuthTask = null;

    private EditText mUserView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private Spinner mAuthLevelSpinner;
    private View mProgressView;
    private View mRegistrationFormView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mUserView = (EditText) findViewById(R.id.user_input);
        mEmailView = (EditText) findViewById(R.id.email_input);
        mPasswordView = (EditText) findViewById(R.id.password_input);
        mPasswordConfirmView = (EditText) findViewById(R.id.confirm_password_input);

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, LoginScreen.class));
            }
        });

        mAuthLevelSpinner = (Spinner) findViewById(R.id.auth_level_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, AuthLevel.names());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAuthLevelSpinner.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRegistrationFormView = findViewById(R.id.registration_form);
        mProgressView = findViewById(R.id.registration_progress);
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        mUserView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        String name = mUserView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mPasswordView.getText().toString();
        AuthLevel authLevel = AuthLevel.valueOf((String) mAuthLevelSpinner.getSelectedItem());

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password)) {
            if (!isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            } else if (!password.equals(confirmPassword)) {
                mPasswordConfirmView.setError(getString(R.string.error_pass_mismatch));
                focusView = mPasswordConfirmView;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        } else if (!isUsernameValid(name)) {
            mUserView.setError(getString(R.string.error_invalid_username));
            focusView = mUserView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(name, email, password, authLevel);
            mAuthTask.execute((Void) null);
        }
    }

    private void writeNewUser(String name, String email, String password, AuthLevel authLevel) {
        User user = new User(name, email, password, authLevel);
        mDatabase.child("users").child(Integer.toString(user.getId())).setValue(user);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isUsernameValid(String name) {
        return name.length() > 4;
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mEmail;
        private final String mPassword;
        private final AuthLevel mAuthLevel;

        UserRegisterTask(String name, String email, String password, AuthLevel authLevel) {
            mName = name;
            mEmail = email;
            mPassword = password;
            mAuthLevel = authLevel;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            writeNewUser(mName, mEmail, mPassword, mAuthLevel);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //TODO This bit
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Shows the progress UI and hides the registration form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegistrationFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

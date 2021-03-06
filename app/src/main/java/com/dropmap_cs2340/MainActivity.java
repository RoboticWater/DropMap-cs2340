package com.dropmap_cs2340;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Central screen for app. Holds buttons to everywhere
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";

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
     * Connects to layout items
     */
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth     = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = auth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        };

        welcomeText = (TextView) findViewById(R.id.text_welcome);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditWaterReport.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        user = auth.getCurrentUser();

        DatabaseReference mRef = database.getReference("users").child(user.getUid());
        mRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                welcomeText.setText("Generic Salutations " + data + "!");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                return true;
            case R.id.edit_profile:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Switches to Profile page on press
     * @param view the current view
     */
    public void onProfileClicked(View view) {
        startActivity(new Intent(getApplicationContext(), Profile.class));
    }

    /**
     * Switches to Map page on press
     * @param view the current view
     */
    public void onMapClicked(View view) {
        startActivity(new Intent(getApplicationContext(), Map.class));
    }

    /**
     * Switches to View Reports page on press
     * @param view the current view
     */
    public void onViewReportsClick(View view) {
        startActivity(new Intent(getApplicationContext(), ViewReportListActivity.class));
    }

    /**
     * Switches to Graph page on press
     * @param view the current view
     */
    public void onQualityHistoryGraphClick(View view) {
        startActivity(new Intent(getApplicationContext(), HistoryForm.class));
    }

}

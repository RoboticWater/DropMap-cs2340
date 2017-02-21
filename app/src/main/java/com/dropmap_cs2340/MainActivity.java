package com.dropmap_cs2340;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Firebase";

    //Firebaase bits
    private FirebaseAuth mAuth;

    //UI hooks
    private TextView mWelcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mWelcomeText = (TextView) findViewById(R.id.text_welcome);
        String uid = getIntent().getExtras().getString("user_id");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onProfileClicked(View view) {
        Log.d(TAG, "1");
        Intent intent = new Intent(MainActivity.this, Profile.class);
        String uid = mAuth.getCurrentUser().getUid();
        intent.putExtra("user_id", uid);
        startActivity(intent);
    }

    public void onLogOutClicked(View view) {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, Login.class));
    }
}

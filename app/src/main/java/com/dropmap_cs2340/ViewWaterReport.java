package com.dropmap_cs2340;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

/**
 * Water report viewing screen
 */
public class ViewWaterReport extends AppCompatActivity {

    private final String TAG = "ViewWaterReport";

    /**
     * Firebase Hooks
     */
    private FirebaseAuth     auth;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authListener;

    /**
     * UI Hooks
     */
    private TextView idText;
    private TextView sourceText;
    private TextView typeText;
    private TextView conditionText;
    private TextView virusText;
    private TextView contaminantText;
    private TextView virusTitle;
    private TextView contaminantTitle;

    private String rid;
    private String authLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_water_report);

        rid = getIntent().getStringExtra("report_id");

        FirebaseUser user;
        auth     = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        };

        idText  = (TextView) findViewById(R.id.id_text);
        sourceText = (TextView) findViewById(R.id.source_text);
        typeText  = (TextView) findViewById(R.id.type_text);
        conditionText = (TextView) findViewById(R.id.condition_text);
        virusText = (TextView) findViewById(R.id.virus_text);
        contaminantText = (TextView) findViewById(R.id.contaminant_text);
        virusTitle = (TextView) findViewById(R.id.title_virus);
        contaminantTitle = (TextView) findViewById(R.id.title_contaminant);
        queryDatabase();

        user = auth.getCurrentUser();
        DatabaseReference ref = database.getReference("users").child(user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                authLevel = u.getAuthLevel();
                Button updateReport = (Button) findViewById(R.id.update_water_report);
                if ("Worker".equals(authLevel) || "Manager".equals(authLevel)) {
                    updateReport.setVisibility(View.VISIBLE);
                } else {
                    updateReport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "RESUMING VIEWING...");
        queryDatabase();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent i = new Intent(getApplicationContext(), EditWaterReport.class);
                i.putExtra("report_id", rid);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void queryDatabase() {
        Log.d("ReportView", rid);
        database.getReference().child("waterReports").child(rid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        WaterReport wr = dataSnapshot.getValue(WaterReport.class);
                        rid = wr.getId();
                        idText.setText(wr.getId());
                        typeText.setText(wr.getType());
                        conditionText.setText(wr.getCondition());
                        sourceText.setText(wr.getX() + ", " + wr.getY());
                        if (wr.getVirusPPM() >= 0) {
                            virusText.setText(String.format(Locale.getDefault(), "%f",
                                    wr.getVirusPPM()));
                        } else {
                            virusText.setVisibility(View.GONE);
                            virusTitle.setVisibility(View.GONE);
                        }
                        if (wr.getContaminantPPM() >= 0) {
                            contaminantText.setText(String.format(Locale.getDefault(), "%f",
                                    wr.getContaminantPPM()));
                        } else {
                            contaminantText.setVisibility(View.GONE);
                            contaminantTitle.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    /**
     * Opens update water report screen when the correct button is pressed
     * @param view the view
     */
    public void onUpdateClicked(View view) {
        if ("Worker".equals(authLevel) || "Manager".equals(authLevel)) {
            Intent i = new Intent(getApplicationContext(), UpdateWaterReport.class);
            i.putExtra("report_id", rid);
            startActivity(i);
        } else {
            Toast.makeText(ViewWaterReport.this,
                    "You are not authorized to edit this report.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

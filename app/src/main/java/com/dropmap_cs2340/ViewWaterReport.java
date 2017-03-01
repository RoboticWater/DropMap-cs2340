package com.dropmap_cs2340;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewWaterReport extends AppCompatActivity {

    /**
     * Firebase Hooks
     */
    private FirebaseAuth     auth;
    private FirebaseDatabase database;

    /**
     * UI Hooks
     */
    private TextView idText;
    private TextView sourceText;
    private TextView typeText;
    private TextView conditionText;

    private String rid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_water_report);

        rid = getIntent().getStringExtra("report_id");

        auth     = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        idText  = (TextView) findViewById(R.id.id_text);
        sourceText = (TextView) findViewById(R.id.source_text);
        typeText  = (TextView) findViewById(R.id.type_text);
        conditionText = (TextView) findViewById(R.id.condition_text);
        databaseStuff();
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


    private void databaseStuff() {
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
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}

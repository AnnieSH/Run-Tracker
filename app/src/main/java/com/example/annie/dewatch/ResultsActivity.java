package com.example.annie.dewatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.annie.dewatch.ExerciseDataStructures.ExerciseData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.annie.dewatch.ExercisePathFragment.exerciseData;

public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        context = getApplicationContext();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.results_map);
        mapFragment.getMapAsync(this);

        setSupportActionBar((Toolbar) findViewById(R.id.results_toolbar));
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Results");

        FloatingActionButton fab = findViewById(R.id.results_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLastExercise();
                ResultsActivity.this.finish();
            }
        });

        TextView distText = findViewById(R.id.result_distance_text);
        TextView timeText = findViewById(R.id.result_time_text);
        TextView speedText = findViewById(R.id.result_speed_text);

        distText.setText(String.format(getString(R.string.dist_text), exerciseData.getTotalDist()));

        int min = exerciseData.getTotalTime() / 60;
        int sec = exerciseData.getTotalTime() % 60;
        timeText.setText(String.format(getString(R.string.time_text), min, sec));

        float avgSpeed = 0;
        if (exerciseData.getTotalTime() != 0)
            avgSpeed = (float) (exerciseData.getTotalDist() / exerciseData.getTotalTime()) * 3600;
        speedText.setText(String.format(getString(R.string.speed_text), avgSpeed));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap map = googleMap;

        Polyline path = map.addPolyline(exerciseData.pathOptions);
        path.setPoints(exerciseData.getPathPoints());

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(exerciseData.getPathCentre(), 14.2f));
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Exit and discard current exercise?")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // todo: use db for this
    private void saveLastExercise() {
        saveLogToDb();

        LocalDate currentDate = LocalDate.now();
        String dateString = currentDate.toString();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(ExerciseData.HAS_STATS, true);

        int min = exerciseData.getTotalTime() / 60;
        double speed = 0;
        if (exerciseData.getTotalTime() > 0)
            speed = exerciseData.getTotalDist() / exerciseData.getTotalTime() * 3600;

        editor.putString(ExerciseData.LAST_DATE, dateString);
        editor.putInt(ExerciseData.LAST_TIME, min);
        editor.putFloat(ExerciseData.LAST_DISTANCE, (float) exerciseData.getTotalDist());
        editor.putFloat(ExerciseData.LAST_SPEED, (float) speed);

        if (exerciseData.getTotalDist() > prefs.getFloat(ExerciseData.DISTANCE_RECORD_DISTANCE, -1)) {
            editor.putString(ExerciseData.DISTANCE_RECORD_DATE, dateString);
            editor.putInt(ExerciseData.DISTANCE_RECORD_TIME, min);
            editor.putFloat(ExerciseData.DISTANCE_RECORD_DISTANCE, (float) exerciseData.getTotalDist());
            editor.putFloat(ExerciseData.DISTANCE_RECORD_SPEED, (float) speed);
        }
        if (speed > prefs.getFloat(ExerciseData.SPEED_RECORD_SPEED, -1)) {
            editor.putString(ExerciseData.SPEED_RECORD_DATE, dateString);
            editor.putInt(ExerciseData.SPEED_RECORD_TIME, min);
            editor.putFloat(ExerciseData.SPEED_RECORD_DISTANCE, (float) exerciseData.getTotalDist());
            editor.putFloat(ExerciseData.SPEED_RECORD_SPEED, (float) speed);
        }

        if (exerciseData.getTotalTime() > prefs.getInt(ExerciseData.TIME_RECORD_TIME_SEC, -1)) {
            editor.putString(ExerciseData.TIME_RECORD_DATE, dateString);
            editor.putInt(ExerciseData.TIME_RECORD_TIME_SEC, exerciseData.getTotalTime());
            editor.putInt(ExerciseData.TIME_RECORD_TIME, min);
            editor.putFloat(ExerciseData.TIME_RECORD_DISTANCE, (float) exerciseData.getTotalDist());
            editor.putFloat(ExerciseData.TIME_RECORD_SPEED, (float) speed);
        }

        editor.apply();
    }

    private void saveLogToDb() {
        Log.d("DB", "WRITE");

        Gson gson = new Gson();
        LocalDateTime currentDateTime = LocalDateTime.now();
        exerciseData.setDate(currentDateTime.toString());

        ExerciseDatabaseAdapter dbAdapter = new ExerciseDatabaseAdapter(this.context);
        dbAdapter.openWritable();

        Log.d("DB", "Write result: " +
                dbAdapter.insertExerciseEntry(exerciseData.getDate(), exerciseData.getTotalTime(), exerciseData.getTotalDist(), exerciseData.getAvgSpeed(), gson.toJson(exerciseData.getPathPoints()), gson.toJson(exerciseData.getSpeedGraphPoints())));

        List<ExerciseData> records = dbAdapter.getAllRecordEntries();

        if(records.isEmpty()) {
            Log.d("Records DB", "Is empty, WRITING NEW ENTRIES");
            Log.d("Records WRITES", "RESULT : " + dbAdapter.insertRecord(ExerciseDatabaseAdapter.RecordEntry.RECORD_DISTANCE, exerciseData));
            dbAdapter.insertRecord(ExerciseDatabaseAdapter.RecordEntry.RECORD_TIME, exerciseData);
            dbAdapter.insertRecord(ExerciseDatabaseAdapter.RecordEntry.RECORD_SPEED, exerciseData);
        } else {
            for (ExerciseData record : records) {
                switch (record.getRecordType()) {
                    case ExerciseDatabaseAdapter.RecordEntry.RECORD_DISTANCE:
                        if (exerciseData.getTotalDist() > record.getTotalDist())
                            dbAdapter.updateRecord(ExerciseDatabaseAdapter.RecordEntry.RECORD_DISTANCE, exerciseData);
                        break;
                    case ExerciseDatabaseAdapter.RecordEntry.RECORD_TIME:
                        if (exerciseData.getTotalTime() > record.getTotalTime())
                            dbAdapter.updateRecord(ExerciseDatabaseAdapter.RecordEntry.RECORD_TIME, exerciseData);
                        break;
                    case ExerciseDatabaseAdapter.RecordEntry.RECORD_SPEED:
                        if (exerciseData.getAvgSpeed() > record.getAvgSpeed())
                            dbAdapter.updateRecord(ExerciseDatabaseAdapter.RecordEntry.RECORD_SPEED, exerciseData);
                        break;
                    default:
                        Log.e("DB", "Unknown record type " + record.getRecordType());
                }
            }
        }

        dbAdapter.close();
    }
}

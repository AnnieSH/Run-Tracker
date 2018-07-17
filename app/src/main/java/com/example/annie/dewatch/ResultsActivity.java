package com.example.annie.dewatch;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordRequestWriteObject;
import com.example.annie.dewatch.deWatchClient.deWatchClient;
import com.example.annie.dewatch.deWatchClient.deWatchServer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.annie.dewatch.ExercisePathFragment.exerciseData;

public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG = Config.APP_TAG + ": RESULTS";

    // User
    private User currentUser;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.results_map);
        mapFragment.getMapAsync(this);

        setSupportActionBar((Toolbar) findViewById(R.id.results_toolbar));
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Results");

        currentUser = User.getCurrentUser();

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
        if(exerciseData.getTotalTime() != 0)
            avgSpeed = (float) (exerciseData.getTotalDist() / exerciseData.getTotalTime()) * 3600;
        speedText.setText(String.format(getString(R.string.speed_text), avgSpeed));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        Polyline path = map.addPolyline(exerciseData.pathOptions);
        path.setPoints(exerciseData.getPathPoints());

        if(!exerciseData.getPathPoints().isEmpty())
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(getPathCentre(exerciseData.getPathPoints()), 14.2f));
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

    private void saveLastExercise() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currDateString = df.format(Calendar.getInstance().getTime());
        long currDate = 0;
        try {
            currDate = df.parse(currDateString).getTime();
        } catch (ParseException e) {
            Log.e("Parse exception", e.getMessage());
        }

        String dateString = df.format(Calendar.getInstance().getTime());

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("hasStats", true);

        int min = exerciseData.getTotalTime() / 60;
        double speed = 0;
        if(exerciseData.getTotalTime() > 0)
            speed = exerciseData.getTotalDist() / exerciseData.getTotalTime() * 3600;

        editor.putLong("lastDate", currDate);
        editor.putInt("lastTime", min);
        editor.putFloat("lastDistance", (float) exerciseData.getTotalDist());
        editor.putFloat("lastSpeed", (float) speed);

        if(exerciseData.getTotalDist() > prefs.getFloat("bestDistDist", 0)) {
            editor.putString("bestDistDate", dateString);
            editor.putInt("bestDistTime", min);
            editor.putFloat("bestDistDist", (float) exerciseData.getTotalDist());
            editor.putFloat("bestDistSpeed", (float) speed);
        }
        if(speed > prefs.getFloat("bestSpeedSpeed", 0)) {
            editor.putString("bestSpeedDate", dateString);
            editor.putInt("bestSpeedTime", min);
            editor.putFloat("bestSpeedDist", (float) exerciseData.getTotalDist());
            editor.putFloat("bestSpeedSpeed", (float) speed);
        }

        if(exerciseData.getTotalTime() > prefs.getInt("bestTimeSeconds", 0)) {
            editor.putString("bestTimeDate", dateString);
            editor.putInt("bestTimeSeconds", exerciseData.getTotalTime());
            editor.putInt("bestTimeTime", min);
            editor.putFloat("bestTimeDist", (float) exerciseData.getTotalDist());
            editor.putFloat("bestTimeSpeed", (float) speed);
        }

        editor.apply();
    }

    public static LatLng getPathCentre(List<LatLng> points) {
        LatLng centre;
        double minLat = points.get(0).latitude;
        double minLng = points.get(0).longitude;
        double maxLat = points.get(0).latitude;
        double maxLng = points.get(0).longitude;

        for(LatLng point : points) {
            if(point.latitude < minLat)
                minLat = point.latitude;
            if(point.latitude > maxLat)
                maxLat = point.latitude;

            if(point.longitude < minLng)
                minLng = point.longitude;
            if(point.longitude > maxLng)
                maxLng = point.longitude;
        }

        centre = new LatLng((minLat + maxLat)/2, (minLng + maxLng)/2);

        return centre;
    }

}

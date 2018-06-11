package com.example.annie.dewatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordRequestReadObject;
import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordResponseObject;
import com.example.annie.dewatch.deWatchClient.deWatchClient;
import com.example.annie.dewatch.deWatchClient.deWatchServer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private User currentUser;

    private Button startButton;
    private Button statsButton;
    private Button progressButton;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setSupportActionBar((Toolbar) findViewById(R.id.profile_toolbar));
        ActionBar actionBar = getSupportActionBar();
        context = getApplicationContext();

        startButton = findViewById(R.id.profile_button_start);
        statsButton = findViewById(R.id.profile_button_stats);
        progressButton = findViewById(R.id.profile_button_progress);

        currentUser = User.getCurrentUser();

        actionBar.setTitle(String.format(getString(R.string.welcome_text), currentUser.getFirstName().trim()));

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ExerciseActivity.class);
                startActivity(intent);
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, StatActivity.class);
                startActivity(intent);
            }
        });

        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProgressActivity.class);
                startActivity(intent);
            }
        });

        displayRecords();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile_settings:
                Intent intent = new Intent(ProfileActivity.this, ProfileSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_profile_logout:
                logout();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        setSupportActionBar((Toolbar) findViewById(R.id.profile_toolbar));
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(String.format(getString(R.string.welcome_text), currentUser.getFirstName().trim()));

        displayRecords();
    }

    @Override
    public void onBackPressed() {
    }

    private void displayRecords() {
        TextView lastExercise = findViewById(R.id.last_exercise);
        TextView lastExerciseStats = findViewById(R.id.last_exercise_stats);
        TextView bestSpeedText = findViewById(R.id.best_speed_stats);
        TextView bestDistText = findViewById(R.id.best_dist_stats);
        TextView bestTimeText = findViewById(R.id.best_time_stats);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean("hasStats", false))
            getRecords();
        // Still false after getting records
        if (!prefs.getBoolean("hasStats", false)) {
            lastExercise.setText("You haven't exercised yet!");
            bestDistText.setText("No personal bests yet!");
        } else {
            // TODO: Okay so this is super janky but basically I get the current date then convert
            // to get base date at midnight then convert back and there's probably a better way to do this
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Long date = TimeUnit.MILLISECONDS.toDays(prefs.getLong("lastDate", 0));
            String currDateString = df.format(Calendar.getInstance().getTime());
            int currDate = 0;
            try {
                currDate = (int) TimeUnit.MILLISECONDS.toDays(df.parse(currDateString).getTime());
            } catch (ParseException e) {
                Log.e("Parse exception", e.getMessage());
            }

            Long daysDiff = currDate - date;

            if (daysDiff == 0) {
                lastExercise.setText(String.format(getString(R.string.last_run), "today"));
            } else if (daysDiff == 1) {
                lastExercise.setText(String.format(getString(R.string.last_run), "yesterday"));
            } else
                lastExercise.setText(String.format(getString(R.string.last_run), daysDiff + " days ago"));

            lastExerciseStats.setText(String.format(getString(R.string.last_run_stats),
                    prefs.getInt("lastTime", 0),
                    prefs.getFloat("lastDistance", 0),
                    prefs.getFloat("lastSpeed", 0)));

            bestSpeedText.setText(String.format(getString(R.string.exercise_stat),
                    prefs.getString("bestSpeedDate", ""),
                    prefs.getInt("bestSpeedTime", 0),
                    prefs.getFloat("bestSpeedDist", 0),
                    prefs.getFloat("bestSpeedSpeed", 0)));

            bestDistText.setText(String.format(getString(R.string.exercise_stat),
                    prefs.getString("bestDistDate", ""),
                    prefs.getInt("bestDistTime", 0),
                    prefs.getFloat("bestDistDist", 0),
                    prefs.getFloat("bestDistSpeed", 0)));

            bestTimeText.setText(String.format(getString(R.string.exercise_stat),
                    prefs.getString("bestTimeDate", ""),
                    prefs.getInt("bestTimeTime", 0),
                    prefs.getFloat("bestTimeDist", 0),
                    prefs.getFloat("bestTimeSpeed", 0)));
        }

    }

    private void getRecords() {
        // Date : YYYY-MM-DD
        // Time : HH:MM:SS
        // Time Traveled : HH:MM:SS
        // GPS Coordinates : JSON

        ExerciseRecordRequestReadObject requestData = new ExerciseRecordRequestReadObject(currentUser.getUid(), null);

        deWatchClient client = deWatchServer.createService(deWatchClient.class);
        Call<List<ExerciseRecordResponseObject>> call = client.readExerRecords(requestData);
        call.enqueue(new Callback<List<ExerciseRecordResponseObject>>() {
            @Override
            public void onResponse(Call<List<ExerciseRecordResponseObject>> call, Response<List<ExerciseRecordResponseObject>> response) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final SharedPreferences.Editor editor = prefs.edit();

                if (response.body().size() == 0) {
                    editor.putBoolean("hasStats", false);
                    return;
                }

                String bestSpeedDate = response.body().get(0).getDate();
                String bestSpeedTime = response.body().get(0).getTime_traveled();
                float bestSpeedDist = response.body().get(0).getDistance();
                float bestSpeed = response.body().get(0).getAvg_speed();

                String bestTimeDate = bestSpeedDate;
                String bestTime = bestSpeedTime;
                int bestTimeSec = Integer.parseInt(bestTime.substring(0, 1)) * 60 * 60
                        + Integer.parseInt(bestTime.substring(3, 4)) * 60
                        + Integer.parseInt(bestTime.substring(6, 7));
                float bestTimeDist = bestSpeedDist;
                float bestTimeSpeed = bestSpeed;

                String bestDistDate = bestSpeedDate;
                String bestDistTime = bestSpeedTime;
                float bestDist = bestSpeedDist;
                float bestDistSpeed = bestSpeed;

                for (int i = 0; i < response.body().size(); i++) {
                    String date = response.body().get(i).getDate();
                    float distance = response.body().get(i).getDistance();
                    String time_traveled = response.body().get(i).getTime_traveled();
                    float speed = response.body().get(i).getAvg_speed();
                    String time_travelled = response.body().get(i).getGps_coord();

                    int sec = Integer.parseInt(time_traveled.substring(0, 1)) * 60 * 60
                            + Integer.parseInt(time_traveled.substring(3, 4)) * 60
                            + Integer.parseInt(time_traveled.substring(6, 7));

                    if (distance > bestDist) {
                        bestDistDate = date;
                        bestDistTime = time_traveled;
                        bestDist = distance;
                        bestDistSpeed = speed;
                    }
                    if (sec > bestTimeSec) {
                        bestTimeDate = date;
                        bestTime = time_traveled;
                        bestTimeSec = sec;
                        bestTimeDist = distance;
                        bestTimeSpeed = speed;
                    }
                    if (speed > bestSpeed) {
                        bestSpeedDate = date;
                        bestSpeedTime = time_traveled;
                        bestSpeedDist = distance;
                        bestSpeed = speed;
                    }
                }

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                editor.putString("bestSpeedDate", bestSpeedDate.substring(0, 10));
                editor.putString("bestDistDate", bestDistDate.substring(0, 10));
                editor.putString("bestTimeDate", bestTimeDate.substring(0, 10));

                editor.putInt("bestSpeedTime", Integer.parseInt(bestSpeedTime.substring(0, 1)) * 60
                        + Integer.parseInt(bestSpeedTime.substring(3, 4)));
                editor.putInt("bestDistTime", Integer.parseInt(bestDistTime.substring(0, 1)) * 60
                        + Integer.parseInt(bestDistTime.substring(3, 4)));
                editor.putInt("bestTimeTime", bestTimeSec / 60);

                editor.putFloat("bestSpeedDist", bestSpeedDist);
                editor.putFloat("bestDistDist", bestDist);
                editor.putFloat("bestTimeDist", bestTimeDist);

                editor.putFloat("bestSpeedSpeed", bestSpeed);
                editor.putFloat("bestDistSpeed", bestDistSpeed);
                editor.putFloat("bestTimeSpeed", bestTimeSpeed);

                int size = response.body().size();
                try {
                    Long lastDay = df.parse(response.body().get(size - 1).getDate()).getTime();
                    String timeTravelled = response.body().get(size - 1).getTime_traveled().trim();
                    String minString = timeTravelled.substring(3, 5);
                    Log.e("timeTr", timeTravelled + " " + minString);
                    int min = Integer.parseInt(minString);

                    editor.putLong("lastDate", lastDay);
                    editor.putInt("lastTime", min);
                    editor.putFloat("lastDistance", response.body().get(size - 1).getDistance());
                    editor.putFloat("lastSpeed", response.body().get(size - 1).getAvg_speed());
                } catch (ParseException e) {
                    Log.e("Record parse", e.getMessage());
                }

                editor.putBoolean("hasStats", true);
                editor.apply();
            }

            @Override
            public void onFailure(Call<List<ExerciseRecordResponseObject>> call, Throwable t) {
                Log.e("Server read", t.getMessage());
            }
        });
    }

    private void logout() {
        currentUser.setLoggedOff(getBaseContext());

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);

        finish();
    }
}
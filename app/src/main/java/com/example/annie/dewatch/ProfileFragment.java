package com.example.annie.dewatch;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ProfileFragment extends Fragment {
    View rootView;
    Context context;
    User currentUser;

    Toolbar toolbar;
    ProfileActivity profileActivity;

    private final int LOC_PERMISSION_CODE = 102;

    public ProfileFragment() { }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        currentUser = User.getCurrentUser();
        profileActivity = (ProfileActivity) getActivity();
        setHasOptionsMenu(true);

        Button startButton = rootView.findViewById(R.id.profile_button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(profileActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                   getLocationPermission();
                } else {
                    Intent intent = new Intent(context, ExerciseActivity.class);
                    startActivity(intent);
                }
            }
        });

        displayRecords();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        profileActivity.setActionBarTitle(String.format(getString(R.string.welcome_text), currentUser.getName()));
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent activityIntent;

        switch (item.getItemId()) {
            case R.id.action_profile_settings:
                activityIntent = new Intent(context, ProfileSettingsActivity.class);
                startActivity(activityIntent);
                break;
            case R.id.action_profile_logout:
                currentUser.logout(context);
                activityIntent = new Intent(context, WelcomeActivity.class);
                startActivity(activityIntent);
                profileActivity.finish();
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayRecords();
    }

    // TODO: REFACTOR
    /**
     * Displays last exercise time and best records
     */
    public void displayRecords() {
        TextView lastExercise = rootView.findViewById(R.id.last_exercise);
        TextView lastExerciseStats = rootView.findViewById(R.id.last_exercise_stats);
        TextView bestSpeedText = rootView.findViewById(R.id.best_speed_stats);
        TextView bestDistText = rootView.findViewById(R.id.best_dist_stats);
        TextView bestTimeText = rootView.findViewById(R.id.best_time_stats);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // todo: Once server or local data exists, check for that
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
                    prefs.getInt(ExerciseData.LAST_TIME, 0),
                    prefs.getFloat(ExerciseData.LAST_DISTANCE, 0),
                    prefs.getFloat(ExerciseData.LAST_SPEED, 0)));

            bestSpeedText.setText(String.format(getString(R.string.exercise_stat),
                    prefs.getString(ExerciseData.SPEED_RECORD_DATE, ""),
                    prefs.getInt(ExerciseData.SPEED_RECORD_TIME, 0),
                    prefs.getFloat(ExerciseData.SPEED_RECORD_DISTANCE, 0),
                    prefs.getFloat(ExerciseData.SPEED_RECORD_SPEED, 0)));

            bestDistText.setText(String.format(getString(R.string.exercise_stat),
                    prefs.getString(ExerciseData.DISTANCE_RECORD_DATE, ""),
                    prefs.getInt(ExerciseData.DISTANCE_RECORD_TIME, 0),
                    prefs.getFloat(ExerciseData.DISTANCE_RECORD_DISTANCE, 0),
                    prefs.getFloat(ExerciseData.DISTANCE_RECORD_SPEED, 0)));

            bestTimeText.setText(String.format(getString(R.string.exercise_stat),
                    prefs.getString(ExerciseData.TIME_RECORD_DATE, ""),
                    prefs.getInt(ExerciseData.TIME_RECORD_TIME, 0),
                    prefs.getFloat(ExerciseData.TIME_RECORD_DISTANCE, 0),
                    prefs.getFloat(ExerciseData.TIME_RECORD_SPEED, 0)));
        }

    }

    /**
     * Called when there is no location permission
     *
     * TODO: Handle when the user presses "Don't ask again"
     */
    private void getLocationPermission() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);

        builder.setMessage("GPS permission is needed to track your path!")
                .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(profileActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_PERMISSION_CODE);
                    }
                });

        builder.create().show();
    }
}

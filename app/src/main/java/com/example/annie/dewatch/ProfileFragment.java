package com.example.annie.dewatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import javax.annotation.Nullable;

public class ProfileFragment extends Fragment {
    View rootView;
    Context context;
    User currentUser;

    Toolbar toolbar;

    public ProfileFragment() { }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        currentUser = User.getCurrentUser();
        setHasOptionsMenu(true);

        Button startButton = rootView.findViewById(R.id.profile_button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ExerciseActivity.class);
                startActivity(intent);
            }
        });

        displayRecords();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((ProfileActivity) getActivity()).setActionBarTitle(String.format(getString(R.string.welcome_text), currentUser.getFirstName()));
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile_settings:
                Intent intent = new Intent(context, ProfileSettingsActivity.class);
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
        displayRecords();
    }

    // TODO: REFACTOR
    /**
     * Displays last exercise time and best records
     */
    private void displayRecords() {
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

    private void logout() {
        currentUser.setLoggedOff(context);

        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);

        getActivity().finish();
    }
}

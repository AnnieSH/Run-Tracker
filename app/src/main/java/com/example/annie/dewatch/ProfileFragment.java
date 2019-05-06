package com.example.annie.dewatch;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.annie.dewatch.ExerciseDataStructures.ExerciseData;
import com.example.annie.dewatch.OpenWeatherMap.WeatherData;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

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

        setWeatherData();
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
        profileActivity.setActionBarTitle(String.format(getString(R.string.welcome_text), currentUser.getName()));
        displayRecords();
    }

    private String getDaysSinceLastRunText(SharedPreferences prefs) {
        if (!prefs.getBoolean("hasStats", false))
            return "You haven't exercised yet!";
        else {
            LocalDate currentDate = LocalDate.now();
            LocalDate lastExerciseDate = LocalDate.parse(prefs.getString(ExerciseData.LAST_DATE, ""));

            int daysDiff = currentDate.compareTo(lastExerciseDate);
            String daysAgoString = "yesterday";

            if (daysDiff == 0) {
                daysAgoString = "today";
            } else if (daysDiff > 1) {
                daysAgoString = daysDiff + " days ago";
            }
            return String.format(getString(R.string.last_run), daysAgoString);
        }
    }

    private String getLastExerciseStatsText(SharedPreferences prefs) {
        if (prefs.getBoolean("hasStats", false))
            return String.format(getString(R.string.last_run_stats),
                prefs.getInt(ExerciseData.LAST_TIME, 0),
                prefs.getFloat(ExerciseData.LAST_DISTANCE, 0),
                prefs.getFloat(ExerciseData.LAST_SPEED, 0));
        else return "";
    }

    // TODO: REFACTOR
    /**
     * Displays last exercise time and best records
     */
    public synchronized void displayRecords() {
        TextView lastExercise = rootView.findViewById(R.id.last_exercise);
        TextView lastExerciseStats = rootView.findViewById(R.id.last_exercise_stats);
        TextView bestSpeedText = rootView.findViewById(R.id.best_speed_stats);
        TextView bestDistText = rootView.findViewById(R.id.best_dist_stats);
        TextView bestTimeText = rootView.findViewById(R.id.best_time_stats);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        lastExercise.setText(getDaysSinceLastRunText(prefs));
        lastExerciseStats.setText(getLastExerciseStatsText(prefs));

        ExerciseDatabaseAdapter dbAdapter = new ExerciseDatabaseAdapter(context);
        dbAdapter.openReadable();
        List<ExerciseData> records = dbAdapter.getAllRecordEntries();
        dbAdapter.close();

        if (records.isEmpty()) {
            bestDistText.setText("No personal bests yet!");
        } else {
            for(ExerciseData record : records) {
                String recordValues = String.format(getString(R.string.exercise_stat),
                        record.getDate().substring(0, 10),
                        record.getTotalTime() / 60,
                        record.getTotalTime() % 60,
                        record.getTotalDist(),
                        record.getAvgSpeed());

                switch(record.getRecordType()) {
                    case ExerciseDatabaseAdapter.RecordEntry.RECORD_DISTANCE:
                        bestDistText.setText(recordValues);
                        break;
                    case ExerciseDatabaseAdapter.RecordEntry.COLUMN_NAME_TIME:
                        bestTimeText.setText(recordValues);
                        break;
                    case ExerciseDatabaseAdapter.RecordEntry.COLUMN_NAME_SPEED:
                        bestSpeedText.setText(recordValues);
                        break;
                }
            }
        }
    }

    /**
     * Called when there is no location permission
     */
    private void getLocationPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("GPS permission is needed to track your runs")
                .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(profileActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_PERMISSION_CODE);
                    }
                });

        builder.create().show();
    }

    private void setWeatherData() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Location currentLocation = null;
        try {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if(currentLocation != null) {
            String apiUrl = WeatherData.getCurrentCityWeatherUrl(currentLocation.getLatitude(), currentLocation.getLongitude());
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            StringRequest weatherRequest = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    WeatherData data = gson.fromJson(response, WeatherData.class);
                    TextView weatherTextView = rootView.findViewById(R.id.current_weather_textview);
                    weatherTextView.setText(String.format(getString(R.string.current_weather_display), data.getWeather(), data.getTemperature()));

                    Log.d("WEATHER READ", data.getWeather());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            requestQueue.add(weatherRequest);
        }
    }
}

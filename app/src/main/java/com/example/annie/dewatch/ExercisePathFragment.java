package com.example.annie.dewatch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.annie.dewatch.ExerciseActivity.exGraphFrag;

public class ExercisePathFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap map;
    TextView timeText;
    TextView distText;
    TextView speedText;

    private Timer timer;
    private final int oneSecInMilli = 1000;
    private int timeSec = 0;

    // User
    private User currentUser;

    public static ExerciseData exerciseData;

    public ExercisePathFragment() {
    }

    public static ExercisePathFragment newInstance() {
        ExercisePathFragment fragment = new ExercisePathFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exercise, container, false);

        currentUser = User.getCurrentUser();
        timer = new Timer();
        timer.scheduleAtFixedRate(createTimer(), 0, oneSecInMilli);

        timeText = rootView.findViewById(R.id.timeText);
        distText = rootView.findViewById(R.id.distanceText);
        speedText = rootView.findViewById(R.id.speed_text);

        timeText.setText(String.format(getString(R.string.time_text), 0, 0));
        distText.setText(String.format(getString(R.string.dist_text), 0.0));
        speedText.setText(String.format(getString(R.string.speed_text), 0.0));

        exerciseData = new ExerciseData();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    public void addToPath(LatLng point) {
        exerciseData.path.add(point);
        map.addPolyline(exerciseData.path);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16.2f));
    }

    public void updateDistance(double dist) {
        exerciseData.setDistance(dist);

        distText.setText(String.format(getString(R.string.dist_text), dist));
        speedText.setText(String.format(getString(R.string.speed_text), exerciseData.getSpeedsList().get(exerciseData.getSpeedsList().size() - 1)));

        exGraphFrag.updateSpeedGraph(exerciseData.getTotalTime(), exerciseData.getSpeedsList().get(exerciseData.getSpeedsList().size() - 1));
    }

    public void updateTime(int time) {
        exerciseData.setTime(time);

        int min = exerciseData.getTotalTime() / 60;
        int sec = exerciseData.getTotalTime() % 60;
        timeText.setText(String.format(getString(R.string.time_text), min, sec));
    }

    private TimerTask createTimer() {
        return new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTime(exerciseData.getTotalTime() + 1);
                    }
                });
            }
        };
    }

}
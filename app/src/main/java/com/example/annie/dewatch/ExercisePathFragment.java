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

import static com.example.annie.dewatch.ExerciseActivity.exGraphFrag;

public class ExercisePathFragment extends Fragment implements OnMapReadyCallback {

    private String TAG = Config.APP_TAG + ": EXERCISE";

    GoogleMap map;
    TextView hrText;
    TextView o2Text;
    TextView timeText;
    TextView distText;
    TextView speedText;

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

        hrText = rootView.findViewById(R.id.hrText);
        o2Text = rootView.findViewById(R.id.o2Text);
        timeText = rootView.findViewById(R.id.timeText);
        distText = rootView.findViewById(R.id.distanceText);
        speedText = rootView.findViewById(R.id.speed_text);

        hrText.setText(String.format(getString(R.string.hr_text), 0));
        o2Text.setText(String.format(getString(R.string.o2_text), 0));
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

    public void updateValues(int hr, int o2, double dist, int time) {
        exerciseData.updateData(hr, o2, dist, time);

        hrText.setText(String.format(getString(R.string.hr_text), hr));
        o2Text.setText(String.format(getString(R.string.o2_text), o2));
        distText.setText(String.format(getString(R.string.dist_text), dist));
        speedText.setText(String.format(getString(R.string.speed_text), exerciseData.speedsList.get(exerciseData.speedsList.size() - 1)));

        int min = exerciseData.totalTime / 60;
        int sec = exerciseData.totalTime % 60;
        timeText.setText(String.format(getString(R.string.time_text), min, sec + 1));

        exGraphFrag.updateSpeedGraph(exerciseData.totalTime, exerciseData.speedsList.get(exerciseData.speedsList.size() - 1));
        exGraphFrag.updateHrGraph(exerciseData.totalTime, hr);
        exGraphFrag.updateO2Graph(exerciseData.totalTime, o2);
    }

}
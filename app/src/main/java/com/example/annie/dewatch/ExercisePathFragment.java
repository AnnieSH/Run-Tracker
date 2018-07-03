package com.example.annie.dewatch;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.annie.dewatch.ExerciseActivity.exGraphFrag;

public class ExercisePathFragment extends Fragment implements OnMapReadyCallback {
    private String TAG = "ExercisePathFragment";

    GoogleMap map;
    TextView timeText;
    TextView distText;
    TextView speedText;

    private Timer timer;
    private final int oneSecInMilli = 1000;

    private LocationManager locationManager;

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
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

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

        try {
            map.setMyLocationEnabled(true);
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 16.2f));
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 0, getLocationListener());
            exerciseData.path = map.addPolyline(exerciseData.pathOptions);
        } catch(SecurityException e) {
            Log.e(TAG, "Path fragment opened without permission");
        }
    }

    private LocationListener getLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                addToPath(latLng);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    public void addToPath(LatLng point) {
        exerciseData.getPathPoints().add(point);
        exerciseData.path.setPoints(exerciseData.getPathPoints());

        List<LatLng> points = exerciseData.getPathPoints();
        int pathSize = points.size();
        if(pathSize > 1) {
            double distance = ExerciseData.calculateDistance(points.get(pathSize - 2), points.get(pathSize - 1));
            updateDistance(distance);
        }

        map.moveCamera(CameraUpdateFactory.newLatLng(point));
    }

    public void updateDistance(double dist) {
        exerciseData.setDistance(dist);

        distText.setText(String.format(getString(R.string.dist_text), exerciseData.getTotalDist()));
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

    public void stopTimer() {
        timer.cancel();
    }

}
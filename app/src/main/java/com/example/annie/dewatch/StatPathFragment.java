package com.example.annie.dewatch;

import android.graphics.Color;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.annie.dewatch.ExercisePathFragment.exerciseData;

/**
 * Created by krisley3094 on 17/03/18.
 */

public class StatPathFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private String TAG = Config.APP_TAG + ": STATS_PATH_FRAG";

    private TextView timeTextView;
    private TextView distTextView;
    private TextView avgSpeedTextView;

    private StatData resultDataObject;

    private PolylineOptions pathOpt;
    private PolylineOptions pathOpt1;
    private PolylineOptions pathOpt2;

    private GoogleMap stat_map;

    public StatPathFragment() {
    }

    public static StatPathFragment newInstance(StatData argObject) {
        StatPathFragment fragment = new StatPathFragment();
        Bundle args = new Bundle();
        args.putParcelable("RESULT_DATA_OBJ", argObject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stats_results, container, false);

        resultDataObject = getArguments().getParcelable("RESULT_DATA_OBJ");

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.stat_map);
        mapFragment.getMapAsync(this);

        timeTextView = rootView.findViewById(R.id.stat_time);
        distTextView = rootView.findViewById(R.id.stat_dist);
        avgSpeedTextView = rootView.findViewById(R.id.stat_avg_speed);

        getExerRecord();

        return rootView;
    }

    public void getExerRecord(){
        int time = resultDataObject.getTime();
        int min = time / 60;
        int sec = time % 60;

        timeTextView.setText(String.format(getString(R.string.time_text), min, sec));
        distTextView.setText(String.format(getString(R.string.dist_text), resultDataObject.getDistance()));
        avgSpeedTextView.setText(String.format(getString(R.string.speed_text), resultDataObject.getAvg_speed()));
    }

    public void getPathRecord(){
        pathOpt = new PolylineOptions().
                geodesic(true).
                color(Color.rgb(0, 155, 224)).
                width(10);

        String path = resultDataObject.getGps_coord();
        Type listType = new TypeToken<ArrayList<LatLng>>(){}.getType();
        List<LatLng> pathList = new Gson().fromJson(path, listType);

        pathOpt.addAll(pathList);
        stat_map.addPolyline(pathOpt);
        stat_map.moveCamera(CameraUpdateFactory.newLatLngZoom(ExerciseData.getPathCentre(pathList), 14.2f));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        stat_map = googleMap;
        getPathRecord();
    }
}

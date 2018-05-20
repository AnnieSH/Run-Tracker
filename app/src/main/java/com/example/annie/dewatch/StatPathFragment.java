package com.example.annie.dewatch;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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

/**
 * Created by krisley3094 on 17/03/18.
 */

public class StatPathFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private String TAG = Config.APP_TAG + ": STATS_PATH_FRAG";

    // User
    private User currentUser;

    private TextView timeTextView;
    private TextView distTextView;
    private TextView timeTraveledTextView;
    private TextView avgSpeedTextView;
    private TextView avgHRTextView;
    private TextView avgO2TextView;

    private StatData resultDataObject;

    private PolylineOptions pathOpt;
    private PolylineOptions pathOpt1;
    private PolylineOptions pathOpt2;

    private GoogleMap stat_map;

    StatPathFragment statPathFrag;


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

        currentUser = User.getCurrentUser();

        resultDataObject = getArguments().getParcelable("RESULT_DATA_OBJ");

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.stat_map);
        mapFragment.getMapAsync(this);

        timeTextView = (TextView) rootView.findViewById(R.id.stat_time);
        distTextView = (TextView) rootView.findViewById(R.id.stat_dist);
        timeTraveledTextView = (TextView) rootView.findViewById(R.id.stat_time_traveled);
        avgSpeedTextView = (TextView) rootView.findViewById(R.id.stat_avg_speed);
        avgHRTextView = (TextView) rootView.findViewById(R.id.stat_hr);
        avgO2TextView = (TextView) rootView.findViewById(R.id.stat_o2);

        getExerRecord();

        return rootView;
    }

    public void getExerRecord(){
        timeTextView.setText("Saved Time : " + resultDataObject.getTime());
        distTextView.setText("Distance Traveled : " + String.valueOf(resultDataObject.getDistance()));
        timeTraveledTextView.setText("Time Traveled : " + resultDataObject.getTime_traveled());
        avgSpeedTextView.setText("Average Speed : " + String.valueOf(resultDataObject.getAvg_speed()) + " km/h");
        avgHRTextView.setText("Average HR : " + String.valueOf(resultDataObject.getAvg_hr()) + " BPM");
        avgO2TextView.setText("Average O2 : " + String.valueOf(resultDataObject.getAvg_o2()) + " %");
    }

    public void getPathRecord(){

        pathOpt = new PolylineOptions().
                geodesic(true).
                color(Color.rgb(0, 155, 224)).
                width(10);

        pathOpt1 = new PolylineOptions().
                geodesic(true).
                color(Color.rgb(255, 0, 0)).
                width(10);

        pathOpt2 = new PolylineOptions().
                geodesic(true).
                color(Color.rgb(255, 0, 0)).
                width(10);

        String path = resultDataObject.getGps_coord();

        String speedsJson = resultDataObject.getSpeeds();
        String hrJson = resultDataObject.getHrs();
        String o2Json = resultDataObject.getO2s();
        String timesJson = resultDataObject.getTimes_list();

        Type listType = new TypeToken<ArrayList<LatLng>>(){}.getType();
        List<LatLng> pathList = new Gson().fromJson(path, listType);

        Type timesListType = new TypeToken<ArrayList<Integer>>(){}.getType();
        List<Integer> timesList = new Gson().fromJson(timesJson, timesListType);

        Type speedListType = new TypeToken<ArrayList<Double>>(){}.getType();
        List<Double> speedsList = new Gson().fromJson(speedsJson, speedListType);

        Type hrListType = new TypeToken<ArrayList<Integer>>(){}.getType();
        List<Integer> hrList = new Gson().fromJson(hrJson, hrListType);

        Type o2ListType = new TypeToken<ArrayList<Integer>>(){}.getType();
        List<Integer> o2List = new Gson().fromJson(o2Json, o2ListType);

        pathOpt.addAll(pathList);

//        for(int i = 0; i < pathList.size(); i++){
//            //pathOpt.color(Color.rgb(255, 0, 0));
//            pathOpt.add(pathList.get(i));
//            //stat_map.addPolyline(pathOpt);
//        }

        int countOpt1 = 0;
        int countOpt = 0;

//        Polyline line = stat_map.addPolyline(new PolylineOptions()
//                .add(new LatLng(-37.81319, 144.96298), new LatLng(-31.95285, 115.85734))
//                .width(25)
//                .color(Color.BLUE)
//                .geodesic(true));

//        for(int i = 0; i < pathList.size(); i++){
//            //if(hrList.get(i) > 100){
//            if(i > pathList.size()/2){
//                //pathOpt.color(Color.rgb(255, 0, 0));
//                //pathOpt1.add(pathList.get(i));
//                stat_map.addPolyline(new PolylineOptions()
//                        .add(pathList.get(i))
//                        .geodesic(true)
//                        .color(Color.rgb(255, 0, 0))
//                        .width(10));
//                countOpt1++;
//                //stat_map.addPolyline(pathOpt);
//            }
////            else if(i >= (pathList.size()/2)){
////                //pathOpt.color(Color.rgb(255, 0, 0));
////                pathOpt2.add(pathList.get(i));
////                //stat_map.addPolyline(pathOpt);
////            }
//            else{
//                //pathOpt.add(pathList.get(i));
//                stat_map.addPolyline(new PolylineOptions()
//                        .add(pathList.get(i))
//                        .geodesic(true)
//                        .color(Color.rgb(0, 255, 0))
//                        .width(10));
//                countOpt++;
//            }
//        }

        Log.d(TAG, "pathOpt1 SIZE : " + countOpt1);
        Log.d(TAG, "pathOpt SIZE : " + countOpt);

        stat_map.addPolyline(pathOpt);
        //stat_map.addPolyline(pathOpt1);
        if(!pathList.isEmpty()) {
            LatLng pathCentre = ResultsActivity.getPathCentre(pathList);
            stat_map.moveCamera(CameraUpdateFactory.newLatLngZoom(pathCentre, 15.0f));
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        stat_map = googleMap;
        getPathRecord();
    }

}

package com.example.annie.dewatch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by krisley3094 on 17/03/18.
 */

public class StatGraphFragment extends android.support.v4.app.Fragment {

    private String TAG = Config.APP_TAG + ": STAT_GRAPH";

    private LineGraphSeries speedSeries;

    private StatData resultDataObject;

    List<Integer> timesList;
    List<Double> speedsList;

    public StatGraphFragment() { }


    public static StatGraphFragment newInstance(StatData argObject) {
        StatGraphFragment fragment = new StatGraphFragment();
        Bundle args = new Bundle();
        args.putParcelable("RESULT_DATA_OBJ", argObject);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stats_graph, container, false);

        resultDataObject = getArguments().getParcelable("RESULT_DATA_OBJ");

        GraphView speedGraph = rootView.findViewById(R.id.stats_speed_graph);
        GridLabelRenderer speedRenderer = speedGraph.getGridLabelRenderer();
        speedRenderer.setHorizontalAxisTitle("Time");
        speedRenderer.setVerticalAxisTitle("km/h");
        speedRenderer.setPadding(32);
        speedGraph.setTitle("Speed");

        String speedsJson = resultDataObject.getSpeeds();
        String timesJson = resultDataObject.getTimes_list();

        Type timesListType = new TypeToken<ArrayList<Integer>>(){}.getType();
        timesList = new Gson().fromJson(timesJson, timesListType);

        Type speedListType = new TypeToken<ArrayList<Double>>(){}.getType();
        speedsList = new Gson().fromJson(speedsJson, speedListType);

        speedSeries = new LineGraphSeries<>();
        speedSeries.setDrawDataPoints(true);
        speedSeries.setDataPointsRadius(10);

        speedGraph.addSeries(speedSeries);
        speedGraph.getViewport().setXAxisBoundsManual(true);
        speedGraph.getViewport().setMinX(0);

//        if(timesList.size() > 0) {
//            speedGraph.getViewport().setMaxX(timesList.get(timesList.size()-1));
//        }
//
//        Log.d(TAG, "timesList.size : " + timesList.size());
//        Log.d(TAG, "speedsList size: " + speedsList.size());
//
//        for(int i = 0; i < timesList.size(); i++){
//            updateSpeedGraph(timesList.get(i), speedsList.get(i));
//        }

        return rootView;
    }

    public void updateSpeedGraph(int time, double speed) {
        boolean scroll = time >= 60;

        speedSeries.appendData(new DataPoint(time, speed), scroll, speedsList.size());
    }

}

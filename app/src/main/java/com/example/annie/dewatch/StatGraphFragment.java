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

    private LineGraphSeries hrSeries;
    private LineGraphSeries o2Series;
    private LineGraphSeries speedSeries;

    private StatData resultDataObject;

    List<Integer> timesList;

    List<Double> speedsList;

    List<Integer> hrList;

    List<Integer> o2List;

    public StatGraphFragment() { }


    public static StatGraphFragment newInstance(StatData argObject) {
        StatGraphFragment fragment = new StatGraphFragment();
        Bundle args = new Bundle();
        args.putParcelable("RESULT_DATA_OBJ", argObject);;
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

        GraphView hrGraph = rootView.findViewById(R.id.stats_hr_graph);
        GridLabelRenderer renderer = hrGraph.getGridLabelRenderer();
        renderer.setHorizontalAxisTitle("Time");
        renderer.setVerticalAxisTitle("BPM");
        renderer.setPadding(32);
        hrGraph.setTitle("Heart Rate");

        GraphView o2Graph = rootView.findViewById(R.id.stats_o2_graph);
        GridLabelRenderer o2renderer = o2Graph.getGridLabelRenderer();
        o2renderer.setHorizontalAxisTitle("Time");
        o2renderer.setVerticalAxisTitle("%");
        o2renderer.setPadding(32);
        o2Graph.setTitle("Blood O2");


        String speedsJson = resultDataObject.getSpeeds();
        String hrJson = resultDataObject.getHrs();
        String o2Json = resultDataObject.getO2s();
        String timesJson = resultDataObject.getTimes_list();

        Type timesListType = new TypeToken<ArrayList<Integer>>(){}.getType();
        timesList = new Gson().fromJson(timesJson, timesListType);

        Type speedListType = new TypeToken<ArrayList<Double>>(){}.getType();
        speedsList = new Gson().fromJson(speedsJson, speedListType);

        Type hrListType = new TypeToken<ArrayList<Integer>>(){}.getType();
        hrList = new Gson().fromJson(hrJson, hrListType);

        Type o2ListType = new TypeToken<ArrayList<Integer>>(){}.getType();
        o2List = new Gson().fromJson(o2Json, o2ListType);

        speedSeries = new LineGraphSeries<>();
        speedSeries.setDrawDataPoints(true);
        speedSeries.setDataPointsRadius(10);

        speedGraph.addSeries(speedSeries);
        speedGraph.getViewport().setXAxisBoundsManual(true);
        speedGraph.getViewport().setMinX(0);

        hrSeries = new LineGraphSeries<>();
        hrSeries.setDrawDataPoints(true);
        hrSeries.setDataPointsRadius(10);

        hrGraph.addSeries(hrSeries);
        hrGraph.getViewport().setXAxisBoundsManual(true);
        hrGraph.getViewport().setMinX(0);

        o2Series = new LineGraphSeries<>();
        o2Series.setDrawDataPoints(true);
        o2Series.setDataPointsRadius(10);

        o2Graph.addSeries(o2Series);
        o2Graph.getViewport().setXAxisBoundsManual(true);
        o2Graph.getViewport().setYAxisBoundsManual(true);
        o2Graph.getViewport().setMinX(0);
        o2Graph.getViewport().setMaxY(100);

        if(timesList.size() > 0) {
            speedGraph.getViewport().setMaxX(timesList.get(timesList.size()-1));
            hrGraph.getViewport().setMaxX(timesList.get(timesList.size()-1));
            o2Graph.getViewport().setMaxX(timesList.get(timesList.size()-1));
        }

        Log.d(TAG, "timesList.size : " + timesList.size());
        Log.d(TAG, "speedsList size: " + speedsList.size());
        Log.d(TAG, "hrList size: " + hrList.size());
        Log.d(TAG, "o2List size: " + o2List.size());

        for(int i = 0; i < timesList.size(); i++){
            updateSpeedGraph(timesList.get(i), speedsList.get(i));
            if(!hrList.isEmpty()){
                updateHrGraph(timesList.get(i), hrList.get(i));
            }
            if(!o2List.isEmpty()){
                updateO2Graph(timesList.get(i), o2List.get(i));
            }

        }

        return rootView;
    }

    public void updateHrGraph(int time, int bpm) {
        boolean scroll = time >= 60;

        hrSeries.appendData(new DataPoint(time, bpm), scroll, hrList.size());
    }

    public void updateO2Graph(int time, int o2) {
        boolean scroll = time >= 60;

        o2Series.appendData(new DataPoint(time, o2), scroll, o2List.size());
    }

    public void updateSpeedGraph(int time, double speed) {
        boolean scroll = time >= 60;

        speedSeries.appendData(new DataPoint(time, speed), scroll, speedsList.size());
    }

}

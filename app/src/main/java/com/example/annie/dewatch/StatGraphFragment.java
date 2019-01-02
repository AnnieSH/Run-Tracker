package com.example.annie.dewatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Arrays;
import java.util.List;

/**
 * Created by krisley3094 on 17/03/18.
 */

public class StatGraphFragment extends android.support.v4.app.Fragment {

    private String TAG = Config.APP_TAG + ": STAT_GRAPH";

    private LineGraphSeries speedSeries;

    private StatData resultDataObject;

    public StatGraphFragment() { }


    public static StatGraphFragment newInstance(StatData argObject) {
        StatGraphFragment fragment = new StatGraphFragment();
        Bundle args = new Bundle();
        args.putParcelable(StatData.INTENT_KEY, argObject);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stats_graph, container, false);

        resultDataObject = getArguments().getParcelable(StatData.INTENT_KEY);

        GraphView speedGraph = rootView.findViewById(R.id.stats_speed_graph);
        GridLabelRenderer speedRenderer = speedGraph.getGridLabelRenderer();
        speedRenderer.setHorizontalAxisTitle("Time");
        speedRenderer.setVerticalAxisTitle("km/h");
        speedRenderer.setPadding(32);
        speedGraph.setTitle("Speed");

        Gson gson = new Gson();
        String speedsJson = resultDataObject.getSpeedGraphPoints();
        List<ExerciseData.SpeedPoint> speedsList = Arrays.asList(gson.fromJson(speedsJson, ExerciseData.SpeedPoint[].class));

        speedSeries = new LineGraphSeries<>();
        speedSeries.setDrawDataPoints(true);
        speedSeries.setDataPointsRadius(10);

        speedGraph.addSeries(speedSeries);
        speedGraph.getViewport().setXAxisBoundsManual(true);
        speedGraph.getViewport().setMinX(0);

        if(speedsList.size() > 0) {
            speedGraph.getViewport().setMaxX(speedsList.get(speedsList.size()-1).getTime());
        }

        for(ExerciseData.SpeedPoint point : speedsList){
            updateSpeedGraph(point.getTime(), point.getSpeed(), speedsList.size());
        }

        return rootView;
    }

    public void updateSpeedGraph(int time, double speed, int maxGraphSize) {
        boolean scroll = time >= 60;

        speedSeries.appendData(new DataPoint(time, speed), scroll, maxGraphSize);
    }

}

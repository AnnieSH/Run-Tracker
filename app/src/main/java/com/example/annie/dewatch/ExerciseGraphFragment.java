package com.example.annie.dewatch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Annie on 2018-03-17.
 *
 * Real time graphs
 */

public class ExerciseGraphFragment extends Fragment {

    private LineGraphSeries hrSeries;
    private LineGraphSeries o2Series;
    private LineGraphSeries speedSeries;

    public ExerciseGraphFragment() { }


    public static ExerciseGraphFragment newInstance() {
        ExerciseGraphFragment fragment = new ExerciseGraphFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exercise_graphs, container, false);

        GraphView speedGraph = rootView.findViewById(R.id.speed_graph);
        GridLabelRenderer speedRenderer = speedGraph.getGridLabelRenderer();
        speedRenderer.setHorizontalAxisTitle("Time");
        speedRenderer.setVerticalAxisTitle("km/h");
        speedRenderer.setPadding(32);
        speedGraph.setTitle("Speed");

        speedSeries = new LineGraphSeries<>();
        speedGraph.addSeries(speedSeries);
        speedGraph.getViewport().setXAxisBoundsManual(true);
        speedGraph.getViewport().setMinX(0);
        speedGraph.getViewport().setMaxX(60);

        return rootView;
    }

    public void updateSpeedGraph(int time, double speed) {
        boolean scroll = time >= 60;

        speedSeries.appendData(new DataPoint(time, speed), scroll, 60);
    }
}

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

        GraphView hrGraph = rootView.findViewById(R.id.hr_graph);
        GridLabelRenderer renderer = hrGraph.getGridLabelRenderer();
        renderer.setHorizontalAxisTitle("Time");
        renderer.setVerticalAxisTitle("BPM");
        renderer.setPadding(32);
        hrGraph.setTitle("Heart Rate");

        GraphView o2Graph = rootView.findViewById(R.id.o2_graph);
        GridLabelRenderer o2renderer = o2Graph.getGridLabelRenderer();
        o2renderer.setHorizontalAxisTitle("Time");
        o2renderer.setVerticalAxisTitle("%");
        o2renderer.setPadding(32);
        o2Graph.setTitle("Blood O2");

        speedSeries = new LineGraphSeries<>();
        speedGraph.addSeries(speedSeries);
        speedGraph.getViewport().setXAxisBoundsManual(true);
        speedGraph.getViewport().setMinX(0);
        speedGraph.getViewport().setMaxX(60);

        hrSeries = new LineGraphSeries<>();
        hrGraph.addSeries(hrSeries);
        hrGraph.getViewport().setXAxisBoundsManual(true);
        hrGraph.getViewport().setMinX(0);
        hrGraph.getViewport().setMaxX(60);

        o2Series = new LineGraphSeries<>();
        o2Graph.addSeries(o2Series);
        o2Graph.getViewport().setXAxisBoundsManual(true);
        o2Graph.getViewport().setMinX(0);
        o2Graph.getViewport().setMaxX(60);
        o2Graph.getViewport().setMaxY(100);
        o2Graph.getViewport().setMaxY(60);

        return rootView;
    }

    public void updateHrGraph(int time, int bpm) {
        boolean scroll = time >= 60;

        hrSeries.appendData(new DataPoint(time, bpm), scroll, 60);
    }

    public void updateO2Graph(int time, int o2) {
        boolean scroll = time >= 60;

        o2Series.appendData(new DataPoint(time, o2), scroll, 60);
    }

    public void updateSpeedGraph(int time, double speed) {
        boolean scroll = time >= 60;

        speedSeries.appendData(new DataPoint(time, speed), scroll, 60);
    }
}

package com.example.annie.dewatch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ProgressFragment extends Fragment {
    Context context;
    User currentUser;

    private LineGraphSeries distSeries;
    private LineGraphSeries timeSeries;
    private LineGraphSeries speedSeries;

    private GraphView distGraph;
    private GraphView timeGraph;
    private GraphView speedGraph;

    int listSize;

    public ProgressFragment() { }

    public static ProgressFragment newInstance() {
        ProgressFragment fragment = new ProgressFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_progress, container, false);
        context = getContext();
        currentUser = User.getCurrentUser();
        setHasOptionsMenu(true);

        distGraph = rootView.findViewById(R.id.progress_dist_graph);
        graphSetup(distGraph, "Distance", "Exercises", "km");

        timeGraph = rootView.findViewById(R.id.progress_time_graph);
        graphSetup(timeGraph, "Time", "Exercises", "min");

        speedGraph = rootView.findViewById(R.id.progress_speed_graph);
        graphSetup(speedGraph, "Speed", "Exercises", "km/h");

        distSeries = new LineGraphSeries<>();
        distGraph.addSeries(distSeries);

        timeSeries = new LineGraphSeries<>();
        timeGraph.addSeries(timeSeries);

        speedSeries = new LineGraphSeries<>();
        speedGraph.addSeries(speedSeries);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((ProfileActivity) getActivity()).setActionBarTitle("Progress");
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void graphSetup(GraphView graph, String title, String x, String y) {
        GridLabelRenderer renderer = graph.getGridLabelRenderer();
        renderer.setHorizontalAxisTitle(x);
        renderer.setVerticalAxisTitle(y);
        graph.setTitle(title);
        graph.setTitleTextSize(50);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setMinY(0);
        graph.getGridLabelRenderer().setHumanRounding(true);
    }
}

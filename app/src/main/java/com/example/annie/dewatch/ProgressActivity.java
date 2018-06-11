package com.example.annie.dewatch;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordRequestReadObject;
import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordResponseObject;
import com.example.annie.dewatch.deWatchClient.deWatchClient;
import com.example.annie.dewatch.deWatchClient.deWatchServer;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressActivity extends AppCompatActivity {
    User currentUser;

    private LineGraphSeries distSeries;
    private LineGraphSeries timeSeries;
    private LineGraphSeries speedSeries;

    private GraphView distGraph;
    private GraphView timeGraph;
    private GraphView speedGraph;

    int listSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Toolbar toolbar = findViewById(R.id.progress_toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Overall Progress");

        currentUser = User.getCurrentUser();

        distGraph = findViewById(R.id.progress_dist_graph);
        graphSetup(distGraph, "Distance", "Exercises", "km");

        timeGraph = findViewById(R.id.progress_time_graph);
        graphSetup(timeGraph, "Time", "Exercises", "min");

        speedGraph = findViewById(R.id.progress_speed_graph);
        graphSetup(speedGraph, "Speed", "Exercises", "km/h");

        distSeries = new LineGraphSeries<>();
        distGraph.addSeries(distSeries);

        timeSeries = new LineGraphSeries<>();
        timeGraph.addSeries(timeSeries);

        speedSeries = new LineGraphSeries<>();
        speedGraph.addSeries(speedSeries);

        attemptRecordRead();
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

    private void attemptRecordRead() {
        ExerciseRecordRequestReadObject requestData = new ExerciseRecordRequestReadObject(currentUser.getUid(), null);

        deWatchClient client = deWatchServer.createService(deWatchClient.class);
        Call<List<ExerciseRecordResponseObject>> call = client.readExerRecords(requestData);
        call.enqueue(new Callback<List<ExerciseRecordResponseObject>>() {
            @Override
            public void onResponse(Call<List<ExerciseRecordResponseObject>> call, Response<List<ExerciseRecordResponseObject>> response) {
                listSize = response.body().size();

                if(listSize == 0)
                    return;

                for (int i = 0; i < listSize; i++) {
                    float distance = response.body().get(i).getDistance();
                    String time_traveled = response.body().get(i).getTime_traveled();
                    float avg_speed = response.body().get(i).getAvg_speed();
                    short avg_hr = response.body().get(i).getAvg_hr();
                    short avg_o2 = response.body().get(i).getAvg_o2();

                    int min = Integer.parseInt(time_traveled.substring(0,1)) * 60 + Integer.parseInt(time_traveled.substring(3,4));

                    distSeries.appendData(new DataPoint(i + 1, distance), false, listSize);
                    timeSeries.appendData(new DataPoint(i + 1, min), false, listSize);
                    speedSeries.appendData(new DataPoint(i + 1, avg_speed), false, listSize);
                }

                int minX;
                if(listSize < 10)
                    minX = 0;
                else
                    minX = listSize - 10;

                distGraph.getViewport().setMaxY(distSeries.getHighestValueY() + 0.5);
                distGraph.getViewport().setMinX(minX);
                distGraph.getViewport().setMaxX(listSize);

                timeGraph.getViewport().setMaxY(timeSeries.getHighestValueY() + 1);
                timeGraph.getViewport().setMinX(minX);
                timeGraph.getViewport().setMaxX(listSize);

                speedGraph.getViewport().setMaxY(speedSeries.getHighestValueY() + 0.1);
                speedGraph.getViewport().setMinX(minX);
                speedGraph.getViewport().setMaxX(listSize);
            }

            @Override
            public void onFailure(Call<List<ExerciseRecordResponseObject>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Server is down", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}

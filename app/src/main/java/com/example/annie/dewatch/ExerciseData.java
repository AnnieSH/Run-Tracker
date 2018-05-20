package com.example.annie.dewatch;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Annie on 2018-03-08.
 */

public class ExerciseData {

    private String TAG = Config.APP_TAG + ": EXERCISE_DATA";

    public double avgHr;
    public double avgO2;
    private int numValues = 0;
    public double totalDist;
    public int totalTime;
    public PolylineOptions path;
    public List<LatLng> pathPoints;
    public ArrayList<Integer> hrsList;
    public ArrayList<Integer> o2sList;
    public ArrayList<Float> speedsList;
    private ArrayList<Double> distList;
    public ArrayList<Integer> timeList;

    private int last_time = 0;
    private int lastTotalTime = 0;

    public ExerciseData() {
        avgHr = 0;
        avgO2 = 0;
        totalDist = 0.0;
        totalTime = 0;
        pathPoints = null;
        path = new PolylineOptions().
                geodesic(true).
                color(Color.rgb(0, 155, 224)).
                width(10);
        distList = new ArrayList<>();
        hrsList = new ArrayList<>();
        o2sList = new ArrayList<>();
        speedsList = new ArrayList<>();
        timeList = new ArrayList<>();
    }

    private void updateAverages(int hr, int o2) {
        hrsList.add(hr);
        o2sList.add(o2);

        if(hr == 0 || o2 == 0)
            return;

        numValues++;
        avgHr = avgHr + (hr - avgHr) / numValues;
        avgO2 = avgO2 + (o2 - avgO2) / numValues;
    }

    public void updateData(int hr, int o2, double dist, int time) {
        if(time < 1520000432)
            return;

        if (last_time > 0 && time >= last_time) {
            totalTime +=  time - last_time;
        }

        int timeElapsed = totalTime - lastTotalTime;
        double distCovered = dist - totalDist;

        totalDist = dist;
        last_time = time;
        lastTotalTime = totalTime;

        Log.e("time input",Integer.toString(time));
        Log.e("time", Integer.toString(totalTime));

        timeList.add(totalTime); // kriz

        updateAverages(hr, o2);

        distList.add(distCovered);

        if(timeElapsed != 0) {
            speedsList.add((float) (distCovered / timeElapsed) * 3600);
        } else {
            distList.add(0.0);
            speedsList.add((float) 0.0);
        }

        Log.e("Distance", Double.toString(distList.get(distList.size()-1)));
        Log.e("Speed", Double.toString(speedsList.get(speedsList.size() - 1)));
        Log.e("Time elapsed", Integer.toString(timeElapsed));
    }
}

package com.example.annie.dewatch;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Annie on 2018-03-08.
 */

public class ExerciseData {
    private double totalDist;
    private int totalTime; // in seconds
    private double avgSpeed;
    public PolylineOptions path;
    private List<LatLng> pathPoints;
    private ArrayList<Double> speedsList;
    private ArrayList<Double> distList;
    public ArrayList<Integer> timeList;

    ExerciseData() {
        totalDist = 0;
        totalTime = 0;
        avgSpeed = 0;
        setPathPoints(null);
        path = new PolylineOptions().
                geodesic(true).
                color(Color.rgb(0, 155, 224)).
                width(10);
        distList = new ArrayList<>();
        speedsList = new ArrayList<>();
        timeList = new ArrayList<>();
    }

    private double updateAverage(double stat, double average, ArrayList<Double> statList) {
        statList.add(stat);

        if(stat == 0)
            return average;
        else {
            return average + (stat - average) / statList.size();
        }
    }

    private double updateTotal(double stat, ArrayList<Double> statList) {
        statList.add(stat);
        return stat;
    }

    private int updateTotal(int stat, ArrayList<Integer> statList) {
        statList.add(stat);
        return stat;
    }

    private double calculateSpeed(double dist, double time) {
        if(time == 0) {
            return 0;
        } else {
            return dist / time;
        }
    }

    public void updateData(double dist, int time) {
        totalDist = updateTotal(dist, distList);
        totalTime = updateTotal(time, timeList);
        double currSpeed = calculateSpeed(totalDist, totalTime);

        avgSpeed = updateAverage(currSpeed, avgSpeed, speedsList);
    }

    public double getTotalDist() {
        return totalDist;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public List<LatLng> getPathPoints() {
        return pathPoints;
    }

    public void setPathPoints(List<LatLng> pathPoints) {
        this.pathPoints = pathPoints;
    }

    public ArrayList<Double> getSpeedsList() {
        return speedsList;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }
}

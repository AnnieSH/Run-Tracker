package com.example.annie.dewatch.ExerciseDataStructures;

public class SpeedPoint extends GraphDataPoint {
    private int time;
    private double speed;

    public int getTime() {
        return time;
    }

    public double getSpeed() {
        return speed;
    }

    SpeedPoint(int time, double speed) {
        super(time, speed);
        this.time = getX();
        this.speed = getY();
    }
}

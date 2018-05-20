package com.example.annie.dewatch.deWatchClient.Classes;

/**
 * Created by krisley3094 on 11/03/18.
 */

public class ExerciseRecordResponseObject {

    private String date;
    private String time;
    private float distance;
    private String time_traveled;
    private float avg_speed;
    private short avg_hr;
    private short avg_o2;
    private String gps_coord;
    private String speeds;
    private String hrs;
    private String o2s;
    private String time_list;

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public float getDistance() {
        return distance;
    }

    public String getTime_traveled() {
        return time_traveled;
    }

    public float getAvg_speed() {
        return avg_speed;
    }

    public short getAvg_hr() {
        return avg_hr;
    }

    public short getAvg_o2() {
        return avg_o2;
    }

    public String getGps_coord() {
        return gps_coord;
    }

    public String getSpeeds() { return speeds; }

    public String getHrs() { return hrs; }

    public String getO2s() { return o2s; }

    public String getTime_list() { return time_list; }
}

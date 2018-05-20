package com.example.annie.dewatch.deWatchClient.Classes;

/**
 * Created by krisley3094 on 11/03/18.
 */

public class ExerciseRecordRequestWriteObject {

    private String uid;
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

    public ExerciseRecordRequestWriteObject(String uid, String date, String time, float distance,
                                            String time_traveled, float avg_speed,
                                            short avg_hr, short avg_o2, String gps_coord,
                                            String speeds, String hrs, String o2s, String time_list) {
        this.uid = uid;
        this.date = date;
        this.time = time;
        this.distance = distance;
        this.time_traveled = time_traveled;
        this.avg_speed = avg_speed;
        this.avg_hr = avg_hr;
        this.avg_o2 = avg_o2;
        this.gps_coord = gps_coord;
        this.speeds = speeds;
        this.hrs = hrs;
        this.o2s = o2s;
        this.time_list = time_list;
    }
}

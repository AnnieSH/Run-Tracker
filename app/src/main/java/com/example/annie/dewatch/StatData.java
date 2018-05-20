package com.example.annie.dewatch;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by krisley3094 on 12/03/18.
 */

public class StatData implements Parcelable {

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
    private String times_list;
    //private PolylineOptions path;

    public StatData(String date, String time, float distance, String time_traveled,
                    float avg_speed, short avg_hr, short avg_o2, String gps_coord,
                    String speeds, String hrs, String o2s, String times_list) {
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
        this.times_list = times_list;
    }

    public StatData(Parcel in) {
        this.date = in.readString();
        this.time = in.readString();
        this.distance = in.readFloat();
        this.time_traveled = in.readString();
        this.avg_speed = in.readFloat();
        this.avg_hr = (short) in.readInt();
        this.avg_o2 = (short) in.readInt();
        this.gps_coord = in.readString();
        this.speeds = in.readString();
        this.hrs = in.readString();
        this.o2s = in.readString();
        this.times_list = in.readString();
    }

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

    public String getTimes_list() { return times_list; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeFloat(this.distance);
        dest.writeString(this.time_traveled);
        dest.writeFloat(this.avg_speed);
        dest.writeInt(this.avg_hr);
        dest.writeInt(this.avg_o2);
        dest.writeString(this.gps_coord);
        dest.writeString(this.speeds);
        dest.writeString(this.hrs);
        dest.writeString(this.o2s);
        dest.writeString(this.times_list);

    }

    public static final Parcelable.Creator<StatData> CREATOR = new Parcelable.Creator<StatData>() {
        public StatData createFromParcel(Parcel in) {
            return new StatData(in);
        }

        @Override
        public StatData[] newArray(int i) {
            return new StatData[0];
        }

    };
}

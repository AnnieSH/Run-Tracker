package com.example.annie.dewatch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by krisley3094 on 12/03/18.
 */

public class StatData implements Parcelable {
    public static final String INTENT_KEY = "RESULT_DATA_OBJECT";

    private String date;
    private int time;
    private double distance;
    private double avg_speed;
    private String gpsCoordinates;
    private String speedGraphPoints;

    public StatData(String date, int time, double distance, double avg_speed, String gpsCoordinates, String speedGraphPoints) {
        this.date = date;
        this.time = time;
        this.distance = distance;
        this.avg_speed = avg_speed;
        this.gpsCoordinates = gpsCoordinates;
        this.speedGraphPoints = speedGraphPoints;
    }

    public StatData(Parcel in) {
        this.date = in.readString();
        this.time = in.readInt();
        this.distance = in.readDouble();
        this.avg_speed = in.readDouble();
        this.gpsCoordinates = in.readString();
        this.speedGraphPoints = in.readString();
    }

    public String getDate() {
        return date;
    }

    public int getTime() {
        return time;
    }

    public double getDistance() {
        return distance;
    }

    public double getAvg_speed() {
        return avg_speed;
    }

    public String getGpsCoordinates() {
        return gpsCoordinates;
    }

    public String getSpeedGraphPoints() {
        return speedGraphPoints;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeInt(this.time);
        dest.writeDouble(this.distance);
        dest.writeDouble(this.avg_speed);
        dest.writeString(this.gpsCoordinates);
        dest.writeString(this.speedGraphPoints);
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

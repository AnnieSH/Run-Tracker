package com.example.annie.dewatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public final class ExerciseDatabaseAdapter {
    private Context context;
    private ExerciseDataDbHelper dbHelper;
    private SQLiteDatabase db;

    public ExerciseDatabaseAdapter(Context context) {
        this.context = context;
        dbHelper = new ExerciseDataDbHelper(context);
    }

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Exercises.db";

    public static final String TABLE_NAME = "exercise";
    public static final String COLUMN_NAME_EXERCISE_NUM = "exerciseNum";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_DISTANCE = "distance";
    public static final String COLUMN_NAME_SPEED = "speed";
    public static final String COLUMN_NAME_COORDINATES = "coordinates";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS" + TABLE_NAME + " (" +
                    "id" + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_DATE + " TEXT," +
                    COLUMN_NAME_TIME + " INTEGER," +
                    COLUMN_NAME_DISTANCE + " REAL," +
                    COLUMN_NAME_SPEED + " REAL," +
                    COLUMN_NAME_COORDINATES + " TEXT)";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static class ExerciseDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "exercise";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_SPEED = "speed";
        public static final String COLUMN_NAME_COORDINATES = "coordinates";
    }

    public static class SpeedRecordEntry implements BaseColumns {
        public static final String TABLE_NAME = "exercise";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_SPEED = "speed";
        public static final String COLUMN_NAME_COORDINATES = "coordinates";
    }

    public ExerciseDatabaseAdapter openWritable() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public ExerciseDatabaseAdapter openReadable() throws SQLException {
        db = dbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    /**
     *
     * @param date
     * @param time
     * @param distance
     * @param speed
     * @param coordinates
     * @return Result from db insert
     */
    public long insertEntry(String date, int time, double distance, double speed, String coordinates) {
        ContentValues newValues = new ContentValues();
        newValues.put(COLUMN_NAME_DATE, date);
        newValues.put(COLUMN_NAME_TIME, time);
        newValues.put(COLUMN_NAME_DISTANCE, distance);
        newValues.put(COLUMN_NAME_SPEED, speed);
        newValues.put(COLUMN_NAME_COORDINATES, coordinates);

        return db.insert(TABLE_NAME, null, newValues);
    }

    // todo: finish up
    public ExerciseData getExerciseEntry(int id) {
        ExerciseData exercise = new ExerciseData();

        Cursor cursor = db.query(TABLE_NAME, null, ExerciseDataEntry._ID + "=?", new String[] {Integer.toString(id)}, null, null, null);

        if(cursor.getCount() < 1) return null;

        exercise.setDistance(cursor.getDouble(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_DISTANCE)));

        return exercise;
    }
}

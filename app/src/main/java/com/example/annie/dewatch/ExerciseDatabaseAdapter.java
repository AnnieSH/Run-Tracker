package com.example.annie.dewatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


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

    public static final String SQL_CREATE_EXERCISE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ExerciseDataEntry.TABLE_NAME + " (" +
                    ExerciseDataEntry._ID + " INTEGER PRIMARY KEY," +
                    ExerciseDataEntry.COLUMN_NAME_DATE + " TEXT," +
                    ExerciseDataEntry.COLUMN_NAME_TIME + " INTEGER," +
                    ExerciseDataEntry.COLUMN_NAME_DISTANCE + " REAL," +
                    ExerciseDataEntry.COLUMN_NAME_SPEED + " REAL," +
                    ExerciseDataEntry.COLUMN_NAME_COORDINATES + " TEXT)";

    public static final String SQL_DELETE_EXERCISE_TABLE =
            "DROP TABLE IF EXISTS " + ExerciseDataEntry.TABLE_NAME;

    public static class ExerciseDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "exercise";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_SPEED = "speed";
        public static final String COLUMN_NAME_COORDINATES = "coordinates";
    }

    public static class RecordEntry implements BaseColumns {
        public static final String TABLE_NAME = "records";
        public static final String COLUMN_NAME_RECORD = "recordType";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_SPEED = "speed";
        public static final String COLUMN_NAME_COORDINATES = "coordinates";
        public static final String RECORD_SPEED = "speed";
        public static final String RECORD_TIME = "time";
        public static final String RECORD_DISTANCE = "distance";
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

    private SQLiteDatabase getDb() {
        return db;
    }

    /**
     *
     * @param date
     * @param time
     * @param distance
     * @param speed
     * @param coordinates
     * @return Result from db insert, index in the db
     */
    public long insertExerciseEntry(String date, int time, double distance, double speed, String coordinates) throws SQLException {
        ContentValues newValues = new ContentValues();
        newValues.put(ExerciseDataEntry.COLUMN_NAME_DATE, date);
        newValues.put(ExerciseDataEntry.COLUMN_NAME_TIME, time);
        newValues.put(ExerciseDataEntry.COLUMN_NAME_DISTANCE, distance);
        newValues.put(ExerciseDataEntry.COLUMN_NAME_SPEED, speed);
        newValues.put(ExerciseDataEntry.COLUMN_NAME_COORDINATES, coordinates);

        return db.insertOrThrow(ExerciseDataEntry.TABLE_NAME, null, newValues);
    }

    public ExerciseData getExerciseEntry(int id) {
        Cursor cursor = db.query(ExerciseDataEntry.TABLE_NAME, null, ExerciseDataEntry._ID + " = ?", new String[] {Integer.toString(id)}, null, null, null);

        if(cursor.getCount() < 1) return null;

        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_DATE));
        int time = cursor.getInt(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_TIME));
        double distance = cursor.getDouble(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_DISTANCE));
        double speed = cursor.getDouble(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_SPEED));
        String coordinates = cursor.getString(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_COORDINATES));
        cursor.close();

        return new ExerciseData(date, time, distance, speed, coordinates);
    }

    public List<ExerciseData> getAllExerciseEntries() {
        ArrayList<ExerciseData> allEntries = new ArrayList<>();

        Cursor cursor = db.query(ExerciseDataEntry.TABLE_NAME, null, null, null, null, null, null);

        Log.d("getExercise", "num " +  cursor.getCount());
        if(cursor.getCount() < 1) return null;

        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_DATE));
            int time = cursor.getInt(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_TIME));
            double distance = cursor.getDouble(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_DISTANCE));
            double speed = cursor.getDouble(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_SPEED));
            String coordinates = cursor.getString(cursor.getColumnIndex(ExerciseDataEntry.COLUMN_NAME_COORDINATES));

            allEntries.add(new ExerciseData(date, time, distance, speed, coordinates));
        }

        cursor.close();

        return allEntries;
    }
}

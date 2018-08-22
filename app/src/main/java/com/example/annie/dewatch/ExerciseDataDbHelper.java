package com.example.annie.dewatch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.annie.dewatch.ExerciseDatabaseAdapter.DATABASE_NAME;
import static com.example.annie.dewatch.ExerciseDatabaseAdapter.DATABASE_VERSION;
import static com.example.annie.dewatch.ExerciseDatabaseAdapter.SQL_CREATE_ENTRIES;
import static com.example.annie.dewatch.ExerciseDatabaseAdapter.SQL_DELETE_TABLE;

public class ExerciseDataDbHelper extends SQLiteOpenHelper {

    public ExerciseDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
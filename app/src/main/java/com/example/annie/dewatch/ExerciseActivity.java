package com.example.annie.dewatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Random;

import static com.example.annie.dewatch.ExercisePathFragment.exerciseData;

public class ExerciseActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private ExercisePathFragment exPathFrag;
    public static ExerciseGraphFragment exGraphFrag;

    private final static int REQUEST_ENABLE_LOC = 102;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        context = getApplicationContext();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endExercise();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Random rand = new Random();
                LatLng test = new LatLng(49.245167 + rand.nextInt(3) * 0.003, -123.115312 + rand.nextInt(3) * 0.003);

                exPathFrag.addToPath(test);
                return true;
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        final int NUM_ITEMS = 2;
        final int PATH = 0;
        final int GRAPH = 1;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch(position) {
                case PATH:
                    exPathFrag = ExercisePathFragment.newInstance();
                    return exPathFrag;
                case GRAPH:
                    exGraphFrag = ExerciseGraphFragment.newInstance();
                    return exGraphFrag;
            }

            return null;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }

    public void endExercise() {
        exPathFrag.stopTimer();
        exPathFrag.stopLocationListener();

        exerciseData.setPathPoints(exerciseData.path.getPoints());

        Intent intent = new Intent(getBaseContext(), ResultsActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Exit and discard current exercise?")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        exPathFrag.stopTimer();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

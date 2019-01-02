package com.example.annie.dewatch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ProfileActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public static User currentUser;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        currentUser = User.getCurrentUser();
        context = getApplicationContext();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.profile_navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                mViewPager.setCurrentItem(mSectionsPagerAdapter.HOME);
                                break;
                            case R.id.action_logs:
                                mViewPager.setCurrentItem(mSectionsPagerAdapter.LOGS);
                                break;
                            case R.id.action_progress:
                                mViewPager.setCurrentItem(mSectionsPagerAdapter.PROGRESS);
                                break;
                        }
                        return true;
                    }
                });

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 3;

        final int HOME = 0;
        final int LOGS = 1;
        final int PROGRESS = 2;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch(position) {
                case HOME:
                    ProfileFragment profileFragment = ProfileFragment.newInstance();
                    return profileFragment;
                case LOGS:
                    StatsFragment statsFragment = StatsFragment.newInstance();
                    return statsFragment;
                case PROGRESS:
                    return ProgressFragment.newInstance();
            }

            return null;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }
}
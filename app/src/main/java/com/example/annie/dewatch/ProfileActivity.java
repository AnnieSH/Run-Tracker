package com.example.annie.dewatch;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.annie.dewatch.OpenWeatherMap.WeatherData;
import com.google.gson.Gson;

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
        setWeatherData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
    }

    private void setWeatherData() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location currentLocation = null;
        try {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if(currentLocation != null) {
            String apiUrl = WeatherData.getCurrentCityWeatherUrl(currentLocation.getLatitude(), currentLocation.getLongitude());
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest weatherRequest = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    WeatherData data = gson.fromJson(response, WeatherData.class);

                    Log.d("WEATHER READ", data.getWeather());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            requestQueue.add(weatherRequest);
        }

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
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
package com.example.annie.dewatch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class StatsFragment extends Fragment {
    private String TAG = "STATS";
    View rootView;
    Context context;

    User currentUser;

    Toolbar toolbar;

    private ListView listView;

    private List<HashMap<String, String>> recordsList;
    private List<StatData> resultDataObject;

    private final String HASH_KEY_DIST = "dist";
    private final String HASH_KEY_SPEED = "speed";
    private final String HASH_KEY_DATE = "date";
    private final String HASH_KEY_TIME = "time";
    private final String HASH_KEY_TIME_FORMATTED = "timeFormatted";
    private final String HASH_KEY_DIST_RAW = "distanceRaw";
    private final String HASH_KEY_SPEED_RAW = "speedRaw";
    private final String HASH_KEY_DATE_FULL = "dateFull";

    public StatsFragment() { }

    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stats, container, false);
        context = getContext();
        currentUser = User.getCurrentUser();
        setHasOptionsMenu(true);

        recordsList = new ArrayList<>();
        listView = rootView.findViewById(R.id.stat_listview);
        resultDataObject = new ArrayList<>();

        readDb();

        return rootView;
    }

    private synchronized void readDb() {
        Log.d("DB", "READ");
        ExerciseDatabaseAdapter dbAdapter = new ExerciseDatabaseAdapter(context);

        dbAdapter.openReadable();
        List<ExerciseData> allEntries = dbAdapter.getAllExerciseEntries();
        dbAdapter.close();

        if (allEntries == null) {
            Log.e("DB", "is null");
            return;
        }

        for(ExerciseData data : allEntries) {
            recordsList.add(0, createHash(data));
            resultDataObject.add(0, new StatData(data.getDate(), data.getTotalTime(), data.getTotalDist(), data.getAvgSpeed(), data.getCoordinatesJson()));
        }

        initAdapter();
    }

    private HashMap<String, String> createHash(ExerciseData data) {
        HashMap<String, String> map = new HashMap<>();
        map.put(HASH_KEY_DATE, data.getDate().substring(0, 10));
        map.put(HASH_KEY_TIME, Integer.toString(data.getTotalTime()));
        map.put(HASH_KEY_DIST, String.format(getString(R.string.dist_text), data.getTotalDist()));
        map.put(HASH_KEY_SPEED, String.format(getString(R.string.speed_text), data.getAvgSpeed()));
        map.put(HASH_KEY_TIME_FORMATTED, String.format(getString(R.string.time_text), data.getTotalTime() / 60, data.getTotalTime() % 60));
        map.put(HASH_KEY_DATE_FULL, data.getDate());
        map.put(HASH_KEY_DIST_RAW, Double.toString(data.getTotalDist()));
        map.put(HASH_KEY_SPEED_RAW, Double.toString(data.getAvgSpeed()));

        return map;
    }

    public void initAdapter(){
        SimpleAdapter simpleAdapter = new SimpleAdapter(context, recordsList, R.layout.list_item_stats,
                new String[]{HASH_KEY_DATE, HASH_KEY_TIME_FORMATTED, HASH_KEY_DIST, HASH_KEY_SPEED},
                new int[]{R.id.textView_stats, R.id.list_subtext_time, R.id.list_subtext_dist,R.id.list_subtext_speed});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked " + position);
                Intent intent = new Intent(context, StatResultsActivity.class);
                intent.putExtra(StatData.INTENT_KEY, resultDataObject.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((ProfileActivity) getActivity()).setActionBarTitle("Exercise Logs");
        inflater.inflate(R.menu.stat_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        recordsList.clear();
        resultDataObject.clear();
        readDb();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.menuSortDate:
                Collections.sort(recordsList, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                        SimpleDateFormat df = new SimpleDateFormat(ExerciseData.DATE_FORMAT);
                        try {
                            Date date1 = df.parse(o1.get(HASH_KEY_DATE_FULL));
                            Date date2 = df.parse(o2.get(HASH_KEY_DATE_FULL));

                            if(date1.after(date2))
                                return -1;
                            else return 1;
                        } catch (ParseException e) {
                            Log.e("Compare parse", e.getMessage());
                        }

                        return 0;
                    }
                });

                initAdapter();

                break;
            case R.id.menuSortDistance:
                Collections.sort(recordsList, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                        if(Double.valueOf(o1.get(HASH_KEY_DIST_RAW)) > Double.valueOf(o2.get(HASH_KEY_DIST_RAW)))
                            return -1;
                        else return 1;
                    }
                });

                initAdapter();
                break;
            case R.id.menuSortSpeed:
                Collections.sort(recordsList, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                        if(Double.valueOf(o1.get(HASH_KEY_SPEED_RAW)) > Double.valueOf(o2.get(HASH_KEY_SPEED_RAW)))
                            return -1;
                        else return 1;
                    }
                });

                initAdapter();
                break;
            case R.id.menuSortTime:
                Collections.sort(recordsList, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                        if(Integer.valueOf(o1.get(HASH_KEY_TIME)) > Integer.valueOf(o2.get(HASH_KEY_TIME)))
                            return -1;
                        else return 1;
                    }
                });

                initAdapter();
                break;
        }
        return true;
    }
}

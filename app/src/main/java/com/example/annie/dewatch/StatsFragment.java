package com.example.annie.dewatch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
    private int listSize;

    private List<HashMap<String, String>> recordsList;
    private List<StatData> resultDataObject;

    private List<ExerciseData> exerciseLogs;

    private SimpleAdapter simpleAdapter;

    public StatsFragment() { }

    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stats, container, false);
        context = getContext();
        currentUser = User.getCurrentUser();
        setHasOptionsMenu(true);

        recordsList = new ArrayList<>();
        listView = rootView.findViewById(R.id.stat_listview);
        resultDataObject = new ArrayList<>();

        return rootView;
    }

    private void readDb() {
        Log.d("DB", "READ");
        ExerciseDatabaseAdapter dbAdapter = new ExerciseDatabaseAdapter(context);
        dbAdapter.openReadable();
        ExerciseData data = dbAdapter.getExerciseEntry(1);

        if(data != null)
            Log.d("DB", "READ DISTANCE " + data.getTotalDist());
        else
            Log.d("DB", "is null");

        dbAdapter.close();

        if(data != null) {
            recordsList.add(createHash(data));
            resultDataObject.add(new StatData(data.getDate(), data.getTotalTime(), data.getTotalDist(), data.getAvgSpeed(), ""));
            initAdapter();
        }
    }

    private HashMap<String, String> createHash(ExerciseData data) {
        HashMap<String, String> map = new HashMap<>();
        map.put("date", data.getDate().substring(0, 10));
        map.put("time", Integer.toString(data.getTotalTime()));
        map.put("dist", String.format(getString(R.string.dist_text),data.getTotalDist()));
        map.put("speed", String.format(getString(R.string.speed_text), data.getAvgSpeed()));

        return map;
    }

    public void initAdapter(){
        simpleAdapter = new SimpleAdapter(context, recordsList, R.layout.list_item_stats,
                new String[]{"date", "time", "dist", "speed"},
                new int[]{R.id.textView_stats, R.id.list_subtext_time, R.id.list_subtext_dist,R.id.list_subtext_speed});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = recordsList.get(position);

                Log.d(TAG, "Clicked " + position);
                Intent intent = new Intent(context, StatResultsActivity.class);
                intent.putExtra("RESULT_DATA_OBJECT", resultDataObject.get(position));
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
        readDb();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.menuSortDate:
                Collections.sort(recordsList, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date1 = df.parse(o1.get("date"));
                            Date date2 = df.parse(o2.get("date"));

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
                        int pos1 = Integer.parseInt(o1.get("position"));
                        int pos2 = Integer.parseInt(o2.get("position"));

                        StatData data1 = resultDataObject.get(pos1);
                        StatData data2 = resultDataObject.get(pos2);

                        if(data1.getDistance() > data2.getDistance())
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
                        int pos1 = Integer.parseInt(o1.get("position"));
                        int pos2 = Integer.parseInt(o2.get("position"));

                        StatData data1 = resultDataObject.get(pos1);
                        StatData data2 = resultDataObject.get(pos2);

                        if(data1.getAvg_speed() > data2.getAvg_speed())
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
                        SimpleDateFormat df = new SimpleDateFormat("HH:MM:SS");
                        int pos1 = Integer.parseInt(o1.get("position"));
                        int pos2 = Integer.parseInt(o2.get("position"));

                        StatData data1 = resultDataObject.get(pos1);
                        StatData data2 = resultDataObject.get(pos2);

                        try {
                            Long time1 = df.parse(data1.getTime_traveled()).getTime();
                            Long time2 = df.parse(data2.getTime_traveled()).getTime();

                            if(time1 > time2)
                                return -1;
                            else return 1;
                        } catch (ParseException e) {
                            Log.e("Time parse", e.getMessage());
                        }

                        return 0;
                    }
                });

                initAdapter();
                break;
        }
        return true;
    }
}

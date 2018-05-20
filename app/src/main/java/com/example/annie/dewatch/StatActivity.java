package com.example.annie.dewatch;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordRequestReadObject;
import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordResponseObject;
import com.example.annie.dewatch.deWatchClient.deWatchClient;
import com.example.annie.dewatch.deWatchClient.deWatchServer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatActivity extends AppCompatActivity {

    private String TAG = Config.APP_TAG + ": STATS";
    // User
    private User currentUser;

    private ListView listView;

    private SimpleAdapter simpleAdapter;

    //private List<Map<String, String>> recordsList;
    private List<HashMap<String, String>> recordsList;
    private List<StatData> resultDataObject;

    private int listSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        Toolbar toolbar = findViewById(R.id.stat_toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Exercise Logs");

        currentUser = User.getCurrentUser();

        recordsList = new ArrayList<HashMap<String, String>>();
        listView = (ListView) findViewById(R.id.stat_listview);
        resultDataObject = new ArrayList<StatData>();
        attemptRecordRead();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.stat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
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

    private HashMap<String, String> createHash(StatData data, String pos) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("date", data.getDate().substring(0, 10));
        map.put("position", pos);
        map.put("time", data.getTime_traveled());
        map.put("dist", String.format(getString(R.string.dist_text),data.getDistance()));
        map.put("speed", String.format(getString(R.string.speed_text), data.getAvg_speed()));

        return map;
    }

    private void attemptRecordRead() {
        // Date : YYYY-MM-DD
        // Time : HH:MM:SS
        // Time Traveled : HH:MM:SS
        // GPS Coordinates : JSON

        ExerciseRecordRequestReadObject requestData = new ExerciseRecordRequestReadObject(currentUser.getUid().toString(), null);

        deWatchClient client = deWatchServer.createService(deWatchClient.class);
        Call<List<ExerciseRecordResponseObject>> call = client.readExerRecords(requestData);
        call.enqueue(new Callback<List<ExerciseRecordResponseObject>>() {
            @Override
            public void onResponse(Call<List<ExerciseRecordResponseObject>> call, Response<List<ExerciseRecordResponseObject>> response) {
                listSize = response.body().size();

                if(listSize == 0)
                    return;

                for (int i = listSize-1; i >= 0; i--) { // Most recent first
                    String date = response.body().get(i).getDate();
                    String time = response.body().get(i).getTime();
                    float distance = response.body().get(i).getDistance();
                    String time_traveled = response.body().get(i).getTime_traveled();
                    float avg_speed = response.body().get(i).getAvg_speed();
                    short avg_hr = response.body().get(i).getAvg_hr();
                    short avg_o2 = response.body().get(i).getAvg_o2();
                    String gps_coord = response.body().get(i).getGps_coord();
                    String speeds = response.body().get(i).getSpeeds();
                    String hrs = response.body().get(i).getHrs();
                    String o2s = response.body().get(i).getO2s();
                    String timesList = response.body().get(i).getTime_list();

                    StatData tempData = new StatData(date, time, distance, time_traveled,
                            avg_speed, avg_hr, avg_o2, gps_coord, speeds, hrs, o2s, timesList);

                    recordsList.add(createHash(tempData, Integer.toString(listSize - i - 1)));
                    resultDataObject.add(tempData);
                }

                Log.d(TAG, recordsList.toString());
                initAdapter();

                Toast.makeText(StatActivity.this, "Read Record from DB Successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<ExerciseRecordResponseObject>> call, Throwable t) {
                Toast.makeText(StatActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "error reading data" + t.getMessage());
            }
        });
    }

    public void initAdapter(){
        simpleAdapter = new SimpleAdapter(StatActivity.this, recordsList, R.layout.list_item_stats,
                new String[]{"date", "time", "dist", "speed"},
                new int[]{R.id.textView_stats, R.id.list_subtext_time, R.id.list_subtext_dist,R.id.list_subtext_speed});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = recordsList.get(position);
                String posString = map.get("position");
                int pos = Integer.parseInt(posString);

                Log.d(TAG, "Clicked " + pos);
                Intent intent = new Intent(getBaseContext(), StatResultsActivity.class);
                intent.putExtra("RESULT_DATA_OBJECT", resultDataObject.get(pos));
                startActivity(intent);
            }
        });
    }



}

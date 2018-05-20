package com.example.annie.dewatch;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.annie.dewatch.deWatchClient.Bluetooth;
import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordRequestReadObject;
import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordResponseObject;
import com.example.annie.dewatch.deWatchClient.deWatchClient;
import com.example.annie.dewatch.deWatchClient.deWatchServer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProfileActivity extends AppCompatActivity {

    private User currentUser;

    private Button recordStartButton;
    private Button recordStopButton;
    private Button startButton;
    private Button statsButton;
    private Button progressButton;

    protected static BTStartListener startListener;
    private StartBluetooth startBluetooth;
    private final static int REQUEST_ENABLE_BT = 87;
    public static Bluetooth bluetooth;
    private Boolean btOn = false;
    private Audio recorder;
    private static final int RequestPermissionCode = 1;

    Context context;
    private Snackbar bluetoothSnackbar;
    private Snackbar btOffSnack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setSupportActionBar((Toolbar) findViewById(R.id.profile_toolbar));
        ActionBar actionBar = getSupportActionBar();
        context = getApplicationContext();

        startButton = findViewById(R.id.profile_button_start);
        statsButton = findViewById(R.id.profile_button_stats);
        recordStartButton = findViewById(R.id.profile_button_voice_start);
        recordStopButton = findViewById(R.id.profile_button_voice_stop);
        progressButton = findViewById(R.id.profile_button_progress);

        recordStopButton.setEnabled(false);
        currentUser = User.getCurrentUser();

        actionBar.setTitle(String.format(getString(R.string.welcome_text), currentUser.getFirstName().trim()));

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Remove ! on testing with hardware
                if(!bluetooth.btConnected) {
                    Intent intent = new Intent(ProfileActivity.this, ExerciseActivity.class);
                    startActivity(intent);
                }
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, StatActivity.class);
                startActivity(intent);
            }
        });

        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProgressActivity.class);
                startActivity(intent);
            }
        });

        recordStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()){
                    recorder = new Audio();
                    recorder.startRecording();
                    recordStartButton.setEnabled(false);
                    recordStopButton.setEnabled(true);
                }
                else {
                    requestPermission();
                }
            }});


        recordStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordStopButton.setEnabled(false);
                recorder.stopRecording();
                String fileName = recorder.AudioSavePathInDevice;
                int size = 1600*10;
                byte[] audioBuffer = new byte[size];
                try{
                    InputStream in = new FileInputStream(fileName);
                    in.read(audioBuffer);
                    in.close();
                }catch(Exception e){
                    Log.d("Audio File","Fail to read from the Audio file");
                }
                   Log.d("Content of Audio data",new String(audioBuffer));

                Voice recognizer = new Voice(context);
                Log.d("Start","yep");
                int command_num;
                command_num = recognizer.localRecognizer(audioBuffer);

                if(bluetooth==null){
                  Log.d("Bluetooth Null","thisis not good");

                }
                else if(command_num==1) {
                    if (bluetooth.btConnected) {
                        Log.d("Exercise Activity", "Starting exercise");
                        recordStartButton.setEnabled(true);
                        Intent intent = new Intent(ProfileActivity.this, ExerciseActivity.class);
                        startActivity(intent);
                    }
                }
                recordStartButton.setEnabled(true);
            }
        });


        displayRecords();

        bluetoothSnackbar = Snackbar.make(findViewById(R.id.profile_layout), "Bluetooth connecting", Snackbar.LENGTH_INDEFINITE);
        bluetoothSnackbar.show();
        bluetooth = new Bluetooth();

        startBluetooth = new StartBluetooth();
        startListener = new BTStartListener();

        startBluetooth.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_profile_settings:
                Intent intent = new Intent(ProfileActivity.this, ProfileSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_profile_logout:
                logout();
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(startListener.getStatus() == AsyncTask.Status.RUNNING)
            startListener.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        setSupportActionBar((Toolbar) findViewById(R.id.profile_toolbar));
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(String.format(getString(R.string.welcome_text), currentUser.getFirstName().trim()));

        if(bluetooth.btConnected && startListener != null && startListener.getStatus() == AsyncTask.Status.FINISHED) {
            startListener = new BTStartListener();
            startListener.execute();
        }

        displayRecords();
    }

    @Override
    public void onBackPressed() {    }

    private void displayRecords() {
        TextView lastExercise = findViewById(R.id.last_exercise);
        TextView lastExerciseStats = findViewById(R.id.last_exercise_stats);
        TextView bestSpeedText = findViewById(R.id.best_speed_stats);
        TextView bestDistText = findViewById(R.id.best_dist_stats);
        TextView bestTimeText = findViewById(R.id.best_time_stats);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean("hasStats", false))
            getRecords();
        // Still false after getting records
        if(!prefs.getBoolean("hasStats", false)) {
            lastExercise.setText("You haven't exercised yet!");
            bestDistText.setText("No personal bests yet!");
        } else {
            // TODO: Okay so this is super janky but basically I get the current date then convert
            // to get base date at midnight then convert back and there's probably a better way to do this
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Long date = TimeUnit.MILLISECONDS.toDays(prefs.getLong("lastDate", 0));
            String currDateString = df.format(Calendar.getInstance().getTime());
            int currDate = 0;
            try {
                currDate = (int) TimeUnit.MILLISECONDS.toDays(df.parse(currDateString).getTime());
            } catch (ParseException e) {
                Log.e("Parse exception", e.getMessage());
            }

            Long daysDiff = currDate - date;

            if (daysDiff == 0) {
                lastExercise.setText(String.format(getString(R.string.last_run), "today"));
            } else if (daysDiff == 1) {
                lastExercise.setText(String.format(getString(R.string.last_run), "yesterday"));
            } else
                lastExercise.setText(String.format(getString(R.string.last_run), daysDiff + " days ago"));

            lastExerciseStats.setText(String.format(getString(R.string.last_run_stats),
                    prefs.getInt("lastTime", 0),
                    prefs.getFloat("lastDistance", 0),
                    prefs.getFloat("lastSpeed", 0)));

            bestSpeedText.setText(String.format(getString(R.string.exercise_stat),
                    prefs.getString("bestSpeedDate", ""),
                    prefs.getInt("bestSpeedTime", 0),
                    prefs.getFloat("bestSpeedDist", 0),
                    prefs.getFloat("bestSpeedSpeed", 0)));

            bestDistText.setText(String.format(getString(R.string.exercise_stat),
                    prefs.getString("bestDistDate", ""),
                    prefs.getInt("bestDistTime", 0),
                    prefs.getFloat("bestDistDist", 0),
                    prefs.getFloat("bestDistSpeed", 0)));

            bestTimeText.setText(String.format(getString(R.string.exercise_stat),
                    prefs.getString("bestTimeDate", ""),
                    prefs.getInt("bestTimeTime", 0),
                    prefs.getFloat("bestTimeDist", 0),
                    prefs.getFloat("bestTimeSpeed", 0)));
        }

    }

    private void getRecords() {
        // Date : YYYY-MM-DD
        // Time : HH:MM:SS
        // Time Traveled : HH:MM:SS
        // GPS Coordinates : JSON

        ExerciseRecordRequestReadObject requestData = new ExerciseRecordRequestReadObject(currentUser.getUid(), null);

        deWatchClient client = deWatchServer.createService(deWatchClient.class);
        Call<List<ExerciseRecordResponseObject>> call = client.readExerRecords(requestData);
        call.enqueue(new Callback<List<ExerciseRecordResponseObject>>() {
            @Override
            public void onResponse(Call<List<ExerciseRecordResponseObject>> call, Response<List<ExerciseRecordResponseObject>> response) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final SharedPreferences.Editor editor = prefs.edit();

                if(response.body().size() == 0) {
                    editor.putBoolean("hasStats", false);
                    return;
                }

                String bestSpeedDate = response.body().get(0).getDate();
                String bestSpeedTime = response.body().get(0).getTime_traveled();
                float bestSpeedDist = response.body().get(0).getDistance();
                float bestSpeed = response.body().get(0).getAvg_speed();

                String bestTimeDate = bestSpeedDate;
                String bestTime = bestSpeedTime;
                int bestTimeSec = Integer.parseInt(bestTime.substring(0,1)) * 60 * 60
                        + Integer.parseInt(bestTime.substring(3,4)) * 60
                        + Integer.parseInt(bestTime.substring(6,7));
                float bestTimeDist = bestSpeedDist;
                float bestTimeSpeed = bestSpeed;

                String bestDistDate = bestSpeedDate;
                String bestDistTime = bestSpeedTime;
                float bestDist = bestSpeedDist;
                float bestDistSpeed = bestSpeed;

                for (int i = 0; i < response.body().size(); i++) {
                    String date = response.body().get(i).getDate();
                    float distance = response.body().get(i).getDistance();
                    String time_traveled = response.body().get(i).getTime_traveled();
                    float speed = response.body().get(i).getAvg_speed();
                    String time_travelled = response.body().get(i).getGps_coord();

                    int sec = Integer.parseInt(time_traveled.substring(0,1)) * 60 * 60
                            + Integer.parseInt(time_traveled.substring(3,4)) * 60
                            + Integer.parseInt(time_traveled.substring(6,7));

                    if(distance > bestDist) {
                        bestDistDate = date;
                        bestDistTime = time_traveled;
                        bestDist = distance;
                        bestDistSpeed = speed;
                    }
                    if(sec > bestTimeSec) {
                        bestTimeDate = date;
                        bestTime = time_traveled;
                        bestTimeSec = sec;
                        bestTimeDist = distance;
                        bestTimeSpeed = speed;
                    }
                    if(speed > bestSpeed) {
                        bestSpeedDate = date;
                        bestSpeedTime = time_traveled;
                        bestSpeedDist = distance;
                        bestSpeed = speed;
                    }
                }

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                editor.putString("bestSpeedDate", bestSpeedDate.substring(0, 10));
                editor.putString("bestDistDate", bestDistDate.substring(0, 10));
                editor.putString("bestTimeDate", bestTimeDate.substring(0, 10));

                editor.putInt("bestSpeedTime", Integer.parseInt(bestSpeedTime.substring(0,1)) * 60
                        + Integer.parseInt(bestSpeedTime.substring(3,4)));
                editor.putInt("bestDistTime", Integer.parseInt(bestDistTime.substring(0, 1)) * 60
                        + Integer.parseInt(bestDistTime.substring(3,4)));
                editor.putInt("bestTimeTime", bestTimeSec / 60);

                editor.putFloat("bestSpeedDist", bestSpeedDist);
                editor.putFloat("bestDistDist", bestDist);
                editor.putFloat("bestTimeDist", bestTimeDist);

                editor.putFloat("bestSpeedSpeed", bestSpeed);
                editor.putFloat("bestDistSpeed", bestDistSpeed);
                editor.putFloat("bestTimeSpeed", bestTimeSpeed);

                int size = response.body().size();
                try {
                    Long lastDay = df.parse(response.body().get(size - 1).getDate()).getTime();
                    String timeTravelled = response.body().get(size - 1).getTime_traveled().trim();
                    String minString = timeTravelled.substring(3,5);
                    Log.e("timeTr", timeTravelled + " " + minString);
                    int min = Integer.parseInt(minString);

                    editor.putLong("lastDate", lastDay);
                    editor.putInt("lastTime", min);
                    editor.putFloat("lastDistance", response.body().get(size-1).getDistance());
                    editor.putFloat("lastSpeed", response.body().get(size-1).getAvg_speed());
                } catch (ParseException e) {
                    Log.e("Record parse", e.getMessage());
                }

                editor.putBoolean("hasStats", true);
                editor.apply();
            }

            @Override
            public void onFailure(Call<List<ExerciseRecordResponseObject>> call, Throwable t) {
                Log.e("Server read", t.getMessage());
            }
        });
    }

    /*
     * BLUETOOTH
     */
    private class StartBluetooth extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            int tries;

            for(tries = 0; tries < 5; tries++) {
                setupBluetooth();

                if(bluetooth.btConnected || !btOn)
                    break;
                else {
                    publishProgress(tries);
                    try {Thread.sleep(150);}
                    catch(InterruptedException e) {
                        Log.e("Delay", "Interrupted exception");
                    }
                }

                if(isCancelled())
                    break;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer ... param) {
            int tryNum = param[0] + 1;
            bluetoothSnackbar.setText("Bluetooth connection failed: Reattempting " + tryNum + "/5");
            bluetoothSnackbar.setAction(null, null);
            bluetoothSnackbar.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if(bluetooth.btConnected) {
                Toast.makeText(context, "Bluetooth connected", Toast.LENGTH_LONG).show();
                bluetoothSnackbar.dismiss();
                startListener.execute();
            }
            else if( btOn ){
                bluetoothSnackbar.setText("Bluetooth connection failed");
                bluetoothSnackbar.setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startBluetooth = new StartBluetooth();
                        startBluetooth.execute();
                    }
                });

                bluetoothSnackbar.show();
                Log.e("Bluetooth", "Not connected");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) {
                    bluetoothSnackbar.dismiss();
                    btOn = true;
                    startBluetooth = new StartBluetooth();
                    startBluetooth.execute();

                    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(mReceiver, filter);
                } else {
                    if(btOffSnack == null) {
                        btOffSnack = Snackbar.make(findViewById(R.id.profile_layout), "Bluetooth is off", Snackbar.LENGTH_INDEFINITE);

                        btOffSnack.setAction("Try again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startBluetooth = new StartBluetooth();
                                startBluetooth.execute();
                            }
                        });
                    }

                    bluetoothSnackbar.dismiss();
                    btOffSnack.show();
                }
                break;
        }
    }

    void setupBluetooth() {
        bluetooth.btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetooth.btAdapter == null) {
            return;
        }

        if(!bluetooth.btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        else {
            btOn = true;
        }
        bluetooth.getBtDevice();
    }

    private void logout() {
        currentUser.setLoggedOff(getBaseContext());

        if(bluetooth.btConnected)
            bluetooth.closeConnection();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear Activity stack
        startActivity(intent);

        finish();
    }

    protected class BTStartListener extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            int first_free_pos = 0;
            int size = 32000*10;
            int command_num = 0;
            byte[] buffer = new byte[size];
            while (true) {
                try {
                    if (isCancelled()) return null;
                    int num = bluetooth.btInStream.read(buffer, first_free_pos,size - first_free_pos);
                    if (num > 0) {
                        first_free_pos += num;
                        Log.e("got stuff", Integer.toString(first_free_pos));
                    }
                    if ((first_free_pos > 7) &&
                        (buffer[first_free_pos - 1] == (byte)0) &&
                        (buffer[first_free_pos - 2] == (byte)0) &&
                        (buffer[first_free_pos - 3] == (byte)255) &&
                        (buffer[first_free_pos - 4] == (byte)255) &&
                        (buffer[first_free_pos - 5] == (byte)0) &&
                        (buffer[first_free_pos - 6] == (byte)0)) {
                        // convert to string and pars
                        String s = new String(buffer, 0, first_free_pos-6);
                        String[] entry = s.split(",|\\*");
                        // check if we have the start command
                        if(entry[0].equals("START")) {
                            break;
                        }
                        // reset buffer anyways
                        Arrays.fill(buffer, (byte) 0);
                        first_free_pos = 0;
                    }
                    if (first_free_pos > 0 &&
                        (buffer[first_free_pos - 1]  == '&') &&
                        (buffer[first_free_pos - 2]  == '&') &&
                        (buffer[first_free_pos - 3]  == '&') &&
                        (buffer[first_free_pos - 4]  == '&')) {
                        Log.d("Running google", "detected $$$");
                        Voice recognizer = new Voice(context);
                        //if(entry[0].equals("AUDIO")){
                            command_num =  recognizer.voiceRecognizer(buffer);
                            if (command_num == 1){
                                break;
                            }
                        //}
                        // reset buffer anyways
                        Arrays.fill(buffer, (byte) 0);
                        first_free_pos = 0;
                    }
                } catch (Exception e) {
//                    bluetooth.ConnectToSerialBluetoothDevice()
                    Log.e("ERROR", e.toString());
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            Intent intent = new Intent(context, ExerciseActivity.class);
            startActivity(intent);
        }
    }

    // https://stackoverflow.com/questions/9693755/detecting-state-changes-made-to-the-bluetoothadapter
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        btOn = false;
                        unregisterReceiver(mReceiver);

                        if(startBluetooth.getStatus() == AsyncTask.Status.RUNNING)
                            startBluetooth.cancel(true);
                        else if(startListener != null && startListener.getStatus() == AsyncTask.Status.RUNNING)
                            startListener.cancel(true);

                        if(btOffSnack == null) {
                            btOffSnack = Snackbar.make(findViewById(R.id.profile_layout), "Bluetooth is off", Snackbar.LENGTH_INDEFINITE);

                            btOffSnack.setAction("Try again", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startBluetooth = new StartBluetooth();
                                    startBluetooth.execute();
                                }
                            });
                        }

                        bluetoothSnackbar.dismiss();
                        btOffSnack.show();

                        break;
                }
            }
        }
    };


    private void requestPermission() {
        ActivityCompat.requestPermissions(ProfileActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(ProfileActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ProfileActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}

package com.example.annie.dewatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.annie.dewatch.ProfileActivity.bluetooth;
import static com.example.annie.dewatch.ExercisePathFragment.exerciseData;

public class ExerciseActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ExercisePathFragment exPathFrag;
    public static ExerciseGraphFragment exGraphFrag;

    private final static int REQUEST_ENABLE_LOC = 102;

    private Context context;
    private BTListener myBTListener;
    private Audio recorder;
    private static final int RequestPermissionCode = 1;
    private Button recordStartButton;
    private Button recordStopButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        context = getApplicationContext();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        recordStartButton = findViewById(R.id.activity_exercise_voice_start);
        recordStopButton = findViewById(R.id.activity_exercise_voice_stop);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endExercise();
                return;
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Random rand = new Random();
                LatLng test = new LatLng(49.245167 + rand.nextInt(3) * 0.003, -123.115312 + rand.nextInt(3) * 0.003);

                exPathFrag.addToPath(test);
                exPathFrag.updateValues(97 + rand.nextInt(10), 96 + rand.nextInt(3), exerciseData.totalDist + 0.3, (int)(Calendar.getInstance().getTimeInMillis()/1000));
                return true;
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

                if (command_num==2) {
                    if (bluetooth.btConnected) {
                        Log.d("Exercise Activity", "Starting exercise");
                        recordStartButton.setEnabled(true);
                        endExercise();
                    }
                }
                recordStartButton.setEnabled(true);
            }
        });

        if (bluetooth.btConnected){
            //send the start command
            try {
                bluetooth.btOutStream.write("B".getBytes());
                myBTListener = new BTListener();
                myBTListener.execute();
                Thread.sleep(200);
                bluetooth.btOutStream.write("B".getBytes());
                Thread.sleep(200);
                bluetooth.btOutStream.write("B".getBytes());
                Thread.sleep(200);
                bluetooth.btOutStream.write("B".getBytes());
                Thread.sleep(200);
                bluetooth.btOutStream.write("B".getBytes());
            } catch (Exception e){
                Log.e("ERROR", e.toString());
            }

        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch(position) {
                case 0:
                    exPathFrag = ExercisePathFragment.newInstance();
                    return exPathFrag;
                case 1:
                    exGraphFrag = ExerciseGraphFragment.newInstance();
                    return exGraphFrag;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public void endExercise() {
        exerciseData.pathPoints = exerciseData.path.getPoints();

        if (bluetooth.btConnected) {
            //send the stop command
            try {
                bluetooth.btOutStream.write("E".getBytes());
                Thread.sleep(200);
                bluetooth.btOutStream.write("E".getBytes());
                Thread.sleep(200);
                bluetooth.btOutStream.write("E".getBytes());
                Thread.sleep(200);
                bluetooth.btOutStream.write("E".getBytes());
                Thread.sleep(200);
                bluetooth.btOutStream.write("E".getBytes());
            } catch (Exception e) {
            }
            if(myBTListener != null)
                myBTListener.cancel(true);
        }

        Intent intent = new Intent(getBaseContext(), ResultsActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear Activity stack
        startActivity(intent);
        this.finish();
        return;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Exit and discard current exercise?")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private class BTListener extends AsyncTask<Void, String, Void> {
        private boolean end = false;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                int first_free_pos = 0;
                int size = 32000 * 10;
                byte[] buffer = new byte[size];
                while (true) {
                    try {
                        if (isCancelled()) return null;
                        int num = bluetooth.btInStream.read(buffer, first_free_pos, size - first_free_pos);
                        if (num > 0) {
                            first_free_pos += num;
                        }
                        if ((first_free_pos > 7) &&
                                (buffer[first_free_pos - 1] == (byte)0) &&
                                (buffer[first_free_pos - 2] == (byte)0) &&
                                (buffer[first_free_pos - 3] == (byte)255) &&
                                (buffer[first_free_pos - 4] == (byte)255) &&
                                (buffer[first_free_pos - 5] == (byte)0) &&
                                (buffer[first_free_pos - 6] == (byte)0)) {
                            // convert to string and pars
                            String s = new String(buffer, 0, first_free_pos - 6);
                            Log.e("BT DATA", s);

                            String[] entry = s.split(",|\\*");

                            if (entry[0].equals("END")) {
                                end = true;
                                Log.e("end", "End command from DE1");
                                break;
                            }
                            publishProgress(s);
                            // reset buffer
                            Arrays.fill(buffer, (byte) 0);
                            first_free_pos = 0;
                        }
                        if (first_free_pos > 0 &&
                                (buffer[first_free_pos - 1] == '&') &&
                                (buffer[first_free_pos - 2] == '&') &&
                                (buffer[first_free_pos - 3] == '&') &&
                                (buffer[first_free_pos - 4] == '&')) {
                            Voice recognizer = new Voice(context);
                            //if(entry[0].equals("AUDIO")){
                            int command_num = recognizer.voiceRecognizer(buffer);
                            //reset buffer
                            Arrays.fill(buffer, (byte) 0);
                            first_free_pos = 0;
                            if (command_num == 2) {

                                break;
                            }
                            //}
                        }


                    } catch (Exception e) {
                        Log.e("ERROR", e.toString());
                    }

                    if (end)
                        return null;
                }
            } catch (Exception ex){
                Log.e("From listener", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            endExercise();
        }

        @Override
        protected void onProgressUpdate(String ... s) {
            super.onProgressUpdate(s);
            try{
                String[] entry = s[0].split(",|\\*");
                Log.e("time", entry[0]);

                if(entry[0].equals("END")) {
                    end = true;
                    Log.e("end", "Got END command from DE1");
                    return;
                }

                int time = Integer.parseInt(entry[0]);
                double lat;
                double lon;
                try {
                    Log.e("LAT", entry[1]);
                    double lat_b = Double.parseDouble(entry[1].substring(0, 2));
                    double lat_s = Double.parseDouble(entry[1].substring(2, entry[1].length() - 1));
                    lat = lat_b + lat_s / 60;
                    Log.e("LON", entry[2]);
                    double lon_b = Double.parseDouble(entry[2].substring(0, 3));
                    double lon_s = Double.parseDouble(entry[2].substring(3, entry[2].length() - 1));
                    lon = lon_b + lon_s / 60;
                } catch (Exception e){
                    lon = 0.0;
                    lat = 0.0;
                }
                Log.e("HR", entry[3]);
                int hr = Integer.parseInt(entry[3]);
                Log.e("O2", entry[4]);
                int o2 = Integer.parseInt(entry[4]);

                double dist = Double.parseDouble(entry[5]);

                exPathFrag.updateValues(hr,o2,dist,time);
                if (lat > 0) {
                    Log.e("lat", Double.toString(lat));
                    Log.e("lon", Double.toString(lon));
                    LatLng newPoint = new LatLng(lat, -lon);
                    exPathFrag.addToPath(newPoint);
                }
            } catch (Exception e){
                Log.e("ERROR", e.toString());
            }

        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ExerciseActivity.this, new
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
                        Toast.makeText(ExerciseActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ExerciseActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
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

package com.example.annie.dewatch;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {
    Context context;

    TextView nameInput;
    private final int LOC_PERMISSION_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        context = getApplicationContext();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(User.LOGGED_IN, false)) {
            Intent loginIntent = new Intent(context, ProfileActivity.class);
            User user = User.getCurrentUser();
            user.logIn(prefs.getString(User.NAME, null), context);

            startActivity(loginIntent);
            finish();
        }

        nameInput = findViewById(R.id.name_input);
        nameInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    attemptLogIn();
                    return true;
                } else
                    return false;
            }
        });

        Button readyButton = findViewById(R.id.ready_button);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogIn();
            }
        });

        View background = findViewById(R.id.welcome_background);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(imm != null)
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void attemptLogIn() {
        if(nameInput.getText() == null || nameInput.getText().length() == 0)
            nameInput.setError("Name required");
        else {
            User user = User.getCurrentUser();
            user.logIn(nameInput.getText().toString().trim(), context);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getLocationPermission();
                return;
            }

            Intent loginIntent = new Intent(context, ProfileActivity.class);
            startActivity(loginIntent);
        }
    }

    /**
     * Called when there is no location permission
     */
    private void getLocationPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);

        builder.setMessage("GPS permission is needed to track your runs")
                .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WelcomeActivity.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_PERMISSION_CODE);
                    }
                });

        builder.create().show();
    }
}

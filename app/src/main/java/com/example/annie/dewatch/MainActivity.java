package com.example.annie.dewatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button signUpButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if already previously logged in and log in automatically
        if(prefs.getBoolean("loggedIn", false)) {
            User user = User.getCurrentUser();
            user.setCurrentUser(prefs.getString("firstName", null),
                    prefs.getString("lastName", null),
                    prefs.getString("email", null),
                    prefs.getString("age", null),
                    prefs.getString("weight", null),
                    prefs.getString("gender", null),
                    prefs.getString("uid", null)

            );

            Toast.makeText(this,"Automatically Logged In", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        signUpButton = (Button) findViewById(R.id.main_button_signup);
        loginButton = (Button) findViewById(R.id.main_button_login);

//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), ExerciseActivity.class);
//                startActivity(intent);
//            }
//        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }


}

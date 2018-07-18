package com.example.annie.dewatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class WelcomeActivity extends AppCompatActivity {
    Context context;

    TextView nameInput;

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
        Button readyButton = findViewById(R.id.ready_button);

        createTextInputFocus();
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogIn();
            }
        });
    }

    private void createTextInputFocus() {
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

        nameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    if(imm != null)
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    private void attemptLogIn() {
        if(nameInput.getText() == null || nameInput.getText().length() == 0)
            nameInput.setError("Name required");
        else {
            User user = User.getCurrentUser();
            user.logIn(nameInput.getText().toString().trim(), context);

            Intent loginIntent = new Intent(context, ProfileActivity.class);
            startActivity(loginIntent);
        }
    }
}

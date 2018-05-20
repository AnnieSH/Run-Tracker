package com.example.annie.dewatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.annie.dewatch.deWatchClient.Classes.AuthResponseObject;
import com.example.annie.dewatch.deWatchClient.Classes.LoginRequestObject;
import com.example.annie.dewatch.deWatchClient.deWatchClient;
import com.example.annie.dewatch.deWatchClient.deWatchServer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private String TAG = Config.APP_TAG + ": Login";

    private User currentUser;

    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        currentUser = User.getCurrentUser();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if already previously logged in and log in automatically
        if (prefs.getBoolean("loggedIn", false)) {
            currentUser.setCurrentUser(prefs.getString("firstName", null),
                    prefs.getString("lastName", null),
                    prefs.getString("email", null),
                    prefs.getString("age", null),
                    prefs.getString("weight", null),
                    prefs.getString("gender", null),
                    prefs.getString("uid", null)

            );

            Toast.makeText(this, "Automatically Logged In", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        emailEditText = (EditText) findViewById(R.id.login_editText_email);
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    if(imm != null)
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        passwordEditText = (EditText) findViewById(R.id.login_editText_password);
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    if(imm != null)
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        loginButton = (Button) findViewById(R.id.login_btn_login);
        Button signupButton = findViewById(R.id.signup_btn);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.putExtra("email", emailEditText.getText().toString());
                intent.putExtra("password", passwordEditText.getText().toString());
                startActivity(intent);
            }
        });

        registerOnClickListener();

    }

    private void registerOnClickListener() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailValid(emailEditText)){
                    attemptSignIn();
                }
            }
        });

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(emailValid(emailEditText))
                        attemptSignIn();

                    return true;
                }
                return false;
            }
        });
    }

    private void attemptSignIn() {
        LoginRequestObject requestData = new LoginRequestObject(emailEditText.getText().toString(), passwordEditText.getText().toString());

        deWatchClient client = deWatchServer.createService(deWatchClient.class);
        Call<AuthResponseObject> call = client.signInUser(requestData);

        call.enqueue(new Callback<AuthResponseObject>() {
            @Override
            public void onResponse(Call<AuthResponseObject> call, Response<AuthResponseObject> response) {
                String firstName = response.body().getFirstName();
                String lastName = response.body().getLastName();
                String email = response.body().getEmail();
                String gender = response.body().getGender();
                int age = response.body().getAge();
                int weight = response.body().getWeight();
                String uid = response.body().getUid();
                int code = response.body().getCode();

                if(code == 204){
                    passwordEditText.setError("Incorrect Password!");
                    Toast.makeText(LoginActivity.this, "Incorrect Password!", Toast.LENGTH_LONG).show();
                }
                else{
                    currentUser.setCurrentUser(firstName, lastName, email, String.valueOf(age), String.valueOf(weight), gender, uid);
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    currentUser.setLoggedIn(getBaseContext());
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<AuthResponseObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "error signing in" + t.getMessage());
            }
        });
    }

    private boolean emailValid(EditText email) {
        if (email.getText().toString().length() > 0 &&
                email.getText().toString().contains("@") &&
                email.getText().toString().contains(".")) {
            return true;
        } else {
            email.setError("Invalid Email!");
            return false;
        }
    }
}

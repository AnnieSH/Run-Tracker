package com.example.annie.dewatch;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditAgeRequest;
import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditEmailRequest;
import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditFirstNameRequest;
import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditLastNameRequest;
import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditWeightRequest;
import com.example.annie.dewatch.deWatchClient.deWatchClient;
import com.example.annie.dewatch.deWatchClient.deWatchServer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSettingsActivity extends AppCompatActivity {

    private User currentUser;

    private TextView textLastName;
    private TextView textFirstName;
    private TextView textEmail;
    private TextView textAge;
    private TextView textWeight;

    private ImageButton buttonLastName;
    private ImageButton buttonFirstName;
    private ImageButton buttonEmail;
    private ImageButton buttonAge;
    private ImageButton buttonWeight;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.profile_settings_toolbar));
        ActionBar actionBar = getSupportActionBar();

        context = getApplicationContext();

        actionBar.setTitle("Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);

        currentUser = User.getCurrentUser();

        textLastName = (TextView)findViewById(R.id.profile_text_lastname);
        textFirstName = (TextView)findViewById(R.id.profile_text_firstname);
        textEmail = (TextView)findViewById(R.id.profile_text_email);
        textAge = (TextView)findViewById(R.id.profile_text_age);
        textWeight = (TextView)findViewById(R.id.profile_text_weight);

        textLastName.setText(currentUser.getLastName());
        textFirstName.setText(currentUser.getFirstName());
        textEmail.setText(currentUser.getEmail());
        textAge.setText(currentUser.getAge());
        textWeight.setText(currentUser.getWeight());

    }

    public void editLastName(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Last Name");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isValid(input.getText().toString())) {

                    ProfileEditLastNameRequest requestData = new ProfileEditLastNameRequest(currentUser.getUid(), input.getText().toString());

                    deWatchClient client = deWatchServer.createService(deWatchClient.class);
                    Call<Void> call = client.profileEditLastName(requestData);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            currentUser.setLastName(input.getText().toString());
                            textLastName.setText(currentUser.getLastName());
                            currentUser.setLoggedIn(getBaseContext());
                            Toast.makeText(ProfileSettingsActivity.this, "Update Successful" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(ProfileSettingsActivity.this, "Update Failure" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    //m_Text = input.getText().toString();
                }
                else{
                    //Toast.makeText(ProfileSettingsActivity.this, "Too short" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void editFirstName(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog d = builder.create();

        builder.setTitle("Change First Name");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isValid(input.getText().toString())){

                    ProfileEditFirstNameRequest requestData = new ProfileEditFirstNameRequest(currentUser.getUid(), input.getText().toString());

                    deWatchClient client = deWatchServer.createService(deWatchClient.class);
                    Call<Void> call = client.profileEditFirstName(requestData);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            currentUser.setFirstName(input.getText().toString());
                            textFirstName.setText(currentUser.getFirstName());
                            currentUser.setLoggedIn(getBaseContext());
                            Toast.makeText(ProfileSettingsActivity.this, "Update Successful" + input.getText().toString(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(ProfileSettingsActivity.this, "Update Failure" + input.getText().toString(), Toast.LENGTH_SHORT).show();

                        }
                    });

                    //m_Text = input.getText().toString();
                }
                else{
                    //Toast.makeText(ProfileSettingsActivity.this, "Too short" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void editEmail(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Email");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(emailValid(input.getText().toString())){
                    ProfileEditEmailRequest requestData = new ProfileEditEmailRequest(currentUser.getUid(), input.getText().toString());

                    deWatchClient client = deWatchServer.createService(deWatchClient.class);
                    Call<Void> call = client.profileEditEmail(requestData);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            currentUser.setEmail(input.getText().toString());
                            textEmail.setText(currentUser.getEmail());
                            currentUser.setLoggedIn(getBaseContext());
                            Toast.makeText(ProfileSettingsActivity.this, "Update Successful" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(ProfileSettingsActivity.this, "Update Failure" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    //Toast.makeText(ProfileSettingsActivity.this, "Too short" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                }
                //m_Text = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void editAge(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Age");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isValid(input.getText().toString())){

                    ProfileEditAgeRequest requestData = new ProfileEditAgeRequest(currentUser.getUid(), Integer.parseInt(input.getText().toString()));

                    deWatchClient client = deWatchServer.createService(deWatchClient.class);
                    Call<Void> call = client.profileEditAge(requestData);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            currentUser.setAge(input.getText().toString());
                            textAge.setText(currentUser.getAge());
                            currentUser.setLoggedIn(getBaseContext());
                            Toast.makeText(ProfileSettingsActivity.this, "Update Successful" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(ProfileSettingsActivity.this, "Update Failure" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    //Toast.makeText(ProfileSettingsActivity.this, "Too short" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                }

                //m_Text = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void editWeight(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Weight");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isValid(input.getText().toString())){

                    ProfileEditWeightRequest requestData = new ProfileEditWeightRequest(currentUser.getUid(), Integer.parseInt(input.getText().toString()));

                    deWatchClient client = deWatchServer.createService(deWatchClient.class);
                    Call<Void> call = client.profileEditWeight(requestData);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            currentUser.setWeight(input.getText().toString());
                            textWeight.setText(currentUser.getWeight());
                            currentUser.setLoggedIn(getBaseContext());
                            Toast.makeText(ProfileSettingsActivity.this, "Update Successful" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(ProfileSettingsActivity.this, "Update Failure" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    //Toast.makeText(ProfileSettingsActivity.this, "Too short" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                }
                //m_Text = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private boolean isValid(String field){
        if(field.length() > 0){
            return true;
        }
        else {
            Toast.makeText(ProfileSettingsActivity.this, "Field cannot be left blank!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean emailValid(String email){
        if(email.length() > 0 &&
                email.contains("@") &&
                email.contains(".")){
            return true;
        }
        else{
            Toast.makeText(ProfileSettingsActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}

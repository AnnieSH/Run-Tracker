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

    private TextView editName;

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

        actionBar.setTitle("Edit Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);

        currentUser = User.getCurrentUser();

        editName = findViewById(R.id.profile_settings_edit_name);

        editName.setText(currentUser.getName());

    }

    public void editName(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isValid(input.getText().toString())) {
                    currentUser.setName(input.getText().toString());
                    editName.setText(currentUser.getName());
                    currentUser.setLoggedIn(getBaseContext());
                    Toast.makeText(ProfileSettingsActivity.this, "Update Successful" + input.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();
            }
        });

        builder.create().show();
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

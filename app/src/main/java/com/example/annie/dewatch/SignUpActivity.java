package com.example.annie.dewatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.annie.dewatch.deWatchClient.Classes.NewUserRequestObject;
import com.example.annie.dewatch.deWatchClient.Classes.AuthResponseObject;
import com.example.annie.dewatch.deWatchClient.deWatchClient;
import com.example.annie.dewatch.deWatchClient.deWatchServer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private String TAG = Config.APP_TAG + ": SIGN UP";
    private User currentUser;

    private EditText lastnameEditText, firstnameEditText, emailEditText,
            passwordEditText, cpasswordEditText, ageEditText, weightEditText;
    private Button submitButton;

    private Spinner genderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Bundle bundle = getIntent().getExtras();

        // initialize Auth
        currentUser = User.getCurrentUser();

        lastnameEditText = (EditText) findViewById(R.id.signup_editText_lastname);
        firstnameEditText = (EditText) findViewById(R.id.signup_editText_firstname);
        emailEditText = (EditText) findViewById(R.id.signup_editText_email);
        passwordEditText = (EditText) findViewById(R.id.signup_editText_password);
        cpasswordEditText = (EditText) findViewById(R.id.signup_editText_cpassword);
        ageEditText = (EditText) findViewById(R.id.signup_editText_age);
        weightEditText = (EditText) findViewById(R.id.signup_editText_weight);

        submitButton = (Button) findViewById(R.id.signup_btn_signUp);

        genderSpinner = (Spinner) findViewById(R.id.signup_spinner_gender);

        String[] gender = {"Male", "Female"};

        // Initializing an ArrayAdapter
        ArrayAdapter<CharSequence> spinnerArrayAdapter = new ArrayAdapter<CharSequence>(
                this,R.layout.spinner_item_gender, gender);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerArrayAdapter);

        if(bundle.getString("email") != null) {
            Log.e("Email", "Exists");
            emailEditText.setText(bundle.getString("email"));
        } else
            Log.e("Email", "Doesnt exist");
        if(bundle.getString("password") != null) {
            Log.e("Password", "Exists");
            passwordEditText.setText(bundle.getString("password"));
        }

        //genderSpinner.setAdapter(adapter);

        registerOnClickListener();
    }

    private void registerOnClickListener(){

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid(lastnameEditText) &&
                        isValid(firstnameEditText) &&
                        emailValid(emailEditText) &&
                        passwordValid(passwordEditText, cpasswordEditText)) {
                    attemptSignup();
                }
            }
        });
    }

    private void attemptSignup(){
        NewUserRequestObject requestData = new NewUserRequestObject(firstnameEditText.getText().toString(),
                lastnameEditText.getText().toString(), genderSpinner.getSelectedItem().toString(), Integer.parseInt(ageEditText.getText().toString()),
                Integer.parseInt(weightEditText.getText().toString()), emailEditText.getText().toString(), passwordEditText.getText().toString());

        deWatchClient client = deWatchServer.createService(deWatchClient.class);
        Call<AuthResponseObject> call = client.signUpNewUser(requestData);
        call.enqueue(new Callback<AuthResponseObject>() {
            @Override
            public void onResponse(Call<AuthResponseObject> call, Response<AuthResponseObject> response) {
                String firstName = response.body().getFirstName();
                String lastName = response.body().getLastName();
                String email = response.body().getEmail();
                String uid = response.body().getUid();
                String gender = response.body().getGender();
                int age = response.body().getAge();
                int weight = response.body().getWeight();

                currentUser.setCurrentUser(firstName, lastName, email, String.valueOf(age), String.valueOf(weight), gender, uid);
                //Toast.makeText(SignUpActivity.this, response.body().getFirstName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<AuthResponseObject> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "error signing up" + t.getMessage());
            }
        });
    }

    private boolean isValid(EditText field){
        if(field.getText().toString().length() > 0){
            return true;
        }
        else {
            field.setError("Field cannot be left blank!");
            Toast.makeText(SignUpActivity.this, "Field cannot be left blank!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean passwordValid(EditText password, EditText cpassword){
        if(password.getText().toString().length() >= 6){
            if(password.getText().toString().equals(cpassword.getText().toString())){
                return true;
            }
            else{
                password.setError("Passwords does not match!");
                Toast.makeText(SignUpActivity.this, "Passwords does not match!", Toast.LENGTH_SHORT).show();
                cpassword.setText(null);
                return false;
            }
        }
        else{
            password.setError("Password should be at least 6 characters");
            Toast.makeText(SignUpActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            cpassword.setText(null);
            return false;
        }
    }

    private boolean emailValid(EditText email){
        if(email.getText().toString().length() > 0 &&
                email.getText().toString().contains("@") &&
                email.getText().toString().contains(".")){
            return true;
        }
        else{
            email.setError("Invalid Email!");
            Toast.makeText(SignUpActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

}

package com.example.annie.dewatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by krisley3094 on 10/03/18.
 */

//Singleton class for tracking current_user
public class User {
    private static User current_user = null;

    private String first_name;
    private String last_name;
    private String email;
    private String age;
    private String weight;
    private String gender;
    private String uid;

    protected User() {

    }
    public static User getCurrentUser(){
        if(current_user == null){
            current_user = new User();
        }
        return current_user;
    }

    public void setCurrentUser(String first_name, String last_name, String email, String age, String weight, String gender, String uid){
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.age = age;
        this.weight = weight;
        this.gender = gender;
        this.uid = uid;
    }

    public String getFirstName() { return first_name; }

    public String getLastName() { return last_name; }

    public String getEmail() { return email; }

    public String getAge() { return age; }

    public String getWeight() { return weight; }

    public String getGender() { return gender; }

    public String getUid() { return uid; }


    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLoggedIn(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();

        // Save user info
        editor.putBoolean("loggedIn", true);
        editor.putString("email", email);
        editor.putString("firstName", first_name);
        editor.putString("lastName", last_name);
        editor.putString("age", age);
        editor.putString("weight", weight);
        editor.putString("gender", gender);
        editor.putString("uid", uid);
        editor.apply();
    }

    public void setLoggedOff(Context context) {
        current_user = null;

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();

        // Remove user info
        editor.clear();
        editor.apply();
    }

}

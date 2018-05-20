package com.example.annie.dewatch.deWatchClient.Classes;

/**
 * Created by krisley3094 on 10/03/18.
 */

public class NewUserRequestObject {

    private String first_name;
    private String last_name;
    private String gender;  // Drop down box
    private int age;
    private int weight;
    private String email;
    private String password;

    public NewUserRequestObject(String first_name, String last_name,
                                String gender, int age, int weight,
                                String email, String password){
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.email = email;
        this.password = password;

    }
}

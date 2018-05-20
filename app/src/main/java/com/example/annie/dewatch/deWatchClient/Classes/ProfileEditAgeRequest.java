package com.example.annie.dewatch.deWatchClient.Classes;

/**
 * Created by krisley3094 on 03/04/18.
 */

public class ProfileEditAgeRequest {
    private String uid;
    private int age;

    public ProfileEditAgeRequest(String uid, int age) {
        this.uid = uid;
        this.age = age;
    }
}

package com.example.annie.dewatch.deWatchClient.Classes;

/**
 * Created by krisley3094 on 03/04/18.
 */

public class ProfileEditFirstNameRequest {
    private String uid;
    private String first_name;

    public ProfileEditFirstNameRequest(String uid, String first_name) {
        this.uid = uid;
        this.first_name = first_name;
    }
}

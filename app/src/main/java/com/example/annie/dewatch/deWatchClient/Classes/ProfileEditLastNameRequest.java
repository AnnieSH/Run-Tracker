package com.example.annie.dewatch.deWatchClient.Classes;

/**
 * Created by krisley3094 on 03/04/18.
 */

public class ProfileEditLastNameRequest {
    private String uid;
    private String last_name;

    public ProfileEditLastNameRequest(String uid, String last_name) {
        this.uid = uid;
        this.last_name = last_name;
    }
}

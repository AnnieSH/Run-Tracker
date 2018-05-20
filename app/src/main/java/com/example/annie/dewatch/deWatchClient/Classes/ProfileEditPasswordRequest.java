package com.example.annie.dewatch.deWatchClient.Classes;

/**
 * Created by krisley3094 on 03/04/18.
 */

public class ProfileEditPasswordRequest {
    private String uid;
    private String password;

    public ProfileEditPasswordRequest(String uid, String password) {
        this.uid = uid;
        this.password = password;
    }
}

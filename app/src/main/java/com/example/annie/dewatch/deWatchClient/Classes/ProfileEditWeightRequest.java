package com.example.annie.dewatch.deWatchClient.Classes;

/**
 * Created by krisley3094 on 03/04/18.
 */

public class ProfileEditWeightRequest {
    private String uid;
    private int weight;

    public ProfileEditWeightRequest(String uid, int weight) {
        this.uid = uid;
        this.weight = weight;
    }
}

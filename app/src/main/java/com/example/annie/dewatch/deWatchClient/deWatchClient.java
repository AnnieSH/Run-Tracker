package com.example.annie.dewatch.deWatchClient;

import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordRequestReadObject;
import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordRequestWriteObject;
import com.example.annie.dewatch.deWatchClient.Classes.ExerciseRecordResponseObject;
import com.example.annie.dewatch.deWatchClient.Classes.LoginRequestObject;
import com.example.annie.dewatch.deWatchClient.Classes.NewUserRequestObject;
import com.example.annie.dewatch.deWatchClient.Classes.AuthResponseObject;
import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditAgeRequest;
import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditEmailRequest;
import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditFirstNameRequest;
import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditLastNameRequest;
import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditPasswordRequest;
import com.example.annie.dewatch.deWatchClient.Classes.ProfileEditWeightRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by krisley3094 on 08/03/18.
 */

public interface deWatchClient {
    @POST("/auth/register")
    Call<AuthResponseObject> signUpNewUser(
            @Body NewUserRequestObject data
    );

    @POST("/auth/login")
    Call<AuthResponseObject> signInUser(
            @Body LoginRequestObject data
    );

    @POST("/records/read")
    Call<List<ExerciseRecordResponseObject>> readExerRecords(
            @Body ExerciseRecordRequestReadObject data
    );

    @POST("/records/write")
    Call<Void> writeExerRecords(
            @Body ExerciseRecordRequestWriteObject data
    );

    @POST("/profile/edit_age")
    Call<Void> profileEditAge(
            @Body ProfileEditAgeRequest data
    );

    @POST("/profile/edit_email")
    Call<Void> profileEditEmail(
            @Body ProfileEditEmailRequest data
    );

    @POST("/profile/edit_first_name")
    Call<Void> profileEditFirstName(
            @Body ProfileEditFirstNameRequest data
    );

    @POST("/profile/edit_last_name")
    Call<Void> profileEditLastName(
            @Body ProfileEditLastNameRequest data
    );

    @POST("/profile/edit_password")
    Call<Void> profileEditPassword(
            @Body ProfileEditPasswordRequest data
    );

    @POST("/profile/edit_weight")
    Call<Void> profileEditWeight(
            @Body ProfileEditWeightRequest data
    );

}

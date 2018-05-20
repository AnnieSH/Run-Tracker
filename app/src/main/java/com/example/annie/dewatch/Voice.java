package com.example.annie.dewatch;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xiezi on 2018-03-21.
 */

public class Voice {
    private String TAG = "VOICE : ";

    private SpeechClient speech;
    private Context context;
    private String voiceContent;

    public Voice(Context context) {

        this.context = context;
        voiceContent = new String();
    }



    public int voiceRecognizer(byte[] audioData){
        try {
            AssetManager assetManager = context.getAssets();
            InputStream keyInputStream = assetManager.open("cpen-91c7e0d5c2d9.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(keyInputStream);
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

            SpeechSettings speechSettings =
                    SpeechSettings.newBuilder()
                            .setCredentialsProvider(credentialsProvider)
                            .build();

            speech = SpeechClient.create(speechSettings);

            ByteString audioBytes = ByteString.copyFrom(audioData);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();
            // Performs speech recognition on the audio file
            RecognizeResponse response = speech.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            StringBuilder builder = new StringBuilder();
            for (SpeechRecognitionResult result : results) {
                Log.d("CLIENT READY : ", "inner");
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                builder.append(alternative.getTranscript());
                builder.append(" ");
            }
            voiceContent = builder.toString();
            Log.d(TAG, "what has been said : "+ voiceContent);
            try {
                speech.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Actual word : " + builder.toString());
            String words = builder.toString();
            String[] wordsArray = words.split(" ");

            List<String> listStr = Arrays.asList(wordsArray);

            if(listStr.contains("start") || listStr.contains("begin")){
                return 1;
            }
            else if(listStr.contains("stop")){
                return 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int localRecognizer(byte[] audioData){
        try {
            AssetManager assetManager = context.getAssets();
            InputStream keyInputStream = assetManager.open("cpen-91c7e0d5c2d9.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(keyInputStream);
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

            SpeechSettings speechSettings =
                    SpeechSettings.newBuilder()
                            .setCredentialsProvider(credentialsProvider)
                            .build();

            speech = SpeechClient.create(speechSettings);

            ByteString audioBytes = ByteString.copyFrom(audioData);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.AMR_WB)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();
            // Performs speech recognition on the audio file
            RecognizeResponse response = speech.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            StringBuilder builder = new StringBuilder();
            for (SpeechRecognitionResult result : results) {
                Log.d("CLIENT READY : ", "inner");
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                builder.append(alternative.getTranscript());
                builder.append(" ");
            }
            voiceContent = builder.toString();
            Log.d(TAG, "what has been said : "+ voiceContent);
            try {
                speech.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Actual word : " + builder.toString());
            String words = builder.toString();
            String[] wordsArray = words.split(" ");

            List<String> listStr = Arrays.asList(wordsArray);

            if(listStr.contains("start") || listStr.contains("begin")){
                return 1;
            }
            else if(listStr.contains("stop")){
                return 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public String getVoiceString() {

        return voiceContent;
    }
}






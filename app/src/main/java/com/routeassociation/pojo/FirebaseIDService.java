package com.routeassociation.pojo;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by techlead on 6/12/17.
 */

public class FirebaseIDService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        System.out.println(s);
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {

        // Add custom implementation, as needed.

        SharedPreferences settings = getSharedPreferences("REG_TOKEN", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token",token );
        editor.commit();

      // fmsLMCcc1Ys:APA91bF8znZwWVPLUzjXCQHLNDijsYqCB9OEM0Sk3TWYhBoSFwVOqnX8W3QIuW89GO-CO61VTliW_a53LLM9SyM0Id93fhMvM3FS5wGuLX6syub3qP-JIDSJRSjvTMoX0y28Ikq2Nygt

    }
}

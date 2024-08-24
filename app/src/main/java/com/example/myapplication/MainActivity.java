package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    //TODO: ASSIGN RELEVANT URLS TO THE STRINGS!
    private static final String LOGIN_URL = "";
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_LOGGED_IN = "loggedIn";
    private static final String KEY_USER_NAME = "userName";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_LOGGED_IN, false);

        if (isLoggedIn) {
            // User is logged in, navigate directly to the main activity
            Intent intent = new Intent(MainActivity.this, OnGoingRequestsActivity.class);
            intent.putExtra("USER_NAME", prefs.getString(KEY_USER_NAME, ""));
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    @SuppressLint("SetTextI18n")
    public void onLoginBtnClick(View view) {
        EditText edtTxtUsername = findViewById(R.id.edtTxtUsername);
        EditText edtTxtPassword = findViewById(R.id.edtTxtPassword);

        String username = edtTxtUsername.getText().toString();
        String password = edtTxtPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "İsim ve şifre alanı boş bırakılamaz!", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            String result = performLogin(username, password);
            handler.post(() -> {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    if (jsonResponse.has("error")) {
                        Toast.makeText(MainActivity.this, jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                    } else {
                        //Saving login state and user name in SharedPreferences
                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(KEY_LOGGED_IN, true);
                        editor.putString(KEY_USER_NAME, jsonResponse.getString("kullanici_adi"));
                        editor.apply();

                        //Redirecting the user to the OnGoingRequestsActivity
                        Intent intent = new Intent(MainActivity.this, OnGoingRequestsActivity.class);
                        intent.putExtra("USER_NAME", jsonResponse.getString("kullanici_adi"));
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String performLogin(String username, String password) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(LOGIN_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String postData = "kullanici_adi=" + username + "&kullanici_sifre=" + password;
            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Request failed: HTTP error code " + responseCode;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Request failed: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

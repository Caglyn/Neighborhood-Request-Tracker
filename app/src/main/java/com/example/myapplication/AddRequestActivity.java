package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRequestActivity extends AppCompatActivity {

    //TODO: ASSIGN RELEVANT URLS TO THE STRINGS!
    private static final String GET_MUHTARS_URL = "";
    private static final String GET_REQUEST_TYPES_URL = "";
    private static final String INSERT_REQUEST_URL = "";

    private EditText etRequestTopic, etRequestDescription;
    private AutoCompleteTextView etRequestMuhtar;
    private Spinner spinnerRequestType;
    private Button btnSubmitRequest;
    private Map<String, Muhtar> muhtarMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);

        etRequestTopic = findViewById(R.id.etRequestTopic);
        etRequestDescription = findViewById(R.id.etRequestDescription);
        etRequestMuhtar = findViewById(R.id.etRequestMuhtar);
        spinnerRequestType = findViewById(R.id.spinnerRequestType);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);

        muhtarMap = new HashMap<>();

        // Set up AutoCompleteTextView and Spinner
        setupMuhtarAutoComplete();
        setupRequestTypeSpinner();

        btnSubmitRequest.setOnClickListener(v -> {
            String topic = etRequestTopic.getText().toString().trim();
            String description = etRequestDescription.getText().toString().trim();
            String selectedItem = etRequestMuhtar.getText().toString().trim();
            String requestType = spinnerRequestType.getSelectedItem().toString();

            if (TextUtils.isEmpty(topic) || TextUtils.isEmpty(description) || TextUtils.isEmpty(selectedItem)) {
                Toast.makeText(AddRequestActivity.this, "Lütfen gerekli tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            Muhtar selectedMuhtar;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                selectedMuhtar = muhtarMap.values().stream().filter(muhtar -> muhtar.toString().equals(selectedItem)).findFirst().orElse(null);
            } else {
                selectedMuhtar = null;
            }

            if (selectedMuhtar == null) {
                Toast.makeText(AddRequestActivity.this, "Muhtar bilgileri eksik.", Toast.LENGTH_SHORT).show();
                return;
            }

            @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String createdBy = getIntent().getStringExtra("USER_NAME");
            String kisiTur = selectedMuhtar.muhtarMi == 0 ? "VATANDAŞ" : "MUHTAR";

            new Thread(() -> {
                try {
                    URL url = new URL(INSERT_REQUEST_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    String postData = "talep_konu=" + topic +
                            "&talep_aciklama=" + description +
                            "&talep_muhtar=" + selectedMuhtar.name +
                            "&talep_ilce=" + selectedMuhtar.ilce +
                            "&talep_mahalle=" + selectedMuhtar.mahalle +
                            "&talep_olusturan=" + createdBy +
                            "&talep_turu=" + requestType +
                            "&kisi_tur=" + kisiTur +
                            "&talep_durumu=Devam Ediyor" +
                            "&talep_sonucu=" +
                            "&talep_tarihi=" + currentDate +
                            "&talep_tamamlanma_tarihi=";

                    OutputStream os = connection.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddRequestActivity.this, "Talep başarıyla eklendi.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddRequestActivity.this, OnGoingRequestsActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(AddRequestActivity.this, "Talep eklenirken hata oluştu.", Toast.LENGTH_SHORT).show());
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private void setupMuhtarAutoComplete() {
        new Thread(() -> {
            try {
                String response = performApiRequest(GET_MUHTARS_URL);
                JSONArray jsonArray = new JSONArray(response);
                final List<Muhtar> muhtarList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String muhtarName = jsonObject.getString("muhtar_adisoyadi");
                    String ilce = jsonObject.getString("muhtar_ilce");
                    String mahalle = jsonObject.getString("muhtar_mahalle");
                    int muhtarMi = jsonObject.getInt("muhtar_mi");

                    Muhtar muhtar = new Muhtar(muhtarName, ilce, mahalle, muhtarMi);
                    muhtarList.add(muhtar);
                    muhtarMap.put(muhtarName, muhtar);
                }
                runOnUiThread(() -> {
                    MuhtarAdapter adapter = new MuhtarAdapter(this, muhtarList);
                    etRequestMuhtar.setAdapter(adapter);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupRequestTypeSpinner() {
        new Thread(() -> {
            try {
                String response = performApiRequest(GET_REQUEST_TYPES_URL);
                JSONArray jsonArray = new JSONArray(response);
                String[] requestTypes = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    requestTypes[i] = jsonObject.getString("tur_adi");
                }
                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, requestTypes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerRequestType.setAdapter(adapter);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String performApiRequest(String apiUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

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
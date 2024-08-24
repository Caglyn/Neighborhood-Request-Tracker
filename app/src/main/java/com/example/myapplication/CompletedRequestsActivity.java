package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletedRequestsActivity extends AppCompatActivity {

    //TODO: ASSIGN RELEVANT URLS TO THE STRINGS!
    private static final String API_URL = "";
    private static final String COMPLETED_ID_API_URL = "";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private LinearLayout cardContainer;
    private String user;
    private String requestIdGlobal;
    private AutoCompleteTextView searchView;
    private List<String> requestTitles = new ArrayList<>();
    private List<CardData> cardDataList = new ArrayList<CardData>(); // Store card data here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_bar);
        user = getIntent().getStringExtra("USER_NAME");

        cardContainer = findViewById(R.id.cardContainer);
        searchView = findViewById(R.id.searchView);

        Button btnOngoingRequests = findViewById(R.id.btnOngoingRequests);
        Button btnCompletedRequests = findViewById(R.id.btnCompletedRequests);
        Button btnAddRequest = findViewById(R.id.btnAddRequest);

        btnCompletedRequests.setAlpha(0.5f);

        btnOngoingRequests.setOnClickListener(v -> {
            // Navigate to PendingRequestsActivity
            Intent intent = new Intent(CompletedRequestsActivity.this, OnGoingRequestsActivity.class);
            intent.putExtra("REQUEST_ID", requestIdGlobal);
            intent.putExtra("USER_NAME", user);
            startActivity(intent);
            finish();
        });

        btnCompletedRequests.setOnClickListener(v -> {
            // Current activity, no action needed
        });

        btnAddRequest.setOnClickListener(v -> {
            Intent intent = new Intent(CompletedRequestsActivity.this, AddRequestActivity.class);
            intent.putExtra("USER_NAME", user); // Pass any required data
            startActivity(intent);
            finish();
        });

        fetchRequests();
    }

    private void fetchRequests() {
        executor.execute(() -> {
            String result = performApiRequest();
            handler.post(() -> {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    requestTitles.clear(); // Clear previous titles
                    cardDataList.clear(); // Clear previous data
                    populateCardViews(jsonArray);
                    setupSearchView();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(CompletedRequestsActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void fetchRequestDetails(String requestId) {
        executor.execute(() -> {
            String result = performRequestDetailsApiRequest(requestId);
            handler.post(() -> {
                // Debug output
                Log.d("Request Details Rponse", result);

                try {
                    JSONObject jsonResponse = new JSONObject(result);

                    // Optional: Check if the JSON response contains the required fields
                    if (jsonResponse.has("talep_id")) {
                        // Handle the response and show details
                        Intent intent = new Intent(CompletedRequestsActivity.this, RequestDetailActivity.class);
                        intent.putExtra("REQUEST_DETAILS", jsonResponse.toString());
                        intent.putExtra("REQUEST_ID", requestIdGlobal);
                        intent.putExtra("USER_NAME", user);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CompletedRequestsActivity.this, "Bu talep için detay bilgisi bulunamadı.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(CompletedRequestsActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String performRequestDetailsApiRequest(String requestId) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(COMPLETED_ID_API_URL + requestId);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Write POST data
            String postData = "talep_id=" + requestId;
            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Request failed: HTTP error code " + responseCode;
            }

            // Read response
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

    private String performApiRequest() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Request failed: HTTP error code " + responseCode;
            }

            // Read response
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

    private void setupSearchView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, requestTitles);
        searchView.setAdapter(adapter);
        searchView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCombinedText = (String) parent.getItemAtPosition(position);
            // Find the requestId for the selectedCombinedText
            for (CardData cardData : cardDataList) {
                if (cardData.getCombinedText().equals(selectedCombinedText)) {
                    fetchRequestDetails(cardData.requestId);
                    break;
                }
            }
            // Clear the search bar and the focus
            searchView.setText("");
            searchView.clearFocus();
        });
    }

    @SuppressLint("SetTextI18n")
    private void populateCardViews(JSONArray jsonArray) {
        LayoutInflater inflater = LayoutInflater.from(this);
        cardContainer.removeAllViews();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Inflate CardView layout
                View cardView = inflater.inflate(R.layout.card_view_item, cardContainer, false);
                CardView card = cardView.findViewById(R.id.cardView);
                TextView cardTitle = cardView.findViewById(R.id.cardTitle);
                TextView cardContent = cardView.findViewById(R.id.cardContent);
                TextView cardContent2 = cardView.findViewById(R.id.cardContent2);

                // Set Title data
                String requestTitle = jsonObject.getString("talep_konu");
                String requestId = jsonObject.getString("talep_id");
                String requestMuhtar = jsonObject.getString("talep_muhtar");
                String combinedText = requestMuhtar + " - " + requestTitle;
                cardTitle.setText(requestId + " - " + requestTitle);
                requestTitles.add(combinedText);

                // Set Content data
                String requestType = jsonObject.getString("talep_turu");
                String requestStatus = jsonObject.getString("talep_durumu");
                String requestDate = jsonObject.getString("talep_tarihi");
                String requestResult = jsonObject.getString("talep_sonucu");
                String requestMahalle = jsonObject.getString("talep_mahalle");
                String requestIlce = jsonObject.getString("talep_ilce");

                cardContent.setText(requestMuhtar + " - " + requestMahalle + "/" + requestIlce + "\n" + requestType);
                cardContent2.setText("Tarih: " + requestDate + " - " + requestStatus + "\nTalep Sonucu: " + requestResult);

                // Store card data
                cardDataList.add(new CardData(requestId, requestTitle, requestMuhtar, requestType, requestStatus, requestDate, requestResult));

                // Set tag for identifying the card
                card.setTag(requestId);

                // Set click listener
                card.setOnClickListener(v -> {
                    String clickedRequestId = (String) v.getTag();
                    fetchRequestDetails(clickedRequestId);
                });

                // Add CardView to container
                cardContainer.addView(cardView);

            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(CompletedRequestsActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
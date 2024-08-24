package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestDetailActivity extends AppCompatActivity {

    //TODO: ASSIGN RELEVANT URLS TO THE STRINGS!
    private static final String COMMENTS_API_URL = "";
    private static final String ADD_COMMENT_URL = "";
    private static final String UPDATE_REQUEST_URL = "";
    private static final String MUHTAR_API_URL = "";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private LinearLayout commentsContainer;
    private String user;
    private int requestId;
    private String muhtarTelefonGlobal;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        // Find views
        TextView tvRequestTitle = findViewById(R.id.tvRequestTitle);
        TextView tvRequestMuhtar = findViewById(R.id.tvRequestMuhtar);
        TextView tvRequestMuhtarNumara = findViewById(R.id.tvRequestMuhtarNumara);
        TextView tvRequestType = findViewById(R.id.tvRequestType);
        TextView tvRequestStatus = findViewById(R.id.tvRequestStatus);
        TextView tvRequestDate = findViewById(R.id.tvRequestDate);
        TextView tvRequestDescription = findViewById(R.id.tvRequestDescription);
        TextView tvRequestAdress = findViewById(R.id.tvRequestAdress);
        TextView tvRequestResult = findViewById(R.id.tvRequestResult);
        commentsContainer = findViewById(R.id.commentsContainer);
        LinearLayout navBar = findViewById(R.id.navBar);
        TextView tvRequestCompletionDate = findViewById(R.id.tvRequestCompletionDate);

        Button btnChangeStatus = findViewById(R.id.btnChangeStatus);
        btnChangeStatus.setOnClickListener(v -> showFinalizeResultDialog());

        // Extract request details from the intent
        String requestDetails = getIntent().getStringExtra("REQUEST_DETAILS");

        try {
            assert requestDetails != null;
            JSONObject jsonObject = new JSONObject(requestDetails);

            // Extract and set data
            requestId = Integer.parseInt(jsonObject.getString("talep_id"));
            String requestKonu = jsonObject.getString("talep_konu");
            String requestMuhtar = jsonObject.getString("talep_muhtar");
            String requestType = jsonObject.getString("talep_turu");
            String requestStatus = jsonObject.getString("talep_durumu");
            String requestDate = jsonObject.getString("talep_tarihi");
            String requestDescription = jsonObject.getString("talep_aciklama");
            String requestIlce = jsonObject.getString("talep_ilce");
            String requestMahalle = jsonObject.getString("talep_mahalle");
            String requestResult = jsonObject.getString("talep_sonucu");
            String requestCompletionDate = jsonObject.getString("talep_tamamlanma_tarihi");

            tvRequestTitle.setText(requestId + " - " + requestKonu);
            tvRequestMuhtar.setText("Muhtar: " + requestMuhtar);
            tvRequestMuhtarNumara.setText("Muhtar Telefon Numarası: ");
            tvRequestAdress.setText("Adres: " + requestMahalle + "/" + requestIlce);
            tvRequestType.setText("İstek Türü: " + requestType);
            tvRequestStatus.setText("Durum: " + requestStatus);
            tvRequestDate.setText("Tarih: " + requestDate);
            tvRequestDescription.setText("Açıklama: " + requestDescription);
            tvRequestResult.setText("Talep Sonucu: " + requestResult);
            tvRequestCompletionDate.setText("Tamamlanma Tarihi: " + requestCompletionDate);

            // Show or hide the navigation bar based on request status
            if ("Devam Ediyor".equals(requestStatus)) {
                navBar.setVisibility(View.VISIBLE);
                tvRequestCompletionDate.setVisibility(View.GONE);
            } else {
                navBar.setVisibility(View.GONE);
                tvRequestCompletionDate.setVisibility(View.VISIBLE);
            }

            // Fetch and display comments
            fetchComments(String.valueOf(requestId));

            // Fetch and display muhtar details
            fetchMuhtarDetails(requestMuhtar);

            findViewById(R.id.cardViewDetail).setOnClickListener(v -> dialPhoneNumber(muhtarTelefonGlobal));

        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error
        }

        // Retrieve user from Intent
        user = getIntent().getStringExtra("USER_NAME");

        Button btnAddComment = findViewById(R.id.btnAddComment);
        btnAddComment.setOnClickListener(v -> showCommentBottomSheet());
    }

    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void showFinalizeResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Talep Sonucu");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_status, null);
        builder.setView(dialogView);

        RadioGroup rgResult = dialogView.findViewById(R.id.rgResult);

        builder.setPositiveButton("Gönder", (dialog, which) -> {
            //Saving selected status and result
            int selectedResultId = rgResult.getCheckedRadioButtonId();

            RadioButton rbResult = dialogView.findViewById(selectedResultId);

            String selectedResult = rbResult != null ? rbResult.getText().toString() : null;

            if (selectedResult == null) {
                Toast.makeText(RequestDetailActivity.this, "Lütfen bir sonuç seçin.", Toast.LENGTH_SHORT).show();
                return;
            }

            //Updating the result of the request
            updateRequestResult(selectedResult);
        });

        builder.setNegativeButton("İptal Et", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateRequestResult(String result) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(UPDATE_REQUEST_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));
                String currentDateTime = sdf.format(new Date());

                //Prepare POST data with URL encoding
                String postData = "talep_id=" + URLEncoder.encode(String.valueOf(requestId), "UTF-8") +
                        "&talep_durumu=" + URLEncoder.encode("Tamamlandı" != null ? "Tamamlandı" : "", "UTF-8") +
                        "&talep_sonucu=" + URLEncoder.encode(result != null ? result : "", "UTF-8") +
                        "&talep_tamamlanma_tarihi=" + URLEncoder.encode(currentDateTime, "UTF-8");

                //Send POST data
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(RequestDetailActivity.this, "Talep durumu güncellendi.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RequestDetailActivity.this, CompletedRequestsActivity.class);
                        startActivity(intent);

                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(RequestDetailActivity.this, "Güncelleme başarısız. HTTP response code: " + responseCode, Toast.LENGTH_SHORT).show());
                }

            } catch (IOException e) {
                e.printStackTrace();
                //runOnUiThread(() -> Toast.makeText(RequestDetailActivity.this, "Güncelleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void showCommentBottomSheet() {
        CommentBottomSheetFragment bottomSheetFragment = new CommentBottomSheetFragment(commentText -> {
            // Handle the comment submission
            submitComment(commentText);
        });
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @SuppressLint("SimpleDateFormat")
    private void submitComment(String commentText) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        String dateTime = sdf.format(new Date());

        executor.execute(() -> {
            String status = addComment(requestId, commentText, user, dateTime);
            runOnUiThread(() -> {
                if ("success".equals(status)) {
                    Toast.makeText(RequestDetailActivity.this, "Yorum gönderildi!", Toast.LENGTH_SHORT).show();
                    // Fetch and display updated comments
                    fetchComments(String.valueOf(requestId));
                } else {
                    Toast.makeText(RequestDetailActivity.this, "Yorum gönderilemedi: " + status, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String addComment(int requestId, String commentText, String user, String dateTime) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ADD_COMMENT_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String postData = "yorum_gid=" + requestId +
                    "&yorum_yazisi=" + commentText +
                    "&yorum_kullanici=" + user +
                    "&yorum_tarihi=" + dateTime;
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

            //Parsing JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("status");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "Request failed: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void fetchComments(String requestId) {
        executor.execute(() -> {
            String result = performApiRequest(requestId);
            runOnUiThread(() -> {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    populateCommentViews(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RequestDetailActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void fetchMuhtarDetails(String muhtarAdisoyadi) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(MUHTAR_API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Prepare POST data
                String postData = "muhtar_adisoyadi=" + URLEncoder.encode(muhtarAdisoyadi, "UTF-8");

                // Send POST data
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    runOnUiThread(() -> {
                        try {
                            String muhtarTelefon = jsonResponse.getString("muhtar_telefon");
                            TextView tvRequestMuhtarNumara = findViewById(R.id.tvRequestMuhtarNumara);
                            tvRequestMuhtarNumara.setText("Muhtar Telefon Numarası: " + muhtarTelefon);
                            muhtarTelefonGlobal = muhtarTelefon;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(RequestDetailActivity.this, "Error parsing muhtar data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }/* else {
                    runOnUiThread(() -> Toast.makeText(RequestDetailActivity.this, "Failed to fetch muhtar details: HTTP response code " + responseCode, Toast.LENGTH_SHORT).show());
                }*/

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                //runOnUiThread(() -> Toast.makeText(RequestDetailActivity.this, "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private String performApiRequest(String requestId) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(COMMENTS_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Write parameters to output stream
            String postData = "talep_id=" + requestId;
            connection.getOutputStream().write(postData.getBytes("UTF-8"));

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

    private void populateCommentViews(JSONArray jsonArray) {
        LayoutInflater inflater = LayoutInflater.from(this);
        commentsContainer.removeAllViews();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Inflate CardView layout for comments
                View cardView = inflater.inflate(R.layout.card_view_item, commentsContainer, false);
                CardView card = cardView.findViewById(R.id.cardView);
                TextView tvCommentUser = cardView.findViewById(R.id.cardTitle);
                TextView tvCommentDate = cardView.findViewById(R.id.cardContent);
                TextView tvCommentContent = cardView.findViewById(R.id.cardContent2);

                // Set data
                String commentUser = jsonObject.getString("yorum_kullanici");
                String commentDate = jsonObject.getString("yorum_tarihi");
                String commentContent = jsonObject.getString("yorum_yazisi");

                tvCommentUser.setText(commentUser);
                tvCommentDate.setText(commentDate);
                tvCommentContent.setText(commentContent);

                // Add CardView to container
                commentsContainer.addView(cardView);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
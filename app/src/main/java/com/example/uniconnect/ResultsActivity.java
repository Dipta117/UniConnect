package com.example.uniconnect;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";
    private RequestQueue mQueue;
    private RecyclerView recyclerView;
    private CustomRecyclerAdapter2 mAdapter;
    private List<ScholarModel> modelList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Log.d(TAG, "onCreate: Initializing components");

        mQueue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchScholarData);

        modelList = new ArrayList<>();

        query = getIntent().getStringExtra("query");
        fetchScholarData();
    }

    private void fetchScholarData() {
        String apiKey = "675597c795064df35815ca02";
        String language = "en";
        int page = 0;
        int results = 10;

        String apiUrl = "https://api.scrapingdog.com/google_scholar/?api_key=" + apiKey
                + "&query=" + query
                + "&language=" + language
                ;

        Log.d(TAG, "fetchScholarData: Requesting URL: " + apiUrl);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: Raw response: " + response.toString());
                        try {
                            JSONArray scholarResults = response.getJSONArray("scholar_results");

                            modelList.clear(); // Clear the list before adding new items
                            for (int i = 0; i < scholarResults.length(); i++) {
                                JSONObject result = scholarResults.getJSONObject(i);

                                String title = result.getString("title");
                                String titleLink = result.getString("title_link");
                                String displayedLink = result.getString("displayed_link");
                                String snippet = result.getString("snippet");

                                // Create and populate the ScholarModel object
                                ScholarModel model = new ScholarModel(title, titleLink, displayedLink, snippet);

                                // Add the model to the list
                                modelList.add(model);
                            }

                            Log.d(TAG, "onResponse: Finished parsing JSON. Total items: " + modelList.size());
                            mAdapter = new CustomRecyclerAdapter2(ResultsActivity.this, modelList);
                            recyclerView.setAdapter(mAdapter);
                            swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation

                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: JSON Parsing error: " + e.getMessage(), e);
                            Toast.makeText(ResultsActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error occurred", error);

                        String errorMessage = error.getMessage();
                        if (error.networkResponse != null) {
                            errorMessage = "HTTP Status Code: " + error.networkResponse.statusCode;
                        }
                        Toast.makeText(ResultsActivity.this, "Failed to load results: " + errorMessage, Toast.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
                    }
                });

        Log.d(TAG, "fetchScholarData: Adding request to queue");
        mQueue.add(request);
    }
}

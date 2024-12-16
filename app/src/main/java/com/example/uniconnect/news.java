package com.example.uniconnect;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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

public class news extends AppCompatActivity {

    private static final String TAG = "NewsActivity";
    private RequestQueue mQueue;
    private RecyclerView recyclerView;
    private CustomRecyclerAdapter mAdapter;
    private List<Model> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Log.d(TAG, "onCreate: Initializing components");
        mQueue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        modelList = new ArrayList<>();
        jsonParse();
    }

    private void jsonParse() {
        String url = "https://api.myjson.online/v1/records/edaee6ec-3b90-45d6-b1a8-458534692105";
        Log.d(TAG, "jsonParse: Requesting URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: Raw response: " + response.toString());
                        try {
                            // First, get the "data" object
                            JSONObject dataObject = response.getJSONObject("data");

                            // Then, get the "items" array from the "data" object
                            JSONArray jsonArray = dataObject.getJSONArray("items");
                            Log.d(TAG, "onResponse: JSON Array length: " + jsonArray.length());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject article = jsonArray.getJSONObject(i);

                                String title = article.getString("title");
                                String description = article.optString("snippet", "No description available");
                                String pubname=article.optString("publisher");
                                String time=article.optString("timestamp");
                                JSONObject images = article.optJSONObject("images");
                                String urlToImage = images != null ? images.optString("thumbnail", "") : ""; // Handle null safely
                                String newsUrl = article.getString("newsUrl");

                                Log.d(TAG, "onResponse: Parsed article " + i + " - Title: " + title);

                                Model modelUtils = new Model();
                                modelUtils.setTitle(title);
                                modelUtils.setDescription(description);
                                modelUtils.setAuthor(pubname);

                                modelUtils.setUrlToImage(urlToImage);
                                modelUtils.setUrl(newsUrl);

                                modelList.add(modelUtils);
                            }

                            Log.d(TAG, "onResponse: Finished parsing JSON. Total items: " + modelList.size());
                            mAdapter = new CustomRecyclerAdapter(news.this, modelList);
                            recyclerView.setAdapter(mAdapter);

                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: JSON Parsing error: " + e.getMessage(), e);
                            Toast.makeText(news.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: Error occurred", error);

                String errorMessage = error.getMessage();
                if (error.networkResponse != null) {
                    errorMessage = "HTTP Status Code: " + error.networkResponse.statusCode;
                }
                Toast.makeText(news.this, "Failed to load news: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        Log.d(TAG, "jsonParse: Adding request to queue");
        mQueue.add(request);
    }


}

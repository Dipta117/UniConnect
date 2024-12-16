package com.example.uniconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.swiperefreshlayout.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ArrayList<Post> postList;
    private DatabaseReference databaseReference;
    private EditText postEditText;
    private Button postButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        // Initialize UI elements
        recyclerView = rootView.findViewById(R.id.recyclerView);
        postEditText = rootView.findViewById(R.id.postEditText);
        postButton = rootView.findViewById(R.id.postButton);
        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        // Initialize RecyclerView
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postAdapter);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh logic here, e.g., re-fetch data
            fetchPosts(); // Replace with your data-fetching logic

            // Stop the refreshing animation
            swipeRefreshLayout.setRefreshing(false);
        });
        // Initialize Firebase reference
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child("Posts");


        // Set up the post button listener
        postButton.setOnClickListener(v -> postText());

        // Fetch posts from Firebase
        fetchPosts();

        return rootView;
    }

    public void onResume() {
        super.onResume();

        // Fetch the data again when the fragment comes into view


        fetchPosts();  // Re-fetch profile data

    }


    private void fetchPosts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        // Automatically detect and make links clickable in post text
                        String postText = post.getText();
                        if (isValidLink(postText)) {
                            postText = Html.fromHtml(postText, Html.FROM_HTML_MODE_LEGACY).toString(); // Convert links to HTML
                        }

                        post.setText(postText);  // Update post with clickable links
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching posts: " + error.getMessage());
            }
        });
    }


    private void postText() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", null);

        String postText = postEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(postText)) {
            // Check if the post contains a link
            if (isValidLink(postText)) {
                postText = Html.fromHtml(postText, Html.FROM_HTML_MODE_LEGACY).toString();  // Convert link to HTML
            }

            // Generate post ID and create a new Post object
            String postId = databaseReference.push().getKey();
            long timestamp = System.currentTimeMillis();

            Post post = new Post(postId, postText, name, 0, new ArrayList<>());

            // Save the post to Firebase
            databaseReference.child(postId).setValue(post).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Post successful!", Toast.LENGTH_SHORT).show();
                    postEditText.setText(""); // Clear the input field
                } else {
                    Toast.makeText(getContext(), "Error posting text.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please enter some text", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidLink(String text) {
        // Simple check for detecting URLs
        return text.startsWith("http://") || text.startsWith("https://");
    }
}

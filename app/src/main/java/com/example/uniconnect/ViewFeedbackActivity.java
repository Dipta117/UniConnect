package com.example.uniconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewFeedbackActivity extends AppCompatActivity {

    private ListView feedbackListView;
    private FeedbackAdapter feedbackAdapter;
    private DatabaseReference databaseReference;
    private ArrayList<Feedback> feedbackList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback);

        feedbackListView = findViewById(R.id.feedbackListView);
        feedbackAdapter = new FeedbackAdapter(this, feedbackList);
        feedbackListView.setAdapter(feedbackAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);
        String groupName = sharedPreferences.getString("cur_group", null);
        String adminKey = sharedPreferences.getString("group_admin_key", null);
        String role = sharedPreferences.getString("group_admin_role", null);
        String owner = sharedPreferences.getString("group_admin_key", null);
        String path = "Classrooms_" + groupName + "_" + owner;
        Intent intent = getIntent();
        String key = intent.getStringExtra("idddd");
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(path).child(key).child("feedback");

        fetchFeedbacks();
    }

    private void fetchFeedbacks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedbackList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Feedback feedback = snapshot.getValue(Feedback.class);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                    }
                }
                feedbackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewFeedbackActivity.this, "Failed to fetch feedbacks: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Feedback {
        private float rating;
        private String feedback;

        public Feedback() {
            // Default constructor required for calls to DataSnapshot.getValue(Feedback.class)
        }

        public Feedback(float rating, String feedback) {
            this.rating = rating;
            this.feedback = feedback;
        }

        public float getRating() {
            return rating;
        }

        public String getFeedback() {
            return feedback;
        }
    }

    public static class FeedbackAdapter extends ArrayAdapter<Feedback> {
        public FeedbackAdapter(Context context, ArrayList<Feedback> feedbackList) {
            super(context, 0, feedbackList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_feedback, parent, false);
            }
            Feedback feedback = getItem(position);

            TextView feedbackTextView = convertView.findViewById(R.id.feedbackTextView);
            RatingBar feedbackRatingBar = convertView.findViewById(R.id.feedbackRatingBar);
            if (feedback != null) {
                feedbackTextView.setText(feedback.getFeedback());
                feedbackRatingBar.setRating(feedback.getRating());
            }
            return convertView;
        }
    }
}

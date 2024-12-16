package com.example.uniconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar classRatingBar;
    private EditText feedbackEditText;
    private Button submitFeedbackButton;

    private ListView feedbackListView;
    private DatabaseReference databaseReference,databaseReference1;
    private String institution;
    private String adminKey;
    private String groupName;
    private boolean isAdmin;
    private FeedbackAdapter feedbackAdapter;
    private ArrayList<String> feedbackList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        classRatingBar = findViewById(R.id.classRatingBar);
        feedbackEditText = findViewById(R.id.feedbackEditText);
        submitFeedbackButton = findViewById(R.id.submitFeedbackButton);

        feedbackListView = findViewById(R.id.feedbackListView);

        Intent intent = getIntent();
        String key = intent.getStringExtra("idddd");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        institution = sharedPreferences.getString("institution", null);
        groupName = sharedPreferences.getString("cur_group", null);
        String myemail = sharedPreferences.getString("email", null);
        adminKey = sharedPreferences.getString("group_admin_key", null);
        String owner = sharedPreferences.getString("group_admin_key", null);
        String rol=sharedPreferences.getString("group_admin_role",null);
        // Save data under the generated classroom ID
        String mynm=sharedPreferences.getString("name",null);
        String path = "Classrooms_" + groupName + "_" + owner;
databaseReference1=FirebaseDatabase.getInstance().getReference("ProfileInfo")
        .child(institution)
        .child(path)
        .child(key)
        .child(mynm);
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(path).child(key).child("feedback");

            submitFeedbackButton.setOnClickListener(v -> submitFeedback());


        feedbackAdapter = new FeedbackAdapter(this, feedbackList);
        feedbackListView.setAdapter(feedbackAdapter);
    }



    private void submitFeedback() {
        float rating = classRatingBar.getRating();
        String feedback = feedbackEditText.getText().toString().trim();

        if (rating == 0 || feedback.isEmpty()) {
            Toast.makeText(this, "Please provide both rating and feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackId = databaseReference.push().getKey();
        Feedback feedbackData = new Feedback(rating, feedback);

        databaseReference.child(feedbackId).setValue(feedbackData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();

               databaseReference1.setValue("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public static class FeedbackAdapter extends ArrayAdapter<String> {
        public FeedbackAdapter(Context context, ArrayList<String> feedbackList) {
            super(context, 0, feedbackList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            String feedback = getItem(position);
            TextView feedbackTextView = convertView.findViewById(android.R.id.text1);
            feedbackTextView.setText(feedback);
            return convertView;
        }
    }
}

package com.example.uniconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SolutionActivity extends AppCompatActivity {

    private ListView submissionsListView;
    private SolutionAdapter solutionAdapter;
    private List<Submission> submissionList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);

        submissionsListView = findViewById(R.id.submissionsListView);
        submissionList = new ArrayList<>();


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);
        String groupname = sharedPreferences.getString("cur_group", null);
        String owner = sharedPreferences.getString("group_admin_key", null);
        String rasta = "Assignment_" + groupname + "_" + owner;
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(rasta).child(key).child("submissions");
        solutionAdapter = new SolutionAdapter(this, submissionList, databaseReference);
        submissionsListView.setAdapter(solutionAdapter);
        fetchSubmissions();
    }

    private void fetchSubmissions() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                submissionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    String content = snapshot.child("link").getValue(String.class);
                    String marks = snapshot.child("marks").getValue(String.class);

                    Submission submission = new Submission(id, content,marks);

                    submissionList.add(submission);
                }
                solutionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SolutionActivity.this, "Failed to load submissions.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

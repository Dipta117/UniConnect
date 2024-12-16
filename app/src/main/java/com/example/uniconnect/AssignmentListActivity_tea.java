package com.example.uniconnect;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AssignmentListActivity_tea extends AppCompatActivity {

    private ListView assignmentListView;
    private teacherAssignment assignmentListAdapter;
    private List<Assignment> assignmentList;
    private DatabaseReference databaseReference;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_assignment_list);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String myemail = sharedPreferences.getString("email", null);
        String sanitizedEmail = myemail.replace(".", "_").replace("@", "_");
        String institution = sharedPreferences.getString("institution", null);
        String groupname = sharedPreferences.getString("cur_group", null);
        String owner = sharedPreferences.getString("group_admin_key", null);

        if (institution == null || groupname == null || owner == null) {
            Toast.makeText(this, "Error: Missing data for Firebase path", Toast.LENGTH_SHORT).show();
            return;
        }

        String rasta = "Assignment_" + groupname + "_" + owner;
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(rasta);

        assignmentListView = findViewById(R.id.assignmentsListView);
        assignmentList = new ArrayList<>();
        assignmentListAdapter = new teacherAssignment(this, assignmentList);
        assignmentListView.setAdapter(assignmentListAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchAssignments();
            swipeRefreshLayout.setRefreshing(false);
        });

        fetchAssignments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAssignments();
    }

    private void fetchAssignments() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assignmentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String assignmentKey = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);
                    String des = snapshot.child("instructions").getValue(String.class);
                    String link = snapshot.child("link").getValue(String.class);
                    String deadline = snapshot.child("deadline").getValue(String.class);
                    String marks = snapshot.child("marks").getValue(String.class);
                    Assignment assignment = new Assignment(title, des, deadline, marks, link, assignmentKey);

                    if (assignment != null) {
                        assignmentList.add(assignment);
                    } else {
                        Log.d("FirebaseData", "Snapshot data is null or doesn't match the Assignment class.");
                    }
                }
                assignmentListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AssignmentListActivity_tea.this, "Failed to load assignments.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.uniconnect;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.*;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.EditText;
import android.widget.Toast;
import android.text.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.swiperefreshlayout.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.*;

public class AssignmentListFragment extends Fragment {

    private ListView assignmentListView;

    private AssignmentAdapter assignmentListAdapter;
    private List<Assignment> assignmentList;
    private DatabaseReference databaseReference;
    private SwipeRefreshLayout swipeRefreshLayout;
    public AssignmentListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_assignment_list, container, false);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
String myemail=sharedPreferences.getString("email",null);
        String sanitizedEmail = myemail.replace(".", "_").replace("@", "_");
        String institution = sharedPreferences.getString("institution", null);
        // Save data under the generated assignment ID
        String groupname=sharedPreferences.getString("cur_group",null);
        String owner=sharedPreferences.getString("group_admin_key",null);
        // Save data under the generated assignment ID
        String rasta="Assignment_"+groupname+"_"+owner;
        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(rasta);

        assignmentListView = rootView.findViewById(R.id.assignmentsListView);
        assignmentList = new ArrayList<>();

        assignmentListAdapter = new AssignmentAdapter(getContext(), assignmentList);
        assignmentListView.setAdapter(assignmentListAdapter);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        assignmentListView = rootView.findViewById(R.id.assignmentsListView);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh logic here, e.g., re-fetch data
            fetchAssignments(); // Replace with your data-fetching logic

            // Stop the refreshing animation
            swipeRefreshLayout.setRefreshing(false);
        });
        // Fetch data from Firebase
        fetchAssignments();

        return rootView;
    }
    public void onResume() {
        super.onResume();

        // Fetch the data again when the fragment comes into view


        fetchAssignments();  // Re-fetch profile data

    }
    private void fetchAssignments() {


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assignmentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  //  Assignment assignment = snapshot.getValue(Assignment.class);
                    String assignmentKey = snapshot.getKey();
                    String title=snapshot.child("title").getValue(String.class);
                    String des=snapshot.child("instructions").getValue(String.class);
                    String link=snapshot.child("link").getValue(String.class);
                    String deadline=snapshot.child("deadline").getValue(String.class);
                    String marks=snapshot.child("marks").getValue(String.class);
                    Assignment assignment=new Assignment(title,des,deadline,marks,link,assignmentKey);

                    if (assignment != null) {
                        //Log.d("FirebaseData", "Title: " + assignment.getTitle() + ", Description: " + assignment.getDescription());

                        assignmentList.add(assignment);
                    } else {
                        Log.d("FirebaseData", "Snapshot data is null or doesn't match the Assignment class.");
                    }
                }
                assignmentListAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load assignments.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}

package com.example.uniconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Roll extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button submitButton;

    private List<Member> members = new ArrayList<>();
    private MemberAdapter memberAdapter;
    private DatabaseReference databaseReference,databaseReference1;
    private Map<String, Boolean> attendanceMap = new HashMap<>();

    private String institution;
    private String groupName;
    private String adminKey, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_call);

        recyclerView = findViewById(R.id.recyclerView);
        submitButton = findViewById(R.id.submitButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberAdapter = new MemberAdapter(this, members, attendanceMap);
        recyclerView.setAdapter(memberAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        institution = sharedPreferences.getString("institution", null);
        groupName = sharedPreferences.getString("cur_group", null);
        adminKey = sharedPreferences.getString("group_admin_key", null);

       role=sharedPreferences.getString("group_admin_role",null);

            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("ProfileInfo").child(institution).child(role).child(adminKey).child("Groups").child(groupName).child("members");

            fetchGroupMembers();


        submitButton.setOnClickListener(v -> submitAttendance());
    }

    private void checkPathExists(String path, RoleCheckCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback.onRoleDetermined("teacher");
                } else {
                    callback.onRoleDetermined("student");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onRoleDetermined("student");
            }
        });
    }

    public interface RoleCheckCallback {
        void onRoleDetermined(String role);
    }

    private void fetchGroupMembers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                members.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Member member = snapshot.getValue(Member.class);

                    if (member != null && !member.getEmail().equals(adminKey)) {
                        members.add(member);
                    }
                }
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Roll.this, "Failed to fetch members", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitAttendance() {
        for (Map.Entry<String, Boolean> entry : attendanceMap.entrySet()) {
            String membername = entry.getKey();
            boolean isPresent = entry.getValue();
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            role=sharedPreferences.getString("group_admin_role",null);
//            databaseReference = FirebaseDatabase.getInstance()
//                    .getReference("ProfileInfo").child(institution).child(role).child(adminKey).child("Groups").child(groupName).child("members").child(membername).child("status");


            String institution = sharedPreferences.getString("institution", null);
            String groupName = sharedPreferences.getString("cur_group", null);
            String adminKey = sharedPreferences.getString("group_admin_key", null);
            String role = sharedPreferences.getString("group_admin_role", null);
            String owner = sharedPreferences.getString("group_admin_key", null);
            String path = "Classrooms_" + groupName + "_" + owner;
            String myName = sharedPreferences.getString("name", null);
            Intent intent = getIntent();
            String key = intent.getStringExtra("idddd");
            databaseReference1 = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(path).child(key).child(myName);
            databaseReference1.setValue("present");
            databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(path).child(key).child(membername);
            databaseReference.setValue(isPresent ? "present" : "absent");


        }
        Toast.makeText(Roll.this, "Attendance Submission Successful", Toast.LENGTH_SHORT).show();
    }
}

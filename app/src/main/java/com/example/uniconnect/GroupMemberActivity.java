package com.example.uniconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
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

public class GroupMemberActivity extends AppCompatActivity {

    private ListView memberListView;
private TextView membername,memberId,memberEmail;
    private ArrayList<Member> members = new ArrayList<>();
    private gmember_adapter memberAdapter;
    private DatabaseReference databaseReference;
    private String institution;
    private String groupName;
    private String adminKey;
    private String myEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);

        memberListView = findViewById(R.id.memberListView);
membername=findViewById(R.id.memberName);



        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        institution = sharedPreferences.getString("institution", null);
        groupName = sharedPreferences.getString("cur_group", null);
        adminKey = sharedPreferences.getString("group_admin_key", null);
        myEmail = sharedPreferences.getString("email", null);



        String sanem = myEmail.replace(".", "_").replace("@", "_");

        String para = "ProfileInfo/" + institution + "/teacher/" + adminKey;
       String role=sharedPreferences.getString("group_admin_role",null);
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("ProfileInfo").child(institution).child(role).child(adminKey).child("Groups").child(groupName).child("members");

            fetchGroupMembers();


        memberAdapter = new gmember_adapter(this, members);
        memberListView.setAdapter(memberAdapter);

        memberListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(GroupMemberActivity.this, ChatActivity.class);
            intent.putExtra("senderEmail", myEmail);
            intent.putExtra("receiverEmail", members.get(position).getEmail());
            intent.putExtra("receiverName", members.get(position).getName());

            if (!sanem.equals(members.get(position).getEmail())) {
                startActivity(intent);
            }
        });
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
                Toast.makeText(GroupMemberActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

                    if (member != null) {
                        members.add(member);
                    }
                }
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupMemberActivity.this, "Failed to fetch members: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

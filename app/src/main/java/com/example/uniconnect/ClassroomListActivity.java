package com.example.uniconnect;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class ClassroomListActivity extends AppCompatActivity {

    private ListView classroomListView;
    private ClassroomAdapter classroomListAdapter;
    private List<Classroom> classroomList;
    private DatabaseReference databaseReference;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_classroom_list);

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

        String path = "Classrooms_" + groupname + "_" + owner;
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(path);

        classroomListView = findViewById(R.id.classroomsListView);
        classroomList = new ArrayList<>();
        classroomListAdapter = new ClassroomAdapter(this, classroomList);
        classroomListView.setAdapter(classroomListAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchClassrooms();
            swipeRefreshLayout.setRefreshing(false);
        });

        fetchClassrooms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchClassrooms();
    }

    private void fetchClassrooms() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                classroomList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String classroomKey = snapshot.getKey();
                    String className = snapshot.child("topic").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);

                    Classroom classroom = new Classroom(className, date, classroomKey);

                    if (classroom != null) {
                        classroomList.add(classroom);
                    } else {
                        Log.d("FirebaseData", "Snapshot data is null or doesn't match the Classroom class.");
                    }
                }
                classroomListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ClassroomListActivity.this, "Failed to load classrooms.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

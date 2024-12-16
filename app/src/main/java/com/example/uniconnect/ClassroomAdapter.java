package com.example.uniconnect;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ClassroomAdapter extends ArrayAdapter<Classroom> {

    private Context context;
    private List<Classroom> classrooms;
    private String state;

    public ClassroomAdapter(Context context, List<Classroom> classrooms) {
        super(context, R.layout.classroom_item, classrooms);
        this.context = context;
        this.classrooms = classrooms;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.classroom_item, parent, false);
        }

        Classroom classroom = classrooms.get(position);

        TextView topicNameTextView = convertView.findViewById(R.id.topicName);
        TextView classDateTextView = convertView.findViewById(R.id.classDate);
        Button attendanceButton = convertView.findViewById(R.id.attendanceButton);
        Button feedbackButton = convertView.findViewById(R.id.feedbackButton);

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String owner = sharedPreferences.getString("group_admin_key", null);
        String emailKey = sharedPreferences.getString("email", null);
        String sanitizedEmail = emailKey.replace(".", "_").replace("@", "_");

        if (classroom != null) {
            topicNameTextView.setText("Class Topic: "+classroom.getClassName());
            classDateTextView.setText("Date: "+classroom.getDate());

            if (!sanitizedEmail.equals(owner)) {
                attendanceButton.setVisibility(GONE);
            }

            attendanceButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, Roll.class);
                intent.putExtra("idddd",classroom.getClassroomKey());
                context.startActivity(intent);
            });

            String institution = sharedPreferences.getString("institution", null);
            String groupName = sharedPreferences.getString("cur_group", null);
            String adminKey = sharedPreferences.getString("group_admin_key", null);
            String roleM = sharedPreferences.getString("group_admin_role", null);
            String myName = sharedPreferences.getString("name", null);
            String path = "Classrooms_" + groupName + "_" + owner;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            databaseReference.child("ProfileInfo")
                    .child(institution)
                    .child(path)
                    .child(classroom.getClassroomKey())
                    .child(myName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DataSnapshot snapshot = task.getResult();
                            state = snapshot.getValue(String.class);
                            String p="present";
                            if (!p.equals(state)) {
                                feedbackButton.setVisibility(GONE);

                            }
                            feedbackButton.setOnClickListener(v -> {

                                    if (!sanitizedEmail.equals(owner)) {
                                        Intent intent = new Intent(context, FeedbackActivity.class);
                                        intent.putExtra("idddd",classroom.getClassroomKey());
                                        context.startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(context, ViewFeedbackActivity.class);
                                        intent.putExtra("idddd",classroom.getClassroomKey());
                                        context.startActivity(intent);
                                    }

                            });

                        } else {
                            Toast.makeText(context, "Failed to load status", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return convertView;
    }
}

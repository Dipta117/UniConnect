package com.example.uniconnect;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class teacherAssignment extends BaseAdapter {
    private String role;
    private Context context;
    private List<Assignment> assignmentList;
    private DatabaseReference databaseReference;

    // Constructor
    public teacherAssignment(Context context, List<Assignment> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;

        // Initialize Firebase Database Reference
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);
        String groupname = sharedPreferences.getString("cur_group", null);
        String owner = sharedPreferences.getString("group_admin_key", null);
        role = sharedPreferences.getString("rool", null);
        String rasta = "Assignment_" + groupname + "_" + owner;
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(rasta);
    }

    @Override
    public int getCount() {
        return assignmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return assignmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_teacher_assignment, parent, false);
        }

        TextView descriptionTextView = convertView.findViewById(R.id.assignmentDescriptionTextView);
        TextView titleTextView = convertView.findViewById(R.id.assignmentTitleTextView);
        TextView deadlineTextView = convertView.findViewById(R.id.assignmentDeadlineTextView);
        TextView marksTextView = convertView.findViewById(R.id.assignmentMarksTextView);
        Button submitButton = convertView.findViewById(R.id.submitButton);
        Button delete=convertView.findViewById(R.id.delete);
        TextView linkTextView = convertView.findViewById(R.id.linkTextView);

        Assignment assignment = assignmentList.get(position);
String key=assignment.getAssignid();
        titleTextView.setText(Html.fromHtml("<b>Topic: </b>" + assignment.getTitle()));
        descriptionTextView.setText(Html.fromHtml("<b>Description: </b>" + assignment.getDescription()));
        deadlineTextView.setText(Html.fromHtml("<b>Deadline: </b>" + assignment.getDeadline()));
        marksTextView.setText(Html.fromHtml("<b>Marks: </b>" + assignment.getMarks()));
        linkTextView.setText(Html.fromHtml("<b>Link: </b>" + assignment.getFileLink()));

        submitButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, SolutionActivity.class);
            intent.putExtra("key",key);
            context.startActivity(intent);
        });

        delete.setOnClickListener(v -> {
            DatabaseReference keyReference = databaseReference.child(key);
            keyReference.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            });
        });


        return convertView;
    }
}

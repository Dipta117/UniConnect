package com.example.uniconnect;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AssignmentFragment extends Fragment {
String assignmentId;
    public AssignmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_assignment, container, false);
        ImageView gifImageView = rootView.findViewById(R.id.gifImageView);
        Glide.with(this) .asGif() .load(R.raw.assign)
                .into(gifImageView);
        // Reference the deadline EditText
        EditText deadlineEditText = rootView.findViewById(R.id.deadlineEditText);
        EditText linkEditText = rootView.findViewById(R.id.linkEditText);
        EditText titleEditText = rootView.findViewById(R.id.titleEditText);
        EditText instructionsEditText = rootView.findViewById(R.id.instructionsEditText);
        EditText marksEditText = rootView.findViewById(R.id.marksEditText);
        Button createAssignmentButton = rootView.findViewById(R.id.createAssignmentButton);
        Button listsButton = rootView.findViewById(R.id.lists);

        // Handle deadline field click to show a DatePickerDialog
        deadlineEditText.setOnClickListener(v -> {
            // Get the current date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Open DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the selected date and set it in the EditText
                        String formattedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        deadlineEditText.setText(formattedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });

        // Handle submission of the assignment
        createAssignmentButton.setOnClickListener(v -> {
            // Get data from input fields
            String title = titleEditText.getText().toString().trim();
            String instructions = instructionsEditText.getText().toString().trim();
            String deadline = deadlineEditText.getText().toString().trim();
            String marks = marksEditText.getText().toString().trim();
            String link = linkEditText.getText().toString().trim();

            // Validate data
            if (title.isEmpty() || instructions.isEmpty() || deadline.isEmpty() || marks.isEmpty() || link.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                saveAssignmentToDatabase(title, instructions, deadline, marks, link);
            }
        });

        listsButton.setOnClickListener(v -> {
            // Open ProfileActivity after clicking "Continue"
            Intent intent = new Intent(getActivity(), AssignmentListActivity_tea.class);
//            intent.putExtra("key",assignmentId);
            startActivity(intent);
        });

        return rootView;
    }

    private void saveAssignmentToDatabase(String title, String instructions, String deadline, String marks, String link) {
        // Get Firebase Database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo");

        // Create a unique ID for each assignment
         assignmentId = databaseReference.push().getKey();

        // Prepare data to save
        Map<String, Object> assignmentData = new HashMap<>();
        assignmentData.put("title", title);
        // assignmentData.put("assignid",assignmentId);
        assignmentData.put("instructions", instructions);
        assignmentData.put("deadline", deadline);
        assignmentData.put("marks", marks);
        assignmentData.put("link", link);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String institution = sharedPreferences.getString("institution", null);
        String groupname = sharedPreferences.getString("cur_group", null);
        String owner = sharedPreferences.getString("group_admin_key", null);
        // Save data under the generated assignment ID
        String rasta = "Assignment_" + groupname + "_" + owner;
        databaseReference.child(institution).child(rasta).child(assignmentId).setValue(assignmentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Assignment Created Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to create assignment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}

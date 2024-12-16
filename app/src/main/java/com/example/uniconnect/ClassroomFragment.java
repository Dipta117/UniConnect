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

public class ClassroomFragment extends Fragment {
    private String classroomId;

    public ClassroomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_classroom, container, false);

        // Reference the EditText fields
        EditText classNameEditText = rootView.findViewById(R.id.titleEditText);
        EditText dateEditText = rootView.findViewById(R.id.dateEditText);

        ImageView gifImageView = rootView.findViewById(R.id.gifImageView);
        Glide.with(this) .asGif() .load(R.raw.cls)
                .into(gifImageView);

        dateEditText.setOnClickListener(v -> {
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
                        dateEditText.setText(formattedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });










        Button createClassroomButton = rootView.findViewById(R.id.createClassButton);
        Button viewClassroomsButton = rootView.findViewById(R.id.lists);

        // Handle submission of the classroom
        createClassroomButton.setOnClickListener(v -> {
            // Get data from input fields
            String className = classNameEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();


            // Validate data
            if (className.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                saveClassroomToDatabase(className, date);
            }
        });


        viewClassroomsButton.setOnClickListener(v -> {
            // Open ClassroomListActivity after clicking "View Classrooms"
            Intent intent = new Intent(getActivity(), ClassroomListActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    private void saveClassroomToDatabase(String className, String date) {
        // Get Firebase Database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo");

        // Create a unique ID for each classroom
        classroomId = databaseReference.push().getKey();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // Prepare data to save
        String myName = sharedPreferences.getString("name", null);
        Map<String, Object> classroomData = new HashMap<>();
        classroomData.put("topic", className);
        classroomData.put("date", date);
        classroomData.put(myName,"present");



        String institution = sharedPreferences.getString("institution", null);
        String groupname = sharedPreferences.getString("cur_group", null);
        String owner = sharedPreferences.getString("group_admin_key", null);
        // Save data under the generated classroom ID
        String path = "Classrooms_" + groupname + "_" + owner;
        databaseReference.child(institution).child(path).child(classroomId).setValue(classroomData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Classroom Created Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to create classroom: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

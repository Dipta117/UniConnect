package com.example.uniconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentListFragment extends Fragment {

    private ListView studentListView;
    private ArrayList<StudentInfo> studentList;
    private StudentListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_student_list, container, false);

        studentListView = rootView.findViewById(R.id.studentListView);
        studentList = new ArrayList<>();
        ImageView gifImageView = rootView.findViewById(R.id.gifImageView);
        Glide.with(this) .asGif() .load(R.raw.stu)
                .into(gifImageView);
        // Fetch data from Firebase
        fetchStudentData();


        // Handle click events on each student item
        studentListView.setOnItemClickListener((parent, view, position, id) -> {
            StudentInfo selectedStudent = studentList.get(position);
            openChatInterface(selectedStudent);
        });

        return rootView;
    }

    private void fetchStudentData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);

        if (institution == null) {
            Toast.makeText(requireContext(), "Institution not found in preferences!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference studentRef = FirebaseDatabase.getInstance()
                .getReference("ProfileInfo")
                .child(institution)
                .child("student");

        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    StudentInfo student = data.getValue(StudentInfo.class);
                    if (student != null) {
                        studentList.add(student);
                    }
                }
                adapter = new StudentListAdapter(requireContext(), studentList);
                studentListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void openChatInterface(StudentInfo student) {
        if (student != null) {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
            String senderEmail = sharedPreferences.getString("email", null);

            if (senderEmail != null) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("senderEmail", senderEmail);  // Sender email should be the logged-in user
                intent.putExtra("receiverEmail", student.getEmail());  // Receiver email should be the student's email

                intent.putExtra("receiverName",student.getname());
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Unable to fetch logged-in user email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Student information is missing!", Toast.LENGTH_SHORT).show();
        }
    }
}

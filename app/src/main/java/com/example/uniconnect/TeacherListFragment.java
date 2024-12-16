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

public class TeacherListFragment extends Fragment {

    private ListView teacherListView;
    private ArrayList<TeacherInfo> teacherList;
    private TeacherListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teacher_list, container, false);

        teacherListView = rootView.findViewById(R.id.teacherListView);
        teacherList = new ArrayList<>();

        ImageView gifImageView = rootView.findViewById(R.id.gifImageView);
        Glide.with(this) .asGif() .load(R.raw.tea)
                .into(gifImageView);

        // Fetch teacher data
        fetchTeacherData();

        // Handle click events on each teacher item
        teacherListView.setOnItemClickListener((parent, view, position, id) -> {
            TeacherInfo selectedTeacher = teacherList.get(position);
            openChatInterface(selectedTeacher);
        });

        return rootView;
    }

    private void fetchTeacherData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);
        String currentUserEmail = sharedPreferences.getString("email", null);

        if (institution == null) {
            Toast.makeText(requireContext(), "Institution not found in preferences!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference teacherRef = FirebaseDatabase.getInstance()
                .getReference("ProfileInfo")
                .child(institution)
                .child("teacher");

        teacherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teacherList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    TeacherInfo teacher = data.getValue(TeacherInfo.class);
                    if (teacher != null) {
                        teacherList.add(teacher);
                    }
                }
                adapter = new TeacherListAdapter(requireContext(), teacherList, currentUserEmail);
                teacherListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void openChatInterface(TeacherInfo teacher) {
        if (teacher != null) {
            // Assuming the logged-in user's email is stored in SharedPreferences
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
            String senderEmail = sharedPreferences.getString("email", null);

            if (senderEmail != null) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("senderEmail", senderEmail);  // Sender email should be the logged-in user
                intent.putExtra("receiverEmail", teacher.getEmail());
               
                intent.putExtra("receiverName",teacher.getname());
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Unable to fetch logged-in user email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Teacher information is missing!", Toast.LENGTH_SHORT).show();
        }
    }
}

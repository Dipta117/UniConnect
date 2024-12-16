package com.example.uniconnect;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment {

    private RecyclerView groupRecyclerView;
    private FloatingActionButton addGroupFab;
    private GroupAdapter groupAdapter;
    private List<Group> groupList = new ArrayList<>();
    private DatabaseReference databaseReference,db;
    private ProgressBar loadingSpinner;
    Button createGroupConfirmButton;
    private String username ,groupName;
    // Replace with dynamic username if available.
    private ArrayList<String> selectedMembers = new ArrayList<>();
    private ArrayList<String> selected_email = new ArrayList<>();
    private ArrayList<String>selected_id=new ArrayList<>();
    private ArrayList<String>selected_con=new ArrayList<>();
    private ArrayList<String>selected_dep=new ArrayList<>();
   public String institution,emailKey,nameowner,admin_name,rolee;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);


        ImageView gifImageView = view.findViewById(R.id.gifImageView);
        Glide.with(this) .asGif() .load(R.raw.gl)
                .into(gifImageView);

    // Replace with your GIF file .into(gifImageView);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        groupRecyclerView = view.findViewById(R.id.groupRecyclerView);
        addGroupFab = view.findViewById(R.id.addGroupFab);



            // Loop the video mp.start(); // Start playing the video });
        groupRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        groupList = new ArrayList<>();
        // Initialize Firebase reference
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
         institution = sharedPreferences.getString("institution", null);
        String email= sharedPreferences.getString("email",null);
        nameowner=sharedPreferences.getString("name",null);

         emailKey = email.replace(".", "_").replace("@", "_");
        String para = "ProfileInfo/" + institution + "/teacher/" + emailKey;

        checkPathExists(para, role -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("rool", role);
            rolee=role;
editor.apply();
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("ProfileInfo").child(institution).child(role).child(emailKey).child("Groups");

            // Fetch and display existing groups after determining role




        // Set up adapter
        groupAdapter = new GroupAdapter(requireContext(),groupList);
        groupRecyclerView.setAdapter(groupAdapter);

        // Fetch and display existing groups
        fetchGroups();
        });
        // Handle FAB click to show the create group dialog
        addGroupFab.setOnClickListener(v -> showCreateGroupDialog());
        groupAdapter = new GroupAdapter(requireContext(), groupList);
        groupRecyclerView.setAdapter(groupAdapter);
        // Navigate to GroupActivity on group click
//        groupRecyclerView.setOnItemClickListener((parent, view1, position, id) -> {
//            String groupName = groupList.get(position);
//            Intent intent = new Intent(requireContext(), GroupActivity.class);
//
//            intent.putExtra("groupName", groupName);
//            startActivity(intent);
//            Toast.makeText(requireContext(),"Welcome to "+groupName, Toast.LENGTH_SHORT).show();
//        });




        return view;
    }

    private void checkPathExists(String path, RoleCheckCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Path exists for a teacher
                    callback.onRoleDetermined("teacher");
                } else {
                    // Default to student if path does not exist
                    callback.onRoleDetermined("student");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onRoleDetermined("student"); // Default to student on error
            }
        });
    }
    public interface RoleCheckCallback {
        void onRoleDetermined(String role);
    }



    private void fetchGroups() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String groupName = snapshot.getKey();
                    String owner = snapshot.child("owner").getValue(String.class);
                    String formattedOwner = Html.fromHtml("<b><font color='#00FF00'>Admin:</b> " + owner).toString();
                    groupList.add(new Group(groupName,formattedOwner));
                }
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to fetch groups", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreateGroupDialog() {
        Dialog createGroupDialog = new Dialog(requireContext());
        createGroupDialog.setContentView(R.layout.activity_group_dialog_box);

        EditText groupNameEditText = createGroupDialog.findViewById(R.id.groupNameEditText);
         createGroupConfirmButton = createGroupDialog.findViewById(R.id.createGroupConfirmButton);
         createGroupConfirmButton.setVisibility(View.GONE);
        Button addMembersButton = createGroupDialog.findViewById(R.id.addMembersButton); // Add button for mem

        addMembersButton.setOnClickListener(v -> showMemberSelectionDialog());

        createGroupConfirmButton.setOnClickListener(v -> {

            loadingSpinner.setVisibility(View.VISIBLE);
            new android.os.Handler().postDelayed(
                    new Runnable()
                    { public void run()
                    {
                        loadingSpinner.setVisibility(View.GONE);
                        // Do something after the operation
                        Toast.makeText(requireContext(), "Group Created Successfully", Toast.LENGTH_SHORT).show(); } }, 5000
            );
             groupName = groupNameEditText.getText().toString().trim();

            if (groupName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a group name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a unique group key
            String groupKey = groupName;
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

            nameowner=sharedPreferences.getString("name",null);
            // Store group in Firebase
            databaseReference.child(groupKey).child("owner").setValue(nameowner);
            databaseReference.child(groupKey).child("admin").setValue(emailKey);
            databaseReference.child(groupKey).child("group_name").setValue(groupName);



            DatabaseReference membersRef = databaseReference.child(groupKey).child("members");
            membersRef.child(nameowner).child("name").setValue(nameowner + " (admin) ");
            membersRef.child(nameowner).child("email").setValue(emailKey);
            membersRef.child(nameowner).child("status").setValue("present");
            for (int i = 0; i < selectedMembers.size(); i++) {
                String member = selectedMembers.get(i);
                String id = selected_id.get(i);
                String eid=selected_email.get(i);// Fetch corresponding ID from the second ArrayList
                String con=selected_con.get(i);
                String dep=selected_dep.get(i);
                membersRef.child(member).child("id").setValue(id);
                membersRef.child(member).child("name").setValue(member);
                membersRef.child(member).child("email").setValue(eid);
                membersRef.child(member).child("contact").setValue(con);
                membersRef.child(member).child("department").setValue(dep);
            }



            FirebaseDatabase.getInstance()
                    .getReference("ProfileInfo")
                    .child(institution)
                    .child(rolee)
                    .child(emailKey)
                    .child("Groups")
                    .child(groupName)
                    .child("admin_role")
                    .setValue(rolee);

            for(String em:selected_email)
            {

                String parat = "ProfileInfo/" + institution + "/teacher/" + em;

                checkPathExists(parat, role -> {


                    FirebaseDatabase.getInstance()
                            .getReference("ProfileInfo")
                            .child(institution)
                            .child(role)
                            .child(em)
                            .child("Groups")
                            .child(groupName)
                            .child("admin")
                            .setValue(emailKey)
                            ;
                    FirebaseDatabase.getInstance()
                            .getReference("ProfileInfo")
                            .child(institution)
                            .child(role)
                            .child(em)
                            .child("Groups")
                            .child(groupName)
                            .child("group_name")
                            .setValue(groupName)
                    ;


                    FirebaseDatabase.getInstance()
                            .getReference("ProfileInfo")
                            .child(institution)
                            .child(role)
                            .child(em)
                            .child("Groups")
                            .child(groupName)
                            .child("admin_role")
                            .setValue(rolee);
                    ;
                    FirebaseDatabase.getInstance()
                            .getReference("ProfileInfo").child(institution).child(role).child(em).child("Groups").child(groupName).child("owner").setValue(nameowner);

                });


            }
            createGroupDialog.dismiss();



        });

        createGroupDialog.show();
    }

    private void showMemberSelectionDialog() {
        Dialog memberSelectionDialog = new Dialog(requireContext());
        memberSelectionDialog.setContentView(R.layout.activity_member_selection); // Create this layout

        GridView membersGridView = memberSelectionDialog.findViewById(R.id.membersGridView);
        Button confirmSelectionButton = memberSelectionDialog.findViewById(R.id.confirmSelectionButton);

        ArrayList<String> memberList = new ArrayList<>();
        ArrayList<String> member_email = new ArrayList<>();
        ArrayList<String>member_id=new ArrayList<>();
        ArrayList<String>member_con=new ArrayList<>();
        ArrayList<String>member_dep=new ArrayList<>();
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, memberList);

        membersGridView.setAdapter(memberAdapter);
        membersGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        // Fetch students and teachers from Firebase
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);

        DatabaseReference membersRef = FirebaseDatabase.getInstance()
                .getReference("ProfileInfo")
                .child(institution);

        membersRef.child("student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot student : snapshot.getChildren()) {
                    String email_stud=student.child("email").getValue(String.class);
                    String studentName1 = student.child("firstname").getValue(String.class);
                    String studentName2 = student.child("lastname").getValue(String.class);
                    String studentName = studentName1+" "+studentName2;  // Fetch 'name' attribute
                    String roll_stu=student.child("roll").getValue(String.class);
                    String cont=student.child("contactNo").getValue(String.class);
                    String depp=student.child("sdept").getValue(String.class);
                    if (studentName != null && !studentName.equals(nameowner)) {
                        memberList.add(studentName);
                        String semail_stud= email_stud.replace(".", "_").replace("@", "_");
                        member_email.add(semail_stud);
                        member_id.add(roll_stu);
                        member_con.add(cont);
                        member_dep.add(depp);

                    }
                }
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load students", Toast.LENGTH_SHORT).show();
            }
        });

        membersRef.child("teacher").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot teacher : snapshot.getChildren()) {
                    String email_teach=teacher.child("email").getValue(String.class);
                    String teacherName1 = teacher.child("firstname").getValue(String.class);
                    String teacherName2 = teacher.child("lastname").getValue(String.class);
                    String teacherName = teacherName1+" "+teacherName2; // Fetch 'name' attribute
                    String id_t=teacher.child("id").getValue(String.class);
                    String cont=teacher.child("contactNo").getValue(String.class);
                    String depp=teacher.child("tdept").getValue(String.class);
                    if (teacherName != null && !teacherName.equals(nameowner)) {
                        memberList.add(teacherName);
                        String semail_teach= email_teach.replace(".", "_").replace("@", "_");
                        member_email.add(semail_teach);
                        member_id.add(id_t);
                        member_con.add(cont);
                        member_dep.add(depp);
                    }
                }
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load teachers", Toast.LENGTH_SHORT).show();
            }
        });


        // Confirm selection
        confirmSelectionButton.setOnClickListener(v -> {
            selectedMembers.clear();
            for (int i = 0; i < membersGridView.getCount(); i++) {
                if (membersGridView.isItemChecked(i)) {
                    selectedMembers.add(memberList.get(i));
                    selected_email.add(member_email.get(i));
                    selected_id.add(member_id.get(i));
                    selected_con.add(member_con.get(i));
                    selected_dep.add(member_dep.get(i));

                }
            }
            loadingSpinner.setVisibility(View.VISIBLE);
            new android.os.Handler().postDelayed(
                    new Runnable()
                    { public void run()
                    {
                        loadingSpinner.setVisibility(View.GONE);
                        // Do something after the operation
                        Toast.makeText(requireContext(), "Selected Members: " + selectedMembers.size(), Toast.LENGTH_SHORT).show();
                        createGroupConfirmButton.setVisibility(View.VISIBLE);} }, 2000

            );

            memberSelectionDialog.dismiss();


            ;


        });

        memberSelectionDialog.show();
    }

}

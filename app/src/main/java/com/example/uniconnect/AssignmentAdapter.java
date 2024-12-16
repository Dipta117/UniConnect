package com.example.uniconnect;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AssignmentAdapter extends BaseAdapter {
    private String fin, gin;
    private String role;
    private Context context;
    private List<Assignment> assignmentList;
    private DatabaseReference databaseReference, databaseReference1, databaseReference2;

    // Constructor
    public AssignmentAdapter(Context context, List<Assignment> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;

        // Initialize Firebase Database References
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);
        String groupname = sharedPreferences.getString("cur_group", null);
        String owner = sharedPreferences.getString("group_admin_key", null);
        String email = sharedPreferences.getString("email", null);
        String emailKey = email.replace(".", "_").replace("@", "_");
        role = sharedPreferences.getString("rool", null);
        String rasta = "Assignment_" + groupname + "_" + owner;
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(rasta);

        databaseReference2 = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child(role).child(emailKey);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.assignment_item, parent, false);
        }

        TextView descriptionTextView = convertView.findViewById(R.id.assignmentDescriptionTextView);
        TextView titleTextView = convertView.findViewById(R.id.assignmentTitleTextView);
        TextView deadlineTextView = convertView.findViewById(R.id.assignmentDeadlineTextView);
        TextView marksTextView = convertView.findViewById(R.id.assignmentMarksTextView);
        TextView obtainedMarksTextView = convertView.findViewById(R.id.obtainedMarksTextView);
        Button submitButton = convertView.findViewById(R.id.submitButton);
        TextView linkTextView = convertView.findViewById(R.id.linkTextView);
        EditText ans = convertView.findViewById(R.id.ans);

        Assignment assignment = assignmentList.get(position);

        titleTextView.setText(Html.fromHtml("<b>Topic: </b>" + assignment.getTitle()));
        descriptionTextView.setText(Html.fromHtml("<b>Description: </b>" + assignment.getDescription()));
        deadlineTextView.setText(Html.fromHtml("<b>Deadline: </b>" + assignment.getDeadline()));
        marksTextView.setText(Html.fromHtml("<b>Marks: </b>" + assignment.getMarks()));
        linkTextView.setText(Html.fromHtml("<b>Link: </b>" + assignment.getFileLink()));
        String iddd = assignment.getAssignid();

        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    fin = dataSnapshot.child("roll").getValue(String.class);
                    gin = dataSnapshot.child("id").getValue(String.class);

                    String st;
                    if (fin == null) {
                        st = gin;
                    } else {
                        st = fin;
                    }

                    if (st != null) {
                        fetchObtainedMarks(iddd,st, obtainedMarksTextView);
                    } else {

                    }

                    databaseReference.child(iddd).child("submissions").child(st).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if (snapshot.exists())
                            {String iss=snapshot.child("issubmitted").getValue(String.class);
                            if(iss.equals("submitted"))
                            {
                                submitButton.setVisibility(View.GONE);
                                ans.setVisibility(View.GONE);
                            }
                        }}

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                    submitButton.setOnClickListener(v -> {
                        String vin = st;
                        String answer = ans.getText().toString().trim();

                        if (!answer.isEmpty()) {
                            databaseReference.child(iddd).child("submissions").child(vin).child("link").setValue(answer)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {

                                            submitButton.setText("Submitted");
                                            ans.setVisibility(View.GONE);
                                            databaseReference.child(iddd).child("submissions").child(vin).child("issubmitted").setValue("submitted");
                                            Toast.makeText(context, "Assignment submitted successfully", Toast.LENGTH_SHORT).show();
                                            obtainedMarksTextView.setText("Obtained Marks: N/A");

                                        } else {
                                            Toast.makeText(context, "Submission failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(context, "Answer cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(context, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    private void fetchObtainedMarks(String iddd,String userId, TextView obtainedMarksTextView) {


        databaseReference.child(iddd).child("submissions").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String obtainedMarks = dataSnapshot.child("marks").getValue(String.class);
                    if(obtainedMarks!=null)
                    {obtainedMarksTextView.setText("Obtained Marks: " + obtainedMarks);}
                    else
                    {
                        obtainedMarksTextView.setText("Obtained Marks:N/A");
                    }
                } else {
                    obtainedMarksTextView.setText("Obtained Marks:N/A");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Failed to fetch obtained marks", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.uniconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class SolutionAdapter extends BaseAdapter {
    private Context context;
    private List<Submission> submissionList;
    private DatabaseReference databaseReference;

    public SolutionAdapter(Context context, List<Submission> submissionList, DatabaseReference databaseReference) {
        this.context = context;
        this.submissionList = submissionList;
        this.databaseReference = databaseReference;
    }

    @Override
    public int getCount() {
        return submissionList.size();
    }

    @Override
    public Object getItem(int position) {
        return submissionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.submission_item, parent, false);
        }

        TextView linkTextView = convertView.findViewById(R.id.linkTextView);
        TextView submissionTextView = convertView.findViewById(R.id.submissionTextView);
        EditText marksEditText = convertView.findViewById(R.id.marksEditText);
        Button giveMarksButton = convertView.findViewById(R.id.giveMarksButton);
        TextView obtainedMarksTextView = convertView.findViewById(R.id.obtainedMarksTextView); // New TextView

        Submission submission = submissionList.get(position);
        submissionTextView.setText("Roll: " + submission.getId());
        linkTextView.setText("Solution Link: " + submission.getContent());
        String gi = submission.getId();
        if (submission.getMarks() != null && !submission.getMarks().isEmpty())
        { marksEditText.setVisibility(View.GONE);
            giveMarksButton.setVisibility(View.GONE);
            obtainedMarksTextView.setVisibility(View.VISIBLE);
            obtainedMarksTextView.setText("Marks Obtained: " + submission.getMarks()); }
        else
        { marksEditText.setVisibility(View.VISIBLE);
            giveMarksButton.setVisibility(View.VISIBLE);
            obtainedMarksTextView.setVisibility(View.GONE); }


        giveMarksButton.setOnClickListener(v -> {
            String marks = marksEditText.getText().toString().trim();
            if (!marks.isEmpty()) {
                databaseReference.child(gi).child("marks").setValue(marks)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Marks given successfully", Toast.LENGTH_SHORT).show();
                                marksEditText.setVisibility(View.GONE);
                                giveMarksButton.setVisibility(View.GONE);
                                obtainedMarksTextView.setVisibility(View.VISIBLE);
                                obtainedMarksTextView.setText("Marks Obtained: " + marks);
                                submission.setMarks(marks);

                            } else {
                                Toast.makeText(context, "Failed to give marks", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(context, "Marks cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Hide the obtainedMarksTextView initially
        obtainedMarksTextView.setVisibility(View.GONE);

        return convertView;
    }
}

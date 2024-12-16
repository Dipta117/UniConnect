package com.example.uniconnect;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class StudentListAdapter extends android.widget.ArrayAdapter<StudentInfo> {
    private Context context;
    private List<StudentInfo> students;

    public StudentListAdapter(Context context, List<StudentInfo> students) {
        super(context, R.layout.student_list_item, students);
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_list_item, parent, false);
        }

        StudentInfo student = students.get(position);

        TextView nameTextView = convertView.findViewById(R.id.studentname);
        TextView emailTextView = convertView.findViewById(R.id.studentemail);
        TextView rollNumberTextView = convertView.findViewById(R.id.studentrollNumber);
TextView contact=convertView.findViewById(R.id.contactView);
        TextView dept=convertView.findViewById(R.id.dept);
        nameTextView.setText(student.getFirstname() + " " + student.getLastname());
        emailTextView.setText(Html.fromHtml("<b>Email: </b>"+student.getEmail()));
        rollNumberTextView.setText(Html.fromHtml("<b>Roll: </b>"+student.getRoll()));
        dept.setText(Html.fromHtml("<b>Department: </b>"+student.getSdept()));
contact.setText(Html.fromHtml("<b>Contact No: </b>"+student.getContactNo()));
        return convertView;
    }
}

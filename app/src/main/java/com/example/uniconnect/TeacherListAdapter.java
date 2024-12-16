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

public class TeacherListAdapter extends android.widget.ArrayAdapter<TeacherInfo> {
    private Context context;
    private List<TeacherInfo> teachers;
    private String currentUserEmail;

    public TeacherListAdapter(Context context, List<TeacherInfo> teachers, String currentUserEmail) {
        super(context, R.layout.teacher_list_item, teachers);
        this.context = context;
        this.teachers = teachers;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.teacher_list_item, parent, false);
        }

        TeacherInfo teacher = teachers.get(position);

        TextView nameTextView = convertView.findViewById(R.id.teacherName);
        TextView emailTextView = convertView.findViewById(R.id.teacherEmail);
        TextView contactTextView = convertView.findViewById(R.id.teacherContact);
        TextView dept=convertView.findViewById(R.id.dept);
TextView id=convertView.findViewById(R.id.teacherid);
        // Set teacher data
        nameTextView.setText(teacher.getFirstname() + " " + teacher.getLastname());
        emailTextView.setText(Html.fromHtml("<b>Email: </b>"+teacher.getEmail()));
        contactTextView.setText(Html.fromHtml("<b>Contact No: </b>"+teacher.getContactNo()));
        dept.setText(Html.fromHtml("<b>Department: </b>"+teacher.getTdept()));
id.setText(Html.fromHtml("<b>ID: </b>"+teacher.getId()));
        // Apply bold style if the current user is the receiver of the message


        return convertView;
    }
}

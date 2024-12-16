package com.example.uniconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Sign_up extends AppCompatActivity {

    // Declare UI components

    private RadioGroup userTypeRadioGroup;
    private LinearLayout studentFields, teacherFields;
    private EditText firstNameEditText, lastNameEditText;
    private EditText emailEditText, passwordEditText, contactNoEditText,institutionEditText;
    private EditText rollEditText, studentDepartmentEditText;
    private EditText teacherDepartmentEditText, idEditText;
    private Button signUpButton;
private TextView textView;
    // Firebase variables
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,databaseReference1;

    // Model class for profile info
    ProfileInfo profileinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();

        profileinfo = new ProfileInfo();

        // Initialize UI components
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        studentFields = findViewById(R.id.studentFields);
        teacherFields = findViewById(R.id.teacherFields);

        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        contactNoEditText = findViewById(R.id.contact);
        rollEditText = findViewById(R.id.rollEditText);
        institutionEditText=findViewById(R.id.institution);
        studentDepartmentEditText = findViewById(R.id.studentDepartmentEditText);
        teacherDepartmentEditText = findViewById(R.id.teacherDepartmentEditText);
        idEditText = findViewById(R.id.idEditText);
        signUpButton = findViewById(R.id.signUpButton);

        // Listener for RadioGroup
        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioStudent) {
                showStudentFields();
            } else if (checkedId == R.id.radioTeacher) {
                showTeacherFields();
            }
        });

        // Listener for sign-up button
        signUpButton.setOnClickListener(view -> signUpUser());
    }

    private void showStudentFields() {
        studentFields.setVisibility(View.VISIBLE);
        teacherFields.setVisibility(View.GONE);
    }

    private void showTeacherFields() {
        studentFields.setVisibility(View.GONE);
        teacherFields.setVisibility(View.VISIBLE);
    }

    private void signUpUser() {

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String contact = contactNoEditText.getText().toString().trim();
        String roll = rollEditText.getText().toString().trim();
        String institution=institutionEditText.getText().toString().trim();
        String studentDept = studentDepartmentEditText.getText().toString().trim();
        String teacherDept = teacherDepartmentEditText.getText().toString().trim();
        String teacherId = idEditText.getText().toString().trim();

        databaseReference = firebaseDatabase.getReference("ProfileInfo").child(institution);
        databaseReference1 = firebaseDatabase.getReference("ProfileInfo").child(institution);
        String sanitizedEmail = email.replace(".", "_").replace("@", "_");

        if (firstName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String emailKey = email.replace(".", "_").replace("@", "_");

        // Determine user type
        int selectedUserType = userTypeRadioGroup.getCheckedRadioButtonId();

        if (selectedUserType == R.id.radioStudent) {
            // Validate student-specific fields
            if (roll.isEmpty() || studentDept.isEmpty()) {
                Toast.makeText(this, "Please fill all student-specific fields", Toast.LENGTH_SHORT).show();
                return;
            }


            // Set student info
            profileinfo.setFirstname(firstName);
            profileinfo.setLastname(lastName);
            profileinfo.setEmail(email);
            profileinfo.setPassword(password);
            profileinfo.setContactNo(contact);
            profileinfo.setRoll(roll);
            profileinfo.setInstitution(institution);
            profileinfo.setSdept(studentDept);
            profileinfo.setBio("Set Bio");
            saveLoginInfo(email,institution);
            databaseReference = firebaseDatabase.getReference("ProfileInfo").child(institution);
            databaseReference1 = firebaseDatabase.getReference("ProfileInfo").child(institution);
            // Push to StudentInfo path in Firebase
            databaseReference.child("student").child(emailKey).setValue(profileinfo).addOnCompleteListener(task -> {


                if (task.isSuccessful()) {
                    saveLoginInfo(email,institution);
                    Toast.makeText(Sign_up.this, "Student Sign Up Successful", Toast.LENGTH_SHORT).show();
                    signUpButton.setText("Continue");
                    signUpButton.setOnClickListener(v -> {
                        // Open ProfileActivity after clicking "Continue"
                        Intent intent = new Intent(Sign_up.this, ProfileActivity.class);
                        intent.putExtra("email_key", sanitizedEmail);
                        intent.putExtra("institution",institution);
                        startActivity(intent);
                    });
                } else {
                    Toast.makeText(Sign_up.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else if (selectedUserType == R.id.radioTeacher) {
            // Validate teacher-specific fields
            if (teacherId.isEmpty() || teacherDept.isEmpty()) {
                Toast.makeText(this, "Please fill all teacher-specific fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set teacher info
            profileinfo.setFirstname(firstName);
            profileinfo.setLastname(lastName);
            profileinfo.setEmail(email);
            profileinfo.setPassword(password);
            profileinfo.setContactNo(contact);
            profileinfo.setInstitution(institution);
            profileinfo.setId(teacherId);
            profileinfo.setTdept(teacherDept);
            profileinfo.setBio("Set Bio");
            saveLoginInfo(email,institution);
            // Push to TeacherInfo path in Firebase
            databaseReference1.child("teacher").child(emailKey).setValue(profileinfo).addOnCompleteListener(task -> {

                emailEditText = findViewById(R.id.email);


                if (task.isSuccessful()) {

                    Toast.makeText(Sign_up.this, "Teacher Sign Up Successful", Toast.LENGTH_SHORT).show();
                    signUpButton.setText("Continue");
                    saveLoginInfo(email,institution);

                    signUpButton.setOnClickListener(v -> {
                        Intent intent = new Intent(Sign_up.this, ProfileActivity.class);
                        intent.putExtra("email_key", sanitizedEmail);
                        intent.putExtra("institution",institution);
                        startActivity(intent);
                    });
                } else {
                    Toast.makeText(Sign_up.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveLoginInfo(String email,String institution) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("email", email);
        editor.putString("institution",institution);

        editor.apply();
    }




}

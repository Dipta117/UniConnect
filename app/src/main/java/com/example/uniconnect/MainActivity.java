package com.example.uniconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.*;
import java.util.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText,institutionEditText;
    private Button loginButton, signUpButton,register;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, databaseReference1;
    private AutoCompleteTextView institutionAutoComplete;
    private List<String> institutionList = new ArrayList<>();
    private ArrayAdapter<String> institutionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();


        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        institutionEditText=findViewById(R.id.institution);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        register=findViewById(R.id.register);
        institutionAutoComplete = findViewById(R.id.institution);
        institutionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, institutionList);
        institutionAutoComplete.setAdapter(institutionAdapter);

        // Fetch institutions from Firebase
        fetchInstitutions();

        checkLoggedInUser();

        // Check if the user is already logged in


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String institution=institutionEditText.getText().toString().trim();
                databaseReference = firebaseDatabase.getReference("ProfileInfo").child(institution).child("student");
                databaseReference1 = firebaseDatabase.getReference("ProfileInfo").child(institution).child("teacher");
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    authenticateUser(email, password,institution);
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Sign_up.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });



    }

    private void authenticateUser(String email, String password,String institution) {
        String sanitizedEmail = email.replace(".", "_").replace("@", "_");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean authenticated = false;
                String fullName="";
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String fname=userSnapshot.child("firstname").getValue(String.class);
                    String lname=userSnapshot.child("lastname").getValue(String.class);
                    fullName=fname+" "+lname;
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);

                    if (email.equals(dbEmail) && password.equals(dbPassword)) {
                        authenticated = true;
                        break;
                    }

                }

                if (authenticated) {
                    // Save login info to SharedPreferences
                    saveLoginInfo(fullName,email,institution);
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("email_key", sanitizedEmail);
                    intent.putExtra("institution",institution);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String fullName="";
                boolean authenticated = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String fname=userSnapshot.child("firstname").getValue(String.class);
                    String lname=userSnapshot.child("lastname").getValue(String.class);
                    fullName=fname+" "+lname;
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);

                    if (email.equals(dbEmail) && password.equals(dbPassword)) {
                        authenticated = true;
                        break;
                    }
                }

                if (authenticated) {
                    // Save login info to SharedPreferences
                    saveLoginInfo(fullName,email,institution);
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("email_key", sanitizedEmail);
                    intent.putExtra("institution",institution);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginInfo(String fullName,String email,String institution) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("name",fullName);
        editor.putString("email", email);
        editor.putString("institution",institution);

        editor.apply();
    }

    private void checkLoggedInUser() {

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String email = sharedPreferences.getString("email", null);
            String institution=sharedPreferences.getString("institution",null);
            if (email != null && institution!=null) {
                String sanitizedEmail = email.replace(".", "_").replace("@", "_");

                //Toast.makeText(this, "Already logged in as: " + email, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("email_key",sanitizedEmail);
                intent.putExtra("institution",institution);
                startActivity(intent);
                finish();
            }
        }
    }

    private void fetchInstitutions() {
        DatabaseReference institutionsRef = firebaseDatabase.getReference("ProfileInfo");
        institutionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                institutionList.clear();
                for (DataSnapshot institutionSnapshot : snapshot.getChildren()) {
                    String institutionName = institutionSnapshot.getKey();
                    if (institutionName != null) {
                        institutionList.add(institutionName);
                    }
                }
                // Notify adapter of data changes
                institutionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error fetching institutions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

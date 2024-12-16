package com.example.uniconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.*;
public class Register extends AppCompatActivity {

    private EditText etInstitutionName, etEIIN, etAddress;
    private Button RegisterButton;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etInstitutionName = findViewById(R.id.etInstitutionName);
        etEIIN = findViewById(R.id.etEIIN);
        etAddress = findViewById(R.id.etAddress);

        RegisterButton = findViewById(R.id.RegisterButton);


        // Initialize Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Set up button listeners
        RegisterButton.setOnClickListener(v -> registerInstitution());

    }

    private void registerInstitution() {
        // Get input values
        String institution = etInstitutionName.getText().toString().trim();
        String eiin = etEIIN.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (institution.isEmpty() || eiin.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a HashMap to store institution details
        HashMap<String, String> institutionInfo = new HashMap<>();
        institutionInfo.put("EIIN", eiin);
        institutionInfo.put("Address", address);

        // Save institution data to Firebase
        databaseRef = firebaseDatabase.getReference("ProfileInfo").child(institution).child("Institution Info");
        databaseRef.setValue(institutionInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Register.this, "Institution Registered Successfully", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(Register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


}

    private void clearFields() {
        etInstitutionName.setText("");
        etEIIN.setText("");
        etAddress.setText("");
    }
}





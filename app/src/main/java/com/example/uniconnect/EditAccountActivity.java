package com.example.uniconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditAccountActivity extends AppCompatActivity {

    private EditText editContact, editPassword, editRoll, oldpass;
    private Button saveContactButton, savePasswordButton, saveRollButton,deleteAccountButton;
    private LinearLayout contactLayout, passwordLayout, rollLayout;
    private DatabaseReference userReference;
    private String email, institution, role, prepass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        // Initializing Views
        editContact = findViewById(R.id.editContact);
        editPassword = findViewById(R.id.editPassword);
        editRoll = findViewById(R.id.editRoll);
        saveContactButton = findViewById(R.id.saveContactButton);
        savePasswordButton = findViewById(R.id.savePasswordButton);
        saveRollButton = findViewById(R.id.saveRollButton);
        contactLayout = findViewById(R.id.contactLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        rollLayout = findViewById(R.id.rollLayout);
        oldpass = findViewById(R.id.old);
        deleteAccountButton=findViewById(R.id.deleteAccount);
        deleteAccountButton.setOnClickListener(v -> deleteAccount());

        // Initializing Buttons
        Button changeContactButton = findViewById(R.id.changeContactButton);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        Button changeRollButton = findViewById(R.id.changeRollButton);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);
        institution = sharedPreferences.getString("institution", null);
        role = sharedPreferences.getString("role", null);

        if (email == null || institution == null) {
            Toast.makeText(this, "Error: Missing account details.", Toast.LENGTH_SHORT).show();
            return;
        }

        userReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution);

        // Setting Button Click Listeners
        changeContactButton.setOnClickListener(v -> toggleVisibility(contactLayout));
        changePasswordButton.setOnClickListener(v -> toggleVisibility(passwordLayout));
        changeRollButton.setOnClickListener(v -> toggleVisibility(rollLayout));

        saveContactButton.setOnClickListener(v -> saveContact());
        savePasswordButton.setOnClickListener(v -> savePassword());
        saveRollButton.setOnClickListener(v -> saveRoll());
    }

    private void toggleVisibility(View view) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void saveContact() {
        String sanitizedEmail = email.replace(".", "_").replace("@", "_");
        String contact = editContact.getText().toString().trim();
        if (!contact.isEmpty()) {
            userReference.child(role).child(sanitizedEmail).child("contactNo").setValue(contact)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Contact updated successfully!", Toast.LENGTH_SHORT).show();
                            contactLayout.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(this, "Failed to update contact.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all saved preferences
        editor.apply();

        // Navigate back to MainActivity
        Intent intent = new Intent(EditAccountActivity.this, MainActivity.class);
        startActivity(intent);


    }

    private void deleteAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String institution = sharedPreferences.getString("institution", null);

        if (email == null || institution == null) {
            Toast.makeText(EditAccountActivity.this, "Error: Missing account details.", Toast.LENGTH_SHORT).show();
            return;
        }

        String sanitizedEmail = email.replace(".", "_").replace("@", "_");


        // Check if the user is a student or teacher
        userReference.child("student").child(sanitizedEmail).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditAccountActivity.this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();
                logoutUser(); // Redirect to login after deletion
            } else {

            }

//            Intent intent = new Intent(requireActivity(), MainActivity.class);
//            startActivity(intent);

        });

        userReference.child("teacher").child(sanitizedEmail).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditAccountActivity.this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();
                logoutUser();
            } else {

            }
        });


    }
    private void savePassword() {
        String sanitizedEmail = email.replace(".", "_").replace("@", "_");
        String newPassword = editPassword.getText().toString().trim();
        String currentPassword = oldpass.getText().toString().trim();

        if (!currentPassword.isEmpty()) {
            userReference.child(role).child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child("password").getValue(String.class).equals(currentPassword)) {
                        if (!newPassword.isEmpty()) {
                            userReference.child(role).child(sanitizedEmail).child("password").setValue(newPassword)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditAccountActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                            passwordLayout.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(EditAccountActivity.this, "Failed to update password.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(EditAccountActivity.this, "New password cannot be empty.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditAccountActivity.this, "Current password is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditAccountActivity.this, "Failed to retrieve current password.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please enter your current password.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveRoll() {
        String sanitizedEmail = email.replace(".", "_").replace("@", "_");
        String roll = editRoll.getText().toString().trim();
        if (!roll.isEmpty()) {
            if (role.equals("student")) {
                userReference.child(role).child(sanitizedEmail).child("roll").setValue(roll);
            } else {
                userReference.child(role).child(sanitizedEmail).child("id").setValue(roll);
            }
            userReference.child(role).child(sanitizedEmail).child("roll").setValue(roll)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Roll/ID updated successfully!", Toast.LENGTH_SHORT).show();
                            rollLayout.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(this, "Failed to update roll/ID.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

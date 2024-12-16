package com.example.uniconnect;

import android.app.Dialog;
import android.content.*;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.*;
import android.widget.*;
import android.widget.Toast;
import androidx.swiperefreshlayout.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.*;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, emailTextView, departmentTextView, contactTextView, institutionTextView,rollView,idview;
    private FirebaseDatabase firebaseDatabase;
    private TextView bioTextView;
    private EditText editBioEditText;
    private Button setBioButton;
    private DatabaseReference studentReference, teacherReference,dbr;
    String role;
    private String query = "";
private Button logout;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button deleteAccountButton, editAccountButton,news;
    private static final String TAG = "ProfileFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Fetch shared preferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String institution = sharedPreferences.getString("institution", null);

        if (email == null || institution == null) {

            Toast.makeText(requireContext(), "Error: Missing login details!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Fetching data for email: " + email + " and institution: " + institution);

        // Fetch data from Firebase
        fetchData(email, institution);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        bioTextView = rootView.findViewById(R.id.bio);
        bioTextView.setText("Set Bio");
        // Initialize TextView references
        nameTextView = rootView.findViewById(R.id.username);
        editBioEditText = rootView.findViewById(R.id.editBio);
        setBioButton = rootView.findViewById(R.id.setBioButton);
        editBioEditText.setVisibility(View.GONE);
        setBioButton.setVisibility(View.GONE);
        ImageView menuImageView = rootView.findViewById(R.id.menuImageView);
        emailTextView = rootView.findViewById(R.id.email);
        departmentTextView = rootView.findViewById(R.id.department);
        contactTextView = rootView.findViewById(R.id.contact);
        institutionTextView = rootView.findViewById(R.id.institution);
        rollView=rootView.findViewById(R.id.roll);
        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        logout = rootView.findViewById(R.id.logout);
        logout.setOnClickListener(v -> logoutUser());

        editAccountButton=rootView.findViewById(R.id.editAccount);
        news=rootView.findViewById(R.id.news);
        news.setOnClickListener(v -> news());


        Button scholar = rootView.findViewById(R.id.scholar);
        scholar.setOnClickListener(v -> showQueryInputDialog());
        editAccountButton.setOnClickListener(v -> editAccount());


        menuImageView.setOnClickListener(this::showPopupMenu);

        setBioButton.setOnClickListener(v ->
        {
            String bio = editBioEditText.getText().toString();
           bioTextView.setText(bio);
                editBioEditText.setVisibility(View.GONE);
                setBioButton.setVisibility(View.GONE);
                bioTextView.setVisibility(View.VISIBLE);
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String rol = sharedPreferences.getString("role", null);
            String email = sharedPreferences.getString("email", null);
            String institution = sharedPreferences.getString("institution", null);
            String sanitizedEmail = email.replace(".", "_").replace("@", "_");
            DatabaseReference dbr = firebaseDatabase.getReference("ProfileInfo")
                    .child(institution)
                    .child(rol)
                    .child(sanitizedEmail)
                    .child("bio");

            dbr.setValue(bio).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    // Additional actions on success
                } else {

                }
            });
        });



        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String institution = sharedPreferences.getString("institution", null);
        fetchData(email,institution);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh logic here, e.g., re-fetch data
            fetchData(email,institution); // Replace with your data-fetching logic



            // Stop the refreshing animation
            swipeRefreshLayout.setRefreshing(false);
        });
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();

        // Fetch the data again when the fragment comes into view
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String institution = sharedPreferences.getString("institution", null);

        if (email != null && institution != null) {
            fetchData(email, institution);  // Re-fetch profile data
        }
    }

private void news()
{
    Intent intent = new Intent(requireActivity(), news.class);
    startActivity(intent);
}
    private void fetchData(String email, String institution) {
        // Sanitize email for Firebase key usage
        String sanitizedEmail = email.replace(".", "_").replace("@", "_");

        studentReference = firebaseDatabase.getReference("ProfileInfo").child(institution).child("student");
        teacherReference = firebaseDatabase.getReference("ProfileInfo").child(institution).child("teacher");

        // Fetch student data
        studentReference.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    role="student";

                    populateUI(snapshot);
                } else {
                   role="teacher";
                    fetchTeacherData(sanitizedEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching student data: " + error.getMessage());
                Toast.makeText(requireContext(), "Failed to load student data.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.menu_profile_options); // Inflate your menu file here
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_setbio) {

                    bioTextView.setVisibility(View.GONE);
                    editBioEditText.setVisibility(View.VISIBLE);
                    setBioButton.setVisibility(View.VISIBLE);
                    return true;
                }

                if(id==R.id.action_del)
                {
                    bioTextView.setText("Set Bio");
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    String rol = sharedPreferences.getString("role", null);
                    String email = sharedPreferences.getString("email", null);
                    String institution = sharedPreferences.getString("institution", null);
                    String sanitizedEmail = email.replace(".", "_").replace("@", "_");
                    DatabaseReference dbr = firebaseDatabase.getReference("ProfileInfo")
                            .child(institution)
                            .child(rol)
                            .child(sanitizedEmail)
                            .child("bio");

                    dbr.setValue("Set Bio").addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // Additional actions on success
                        } else {

                        }
                    });
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void fetchTeacherData(String email) {
        teacherReference.child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    role="teacher";

                    populateUI(snapshot);
                } else {
                    role="student";

                    Toast.makeText(requireContext(), "No profile data available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(requireContext(), "Failed to load teacher data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI(DataSnapshot dataSnapshot) {
        String firstname = dataSnapshot.child("firstname").getValue(String.class);
        String lastname = dataSnapshot.child("lastname").getValue(String.class);
        String department = dataSnapshot.hasChild("sdept") ?
                dataSnapshot.child("sdept").getValue(String.class) :
                dataSnapshot.child("tdept").getValue(String.class);

        String contactNo = dataSnapshot.child("contactNo").getValue(String.class);
        String email = dataSnapshot.child("email").getValue(String.class);
        String institution = dataSnapshot.child("institution").getValue(String.class);
        String bio=dataSnapshot.child("bio").getValue(String.class);

        // Update UI
        if (nameTextView != null) nameTextView.setText(firstname + " " + lastname);
        if (departmentTextView != null) departmentTextView.setText(Html.fromHtml("<b>Department: </b>" + department));
        if (emailTextView != null) emailTextView.setText(Html.fromHtml("<b>Email: </b>" + email));
        if (contactTextView != null) contactTextView.setText(Html.fromHtml("<b>Contact: </b>" + contactNo));
        if (institutionTextView != null) institutionTextView.setText(Html.fromHtml("<b>Institution: </b>" + institution));

        bioTextView.setText(bio);


        if(dataSnapshot.hasChild("roll"))
        {String id=dataSnapshot.child("roll").getValue(String.class);
            rollView.setText(Html.fromHtml("<b>Roll : </b>"+id));
        }
        else
        {String id=dataSnapshot.child("id").getValue(String.class);
            rollView.setText(Html.fromHtml("<b>ID : </b>"+id));
       }
        saveLoginInfo(email, firstname + " " + lastname);
    }

    private void saveLoginInfo(String email, String name) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putString("role",role);
        editor.apply();

    }


    private void logoutUser() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all saved preferences
        editor.apply();

        // Navigate back to MainActivity
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);


    }


    private void editAccount() {
        Intent intent = new Intent(requireActivity(), EditAccountActivity.class);

        startActivity(intent);

    }

    private void showQueryInputDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.activity_google_scholar);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText queryEditText = dialog.findViewById(R.id.queryEditText);
        Button searchButton = dialog.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v -> {
            query = queryEditText.getText().toString();
            if (!query.isEmpty()) {
                Intent intent = new Intent(requireContext(), ResultsActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                dialog.dismiss();
            } else {
                queryEditText.setError("Query cannot be empty");
            }
        });

        dialog.show();
    }


}

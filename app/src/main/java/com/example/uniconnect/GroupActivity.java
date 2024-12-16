package com.example.uniconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private final String[] tabTitles = {"HOME", "ACTIVITY", "CLASSROOM"};
    private TextView groupNameTextView, groupmembernumber;

    long count;
    private String institution,adminKey, emailKey, groupName,role,admin_role,myname;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ImageView groupImageView = findViewById(R.id.groupImageView);
        ImageView menuImageView = findViewById(R.id.menuImageView);

        groupImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        menuImageView.setOnClickListener(v -> {
            showPopupMenu(v);
        });

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        groupNameTextView = findViewById(R.id.groupNameTextView);
        groupmembernumber = findViewById(R.id.groupmembernumber);
        groupmembernumber.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivity.this, GroupMemberActivity.class);
            intent.putExtra("groupName", groupName);
            startActivity(intent);});
        // Receive the group name from the Intent
        Intent intent = getIntent();
        groupName = intent.getStringExtra("groupName");

        // Display the group name
        if (groupName != null) {
            groupNameTextView.setText(groupName);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cur_group", groupName);
        editor.apply();
        String email = sharedPreferences.getString("email", null);
myname=sharedPreferences.getString("name",null);
        // Validate data
         institution = sharedPreferences.getString("institution", null);
        // Process email key
        emailKey = email.replace(".", "_").replace("@", "_");
        String rolePath = "ProfileInfo/" + institution + "/teacher/" + emailKey;
         role = sharedPreferences.getString("rool", null);

        // Check user role and initialize Firebase references
        DatabaseReference groupRef = FirebaseDatabase.getInstance()
                .getReference("ProfileInfo").child(institution).child(role).child(emailKey).child("Groups").child(groupName);

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    adminKey = snapshot.child("admin").getValue(String.class);

                    admin_role=snapshot.child("admin_role").getValue(String.class);

                     know_num();


                    editor.putString("group_admin_key", adminKey);
                    editor.putString("group_admin_role",admin_role);
                    editor.apply();
                    GroupPagerAdapter adapter = new GroupPagerAdapter(GroupActivity.this, adminKey);
                    viewPager.setAdapter(adapter);

                    // Attach the TabLayout with ViewPager2
                    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set up the ViewPager with a custom adapter
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Enlarge the selected tab
                tab.view.setScaleX(1.5f);  // Increase width
                tab.view.setScaleY(1.5f);  // Increase height
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Reset size for unselected tabs
                tab.view.setScaleX(1f);  // Reset width to normal
                tab.view.setScaleY(1f);  // Reset height to normal
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional: Keep magnified size when tab is reselected
                tab.view.setScaleX(1.5f);
                tab.view.setScaleY(1.5f);
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_group_options); // Inflate your menu file here
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_exit_group) {
                    exitGroup();
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void know_num() {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("ProfileInfo").child(institution).child(admin_role).child(adminKey).child("Groups").child(groupName).child("members");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = dataSnapshot.getChildrenCount();
                Log.d("KeyCount", "Number of child keys: " + count);

                groupmembernumber.setText(count + " Members ");// Log the count
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
            }
        });



    }

    private void exitGroup() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);

        if(emailKey.equals(adminKey)) {
            DatabaseReference userGroupRef = FirebaseDatabase.getInstance()
                    .getReference("ProfileInfo").child(institution).child(role).child(emailKey).child("Groups").child(groupName);

            userGroupRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(GroupActivity.this, "You have exited the group", Toast.LENGTH_SHORT).show();
                    // Navigate back to the GroupListFragment
                    Intent intent = new Intent(GroupActivity.this, MainActivity.class);
                    intent.putExtra("navigateTo", "GroupListFragment");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(GroupActivity.this, "Failed to exit group", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {DatabaseReference userGroupRef1 = FirebaseDatabase.getInstance()
                .getReference("ProfileInfo").child(institution).child(admin_role).child(adminKey).child("Groups").child(groupName);

        userGroupRef1.child("members").child(myname).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

            } else {

            }
        });

        //userGroupRef1.child("member_number").

            DatabaseReference userGroupRef = FirebaseDatabase.getInstance()
                    .getReference("ProfileInfo").child(institution).child(role).child(emailKey).child("Groups").child(groupName);

            userGroupRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(GroupActivity.this, "You have exited the group", Toast.LENGTH_SHORT).show();
                    // Navigate back to the GroupListFragment
                    Intent intent = new Intent(GroupActivity.this, MainActivity.class);
                    intent.putExtra("navigateTo", "GroupListFragment");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(GroupActivity.this, "Failed to exit group", Toast.LENGTH_SHORT).show();
                }
            });

    }}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Set the selected image to the ImageView
                ImageView groupImageView = findViewById(R.id.groupImageView);
                groupImageView.setImageURI(imageUri);

                // Optionally, upload the image to Firebase or save it locally
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class GroupPagerAdapter extends FragmentStateAdapter {
        private final String adminKey;

        public GroupPagerAdapter(@NonNull FragmentActivity fragmentActivity, String adminKey) {
            super(fragmentActivity);
            this.adminKey = adminKey;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new PostFragment();
                case 1:
                    if (adminKey != null && adminKey.equals(emailKey)) {
                        return new AssignmentFragment();
                    } else {
                        return new AssignmentListFragment();
                    }
                case 2:
                    if (adminKey != null && adminKey.equals(emailKey)) {
                        return new ClassroomFragment();
                    } else {
                        return new ClassroomListFragment();
                    }
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Total tabs
        }
    }
}

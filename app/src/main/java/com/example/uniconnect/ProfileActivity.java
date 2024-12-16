package com.example.uniconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.uniconnect.databinding.ActivityProfileBinding;
import com.google.android.material.bottomnavigation.*;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Inflate the binding layout
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the default fragment
        replaceFragment(new HomeFragment());

        // Set up BottomNavigationView and item selection listener

        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        replaceFragment(new HomeFragment());
                        return true;
                    case R.id.navigation_profile:
                        replaceFragment(new ProfileFragment());
                        return true;
                    case R.id.navigation_teacher_list:
                        replaceFragment(new TeacherListFragment());
                        return true;
                    case R.id.navigation_student_list:
                        replaceFragment(new StudentListFragment());
                        return true;
                    case R.id.navigation_group_list:
                        replaceFragment(new GroupListFragment());
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Optional: Highlight the current tab
        binding.bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }



 private void replaceFragment(Fragment fragment)
 { if (fragment != currentFragment)
 { FragmentManager fragmentManager = getSupportFragmentManager();
     FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
     if (fragment.isAdded()) { fragmentTransaction.show(fragment); }
     else { fragmentTransaction.add(R.id.frame_layout, fragment); }
     if (currentFragment != null)
     { fragmentTransaction.hide(currentFragment); }
     fragmentTransaction.commit(); currentFragment = fragment; } }


}

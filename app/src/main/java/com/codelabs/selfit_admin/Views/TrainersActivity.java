package com.codelabs.selfit_admin.Views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.fragments.AdminHomeFragment;
import com.codelabs.selfit_admin.Views.fragments.TrainersUsersFragment;
import com.codelabs.selfit_admin.Views.fragments.TrainersHomeFragment;
import com.codelabs.selfit_admin.Views.fragments.TrainersProfileFragment;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class TrainersActivity extends AppCompatActivity {
    private AnimatedBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainers);

        bottomBar = findViewById(R.id.trainersBottomBar);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_trainers, new TrainersHomeFragment()).commit();
        bottomBar.selectTabById(R.id.trainers_home,true);

        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                Fragment fragment = null;
                switch (tab1.getId()) {
                    case R.id.trainers_home:
                        fragment = new TrainersHomeFragment();
                        break;
                    case R.id.trainers_chat:
                        fragment = new TrainersUsersFragment();
                        break;
                    case R.id.trainers_profile:
                        fragment = new TrainersProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_trainers, fragment).commit();
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });
    }
}
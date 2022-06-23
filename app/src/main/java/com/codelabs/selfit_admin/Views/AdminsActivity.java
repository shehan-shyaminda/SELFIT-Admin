package com.codelabs.selfit_admin.Views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.fragments.AdminHomeFragment;
import com.codelabs.selfit_admin.Views.fragments.AdminPaymentFragment;
import com.codelabs.selfit_admin.Views.fragments.AdminProfileFragment;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import nl.joery.animatedbottombar.AnimatedBottomBar;

public class AdminsActivity extends BaseActivity {
    private AnimatedBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins);

        bottomBar = findViewById(R.id.adminsBottomBar);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_admins, new AdminHomeFragment()).commit();
        bottomBar.selectTabById(R.id.home,true);

        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                Fragment fragment = null;
                switch (tab1.getId()) {
                    case R.id.home:
                        fragment = new AdminHomeFragment();
                        break;
                    case R.id.payments:
                        fragment = new AdminPaymentFragment();
                        break;
                    case R.id.profile:
                        fragment = new AdminProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_admins, fragment).commit();
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });
    }
}
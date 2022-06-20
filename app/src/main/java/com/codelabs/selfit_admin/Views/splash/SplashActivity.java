package com.codelabs.selfit_admin.Views.splash;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.BaseActivity;
import com.codelabs.selfit_admin.Views.MainActivity;
import com.codelabs.selfit_admin.Views.authentication.LoginActivity;
import com.codelabs.selfit_admin.Views.authentication.RegisterActivity;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;

public class SplashActivity extends BaseActivity {

    private SharedPreferencesManager sharedPreferencesManager;
    private ConstraintLayout textLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        textLayout = findViewById(R.id.text_layout);

        sharedPreferencesManager = new SharedPreferencesManager(SplashActivity.this);

        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(2000);
        animation.setStartOffset(2000);
        animation.setFillAfter(true);
        textLayout.startAnimation(animation);

        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(5000);

                    if(sharedPreferencesManager.getBooleanPreferences(SharedPreferencesManager.IS_DONE_TUTORIAL)) {
                        if(sharedPreferencesManager.getBooleanPreferences(SharedPreferencesManager.USER_LOGGED_IN)) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }else{
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        }
                    }else{
                        startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
                    }
                    finishAffinity();
                } catch (Exception e) {
                    Log.e(TAG, "run: " +e.getLocalizedMessage());
                }
            }
        };
        background.start();
    }
}
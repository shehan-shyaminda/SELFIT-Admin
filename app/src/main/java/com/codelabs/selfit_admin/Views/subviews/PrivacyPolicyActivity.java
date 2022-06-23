package com.codelabs.selfit_admin.Views.subviews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.codelabs.selfit_admin.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private CircleImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        btnBack = findViewById(R.id.btn_privacy_policy);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
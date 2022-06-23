package com.codelabs.selfit_admin.Views.subviews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.AdminsActivity;

public class SuccessEmailActivity extends AppCompatActivity {

    Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_email);

        btnBack = findViewById(R.id.btn_success_email_send);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SuccessEmailActivity.this, AdminsActivity.class));
                Animatoo.animateSlideRight(SuccessEmailActivity.this);
            }
        });

    }
}
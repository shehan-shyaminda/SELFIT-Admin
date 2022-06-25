package com.codelabs.selfit_admin.Views.subviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.codelabs.selfit_admin.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageUsersActivity extends AppCompatActivity {

    private Intent i;
    private TextView txtUserEmail, txtRegDate;
    private CircleImageView btnBack;
    private ConstraintLayout consMeal, consEx, consPhysic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        txtUserEmail = findViewById(R.id.txt_user_name);
        txtRegDate = findViewById(R.id.txt_user_regdate);
        btnBack = findViewById(R.id.btn_user_back);
        consMeal = findViewById(R.id.cons_assign_meal);
        consEx = findViewById(R.id.cons_assign_ex);
        consPhysic = findViewById(R.id.cons_view_images);

        i = getIntent();

        txtUserEmail.setText(i.getStringExtra("usersEmail"));
        txtRegDate.setText(i.getStringExtra("userRegDate"));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        consMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ManageUsersActivity.this, AssignMealActivity.class);
                i.putExtra("usersEmail", txtUserEmail.getText());
                startActivity(i);
            }
        });

        consEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ManageUsersActivity.this, AssignExerciseActivity.class);
                i.putExtra("usersEmail", txtUserEmail.getText());
                startActivity(i);
            }
        });

        consPhysic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ManageUsersActivity.this, ViewPhysicActivity.class);
                i.putExtra("usersEmail", txtUserEmail.getText());
                startActivity(i);
            }
        });
    }
}
package com.codelabs.selfit_admin.Views.subviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.helpers.CustomAlertDialog;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tomlonghurst.expandablehinttext.ExpandableHintText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAdminActivity extends AppCompatActivity {

    private CircleImageView btnBack;
    private ExpandableHintText txtUserName, txtUserPW;
    private Button btnUpdate;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin);

        btnBack = findViewById(R.id.btn_edit_admin);
        txtUserName = findViewById(R.id.txt_admin_name);
        txtUserPW = findViewById(R.id.txt_admin_pw);
        btnUpdate = findViewById(R.id.btn_update_admin);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(EditAdminActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(EditAdminActivity.this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();
                if(txtUserName.getText().isEmpty() && txtUserPW.getText().isEmpty()){
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(EditAdminActivity.this, "Oops!", "Please fill out all the\nrequired inputs!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    db.collection("admins").document(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                            .update(mapData(txtUserName.getText(), txtUserPW.getText()))
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    customProgressDialog.dismissProgress();
                                    clear();
                                    new CustomAlertDialog().positiveAlert(EditAdminActivity.this, "Congrats!", "Profile updated!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                 }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismissProgress();
                                    new CustomAlertDialog().negativeAlert(EditAdminActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            });
                }
            }
        });
    }

    private Map mapData(String name, String password) {
        Map<String, Object> map = new HashMap<>();
        if (!name.isEmpty() && !password.isEmpty()){
            map.put("adminName", name);
            map.put("adminPassword", password);
        }else if (name.isEmpty()){
            map.put("adminPassword", password);
        }else {
            map.put("adminName", name);
        }

        return map;
    }

    private void clear(){
        txtUserName.setText("");
        txtUserPW.setText("");
    }
}
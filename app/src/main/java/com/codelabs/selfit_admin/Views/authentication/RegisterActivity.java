package com.codelabs.selfit_admin.Views.authentication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.BaseActivity;
import com.codelabs.selfit_admin.helpers.CustomAlertDialog;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tomlonghurst.expandablehinttext.ExpandableEditText;
import com.tomlonghurst.expandablehinttext.ExpandableHintText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity {

    private Button btnReg, btnLog, btnSignup;
    private ExpandableHintText txtEmail, txtPassword, txtConPassword;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnLog = findViewById(R.id.btn_reg_login);
        btnReg = findViewById(R.id.btn_reg_signup);
        btnSignup = findViewById(R.id.btn_register);
        txtEmail = findViewById(R.id.txt_reg_email);
        txtPassword = findViewById(R.id.txt_reg_password);
        txtConPassword = findViewById(R.id.txt_reg_con_password);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(RegisterActivity.this);

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                Animatoo.animateFade(RegisterActivity.this);
                finishAffinity();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();
                if (txtEmail.getText().toString().isEmpty() ||txtPassword.getText().toString().isEmpty() ||txtConPassword.getText().toString().isEmpty()){
                    Log.e(TAG, "onClick: Field Empty");
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(RegisterActivity.this, "Oops!", "Please fill out all fields!", CFAlertDialog.CFAlertStyle.ALERT);
                }else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString().trim()).matches()){
                    Log.e(TAG, "onClick: Invalid Email");
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(RegisterActivity.this, "Oops!", "Invalid Email Address!", CFAlertDialog.CFAlertStyle.ALERT);
                }else if (!txtPassword.getText().toString().matches(txtConPassword.getText().toString())){
                    Log.e(TAG, "onClick: Not matching passwords");
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(RegisterActivity.this, "Oops!", "Passwords doesn't match!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    db.collection("admins").document(txtEmail.getText().toString().trim().toLowerCase()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()){
                                        Log.e(TAG, "onSuccess: " + txtEmail.getText().toString().trim().toLowerCase() );
                                        customProgressDialog.dismissProgress();
                                        new CustomAlertDialog().negativeDismissAlert(RegisterActivity.this, "Oops!", "This Email has already registered!", CFAlertDialog.CFAlertStyle.ALERT);
                                    }else{
                                        setData();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismissProgress();
                                    new CustomAlertDialog().negativeDismissAlert(RegisterActivity.this, "Oops!", "Something went wrong\nPlease try again later!", CFAlertDialog.CFAlertStyle.ALERT);
                                    Log.e(TAG, e.getLocalizedMessage());
                                }
                            });
                }
            }
        });
    }

    private void setData(){
        db.collection("admins").document(txtEmail.getText().toString().trim().toLowerCase())
                .set(mapData(txtEmail.getText().toString().toLowerCase(), txtPassword.getText().toString()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        customProgressDialog.dismissProgress();
                        Log.e(TAG, "Success");
                        clearFields();

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        Animatoo.animateFade(RegisterActivity.this);
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        new CustomAlertDialog().negativeDismissAlert(RegisterActivity.this, "Oops!", "Something went wrong\nPlease try again later!", CFAlertDialog.CFAlertStyle.ALERT);
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                });
    }

    @SuppressLint("SimpleDateFormat")
    private Map mapData(String email, String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("adminEmail", email);
        map.put("adminName", "");
        map.put("adminPassword", password);
        map.put("isAdmin", true);
        map.put("trainerAdminsID", "");
        map.put("regDate", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        return map;
    }

    private void clearFields(){
        txtEmail.setText("");
        txtPassword.setText("");
        txtConPassword.setText("");
    }
}
package com.codelabs.selfit_admin.Views.authentication;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.BaseActivity;
import com.codelabs.selfit_admin.Views.AdminsActivity;
import com.codelabs.selfit_admin.Views.TrainersActivity;
import com.codelabs.selfit_admin.helpers.CustomAlertDialog;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tomlonghurst.expandablehinttext.ExpandableHintText;

public class LoginActivity extends BaseActivity {

    private Button btnReg, btnLog, btnLogin;
    private ExpandableHintText txtEmail, txtPassword;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLog = findViewById(R.id.btn_log_login);
        btnReg = findViewById(R.id.btn_log_signup);
        btnLogin = findViewById(R.id.btn_login);
        txtEmail = findViewById(R.id.txt_log_email);
        txtPassword = findViewById(R.id.txt_log_password);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(LoginActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(LoginActivity.this);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                Animatoo.animateFade(LoginActivity.this);
                finishAffinity();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();
                if (txtEmail.getText().toString().isEmpty() ||txtPassword.getText().toString().isEmpty()){
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(LoginActivity.this, "Oops!", "Email & Password are required!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    db.collection("admins").document(txtEmail.getText().toString().trim().toLowerCase()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    customProgressDialog.dismissProgress();
                                    if (documentSnapshot.exists() && documentSnapshot.get("adminPassword").toString().equals(txtPassword.getText().toString().trim())){
                                        clearFields();
                                        savePreferences(Boolean.parseBoolean(documentSnapshot.get("isAdmin").toString()), documentSnapshot.get("trainerAdminsID").toString(), txtEmail.getText().toString().trim().toLowerCase());
                                        if (Boolean.parseBoolean(documentSnapshot.get("isAdmin").toString())){
                                            startActivity(new Intent(LoginActivity.this, AdminsActivity.class));
                                        }else{
                                            startActivity(new Intent(LoginActivity.this, TrainersActivity.class));
                                        }
                                        Animatoo.animateSlideLeft(LoginActivity.this);
                                        finishAffinity();
                                    }else{
                                        new CustomAlertDialog().negativeDismissAlert(LoginActivity.this, "Oops!", "Email or Password incorrect!", CFAlertDialog.CFAlertStyle.ALERT);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismissProgress();
                                    new CustomAlertDialog().negativeDismissAlert(LoginActivity.this, "Oops!", "Email or Password incorrect!", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            });
                }
            }
        });
    }

    private void savePreferences(Boolean isAdmin, String adminsID, String userID) {
        sharedPreferencesManager.savePreferences(SharedPreferencesManager.USER_LOGGED_IN, true);
        if (isAdmin){
            sharedPreferencesManager.savePreferences(SharedPreferencesManager.IS_ADMIN, true);
            sharedPreferencesManager.savePreferences(SharedPreferencesManager.TRAINER_ID, userID);
            sharedPreferencesManager.savePreferences(SharedPreferencesManager.TRAINER_ADMINS_ID, "ADMIN");
        } else{
            sharedPreferencesManager.savePreferences(SharedPreferencesManager.IS_ADMIN, false);
            sharedPreferencesManager.savePreferences(SharedPreferencesManager.TRAINER_ID, userID);
            sharedPreferencesManager.savePreferences(SharedPreferencesManager.TRAINER_ADMINS_ID, adminsID);
        }
    }

    private void clearFields(){
        txtEmail.setText("");
        txtPassword.setText("");
    }
}

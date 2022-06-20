package com.codelabs.selfit_admin.Views.authentication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.BaseActivity;
import com.codelabs.selfit_admin.Views.MainActivity;
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
                    db.collection("users").document(txtEmail.getText().toString().trim().toLowerCase()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    customProgressDialog.dismissProgress();
                                    if (documentSnapshot.exists() && documentSnapshot.get("userPassword").toString().equals(txtPassword.getText().toString().trim())){
                                        customProgressDialog.dismissProgress();
                                        clearFields();
                                        savePreferences();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        Animatoo.animateSlideLeft(LoginActivity.this);
                                        finishAffinity();
                                    }else{
                                        customProgressDialog.dismissProgress();
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

    private void savePreferences() {
        sharedPreferencesManager.savePreferences(SharedPreferencesManager.USER_LOGGED_IN, true);
    }

    private void clearFields(){
        txtEmail.setText("");
        txtPassword.setText("");
    }
}

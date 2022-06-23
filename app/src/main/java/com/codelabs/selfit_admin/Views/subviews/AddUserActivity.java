package com.codelabs.selfit_admin.Views.subviews;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.authentication.LoginActivity;
import com.codelabs.selfit_admin.Views.authentication.RegisterActivity;
import com.codelabs.selfit_admin.helpers.CustomAlertDialog;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomlonghurst.expandablehinttext.ExpandableHintText;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUserActivity extends AppCompatActivity {

    private CircleImageView btnBack;
    private ExpandableHintText txtUserEmail;
    private Button btnReg;
    private Spinner spnTrainer;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private SecureRandom random;
    private StringBuilder sb;

    final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        btnBack = findViewById(R.id.btn_add_user_back);
        txtUserEmail = findViewById(R.id.txt_add_user_email);
        btnReg = findViewById(R.id.btn_reg_user);
        spnTrainer = findViewById(R.id.spinner_add_users_trainer);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(AddUserActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(AddUserActivity.this);

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();
                if(txtUserEmail.getText().isEmpty() || spnTrainer.getSelectedItem().toString().equals("Loading...")){
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(AddUserActivity.this, "Oops!", "Please fill out all the\nrequired inputs!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    db.collection("users").document(txtUserEmail.getText().toString().trim().toLowerCase())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        customProgressDialog.dismissProgress();
                                        new CustomAlertDialog().negativeDismissAlert(AddUserActivity.this, "Oops!", "Email has been already registered!", CFAlertDialog.CFAlertStyle.ALERT);
                                    }else{
                                        random = new SecureRandom();
                                        sb = new StringBuilder();

                                        for (int j = 0; j < 6; j++)
                                        {
                                            int randomIndex = random.nextInt(chars.length());
                                            sb.append(chars.charAt(randomIndex));
                                        }

                                        db.collection("users").document(txtUserEmail.getText().toString().trim().toLowerCase())
                                                .set(mapData(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID), spnTrainer.getSelectedItem().toString(),
                                                        txtUserEmail.getText().toString().trim().toLowerCase(), sb.toString()))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        try {
                                                            Properties properties = System.getProperties();

                                                            properties.put("mail.smtp.host", "smtp.gmail.com");
                                                            properties.put("mail.smtp.port", "465");
                                                            properties.put("mail.smtp.ssl.enable", "true");
                                                            properties.put("mail.smtp.auth", "true");

                                                            Session session = Session.getInstance(properties, new Authenticator() {
                                                                @Override
                                                                protected PasswordAuthentication getPasswordAuthentication() {
                                                                    return new PasswordAuthentication("noreply.selfit@gmail.com","xjetdxlwxszlhxts");
                                                                }
                                                            });

                                                            MimeMessage mimeMessage = new MimeMessage(session);
                                                            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(txtUserEmail.getText().toString().trim()));
                                                            mimeMessage.setSubject("Subject: Regarding Account Creation");
                                                            mimeMessage.setText("Dear valued customer, \n\nYou have registered to SELFIT - Fitness Training App. \n\nYour Email : " + txtUserEmail.getText().toString().trim() + "\nYour Password : " +  sb.toString() +  " \n\nCheers!\n@TEAM-SELFIT");

                                                            Thread thread = new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        Transport.send(mimeMessage);
                                                                        startActivity(new Intent(AddUserActivity.this, SuccessEmailActivity.class));
                                                                        Animatoo.animateSlideLeft(AddUserActivity.this);
                                                                        customProgressDialog.dismissProgress();
                                                                    } catch (MessagingException e) {
//                                                    new CustomAlertDialog().negativeAlert(AddUserActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                                                        e.printStackTrace();
                                                                        customProgressDialog.dismissProgress();
                                                                    }
                                                                }
                                                            });
                                                            thread.start();
                                                        } catch (AddressException e) {
                                                            customProgressDialog.dismissProgress();
                                                            new CustomAlertDialog().negativeAlert(AddUserActivity.this, "Oops!", "Check Your Email Address & Please try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                                            e.printStackTrace();
                                                        } catch (MessagingException e) {
                                                            customProgressDialog.dismissProgress();
                                                            new CustomAlertDialog().negativeAlert(AddUserActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                                            e.printStackTrace();
                                                        }catch (Exception e){
                                                            customProgressDialog.dismissProgress();
                                                            new CustomAlertDialog().negativeAlert(AddUserActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        customProgressDialog.dismissProgress();
                                                    }
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismissProgress();
                                    new CustomAlertDialog().negativeDismissAlert(AddUserActivity.this, "Oops!", "Something went wrong\nPlease try again later!", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            });
                }
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private Map mapData(String AdminID, String TrainerID, String userEmail, String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("userAdminID", AdminID);
        map.put("userEmail", userEmail);
        map.put("userPassword", password);
        map.put("userRegDate", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        map.put("userTrainerID", TrainerID);

        return map;
    }

    private void init(){
        customProgressDialog.createProgress();
        db.collection("admins").whereEqualTo("trainerAdminsID", sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> arraylist = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if (document != null) {
                                    String item = document.getId();
                                    arraylist.add(item);
                                }
                            }
                            Collections.sort(arraylist);
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AddUserActivity.this, android.R.layout.simple_spinner_dropdown_item, arraylist);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            customProgressDialog.dismissProgress();
                            spnTrainer.setAdapter(spinnerArrayAdapter);
                        } else {
                            customProgressDialog.dismissProgress();
                            Log.d(TAG, task.getException().getMessage());
                            new CustomAlertDialog().negativeAlert(AddUserActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                           }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        new CustomAlertDialog().negativeAlert(AddUserActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        e.printStackTrace();
                    }
                });
    }
}
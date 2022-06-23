package com.codelabs.selfit_admin.Views.subviews;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.codelabs.selfit_admin.helpers.CustomAlertDialog;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
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

public class EditUserActivity extends AppCompatActivity {

    private CircleImageView btnBack;
    private Button btnReset;
    private Spinner spnEmail;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private SecureRandom random;
    private StringBuilder sb;

    final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        btnBack = findViewById(R.id.btn_reset_user_back);
        btnReset = findViewById(R.id.btn_reset_user);
        spnEmail = findViewById(R.id.spinner_reset_user);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(EditUserActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(EditUserActivity.this);

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();
                if(spnEmail.getSelectedItem().toString().equals("Loading...")){
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(EditUserActivity.this, "Oops!", "Please fill out all the\nrequired inputs!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    db.collection("users").document(spnEmail.getSelectedItem().toString())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        random = new SecureRandom();
                                        sb = new StringBuilder();

                                        for (int j = 0; j < 6; j++)
                                        {
                                            int randomIndex = random.nextInt(chars.length());
                                            sb.append(chars.charAt(randomIndex));
                                        }

                                        db.collection("users").document(spnEmail.getSelectedItem().toString())
                                                .update(mapData(sb.toString()))
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
                                                            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(spnEmail.getSelectedItem().toString()));
                                                            mimeMessage.setSubject("Subject: Regarding Account Password Reset");
                                                            mimeMessage.setText("Dear valued customer, \n\nYou have successfully changed your password of SELFIT - Fitness Training App. \n\nYour Email : " + spnEmail.getSelectedItem().toString() + "\nYour Password : " +  sb.toString() +  " \n\nCheers!\n@TEAM-SELFIT");

                                                            Thread thread = new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        Transport.send(mimeMessage);
                                                                        startActivity(new Intent(EditUserActivity.this, SuccessEmailActivity.class));
                                                                        Animatoo.animateSlideLeft(EditUserActivity.this);
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
                                                            new CustomAlertDialog().negativeAlert(EditUserActivity.this, "Oops!", "Check Your Email Address & Please try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                                            e.printStackTrace();
                                                        } catch (MessagingException e) {
                                                            customProgressDialog.dismissProgress();
                                                            new CustomAlertDialog().negativeAlert(EditUserActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                                            e.printStackTrace();
                                                        }catch (Exception e){
                                                            customProgressDialog.dismissProgress();
                                                            new CustomAlertDialog().negativeAlert(EditUserActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
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
                                    }else{
                                        customProgressDialog.dismissProgress();
                                        new CustomAlertDialog().negativeDismissAlert(EditUserActivity.this, "Oops!", "There is no any account related to this Email!", CFAlertDialog.CFAlertStyle.ALERT);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismissProgress();
                                    new CustomAlertDialog().negativeDismissAlert(EditUserActivity.this, "Oops!", "Something went wrong\nPlease try again later!", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            });
                }
            }
        });
    }

    private void init(){
        customProgressDialog.createProgress();
        db.collection("users").whereEqualTo("userAdminID", sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
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
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(EditUserActivity.this, android.R.layout.simple_spinner_dropdown_item, arraylist);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            customProgressDialog.dismissProgress();
                            spnEmail.setAdapter(spinnerArrayAdapter);
                        } else {
                            customProgressDialog.dismissProgress();
                            Log.d(TAG, task.getException().getMessage());
                            new CustomAlertDialog().negativeAlert(EditUserActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        new CustomAlertDialog().negativeAlert(EditUserActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        e.printStackTrace();
                    }
                });
    }

    private Map mapData(String password) {
        Map<String, Object> map = new HashMap<>();

        map.put("userPassword", password);
        return map;
    }
}
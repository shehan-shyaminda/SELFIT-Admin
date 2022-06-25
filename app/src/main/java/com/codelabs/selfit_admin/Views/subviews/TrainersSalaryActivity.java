package com.codelabs.selfit_admin.Views.subviews;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.adapters.PaymentHistoryAdapter;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.codelabs.selfit_admin.models.PaymentHistory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainersSalaryActivity extends AppCompatActivity {

    private SharedPreferencesManager sharedPreferencesManager;
    private CustomProgressDialog customProgressDialog;
    private CircleImageView btnBack;
    private FirebaseFirestore db;
    private TextView txtEmpty;
    private ListView lstHistory;
    private ArrayList<PaymentHistory> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainers_salary);

        db = FirebaseFirestore.getInstance();
        sharedPreferencesManager = new SharedPreferencesManager(TrainersSalaryActivity.this);
        customProgressDialog = new CustomProgressDialog(TrainersSalaryActivity.this);

        btnBack = findViewById(R.id.btn_salary_trainer_back);
        txtEmpty = findViewById(R.id.txtEmptySalary);
        lstHistory = findViewById(R.id.lst_trainer_payments);

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        customProgressDialog.createProgress();
        lstHistory.setAdapter(null);
        list = new ArrayList<>();

        db.collection("adminMakesPayment")
                .whereEqualTo("trainersID", sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            Log.e(TAG, "onSuccess: Empty Collection");
                            lstHistory.setEnabled(false);
                            lstHistory.setVisibility(View.GONE);
                            txtEmpty.setEnabled(true);
                            txtEmpty.setVisibility(View.VISIBLE);
                        }
                        else{
                            DocumentSnapshot snapsList;
                            for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                                snapsList = task.getResult().getDocuments().get(i);
                                list.add(new PaymentHistory(snapsList.get("adminsID").toString(), snapsList.get("trainersID").toString(), snapsList.get("transAmount").toString(),
                                        snapsList.get("transDate").toString(), snapsList.get("transRef").toString()));
                            }

                            txtEmpty.setEnabled(false);
                            txtEmpty.setVisibility(View.GONE);
                            lstHistory.setEnabled(true);
                            lstHistory.setVisibility(View.VISIBLE);

                            PaymentHistoryAdapter listAdapter = new PaymentHistoryAdapter(TrainersSalaryActivity.this, list);
                            lstHistory.setAdapter(listAdapter);
                        }
                        customProgressDialog.dismissProgress();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        Log.e(TAG, "onFailure: " + e);
                    }
                });
    }
}
package com.codelabs.selfit_admin.Views.subviews;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.adapters.PaymentHistoryAdapter;
import com.codelabs.selfit_admin.adapters.PhysicAdapter;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.codelabs.selfit_admin.models.PaymentHistory;
import com.codelabs.selfit_admin.models.PhysicModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPhysicActivity extends AppCompatActivity {

    private Intent i;
    private TextView txtEmpty;
    private ListView lstPhotos;
    private CircleImageView btnBack;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private String userEmail;
    private ArrayList<PhysicModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_physic);

        txtEmpty = findViewById(R.id.txtEmptyImages);
        lstPhotos = findViewById(R.id.lst_physic);
        btnBack = findViewById(R.id.btn_physic_back);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(ViewPhysicActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(ViewPhysicActivity.this);

        i = getIntent();
        userEmail = i.getStringExtra("usersEmail");
        Log.e(TAG, "onCreate: " + userEmail );

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lstPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ViewPhysicActivity.this, ViewPhotoActivity.class);
                intent.putExtra("url", list.get(i).getImageUrl());
                intent.putExtra("date", list.get(i).getUploadedDate().replace("/","-"));
                startActivity(intent);
            }
        });
    }

    private void init() {
        customProgressDialog.createProgress();
        lstPhotos.setAdapter(null);
        list = new ArrayList<>();

        db.collection("users").document(userEmail).collection("userPhysics")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            Log.e(TAG, "onSuccess: Empty Collection");
                            lstPhotos.setEnabled(false);
                            lstPhotos.setVisibility(View.GONE);
                            txtEmpty.setEnabled(true);
                            txtEmpty.setVisibility(View.VISIBLE);
                        }
                        else{
                            DocumentSnapshot snapsList;
                            for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                                snapsList = task.getResult().getDocuments().get(i);
                                list.add(new PhysicModel(snapsList.get("imageUrl").toString(), snapsList.get("uploadedDate").toString()));
                            }

                            txtEmpty.setEnabled(false);
                            txtEmpty.setVisibility(View.GONE);
                            lstPhotos.setEnabled(true);
                            lstPhotos.setVisibility(View.VISIBLE);

                            PhysicAdapter listAdapter = new PhysicAdapter(ViewPhysicActivity.this, list);
                            lstPhotos.setAdapter(listAdapter);
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
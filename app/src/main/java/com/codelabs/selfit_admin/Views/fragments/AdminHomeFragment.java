package com.codelabs.selfit_admin.Views.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.authentication.LoginActivity;
import com.codelabs.selfit_admin.Views.subviews.AddTrainerActivity;
import com.codelabs.selfit_admin.Views.subviews.AddUserActivity;
import com.codelabs.selfit_admin.Views.subviews.EditTrainerActivity;
import com.codelabs.selfit_admin.Views.subviews.EditUserActivity;
import com.codelabs.selfit_admin.Views.subviews.RemoveTrainerActivity;
import com.codelabs.selfit_admin.Views.subviews.RemoveUserActivity;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class AdminHomeFragment extends Fragment {

    private ConstraintLayout cons_addUser, cons_rmUser, cons_editUser, cons_addTrainer, cons_rmTrainer, cons_editTrainer;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private Integer usersCount, trainersCount;
    private CircularProgressIndicator circularProgressUsers, circularProgressTrainers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin_home, container, false);

        cons_addUser= v.findViewById(R.id.cons_add_user);
        cons_rmUser= v.findViewById(R.id.cons_remove_user);
        cons_editUser= v.findViewById(R.id.cons_reset_user);
        cons_addTrainer= v.findViewById(R.id.cons_add_trainer);
        cons_rmTrainer= v.findViewById(R.id.cons_remove_trainer);
        cons_editTrainer= v.findViewById(R.id.cons_reset_trainer);
        circularProgressUsers = v.findViewById(R.id.cp_users);
        circularProgressTrainers = v.findViewById(R.id.cp_trainers);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getActivity());

        init();

        cons_addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddUserActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        cons_rmUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RemoveUserActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        cons_editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditUserActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        cons_addTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTrainerActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        cons_rmTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RemoveTrainerActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        cons_editTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditTrainerActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });
        return v;
    }

    private void init() {
        usersCount = 0;
        trainersCount = 0;
        customProgressDialog.createProgress();

        Log.e(TAG, "init: " + sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID) );
        db.collection("users")
                .whereEqualTo("userAdminID", sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            Log.e(TAG, "Empty Users Snapshot");

                            db.collection("admins")
                                    .whereEqualTo("trainerAdminsID", sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.getResult().isEmpty()){
                                                Log.e(TAG, "Empty Trainers Snapshot");
                                            }else{
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                    trainersCount += 1;
                                                }
                                                circularProgressUsers.setProgress(usersCount, usersCount+trainersCount);
                                                circularProgressTrainers.setProgress(trainersCount, usersCount+trainersCount);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
                                        }
                                    });
                        }else{
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                usersCount += 1;
                            }

                            db.collection("admins")
                                    .whereEqualTo("trainerAdminsID", sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.getResult().isEmpty()){
                                                Log.e(TAG, "Empty Trainers Snapshot");
                                            }else{
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                    trainersCount += 1;
                                                }
                                                circularProgressUsers.setProgress(usersCount, usersCount+trainersCount);
                                                circularProgressTrainers.setProgress(trainersCount, usersCount+trainersCount);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
                    }
                });

        customProgressDialog.dismissProgress();
    }
}
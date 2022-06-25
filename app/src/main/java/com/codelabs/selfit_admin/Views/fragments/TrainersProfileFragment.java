package com.codelabs.selfit_admin.Views.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.authentication.LoginActivity;
import com.codelabs.selfit_admin.Views.subviews.EditAdminActivity;
import com.codelabs.selfit_admin.Views.subviews.PrivacyPolicyActivity;
import com.codelabs.selfit_admin.Views.subviews.TrainersSalaryActivity;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TrainersProfileFragment extends Fragment {

    private ConstraintLayout consSignout, consResetPW, consPrivacyPolicy, consSalary;
    private TextView txtName;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trainers_profile, container, false);

        consSignout = v.findViewById(R.id.cons_trainers_signout);
        consResetPW = v.findViewById(R.id.cons_edit_trainers_profile);
        consPrivacyPolicy = v.findViewById(R.id.cons_trainers_privacy_policy);
        consSalary = v.findViewById(R.id.cons_salary_history);
        txtName = v.findViewById(R.id.txt_trainers_name);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getActivity());

        init();

        consSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();

                sharedPreferencesManager.clearPreferences(SharedPreferencesManager.IS_ADMIN);
                sharedPreferencesManager.clearPreferences(SharedPreferencesManager.TRAINER_ID);
                sharedPreferencesManager.clearPreferences(SharedPreferencesManager.TRAINER_ADMINS_ID);
                sharedPreferencesManager.clearPreferences(SharedPreferencesManager.USER_LOGGED_IN);

                customProgressDialog.dismissProgress();

                startActivity(new Intent(getActivity(), LoginActivity.class));
                Animatoo.animateSlideRight(getActivity());
                getActivity().finishAffinity();
            }
        });

        consResetPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditAdminActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        consSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), TrainersSalaryActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        consPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });
        return v;
    }

    private void init() {
        customProgressDialog.createProgress();
        db.collection("admins").document(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID)).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            if(task.getResult().get("adminName").toString().isEmpty()){
                                customProgressDialog.dismissProgress();
                                Log.e(TAG, "onComplete: " + sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID) );
                                txtName.setText(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID));
                            }else{
                                customProgressDialog.dismissProgress();
                                txtName.setText(task.getResult().get("adminName").toString());
                            }
                        }else{
                            customProgressDialog.dismissProgress();
                            txtName.setText(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID));
                            Log.e(TAG, "onComplete: " + sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID) );
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
//                        new CustomAlertDialog().negativeDismissAlert(getActivity(), "Oops!", "Something went wrong\nPlease try again later!", CFAlertDialog.CFAlertStyle.ALERT);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}
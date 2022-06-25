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

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.subviews.AddExerciseActivity;
import com.codelabs.selfit_admin.Views.subviews.AddMealActivity;
import com.codelabs.selfit_admin.Views.subviews.AddUserActivity;
import com.codelabs.selfit_admin.Views.subviews.EditExerciseActivity;
import com.codelabs.selfit_admin.Views.subviews.EditMealActivity;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class TrainersHomeFragment extends Fragment {

    private ConstraintLayout cons_add_exercise, cons_edit_exercise, cons_add_meal, cons_edit_meal;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private Integer usersCount;
    private CircularProgressIndicator circularProgressUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trainers_home, container, false);

        cons_add_exercise = v.findViewById(R.id.cons_add_exercise);
        cons_edit_exercise = v.findViewById(R.id.cons_edit_exercise);
        cons_add_meal = v.findViewById(R.id.cons_add_meal);
        cons_edit_meal = v.findViewById(R.id.cons_edit_meal);
        circularProgressUsers = v.findViewById(R.id.cp_trainer_users);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getActivity());

        init();

        cons_add_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddExerciseActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        cons_edit_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditExerciseActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        cons_add_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddMealActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        cons_edit_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditMealActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });
        return v;
    }

    private void init() {
        usersCount = 0;
        customProgressDialog.createProgress();

        Log.e(TAG, "init: " + sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID) );
        db.collection("users")
                .whereEqualTo("userTrainerID", sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            customProgressDialog.dismissProgress();
                            Log.e(TAG, "Empty Trainers Snapshot");
                        }else{
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                usersCount += 1;
                            }
                            customProgressDialog.dismissProgress();
                            circularProgressUsers.setProgress(usersCount, 10);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
                    }
                });
    }
}
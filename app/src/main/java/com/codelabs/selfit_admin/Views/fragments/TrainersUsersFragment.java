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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.subviews.ManageUsersActivity;
import com.codelabs.selfit_admin.Views.subviews.TrainersSalaryActivity;
import com.codelabs.selfit_admin.adapters.PaymentHistoryAdapter;
import com.codelabs.selfit_admin.adapters.TrainerUsersAdapter;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.codelabs.selfit_admin.models.PaymentHistory;
import com.codelabs.selfit_admin.models.UsersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TrainersUsersFragment extends Fragment {

    private ListView lstUsers;
    private SharedPreferencesManager sharedPreferencesManager;
    private CustomProgressDialog customProgressDialog;
    private FirebaseFirestore db;
    private ArrayList<UsersModel> list;
    private TextView txtEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_traiers_users, container, false);

        db = FirebaseFirestore.getInstance();
        sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        customProgressDialog = new CustomProgressDialog(getActivity());

        lstUsers = v.findViewById(R.id.lst_users);
        txtEmpty = v.findViewById(R.id.txtEmptyUsers);

        init();

        lstUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                Intent i = new Intent(getActivity(), ManageUsersActivity.class);
                i.putExtra("usersEmail", list.get(position).getUsersName());
                i.putExtra("userRegDate", list.get(position).getUsersRegDate().replace("/","-"));
                startActivity(i);
            }
        });

        return v;
    }

    private void init() {
        customProgressDialog.createProgress();
        lstUsers.setAdapter(null);
        list = new ArrayList<>();

        db.collection("users")
                .whereEqualTo("userTrainerID", sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            Log.e(TAG, "onSuccess: Empty Collection");
                            lstUsers.setEnabled(false);
                            lstUsers.setVisibility(View.GONE);
                            txtEmpty.setEnabled(true);
                            txtEmpty.setVisibility(View.VISIBLE);
                        }
                        else{
                            DocumentSnapshot snapsList;
                            for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                                snapsList = task.getResult().getDocuments().get(i);
                                list.add(new UsersModel(snapsList.get("userEmail").toString(), snapsList.get("userRegDate").toString()));
                            }

                            txtEmpty.setEnabled(false);
                            txtEmpty.setVisibility(View.GONE);
                            lstUsers.setEnabled(true);
                            lstUsers.setVisibility(View.VISIBLE);

                            TrainerUsersAdapter listAdapter = new TrainerUsersAdapter(getActivity(), list);
                            lstUsers.setAdapter(listAdapter);
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
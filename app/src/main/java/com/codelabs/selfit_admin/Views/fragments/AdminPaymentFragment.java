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
import android.widget.ListView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.Views.subviews.AddPaymentActivity;
import com.codelabs.selfit_admin.Views.subviews.RemoveUserActivity;
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
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Calendar;

public class AdminPaymentFragment extends Fragment {

    private ConstraintLayout cons_addUser;
    private SharedPreferencesManager sharedPreferencesManager;
    private CustomProgressDialog customProgressDialog;
    private FirebaseFirestore db;
    private TextView txtEmpty;
    private ListView lstHistory;
    private ArrayList<PaymentHistory> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin_payment, container, false);

        db = FirebaseFirestore.getInstance();
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        customProgressDialog = new CustomProgressDialog(getContext());

        cons_addUser = v.findViewById(R.id.cons_make_payment);
        txtEmpty = v.findViewById(R.id.textView13);
        lstHistory = v.findViewById(R.id.lst_payments);

//        init();

        cons_addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddPaymentActivity.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        return v;
    }

    private void init() {
        customProgressDialog.createProgress();
        lstHistory.setAdapter(null);
        list = new ArrayList<>();

        db.collection("adminMakesPayment")
                .whereEqualTo("adminsID", sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
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

                            if (getActivity()!=null){
                                PaymentHistoryAdapter listAdapter = new PaymentHistoryAdapter(getActivity(), list);
                                lstHistory.setAdapter(listAdapter);
                            }
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

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}
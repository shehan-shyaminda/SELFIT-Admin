package com.codelabs.selfit_admin.Views.subviews;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.helpers.CustomAlertDialog;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.codelabs.selfit_admin.models.MealsModel;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.numberpicker.NumberPicker;

public class AssignMealActivity extends AppCompatActivity {

    private Intent i;
    private Spinner spn01, spn02, spn03;
    private NumberPicker picker01, picker02, picker03;
    private CircleImageView btnBack;
    private Button btnAssign;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private ArrayAdapter<MealsModel> spinnerArrayAdapter;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_meal);

        spn01 = findViewById(R.id.spn_meal_01);
        spn02 = findViewById(R.id.spn_meal_02);
        spn03 = findViewById(R.id.spn_meal_03);
        picker01 = findViewById(R.id.picker_01);
        picker02 = findViewById(R.id.picker_02);
        picker03 = findViewById(R.id.picker_03);
        btnAssign = findViewById(R.id.btn_assign_meal);
        btnBack = findViewById(R.id.btn_assign_meal_back);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(AssignMealActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(AssignMealActivity.this);

        init();

        i = getIntent();
        userEmail = i.getStringExtra("usersEmail");
        Log.e(TAG, "onCreate: " + userEmail );

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();
                if (spn01.getSelectedItem().toString() == "Loading..." || spn02.getSelectedItem().toString() == "Loading..." || spn03.getSelectedItem().toString() == "Loading..." || picker01.getProgress() == 0 || picker02.getProgress() == 0 || picker03.getProgress() == 0 ){
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(AssignMealActivity.this, "Oops!", "Please fill out all the\nrequired inputs!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    db.collection("trainerAssignMeal").document(userEmail)
                            .set(mapData(spinnerArrayAdapter.getItem(spn01.getSelectedItemPosition()).getMealID(), Integer.toString(picker01.getProgress()), spinnerArrayAdapter.getItem(spn02.getSelectedItemPosition()).getMealID(), Integer.toString(picker02.getProgress()),
                                    spinnerArrayAdapter.getItem(spn03.getSelectedItemPosition()).getMealID(), Integer.toString(picker03.getProgress())))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    customProgressDialog.dismissProgress();
                                    clear();
                                    new CustomAlertDialog().positiveAlert(AssignMealActivity.this, "Done!", "Meal assigned!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismissProgress();
                                    Log.d(TAG, e.getLocalizedMessage());
                                    new CustomAlertDialog().negativeAlert(AssignMealActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            });
                }
            }
        });
    }

    private void init(){
        customProgressDialog.createProgress();
        db.collection("admins").document(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                .collection("meals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<MealsModel> arraylist = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if (document != null) {
                                    String item = document.getString("mealName");
                                    arraylist.add(new MealsModel(document.getId(), document.getString("mealName")));
                                }
                            };
                            spinnerArrayAdapter = new ArrayAdapter<MealsModel>(AssignMealActivity.this, android.R.layout.simple_spinner_dropdown_item, arraylist);
//                            spinnerArrayAdapter.clear();
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            customProgressDialog.dismissProgress();
                            spn01.setAdapter(spinnerArrayAdapter);
                            spn02.setAdapter(spinnerArrayAdapter);
                            spn03.setAdapter(spinnerArrayAdapter);
                        } else {
                            customProgressDialog.dismissProgress();
                            Log.d(TAG, task.getException().getMessage());
                            new CustomAlertDialog().negativeAlert(AssignMealActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        new CustomAlertDialog().negativeAlert(AssignMealActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        e.printStackTrace();
                    }
                });
    }

    private Map mapData(String meal01, String measure01, String meal02, String measure02, String meal03, String measure03) {
        Map<String, Object> map = new HashMap<>();
        map.put("meal01", meal01);
        map.put("measure01", measure01);
        map.put("meal02", meal02);
        map.put("measure02", measure02);
        map.put("meal03", meal03);
        map.put("measure03", measure03);

        return map;
    }

    private void clear(){
        spn01.setSelection(0);
        spn02.setSelection(0);
        spn03.setSelection(0);
        picker01.setProgress(0);
        picker02.setProgress(0);
        picker03.setProgress(0);
    }
}
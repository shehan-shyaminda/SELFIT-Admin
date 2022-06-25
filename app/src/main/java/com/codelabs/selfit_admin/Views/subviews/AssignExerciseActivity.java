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

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.helpers.CustomAlertDialog;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.codelabs.selfit_admin.models.ExercisesModel;
import com.codelabs.selfit_admin.models.MealsModel;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.numberpicker.NumberPicker;

public class AssignExerciseActivity extends AppCompatActivity {

    private Intent i;
    private Spinner spn01, spn02, spn03;
    private NumberPicker picker01, picker02, picker03;
    private CircleImageView btnBack;
    private Button btnAssign;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private ArrayAdapter<ExercisesModel> spinnerArrayAdapter;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_exercise);

        spn01 = findViewById(R.id.spn_ex_01);
        spn02 = findViewById(R.id.spn_ex_02);
        spn03 = findViewById(R.id.spn_ex_03);
        picker01 = findViewById(R.id.picker_ex_01);
        picker02 = findViewById(R.id.picker_ex_02);
        picker03 = findViewById(R.id.picker_ex_03);
        btnAssign = findViewById(R.id.btn_assign_ex);
        btnBack = findViewById(R.id.btn_assign_ex_back);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(AssignExerciseActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(AssignExerciseActivity.this);

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
                    new CustomAlertDialog().negativeDismissAlert(AssignExerciseActivity.this, "Oops!", "Please fill out all the\nrequired inputs!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    db.collection("trainerAssignWorkout").document(userEmail)
                            .set(mapData(spinnerArrayAdapter.getItem(spn01.getSelectedItemPosition()).getExerciseID(), Integer.toString(picker01.getProgress()), spinnerArrayAdapter.getItem(spn02.getSelectedItemPosition()).getExerciseID(), Integer.toString(picker02.getProgress()),
                                    spinnerArrayAdapter.getItem(spn03.getSelectedItemPosition()).getExerciseID(), Integer.toString(picker03.getProgress())))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    customProgressDialog.dismissProgress();
                                    clear();
                                    new CustomAlertDialog().positiveAlert(AssignExerciseActivity.this, "Done!", "Exercise assigned!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismissProgress();
                                    Log.d(TAG, e.getLocalizedMessage());
                                    new CustomAlertDialog().negativeAlert(AssignExerciseActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            });
                }
            }
        });
    }

    private void init(){
        customProgressDialog.createProgress();
        db.collection("admins").document(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                .collection("exercises")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<ExercisesModel> arraylist = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if (document != null) {
                                    String item = document.getString("mealName");
                                    arraylist.add(new ExercisesModel(document.getId(), document.getString("exName")));
                                }
                            };
                            spinnerArrayAdapter = new ArrayAdapter<ExercisesModel>(AssignExerciseActivity.this, android.R.layout.simple_spinner_dropdown_item, arraylist);
//                            spinnerArrayAdapter.clear();
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            customProgressDialog.dismissProgress();
                            spn01.setAdapter(spinnerArrayAdapter);
                            spn02.setAdapter(spinnerArrayAdapter);
                            spn03.setAdapter(spinnerArrayAdapter);
                        } else {
                            customProgressDialog.dismissProgress();
                            Log.d(TAG, task.getException().getMessage());
                            new CustomAlertDialog().negativeAlert(AssignExerciseActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        new CustomAlertDialog().negativeAlert(AssignExerciseActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        e.printStackTrace();
                    }
                });
    }

    private Map mapData(String ex01, String count01, String ex02, String count02, String ex03, String count03) {
        Map<String, Object> map = new HashMap<>();
        map.put("ex01", ex01);
        map.put("count01", count01);
        map.put("ex02", ex02);
        map.put("count02", count02);
        map.put("ex03", ex03);
        map.put("count03", count03);

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
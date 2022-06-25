package com.codelabs.selfit_admin.Views.subviews;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.codelabs.selfit_admin.models.MealsModel;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomlonghurst.expandablehinttext.ExpandableHintText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditMealActivity extends AppCompatActivity {

    private CircleImageView btnBack;
    private ExpandableHintText txtName, txtCals;
    private Button btnEdit;
    private Spinner spnMeals, spnMealUnits;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private ArrayAdapter<MealsModel> spinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meal);

        btnBack = findViewById(R.id.btn_edit_meal_back);
        btnEdit = findViewById(R.id.btn_edit_meal);
        txtName = findViewById(R.id.txt_edit_meal_name);
        txtCals = findViewById(R.id.txt_edit_meal_calories);
        spnMealUnits = findViewById(R.id.spn_edit_meal_unit);
        spnMeals = findViewById(R.id.spn_meal);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(EditMealActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(EditMealActivity.this);

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();
                if (txtName.getText().isEmpty() || txtCals.getText().isEmpty() || spnMeals.getSelectedItem().toString() == "Loading..."){
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(EditMealActivity.this, "Oops!", "Please fill out all the\nrequired inputs!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    db.collection("admins").document(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                            .collection("meals").document(spinnerArrayAdapter.getItem(spnMeals.getSelectedItemPosition()).getMealID())
                            .update(mapData(txtName.getText().toString(), txtCals.getText().toString(), spnMealUnits.getSelectedItem().toString()))
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    customProgressDialog.dismissProgress();
                                    clear();
                                    new CustomAlertDialog().positiveAlert(EditMealActivity.this, "Done!", "Meal details updated!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismissProgress();
                                    new CustomAlertDialog().negativeAlert(EditMealActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
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
                            spinnerArrayAdapter = new ArrayAdapter<MealsModel>(EditMealActivity.this, android.R.layout.simple_spinner_dropdown_item, arraylist);
//                            spinnerArrayAdapter.clear();
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            customProgressDialog.dismissProgress();
                            spnMeals.setAdapter(spinnerArrayAdapter);
                        } else {
                            customProgressDialog.dismissProgress();
                            Log.d(TAG, task.getException().getMessage());
                            new CustomAlertDialog().negativeAlert(EditMealActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        new CustomAlertDialog().negativeAlert(EditMealActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        e.printStackTrace();
                    }
                });
    }

    private Map mapData(String name, String calories, String unit) {
        Map<String, Object> map = new HashMap<>();
        map.put("mealCalories", calories + "Cals");
        map.put("mealName", name);
        map.put("mealUnit", unit);

        return map;
    }

    private void clear(){
        txtName.setText("");
        txtCals.setText("");
        spnMeals.setSelection(0);
        spnMealUnits.setSelection(0);
    }
}
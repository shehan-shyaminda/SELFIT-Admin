package com.codelabs.selfit_admin.Views.subviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.helpers.CustomAlertDialog;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tomlonghurst.expandablehinttext.ExpandableHintText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddMealActivity extends AppCompatActivity {

    private CircleImageView btnBack;
    private ExpandableHintText txtName, txtCals;
    private Button btnAdd;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private Spinner spnMealUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        btnBack = findViewById(R.id.btn_add_meal_back);
        btnAdd = findViewById(R.id.btn_add_meal);
        txtName = findViewById(R.id.txt_meal_name);
        txtCals = findViewById(R.id.txt_meal_calories);
        spnMealUnits = findViewById(R.id.spn_meal_unit);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(AddMealActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(AddMealActivity.this);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();
                if (txtName.getText().isEmpty() || txtCals.getText().isEmpty()){
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(AddMealActivity.this, "Oops!", "Please fill out all the\nrequired inputs!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    db.collection("admins").document(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                            .collection("meals")
                            .add(mapData(txtName.getText().toString(), txtCals.getText().toString(), spnMealUnits.getSelectedItem().toString()))
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    customProgressDialog.dismissProgress();
                                    clear();
                                    new CustomAlertDialog().positiveAlert(AddMealActivity.this, "Done!", "New exercise added!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismissProgress();
                                    new CustomAlertDialog().negativeAlert(AddMealActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            });
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        spnMealUnits.setSelection(0);
    }
}
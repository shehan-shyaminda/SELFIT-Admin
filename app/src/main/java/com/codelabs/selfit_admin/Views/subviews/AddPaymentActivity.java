package com.codelabs.selfit_admin.Views.subviews;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tomlonghurst.expandablehinttext.ExpandableHintText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPaymentActivity extends AppCompatActivity {

    private CircleImageView btnBack;
    private ExpandableHintText txtAmount, txtRef;
    private Spinner spnEmail;
    private Button btnPay;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private Pattern numberPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        btnBack = findViewById(R.id.btn_pay_back);
        txtAmount  = findViewById(R.id.txt_pay_fee);
        txtRef  = findViewById(R.id.txt_pay_ref);
        spnEmail  = findViewById(R.id.spinner_reset_trainer);
        btnPay = findViewById(R.id.btn_pay);

        db = FirebaseFirestore.getInstance();
        customProgressDialog = new CustomProgressDialog(AddPaymentActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(AddPaymentActivity.this);
        numberPattern = Pattern.compile("^[0-9]*\\.[0-9]{2}$");

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();
                if(spnEmail.getSelectedItem().toString().equals("Loading...") || txtAmount.getText().isEmpty() || txtRef.getText().isEmpty()){
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(AddPaymentActivity.this, "Oops!", "Please fill out all the\nrequired inputs!", CFAlertDialog.CFAlertStyle.ALERT);
                }else if(!numberPattern.matcher(txtAmount.getText().toString()).matches()){
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(AddPaymentActivity.this, "Oops!", "Invalid amount!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    db.collection("adminMakesPayment").add(mapData(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID),spnEmail.getSelectedItem().toString(),
                            txtAmount.getText(), txtRef.getText()))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    customProgressDialog.dismissProgress();
                                    clear();
                                    new CustomAlertDialog().positiveAlert(AddPaymentActivity.this, "Done!", "Payment has successfully recorded!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismissProgress();
                                    new CustomAlertDialog().negativeAlert(AddPaymentActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                }
                            });
                }
            }
        });
    }

    private void init(){
        customProgressDialog.createProgress();
        db.collection("admins").whereEqualTo("trainerAdminsID", sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> arraylist = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if (document != null) {
                                    String item = document.getId();
                                    arraylist.add(item);
                                }
                            }
                            Collections.sort(arraylist);
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AddPaymentActivity.this, android.R.layout.simple_spinner_dropdown_item, arraylist);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            customProgressDialog.dismissProgress();
                            spnEmail.setAdapter(spinnerArrayAdapter);
                        } else {
                            customProgressDialog.dismissProgress();
                            Log.d(TAG, task.getException().getMessage());
                            new CustomAlertDialog().negativeAlert(AddPaymentActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        new CustomAlertDialog().negativeAlert(AddPaymentActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        e.printStackTrace();
                    }
                });
    }

    @SuppressLint("SimpleDateFormat")
    private Map mapData(String adminsID, String trainerEmail, String amount, String ref) {
        Map<String, Object> map = new HashMap<>();
        map.put("adminsID", adminsID);
        map.put("trainersID", trainerEmail);
        map.put("transAmount", amount);
        map.put("transDate", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        map.put("transRef", ref);

        return map;
    }

    private void clear(){
        txtAmount.setText("");
        txtRef.setText("");
        spnEmail.setSelection(0);
    }
}
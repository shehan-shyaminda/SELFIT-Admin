package com.codelabs.selfit_admin.Views.subviews;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.VideoView;

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.helpers.CustomAlertDialog;
import com.codelabs.selfit_admin.helpers.CustomProgressDialog;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;
import com.codelabs.selfit_admin.models.ExercisesModel;
import com.codelabs.selfit_admin.models.MealsModel;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tomlonghurst.expandablehinttext.ExpandableHintText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditExerciseActivity extends AppCompatActivity {

    private Button btnFindVideo, btnUpload;
    private StorageReference storageRef;
    private VideoView videoView;
    private Uri videoUri;
    private MediaController mediaController;
    private FirebaseFirestore db;
    private CustomProgressDialog customProgressDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private ExpandableHintText txtName, txtCalories;
    private Spinner spnExercise;
    private ArrayAdapter<ExercisesModel> spinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        btnFindVideo = findViewById(R.id.btn_choose_edit_video);
        btnUpload = findViewById(R.id.btn_upload_edit_video);
        videoView = findViewById(R.id.video_edit_view);
        txtName = findViewById(R.id.txt_ex_edit_name);
        txtCalories = findViewById(R.id.txt_ex_edit_calories);
        spnExercise = findViewById(R.id.spn_exercise);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("exercise-videos");
        customProgressDialog = new CustomProgressDialog(EditExerciseActivity.this);
        sharedPreferencesManager = new SharedPreferencesManager(EditExerciseActivity.this);

        init();

        mediaController = new MediaController(EditExerciseActivity.this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        btnFindVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("video/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i,45);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.createProgress();
                if (txtName.getText().isEmpty() || txtCalories.getText().isEmpty() || videoUri == null){
                    customProgressDialog.dismissProgress();
                    new CustomAlertDialog().negativeDismissAlert(EditExerciseActivity.this, "Oops!", "Please fill out all the\nrequired inputs!", CFAlertDialog.CFAlertStyle.ALERT);
                }else{
                    final StorageReference ref = storageRef.child(System.currentTimeMillis() + "." + getExtensions(videoUri));
                    UploadTask uploadTask = ref.putFile(videoUri);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                db.collection("admins").document(sharedPreferencesManager.getPreferences(SharedPreferencesManager.TRAINER_ID))
                                        .collection("exercises").document(spinnerArrayAdapter.getItem(spnExercise.getSelectedItemPosition()).getExerciseID())
                                        .update(mapData(txtName.getText().toString(), txtCalories.getText().toString(), downloadUri.toString()))
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                customProgressDialog.dismissProgress();
                                                Log.e(TAG, "onSuccess: " + downloadUri.toString());
                                                clear();
                                                new CustomAlertDialog().positiveAlert(EditExerciseActivity.this, "Done!", "New exercise added!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                customProgressDialog.dismissProgress();
                                                new CustomAlertDialog().negativeAlert(EditExerciseActivity.this, "Done!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                                            }
                                        });

                            } else {
                                customProgressDialog.dismissProgress();
                                new CustomAlertDialog().negativeAlert(EditExerciseActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==45 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);

            videoView.start();
        }else{
            Log.e(TAG, "onActivityResult: error");
        }
    }

    private String getExtensions(Uri videoUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }

    private Map mapData(String name, String calories, String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("exName", name);
        map.put("exCalories", calories + " Cal");
        map.put("exVideoPath", url);

        return map;
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
                                    arraylist.add(new ExercisesModel(document.getId(), document.getString("exName")));
                                }
                            };
                            spinnerArrayAdapter = new ArrayAdapter<ExercisesModel>(EditExerciseActivity.this, android.R.layout.simple_spinner_dropdown_item, arraylist);
//                            spinnerArrayAdapter.clear();
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            customProgressDialog.dismissProgress();
                            spnExercise.setAdapter(spinnerArrayAdapter);
                        } else {
                            customProgressDialog.dismissProgress();
                            Log.d(TAG, task.getException().getMessage());
                            new CustomAlertDialog().negativeAlert(EditExerciseActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialog.dismissProgress();
                        new CustomAlertDialog().negativeAlert(EditExerciseActivity.this, "Oops!", "Something went wrong.\nPlease try again later!","OK", CFAlertDialog.CFAlertStyle.ALERT);
                        e.printStackTrace();
                    }
                });
    }

    private void clear(){
        videoView.setVideoURI(null);
        txtName.setText("");
        txtCalories.setText("");
        spnExercise.setSelection(0);
    }
}
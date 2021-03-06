package com.example.a711;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChooseADoctorAfterSignup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Map<String, Object> prescriptionMap = new HashMap<>();
    Map<String, Object> userMap = new HashMap<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> spinnerValueHoldValue = new ArrayList ();
    ArrayList<Doctor> listDoctor = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Spinner DoctorChoice;
    Doctor patientDoctor = null;
    final String TAG ="ChooseADoctorAfterSignup";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_a_doctor_after_signup);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerValueHoldValue);
        db.collection("doctors").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document:task.getResult()){
                                Doctor doctor = new Doctor(document.getId(), document.getData().values().toArray()[0].toString());
                                listDoctor.add(doctor);
                                spinnerValueHoldValue.add(doctor.getName());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        DoctorChoice =(Spinner) findViewById(R.id.ChooseADoctorAfterSignupSpinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DoctorChoice.setAdapter(adapter);
        DoctorChoice.setSelection(1);
        DoctorChoice.setOnItemSelectedListener(this);
        final Button done = findViewById(R.id.ChooseADoctorAfterSignupDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMap.put("Doctor", patientDoctor.getName());
                userMap.put("Doctor ID", patientDoctor.getId());
                userMap.put("Name", user.getDisplayName());
                userMap.put("Email", user.getEmail());
                userMap.put("Age", "0");
                db.collection("patient").document(user.getUid()).set(userMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + user.getUid());
                                db.collection("doctors").document(patientDoctor.getId())
                                        .collection("Doctor Patients")
                                        .document(user.getUid()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        prescriptionMap.put("Name", "");
                                        prescriptionMap.put("Begin","");
                                        prescriptionMap.put("End","");
                                        prescriptionMap.put("Doctor", patientDoctor.getName());
                                        db.collection("patient").document(user.getUid()).collection("Prescription").document("None").set(prescriptionMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        startActivity(new Intent(ChooseADoctorAfterSignup.this, PatientHomePage.class));

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Could not add prescription");
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Could not add patient to the doctor");

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Error adding document", e);
                    }
                });
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String nameDoctor= DoctorChoice.getSelectedItem().toString();

        for(int i=0;i<listDoctor.size();i++){
            if (listDoctor.get(i).getName().equals(nameDoctor)){
                patientDoctor = listDoctor.get(i);
                break;
            }
        }
        Toast.makeText(parent.getContext(), "Selected: " + nameDoctor, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
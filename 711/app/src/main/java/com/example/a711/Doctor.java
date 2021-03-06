package com.example.a711;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import static java.time.LocalTime.now;

public class Doctor {

    private String id;
    private String name;
    private ArrayList<Patient> patientList;

    public Doctor(String id, String name){
        this.setId(id);
        this.setName(name);
        this.patientList = new ArrayList<>();
    };

    public String getId(){
        return id;
    }
    public void setId(String x){
        this.id = x;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Patient> getPatientList() {
        return patientList;
    }

    public void addPatient(Patient patient){
        patientList.add(new Patient(patient.getId(),patient.getName(),patient.getAge(),patient.getEmail()));
    }
    public void removePatient(Patient patient){
        patientList.remove(patient);
    }
    public void renewPrescription(Patient patient, Prescription prescription, LocalDate date){
        LocalDate today = LocalDate.now();
        Prescription newPrescription = new Prescription(prescription.getDrug(),today.toString(),date.toString(),name);
        patient.getMedicalRecord().addPrescription(newPrescription);
    }
    public void newPrescription(Prescription prescription, Patient patient){
        patient.getMedicalRecord().addPrescription(prescription);
    }

}

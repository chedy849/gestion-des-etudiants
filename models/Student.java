package com.example.gestetudiant.models;

import com.example.gestetudiant.utils.DateUtils;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Student implements Serializable {
    @SerializedName("idEtudiant")
    private int id;

    @SerializedName("nomEtudiant")
    private String lastName;

    @SerializedName("prenomEtudiant")
    private String firstName;

    @SerializedName("dateNais")
    @JsonAdapter(DateUtils.GsonDateAdapter.class) // Add this
    private Date birthDate;

    // Constructeurs
    public Student() {}

    public Student(String lastName, String firstName, Date birthDate) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

}
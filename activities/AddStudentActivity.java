package com.example.gestetudiant.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestetudiant.R;
import com.example.gestetudiant.api.ApiClient;
import com.example.gestetudiant.api.ApiInterface;
import com.example.gestetudiant.models.Student;
import com.example.gestetudiant.utils.DateUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStudentActivity extends AppCompatActivity {
    private TextInputEditText etNom, etPrenom, etDateNais;
    private Button btnSave;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add these checks for debugging:
                Log.d("BACK_BUTTON", "Button clicked");


                finish();


            }
        });

        initViews();
        setupDatePicker();
        setupSaveButton();
    }

    private void initViews() {
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etDateNais = findViewById(R.id.etDateNais);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupDatePicker() {
        etDateNais.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddStudentActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                        selectedDate = selectedCalendar.getTime();
                        etDateNais.setText(DateUtils.formatDate(selectedDate));
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            String lastName = etNom.getText().toString().trim();
            String firstName = etPrenom.getText().toString().trim();
            String birthDate = etDateNais.getText().toString().trim();

            if (validateInputs(lastName, firstName, birthDate)) {
                Student student = new Student();
                student.setLastName(lastName);
                student.setFirstName(firstName);
                student.setBirthDate(selectedDate);

                saveStudent(student);
            }
        });
    }

    private boolean validateInputs(String lastName, String firstName, String birthDate) {
        boolean isValid = true;

        if (lastName.isEmpty()) {
            etNom.setError("Le nom est requis");
            isValid = false;
        }
        if (firstName.isEmpty()) {
            etPrenom.setError("Le prénom est requis");
            isValid = false;
        }
        if (birthDate.isEmpty()) {
            etDateNais.setError("La date de naissance est requise");
            isValid = false;
        }
        return isValid;
    }

    private void saveStudent(Student student) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Student>> callList = apiService.getAllStudents();

        callList.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Student> students = response.body();

                    int newId = students.size() + 1;
                    student.setId(newId);
                    newId = students.size();



                    Call<Student> callAdd = apiService.addStudent(student);
                    callAdd.enqueue(new Callback<Student>() {
                        @Override
                        public void onResponse(Call<Student> call, Response<Student> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AddStudentActivity.this, "Étudiant ajouté avec succès", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(AddStudentActivity.this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Student> call, Throwable t) {
                            Toast.makeText(AddStudentActivity.this, "Échec de la connexion: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(AddStudentActivity.this, "Erreur de chargement de la liste", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                Toast.makeText(AddStudentActivity.this, "Échec de la connexion: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
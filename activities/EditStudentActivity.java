package com.example.gestetudiant.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestetudiant.R;
import com.example.gestetudiant.api.ApiClient;
import com.example.gestetudiant.api.ApiInterface;
import com.example.gestetudiant.models.Student;
import com.example.gestetudiant.utils.DateUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditStudentActivity extends AppCompatActivity {
    private TextInputEditText etFirstName, etLastName, etBirthDate;
    private Button btnUpdate, btnDelete;
    private Student student;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add these checks for debugging:
                Log.d("BACK_BUTTON", "Button clicked"); // Check if click registers

                // Option 1: Simple back navigation
                finish();

                // Option 2: Explicit navigation with animation
                // startActivity(new Intent(EditStudentActivity.this, MainActivity.class));
                // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                // finish();
            }
        });

        student = (Student) getIntent().getSerializableExtra("student");
        if (student == null) {
            finish();
            return;
        }

        initViews();
        populateFields();
        setupDatePicker();
        setupButtons();


    }

    private void initViews() {
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etBirthDate = findViewById(R.id.et_birth_date);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
    }

    private void populateFields() {
        etFirstName.setText(student.getFirstName());
        etLastName.setText(student.getLastName());
        etBirthDate.setText(DateUtils.formatDate(student.getBirthDate()));
        selectedDate = student.getBirthDate();
    }

    private void setupDatePicker() {
        etBirthDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (selectedDate != null) {
                calendar.setTime(selectedDate);
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditStudentActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                        selectedDate = selectedCalendar.getTime();
                        etBirthDate.setText(DateUtils.formatDate(selectedDate));
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupButtons() {
        btnUpdate.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String birthDate = etBirthDate.getText().toString().trim();

            if (validateInputs(firstName, lastName, birthDate)) {
                student.setFirstName(firstName);
                student.setLastName(lastName);
                student.setBirthDate(selectedDate);
                updateStudent();
            }
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private boolean validateInputs(String firstName, String lastName, String birthDate) {
        boolean isValid = true;

        if (firstName.isEmpty()) {
            etFirstName.setError("Le prénom est requis");
            isValid = false;
        }
        if (lastName.isEmpty()) {
            etLastName.setError("Le nom est requis");
            isValid = false;
        }
        if (birthDate.isEmpty()) {
            etBirthDate.setError("La date de naissance est requise");
            isValid = false;
        }
        return isValid;
    }

    private void updateStudent() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Student> call = apiService.updateStudent(student.getId(), student);

        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditStudentActivity.this, "Mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Erreur inconnue";
                        Toast.makeText(EditStudentActivity.this, "Erreur: " + error, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(EditStudentActivity.this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Toast.makeText(EditStudentActivity.this, "Échec de la connexion: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation de suppression")
                .setMessage("Êtes-vous sûr de vouloir supprimer cet étudiant?")
                .setPositiveButton("Oui", (dialog, which) -> deleteStudent())
                .setNegativeButton("Non", null)
                .show();
    }

    private void deleteStudent() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Void> call = apiService.deleteStudent(student.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditStudentActivity.this, "Étudiant supprimé avec succès", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Erreur inconnue";
                        Toast.makeText(EditStudentActivity.this, "Erreur: " + error, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(EditStudentActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditStudentActivity.this, "Échec de la connexion: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
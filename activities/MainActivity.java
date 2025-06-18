package com.example.gestetudiant.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.graphics.Color;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;
import com.google.android.material.search.SearchBar;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Color;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gestetudiant.R;
import com.example.gestetudiant.adapters.StudentAdapter;
import com.example.gestetudiant.api.ApiClient;
import com.example.gestetudiant.api.ApiInterface;
import com.example.gestetudiant.models.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> studentList = new ArrayList<>();
    private List<Student> filteredList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAdd;
    private SearchBar searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupSearch();
        loadStudents();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
            startActivity(intent);
        });

        swipeRefreshLayout.setOnRefreshListener(this::loadStudents);

        FloatingActionButton fabRefresh = findViewById(R.id.fab_refresh);
        fabRefresh.setOnClickListener(v -> {
            fabRefresh.animate().rotationBy(360).setDuration(500).start();
            loadStudents();
        });
    }


    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        fabAdd = findViewById(R.id.fab_add);

    }

    private void setupSearch() {
        EditText searchEditText = findViewById(R.id.search_input_field); // Votre champ de recherche

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudentsByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void filterStudentsByName(String searchQuery) {
        List<Student> filteredList = new ArrayList<>();

        if (searchQuery.isEmpty()) {
            filteredList.addAll(studentList); // Afficher tous les étudiants si la recherche est vide
        } else {
            String lowerCaseQuery = searchQuery.toLowerCase(Locale.getDefault()).trim();

            for (Student student : studentList) {
                // Recherche seulement sur le nom de famille
                if (student.getLastName().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredList.add(student);
                }

                // Option: Pour inclure aussi le prénom dans la recherche:
            /* if (student.getLastName().toLowerCase().contains(lowerCaseQuery) ||
                  student.getFirstName().toLowerCase().contains(lowerCaseQuery)) {
                filteredList.add(student);
            } */
            }
        }

        adapter.updateData(filteredList);
    }



    private void setupRecyclerView() {
        adapter = new StudentAdapter(filteredList, student -> {
            Intent intent = new Intent(MainActivity.this, EditStudentActivity.class);
            intent.putExtra("student", student);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadStudents() {
        swipeRefreshLayout.setRefreshing(true);
        try {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<List<Student>> call = apiService.getAllStudents();

            call.enqueue(new Callback<List<Student>>() {
                @Override
                public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            studentList.clear();
                            studentList.addAll(response.body());
                            adapter.updateData(studentList);
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("APIError", "Response handling failed", e);
                    }
                }

                @Override
                public void onFailure(Call<List<Student>> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this,
                            "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            swipeRefreshLayout.setRefreshing(false);
            Log.e("LoadError", "Failed to load students", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }
}
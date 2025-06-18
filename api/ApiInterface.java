package com.example.gestetudiant.api;

import com.example.gestetudiant.models.Student;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("etudiant/liste")
    Call<List<Student>> getAllStudents();

    @POST("etudiant/liste")
    Call<Student> addStudent(@Body Student student);

    @PUT("etudiant/liste/{id}")
    Call<Student> updateStudent(@Path("id") int id, @Body Student student);

    @DELETE("etudiant/liste/{id}")
    Call<Void> deleteStudent(@Path("id") int id);
}
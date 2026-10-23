package com.example.data.remote

import com.example.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface SupabaseApiService {

    // Ping check (fetching OpenAPI specs or small table to verify connectivity)
    @GET("rest/v1/")
    suspend fun ping(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<ResponseBody>

    // Madrasah Software Key-Value Store (public.kv_store table)
    @GET("rest/v1/kv_store")
    suspend fun getKvItem(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("key") keyFilter: String,
        @Query("select") select: String = "*"
    ): List<KvItem>

    @GET("rest/v1/kv_store")
    suspend fun getAllKvItems(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): List<KvItem>

    @POST("rest/v1/kv_store")
    suspend fun upsertKvItem(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates,return=representation",
        @Body item: KvItem
    ): List<KvItem>

    // Students
    @GET("rest/v1/students")
    suspend fun getStudents(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): List<Student>

    @GET("rest/v1/students")
    suspend fun getStudentByRoll(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("roll_number") rollNumberFilter: String, // e.g. "eq.02"
        @Query("select") select: String = "*"
    ): List<Student>

    // Teachers
    @GET("rest/v1/teachers")
    suspend fun getTeachers(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): List<Teacher>

    @GET("rest/v1/teachers")
    suspend fun getTeacherByStaffId(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("staff_id") staffIdFilter: String,
        @Query("select") select: String = "*"
    ): List<Teacher>

    // Attendance
    @GET("rest/v1/attendance")
    suspend fun getAttendance(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("student_id") studentIdFilter: String,
        @Query("order") order: String = "date.desc",
        @Query("select") select: String = "*"
    ): List<AttendanceRecord>

    @POST("rest/v1/attendance")
    suspend fun markAttendance(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body record: AttendanceRecord
    ): List<AttendanceRecord>

    // Grades / Results
    @GET("rest/v1/grades")
    suspend fun getGrades(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("student_id") studentIdFilter: String,
        @Query("select") select: String = "*"
    ): List<GradeRecord>

    @POST("rest/v1/grades")
    suspend fun insertGrade(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body grade: GradeRecord
    ): List<GradeRecord>

    // Routine
    @GET("rest/v1/routines")
    suspend fun getRoutines(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "period_no.asc"
    ): List<RoutineItem>

    // Homework
    @GET("rest/v1/homework")
    suspend fun getHomework(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "due_date.desc"
    ): List<HomeworkItem>

    @POST("rest/v1/homework")
    suspend fun createHomework(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body homework: HomeworkItem
    ): List<HomeworkItem>

    // Notices
    @GET("rest/v1/notices")
    suspend fun getNotices(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "published_at.desc"
    ): List<NoticeItem>

    @POST("rest/v1/notices")
    suspend fun createNotice(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body notice: NoticeItem
    ): List<NoticeItem>

    // Fees & Payments
    @GET("rest/v1/fees_payments")
    suspend fun getFees(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("student_id") studentIdFilter: String,
        @Query("select") select: String = "*"
    ): List<FeesPayment>

    // Quizzes
    @GET("rest/v1/quizzes")
    suspend fun getQuizzes(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): List<QuizQuestion>
}

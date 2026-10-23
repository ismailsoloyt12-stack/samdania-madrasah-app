package com.example.data.remote

import android.content.Context
import com.example.data.model.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class SupabaseRepository(private val context: Context) {

    private var currentBaseUrl: String = ""
    private var apiService: SupabaseApiService? = null
    private var imgbbApiService: ImgbbApiService? = null

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val teacherAccountListAdapter: JsonAdapter<List<TeacherAccount>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, TeacherAccount::class.java))

    private val teacherStaffListAdapter: JsonAdapter<List<TeacherStaff>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, TeacherStaff::class.java))

    private val studentListAdapter: JsonAdapter<List<Student>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, Student::class.java))

    private val teacherListAdapter: JsonAdapter<List<Teacher>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, Teacher::class.java))

    private val noticeListAdapter: JsonAdapter<List<NoticeItem>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, NoticeItem::class.java))

    private val routineListAdapter: JsonAdapter<List<RoutineItem>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, RoutineItem::class.java))

    private val homeworkListAdapter: JsonAdapter<List<HomeworkItem>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, HomeworkItem::class.java))

    private val quizListAdapter: JsonAdapter<List<QuizQuestion>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, QuizQuestion::class.java))

    private val academicInfoAdapter: JsonAdapter<List<AcademicInfo>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, AcademicInfo::class.java))
        
    private val attendanceRecordListAdapter: JsonAdapter<List<AttendanceRecord>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, AttendanceRecord::class.java))
        
    private val gradeRecordListAdapter: JsonAdapter<List<GradeRecord>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, GradeRecord::class.java))

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private fun getService(): SupabaseApiService {
        val url = SupabaseConfig.getProjectUrl(context)
        if (apiService == null || currentBaseUrl != url) {
            currentBaseUrl = url
            apiService = Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(SupabaseApiService::class.java)
        }
        return apiService!!
    }

    private fun getImgbbService(): ImgbbApiService {
        if (imgbbApiService == null) {
            imgbbApiService = Retrofit.Builder()
                .baseUrl("https://api.imgbb.com/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(ImgbbApiService::class.java)
        }
        return imgbbApiService!!
    }

    private fun getApiKey(): String = SupabaseConfig.getAnonKey(context)
    private fun getAuthHeader(): String = "Bearer ${getApiKey()}"

    suspend fun pingBackend(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = getService().ping(getApiKey(), getAuthHeader())
            if (response.isSuccessful || response.code() in 200..404) {
                Result.success("Supabase API Online (${response.code()})")
            } else {
                Result.failure(Exception("Supabase HTTP Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun readFromKvStore(key: String): String? {
        return try {
            val items = getService().getKvItem(getApiKey(), getAuthHeader(), keyFilter = "eq.$key")
            val raw = items.firstOrNull()?.value
            if (raw == null) return null
            
            // The value might be a JSONB object/array or a JSON string.
            // If it's a string that looks like an array or object, return it.
            // If it's something else (Map/List from Moshi), convert back to JSON string for consistent parsing.
            if (raw is String) return raw
            
            val adapter = moshi.adapter(Any::class.java)
            adapter.toJson(raw)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun writeToKvStore(key: String, jsonValue: String): Boolean {
        return try {
            getService().upsertKvItem(
                apiKey = getApiKey(),
                authHeader = getAuthHeader(),
                item = KvItem(key = key, value = jsonValue)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    // ==========================================
    // 1. STUDENTS
    // ==========================================
    suspend fun fetchAllStudents(): List<Student> = withContext(Dispatchers.IO) {
        try {
            val json = readFromKvStore("samdani_students")
            if (!json.isNullOrBlank()) {
                return@withContext studentListAdapter.fromJson(json) ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }

    suspend fun saveStudents(students: List<Student>): Result<Boolean> = withContext(Dispatchers.IO) {
        val json = studentListAdapter.toJson(students)
        val ok = writeToKvStore("samdani_students", json)
        Result.success(ok)
    }

    // ==========================================
    // 2. TEACHERS & STAFF
    // ==========================================
    suspend fun fetchAllTeachers(): List<Teacher> = withContext(Dispatchers.IO) {
        val mergedTeachers = mutableListOf<Teacher>()
        try {
            val accountsJson = readFromKvStore("samdani_teacher_accounts")
            val staffJson = readFromKvStore("samdani_teachers_staff")

            val accounts = if (!accountsJson.isNullOrBlank()) {
                teacherAccountListAdapter.fromJson(accountsJson) ?: emptyList()
            } else emptyList()

            val staff = if (!staffJson.isNullOrBlank()) {
                teacherStaffListAdapter.fromJson(staffJson) ?: emptyList()
            } else emptyList()

            // Filter valid accounts
            val validAccounts = accounts.filter { it.registered && it.status == "verified" }

            // Merge logic
            val processedEmails = mutableSetOf<String>()
            val processedNames = mutableSetOf<String>()

            // First, add all valid accounts
            for (acc in validAccounts) {
                // Find matching staff for fallback fields
                val matchingStaff = staff.find { s ->
                    (acc.email != null && s.email != null && acc.email.equals(s.email, ignoreCase = true)) ||
                    (acc.fullName.equals(s.name, ignoreCase = true))
                }

                val merged = Teacher(
                    id = acc.id,
                    fullName = acc.fullName,
                    designation = acc.designation ?: matchingStaff?.designation ?: "",
                    email = acc.email ?: matchingStaff?.email ?: "",
                    contact = acc.contact ?: matchingStaff?.contact ?: "",
                    bio = acc.bio ?: matchingStaff?.bio ?: "",
                    photoUrl = acc.photoUrl ?: "",
                    role = acc.role ?: matchingStaff?.role ?: "Teacher",
                    registered = acc.registered,
                    status = acc.status ?: "",
                    createdAt = acc.createdAt ?: ""
                )
                mergedTeachers.add(merged)
                if (merged.email.isNotEmpty()) processedEmails.add(merged.email.lowercase())
                processedNames.add(merged.fullName.lowercase())
            }

            // Then, add staff that aren't in accounts
            for (s in staff) {
                val emailMatch = s.email != null && processedEmails.contains(s.email.lowercase())
                val nameMatch = processedNames.contains(s.name.lowercase())

                if (!emailMatch && !nameMatch) {
                    val teacher = Teacher(
                        id = s.id,
                        fullName = s.name,
                        designation = s.designation ?: "",
                        email = s.email ?: "",
                        contact = s.contact ?: "",
                        bio = s.bio ?: "",
                        role = s.role ?: "Teacher",
                        registered = false,
                        status = "staff_only"
                    )
                    mergedTeachers.add(teacher)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        mergedTeachers
    }

    suspend fun fetchTeacherByStaffId(staffId: String): Result<Teacher?> = withContext(Dispatchers.IO) {
        try {
            val all = fetchAllTeachers()
            val found = all.find {
                it.id.trim().equals(staffId.trim(), ignoreCase = true) ||
                it.contact.trim().equals(staffId.trim(), ignoreCase = true) ||
                it.email.trim().equals(staffId.trim(), ignoreCase = true)
            }
            Result.success(found)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // 3. ATTENDANCE
    // ==========================================
    suspend fun fetchAttendance(studentId: String, studentName: String = ""): List<AttendanceRecord> = withContext(Dispatchers.IO) {
        val uiList = mutableListOf<AttendanceRecord>()
        try {
            val json = readFromKvStore("samdani_student_attendance_records")
            if (!json.isNullOrBlank()) {
                val portalRecords = attendanceRecordListAdapter.fromJson(json) ?: emptyList()
                for (record in portalRecords) {
                    for (status in record.statuses) {
                        if (status.studentId == studentId || (studentName.isNotBlank() && status.studentName == studentName)) {
                            uiList.add(
                                AttendanceRecord(
                                    id = status.id.ifEmpty { "att-${status.recordedAt}" },
                                    teacherId = record.teacherId,
                                    teacherName = record.teacherName,
                                    className = status.className,
                                    date = status.date,
                                    createdAt = status.recordedAt,
                                    statuses = listOf(status)
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        uiList.sortByDescending { it.date }
        uiList
    }

    suspend fun markAttendance(record: AttendanceRecord): Result<Boolean> = withContext(Dispatchers.IO) {
        Result.success(false) 
    }

    // ==========================================
    // 4. GRADES / RESULTS
    // ==========================================
    suspend fun fetchGrades(studentId: String, studentName: String = ""): List<GradeRecord> = withContext(Dispatchers.IO) {
        try {
            val json = readFromKvStore("samdani_student_results")
            if (!json.isNullOrBlank()) {
                val all = gradeRecordListAdapter.fromJson(json) ?: emptyList()
                // Only display when marks are not empty and marks have obtained/max fields
                return@withContext all.filter { 
                    (it.studentId == studentId || (studentName.isNotBlank() && it.studentName == studentName)) &&
                    it.marks.isNotEmpty() &&
                    it.marks.all { m -> m.marksObtained >= 0 && m.maxMarks > 0 }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }

    // ==========================================
    // 5. ROUTINES
    // ==========================================
    suspend fun fetchRoutines(className: String): List<RoutineItem> = withContext(Dispatchers.IO) {
        try {
            val json = readFromKvStore("samdani_teacher_routines")
            if (!json.isNullOrBlank()) {
                val all = routineListAdapter.fromJson(json) ?: emptyList()
                // Filter by class 1-5 as per agent instructions
                val validClasses = listOf("Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "শ্রেণি ১", "শ্রেণি ২", "শ্রেণি ৩", "শ্রেণি ৪", "শ্রেণি ৫")
                return@withContext if (className.isNotBlank()) {
                    all.filter { it.className.equals(className, ignoreCase = true) }
                } else {
                    all.filter { it.className in validClasses }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }

    // ==========================================
    // 6. HOMEWORK
    // ==========================================
    suspend fun fetchHomework(className: String): List<HomeworkItem> = withContext(Dispatchers.IO) {
        try {
            val json = readFromKvStore("samdani_homework")
            if (!json.isNullOrBlank()) {
                val all = homeworkListAdapter.fromJson(json) ?: emptyList()
                return@withContext if (className.isNotBlank()) {
                    all.filter { it.className.equals(className, ignoreCase = true) }
                } else all
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }

    // ==========================================
    // 7. NOTICES
    // ==========================================
    suspend fun fetchNotices(): List<NoticeItem> = withContext(Dispatchers.IO) {
        try {
            val json = readFromKvStore("samdani_notifications")
            if (!json.isNullOrBlank()) {
                return@withContext noticeListAdapter.fromJson(json) ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }

    suspend fun createNotice(notice: NoticeItem): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val current = fetchNotices().toMutableList()
            current.add(0, notice)
            val json = noticeListAdapter.toJson(current)
            val ok = writeToKvStore("samdani_notifications", json)
            Result.success(ok)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotice(noticeId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val current = fetchNotices().toMutableList()
            val removed = current.removeIf { it.id == noticeId }
            if (removed) {
                val json = noticeListAdapter.toJson(current)
                val ok = writeToKvStore("samdani_notifications", json)
                Result.success(ok)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // 8. FEES
    // ==========================================
    suspend fun fetchFees(studentId: String, student: Student): List<FeesPayment> = withContext(Dispatchers.IO) {
        try {
            val json = readFromKvStore("samdani_fees")
            if (!json.isNullOrBlank()) {
                val adapter = moshi.adapter<List<FeesPayment>>(Types.newParameterizedType(List::class.java, FeesPayment::class.java))
                val all = adapter.fromJson(json) ?: emptyList()
                return@withContext all.filter { it.studentId == studentId }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }

    // ==========================================
    // 9. QUIZZES
    // ==========================================
    suspend fun fetchQuizzes(): List<QuizQuestion> = withContext(Dispatchers.IO) {
        try {
            val json = readFromKvStore("quizzes")
            if (!json.isNullOrBlank()) {
                return@withContext quizListAdapter.fromJson(json) ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }

    // ==========================================
    // 10. ACADEMIC INFO
    // ==========================================
    suspend fun fetchAcademicInfo(className: String): List<AcademicInfo> = withContext(Dispatchers.IO) {
        try {
            val json = readFromKvStore("samdani_academic_info")
            if (!json.isNullOrBlank()) {
                val all = academicInfoAdapter.fromJson(json) ?: emptyList()
                return@withContext if (className.isNotBlank()) {
                    all.filter { it.className.equals(className, ignoreCase = true) }
                } else all
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }

    suspend fun uploadImageToImgbb(imagePart: okhttp3.MultipartBody.Part): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = getImgbbService().uploadImage("b6e7f704bd9db049251beab63469cebb", imagePart)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data?.url ?: "")
            } else {
                Result.failure(Exception("ImgBB Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudentPhoto(studentId: String, photoUrl: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val students = fetchAllStudents().toMutableList()
            val index = students.indexOfFirst { it.id == studentId }
            if (index != -1) {
                val updatedStudent = students[index].copy(photoUrlAlt = photoUrl)
                students[index] = updatedStudent
                saveStudents(students)
                Result.success(true)
            } else {
                Result.failure(Exception("Student not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveRoutines(routines: List<RoutineItem>): Result<Boolean> = withContext(Dispatchers.IO) {
        val json = routineListAdapter.toJson(routines)
        val ok = writeToKvStore("samdani_teacher_routines", json)
        Result.success(ok)
    }

    suspend fun saveAcademicInfo(info: List<AcademicInfo>): Result<Boolean> = withContext(Dispatchers.IO) {
        val json = academicInfoAdapter.toJson(info)
        val ok = writeToKvStore("samdani_academic_info", json)
        Result.success(ok)
    }

    suspend fun saveHomework(homework: List<HomeworkItem>): Result<Boolean> = withContext(Dispatchers.IO) {
        val json = homeworkListAdapter.toJson(homework)
        val ok = writeToKvStore("samdani_homework", json)
        Result.success(ok)
    }

    suspend fun initializeDatabaseWithStarterData(): Result<Boolean> = withContext(Dispatchers.IO) {
        Result.success(true)
    }
}

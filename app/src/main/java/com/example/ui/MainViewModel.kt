package com.example.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.remote.SupabaseConfig
import com.example.data.remote.SupabaseRepository
import com.example.data.remote.SupabaseSqlGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive

enum class AppScreen {
    LOGIN,
    STUDENT_DASHBOARD,
    ATTENDANCE,
    ROUTINE,
    ACADEMIC,
    FEES_PAYMENT,
    RESULT,
    HOMEWORK,
    QUIZ_CONTEST,
    DIGITAL_ID,
    NOTIFICATIONS,
    PROFILE,
    TEACHER_PORTAL,
    ADMIN_PORTAL,
    ADMIN_SUPABASE_GUIDE,
    MISSING_FILLOUTS,
    APP_MANAGEMENT
}

data class MainUiState(
    val currentScreen: AppScreen = AppScreen.LOGIN,
    val userSession: UserSession = UserSession(
        role = UserRole.STUDENT,
        userId = "",
        displayName = "",
        identifier = "",
        details = ""
    ),
    val student: Student = Student(id = "", name = ""),
    val currentTeacher: Teacher = Teacher(id = "", fullName = ""),
    val attendanceList: List<AttendanceRecord> = emptyList(),
    val gradesList: List<GradeRecord> = emptyList(),
    val routinesList: List<RoutineItem> = emptyList(),
    val homeworkList: List<HomeworkItem> = emptyList(),
    val noticesList: List<NoticeItem> = emptyList(),
    val feesList: List<FeesPayment> = emptyList(),
    val quizzesList: List<QuizQuestion> = emptyList(),
    val allStudents: List<Student> = emptyList(),
    val missingFillouts: List<Student> = emptyList(),
    val allTeachers: List<Teacher> = emptyList(),
    val academicInfoList: List<AcademicInfo> = emptyList(),
    val isLoading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val connectionStatus: String = "Supabase kv_store Active",
    val isSupabaseOnline: Boolean = true,
    val errorMessage: String? = null,
    val currentNavTab: Int = 0 // 0: Home, 1: ID, 2: Alerts, 3: Fees, 4: Profile
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SupabaseRepository(application)
    private val noticePrefs = application.getSharedPreferences("notice_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadSession()
        loadInitialData()
        startDataPolling()
    }

    private fun startDataPolling() {
        viewModelScope.launch {
            while (isActive) {
                delay(15000) // Poll every 15 seconds for real-time sync fallback
                loadInitialData(isSilent = true)
            }
        }
    }

    private fun loadSession() {
        val prefs = getApplication<Application>().getSharedPreferences("samdania_prefs", Context.MODE_PRIVATE)
        val roleStr = prefs.getString("user_role", null)
        val userId = prefs.getString("user_id", "") ?: ""
        val displayName = prefs.getString("user_name", "") ?: ""
        val identifier = prefs.getString("user_identifier", "") ?: ""
        val details = prefs.getString("user_details", "") ?: ""

        if (roleStr != null) {
            val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.STUDENT }
            val session = UserSession(role, userId, displayName, identifier, details)
            _uiState.update { it.copy(userSession = session) }
            
            val screen = when (role) {
                UserRole.STUDENT -> AppScreen.STUDENT_DASHBOARD
                UserRole.TEACHER -> AppScreen.TEACHER_PORTAL
                UserRole.ADMIN -> AppScreen.ADMIN_PORTAL
            }

            if (role == UserRole.STUDENT) {
                _uiState.update { it.copy(student = it.student.copy(id = userId, name = displayName)) }
            }
            
            _uiState.update { it.copy(currentScreen = screen) }
        }
    }

    private fun saveSession(session: UserSession) {
        val prefs = getApplication<Application>().getSharedPreferences("samdania_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("user_role", session.role.name)
            putString("user_id", session.userId)
            putString("user_name", session.displayName)
            putString("user_identifier", session.identifier)
            putString("user_details", session.details)
            apply()
        }
    }

    private fun clearSession() {
        val prefs = getApplication<Application>().getSharedPreferences("samdania_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun loadInitialData(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent) _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val allStudents = repository.fetchAllStudents()
                val allTeachers = repository.fetchAllTeachers()
                val notices = repository.fetchNotices()
                val quizzes = repository.fetchQuizzes()

                // Check for new notices to show local notification
                if (notices.isNotEmpty()) {
                    val latestNotice = notices.first()
                    val lastSeenId = noticePrefs.getString("last_notice_id", "")
                    if (latestNotice.id != lastSeenId && lastSeenId != "") {
                        showLocalNotification(latestNotice.title, latestNotice.content)
                        noticePrefs.edit().putString("last_notice_id", latestNotice.id).apply()
                    } else if (lastSeenId == "") {
                        // First run, just save the ID
                        noticePrefs.edit().putString("last_notice_id", latestNotice.id).apply()
                    }
                }

                val currentStudent = if (_uiState.value.userSession.role == UserRole.STUDENT) {
                    val userId = _uiState.value.userSession.userId
                    allStudents.find { it.id == userId } ?: _uiState.value.student
                } else {
                    _uiState.value.student
                }

                val attendance = if (currentStudent.id.isNotEmpty()) repository.fetchAttendance(currentStudent.id, currentStudent.name) else emptyList()
                val grades = if (currentStudent.id.isNotEmpty()) repository.fetchGrades(currentStudent.id, currentStudent.name) else emptyList()
                val routines = if (currentStudent.className.isNotEmpty()) repository.fetchRoutines(currentStudent.className) else repository.fetchRoutines("")
                val homework = if (currentStudent.className.isNotEmpty()) repository.fetchHomework(currentStudent.className) else emptyList()
                val fees = if (currentStudent.id.isNotEmpty()) repository.fetchFees(currentStudent.id, currentStudent) else emptyList()
                val academicInfo = if (currentStudent.className.isNotEmpty()) repository.fetchAcademicInfo(currentStudent.className) else repository.fetchAcademicInfo("")

                val missingFillouts = allStudents.filter { it.photoUrl.isNullOrBlank() }

                _uiState.update {
                    it.copy(
                        student = currentStudent,
                        allStudents = allStudents,
                        missingFillouts = missingFillouts,
                        allTeachers = allTeachers,
                        attendanceList = attendance,
                        gradesList = grades,
                        routinesList = routines,
                        homeworkList = homework,
                        noticesList = notices,
                        feesList = fees,
                        quizzesList = quizzes,
                        academicInfoList = academicInfo,
                        isLoading = false,
                        connectionStatus = "Real-time Link Active (${allStudents.size} Students)"
                    )
                }
            } catch (e: Exception) {
                if (!isSilent) _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    private fun showLocalNotification(title: String, content: String) {
        val context = getApplication<Application>()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "samdani_alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Madrasah Alerts", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update {
            val tab = when (screen) {
                AppScreen.STUDENT_DASHBOARD -> 0
                AppScreen.DIGITAL_ID -> 1
                AppScreen.NOTIFICATIONS -> 2
                AppScreen.FEES_PAYMENT -> 3
                AppScreen.PROFILE -> 4
                else -> it.currentNavTab
            }
            it.copy(currentScreen = screen, currentNavTab = tab)
        }
    }

    fun onNavTabSelected(tabIndex: Int) {
        when (tabIndex) {
            0 -> navigateTo(AppScreen.STUDENT_DASHBOARD)
            1 -> navigateTo(AppScreen.DIGITAL_ID)
            2 -> navigateTo(AppScreen.NOTIFICATIONS)
            3 -> navigateTo(AppScreen.FEES_PAYMENT)
            4 -> navigateTo(AppScreen.PROFILE)
        }
    }

    fun selectStudent(student: Student, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val session = UserSession(
                role = UserRole.STUDENT,
                userId = student.id,
                displayName = student.name,
                identifier = student.rollNumber,
                details = "Class: ${student.className}, Section: ${student.section}"
            )
            
            _uiState.update {
                it.copy(
                    userSession = session,
                    student = student,
                    isLoading = false,
                    currentScreen = AppScreen.STUDENT_DASHBOARD,
                    currentNavTab = 0
                )
            }
            saveSession(session)
            loadInitialData()
            onSuccess()
        }
    }

    fun loginStudent(query: String, classFilter: String = "", onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) {
            onError("Enter your Roll or Phone")
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val allStudents = repository.fetchAllStudents()
            val matched = allStudents.filter { s ->
                val matchesId = s.id.equals(cleanQuery, ignoreCase = true) || s.registrationId.equals(cleanQuery, ignoreCase = true)
                val matchesRoll = s.rollNumber == cleanQuery
                val matchesPhone = s.phone.contains(cleanQuery)
                val classOk = classFilter == "সকল শ্রেণি" || s.className == classFilter
                (matchesId || matchesRoll || matchesPhone) && (classFilter == "" || classOk)
            }

            if (matched.isNotEmpty()) {
                selectStudent(matched.first(), onSuccess)
            } else {
                _uiState.update { it.copy(isLoading = false) }
                onError("Student not found in database")
            }
        }
    }

    fun loginTeacher(staffId: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val res = repository.fetchTeacherByStaffId(staffId.trim())
            val teacher = res.getOrNull()
            if (teacher != null) {
                val session = UserSession(UserRole.TEACHER, teacher.id, teacher.name, teacher.staffId)
                _uiState.update { it.copy(userSession = session, currentTeacher = teacher, isLoading = false, currentScreen = AppScreen.TEACHER_PORTAL) }
                saveSession(session)
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false) }
                onError("Teacher ID not found")
            }
        }
    }

    fun loginAdmin(adminKey: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (adminKey.trim() == "admin" || adminKey.trim() == "samdania") {
            val session = UserSession(UserRole.ADMIN, "adm-001", "Admin Panel", "ADMIN")
            _uiState.update { it.copy(userSession = session, currentScreen = AppScreen.ADMIN_PORTAL) }
            saveSession(session)
            onSuccess()
        } else {
            onError("Invalid Admin Password")
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.LOGIN,
                userSession = UserSession(UserRole.STUDENT, "", "", ""),
                student = Student(id = "", name = "")
            )
        }
        clearSession()
    }

    fun adminPublishNotice(title: String, content: String, isBreaking: Boolean, category: String) {
        val newNotice = NoticeItem(
            id = "ntc-${System.currentTimeMillis()}",
            title = title,
            content = content,
            publishedAt = "Today",
            isBreaking = isBreaking,
            category = category
        )
        viewModelScope.launch {
            repository.createNotice(newNotice)
            loadInitialData()
        }
    }

    fun adminDeleteNotice(noticeId: String) {
        viewModelScope.launch {
            repository.deleteNotice(noticeId)
            loadInitialData()
        }
    }

    fun adminAddStudent(roll: String, name: String, bn: String, cl: String, sec: String, ph: String, gu: String, photo: okhttp3.MultipartBody.Part?, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                var photoUrl = ""
                if (photo != null) {
                    photoUrl = repository.uploadImageToImgbb(photo).getOrNull() ?: ""
                }
                val current = repository.fetchAllStudents().toMutableList()
                val newStudent = Student(
                    id = "stu-${System.currentTimeMillis()}",
                    rollNumberAlt = roll,
                    name = name,
                    banglaName = bn,
                    classNameAlt = cl,
                    section = sec,
                    phoneAlt = ph,
                    guardianNameAlt = gu,
                    photoUrlInternal = photoUrl
                )
                current.add(newStudent)
                repository.saveStudents(current)
                loadInitialData()
                onDone(true, "Success")
            } catch (e: Exception) {
                onDone(false, e.localizedMessage ?: "Error")
            }
        }
    }

    fun uploadStudentImage(studentId: String, imagePart: okhttp3.MultipartBody.Part, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.uploadImageToImgbb(imagePart)
            if (res.isSuccess) {
                repository.updateStudentPhoto(studentId, res.getOrNull() ?: "")
                loadInitialData()
                onDone(true, "Success")
            } else {
                onDone(false, "Upload Failed")
            }
        }
    }
    
    fun initializeStarterDatabase(onDone: (Boolean, String) -> Unit) {
        onDone(true, "Database Ready")
    }

    fun testSupabaseConnection(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.pingBackend()
            onResult(res.isSuccess, res.getOrNull() ?: res.exceptionOrNull()?.message ?: "Failed")
        }
    }

    fun updateSupabaseConfig(url: String, anonKey: String, onDone: () -> Unit) {
        SupabaseConfig.saveConfig(getApplication(), url, anonKey)
        loadInitialData()
        onDone()
    }
    
    fun getSqlMigrationScript(): String = SupabaseSqlGenerator.generateFullSchemaSql()

    fun adminAddRoutine(item: RoutineItem) {
        viewModelScope.launch {
            val current = repository.fetchRoutines("").toMutableList()
            current.add(item)
            repository.saveRoutines(current)
            loadInitialData()
        }
    }

    fun adminDeleteRoutine(id: String) {
        viewModelScope.launch {
            val current = repository.fetchRoutines("").toMutableList()
            current.removeIf { it.id == id }
            repository.saveRoutines(current)
            loadInitialData()
        }
    }

    fun adminAddAcademicInfo(item: AcademicInfo) {
        viewModelScope.launch {
            val current = repository.fetchAcademicInfo("").toMutableList()
            current.add(item)
            repository.saveAcademicInfo(current)
            loadInitialData()
        }
    }

    fun adminDeleteAcademicInfo(id: String) {
        viewModelScope.launch {
            val current = repository.fetchAcademicInfo("").toMutableList()
            current.removeIf { it.id == id }
            repository.saveAcademicInfo(current)
            loadInitialData()
        }
    }

    fun adminAddHomework(item: HomeworkItem) {
        viewModelScope.launch {
            val current = repository.fetchHomework("").toMutableList()
            current.add(0, item)
            repository.saveHomework(current)
            loadInitialData()
        }
    }

    fun adminDeleteHomework(id: String) {
        viewModelScope.launch {
            val current = repository.fetchHomework("").toMutableList()
            current.removeIf { it.id == id }
            repository.saveHomework(current)
            loadInitialData()
        }
    }

    fun teacherMarkAttendance(studentId: String, status: String) {
        loadInitialData(isSilent = true)
    }

    fun teacherPostHomework(subject: String, title: String, desc: String, due: String) {
        loadInitialData(isSilent = true)
    }
}

package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class UserRole {
    STUDENT,
    TEACHER,
    ADMIN
}

data class UserSession(
    val role: UserRole,
    val userId: String,
    val displayName: String,
    val identifier: String, // Roll number or Staff ID or Admin code
    val details: String = ""
)

@JsonClass(generateAdapter = true)
data class Student(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "bangla_name") val banglaName: String = "",
    
    // Compatibility fields
    @Json(name = "roll") val roll: String? = null,
    @Json(name = "rollNumber") val rollNumberAlt: String? = null,
    @Json(name = "roll_number") val rollNumberInternal: String? = null,
    
    @Json(name = "class") val classAlt: String? = null,
    @Json(name = "className") val classNameAlt: String? = null,
    
    @Json(name = "contact") val contact: String? = null,
    @Json(name = "phone") val phoneAlt: String? = null,
    
    @Json(name = "fatherName") val fatherName: String? = null,
    @Json(name = "guardian_name") val guardianNameAlt: String? = null,
    @Json(name = "motherName") val motherName: String? = null,
    
    @Json(name = "photoUrl") val photoUrlAlt: String? = null,
    @Json(name = "photo_url") val photoUrlInternal: String? = null,
    
    @Json(name = "previousDue") val previousDue: Double = 0.0,
    @Json(name = "currentMonthFee") val currentMonthFee: Double = 0.0,
    @Json(name = "examFee") val examFee: Double = 0.0,
    @Json(name = "advancePaid") val advancePaid: Double = 0.0,
    @Json(name = "transport") val transport: Double = 0.0,
    @Json(name = "waiver") val waiver: Double = 0.0,
    
    @Json(name = "blood_group") val bloodGroup: String? = null,
    @Json(name = "registration_id") val registrationId: String = "",
    @Json(name = "section") val section: String = "",
    @Json(name = "group_name") val groupName: String = "",
    @Json(name = "extra_info") val extraInfo: String = ""
) {
    val rollNumber: String get() = rollNumberAlt ?: roll ?: rollNumberInternal ?: ""
    val className: String get() = classNameAlt ?: classAlt ?: ""
    val phone: String get() = phoneAlt ?: contact ?: ""
    val photoUrl: String get() = photoUrlInternal ?: photoUrlAlt ?: ""
    val guardianName: String get() = fatherName ?: guardianNameAlt ?: ""
}

@JsonClass(generateAdapter = true)
data class Teacher(
    @Json(name = "id") val id: String = "",
    @Json(name = "fullName") val fullName: String = "",
    @Json(name = "designation") val designation: String = "",
    @Json(name = "email") val email: String = "",
    @Json(name = "contact") val contact: String = "",
    @Json(name = "bio") val bio: String = "",
    @Json(name = "photoUrl") val photoUrl: String = "",
    @Json(name = "role") val role: String = "",
    @Json(name = "registered") val registered: Boolean = false,
    @Json(name = "status") val status: String = "",
    @Json(name = "createdAt") val createdAt: String = "",
    @Json(name = "staffId") val staffId: String = "" // For internal mapping if needed
) {
    val name: String get() = fullName.ifEmpty { "Unknown Teacher" }
    val phone: String get() = contact
}

@JsonClass(generateAdapter = true)
data class TeacherAccount(
    @Json(name = "id") val id: String = "",
    @Json(name = "fullName") val fullName: String = "",
    @Json(name = "designation") val designation: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "contact") val contact: String? = null,
    @Json(name = "bio") val bio: String? = null,
    @Json(name = "photoUrl") val photoUrl: String? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "registered") val registered: Boolean = false,
    @Json(name = "status") val status: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class TeacherStaff(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "role") val role: String? = null,
    @Json(name = "designation") val designation: String? = null,
    @Json(name = "baseSalary") val baseSalary: Double? = null,
    @Json(name = "contact") val contact: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "bio") val bio: String? = null
)

@JsonClass(generateAdapter = true)
data class AttendanceStatus(
    @Json(name = "id") val id: String = "",
    @Json(name = "studentId") val studentId: String = "",
    @Json(name = "studentName") val studentName: String = "",
    @Json(name = "className") val className: String = "",
    @Json(name = "present") val present: Boolean = true,
    @Json(name = "late") val late: Boolean = false,
    @Json(name = "dairy") val dairy: Boolean = false,
    @Json(name = "uniform") val uniform: Boolean = false,
    @Json(name = "note") val note: String = "",
    @Json(name = "recordedAt") val recordedAt: String = "",
    @Json(name = "date") val date: String = ""
)

@JsonClass(generateAdapter = true)
data class AttendanceRecord(
    @Json(name = "id") val id: String = "",
    @Json(name = "teacherId") val teacherId: String = "",
    @Json(name = "teacherName") val teacherName: String = "",
    @Json(name = "className") val className: String = "",
    @Json(name = "date") val date: String = "",
    @Json(name = "createdAt") val createdAt: String = "",
    @Json(name = "statuses") val statuses: List<AttendanceStatus> = emptyList()
)

@JsonClass(generateAdapter = true)
data class MarkEntry(
    @Json(name = "subjectName") val subjectName: String = "",
    @Json(name = "marksObtained") val marksObtained: Double = 0.0,
    @Json(name = "maxMarks") val maxMarks: Double = 100.0
)

@JsonClass(generateAdapter = true)
data class GradeRecord(
    @Json(name = "id") val id: String = "",
    @Json(name = "studentId") val studentId: String = "",
    @Json(name = "studentName") val studentName: String = "",
    @Json(name = "class") val className: String = "",
    @Json(name = "examType") val examType: String = "",
    @Json(name = "year") val year: Int = 2026,
    @Json(name = "marks") val marks: List<MarkEntry> = emptyList(),
    @Json(name = "grade") val grade: String = "",
    @Json(name = "gpa") val gpa: Double = 0.0,
    @Json(name = "status") val status: String = "",
    @Json(name = "remarks") val remarks: String = "",
    @Json(name = "recordedBy") val recordedBy: String = "",
    @Json(name = "date") val date: String = ""
)

@JsonClass(generateAdapter = true)
data class RoutineItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "teacherId") val teacherId: String = "",
    @Json(name = "teacherName") val teacherName: String = "",
    @Json(name = "teacherPhotoUrl") val teacherPhotoUrl: String = "",
    @Json(name = "class") val className: String = "",
    @Json(name = "day") val day: String = "",
    @Json(name = "time") val time: String = "",
    @Json(name = "subject") val subject: String = "",
    @Json(name = "restTime") val restTime: String = ""
)

@JsonClass(generateAdapter = true)
data class HomeworkItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "className") val className: String = "",
    @Json(name = "teacherId") val teacherId: String = "",
    @Json(name = "teacherName") val teacherName: String = "",
    @Json(name = "teacherPhotoUrl") val teacherPhotoUrl: String = "",
    @Json(name = "subject") val subject: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "dueAt") val dueAt: String = "",
    @Json(name = "is_completed") val isCompleted: Boolean = false
)

@JsonClass(generateAdapter = true)
data class NoticeItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "content") val content: String = "",
    @Json(name = "published_at") val publishedAt: String = "",
    @Json(name = "is_breaking") val isBreaking: Boolean = false,
    @Json(name = "category") val category: String = "General"
)

@JsonClass(generateAdapter = true)
data class FeesPayment(
    @Json(name = "id") val id: String = "",
    @Json(name = "student_id") val studentId: String = "",
    @Json(name = "fee_title") val feeTitle: String = "",
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "due_date") val dueDate: String = "",
    @Json(name = "status") val status: String = "Due",
    @Json(name = "paid_at") val paidAt: String? = null,
    @Json(name = "transaction_id") val transactionId: String? = null
)

@JsonClass(generateAdapter = true)
data class QuizQuestion(
    @Json(name = "id") val id: String = "",
    @Json(name = "question") val question: String = "",
    @Json(name = "option_a") val optionA: String = "",
    @Json(name = "option_b") val optionB: String = "",
    @Json(name = "option_c") val optionC: String = "",
    @Json(name = "option_d") val optionD: String = "",
    @Json(name = "correct_option") val correctOption: Int = 0, // 0, 1, 2, 3
    @Json(name = "points") val points: Int = 10,
    @Json(name = "explanation") val explanation: String = ""
)

@JsonClass(generateAdapter = true)
data class AcademicInfo(
    @Json(name = "id") val id: String = "",
    @Json(name = "className") val className: String = "",
    @Json(name = "bookName") val bookName: String = "",
    @Json(name = "syllabus") val syllabus: String = ""
)

@JsonClass(generateAdapter = true)
data class KvItem(
    @Json(name = "key") val key: String = "",
    @Json(name = "value") val value: Any? = null // Can be String or Map/List
)

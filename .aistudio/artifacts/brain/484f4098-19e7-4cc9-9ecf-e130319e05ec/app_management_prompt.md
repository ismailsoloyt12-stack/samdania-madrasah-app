# Software Agent Prompt: Samdania Madrasah Admin Portal Update

You are tasked with updating the **Samdania Madrasah Admin Portal (Web)** to integrate with the newly updated Android application. The Android app is 100% data-driven and relies on a Supabase `kv_store` table.

## PROJECT GOALS
1. **Real-time Data**: Implement management forms that write directly to the Supabase `kv_store` table.
2. **Zero Mock Data**: Ensure every field entered in the portal is correctly formatted for the Android app.
3. **App Management**: Add a dedicated "App Management" page to push notices and local notifications.

## DATABASE CONFIGURATION
- **Supabase URL**: `https://ursocwwzhkzdpjptpvkl.supabase.co`
- **Table**: `public.kv_store`
- **Columns**: `key` (TEXT PRIMARY KEY), `value` (JSONB or JSON String)

## SECTION 1: STUDENT MANAGEMENT & REGISTRATION
Update the Student Registration form:
- **ImgBB Integration**: When a student is registered, upload their photo to ImgBB using API Key: `b6e7f704bd9db049251beab63469cebb`.
- **Photo URL**: Save the resulting ImgBB URL in the `photo_url` field of the student object.
- **Key in kv_store**: `samdani_students` (an array of student objects).
- **Missing Fillouts**: Implement a view to list all students who do not have a `photo_url` and provide an upload button for them.

## SECTION 2: ATTENDANCE (REAL-TIME LOGS)
Implement a daily attendance taker:
- **Key in kv_store**: `samdani_student_attendance_records`
- **Structure**:
```json
{
  "id": "ATT-unique-id",
  "teacherId": "teacher-id",
  "teacherName": "Teacher name",
  "className": "Class 1",
  "date": "2026-10-23",
  "statuses": [
    {
      "studentId": "student-id",
      "studentName": "Student name",
      "present": true,
      "late": false,
      "dairy": false,
      "uniform": false,
      "note": ""
    }
  ]
}
```
- **Validation**: Ensure attendance for the same class and date can be edited/overwritten.

## SECTION 3: CLASS ROUTINES (CLASS 1 TO 5)
Add a "Routine Management" section:
- **Key in kv_store**: `samdani_teacher_routines`
- **Input**: Day (Saturday-Thursday), Time, Subject, Class (1-5), Teacher Name, and Teacher Photo URL.
- **Sort**: The Android app sorts these by `time`.

## SECTION 4: EXAM RESULTS
Add a "Result Entry" section:
- **Key in kv_store**: `samdani_student_results`
- **Logic**: Do NOT show "A+" or mock results in the app if data is missing.
- **Input**: Student, Exam Type (First Term, Second Term, Annual), Year, and an array of marks:
```json
"marks": [
  { "subjectName": "Mathematics", "marksObtained": 78, "maxMarks": 100 }
]
```

## SECTION 5: HOMEWORK & ACADEMIC INFO
- **Homework Key**: `samdani_homework`. Include `teacherPhotoUrl` so it shows in the app.
- **Academic Info Key**: `samdani_academic_info`. Fields: `className`, `bookName`, `syllabus`.

## SECTION 6: APP NOTIFICATIONS
On the "App Management" page:
- **Key in kv_store**: `samdani_notifications`
- **Trigger**: Every time a new notice is added to this array, the Android app (via ID comparison) will trigger a local push notification.
- **Structure**:
```json
{
  "id": "NOTICE-unique-timestamp",
  "title": "...",
  "content": "...",
  "published_at": "Oct 23, 2026",
  "is_breaking": true
}
```

## CRITICAL INSTRUCTIONS
1. **No Mock Data**: Ensure the initial state of these keys is an empty array `[]`, not sample data.
2. **Safe Parsing**: The Android app uses Moshi for parsing. Ensure JSON is valid and keys match exactly.
3. **Session**: The Android app now saves sessions. No further login changes needed in the app, but ensure the portal can manage IDs that match what students enter.

**Final Request**: Update the SQL schema if you add new tables, but prioritize using the `kv_store` table as it is the current bridge between the Web and Android apps.

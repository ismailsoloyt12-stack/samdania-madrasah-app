# Samdania Talimul Islam Madrasah App

A dedicated Android management and academic portal for **সামদানিয়া তালিমুল ইসলাম মাদ্রাসা, গলাচিপা** (Samdania Talimul Islam Madrasah, Galachipa). The application faithfully mirrors the provided institutional UI layout with a high-fidelity Material 3 design, dynamic Supabase backend connectivity, zero local mock data dependence, role-based portals (Student, Teacher, and Admin), live attendance logging, grade cards, routine schedules, homework, news ticker, and direct messaging.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The user confirmed the institutional identity and backend architecture:
> - **Madrasah Title**: সামদানিয়া তালিমুল ইসলাম মাদ্রাসা, গলাচিপা
> - **Address**: পীর বদরুল ইসলাম সামদানী কমপ্লেক্স, খানকায়ে ফুরফুরা শরীফ, ২ নং ওয়ার্ড, গলাচিপা, পটুয়াখালী
> - **Contact**: মোবাইল: 01792776433
> - **Backend Architecture**: Supabase REST/PostgREST client with live dynamic API fetching and zero local caching dependencies.
> - **Admin Tools**: Integrated Supabase configuration console with live connection tester and instant SQL schema migration generator for 1-click backend deployment.
> - **Authentication Roles**: Multi-role login supporting Students (Roll No & Password), Teachers (Staff ID & Password), and Administrators (Admin Key & Password).

---

### 1. Overview & Core Concept

- **What It Does**: Provides students, guardians, teachers, and madrasah administrators with an interconnected digital hub for tracking attendance, academic routines, fee receipts, examination marks, homework submissions, madrasah announcements, Islamic quiz competitions, and teacher-student messaging.
- **Target Audience**: Students and parents of Samdania Madrasah, teachers managing daily attendance and grades, and administrative staff managing records.
- **Key Value**: Seamless real-time synchronization with Supabase without fake mock data; allows the institution to manage all operations online through their existing software while students and teachers interact through a native, smooth Android interface matching EDTECO standards.

---

### 2. User Experience & Visual Design

#### Visual Identity & Theme (Matching Attachment)
- **Top Institution Header**:
  - Centered institutional emblem card with shadow and subtle background watermark.
  - Bold Navy Typography (`#1A237E` / `#0D1B2A`): **সামদানিয়া তালিমুল ইসলাম মাদ্রাসা, গলাচিপা**.
  - Sub-header: পীর বদরুল ইসলাম সামদানী কমপ্লেক্স, খানকায়ে ফুরফুরা শরীফ, ২ নং ওয়ার্ড, গলাচিপা, পটুয়াখালী | মোবাইল: 01792776433.
- **Student Profile Card**:
  - Elevated light container (`#F4F6F9`) with circular student photo avatar.
  - Student full name, Class Roll number, Student ID, Class, and Department/Group (e.g., *কিতাব / হিফজ / আলিম বিভাগ*).
- **Live News Marquee**:
  - Dark Blue pill badge labeled **NEWS**.
  - Auto-scrolling horizontal announcement ticker with latest madrasah updates and admission notices.
- **3x2 Grid Action Tiles (Faithful Visual Replication)**:
  1. **Attendance**: Gradient purple/pink circular badge (`#9C27B0` -> `#E91E63`).
  2. **Routine**: Gradient cyan/blue circular badge (`#00BCD4` -> `#2196F3`).
  3. **Academic**: Emerald green circular badge with academic cap (`#2E7D32`).
  4. **Fees Payment**: Amber/golden coin stack circular badge (`#FF9800`).
  5. **Result**: Sky blue / yellow marksheet circular badge (`#0288D1`).
  6. **Homework**: Warm orange calendar & notebook circular badge (`#F57C00`).
- **Feature Card**:
  - **Quiz Contest (Play Quiz & Win Prize)** card with quiz trophy icon and live contest launcher.
- **Distinctive Curved Bottom Navigation Bar**:
  - Deep Navy background (`#0D1B44`) with curved notch.
  - Floating raised circular Home button with white outline and elevation.
  - Dedicated icons: Home, Digital ID Card, Notifications, Fees/Cart, and Profile/Account.

#### Key User Flows
1. **Multi-Role Authentication**:
   - Tabbed sign-in selector: **Student** (Roll Number & Password), **Teacher** (Staff ID & Password), **Admin** (Admin Key & Password).
   - Instant validation against Supabase `users` and `students` tables.
2. **Student Flow**:
   - Dashboard matching attachment -> Tap any tile to open full dedicated screens:
     - **Attendance Screen**: Monthly calendar heatmap, percentage bar, daily present/absent/late status.
     - **Routine Screen**: Day-by-day timetable with periods, subjects, teachers, and timings.
     - **Academic Screen**: Syllabus outline, booklist, assigned Ustadhs/teachers, madrasah calendar.
     - **Fees Payment Screen**: Dues breakdown, paid history, digital receipt viewer, payment gateway options.
     - **Result Screen**: Term-wise marksheets, subject grades, GPA/Division calculation, printable scorecard.
     - **Homework Screen**: Active assignments with due dates, teacher remarks, and submission status.
     - **Quiz Contest**: Interactive Islamic and general knowledge quiz with timer and score leaderboard.
     - **Direct Messaging**: Chat thread between student/parent and designated class teachers.
     - **Digital ID Card**: Flippable student identification card with barcode/QR and validity date.
3. **Teacher Flow**:
   - Rapid Attendance marker (mark whole class present/absent with 1 tap).
   - Grade & Mark entry for assigned subjects.
   - Assignment / Homework posting with due date.
   - Direct messaging inbox to reply to student queries.
4. **Admin Flow & Supabase Management**:
   - **Supabase Integration Guide & Live Console**:
     - Live configuration fields for Supabase Project URL and Public Anon Key.
     - **One-Click SQL Schema Script**: Full PostgreSQL DDL script with ready-to-execute schema for `students`, `attendance`, `grades`, `routines`, `homework`, `notices`, `messages`, and `fees`. Includes button to copy complete SQL to clipboard.
     - Live Connection Ping test to verify connectivity with the user's Supabase instance.
   - Student & Staff management directory.
   - Notice board publishing tool (updates the live news ticker instantly).
   - System activity monitor.

---

### 3. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├───────────────────┬───────────────────┬────────────────────────────────┤
│   Student Portal  │   Teacher Portal  │          Admin Portal          │
│ - Dashboard Grid  │ - Attendance Taker│ - Live Supabase Console        │
│ - Attendance View │ - Mark Entry      │ - SQL Schema Generator (DDL)   │
│ - Results & Grades│ - Homework Post   │ - User & Notice Management     │
│ - Routine/Schedule│ - Direct Messaging│ - Live Sync Status & Logs      │
│ - Quiz & Messages │                   │                                │
└─────────┬─────────┴─────────┬─────────┴────────────────┬───────────────┘
          │                   │                          │
┌─────────▼───────────────────▼──────────────────────────▼───────────────┐
│                       ViewModels & State Flow                          │
│   (AuthViewModel, MadrasahViewModel, MessagingViewModel, AdminViewModel)│
└─────────────────────────────────┬──────────────────────────────────────┘
                                  │
┌─────────────────────────────────▼──────────────────────────────────────┐
│                  Supabase Repository & API Service                     │
│    (Retrofit / OkHttp PostgREST REST Client with JSON Deserialization)  │
│  - Dynamic remote fetch (zero stale mock caching)                      │
│  - Configurable Project URL & Anon Key with persistent secure storage  │
│  - Full SQL migration script generator for external database setup     │
└─────────────────────────────────┬──────────────────────────────────────┘
                                  │
┌─────────────────────────────────▼──────────────────────────────────────┐
│                    External Supabase PostgreSQL DB                     │
│  - students          - attendance        - grades/results              │
│  - routines          - homework          - fees_payments               │
│  - notices           - direct_messages   - quizzes                     │
└────────────────────────────────────────────────────────────────────────┘
```

#### Supabase Database Schema (Generated in Admin Panel)
- `students`: `(id, roll_number, name, class, section, group_name, guardian_phone, photo_url, password_hash)`
- `teachers`: `(id, staff_id, name, designation, phone, subject, password_hash)`
- `attendance`: `(id, student_id, date, status, remarks)`
- `grades`: `(id, student_id, term_name, subject, marks_obtained, max_marks, grade, gpa)`
- `routines`: `(id, class_name, day_of_week, period_no, start_time, end_time, subject, teacher_name)`
- `homework`: `(id, class_name, subject, title, description, due_date, teacher_name)`
- `notices`: `(id, title, content, published_at, is_breaking, category)`
- `fees_payments`: `(id, student_id, fee_title, amount, due_date, status, paid_at, transaction_id)`
- `direct_messages`: `(id, sender_id, sender_role, receiver_id, receiver_role, message_text, created_at)`
- `quizzes`: `(id, question, option_a, option_b, option_c, option_d, correct_option, points)`

---

### 4. Implementation Steps & Deliverables

1. **Project Identity & Dependencies**:
   - Set app name to **Samdania Madrasah** in `metadata.json` and `strings.xml`.
   - Update `applicationId` to `com.aistudio.samdania.madrasah`.
   - Ensure Retrofit, Moshi/Gson, OkHttp, and Compose Material 3 icons are configured.
2. **Supabase Data Layer & SQL Provisioner**:
   - Implement `SupabaseConfigManager` for dynamic endpoint and anon key storage.
   - Build `SupabaseApiService` using Retrofit to communicate with Supabase PostgREST endpoints (`/rest/v1/...`).
   - Create comprehensive SQL schema generator containing copyable DDL commands so the user can paste them directly into Supabase's SQL Editor.
3. **UI Implementation (Exact Match to Reference)**:
   - Institutional Header with Bengali and English typography.
   - Student Card with avatar, roll number, class, and group details.
   - Animated News Ticker marquee bar.
   - 6 Circular Action Tiles with exact color gradients and Material 3 vector icons.
   - Quiz Banner and Custom curved bottom navigation bar with raised Home button.
4. **Dedicated Screen Implementations**:
   - `LoginScreen` (Student Roll/Pass, Teacher ID/Pass, Admin Key/Pass).
   - `AttendanceScreen` (Calendar, monthly stats, daily timeline).
   - `RoutineScreen` (Interactive weekly schedule selector).
   - `AcademicScreen` (Syllabus, subject syllabus breakdown, Ustadh directory).
   - `FeesPaymentScreen` (Due dues, paid receipts, payment simulator).
   - `ResultScreen` (Term marksheets, subject grades, grade point calculation).
   - `HomeworkScreen` (Task list, status toggles, detail view).
   - `QuizScreen` (Live interactive quiz contest with score calculation).
   - `DirectMessagingScreen` (Live chat thread between students and madrasah teachers).
   - `DigitalIdCardScreen` (Front and back student ID card with barcode).
   - `AdminPanelScreen` & `AdminSupabaseGuideScreen` (Supabase configuration, connection test, user management, and instant SQL schema copy).
5. **Compilation & Polish**:
   - Ensure clean compilation with `compile_applet`.
   - Ensure full edge-to-edge support, window insets, responsive sizing, and high accessibility standards.

package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Student
import com.example.data.model.Teacher
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginStudent: (String, String, () -> Unit, (String) -> Unit) -> Unit,
    onSelectStudent: (Student) -> Unit,
    onLoginTeacher: (String, String, () -> Unit, (String) -> Unit) -> Unit,
    onLoginAdmin: (String, () -> Unit, (String) -> Unit) -> Unit,
    onRefreshServer: () -> Unit,
    allStudents: List<Student>,
    allTeachers: List<Teacher>,
    connectionStatus: String,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRoleIndex by remember { mutableIntStateOf(0) } // 0: Student, 1: Teacher, 2: Admin
    val roles = listOf("শিক্ষার্থী", "উস্তাদ/শিক্ষক", "অ্যাডমিন")

    // Student fields
    var studentQuery by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("সকল শ্রেণি") }
    val classOptions = listOf("সকল শ্রেণি", "শিশু", "প্রথম", "দ্বিতীয়", "তৃতীয়", "চতুর্থ", "পঞ্চম", "Hifz")
    var classDropdownExpanded by remember { mutableStateOf(false) }

    // Teacher fields
    var teacherIdentifier by remember { mutableStateOf("") }
    var teacherPassword by remember { mutableStateOf("") }

    // Admin fields
    var adminPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Dialog for multiple matching students
    var multipleMatchesDialogList by remember { mutableStateOf<List<Student>?>(null) }

    LaunchedEffect(selectedRoleIndex) {
        errorMessage = null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Official Madrasah Logo
        Surface(
            modifier = Modifier
                .size(92.dp)
                .shadow(6.dp, RoundedCornerShape(20.dp))
                .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(6.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.madrasah_logo),
                    contentDescription = "মাদ্রাসা লোগো",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "সামদানিয়া তা'লীমুল ইসলাম মাদ্রাসা",
            color = Color(0xFF0F172A),
            fontSize = 18.5.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Text(
            text = "গলাচিপা, পটুয়াখালী • স্থাপিত: ২০০৬ ইং",
            color = Color(0xFF334155),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Animated Header with Counter and Message
        InvitationHeader(studentCount = allStudents.size)

        Spacer(modifier = Modifier.height(16.dp))

        // Role Selector Tabs with high contrast
        TabRow(
            selectedTabIndex = selectedRoleIndex,
            containerColor = Color(0xFFE2E8F0),
            contentColor = Color(0xFF0F172A),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
        ) {
            roles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedRoleIndex == index,
                    onClick = { selectedRoleIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedRoleIndex == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedRoleIndex == index) Color(0xFF0D1B44) else Color(0xFF475569)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header of card
                Text(
                    text = when (selectedRoleIndex) {
                        0 -> "শিক্ষার্থী লগইন (সফটওয়্যার ডেটাবেস)"
                        1 -> "উস্তাদ / শিক্ষক লগইন (Staff ID)"
                        else -> "প্রশাসন লগইন (Admin Management)"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )

                // STUDENT VIEW
                if (selectedRoleIndex == 0) {
                    ExposedDropdownMenuBox(
                        expanded = classDropdownExpanded,
                        onExpandedChange = { classDropdownExpanded = !classDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedClass,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("শ্রেণি নির্বাচন (Class Filter)", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded) },
                            leadingIcon = { Icon(Icons.Default.Class, contentDescription = null, tint = Color(0xFF0D1B44)) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF0D1B44),
                                unfocusedBorderColor = Color(0xFF94A3B8)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = classDropdownExpanded,
                            onDismissRequest = { classDropdownExpanded = false }
                        ) {
                            classOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt, fontWeight = if (opt == selectedClass) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedClass = opt
                                        classDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = studentQuery,
                        onValueChange = { studentQuery = it },
                        label = { Text("ক্লাস রোল, আইডি বা মোবাইল নম্বর", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF0D1B44)) },
                        modifier = Modifier.fillMaxWidth().testTag("login_identifier_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = Color(0xFF0D1B44),
                            focusedBorderColor = Color(0xFF0D1B44),
                            unfocusedBorderColor = Color(0xFF94A3B8)
                        ),
                        singleLine = true
                    )
                }

                // TEACHER VIEW
                if (selectedRoleIndex == 1) {
                    OutlinedTextField(
                        value = teacherIdentifier,
                        onValueChange = { teacherIdentifier = it },
                        label = { Text("স্টাফ আইডি বা ইমেইল", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0D1B44)) },
                        modifier = Modifier.fillMaxWidth().testTag("teacher_id_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedBorderColor = Color(0xFF0D1B44),
                            unfocusedBorderColor = Color(0xFF94A3B8)
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = teacherPassword,
                        onValueChange = { teacherPassword = it },
                        label = { Text("পাসওয়ার্ড", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF0D1B44)) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedBorderColor = Color(0xFF0D1B44),
                            unfocusedBorderColor = Color(0xFF94A3B8)
                        ),
                        singleLine = true
                    )
                }

                // ADMIN VIEW
                if (selectedRoleIndex == 2) {
                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it },
                        label = { Text("অ্যাডমিন গোপন পিন/পাসওয়ার্ড", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF0D1B44)) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedBorderColor = Color(0xFF0D1B44),
                            unfocusedBorderColor = Color(0xFF94A3B8)
                        ),
                        singleLine = true
                    )
                }

                errorMessage?.let { error ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEE2E2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFF991B1B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        when (selectedRoleIndex) {
                            0 -> onLoginStudent(studentQuery, selectedClass, { isLoading = false; onSuccess() }, { isLoading = false; errorMessage = it })
                            1 -> onLoginTeacher(teacherIdentifier, teacherPassword, { isLoading = false; onSuccess() }, { isLoading = false; errorMessage = it })
                            2 -> onLoginAdmin(adminPassword, { isLoading = false; onSuccess() }, { isLoading = false; errorMessage = it })
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("login_submit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D1B44)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = when (selectedRoleIndex) {
                                0 -> "শিক্ষার্থী লগইন (Sign In)"
                                1 -> "শিক্ষক পোর্টাল প্রবেশ (Sign In)"
                                else -> "প্রশাসন ড্যাশবোর্ড প্রবেশ"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog if multiple students match the entered query
    multipleMatchesDialogList?.let { matches ->
        AlertDialog(
            onDismissRequest = { multipleMatchesDialogList = null },
            title = { Text("আপনার শিক্ষার্থী প্রোফাইল নির্বাচন করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("একই রোল নম্বরে একাধিক শ্রেণিতে শিক্ষার্থী পাওয়া গেছে। আপনার শ্রেণি ও নামের উপর ট্যাপ করুন:", fontSize = 12.sp, color = Color(0xFF475569))
                    matches.forEach { student ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier.fillMaxWidth().clickable {
                                multipleMatchesDialogList = null
                                onSelectStudent(student)
                            }
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = student.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                    Text(text = "শ্রেণি: ${student.className} • রোল: ${student.rollNumber} • আইডি: ${student.id}", fontSize = 11.5.sp, color = Color(0xFF1E3A8A))
                                }
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { multipleMatchesDialogList = null }) { Text("বাতিল করুন", color = Color(0xFF64748B)) }
            }
        )
    }
}

@Composable
fun InvitationHeader(studentCount: Int) {
    var count by remember { mutableIntStateOf(0) }
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "student_count"
    )

    LaunchedEffect(studentCount) {
        count = studentCount
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$animatedCount", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E3A8A))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "জন শিক্ষার্থী", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
        }
        Text(text = "আমাদের দ্বীনি পরিবারে আপনাকে স্বাগতম", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A), modifier = Modifier.padding(top = 4.dp))
        Text(text = "আপনার সন্তানকে দ্বীনি জ্ঞানে আলোকিত করতে আমাদের মাদ্রাসায় আমন্ত্রিত", fontSize = 12.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)
    }
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherPortalScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    onMarkAttendance: (String, String) -> Unit,
    onPostHomework: (String, String, String, String) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Attendance, 1: Post Homework
    val tabs = listOf("হাজিরা গ্রহণ", "হোমওয়ার্ক দিন")

    var hwSubject by remember { mutableStateOf("হাদিস শরীফ") }
    var hwTitle by remember { mutableStateOf("") }
    var hwDesc by remember { mutableStateOf("") }
    var hwDue by remember { mutableStateOf("আগামীকাল") }
    var showHwSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("শিক্ষক পোর্টাল (Teacher Panel)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${uiState.currentTeacher.name} • ${uiState.currentTeacher.designation}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("teacher_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MadrasahNavy,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = MadrasahNavy
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal, fontSize = 12.5.sp)
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Mark attendance section
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                "শ্রেণি: ELEVEN-DAY-A এর উপস্থিতি তালিকা",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = MadrasahNavy
                            )
                        }

                        items(uiState.allStudents) { student ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(student.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("রোল: ${student.rollNumber} | শাখা: ${student.section}", fontSize = 11.5.sp, color = TextSecondary)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = { onMarkAttendance(student.id, "Present") },
                                            colors = ButtonDefaults.buttonColors(containerColor = AcademicGreen),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("উপস্থিত", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { onMarkAttendance(student.id, "Absent") },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("অনুপস্থিত", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Post homework section
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "নতুন হোমওয়ার্ক তৈরি করুন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MadrasahNavy
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = hwSubject,
                                onValueChange = { hwSubject = it },
                                label = { Text("বিষয় (Subject)", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = Color(0xFF0D1B44),
                                    focusedBorderColor = Color(0xFF0D1B44),
                                    unfocusedBorderColor = Color(0xFF94A3B8)
                                )
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = hwTitle,
                                onValueChange = { hwTitle = it },
                                label = { Text("পাঠের শিরোনাম (Title)", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = Color(0xFF0D1B44),
                                    focusedBorderColor = Color(0xFF0D1B44),
                                    unfocusedBorderColor = Color(0xFF94A3B8)
                                )
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = hwDesc,
                                onValueChange = { hwDesc = it },
                                label = { Text("বিস্তারিত নির্দেশনা (Instructions)", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = Color(0xFF0D1B44),
                                    focusedBorderColor = Color(0xFF0D1B44),
                                    unfocusedBorderColor = Color(0xFF94A3B8)
                                )
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = hwDue,
                                onValueChange = { hwDue = it },
                                label = { Text("জমার তারিখ (Due Date)", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = Color(0xFF0D1B44),
                                    focusedBorderColor = Color(0xFF0D1B44),
                                    unfocusedBorderColor = Color(0xFF94A3B8)
                                )
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    if (hwTitle.isNotBlank()) {
                                        onPostHomework(hwSubject, hwTitle, hwDesc, hwDue)
                                        hwTitle = ""
                                        hwDesc = ""
                                        showHwSuccess = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("হোমওয়ার্ক প্রকাশ করুন", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showHwSuccess) {
        AlertDialog(
            onDismissRequest = { showHwSuccess = false },
            confirmButton = {
                TextButton(onClick = { showHwSuccess = false }) { Text("ঠিক আছে") }
            },
            title = { Text("সফল", fontWeight = FontWeight.Bold) },
            text = { Text("হোমওয়ার্ক সফলভাবে Supabase ডেটাবেসে পোস্ট হয়েছে এবং ছাত্ররা তাদের ড্যাশবোর্ডে দেখতে পাবে।") }
        )
    }
}

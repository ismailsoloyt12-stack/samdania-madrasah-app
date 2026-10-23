package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Chat
import com.example.data.model.Teacher
import com.example.ui.MainUiState
import com.example.ui.components.EmptyStateCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("কিতাব ও সিলেবাস", "উস্তাদবৃন্দ", "রুটিন", "ক্যালেন্ডার")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("একাডেমিক তথ্য (Academic)", fontWeight = FontWeight.Bold, fontSize = 16.5.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("academic_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D1B44),
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
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFF0D1B44)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (selectedTabIndex == index) Color(0xFF0D1B44) else Color(0xFF475569)
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> SyllabusSection(uiState)
                1 -> TeachersDirectorySection(uiState.allTeachers)
                2 -> AcademicRoutineSection(uiState)
                3 -> AcademicCalendarSection()
            }
        }
    }
}

@Composable
private fun AcademicRoutineSection(uiState: MainUiState) {
    val routines = uiState.routinesList
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (routines.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.Schedule,
                    title = "রুটিন পাওয়া যায়নি",
                    description = "অ্যাডমিন প্যানেল থেকে রুটিন নির্ধারিত হলে এখানে দেখা যাবে।"
                )
            }
        } else {
            items(routines) { routine ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(routine.className, fontWeight = FontWeight.Bold, color = Color(0xFF0D1B44), fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                Text(routine.day, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(routine.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("উস্তাদ: ${routine.teacherName} | সময়: ${routine.time}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun SyllabusSection(uiState: MainUiState) {
    val academicInfo = uiState.academicInfoList

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "পাঠ্যসূচি ও কিতাব — শ্রেণি: ${uiState.student.className}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = Color(0xFF0D1B44),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        if (academicInfo.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.Book,
                    title = "কোনো পাঠ্যসূচি পাওয়া যায়নি",
                    description = "মাদ্রাসা অ্যাডমিন প্যানেল থেকে পাঠ্যসূচি যুক্ত করা হলে এখানে দেখা যাবে।"
                )
            }
        } else {
            items(academicInfo) { info ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFDCFCE7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Book, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(22.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(info.bookName, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(info.syllabus, fontSize = 12.5.sp, color = Color(0xFF334155), lineHeight = 17.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeachersDirectorySection(teachers: List<Teacher>) {
    val context = LocalContext.current
    
    val verifiedTeachers = teachers.filter { it.status == "verified" || it.registered }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (verifiedTeachers.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.Person,
                    title = "কোনো ভেরিফাইড শিক্ষক পাওয়া যায়নি",
                    description = "মাদ্রাসার সকল নিবন্ধিত ও ভেরিফাইড শিক্ষকদের তালিকা এখানে দেখা যাবে।"
                )
            }
        } else {
            items(verifiedTeachers) { teacher ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Teacher Photo
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (teacher.photoUrl.isNotBlank()) {
                                    coil.compose.AsyncImage(
                                        model = teacher.photoUrl,
                                        contentDescription = "Teacher Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(teacher.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF0F172A))
                                Text(teacher.designation, fontSize = 13.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                                
                                if (teacher.status == "verified") {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFDCFCE7),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF166534),
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "VERIFIED",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF166534)
                                            )
                                        }
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (teacher.contact.isNotBlank()) {
                                    // WhatsApp Button
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF25D366),
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clickable {
                                                // Convert to digits only
                                                var digits = teacher.contact.filter { it.isDigit() }
                                                
                                                // BD formatting: 01... -> 8801...
                                                if (digits.startsWith("0")) {
                                                    digits = "88$digits"
                                                } else if (digits.startsWith("1")) {
                                                    digits = "880$digits"
                                                } else if (!digits.startsWith("88")) {
                                                    digits = "88$digits"
                                                }
                                                
                                                val url = "https://wa.me/$digits"
                                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                    data = android.net.Uri.parse(url)
                                                }
                                                context.startActivity(intent)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Chat,
                                                contentDescription = "WhatsApp",
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    // Call Button
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clickable {
                                                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                                    data = android.net.Uri.parse("tel:${teacher.contact}")
                                                }
                                                context.startActivity(intent)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Phone, contentDescription = "ফোন", tint = Color(0xFF0D1B44), modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Additional Details: Phone & Email
                        if (teacher.contact.isNotBlank()) {
                            DetailRow(label = "মোবাইল:", value = teacher.contact, icon = Icons.Default.Phone)
                        }
                        if (teacher.email.isNotBlank()) {
                            DetailRow(label = "ইমেইল:", value = teacher.email, icon = Icons.Default.Email)
                        }

                        if (teacher.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "পরিচয়:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D1B44)
                            )
                            Text(
                                text = teacher.bio,
                                fontSize = 13.sp,
                                color = Color(0xFF334155),
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, fontSize = 12.5.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AcademicCalendarSection() {
    val events = emptyList<Triple<String, String, String>>()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (events.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.Schedule,
                    title = "একাডেমিক ক্যালেন্ডার ফাঁকা",
                    description = "ছুটির তালিকা ও বার্ষিক ক্যালেন্ডার মাদ্রাসা থেকে প্রকাশ করা হলে এখানে দেখা যাবে।"
                )
            }
        } else {
            items(events) { (date, title, venue) ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(date, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFFB45309))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color(0xFF0F172A))
                        Text(venue, fontSize = 12.sp, color = Color(0xFF334155))
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoticeItem
import com.example.ui.MainUiState
import com.example.ui.theme.MadrasahGold
import com.example.ui.theme.MadrasahNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppManagementScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    onPublishNotice: (String, String, Boolean, String) -> Unit,
    onDeleteNotice: (String) -> Unit,
    onNavigateToMissingFillouts: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("নোটিশ", "রুটিন", "একাডেমিক", "হোমওয়ার্ক")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("অ্যাপ ম্যানেজমেন্ট", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MadrasahNavy)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontSize = 12.sp) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> NoticesTab(uiState, onPublishNotice, onDeleteNotice, onNavigateToMissingFillouts)
                1 -> RoutinesTab(uiState)
                2 -> AcademicTab(uiState)
                3 -> HomeworkTab(uiState)
            }
        }
    }
}

@Composable
private fun NoticesTab(
    uiState: MainUiState,
    onPublishNotice: (String, String, Boolean, String) -> Unit,
    onDeleteNotice: (String) -> Unit,
    onNavigateToMissingFillouts: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("সাধারণ") }
    var isBreaking by remember { mutableStateOf(false) }
    var showPublishDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "প্রশাসনিক কার্যক্রম",
                        style = MaterialTheme.typography.titleMedium,
                        color = MadrasahNavy,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onNavigateToMissingFillouts,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy)
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("অসম্পূর্ণ প্রোফাইল (${uiState.missingFillouts.size})")
                    }
                }
            }

            Text(
                "বর্তমান নোটিশসমূহ",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.noticesList) { notice ->
                    NoticeManagementCard(
                        notice = notice,
                        onDelete = { onDeleteNotice(notice.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showPublishDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MadrasahGold,
            contentColor = MadrasahNavy
        ) {
            Icon(Icons.Default.Add, contentDescription = "নতুন নোটিশ")
        }
    }

    if (showPublishDialog) {
        AlertDialog(
            onDismissRequest = { showPublishDialog = false },
            title = { Text("নতুন নোটিশ প্রকাশ করুন") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("শিরোনাম") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("বিস্তারিত") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("ক্যাটাগরি (উদা: পরীক্ষা, ভর্তি)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(checked = isBreaking, onCheckedChange = { isBreaking = it })
                        Text("জরুরী (Breaking News)")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onPublishNotice(title, content, isBreaking, category)
                            showPublishDialog = false
                            title = ""
                            content = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy)
                ) {
                    Text("প্রকাশ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPublishDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
private fun RoutinesTab(uiState: MainUiState) {
    val viewModel: com.example.ui.MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    var showAddDialog by remember { mutableStateOf(false) }
    
    var className by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var selectedTeacher by remember { mutableStateOf<com.example.data.model.Teacher?>(null) }
    var day by remember { mutableStateOf("Saturday") }
    var time by remember { mutableStateOf("") }
    var teacherDropdownExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.routinesList) { routine ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${routine.className} - ${routine.subject}", fontWeight = FontWeight.Bold)
                            Text("Day: ${routine.day} | Time: ${routine.time}", fontSize = 12.sp)
                            Text("Teacher: ${routine.teacherName}", fontSize = 12.sp)
                        }
                        IconButton(onClick = { viewModel.adminDeleteRoutine(routine.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MadrasahGold
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("নতুন রুটিন যোগ করুন") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = className, onValueChange = { className = it }, label = { Text("শ্রেণি (উদা: Class 1)") })
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("বিষয়") })
                    
                    // Teacher Picker
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedTeacher?.fullName ?: "শিক্ষক নির্বাচন করুন",
                            onValueChange = {},
                            label = { Text("শিক্ষক") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().clickable { teacherDropdownExpanded = true },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                        )
                        DropdownMenu(
                            expanded = teacherDropdownExpanded,
                            onDismissRequest = { teacherDropdownExpanded = false }
                        ) {
                            uiState.allTeachers.forEach { teacher ->
                                DropdownMenuItem(
                                    text = { Text(teacher.fullName) },
                                    onClick = {
                                        selectedTeacher = teacher
                                        teacherDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    OutlinedTextField(value = day, onValueChange = { day = it }, label = { Text("দিন (Saturday, Sunday...)") })
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("সময় (উদা: 09:00 AM)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val item = com.example.data.model.RoutineItem(
                        id = "rtn-${System.currentTimeMillis()}",
                        className = className,
                        subject = subject,
                        teacherId = selectedTeacher?.id ?: "",
                        teacherName = selectedTeacher?.fullName ?: "মাদ্রাসা শিক্ষক",
                        teacherPhotoUrl = selectedTeacher?.photoUrl ?: "",
                        day = day,
                        time = time
                    )
                    viewModel.adminAddRoutine(item)
                    showAddDialog = false
                }) { Text("যোগ করুন") }
            }
        )
    }
}

@Composable
private fun AcademicTab(uiState: MainUiState) {
    val viewModel: com.example.ui.MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    var showAddDialog by remember { mutableStateOf(false) }
    
    var className by remember { mutableStateOf("") }
    var bookName by remember { mutableStateOf("") }
    var syllabus by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.academicInfoList) { info ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${info.className} - ${info.bookName}", fontWeight = FontWeight.Bold)
                            Text(info.syllabus, fontSize = 12.sp)
                        }
                        IconButton(onClick = { viewModel.adminDeleteAcademicInfo(info.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MadrasahGold
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("একাডেমিক তথ্য যোগ করুন") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = className, onValueChange = { className = it }, label = { Text("শ্রেণি") })
                    OutlinedTextField(value = bookName, onValueChange = { bookName = it }, label = { Text("বইয়ের নাম") })
                    OutlinedTextField(value = syllabus, onValueChange = { syllabus = it }, label = { Text("সিলেবাস") }, minLines = 3)
                }
            },
            confirmButton = {
                Button(onClick = {
                    val item = com.example.data.model.AcademicInfo(
                        id = "aca-${System.currentTimeMillis()}",
                        className = className,
                        bookName = bookName,
                        syllabus = syllabus
                    )
                    viewModel.adminAddAcademicInfo(item)
                    showAddDialog = false
                }) { Text("যোগ করুন") }
            }
        )
    }
}

@Composable
private fun HomeworkTab(uiState: MainUiState) {
    val viewModel: com.example.ui.MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    var showAddDialog by remember { mutableStateOf(false) }
    
    var className by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var selectedTeacher by remember { mutableStateOf<com.example.data.model.Teacher?>(null) }
    var description by remember { mutableStateOf("") }
    var dueAt by remember { mutableStateOf("") }
    var teacherDropdownExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.homeworkList) { hw ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${hw.className} - ${hw.subject}", fontWeight = FontWeight.Bold)
                            Text("Teacher: ${hw.teacherName}", fontSize = 12.sp)
                            Text("Due: ${hw.dueAt}", fontSize = 12.sp)
                        }
                        IconButton(onClick = { viewModel.adminDeleteHomework(hw.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MadrasahGold
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("নতুন হোমওয়ার্ক") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = className, onValueChange = { className = it }, label = { Text("শ্রেণি") })
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("বিষয়") })
                    
                    // Teacher Picker
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedTeacher?.fullName ?: "শিক্ষক নির্বাচন করুন",
                            onValueChange = {},
                            label = { Text("শিক্ষক") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().clickable { teacherDropdownExpanded = true },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                        )
                        DropdownMenu(
                            expanded = teacherDropdownExpanded,
                            onDismissRequest = { teacherDropdownExpanded = false }
                        ) {
                            uiState.allTeachers.forEach { teacher ->
                                DropdownMenuItem(
                                    text = { Text(teacher.fullName) },
                                    onClick = {
                                        selectedTeacher = teacher
                                        teacherDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    OutlinedTextField(value = dueAt, onValueChange = { dueAt = it }, label = { Text("জমার তারিখ (উদা: ২৫ অক্টোবর)") })
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("বিবরণ") }, minLines = 3)
                }
            },
            confirmButton = {
                Button(onClick = {
                    val item = com.example.data.model.HomeworkItem(
                        id = "hw-${System.currentTimeMillis()}",
                        className = className,
                        subject = subject,
                        teacherId = selectedTeacher?.id ?: "",
                        teacherName = selectedTeacher?.fullName ?: "মাদ্রাসা শিক্ষক",
                        teacherPhotoUrl = selectedTeacher?.photoUrl ?: "",
                        description = description,
                        dueAt = dueAt
                    )
                    viewModel.adminAddHomework(item)
                    showAddDialog = false
                }) { Text("যোগ করুন") }
            }
        )
    }
}


@Composable
fun NoticeManagementCard(notice: NoticeItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (notice.isBreaking) {
                        Surface(
                            color = Color.Red,
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                "জরুরী",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        notice.category,
                        color = MadrasahGold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    notice.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MadrasahNavy
                )
                Text(
                    notice.publishedAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}

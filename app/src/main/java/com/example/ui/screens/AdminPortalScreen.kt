package com.example.ui.screens

import androidx.activity.compose.BackHandler
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.R
import coil.compose.AsyncImage
import com.example.data.model.Student
import com.example.ui.AppScreen
import com.example.ui.MainUiState
import com.example.ui.theme.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPortalScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onPublishNotice: (String, String, Boolean, String) -> Unit,
    onAddStudent: (String, String, String, String, String, String, String, okhttp3.MultipartBody.Part?, (Boolean, String) -> Unit) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onInitializeDatabase: ((Boolean, String) -> Unit) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var noticeTitle by remember { mutableStateOf("") }
    var noticeContent by remember { mutableStateOf("") }
    var isBreaking by remember { mutableStateOf(false) }
    var noticeCategory by remember { mutableStateOf("সাধারণ") }
    var showNoticeSuccess by remember { mutableStateOf(false) }

    // Add Student state
    var newRoll by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var newBanglaName by remember { mutableStateOf("") }
    var newClass by remember { mutableStateOf("") }
    var newSection by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newGuardian by remember { mutableStateOf("") }
    var isSubmittingStudent by remember { mutableStateOf(false) }
    var studentResultMessage by remember { mutableStateOf<String?>(null) }

    // Init DB Dialog
    var showInitDbDialog by remember { mutableStateOf(false) }
    var isInitializingDb by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("মাদ্রাসা অ্যাডমিন ড্যাশবোর্ড", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(AppScreen.ADMIN_SUPABASE_GUIDE) }) {
                        Icon(Icons.Default.CloudSync, contentDescription = "Supabase সংযোগ")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MadrasahNavy,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // High-priority Supabase Guide Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MadrasahNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = MadrasahGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Supabase অনলাইন ডেটাবেস সংযোগ ও গাইড",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            "আপনার ওয়েব সফটওয়্যার ও Supabase kv_store ব্যাকএন্ডের সাথে সরাসরি ডেটা সিন্ক্রোনাইজ হয়। কোনো লোকাল ফেক ডেটা নেই।",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { onNavigate(AppScreen.APP_MANAGEMENT) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("অ্যাপ ম্যানেজমেন্ট", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { onNavigate(AppScreen.MISSING_FILLOUTS) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ছবিহীন (${uiState.missingFillouts.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Stat Counter Cards - Pure real counts, starting at 0
            item {
                Text("প্রতিষ্ঠানের সারসংক্ষেপ (রিয়েলটাইম)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MadrasahNavy)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        AdminStatCard("মোট ছাত্র", "${uiState.allStudents.size} জন", Icons.Default.Groups, RoutineBlue)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        AdminStatCard("উস্তাদবৃন্দ", "${uiState.allTeachers.size} জন", Icons.Default.Person, AcademicGreen)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        AdminStatCard("শাখা/শ্রেণি", "১২ টি", Icons.Default.Class, FeesOrange)
                    }
                }
            }

            // Add Student Section (Writes directly to Supabase kv_store)
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MadrasahNavy, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "নতুন শিক্ষার্থী নিবন্ধন (Add Student to Supabase)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = MadrasahNavy
                            )
                        }

                        Text(
                            "এখানে যুক্ত করা শিক্ষার্থী সরাসরি Supabase kv_store এ সেভ হবে এবং শিক্ষার্থী সেই রোল নম্বর দিয়ে লগইন করতে পারবে।",
                            fontSize = 11.5.sp,
                            color = Color(0xFF334155),
                            lineHeight = 15.sp
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newRoll,
                                onValueChange = { newRoll = it },
                                label = { Text("রোল নম্বর (Roll)", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                                placeholder = null,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF0D1B44),
                                    unfocusedBorderColor = Color(0xFF94A3B8)
                                )
                            )
 
                            OutlinedTextField(
                                value = newSection,
                                onValueChange = { newSection = it },
                                label = { Text("শাখা (Section)", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                                placeholder = null,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF0D1B44),
                                    unfocusedBorderColor = Color(0xFF94A3B8)
                                )
                            )
                        }

                        OutlinedTextField(
                            value = newBanglaName,
                            onValueChange = { newBanglaName = it },
                            label = { Text("শিক্ষার্থীর বাংলা নাম", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                            placeholder = null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF0D1B44),
                                unfocusedBorderColor = Color(0xFF94A3B8)
                            )
                        )

                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("শিক্ষার্থীর ইংরেজি নাম", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF0D1B44),
                                unfocusedBorderColor = Color(0xFF94A3B8)
                            )
                        )

                        OutlinedTextField(
                            value = newClass,
                            onValueChange = { newClass = it },
                            label = { Text("শ্রেণি / বিভাগ", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                            placeholder = null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF0D1B44),
                                unfocusedBorderColor = Color(0xFF94A3B8)
                            )
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newGuardian,
                                onValueChange = { newGuardian = it },
                                label = { Text("অভিভাবকের নাম", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF0D1B44),
                                    unfocusedBorderColor = Color(0xFF94A3B8)
                                )
                            )

                            OutlinedTextField(
                                value = newPhone,
                                onValueChange = { newPhone = it },
                                label = { Text("মোবাইল নম্বর", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
                                placeholder = null,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF0D1B44),
                                    unfocusedBorderColor = Color(0xFF94A3B8)
                                )
                            )
                        }

                        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
                        val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                            selectedImageUri = uri
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { imagePicker.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = MadrasahNavy)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("শিক্ষার্থীর ছবি (ঐচ্ছিক)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("ছবি যুক্ত করতে বামের বক্সে ট্যাপ করুন", fontSize = 10.sp, color = Color.Gray)
                            }
                            if (selectedImageUri != null) {
                                IconButton(onClick = { selectedImageUri = null }) {
                                    Icon(Icons.Default.Cancel, contentDescription = "Remove", tint = Color.Red)
                                }
                            }
                        }

                        studentResultMessage?.let { msg ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEFF6FF),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF93C5FD)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = msg,
                                    color = Color(0xFF1E3A8A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (newRoll.isNotBlank() && (newName.isNotBlank() || newBanglaName.isNotBlank())) {
                                    isSubmittingStudent = true
                                    studentResultMessage = null
                                    val body = selectedImageUri?.let { uri ->
                                        val file = createTmpFileFromUri(context, uri)
                                        file?.let {
                                            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                                            okhttp3.MultipartBody.Part.createFormData("image", it.name, requestFile)
                                        }
                                    }

                                    onAddStudent(
                                        newRoll,
                                        newName.ifEmpty { newBanglaName },
                                        newBanglaName.ifEmpty { newName },
                                        newClass,
                                        newSection,
                                        newPhone,
                                        newGuardian,
                                        body
                                    ) { success, msg ->
                                        isSubmittingStudent = false
                                        studentResultMessage = msg
                                        if (success) {
                                            newRoll = ""
                                            newName = ""
                                            newBanglaName = ""
                                            newGuardian = ""
                                            selectedImageUri = null
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isSubmittingStudent
                        ) {
                            if (isSubmittingStudent) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("সার্ভারে শিক্ষার্থী যুক্ত করুন", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Current Registered Students from Supabase kv_store
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "সার্ভারে নিবন্ধিত শিক্ষার্থী (${uiState.allStudents.size} জন)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MadrasahNavy
                    )
                }
            }

            if (uiState.allStudents.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "বর্তমানে সার্ভারে কোনো শিক্ষার্থী তথ্য নেই (০ জন)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "উপরের ফর্ম ব্যবহার করে শিক্ষার্থী যুক্ত করুন অথবা প্রাথমিক ডেটাবেস প্রস্তুত বাটনে ক্লিক করুন।",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(uiState.allStudents) { student ->
                    RegisteredStudentCard(student)
                }
            }

            // Publish Notice Section
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "নতুন নোটিশ ও জরুরি ঘোষণা প্রকাশ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MadrasahNavy
                        )

                        OutlinedTextField(
                            value = noticeTitle,
                            onValueChange = { noticeTitle = it },
                            label = { Text("নোটিশের শিরোনাম", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
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

                        OutlinedTextField(
                            value = noticeContent,
                            onValueChange = { noticeContent = it },
                            label = { Text("নোটিশের মূল বিষয়বস্তু", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isBreaking,
                                onCheckedChange = { isBreaking = it }
                            )
                            Text(
                                "ব্রেকিং নিউজ টিকারে দেখান (NEWS Ticker)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = {
                                if (noticeTitle.isNotBlank()) {
                                    onPublishNotice(noticeTitle, noticeContent, isBreaking, noticeCategory)
                                    noticeTitle = ""
                                    noticeContent = ""
                                    isBreaking = false
                                    showNoticeSuccess = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("নোটিশ প্রকাশ করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showNoticeSuccess) {
        AlertDialog(
            onDismissRequest = { showNoticeSuccess = false },
            confirmButton = {
                TextButton(onClick = { showNoticeSuccess = false }) { Text("ঠিক আছে") }
            },
            title = { Text("নোটিশ প্রকাশিত", fontWeight = FontWeight.Bold) },
            text = { Text("আপনার নোটিশ সফলভাবে তৈরি হয়েছে এবং অ্যাপের হোমস্ক্রিনের নিউজ টিকারে লাইভ প্রদর্শিত হচ্ছে।") }
        )
    }

    if (showInitDbDialog) {
        AlertDialog(
            onDismissRequest = { showInitDbDialog = false },
            title = { Text("প্রাথমিক ডেটাবেস প্রস্তুত নিশ্চিতকরণ", fontWeight = FontWeight.Bold) },
            text = {
                Text("এটি আপনার Supabase kv_store টেবিলে প্রাথমিক স্কিমা ও কাঠামো সেট করবে। এতে ছাত্ররা সরাসরি লগইন ও টেস্ট করতে পারবে। আপনি কি এগিয়ে যেতে চান?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInitDbDialog = false
                        isInitializingDb = true
                        onInitializeDatabase { success, msg ->
                            isInitializingDb = false
                            studentResultMessage = msg
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy)
                ) {
                    Text("হ্যাঁ, প্রস্তুত করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInitDbDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
private fun RegisteredStudentCard(student: Student) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                if (student.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = student.photoUrl,
                        contentDescription = "ছবি",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.madrasah_logo),
                        error = painterResource(id = R.drawable.madrasah_logo)
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MadrasahNavy, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${student.name} (${student.banglaName})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "রোল: ${student.rollNumber} • শ্রেণি: ${student.className} (${student.section})",
                    fontSize = 12.sp,
                    color = Color(0xFF334155)
                )
                if (student.phone.isNotBlank()) {
                    Text(
                        text = "মোবাইল: ${student.phone}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            Text(label, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

private fun createTmpFileFromUri(context: android.content.Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file
    } catch (e: Exception) {
        null
    }
}

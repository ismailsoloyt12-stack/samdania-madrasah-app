package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.AppScreen
import com.example.ui.MainUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val student = uiState.student

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("শিক্ষার্থী প্রোফাইল", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout, modifier = Modifier.testTag("logout_button")) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "লগআউট")
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
            // Profile Header
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0))
                                .border(2.dp, MadrasahNavy, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (student.photoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = student.photoUrl,
                                    contentDescription = "ছবি",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "প্রোফাইল ছবি",
                                    tint = MadrasahNavy,
                                    modifier = Modifier.size(46.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(student.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MadrasahNavy)
                        Text(student.banglaName, fontSize = 13.5.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MadrasahNavy.copy(alpha = 0.08f)
                        ) {
                            Text(
                                text = "রোল: ${student.rollNumber} | শ্রেণি: ${student.className}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MadrasahNavy,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Academic & Guardian Details
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("ব্যক্তিগত ও প্রাতিষ্ঠানিক তথ্য", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MadrasahNavy)
                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        ProfileInfoRow("শিক্ষার্থীর নাম", student.name)
                        ProfileInfoRow("বাংলা নাম", student.banglaName)
                        ProfileInfoRow("শ্রেণি ও শাখা", "${student.className} - ${student.section}")
                        ProfileInfoRow("বিভাগ / গ্রুপ", student.groupName)
                        ProfileInfoRow("ক্লাস রোল", student.rollNumber)
                        ProfileInfoRow("রেজিস্ট্রেশন আইডি", student.registrationId)
                        ProfileInfoRow("অভিভাবকের নাম", student.guardianName)
                        ProfileInfoRow("যোগাযোগ নম্বর", student.phone)
                    }
                }
            }

            // Quick Portal Navigation
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("অন্যান্য পোর্টাল ও সেটিংস", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MadrasahNavy)

                        OutlinedButton(
                            onClick = { onNavigate(AppScreen.TEACHER_PORTAL) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("শিক্ষক পোর্টাল (Teacher Portal)")
                        }

                        Button(
                            onClick = { onNavigate(AppScreen.ADMIN_PORTAL) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("অ্যাডমিন প্যানেল ও Supabase গাইড")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

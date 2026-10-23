package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.ui.MainUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val records = uiState.attendanceList
    val total = records.size
    
    // Calculate stats based on the flattened statuses
    var presentCount = 0
    var absentCount = 0
    var lateCount = 0
    
    records.forEach { record ->
        record.statuses.firstOrNull()?.let { status ->
            when {
                status.late -> lateCount++
                status.present -> presentCount++
                else -> absentCount++
            }
        }
    }
    
    val percentage = if (total > 0) ((presentCount.toDouble() / total) * 100).toInt() else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("উপস্থিতি রেকর্ড (Attendance)", fontWeight = FontWeight.Bold, fontSize = 16.5.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("attendance_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "রিফ্রেশ")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stat Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("মোট উপস্থিতি হার", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                Text(
                                    "$percentage%",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (percentage >= 80) Color(0xFF15803D) else Color(0xFFC2410C)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7))
                                    .border(2.dp, Color(0xFF166534), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$presentCount/$total", fontWeight = FontWeight.Bold, color = Color(0xFF166534), fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatPill("উপস্থিত", "$presentCount", Color(0xFFDCFCE7), Color(0xFF166534), Color(0xFF86EFAC))
                            StatPill("অনুপস্থিত", "$absentCount", Color(0xFFFEE2E2), Color(0xFF991B1B), Color(0xFFFCA5A5))
                            StatPill("দেরি", "$lateCount", Color(0xFFFEF3C7), Color(0xFF92400E), Color(0xFFFCD34D))
                        }
                    }
                }
            }

            item {
                Text(
                    text = "দৈনিক হাজিরা লগ",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasahNavy,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            if (records.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.Info,
                        title = "কোনো হাজিরার রেকর্ড নেই",
                        description = "মাদ্রাসা সফটওয়্যার থেকে হাজিরা আপডেট করা হলে এখানে লাইভ দেখতে পাবেন।"
                    )
                }
            } else {
                items(records) { record ->
                    record.statuses.firstOrNull()?.let { status ->
                        AttendanceDetailCard(record, status)
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceDetailCard(record: AttendanceRecord, status: AttendanceStatus) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "তারিখ: ${status.date}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "শিক্ষক: ${record.teacherName}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                val (statusText, statusBg, statusFg, statusBorder) = when {
                    status.late -> Quadruple("দেরি", Color(0xFFFEF3C7), Color(0xFF92400E), Color(0xFFFCD34D))
                    status.present -> Quadruple("উপস্থিত", Color(0xFFDCFCE7), Color(0xFF166534), Color(0xFF86EFAC))
                    else -> Quadruple("অনুপস্থিত", Color(0xFFFEE2E2), Color(0xFF991B1B), Color(0xFFFCA5A5))
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg,
                    border = BorderStroke(1.dp, statusBorder)
                ) {
                    Text(
                        text = statusText,
                        color = statusFg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            if (status.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "মন্তব্য: ${status.note}",
                    fontSize = 12.sp,
                    color = Color(0xFF334155),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CheckStatusItem("ডায়েরি", status.dairy)
                CheckStatusItem("ইউনিফর্ম", status.uniform)
            }
        }
    }
}

@Composable
private fun CheckStatusItem(label: String, checked: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (checked) Color(0xFF16A34A) else Color(0xFFDC2626),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
    }
}

@Composable
private fun StatPill(label: String, value: String, bgColor: Color, fgColor: Color, borderColor: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = fgColor)
            Text(label, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
        }
    }
}

@Composable
fun EmptyStateCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF334155))
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 11.5.sp, color = Color(0xFF64748B), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

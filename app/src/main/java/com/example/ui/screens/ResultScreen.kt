package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GradeRecord
import com.example.ui.MainUiState
import com.example.ui.components.EmptyStateCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val grades = uiState.gradesList
    val totalMarks = grades.sumOf { it.marks.sumOf { m -> m.marksObtained } }
    val totalMax = grades.sumOf { it.marks.sumOf { m -> m.maxMarks } }
    val averageGpa = if (grades.isNotEmpty()) grades.map { it.gpa }.average() else 0.0
    val formattedGpa = String.format("%.2f", averageGpa)
    
    val summaryGrade = when {
        averageGpa >= 5.0 -> "A+"
        averageGpa >= 4.0 -> "A"
        averageGpa >= 3.5 -> "A-"
        averageGpa >= 3.0 -> "B"
        averageGpa >= 2.0 -> "C"
        averageGpa >= 1.0 -> "D"
        else -> "F"
    }
    
    val summaryGradeColor = when {
        averageGpa >= 4.0 -> Color(0xFF81C784)
        averageGpa >= 3.0 -> Color(0xFFFFB74D)
        else -> Color(0xFFE57373)
    }

    var showPrintDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("পরীক্ষার ফলাফল (Result)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("result_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(onClick = { showPrintDialog = true }, enabled = grades.isNotEmpty()) {
                        Icon(Icons.Default.Print, contentDescription = "প্রিন্ট")
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
            // Result Summary Card
            if (grades.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MadrasahNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("১ম সাময়িক পরীক্ষা ২০২৬", color = MadrasahGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("শ্রেণি: ${uiState.student.className} | রোল: ${uiState.student.rollNumber}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(MadrasahGold.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MadrasahGold, modifier = Modifier.size(30.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("জিপিএ (GPA)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    Text(formattedGpa, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("গ্রেড (Grade)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    Text(summaryGrade, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = summaryGradeColor)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("মোট নম্বর", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    Text("${totalMarks.toInt()} / ${totalMax.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "বিষয়ভিত্তিক নম্বরপত্র (Marksheet)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasahNavy,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (grades.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.EmojiEvents,
                        title = "কোনো ফলাফল এখনো প্রকাশ করা হয়নি",
                        description = "পরীক্ষার ফলাফল মাদ্রাসার সার্ভারে আপলোড হওয়ার সাথে সাথে মার্কশিট লাইভ দেখা যাবে।"
                    )
                }
            } else {
                items(grades) { examResult ->
                    ExamResultSection(examResult)
                }
            }
        }
    }

    if (showPrintDialog) {
        AlertDialog(
            onDismissRequest = { showPrintDialog = false },
            confirmButton = {
                Button(
                    onClick = { showPrintDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy)
                ) {
                    Text("পিডিএফ ডাউনলোড")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrintDialog = false }) {
                    Text("বন্ধ করুন")
                }
            },
            title = { Text("নম্বরপত্র প্রিন্ট ও সংরক্ষণ", fontWeight = FontWeight.Bold) },
            text = {
                Text("সামদানিয়া তালিমুল ইসলাম মাদ্রাসা, গলাচিপা কর্তৃক অনুমোদিত অফিশিয়াল মার্কশিট পিডিএফ প্রস্তুত হয়েছে। আপনি এটি ডাউনলোড বা শেয়ার করতে পারবেন।")
            }
        )
    }
}

@Composable
private fun ExamResultSection(examResult: GradeRecord) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            color = MadrasahGold.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MadrasahGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${examResult.examType} - ${examResult.year}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MadrasahNavy
                )
                Text(
                    text = "গড় গ্রেড: ${examResult.grade} (GPA: ${examResult.gpa})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF15803D)
                )
            }
        }
        
        examResult.marks.forEach { mark ->
            SubjectGradeRow(mark)
        }
    }
}

@Composable
private fun SubjectGradeRow(mark: com.example.data.model.MarkEntry) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(mark.subjectName, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color(0xFF0F172A))
                Text("পূর্ণমান: ${mark.maxMarks.toInt()}", fontSize = 12.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${mark.marksObtained.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A)
                )
            }
        }
    }
}

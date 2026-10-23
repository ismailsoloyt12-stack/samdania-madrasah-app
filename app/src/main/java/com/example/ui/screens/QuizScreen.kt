package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val quizzes = uiState.quizzesList
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var answeredCount by remember { mutableIntStateOf(0) }

    val currentQuiz = quizzes.getOrNull(currentQuestionIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("কুইজ প্রতিযোগিতা (Quiz Contest)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("quiz_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        currentQuestionIndex = 0
                        selectedOption = null
                        score = 0
                        answeredCount = 0
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "পুনরায় শুরু")
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
            // Score Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MadrasahNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("দৈনিক ইসলামিক কুইজ", color = MadrasahGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("প্রশ্ন: ${currentQuestionIndex + 1} / ${quizzes.size}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MadrasahGold.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MadrasahGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("স্কোর: $score", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            if (currentQuiz != null) {
                // Question Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = currentQuiz.question,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val options = listOf(
                                currentQuiz.optionA,
                                currentQuiz.optionB,
                                currentQuiz.optionC,
                                currentQuiz.optionD
                            )

                            options.forEachIndexed { index, optionText ->
                                val isSelected = selectedOption == index
                                val isCorrect = index == currentQuiz.correctOption
                                val isRevealed = selectedOption != null

                                val (bgColor, borderColor, textColor) = when {
                                    isRevealed && isCorrect -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), Color(0xFF166534))
                                    isRevealed && isSelected && !isCorrect -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), Color(0xFF991B1B))
                                    else -> Triple(Color.White, Color(0xFFCBD5E1), Color(0xFF0F172A))
                                }

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable(enabled = selectedOption == null) {
                                            selectedOption = index
                                            answeredCount++
                                            if (index == currentQuiz.correctOption) {
                                                score += currentQuiz.points
                                            }
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = bgColor
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .background(Color(0xFF0D1B44), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = when (index) {
                                                    0 -> "ক"
                                                    1 -> "খ"
                                                    2 -> "গ"
                                                    else -> "ঘ"
                                                },
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = optionText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            // Explanation & Next Button
                            if (selectedOption != null) {
                                Spacer(modifier = Modifier.height(14.dp))

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = if (selectedOption == currentQuiz.correctOption) "মাশাআল্লাহ! সঠিক উত্তর।" else "সঠিক উত্তর: ${options[currentQuiz.correctOption]}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = if (selectedOption == currentQuiz.correctOption) Color(0xFF166534) else Color(0xFF991B1B)
                                        )
                                        if (currentQuiz.explanation.isNotEmpty()) {
                                            Text(
                                                text = currentQuiz.explanation,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF334155),
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (currentQuestionIndex < quizzes.size - 1) {
                                            currentQuestionIndex++
                                            selectedOption = null
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = if (currentQuestionIndex < quizzes.size - 1) "পরবর্তী প্রশ্ন" else "কুইজ শেষ",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

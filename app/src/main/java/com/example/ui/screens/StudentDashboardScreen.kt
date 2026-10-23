package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainUiState
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun StudentDashboardScreen(
    uiState: MainUiState,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFC))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Institutional Branding Header
        MadrasahInstitutionalHeader(
            onAdminClick = { onNavigate(AppScreen.ADMIN_PORTAL) }
        )

        // Student Profile Card (MD. RAHAT style)
        StudentProfileCard(
            student = uiState.student,
            onClick = { onNavigate(AppScreen.PROFILE) }
        )

        // Animated NEWS Ticker
        val breakingNotice = uiState.noticesList.filter { it.isBreaking }.joinToString(" • ") { it.title }.ifEmpty { 
            if (uiState.noticesList.isNotEmpty()) uiState.noticesList.first().title else "সামদানিয়া তা'লীমুল ইসলাম মাদ্রাসায় আপনাকে স্বাগতম"
        }
        NewsTickerBar(
            newsText = breakingNotice,
            onClick = { onNavigate(AppScreen.NOTIFICATIONS) }
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3x2 Grid of 6 Colorful Action Tiles matching reference
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Attendance, Routine, Academic
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    DashboardActionTile(
                        title = "Attendance",
                        icon = Icons.Default.EventAvailable,
                        gradientColors = listOf(AttendancePurple, AttendancePink),
                        onClick = { onNavigate(AppScreen.ATTENDANCE) }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    DashboardActionTile(
                        title = "Routine",
                        icon = Icons.Default.CalendarMonth,
                        gradientColors = listOf(RoutineCyan, RoutineBlue),
                        onClick = { onNavigate(AppScreen.ROUTINE) }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    DashboardActionTile(
                        title = "Academic",
                        icon = Icons.Default.School,
                        gradientColors = listOf(AcademicGreen, Color(0xFF43A047)),
                        onClick = { onNavigate(AppScreen.ACADEMIC) }
                    )
                }
            }

            // Row 2: Fees Payment, Result, Homework
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    DashboardActionTile(
                        title = "Fees Payment",
                        icon = Icons.Default.Paid,
                        gradientColors = listOf(FeesOrange, Color(0xFFFFB300)),
                        onClick = { onNavigate(AppScreen.FEES_PAYMENT) }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    DashboardActionTile(
                        title = "Result",
                        icon = Icons.Default.Assessment,
                        gradientColors = listOf(ResultBlue, Color(0xFF4FC3F7)),
                        onClick = { onNavigate(AppScreen.RESULT) }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    DashboardActionTile(
                        title = "Homework",
                        icon = Icons.Default.Assignment,
                        gradientColors = listOf(HomeworkAmber, Color(0xFFFF8F00)),
                        onClick = { onNavigate(AppScreen.HOMEWORK) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quiz Contest Banner
        QuizContestBanner(
            onClick = { onNavigate(AppScreen.QUIZ_CONTEST) }
        )

        Spacer(modifier = Modifier.height(80.dp)) // Extra space for curved floating bottom nav
    }
}

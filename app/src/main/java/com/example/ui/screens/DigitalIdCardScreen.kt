package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.MainUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalIdCardScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val student = uiState.student

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ডিজিটাল আইডি কার্ড", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("id_card_back_button")) {
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
                .background(Color(0xFFF1F5F9))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ID Card Container
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(1.5.dp, MadrasahGold.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Card Navy Header Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MadrasahNavy
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "সামদানিয়া তালিমুল ইসলাম মাদ্রাসা",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "গলাচিপা, পটুয়াখালী • স্থাপিত: ২০০৬ ইং",
                                color = MadrasahGold,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MadrasahGold,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = "STUDENT IDENTITY CARD",
                                    color = MadrasahNavy,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Photo in circular border
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0))
                            .border(3.dp, MadrasahNavy, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (student.photoUrl.isNotBlank()) {
                            AsyncImage(
                                model = student.photoUrl,
                                contentDescription = "আইডি ছবি",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.madrasah_logo),
                                error = painterResource(id = R.drawable.madrasah_logo)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "আইডি ছবি",
                                tint = MadrasahNavy,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = student.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MadrasahNavy
                    )
                    Text(
                        text = student.banglaName,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Information Grid inside ID Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IdDetailRow(label = "শ্রেণি (Class)", value = student.className)
                            IdDetailRow(label = "শাখা / বিভাগ", value = "${student.section} (${student.groupName})")
                            IdDetailRow(label = "রোল নম্বর (Roll)", value = student.rollNumber)
                            IdDetailRow(label = "রেজিস্ট্রেশন আইডি", value = student.registrationId)
                            IdDetailRow(label = "জরুরি মোবাইল", value = student.phone)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Barcode / QR Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            tint = MadrasahNavy,
                            modifier = Modifier.size(54.dp)
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "মাওলানা সাইফুল ইসলাম",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MadrasahNavy
                            )
                            Text(
                                text = "মুহতামিম / অধ্যক্ষের স্বাক্ষর",
                                fontSize = 9.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun IdDetailRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.5.sp, color = TextSecondary)
        Text(text = value, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

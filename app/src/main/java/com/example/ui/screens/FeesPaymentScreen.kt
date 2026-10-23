package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeesPayment
import com.example.ui.MainUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeesPaymentScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val fees = uiState.feesList
    val totalDue = fees.filter { it.status == "Due" }.sumOf { it.amount }
    val totalPaid = fees.filter { it.status == "Paid" }.sumOf { it.amount }

    var selectedReceipt by remember { mutableStateOf<FeesPayment?>(null) }
    var showPaymentSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ফি ও পেমেন্ট (Fees Payment)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("fees_back_button")) {
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MadrasahNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("শিক্ষার্থীর নাম: ${uiState.student.name}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text("রোল: ${uiState.student.rollNumber} | আইডি: ${uiState.student.registrationId}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.5.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("বর্তমান বকেয়া (Due)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                Text("৳ ${totalDue.toInt()}", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = if (totalDue > 0) Color(0xFFFFB74D) else Color.White)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("পরিশোধিত (Paid)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                Text("৳ ${totalPaid.toInt()}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "ফি বিবরণী ও রসিদ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasahNavy,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            if (fees.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "কোনো ফি বিবরণী পাওয়া যায়নি (০ টি)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "মাদ্রাসা অফিস থেকে ফি বা রসিদ ইস্যু করা হলে এখানে স্বয়ংক্রিয়ভাবে প্রদর্শিত হবে।",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(fees) { item ->
                    FeeItemRow(
                        payment = item,
                        onViewReceipt = { selectedReceipt = item },
                        onPayNow = { showPaymentSuccessDialog = true }
                    )
                }
            }
        }
    }

    // Receipt Dialog
    selectedReceipt?.let { receipt ->
        AlertDialog(
            onDismissRequest = { selectedReceipt = null },
            confirmButton = {
                TextButton(onClick = { selectedReceipt = null }) {
                    Text("ঠিক আছে", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text("ডিজিটাল ফি রসিদ", fontWeight = FontWeight.Bold, color = MadrasahNavy)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("মাদ্রাসা: সামদানিয়া তালিমুল ইসলাম মাদ্রাসা, গলাচিপা", fontSize = 11.5.sp, color = TextSecondary)
                    Text("ছাত্রের নাম: ${uiState.student.name}", fontWeight = FontWeight.SemiBold)
                    Text("রোল: ${uiState.student.rollNumber} | শ্রেণি: ${uiState.student.className}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("ফি বাবদ: ${receipt.feeTitle}")
                    Text("পরিমাণ: ৳ ${receipt.amount.toInt()}", fontWeight = FontWeight.Bold, color = MadrasahNavy)
                    Text("অবস্থা: ${if (receipt.status == "Paid") "পরিশোধিত (Paid)" else "বকেয়া (Due)"}")
                    receipt.transactionId?.let {
                        Text("ট্রানজেকশন আইডি: $it", fontSize = 11.5.sp, color = TextSecondary)
                    }
                }
            }
        )
    }

    // Payment Success Simulation Dialog
    if (showPaymentSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentSuccessDialog = false },
            confirmButton = {
                Button(
                    onClick = { showPaymentSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy)
                ) {
                    Text("সম্পন্ন")
                }
            },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AcademicGreen, modifier = Modifier.size(36.dp)) },
            title = { Text("পেমেন্ট সফল হয়েছে", fontWeight = FontWeight.Bold) },
            text = {
                Text("আপনার ফি মাদ্রাসার অনলাইন অ্যাকাউন্টে সফলভাবে জমা হয়েছে। রসিদ নম্বর: TXN-${System.currentTimeMillis().toString().takeLast(7)}। ধন্যবাদ।")
            }
        )
    }
}

@Composable
private fun FeeItemRow(
    payment: FeesPayment,
    onViewReceipt: () -> Unit,
    onPayNow: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(payment.feeTitle, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color(0xFF0F172A))
                    Text("মেয়াদ: ${payment.dueDate}", fontSize = 12.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (payment.status == "Paid") Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (payment.status == "Paid") Color(0xFF86EFAC) else Color(0xFFFCA5A5))
                ) {
                    Text(
                        text = if (payment.status == "Paid") "পরিশোধিত" else "বকেয়া",
                        color = if (payment.status == "Paid") Color(0xFF166534) else Color(0xFF991B1B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("৳ ${payment.amount.toInt()}", fontSize = 19.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))

                if (payment.status == "Paid") {
                    OutlinedButton(
                        onClick = onViewReceipt,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF0D1B44)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = Color(0xFF0D1B44), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("রসিদ দেখুন", fontSize = 12.sp, color = Color(0xFF0D1B44), fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onPayNow,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2410C)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text("ফি পরিশোধ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

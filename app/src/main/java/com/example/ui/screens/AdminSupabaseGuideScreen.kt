package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.SupabaseConfig
import com.example.ui.MainUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSupabaseGuideScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    onSaveConfig: (String, String, () -> Unit) -> Unit,
    onTestPing: ((Boolean, String) -> Unit) -> Unit,
    getSqlScript: () -> String,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var urlInput by remember { mutableStateOf(SupabaseConfig.getProjectUrl(context)) }
    var keyInput by remember { mutableStateOf(SupabaseConfig.getAnonKey(context)) }
    var pingResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var isTesting by remember { mutableStateOf(false) }

    val sqlScript = remember { getSqlScript() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supabase অনলাইন কানেকশন গাইড", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("supabase_guide_back")) {
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Info Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MadrasahNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "রিয়েলটাইম Supabase আর্কিটেকচার",
                            color = MadrasahGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "এই অ্যাপটি কোনো লোকাল স্ট্যাটিক মক ডেটার ওপর নির্ভরশীল নয়। আপনার মাদ্রাসার মূল ওয়েব সফটওয়্যার/ডাটাবেস থেকে PostgREST API এর মাধ্যমে সরাসরি ডেটা আনা হয়।",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Connection Settings Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "১. ব্যাকএন্ড এন্ডপয়েন্ট কনফিগারেশন",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MadrasahNavy
                        )

                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            label = { Text("Supabase Project URL", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
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
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = keyInput,
                            onValueChange = { keyInput = it },
                            label = { Text("Supabase Anon Public API Key", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold) },
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
                            ),
                            maxLines = 3
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onSaveConfig(urlInput, keyInput) {
                                        Toast.makeText(context, "কনফিগারেশন সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MadrasahNavy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("সেভ করুন")
                            }

                            FilledTonalButton(
                                onClick = {
                                    isTesting = true
                                    onTestPing { success, msg ->
                                        isTesting = false
                                        pingResult = Pair(success, msg)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isTesting) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("সংযোগ পরীক্ষা")
                                }
                            }
                        }

                        // Ping Result Display
                        pingResult?.let { (success, msg) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (success) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (success) AcademicGreen else Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = msg,
                                        color = if (success) AcademicGreen else Color(0xFFDC2626),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Step-by-Step Guide
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "২. ৫-ধাপের সেটআপ গাইড (How to Connect)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MadrasahNavy
                        )

                        GuideStepRow("১", "Supabase.com এ লগইন করে আপনার প্রজেক্টটি খুলুন।")
                        GuideStepRow("২", "বামের মেনু থেকে 'SQL Editor' এ ক্লিক করে 'New Query' চাপুন।")
                        GuideStepRow("৩", "নিচের 'সম্পূর্ণ SQL কোড কপি করুন' বাটনে ক্লিক করে কোডটি পেস্ট করুন।")
                        GuideStepRow("৪", "'Run' বাটনে চাপুন। সাথে সাথে আপনার সমস্ত টেবিল (students, attendance, grades, notices ইত্যাদি) তৈরি হয়ে যাবে।")
                        GuideStepRow("৫", "Project Settings -> API থেকে আপনার Project URL এবং Anon Key কপি করে উপরে পেস্ট করে সেভ করুন।")
                    }
                }
            }

            // SQL Schema Script with 1-Click Copy
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "৩. সম্পূর্ণ SQL স্কিমা স্ক্রিপ্ট",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MadrasahNavy
                            )

                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Supabase Schema SQL", sqlScript)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "SQL স্ক্রিপ্ট ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RoutineBlue),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("কপি করুন (Copy SQL)", fontSize = 11.5.sp)
                            }
                        }

                        Text(
                            "এই স্ক্রিপ্টটিতে ১০টি টেবিল, RLS সিকিউরিটি রুলস এবং প্রাথমিক টেস্ট ডেটা অন্তর্ভুক্ত রয়েছে:",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )

                        // Code Box preview
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                        ) {
                            SelectionContainer {
                                Text(
                                    text = sqlScript.take(1500) + "\n... [সম্পূর্ণ কোড পেতে 'কপি করুন' বাটনে ক্লিক করুন]",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .horizontalScroll(rememberScrollState())
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideStepRow(stepNumber: String, stepText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = androidx.compose.foundation.shape.CircleShape,
            color = MadrasahNavy.copy(alpha = 0.12f),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(stepNumber, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = MadrasahNavy)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stepText,
            fontSize = 12.sp,
            color = TextPrimary,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

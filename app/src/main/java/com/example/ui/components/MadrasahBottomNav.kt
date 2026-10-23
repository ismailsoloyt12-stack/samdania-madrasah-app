package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MadrasahGold
import com.example.ui.theme.MadrasahNavy

@Composable
fun MadrasahBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .height(72.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Deep Navy Rounded Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(10.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = MadrasahNavy
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spacer for the raised Home button position
                Spacer(modifier = Modifier.width(52.dp))

                // 1. Digital ID Card
                NavIconButton(
                    icon = Icons.Default.Badge,
                    description = "ডিজিটাল আইডি কার্ড",
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    testTag = "nav_id_card"
                )

                // 2. Notifications / Notices
                NavIconButton(
                    icon = Icons.Default.Notifications,
                    description = "নোটিশ ও বার্তা",
                    isSelected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    testTag = "nav_notifications"
                )

                // 3. Fees / Payments
                NavIconButton(
                    icon = Icons.Default.AccountBalanceWallet,
                    description = "ফি ও পেমেন্ট",
                    isSelected = selectedTab == 3,
                    onClick = { onTabSelected(3) },
                    testTag = "nav_fees"
                )

                // 4. Profile / Settings
                NavIconButton(
                    icon = Icons.Default.Person,
                    description = "প্রোফাইল",
                    isSelected = selectedTab == 4,
                    onClick = { onTabSelected(4) },
                    testTag = "nav_profile"
                )
            }
        }

        // Raised Floating White Circular Home Button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp)
                .offset(y = (-6).dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, radius = 28.dp)
                    ) { onTabSelected(0) }
                    .testTag("nav_home_fab"),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "হোম",
                        tint = if (selectedTab == 0) MadrasahNavy else Color(0xFF64748B),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavIconButton(
    icon: ImageVector,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (isSelected) MadrasahGold else Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(24.dp)
        )
    }
}

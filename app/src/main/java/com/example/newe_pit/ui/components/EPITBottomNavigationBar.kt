package com.example.newe_pit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.theme.*

/**
 * Komponen Reusable: Custom Bottom Navigation Bar e-PIT
 * Dilengkapi tombol aksi jangkar utama (Operasi) di bagian tengah.
 */
@Composable
fun EPITBottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = PrimaryNavy,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "Beranda",
                selected = currentRoute == "home",
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                onClick = { onNavigate("home") }
            )

            BottomNavItem(
                label = "Dokumen",
                selected = currentRoute == "documents",
                activeIcon = Icons.Filled.Description,
                inactiveIcon = Icons.Outlined.Description,
                onClick = { onNavigate("documents") }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onNavigate("logbook_1") }
                    .offset(y = (-4).dp)
            ) {
                val isLogbookActive = currentRoute.startsWith("logbook")
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            if (isLogbookActive) ActionCyan else Color.White,
                            CircleShape
                        )
                        .border(2.dp, PrimaryNavy, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Anchor,
                        contentDescription = "Operasi",
                        tint = PrimaryNavy,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = "Operasi",
                    color = if (isLogbookActive) ActionCyan else InactiveGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            BottomNavItem(
                label = "Notifikasi",
                selected = currentRoute == "notif",
                activeIcon = Icons.Filled.Notifications,
                inactiveIcon = Icons.Outlined.Notifications,
                onClick = { onNavigate("notif") }
            )

            BottomNavItem(
                label = "Profil",
                selected = currentRoute == "profile",
                activeIcon = Icons.Filled.Person,
                inactiveIcon = Icons.Outlined.Person,
                onClick = { onNavigate("profile") }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = if (selected) activeIcon else inactiveIcon,
            contentDescription = label,
            tint = if (selected) ActionCyan else InactiveGray,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = if (selected) ActionCyan else InactiveGray,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
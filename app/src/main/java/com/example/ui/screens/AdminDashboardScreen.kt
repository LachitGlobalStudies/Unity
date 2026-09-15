package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LachitBlue
import com.example.ui.theme.LachitRose

@Composable
fun AdminDashboardScreen(
    users: List<UserEntity>,
    posts: List<PostEntity>,
    reports: List<ReportEntity>,
    onBack: () -> Unit,
    onBanUser: (UserEntity) -> Unit,
    onDeleteUser: (String) -> Unit,
    onUpdateReport: (reportId: String, status: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val pendingReports = reports.filter { it.status == "PENDING" }

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Icon(Icons.Default.Security, contentDescription = null, tint = LachitRose, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Security & Trust Admin Panel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Lachit Social Moderation & Platform Health", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Stats KPI Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiCard(title = "Total Users", value = "${users.size}", icon = Icons.Default.People, color = LachitBlue, modifier = Modifier.weight(1f))
            KpiCard(title = "Active Posts", value = "${posts.size}", icon = Icons.Default.Security, color = Color(0xFF10B981), modifier = Modifier.weight(1f))
            KpiCard(title = "Pending Reports", value = "${pendingReports.size}", icon = Icons.Default.Flag, color = LachitRose, modifier = Modifier.weight(1f))
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Reports Queue (${pendingReports.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("User Accounts (${users.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (selectedTab == 0) {
                // Reports
                if (reports.isEmpty()) {
                    item {
                        EmptyState(message = "No moderation reports in queue. Everything is clean!")
                    }
                } else {
                    items(reports, key = { it.id }) { report ->
                        ReportItemCard(
                            report = report,
                            onResolve = { onUpdateReport(report.id, "RESOLVED") },
                            onDismiss = { onUpdateReport(report.id, "DISMISSED") }
                        )
                    }
                }
            } else {
                // Users
                items(users, key = { it.id }) { user ->
                    AdminUserItemCard(
                        user = user,
                        onBanToggle = { onBanUser(user) },
                        onDelete = { onDeleteUser(user.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ReportItemCard(
    report: ReportEntity,
    onResolve: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reported ${report.targetType}: ${report.reason}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = LachitRose
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (report.status == "PENDING") LachitRose.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(report.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (report.status == "PENDING") LachitRose else Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Content: \"${report.targetContent}\"",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onResolve,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Resolve & Take Action", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dismiss", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun AdminUserItemCard(
    user: UserEntity,
    onBanToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            UserAvatar(avatarUrl = user.avatarUrl, name = user.fullName, size = 44.dp)
            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (user.role == "ADMIN") {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🛡️ Admin", fontSize = 10.sp, color = LachitBlue, fontWeight = FontWeight.Bold)
                    }
                }
                Text("@${user.username} • ${user.email}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (user.isBanned) {
                    Text("⚠️ Suspended Account", fontSize = 11.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }

            if (user.role != "ADMIN") {
                IconButton(onClick = onBanToggle) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = "Ban/Unban",
                        tint = if (user.isBanned) Color(0xFF10B981) else MaterialTheme.colorScheme.error
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

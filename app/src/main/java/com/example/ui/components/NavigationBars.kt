package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.LachitBlue
import com.example.ui.theme.LachitCyan
import com.example.ui.theme.LachitRose
import com.example.ui.viewmodel.Screen

@Composable
fun LachitTopAppBar(
    currentUser: UserEntity?,
    isDarkMode: Boolean,
    unreadNotifications: Int,
    unreadMessages: Int,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand Title
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(listOf(LachitBlue, LachitCyan))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Unity",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = LachitBlue
                )
                Text(
                    text = "SOCIAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action Icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Search
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Dark Mode
            IconButton(
                onClick = onToggleDarkMode,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Admin Shield if role is ADMIN
            if (currentUser?.role == "ADMIN") {
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Dashboard",
                        tint = LachitRose
                    )
                }
            }

            // Messages with badge
            IconButton(
                onClick = onMessagesClick,
                modifier = Modifier.size(38.dp)
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Outlined.Chat,
                        contentDescription = "Messages",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    if (unreadMessages > 0) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                                .background(LachitRose, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unreadMessages",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Notifications with badge
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(38.dp)
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    if (unreadNotifications > 0) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                                .background(LachitRose, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unreadNotifications",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LachitBottomNavigationBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        modifier = modifier.navigationBarsPadding()
    ) {
        // Feed
        NavigationBarItem(
            selected = currentScreen == Screen.FEED,
            onClick = { onNavigate(Screen.FEED) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.FEED) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Feed"
                )
            },
            label = { Text("Feed", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LachitBlue,
                selectedTextColor = LachitBlue,
                indicatorColor = LachitBlue.copy(alpha = 0.15f)
            )
        )

        // Reels
        NavigationBarItem(
            selected = currentScreen == Screen.REELS,
            onClick = { onNavigate(Screen.REELS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.REELS) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                    contentDescription = "Reels"
                )
            },
            label = { Text("Reels", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LachitCyan,
                selectedTextColor = LachitCyan,
                indicatorColor = LachitCyan.copy(alpha = 0.15f)
            )
        )

        // Friends
        NavigationBarItem(
            selected = currentScreen == Screen.FRIENDS,
            onClick = { onNavigate(Screen.FRIENDS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.FRIENDS) Icons.Filled.People else Icons.Outlined.People,
                    contentDescription = "Friends"
                )
            },
            label = { Text("Friends", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LachitBlue,
                selectedTextColor = LachitBlue,
                indicatorColor = LachitBlue.copy(alpha = 0.15f)
            )
        )

        // Groups
        NavigationBarItem(
            selected = currentScreen == Screen.GROUPS,
            onClick = { onNavigate(Screen.GROUPS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.GROUPS) Icons.Filled.Groups else Icons.Outlined.Groups,
                    contentDescription = "Groups"
                )
            },
            label = { Text("Groups", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LachitBlue,
                selectedTextColor = LachitBlue,
                indicatorColor = LachitBlue.copy(alpha = 0.15f)
            )
        )

        // Profile
        NavigationBarItem(
            selected = currentScreen == Screen.PROFILE,
            onClick = { onNavigate(Screen.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LachitBlue,
                selectedTextColor = LachitBlue,
                indicatorColor = LachitBlue.copy(alpha = 0.15f)
            )
        )
    }
}

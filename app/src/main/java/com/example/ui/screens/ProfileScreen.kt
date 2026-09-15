package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.PostCard
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LachitBlue
import com.example.ui.theme.LachitCyan
import com.example.ui.theme.LachitRose

@Composable
fun ProfileScreen(
    user: UserEntity,
    currentUser: UserEntity?,
    userPosts: List<PostEntity>,
    savedPosts: List<PostEntity>,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onStartChat: (UserEntity) -> Unit,
    onSendFriendRequest: (UserEntity) -> Unit,
    onReact: (PostEntity, String) -> Unit,
    onVotePoll: (PostEntity, Int) -> Unit,
    onOpenComments: (PostEntity) -> Unit,
    onShare: (PostEntity) -> Unit,
    onToggleSave: (PostEntity) -> Unit,
    onDeletePost: (String) -> Unit,
    onReportPost: (PostEntity) -> Unit,
    onHashtagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isMe = currentUser?.id == user.id
    var selectedTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Cover Photo + Overlapping Avatar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Cover
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Cover photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )

                // Overlapping Avatar
                UserAvatar(
                    avatarUrl = user.avatarUrl,
                    name = user.fullName,
                    size = 90.dp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp)
                        .border(3.dp, MaterialTheme.colorScheme.background, CircleShape)
                )

                // Top right settings if self
                if (isMe) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            }
        }

        // 2. Profile Details & Bio
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = user.fullName,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "@${user.username}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Primary Action button
                    if (isMe) {
                        Button(
                            onClick = onEditProfileClick,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LachitBlue),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Profile", fontSize = 12.sp)
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onSendFriendRequest(user) },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LachitBlue),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Friend", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { onStartChat(user) },
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Message", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bio
                if (user.bio.isNotBlank()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Info Meta Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    if (user.location.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(user.location, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    if (user.joinedDate.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Joined ${user.joinedDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Counts Row (Friends, Followers, Following)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    CountItem(count = user.friendsCount, label = "Friends")
                    CountItem(count = user.followersCount, label = "Followers")
                    CountItem(count = user.followingCount, label = "Following")
                }
            }
        }

        // 3. Profile Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Posts (${userPosts.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                )
                if (isMe) {
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Saved (${savedPosts.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("About", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        // 4. Tab Contents
        when (selectedTab) {
            0 -> {
                if (userPosts.isEmpty()) {
                    item {
                        EmptyState(message = "No posts yet.")
                    }
                } else {
                    items(userPosts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            currentUser = currentUser,
                            onReact = { onReact(post, it) },
                            onVotePoll = { onVotePoll(post, it) },
                            onOpenComments = { onOpenComments(post) },
                            onShare = { onShare(post) },
                            onToggleSave = { onToggleSave(post) },
                            onDelete = { onDeletePost(post.id) },
                            onReport = { onReportPost(post) },
                            onUserClick = {},
                            onHashtagClick = onHashtagClick
                        )
                    }
                }
            }
            1 -> {
                if (savedPosts.isEmpty()) {
                    item {
                        EmptyState(message = "No saved posts.")
                    }
                } else {
                    items(savedPosts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            currentUser = currentUser,
                            onReact = { onReact(post, it) },
                            onVotePoll = { onVotePoll(post, it) },
                            onOpenComments = { onOpenComments(post) },
                            onShare = { onShare(post) },
                            onToggleSave = { onToggleSave(post) },
                            onDelete = { onDeletePost(post.id) },
                            onReport = { onReportPost(post) },
                            onUserClick = {},
                            onHashtagClick = onHashtagClick
                        )
                    }
                }
            }
            2 -> {
                item {
                    AboutCard(user = user)
                }
            }
        }
    }
}

@Composable
fun CountItem(count: Int, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AboutCard(user: UserEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("About ${user.fullName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("📧 Email: ${user.email}", fontSize = 13.sp)
            if (user.location.isNotBlank()) Text("📍 Location: ${user.location}", fontSize = 13.sp)
            if (user.dob.isNotBlank()) Text("🎂 Birthday: ${user.dob}", fontSize = 13.sp)
            if (user.website.isNotBlank()) Text("🌐 Website: ${user.website}", fontSize = 13.sp)
            Text("🛡️ Platform Role: ${user.role}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LachitBlue)
        }
    }
}

@Composable
fun EditProfileDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSave: (name: String, bio: String, location: String, website: String, avatarUrl: String, coverUrl: String) -> Unit
) {
    var name by remember { mutableStateOf(user.fullName) }
    var bio by remember { mutableStateOf(user.bio) }
    var location by remember { mutableStateOf(user.location) }
    var website by remember { mutableStateOf(user.website) }
    var avatarUrl by remember { mutableStateOf(user.avatarUrl) }
    var coverUrl by remember { mutableStateOf(user.coverUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("Avatar Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = coverUrl,
                    onValueChange = { coverUrl = it },
                    label = { Text("Cover Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, bio, location, website, avatarUrl, coverUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = LachitBlue)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

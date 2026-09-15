package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FriendshipEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LachitBlue

@Composable
fun FriendsScreen(
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    friendships: List<FriendshipEntity>,
    onAcceptRequest: (requesterId: String) -> Unit,
    onRejectRequest: (requesterId: String) -> Unit,
    onSendRequest: (UserEntity) -> Unit,
    onStartChat: (UserEntity) -> Unit,
    onUserClick: (UserEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val myId = currentUser?.id ?: ""

    // Classify friendships
    val myFriendIds = friendships.filter {
        (it.user1Id == myId || it.user2Id == myId) && it.status == "FRIENDS"
    }.map { if (it.user1Id == myId) it.user2Id else it.user1Id }.toSet()

    val pendingIncomingIds = friendships.filter {
        it.user2Id == myId && it.status == "PENDING_U1_TO_U2"
    }.map { it.user1Id }.toSet()

    val pendingOutgoingIds = friendships.filter {
        it.user1Id == myId && it.status == "PENDING_U1_TO_U2"
    }.map { it.user2Id }.toSet()

    val friendsList = allUsers.filter { it.id in myFriendIds }
    val incomingRequests = allUsers.filter { it.id in pendingIncomingIds }
    val suggestions = allUsers.filter { it.id != myId && it.id !in myFriendIds && it.id !in pendingIncomingIds }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Friends (${friendsList.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Requests (${incomingRequests.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Suggestions", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Friends List
                    if (friendsList.isEmpty()) {
                        item {
                            EmptyState(message = "No friends added yet. Check out Suggestions to build your circle!")
                        }
                    } else {
                        items(friendsList, key = { it.id }) { user ->
                            FriendRowCard(
                                user = user,
                                onUserClick = { onUserClick(user) },
                                onChatClick = { onStartChat(user) },
                                onRemoveClick = { onRejectRequest(user.id) }
                            )
                        }
                    }
                }
                1 -> {
                    // Incoming Requests
                    if (incomingRequests.isEmpty()) {
                        item {
                            EmptyState(message = "No pending friend requests.")
                        }
                    } else {
                        items(incomingRequests, key = { it.id }) { user ->
                            FriendRequestCard(
                                user = user,
                                onAccept = { onAcceptRequest(user.id) },
                                onDecline = { onRejectRequest(user.id) },
                                onUserClick = { onUserClick(user) }
                            )
                        }
                    }
                }
                2 -> {
                    // Suggestions
                    if (suggestions.isEmpty()) {
                        item {
                            EmptyState(message = "No suggestions at the moment.")
                        }
                    } else {
                        items(suggestions, key = { it.id }) { user ->
                            val isPendingOut = user.id in pendingOutgoingIds
                            FriendSuggestionCard(
                                user = user,
                                isPending = isPendingOut,
                                onAddFriend = { onSendRequest(user) },
                                onUserClick = { onUserClick(user) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendRowCard(
    user: UserEntity,
    onUserClick: () -> Unit,
    onChatClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            UserAvatar(
                avatarUrl = user.avatarUrl,
                name = user.fullName,
                size = 50.dp,
                onClick = onUserClick
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { onUserClick() }
                )
                Text(
                    text = "@${user.username} • ${user.friendsCount} friends",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onChatClick) {
                Icon(Icons.Default.Chat, contentDescription = "Message", tint = LachitBlue)
            }

            IconButton(onClick = onRemoveClick) {
                Icon(Icons.Default.PersonRemove, contentDescription = "Remove Friend", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun FriendRequestCard(
    user: UserEntity,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onUserClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            UserAvatar(
                avatarUrl = user.avatarUrl,
                name = user.fullName,
                size = 54.dp,
                onClick = onUserClick
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { onUserClick() }
                )
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAccept,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LachitBlue),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Confirm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDecline,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Delete", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FriendSuggestionCard(
    user: UserEntity,
    isPending: Boolean,
    onAddFriend: () -> Unit,
    onUserClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            UserAvatar(
                avatarUrl = user.avatarUrl,
                name = user.fullName,
                size = 50.dp,
                onClick = onUserClick
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { onUserClick() }
                )
                Text(
                    text = if (user.location.isNotBlank()) "📍 ${user.location}" else "@${user.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onAddFriend,
                enabled = !isPending,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LachitBlue),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isPending) "Requested" else "Add Friend", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

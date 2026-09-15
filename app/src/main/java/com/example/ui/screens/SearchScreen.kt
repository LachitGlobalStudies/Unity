package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GroupEntity
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.PostCard
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LachitBlue
import com.example.ui.viewmodel.SearchFilter

@Composable
fun SearchScreen(
    query: String,
    filter: SearchFilter,
    allUsers: List<UserEntity>,
    allPosts: List<PostEntity>,
    allGroups: List<GroupEntity>,
    currentUser: UserEntity?,
    onQueryChange: (String) -> Unit,
    onFilterChange: (SearchFilter) -> Unit,
    onUserClick: (UserEntity) -> Unit,
    onPostReact: (PostEntity, String) -> Unit,
    onPostComment: (PostEntity) -> Unit,
    onGroupClick: (GroupEntity) -> Unit,
    onHashtagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cleanQ = query.trim()
    val filteredUsers = allUsers.filter {
        cleanQ.isEmpty() || it.fullName.contains(cleanQ, ignoreCase = true) || it.username.contains(cleanQ, ignoreCase = true)
    }
    val filteredPosts = allPosts.filter {
        cleanQ.isEmpty() || it.content.contains(cleanQ, ignoreCase = true) || it.hashtags.contains(cleanQ, ignoreCase = true)
    }
    val filteredGroups = allGroups.filter {
        cleanQ.isEmpty() || it.name.contains(cleanQ, ignoreCase = true) || it.description.contains(cleanQ, ignoreCase = true)
    }

    val trendingTags = listOf("#LachitSocial", "#Assam", "#Technology", "#Design", "#Photography", "#Nature")

    Column(modifier = modifier.fillMaxSize()) {
        // Search Input Field
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search Lachit Social...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )

        // Filter Chips Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SearchFilter.values()) { f ->
                FilterChip(
                    selected = filter == f,
                    onClick = { onFilterChange(f) },
                    label = { Text(f.name.lowercase().capitalize(), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LachitBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Trending Hashtags Bar
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
            Text("Trending Topics", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(trendingTags) { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onHashtagClick(tag) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(tag, fontSize = 11.sp, color = LachitBlue, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Search Results List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // People section
            if (filter == SearchFilter.ALL || filter == SearchFilter.PEOPLE) {
                if (filteredUsers.isNotEmpty()) {
                    item {
                        Text(
                            text = "People (${filteredUsers.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                    items(filteredUsers, key = { it.id }) { user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUserClick(user) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            UserAvatar(avatarUrl = user.avatarUrl, name = user.fullName, size = 44.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("@${user.username} • ${user.friendsCount} friends", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Groups section
            if (filter == SearchFilter.ALL || filter == SearchFilter.GROUPS) {
                if (filteredGroups.isNotEmpty()) {
                    item {
                        Text(
                            text = "Groups (${filteredGroups.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                    items(filteredGroups, key = { it.id }) { group ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGroupClick(group) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LachitBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(group.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(group.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${group.memberCount} members • ${group.category}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Posts section
            if (filter == SearchFilter.ALL || filter == SearchFilter.POSTS || filter == SearchFilter.HASHTAGS) {
                if (filteredPosts.isNotEmpty()) {
                    item {
                        Text(
                            text = "Posts (${filteredPosts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                    items(filteredPosts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            currentUser = currentUser,
                            onReact = { onPostReact(post, it) },
                            onVotePoll = {},
                            onOpenComments = { onPostComment(post) },
                            onShare = {},
                            onToggleSave = {},
                            onDelete = {},
                            onReport = {},
                            onUserClick = {},
                            onHashtagClick = onHashtagClick
                        )
                    }
                }
            }
        }
    }
}

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.StoryEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.PostCard
import com.example.ui.components.UserAvatar
import com.example.ui.components.postGradients
import com.example.ui.theme.LachitBlue
import com.example.ui.theme.LachitCyan
import com.example.ui.theme.LachitRose

@Composable
fun FeedScreen(
    currentUser: UserEntity?,
    stories: List<StoryEntity>,
    posts: List<PostEntity>,
    onStoryClick: (StoryEntity) -> Unit,
    onCreateStoryClick: () -> Unit,
    onCreatePostClick: () -> Unit,
    onReact: (PostEntity, String) -> Unit,
    onVotePoll: (PostEntity, Int) -> Unit,
    onOpenComments: (PostEntity) -> Unit,
    onShare: (PostEntity) -> Unit,
    onToggleSave: (PostEntity) -> Unit,
    onDeletePost: (String) -> Unit,
    onReportPost: (PostEntity) -> Unit,
    onUserClick: (String) -> Unit,
    onHashtagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Stories Reel
        item {
            StoriesRail(
                currentUser = currentUser,
                stories = stories,
                onStoryClick = onStoryClick,
                onCreateStoryClick = onCreateStoryClick
            )
        }

        // 2. What's on your mind Composer trigger
        item {
            CreatePostTriggerCard(
                currentUser = currentUser,
                onClick = onCreatePostClick
            )
        }

        // 3. Posts List
        if (posts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No posts yet. Share something with the community!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(posts, key = { it.id }) { post ->
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
                    onUserClick = { onUserClick(post.authorId) },
                    onHashtagClick = onHashtagClick
                )
            }
        }
    }
}

@Composable
fun StoriesRail(
    currentUser: UserEntity?,
    stories: List<StoryEntity>,
    onStoryClick: (StoryEntity) -> Unit,
    onCreateStoryClick: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Create Story Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .width(105.dp)
                    .height(170.dp)
                    .clickable { onCreateStoryClick() }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top half: user avatar
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentUser?.avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "User profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                    )

                    // Add button in middle
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 40.dp)
                            .size(32.dp)
                            .background(LachitBlue, CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Story",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Bottom label
                    Text(
                        text = "Create Story",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }

        // Friends stories
        items(stories, key = { it.id }) { story ->
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .width(105.dp)
                    .height(170.dp)
                    .clickable { onStoryClick(story) }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (story.mediaUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(story.mediaUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Story media",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        val brush = postGradients.getOrNull(story.bgGradient.coerceIn(1, 4))
                            ?: Brush.linearGradient(listOf(LachitBlue, LachitCyan))
                        Box(modifier = Modifier.fillMaxSize().background(brush))
                    }

                    // Subtle dark gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )

                    // Top Avatar
                    UserAvatar(
                        avatarUrl = story.userAvatar,
                        name = story.userName,
                        size = 32.dp,
                        hasStoryBorder = true,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                    )

                    // Bottom User Name
                    Text(
                        text = story.userName,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePostTriggerCard(
    currentUser: UserEntity?,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserAvatar(
                    avatarUrl = currentUser?.avatarUrl ?: "",
                    name = currentUser?.fullName ?: "You",
                    size = 40.dp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "What's on your mind, ${currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Alex"}?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.6.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Quick attachments row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Photo",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Photo", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Poll,
                        contentDescription = "Poll",
                        tint = LachitCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Poll", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Feeling", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

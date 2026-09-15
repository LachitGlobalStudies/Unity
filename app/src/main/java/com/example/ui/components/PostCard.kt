package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
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
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.LachitBlue
import com.example.ui.theme.LachitRose
import com.example.ui.theme.ReactionAngry
import com.example.ui.theme.ReactionHaha
import com.example.ui.theme.ReactionLike
import com.example.ui.theme.ReactionLove
import com.example.ui.theme.ReactionSad
import com.example.ui.theme.ReactionWow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostCard(
    post: PostEntity,
    currentUser: UserEntity?,
    onReact: (String) -> Unit,
    onVotePoll: (Int) -> Unit,
    onOpenComments: () -> Unit,
    onShare: () -> Unit,
    onToggleSave: () -> Unit,
    onDelete: () -> Unit,
    onReport: () -> Unit,
    onUserClick: () -> Unit,
    onHashtagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showReactionPicker by remember { mutableStateOf(false) }

    val formattedTime = remember(post.createdAt) {
        val diff = System.currentTimeMillis() - post.createdAt
        when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(post.createdAt))
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {

            // Author Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserAvatar(
                    avatarUrl = post.authorAvatar,
                    name = post.authorName,
                    size = 44.dp,
                    onClick = onUserClick
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.clickable { onUserClick() }
                        )

                        if (post.feeling.isNotBlank()) {
                            Text(
                                text = " is feeling ${post.feeling}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "@${post.authorUsername}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " • $formattedTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Public",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (post.location.isNotBlank()) {
                            Text(
                                text = " • 📍${post.location}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Options Menu
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Post options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (post.isSaved) "Remove Bookmark" else "Save Post") },
                            leadingIcon = {
                                Icon(
                                    if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showMenu = false
                                onToggleSave()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onShare()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Report Post") },
                            onClick = {
                                showMenu = false
                                onReport()
                            }
                        )
                        if (currentUser?.id == post.authorId || currentUser?.role == "ADMIN") {
                            DropdownMenuItem(
                                text = { Text("Delete Post", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Content - Banner or regular text
            if (post.backgroundGradientIndex > 0 && post.backgroundGradientIndex < postGradients.size) {
                val brush = postGradients[post.backgroundGradientIndex] ?: Brush.linearGradient(listOf(LachitBlue, LachitRose))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.content,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            lineHeight = 26.sp
                        )
                    )
                }
            } else {
                if (post.content.isNotBlank()) {
                    Text(
                        text = post.content,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Media Image Preview
            if (post.mediaUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.mediaUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Post image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            // Interactive Poll
            if (post.pollQuestion.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                val totalVotes = post.pollVotes1 + post.pollVotes2
                val pct1 = if (totalVotes > 0) (post.pollVotes1 * 100 / totalVotes) else 0
                val pct2 = if (totalVotes > 0) (post.pollVotes2 * 100 / totalVotes) else 0

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "📊 ${post.pollQuestion}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Option 1
                        PollOptionRow(
                            label = post.pollOption1,
                            percentage = pct1,
                            votes = post.pollVotes1,
                            isVoted = post.userVotedOption == 1,
                            hasVoted = post.userVotedOption != 0,
                            onClick = { onVotePoll(1) }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Option 2
                        PollOptionRow(
                            label = post.pollOption2,
                            percentage = pct2,
                            votes = post.pollVotes2,
                            isVoted = post.userVotedOption == 2,
                            hasVoted = post.userVotedOption != 0,
                            onClick = { onVotePoll(2) }
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalVotes total votes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats Row: reactions, comments count, shares
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reaction icon cluster + count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (post.likesCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(ReactionLike, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👍", fontSize = 11.sp)
                        }
                        if (post.userReaction == "LOVE") {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(ReactionLove, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("❤️", fontSize = 11.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${post.likesCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    if (post.commentsCount > 0) {
                        Text(
                            text = "${post.commentsCount} comments",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable { onOpenComments() }
                        )
                    }
                    if (post.sharesCount > 0) {
                        Text(
                            text = " • ${post.sharesCount} shares",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.8.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Reaction Picker Popover (Visible when triggered)
            AnimatedVisibility(visible = showReactionPicker) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val reactions = listOf(
                            "LIKE" to "👍",
                            "LOVE" to "❤️",
                            "HAHA" to "😂",
                            "WOW" to "😮",
                            "SAD" to "😢",
                            "ANGRY" to "😡"
                        )
                        reactions.forEach { (type, emoji) ->
                            Text(
                                text = emoji,
                                fontSize = 26.sp,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        showReactionPicker = false
                                        onReact(type)
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }

            // Action Buttons Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like / React Button
                val (reactionLabel, reactionColor, reactionIcon) = when (post.userReaction) {
                    "LOVE" -> Triple("Love", ReactionLove, "❤️")
                    "HAHA" -> Triple("Haha", ReactionHaha, "😂")
                    "WOW" -> Triple("Wow", ReactionWow, "😮")
                    "SAD" -> Triple("Sad", ReactionSad, "😢")
                    "ANGRY" -> Triple("Angry", ReactionAngry, "😡")
                    "LIKE" -> Triple("Liked", ReactionLike, "👍")
                    else -> Triple("Like", MaterialTheme.colorScheme.onSurfaceVariant, "👍")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (post.userReaction.isEmpty()) {
                                onReact("LIKE")
                            } else {
                                showReactionPicker = !showReactionPicker
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = reactionIcon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = reactionLabel,
                        color = reactionColor,
                        fontWeight = if (post.userReaction.isNotEmpty()) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }

                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenComments() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Comment",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }

                // Share Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onShare() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PollOptionRow(
    label: String,
    percentage: Int,
    votes: Int,
    isVoted: Boolean,
    hasVoted: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = if (isVoted) 2.dp else 1.dp,
                color = if (isVoted) LachitBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !hasVoted) { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isVoted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Voted",
                            tint = LachitBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = label,
                        fontWeight = if (isVoted) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (hasVoted) {
                    Text(
                        text = "$percentage% ($votes)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = LachitBlue
                    )
                }
            }

            if (hasVoted) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = LachitBlue,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.StoryEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.LachitBlue
import com.example.ui.theme.LachitCyan
import com.example.ui.theme.LachitRose
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSheetModal(
    post: PostEntity,
    comments: List<CommentEntity>,
    currentUser: UserEntity?,
    onClose: () -> Unit,
    onAddComment: (postId: String, authorId: String, content: String, parentId: String?) -> Unit,
    onDeleteComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var replyingToComment by remember { mutableStateOf<CommentEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comments (${comments.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Post snapshot reminder
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(avatarUrl = post.authorAvatar, name = post.authorName, size = 32.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = post.content.take(80) + if (post.content.length > 80) "..." else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Comments List
            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No comments yet. Be the first to spark the conversation!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    // Top level comments
                    val topLevel = comments.filter { it.parentCommentId == null }
                    items(topLevel) { parent ->
                        CommentItem(
                            comment = parent,
                            currentUser = currentUser,
                            onReply = { replyingToComment = parent },
                            onDelete = { onDeleteComment(parent.id) }
                        )

                        // Replies
                        val replies = comments.filter { it.parentCommentId == parent.id }
                        replies.forEach { reply ->
                            CommentItem(
                                comment = reply,
                                currentUser = currentUser,
                                isReply = true,
                                onReply = { replyingToComment = parent },
                                onDelete = { onDeleteComment(reply.id) }
                            )
                        }
                    }
                }
            }

            // Replying banner
            replyingToComment?.let { replyTarget ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Replying to @${replyTarget.authorUsername}",
                        style = MaterialTheme.typography.bodySmall,
                        color = LachitBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(
                        onClick = { replyingToComment = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel reply", modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    avatarUrl = currentUser?.avatarUrl ?: "",
                    name = currentUser?.fullName ?: "You",
                    size = 36.dp
                )
                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text(if (replyingToComment != null) "Write a reply..." else "Add a comment...")
                    },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onAddComment(post.id, post.authorId, commentText, replyingToComment?.id)
                            commentText = ""
                            replyingToComment = null
                        }
                    },
                    enabled = commentText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (commentText.isNotBlank()) LachitBlue else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentEntity,
    currentUser: UserEntity?,
    isReply: Boolean = false,
    onReply: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isReply) 38.dp else 0.dp, top = 6.dp, bottom = 6.dp)
    ) {
        UserAvatar(avatarUrl = comment.authorAvatar, name = comment.authorName, size = if (isReply) 28.dp else 36.dp)
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = comment.authorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = comment.content,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            ) {
                Text(
                    text = "Reply",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LachitBlue,
                    modifier = Modifier.clickable { onReply() }
                )

                if (currentUser?.id == comment.authorId || currentUser?.role == "ADMIN") {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Delete",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable { onDelete() }
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePostModal(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onSubmit: (
        content: String,
        mediaUrl: String,
        feeling: String,
        location: String,
        privacy: String,
        bgGradientIndex: Int,
        pollQ: String,
        poll1: String,
        poll2: String
    ) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var mediaUrl by remember { mutableStateOf("") }
    var feeling by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var privacy by remember { mutableStateOf("PUBLIC") }
    var bgGradientIndex by remember { mutableIntStateOf(0) }

    var showPhotoInput by remember { mutableStateOf(false) }
    var showPollInputs by remember { mutableStateOf(false) }
    var showFeelingPicker by remember { mutableStateOf(false) }
    var showLocationInput by remember { mutableStateOf(false) }

    var pollQuestion by remember { mutableStateOf("") }
    var pollOption1 by remember { mutableStateOf("") }
    var pollOption2 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank() || mediaUrl.isNotBlank() || pollQuestion.isNotBlank()) {
                        onSubmit(
                            content,
                            mediaUrl,
                            feeling,
                            location,
                            privacy,
                            bgGradientIndex,
                            pollQuestion,
                            pollOption1,
                            pollOption2
                        )
                    }
                },
                enabled = content.isNotBlank() || mediaUrl.isNotBlank() || pollQuestion.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = LachitBlue)
            ) {
                Text("Publish Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Post", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Author row + privacy badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(
                        avatarUrl = currentUser?.avatarUrl ?: "",
                        name = currentUser?.fullName ?: "You",
                        size = 40.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = currentUser?.fullName ?: "You",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    privacy = when (privacy) {
                                        "PUBLIC" -> "FRIENDS"
                                        "FRIENDS" -> "ONLY_ME"
                                        else -> "PUBLIC"
                                    }
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when (privacy) {
                                    "FRIENDS" -> "👥 Friends"
                                    "ONLY_ME" -> "🔒 Only Me"
                                    else -> "🌐 Public"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Post Content Input
                if (bgGradientIndex > 0 && bgGradientIndex < postGradients.size) {
                    val brush = postGradients[bgGradientIndex] ?: Brush.linearGradient(listOf(LachitBlue, LachitRose))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                            .padding(12.dp)
                    ) {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            placeholder = { Text("What's on your mind?", color = Color.White.copy(alpha = 0.7f)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = { Text("What's on your mind, ${currentUser?.fullName?.split(" ")?.firstOrNull() ?: ""}?") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Gradient Theme Swatches
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Theme:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .border(if (bgGradientIndex == 0) 2.dp else 0.dp, LachitBlue, CircleShape)
                            .clickable { bgGradientIndex = 0 }
                    )
                    listOf(
                        Color(0xFF2563EB),
                        Color(0xFF10B981),
                        Color(0xFFF43F5E),
                        Color(0xFF8B5CF6)
                    ).forEachIndexed { idx, col ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(col)
                                .border(if (bgGradientIndex == idx + 1) 2.dp else 0.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                .clickable { bgGradientIndex = idx + 1 }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Photo URL input toggle
                AnimatedVisibility(visible = showPhotoInput) {
                    OutlinedTextField(
                        value = mediaUrl,
                        onValueChange = { mediaUrl = it },
                        placeholder = { Text("Enter photo or image URL") },
                        label = { Text("Image URL") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                // Poll inputs toggle
                AnimatedVisibility(visible = showPollInputs) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("Create a Community Poll", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        OutlinedTextField(
                            value = pollQuestion,
                            onValueChange = { pollQuestion = it },
                            placeholder = { Text("Ask a question...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = pollOption1,
                            onValueChange = { pollOption1 = it },
                            placeholder = { Text("Option 1") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = pollOption2,
                            onValueChange = { pollOption2 = it },
                            placeholder = { Text("Option 2") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Location input toggle
                AnimatedVisibility(visible = showLocationInput) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("e.g. Guwahati, Assam") },
                        label = { Text("Location") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                // Feeling Picker toggle
                AnimatedVisibility(visible = showFeelingPicker) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("blessed 🙏", "excited ⚡", "happy 😊", "thinking 🤔").forEach { feel ->
                            Text(
                                text = feel,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (feeling == feel) LachitBlue else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { feeling = if (feeling == feel) "" else feel }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                color = if (feeling == feel) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Toolbar Icons to add attachments
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showPhotoInput = !showPhotoInput }) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add Photo", tint = Color(0xFF10B981))
                    }
                    IconButton(onClick = { showPollInputs = !showPollInputs }) {
                        Icon(Icons.Default.Poll, contentDescription = "Add Poll", tint = LachitCyan)
                    }
                    IconButton(onClick = { showFeelingPicker = !showFeelingPicker }) {
                        Icon(Icons.Default.EmojiEmotions, contentDescription = "Feeling", tint = Color(0xFFF59E0B))
                    }
                    IconButton(onClick = { showLocationInput = !showLocationInput }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = LachitRose)
                    }
                }
            }
        }
    )
}

@Composable
fun StoryViewerModal(
    story: StoryEntity,
    allStories: List<StoryEntity>,
    onClose: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(allStories.indexOfFirst { it.id == story.id }.coerceAtLeast(0)) }
    val currentStory = allStories.getOrNull(currentIndex) ?: story

    var progress by remember(currentIndex) { mutableFloatStateOf(0f) }

    LaunchedEffect(currentIndex) {
        progress = 0f
        val duration = 5000L // 5 seconds per story
        val steps = 50
        val interval = duration / steps
        for (i in 1..steps) {
            delay(interval)
            progress = i / steps.toFloat()
        }
        // Advance to next or close
        if (currentIndex < allStories.size - 1) {
            currentIndex += 1
        } else {
            onClose()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Story background media or gradient
        if (currentStory.mediaUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentStory.mediaUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Story media",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val brush = postGradients.getOrNull(currentStory.bgGradient.coerceIn(1, 4))
                ?: Brush.linearGradient(listOf(LachitBlue, LachitRose))
            Box(modifier = Modifier.fillMaxSize().background(brush))
        }

        // Tap gestures: Left side = previous, Right side = next
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        if (currentIndex > 0) currentIndex -= 1
                    }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        if (currentIndex < allStories.size - 1) currentIndex += 1 else onClose()
                    }
            )
        }

        // Overlay header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
        ) {
            // Segmented Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                allStories.forEachIndexed { idx, _ ->
                    val segmentProgress = when {
                        idx < currentIndex -> 1f
                        idx == currentIndex -> progress
                        else -> 0f
                    }
                    LinearProgressIndicator(
                        progress = { segmentProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // User info row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(avatarUrl = currentStory.userAvatar, name = currentStory.userName, size = 36.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = currentStory.userName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "24h story",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close story", tint = Color.White)
                }
            }
        }

        // Bottom Caption
        if (currentStory.caption.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 40.dp)
            ) {
                Text(
                    text = currentStory.caption,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ReportDialog(
    targetType: String,
    onDismiss: () -> Unit,
    onSubmitReport: (reason: String) -> Unit
) {
    var selectedReason by remember { mutableStateOf("Spam or fraudulent content") }
    val reasons = listOf(
        "Spam or fraudulent content",
        "Harassment or hate speech",
        "Violence or dangerous content",
        "Misinformation",
        "Intellectual property violation"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report $targetType", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Why are you reporting this $targetType? Your report is anonymous.")
                Spacer(modifier = Modifier.height(8.dp))
                reasons.forEach { r ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = r }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedReason == r,
                            onClick = { selectedReason = r }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(r, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitReport(selectedReason) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Submit Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

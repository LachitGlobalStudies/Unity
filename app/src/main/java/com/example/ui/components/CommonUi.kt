package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.LachitBlue
import com.example.ui.theme.LachitCyan
import com.example.ui.theme.LachitRose
import com.example.ui.theme.LachitAmber

@Composable
fun UserAvatar(
    avatarUrl: String,
    name: String,
    size: Dp = 44.dp,
    hasStoryBorder: Boolean = false,
    isOnline: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val borderModifier = if (hasStoryBorder) {
        Modifier.border(
            width = 2.dp,
            brush = Brush.linearGradient(listOf(LachitRose, LachitAmber, LachitCyan)),
            shape = CircleShape
        )
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .then(borderModifier)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        if (avatarUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "$name avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Fallback initials
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LachitBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.45).sp
                )
            }
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(size * 0.3f)
                    .align(Alignment.BottomEnd)
                    .background(Color(0xFF10B981), CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}

val postGradients = listOf(
    null, // 0 = standard
    Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))),
    Brush.linearGradient(listOf(Color(0xFF065F46), Color(0xFF10B981))),
    Brush.linearGradient(listOf(Color(0xFF831843), Color(0xFFF43F5E))),
    Brush.linearGradient(listOf(Color(0xFF581C87), Color(0xFF9333EA)))
)

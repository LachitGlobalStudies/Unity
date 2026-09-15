package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val avatarUrl: String,
    val coverUrl: String,
    val bio: String,
    val location: String,
    val website: String,
    val dob: String,
    val joinedDate: String,
    val friendsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isPrivate: Boolean = false,
    val role: String = "USER", // "USER" or "ADMIN"
    val isBanned: Boolean = false
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatar: String,
    val content: String,
    val mediaUrl: String = "",
    val feeling: String = "", // e.g. "blessed", "excited"
    val location: String = "",
    val privacy: String = "PUBLIC", // "PUBLIC", "FRIENDS", "ONLY_ME"
    val backgroundGradientIndex: Int = 0, // 0 = standard white/dark card, 1..4 = rich colored gradient
    val pollQuestion: String = "",
    val pollOption1: String = "",
    val pollOption2: String = "",
    val pollVotes1: Int = 0,
    val pollVotes2: Int = 0,
    val userVotedOption: Int = 0, // 0 = none, 1 = option 1, 2 = option 2
    val userReaction: String = "", // "", "LIKE", "LOVE", "HAHA", "WOW", "SAD", "ANGRY"
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val groupId: String? = null,
    val groupName: String? = null,
    val hashtags: String = "", // comma separated, e.g. "#LachitSocial,#Tech"
    val isSaved: Boolean = false,
    val isReported: Boolean = false
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatar: String,
    val content: String,
    val parentCommentId: String? = null, // for nested replies
    val likesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reactions")
data class ReactionEntity(
    @PrimaryKey val id: String, // "${postId}_${userId}"
    val postId: String,
    val userId: String,
    val reactionType: String, // "LIKE", "LOVE", "HAHA", "WOW", "SAD", "ANGRY"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val mediaUrl: String = "",
    val caption: String = "",
    val bgGradient: Int = 0,
    val viewsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
)

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey val id: String,
    val creatorId: String,
    val creatorName: String,
    val creatorUsername: String,
    val creatorAvatar: String,
    val caption: String,
    val videoThumbnailUrl: String,
    val audioTitle: String,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val viewsCount: Int = 0,
    val isLiked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val text: String,
    val mediaUrl: String = "",
    val reaction: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserUsername: String,
    val otherUserAvatar: String,
    val lastMessage: String,
    val lastTimestamp: Long = System.currentTimeMillis(),
    val isOnline: Boolean = true,
    val unreadCount: Int = 0
)

@Entity(tableName = "friendships")
data class FriendshipEntity(
    @PrimaryKey val id: String,
    val user1Id: String,
    val user2Id: String,
    val status: String, // "FRIENDS", "PENDING_U1_TO_U2", "PENDING_U2_TO_U1", "BLOCKED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "follows")
data class FollowEntity(
    @PrimaryKey val id: String, // "${followerId}_${followingId}"
    val followerId: String,
    val followingId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val coverUrl: String,
    val category: String,
    val isPrivate: Boolean = false,
    val ownerId: String,
    val memberCount: Int = 1,
    val isJoined: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val recipientId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val type: String, // "FRIEND_REQ", "FRIEND_ACCEPT", "POST_REACTION", "COMMENT", "FOLLOW", "MESSAGE"
    val title: String,
    val message: String,
    val relatedId: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val reporterId: String,
    val targetType: String, // "POST", "USER", "COMMENT"
    val targetId: String,
    val targetContent: String,
    val reason: String,
    val status: String = "PENDING", // "PENDING", "RESOLVED", "DISMISSED"
    val createdAt: Long = System.currentTimeMillis()
)

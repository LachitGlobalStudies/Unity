package com.example.data.repository

import com.example.data.local.dao.SocialDao
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.FollowEntity
import com.example.data.local.entity.FriendshipEntity
import com.example.data.local.entity.GroupEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.ReactionEntity
import com.example.data.local.entity.ReelEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.StoryEntity
import com.example.data.local.entity.UserEntity
import com.example.data.seed.SeedData
import com.example.firebase.FirebaseAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class SocialRepository(
    private val dao: SocialDao,
    private val authService: FirebaseAuthService? = null
) {

    val authStateFlow: Flow<com.google.firebase.auth.FirebaseUser?>? = authService?.authStateFlow

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        val adminUser = dao.getUserById("user_admin")
        if (adminUser == null) {
            dao.insertUsers(SeedData.initialUsers)
            dao.insertPosts(SeedData.initialPosts)
            dao.insertComments(SeedData.initialComments)
            dao.insertStories(SeedData.initialStories)
            dao.insertReels(SeedData.initialReels)
            dao.insertConversations(SeedData.initialConversations)
            dao.insertMessages(SeedData.initialMessages)
            dao.insertGroups(SeedData.initialGroups)
            dao.insertFriendships(SeedData.initialFriendships)
            dao.insertNotifications(SeedData.initialNotifications)
            dao.insertReports(SeedData.initialReports)
        }
    }

    // Auth & Users
    suspend fun login(emailOrUsername: String, password: String): Result<UserEntity> {
        if (authService != null) {
            val fbResult = authService.login(emailOrUsername, password)
            if (fbResult.isSuccess) {
                return fbResult
            }
            val err = fbResult.exceptionOrNull()?.message.orEmpty()
            // If it's a specific credential error (wrong password, account not found), propagate it
            if (err.contains("Invalid", ignoreCase = true) || err.contains("password", ignoreCase = true) || err.contains("not found", ignoreCase = true)) {
                // Check if it's a local demo account before failing
                val localUser = dao.getUserByUsername(emailOrUsername.trim().lowercase())
                    ?: dao.getUserByEmail(emailOrUsername.trim().lowercase())
                if (localUser == null || localUser.passwordHash != password) {
                    return fbResult
                }
            }
        }

        return withContext(Dispatchers.IO) {
            val user = if (emailOrUsername.contains("@")) {
                dao.getUserByEmail(emailOrUsername.trim().lowercase())
            } else {
                dao.getUserByUsername(emailOrUsername.trim().lowercase())
            }

            if (user == null) {
                Result.failure(Exception("Account not found. Please check your credentials or register."))
            } else if (user.isBanned) {
                Result.failure(Exception("This account has been suspended by the platform administrator."))
            } else if (user.passwordHash != password && user.passwordHash != "demo") {
                Result.failure(Exception("Incorrect password. Please try again."))
            } else {
                Result.success(user)
            }
        }
    }

    suspend fun register(
        fullName: String,
        username: String,
        email: String,
        password: String,
        dob: String,
        avatarUrl: String
    ): Result<UserEntity> {
        if (authService != null) {
            val fbResult = authService.register(fullName, username, email, password, dob, avatarUrl)
            if (fbResult.isSuccess) return fbResult
            val err = fbResult.exceptionOrNull()?.message.orEmpty()
            if (err.contains("already", ignoreCase = true) || err.contains("least 6", ignoreCase = true)) {
                return fbResult
            }
        }

        return withContext(Dispatchers.IO) {
            val cleanUsername = username.trim().lowercase().replace("@", "")
            val cleanEmail = email.trim().lowercase()

            val existingUser = dao.getUserByUsername(cleanUsername)
            if (existingUser != null) {
                return@withContext Result.failure(Exception("Username @$cleanUsername is already taken."))
            }

            val existingEmail = dao.getUserByEmail(cleanEmail)
            if (existingEmail != null) {
                return@withContext Result.failure(Exception("An account with email $cleanEmail already exists."))
            }

            val newUser = UserEntity(
                id = "user_${UUID.randomUUID()}",
                username = cleanUsername,
                fullName = fullName.trim(),
                email = cleanEmail,
                passwordHash = password,
                avatarUrl = if (avatarUrl.isNotBlank()) avatarUrl else "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=400&q=80",
                coverUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?auto=format&fit=crop&w=1200&q=80",
                bio = "New explorer on Lachit Social! ✨",
                location = "India",
                website = "",
                dob = dob,
                joinedDate = "September 2026",
                role = "USER"
            )
            dao.insertUser(newUser)
            Result.success(newUser)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return authService?.sendPasswordReset(email) ?: Result.success(Unit)
    }

    fun logout() {
        authService?.logout()
    }

    suspend fun fetchFirebaseUserEntity(firebaseUser: com.google.firebase.auth.FirebaseUser): UserEntity? {
        return authService?.fetchOrCreateUserEntity(firebaseUser, firebaseUser.uid)
    }

    suspend fun getUserById(userId: String): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUserById(userId)
    }

    suspend fun updateUserProfile(user: UserEntity) = withContext(Dispatchers.IO) {
        dao.updateUser(user)
    }

    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun banUser(userId: String, isBanned: Boolean) = withContext(Dispatchers.IO) {
        dao.setUserBanned(userId, isBanned)
    }

    suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        dao.deleteUser(userId)
    }

    // Posts & Feed
    fun getAllFeedPosts(): Flow<List<PostEntity>> = dao.getAllFeedPosts()
    fun getPostsByUser(userId: String): Flow<List<PostEntity>> = dao.getPostsByUser(userId)
    fun getPostsByGroup(groupId: String): Flow<List<PostEntity>> = dao.getPostsByGroup(groupId)
    fun getSavedPosts(): Flow<List<PostEntity>> = dao.getSavedPosts()
    fun getPostsByHashtag(hashtag: String): Flow<List<PostEntity>> = dao.getPostsByHashtag(hashtag)

    suspend fun createPost(
        author: UserEntity,
        content: String,
        mediaUrl: String,
        feeling: String,
        location: String,
        privacy: String,
        bgGradientIndex: Int,
        pollQuestion: String,
        pollOption1: String,
        pollOption2: String,
        groupId: String? = null,
        groupName: String? = null
    ) = withContext(Dispatchers.IO) {
        // Extract hashtags
        val regex = Regex("#\\w+")
        val tags = regex.findAll(content).map { it.value }.joinToString(",")

        val post = PostEntity(
            id = "post_${UUID.randomUUID()}",
            authorId = author.id,
            authorName = author.fullName,
            authorUsername = author.username,
            authorAvatar = author.avatarUrl,
            content = content.trim(),
            mediaUrl = mediaUrl.trim(),
            feeling = feeling,
            location = location,
            privacy = privacy,
            backgroundGradientIndex = bgGradientIndex,
            pollQuestion = pollQuestion.trim(),
            pollOption1 = pollOption1.trim(),
            pollOption2 = pollOption2.trim(),
            groupId = groupId,
            groupName = groupName,
            hashtags = tags,
            createdAt = System.currentTimeMillis()
        )
        dao.insertPost(post)
    }

    suspend fun reactToPost(post: PostEntity, currentUser: UserEntity, reactionType: String) = withContext(Dispatchers.IO) {
        val currentReaction = post.userReaction
        val newReaction = if (currentReaction == reactionType) "" else reactionType
        val diff = when {
            currentReaction.isEmpty() && newReaction.isNotEmpty() -> 1
            currentReaction.isNotEmpty() && newReaction.isEmpty() -> -1
            else -> 0
        }
        val newLikes = (post.likesCount + diff).coerceAtLeast(0)
        dao.updatePostReaction(post.id, newReaction, newLikes)

        if (newReaction.isNotEmpty()) {
            dao.insertReaction(
                ReactionEntity(
                    id = "${post.id}_${currentUser.id}",
                    postId = post.id,
                    userId = currentUser.id,
                    reactionType = newReaction
                )
            )
            // Create notification for author if not self
            if (post.authorId != currentUser.id) {
                dao.insertNotification(
                    NotificationEntity(
                        id = "notif_${UUID.randomUUID()}",
                        recipientId = post.authorId,
                        senderId = currentUser.id,
                        senderName = currentUser.fullName,
                        senderAvatar = currentUser.avatarUrl,
                        type = "POST_REACTION",
                        title = "New Reaction",
                        message = "${currentUser.fullName} reacted to your post: \"${post.content.take(30)}...\"",
                        relatedId = post.id
                    )
                )
            }
        } else {
            dao.deleteReaction(post.id, currentUser.id)
        }
    }

    suspend fun votePoll(post: PostEntity, option: Int) = withContext(Dispatchers.IO) {
        if (post.userVotedOption != 0) return@withContext // already voted
        var v1 = post.pollVotes1
        var v2 = post.pollVotes2
        if (option == 1) v1 += 1
        if (option == 2) v2 += 1
        dao.votePoll(post.id, option, v1, v2)
    }

    suspend fun toggleSavePost(postId: String, isCurrentlySaved: Boolean) = withContext(Dispatchers.IO) {
        dao.setPostSaved(postId, !isCurrentlySaved)
    }

    suspend fun deletePost(postId: String) = withContext(Dispatchers.IO) {
        dao.deletePost(postId)
    }

    suspend fun sharePost(postId: String) = withContext(Dispatchers.IO) {
        dao.incrementShareCount(postId)
    }

    // Comments
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>> = dao.getCommentsForPost(postId)

    suspend fun addComment(
        postId: String,
        postAuthorId: String,
        currentUser: UserEntity,
        content: String,
        parentCommentId: String? = null
    ) = withContext(Dispatchers.IO) {
        val comment = CommentEntity(
            id = "comment_${UUID.randomUUID()}",
            postId = postId,
            authorId = currentUser.id,
            authorName = currentUser.fullName,
            authorUsername = currentUser.username,
            authorAvatar = currentUser.avatarUrl,
            content = content.trim(),
            parentCommentId = parentCommentId,
            createdAt = System.currentTimeMillis()
        )
        dao.insertComment(comment)
        dao.incrementCommentCount(postId)

        if (postAuthorId != currentUser.id) {
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_${UUID.randomUUID()}",
                    recipientId = postAuthorId,
                    senderId = currentUser.id,
                    senderName = currentUser.fullName,
                    senderAvatar = currentUser.avatarUrl,
                    type = "COMMENT",
                    title = "New Comment",
                    message = "${currentUser.fullName} commented: \"${content.take(35)}\"",
                    relatedId = postId
                )
            )
        }
    }

    suspend fun deleteComment(commentId: String) = withContext(Dispatchers.IO) {
        dao.deleteComment(commentId)
    }

    // Stories
    fun getAllStories(): Flow<List<StoryEntity>> = dao.getAllStories()

    suspend fun createStory(currentUser: UserEntity, mediaUrl: String, caption: String, bgGradient: Int) = withContext(Dispatchers.IO) {
        val story = StoryEntity(
            id = "story_${UUID.randomUUID()}",
            userId = currentUser.id,
            userName = currentUser.fullName,
            userAvatar = currentUser.avatarUrl,
            mediaUrl = mediaUrl.trim(),
            caption = caption.trim(),
            bgGradient = bgGradient,
            createdAt = System.currentTimeMillis()
        )
        dao.insertStory(story)
    }

    suspend fun deleteStory(storyId: String) = withContext(Dispatchers.IO) {
        dao.deleteStory(storyId)
    }

    suspend fun viewStory(storyId: String) = withContext(Dispatchers.IO) {
        dao.incrementStoryViews(storyId)
    }

    // Reels
    fun getAllReels(): Flow<List<ReelEntity>> = dao.getAllReels()

    suspend fun toggleReelLike(reel: ReelEntity) = withContext(Dispatchers.IO) {
        val newLiked = !reel.isLiked
        val newCount = if (newLiked) reel.likesCount + 1 else (reel.likesCount - 1).coerceAtLeast(0)
        dao.updateReelLike(reel.id, newLiked, newCount)
    }

    // Messaging
    fun getAllConversations(): Flow<List<ConversationEntity>> = dao.getAllConversations()

    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>> =
        dao.getMessagesForConversation(convId)

    suspend fun sendMessage(
        conversationId: String,
        sender: UserEntity,
        otherUser: UserEntity,
        text: String,
        mediaUrl: String = ""
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val msg = MessageEntity(
            id = "msg_${UUID.randomUUID()}",
            conversationId = conversationId,
            senderId = sender.id,
            senderName = sender.fullName,
            senderAvatar = sender.avatarUrl,
            text = text.trim(),
            mediaUrl = mediaUrl.trim(),
            timestamp = now,
            isRead = true
        )
        dao.insertMessage(msg)

        val conv = ConversationEntity(
            id = conversationId,
            otherUserId = otherUser.id,
            otherUserName = otherUser.fullName,
            otherUserUsername = otherUser.username,
            otherUserAvatar = otherUser.avatarUrl,
            lastMessage = if (text.isNotBlank()) text else "Sent a photo",
            lastTimestamp = now,
            unreadCount = 0
        )
        dao.insertConversation(conv)
    }

    suspend fun clearConversationUnread(convId: String) = withContext(Dispatchers.IO) {
        dao.clearConversationUnread(convId)
    }

    // Friends & Follows
    fun getAllFriendships(): Flow<List<FriendshipEntity>> = dao.getAllFriendships()

    suspend fun sendFriendRequest(sender: UserEntity, targetUser: UserEntity) = withContext(Dispatchers.IO) {
        val id = "${sender.id}_${targetUser.id}"
        dao.insertFriendship(
            FriendshipEntity(
                id = id,
                user1Id = sender.id,
                user2Id = targetUser.id,
                status = "PENDING_U1_TO_U2"
            )
        )
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${UUID.randomUUID()}",
                recipientId = targetUser.id,
                senderId = sender.id,
                senderName = sender.fullName,
                senderAvatar = sender.avatarUrl,
                type = "FRIEND_REQ",
                title = "Friend Request",
                message = "${sender.fullName} sent you a friend request.",
                relatedId = sender.id
            )
        )
    }

    suspend fun acceptFriendRequest(currentUser: UserEntity, requesterId: String) = withContext(Dispatchers.IO) {
        val existing = dao.getFriendship(currentUser.id, requesterId)
        if (existing != null) {
            dao.insertFriendship(existing.copy(status = "FRIENDS"))
            val requester = dao.getUserById(requesterId)
            if (requester != null) {
                dao.insertNotification(
                    NotificationEntity(
                        id = "notif_${UUID.randomUUID()}",
                        recipientId = requesterId,
                        senderId = currentUser.id,
                        senderName = currentUser.fullName,
                        senderAvatar = currentUser.avatarUrl,
                        type = "FRIEND_ACCEPT",
                        title = "Friend Request Accepted",
                        message = "${currentUser.fullName} accepted your friend request!",
                        relatedId = currentUser.id
                    )
                )
            }
        }
    }

    suspend fun removeOrRejectFriend(currentUser: UserEntity, otherUserId: String) = withContext(Dispatchers.IO) {
        dao.deleteFriendship(currentUser.id, otherUserId)
    }

    suspend fun followUser(follower: UserEntity, targetUser: UserEntity) = withContext(Dispatchers.IO) {
        dao.insertFollow(FollowEntity(id = "${follower.id}_${targetUser.id}", followerId = follower.id, followingId = targetUser.id))
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${UUID.randomUUID()}",
                recipientId = targetUser.id,
                senderId = follower.id,
                senderName = follower.fullName,
                senderAvatar = follower.avatarUrl,
                type = "FOLLOW",
                title = "New Follower",
                message = "${follower.fullName} started following you.",
                relatedId = follower.id
            )
        )
    }

    suspend fun unfollowUser(followerId: String, targetUserId: String) = withContext(Dispatchers.IO) {
        dao.deleteFollow(followerId, targetUserId)
    }

    // Groups
    fun getAllGroups(): Flow<List<GroupEntity>> = dao.getAllGroups()

    suspend fun createGroup(
        name: String,
        description: String,
        coverUrl: String,
        category: String,
        isPrivate: Boolean,
        owner: UserEntity
    ) = withContext(Dispatchers.IO) {
        val group = GroupEntity(
            id = "group_${UUID.randomUUID()}",
            name = name.trim(),
            description = description.trim(),
            coverUrl = if (coverUrl.isNotBlank()) coverUrl else "https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=800&q=80",
            category = category,
            isPrivate = isPrivate,
            ownerId = owner.id,
            memberCount = 1,
            isJoined = true
        )
        dao.insertGroup(group)
    }

    suspend fun toggleGroupJoin(group: GroupEntity) = withContext(Dispatchers.IO) {
        val newJoined = !group.isJoined
        val newCount = if (newJoined) group.memberCount + 1 else (group.memberCount - 1).coerceAtLeast(1)
        dao.updateGroupMembership(group.id, newJoined, newCount)
    }

    // Notifications
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>> =
        dao.getNotificationsForUser(userId)

    suspend fun markAllNotificationsRead(userId: String) = withContext(Dispatchers.IO) {
        dao.markAllNotificationsRead(userId)
    }

    // Reporting & Admin
    suspend fun submitReport(
        reporterId: String,
        targetType: String,
        targetId: String,
        targetContent: String,
        reason: String
    ) = withContext(Dispatchers.IO) {
        val report = ReportEntity(
            id = "rep_${UUID.randomUUID()}",
            reporterId = reporterId,
            targetType = targetType,
            targetId = targetId,
            targetContent = targetContent,
            reason = reason,
            status = "PENDING"
        )
        dao.insertReport(report)
    }

    fun getAllReports(): Flow<List<ReportEntity>> = dao.getAllReports()

    suspend fun updateReportStatus(reportId: String, status: String) = withContext(Dispatchers.IO) {
        dao.updateReportStatus(reportId, status)
    }
}

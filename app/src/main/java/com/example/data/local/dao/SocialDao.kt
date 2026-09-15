package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {

    // Users
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY joinedDate DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isBanned = :isBanned WHERE id = :userId")
    suspend fun setUserBanned(userId: String, isBanned: Boolean)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    // Posts
    @Query("SELECT * FROM posts WHERE groupId IS NULL ORDER BY createdAt DESC")
    fun getAllFeedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE authorId = :userId ORDER BY createdAt DESC")
    fun getPostsByUser(userId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getPostsByGroup(groupId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isSaved = 1 ORDER BY createdAt DESC")
    fun getSavedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE hashtags LIKE '%' || :hashtag || '%' ORDER BY createdAt DESC")
    fun getPostsByHashtag(hashtag: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)

    @Query("UPDATE posts SET userReaction = :reaction, likesCount = :likesCount WHERE id = :postId")
    suspend fun updatePostReaction(postId: String, reaction: String, likesCount: Int)

    @Query("UPDATE posts SET userVotedOption = :option, pollVotes1 = :v1, pollVotes2 = :v2 WHERE id = :postId")
    suspend fun votePoll(postId: String, option: Int, v1: Int, v2: Int)

    @Query("UPDATE posts SET isSaved = :isSaved WHERE id = :postId")
    suspend fun setPostSaved(postId: String, isSaved: Boolean)

    @Query("UPDATE posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    suspend fun incrementCommentCount(postId: String)

    @Query("UPDATE posts SET sharesCount = sharesCount + 1 WHERE id = :postId")
    suspend fun incrementShareCount(postId: String)

    // Comments
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)

    // Reactions
    @Query("SELECT * FROM reactions WHERE postId = :postId AND userId = :userId")
    suspend fun getReaction(postId: String, userId: String): ReactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaction(reaction: ReactionEntity)

    @Query("DELETE FROM reactions WHERE postId = :postId AND userId = :userId")
    suspend fun deleteReaction(postId: String, userId: String)

    // Stories
    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("DELETE FROM stories WHERE id = :storyId")
    suspend fun deleteStory(storyId: String)

    @Query("UPDATE stories SET viewsCount = viewsCount + 1 WHERE id = :storyId")
    suspend fun incrementStoryViews(storyId: String)

    // Reels
    @Query("SELECT * FROM reels ORDER BY createdAt DESC")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReels(reels: List<ReelEntity>)

    @Query("UPDATE reels SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :reelId")
    suspend fun updateReelLike(reelId: String, isLiked: Boolean, likesCount: Int)

    // Messages & Conversations
    @Query("SELECT * FROM conversations ORDER BY lastTimestamp DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)

    @Query("UPDATE conversations SET lastMessage = :lastMessage, lastTimestamp = :timestamp WHERE id = :convId")
    suspend fun updateConversationLastMessage(convId: String, lastMessage: String, timestamp: Long)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :convId")
    suspend fun clearConversationUnread(convId: String)

    // Friendships & Follows
    @Query("SELECT * FROM friendships")
    fun getAllFriendships(): Flow<List<FriendshipEntity>>

    @Query("SELECT * FROM friendships WHERE (user1Id = :u1 AND user2Id = :u2) OR (user1Id = :u2 AND user2Id = :u1) LIMIT 1")
    suspend fun getFriendship(u1: String, u2: String): FriendshipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendship(friendship: FriendshipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendships(friendships: List<FriendshipEntity>)

    @Query("DELETE FROM friendships WHERE (user1Id = :u1 AND user2Id = :u2) OR (user1Id = :u2 AND user2Id = :u1)")
    suspend fun deleteFriendship(u1: String, u2: String)

    @Query("SELECT * FROM follows WHERE followerId = :userId")
    fun getFollowsByFollower(userId: String): Flow<List<FollowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollow(follow: FollowEntity)

    @Query("DELETE FROM follows WHERE followerId = :followerId AND followingId = :followingId")
    suspend fun deleteFollow(followerId: String, followingId: String)

    // Groups
    @Query("SELECT * FROM `groups` ORDER BY memberCount DESC")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM `groups` WHERE id = :groupId")
    suspend fun getGroupById(groupId: String): GroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>)

    @Query("UPDATE `groups` SET isJoined = :isJoined, memberCount = :memberCount WHERE id = :groupId")
    suspend fun updateGroupMembership(groupId: String, isJoined: Boolean, memberCount: Int)

    // Notifications
    @Query("SELECT * FROM notifications WHERE recipientId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientId = :userId")
    suspend fun markAllNotificationsRead(userId: String)

    // Reports (Admin)
    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<ReportEntity>)

    @Query("UPDATE reports SET status = :status WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: String, status: String)
}

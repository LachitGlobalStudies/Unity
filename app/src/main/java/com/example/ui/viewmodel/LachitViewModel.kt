package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.FriendshipEntity
import com.example.data.local.entity.GroupEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.ReelEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.StoryEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    FEED,
    REELS,
    MESSAGES,
    CHAT_DETAIL,
    FRIENDS,
    NOTIFICATIONS,
    PROFILE,
    GROUPS,
    SEARCH,
    SETTINGS,
    ADMIN_DASHBOARD,
    AUTH
}

enum class SearchFilter {
    ALL,
    PEOPLE,
    POSTS,
    GROUPS,
    HASHTAGS
}

class LachitViewModel(private val repository: SocialRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.FEED)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilter = MutableStateFlow(SearchFilter.ALL)
    val searchFilter: StateFlow<SearchFilter> = _searchFilter.asStateFlow()

    // Modals & Navigation state
    val isCreatePostOpen = MutableStateFlow(false)
    val isCreateStoryOpen = MutableStateFlow(false)
    val isCreateGroupOpen = MutableStateFlow(false)
    val isEditProfileOpen = MutableStateFlow(false)
    val activeCommentPost = MutableStateFlow<PostEntity?>(null)
    val activeStoryViewer = MutableStateFlow<StoryEntity?>(null)
    val activeChatConversation = MutableStateFlow<ConversationEntity?>(null)
    val activeProfileUser = MutableStateFlow<UserEntity?>(null)
    val selectedHashtag = MutableStateFlow<String?>(null)
    val authError = MutableStateFlow<String?>(null)
    val toastMessage = MutableStateFlow<String?>(null)
    val isAuthLoading = MutableStateFlow(false)
    val isFirebaseConnected = MutableStateFlow(com.example.firebase.FirebaseConfig.isConfigured)

    // Data streams
    val feedPosts: StateFlow<List<PostEntity>> = repository.getAllFeedPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stories: StateFlow<List<StoryEntity>> = repository.getAllStories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reels: StateFlow<List<ReelEntity>> = repository.getAllReels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<ConversationEntity>> = repository.getAllConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friendships: StateFlow<List<FriendshipEntity>> = repository.getAllFriendships()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groups: StateFlow<List<GroupEntity>> = repository.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<ReportEntity>> = repository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPosts: StateFlow<List<PostEntity>> = repository.getSavedPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val activeChatMessages: StateFlow<List<MessageEntity>> = _activeChatMessages.asStateFlow()

    private val _activePostComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val activePostComments: StateFlow<List<CommentEntity>> = _activePostComments.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeIfEmpty()

            // Observe Firebase Auth state
            val authFlow = repository.authStateFlow
            if (authFlow != null) {
                authFlow.collect { fbUser ->
                    if (fbUser != null) {
                        val user = repository.fetchFirebaseUserEntity(fbUser)
                        if (user != null) {
                            _currentUser.value = user
                            activeProfileUser.value = user
                            observeUserNotifications(user.id)
                        }
                    } else if (_currentUser.value == null) {
                        // Fallback to initial local user
                        val defaultUser = repository.getUserById("user_me")
                        _currentUser.value = defaultUser
                        activeProfileUser.value = defaultUser
                        defaultUser?.let { observeUserNotifications(it.id) }
                    }
                }
            } else {
                val user = repository.getUserById("user_me")
                _currentUser.value = user
                activeProfileUser.value = user
                user?.let { observeUserNotifications(it.id) }
            }
        }
    }

    private fun observeUserNotifications(userId: String) {
        viewModelScope.launch {
            repository.getNotificationsForUser(userId).collect { list ->
                _notifications.value = list
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilter(filter: SearchFilter) {
        _searchFilter.value = filter
    }

    fun selectHashtag(tag: String) {
        selectedHashtag.value = tag
        _searchQuery.value = tag
        _searchFilter.value = SearchFilter.HASHTAGS
        navigateTo(Screen.SEARCH)
    }

    fun viewUserProfile(user: UserEntity) {
        activeProfileUser.value = user
        navigateTo(Screen.PROFILE)
    }

    fun openChat(conv: ConversationEntity) {
        activeChatConversation.value = conv
        viewModelScope.launch {
            repository.clearConversationUnread(conv.id)
            repository.getMessagesForConversation(conv.id).collect { messages ->
                _activeChatMessages.value = messages
            }
        }
        navigateTo(Screen.CHAT_DETAIL)
    }

    fun startChatWithUser(otherUser: UserEntity) {
        val current = _currentUser.value ?: return
        val convId = "conv_${listOf(current.id, otherUser.id).sorted().joinToString("_")}"
        val conv = ConversationEntity(
            id = convId,
            otherUserId = otherUser.id,
            otherUserName = otherUser.fullName,
            otherUserUsername = otherUser.username,
            otherUserAvatar = otherUser.avatarUrl,
            lastMessage = "Started a conversation",
            lastTimestamp = System.currentTimeMillis()
        )
        openChat(conv)
    }

    fun openCommentsForPost(post: PostEntity) {
        activeCommentPost.value = post
        viewModelScope.launch {
            repository.getCommentsForPost(post.id).collect { comments ->
                _activePostComments.value = comments
            }
        }
    }

    fun closeComments() {
        activeCommentPost.value = null
    }

    // Auth actions
    fun login(emailOrUser: String, pass: String) {
        authError.value = null
        isAuthLoading.value = true
        viewModelScope.launch {
            val result = repository.login(emailOrUser, pass)
            isAuthLoading.value = false
            result.onSuccess { user ->
                _currentUser.value = user
                activeProfileUser.value = user
                observeUserNotifications(user.id)
                navigateTo(Screen.FEED)
            }.onFailure { error ->
                authError.value = error.message
            }
        }
    }

    fun register(
        name: String,
        username: String,
        email: String,
        pass: String,
        dob: String,
        avatar: String
    ) {
        authError.value = null
        isAuthLoading.value = true
        viewModelScope.launch {
            val result = repository.register(name, username, email, pass, dob, avatar)
            isAuthLoading.value = false
            result.onSuccess { user ->
                _currentUser.value = user
                activeProfileUser.value = user
                observeUserNotifications(user.id)
                navigateTo(Screen.FEED)
            }.onFailure { error ->
                authError.value = error.message
            }
        }
    }

    fun sendPasswordReset(email: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.sendPasswordReset(email)
            result.onSuccess {
                onComplete(true, "Password reset instructions sent to $email. Please check your inbox.")
            }.onFailure { error ->
                onComplete(false, error.message ?: "Failed to send reset email.")
            }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        navigateTo(Screen.AUTH)
    }

    fun switchAccount(userId: String) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            if (user != null) {
                _currentUser.value = user
                activeProfileUser.value = user
                observeUserNotifications(user.id)
                toastMessage.value = "Switched to @${user.username}"
            }
        }
    }

    // Post actions
    fun createPost(
        content: String,
        mediaUrl: String,
        feeling: String,
        location: String,
        privacy: String,
        bgGradientIndex: Int,
        pollQ: String,
        poll1: String,
        poll2: String,
        groupId: String? = null,
        groupName: String? = null
    ) {
        val author = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createPost(
                author,
                content,
                mediaUrl,
                feeling,
                location,
                privacy,
                bgGradientIndex,
                pollQ,
                poll1,
                poll2,
                groupId,
                groupName
            )
            isCreatePostOpen.value = false
            toastMessage.value = "Post published successfully!"
        }
    }

    fun reactToPost(post: PostEntity, reactionType: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.reactToPost(post, user, reactionType)
        }
    }

    fun votePoll(post: PostEntity, option: Int) {
        viewModelScope.launch {
            repository.votePoll(post, option)
        }
    }

    fun toggleSavePost(post: PostEntity) {
        viewModelScope.launch {
            repository.toggleSavePost(post.id, post.isSaved)
            toastMessage.value = if (!post.isSaved) "Post saved to bookmarks!" else "Post removed from bookmarks."
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
            toastMessage.value = "Post deleted."
        }
    }

    fun sharePost(post: PostEntity) {
        viewModelScope.launch {
            repository.sharePost(post.id)
            toastMessage.value = "Link copied! Post shared to your network."
        }
    }

    // Comments
    fun addComment(postId: String, postAuthorId: String, content: String, parentId: String? = null) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.addComment(postId, postAuthorId, user, content, parentId)
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
        }
    }

    // Story actions
    fun createStory(mediaUrl: String, caption: String, bgGradient: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createStory(user, mediaUrl, caption, bgGradient)
            isCreateStoryOpen.value = false
            toastMessage.value = "Story added! Visible for 24 hours."
        }
    }

    fun openStoryViewer(story: StoryEntity) {
        activeStoryViewer.value = story
        viewModelScope.launch {
            repository.viewStory(story.id)
        }
    }

    fun closeStoryViewer() {
        activeStoryViewer.value = null
    }

    // Reel actions
    fun toggleReelLike(reel: ReelEntity) {
        viewModelScope.launch {
            repository.toggleReelLike(reel)
        }
    }

    // Chat actions
    fun sendMessage(text: String, mediaUrl: String = "") {
        val sender = _currentUser.value ?: return
        val conv = activeChatConversation.value ?: return
        viewModelScope.launch {
            val other = repository.getUserById(conv.otherUserId) ?: return@launch
            repository.sendMessage(conv.id, sender, other, text, mediaUrl)
        }
    }

    // Friend & Follow
    fun sendFriendRequest(targetUser: UserEntity) {
        val me = _currentUser.value ?: return
        viewModelScope.launch {
            repository.sendFriendRequest(me, targetUser)
            toastMessage.value = "Friend request sent to ${targetUser.fullName}"
        }
    }

    fun acceptFriendRequest(requesterId: String) {
        val me = _currentUser.value ?: return
        viewModelScope.launch {
            repository.acceptFriendRequest(me, requesterId)
            toastMessage.value = "Friend request accepted!"
        }
    }

    fun rejectOrRemoveFriend(otherUserId: String) {
        val me = _currentUser.value ?: return
        viewModelScope.launch {
            repository.removeOrRejectFriend(me, otherUserId)
            toastMessage.value = "Removed from friends."
        }
    }

    fun followUser(targetUser: UserEntity) {
        val me = _currentUser.value ?: return
        viewModelScope.launch {
            repository.followUser(me, targetUser)
            toastMessage.value = "Now following ${targetUser.fullName}!"
        }
    }

    fun unfollowUser(targetUserId: String) {
        val me = _currentUser.value ?: return
        viewModelScope.launch {
            repository.unfollowUser(me.id, targetUserId)
            toastMessage.value = "Unfollowed user."
        }
    }

    // Groups
    fun createGroup(name: String, desc: String, coverUrl: String, category: String, isPrivate: Boolean) {
        val me = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createGroup(name, desc, coverUrl, category, isPrivate, me)
            isCreateGroupOpen.value = false
            toastMessage.value = "Group '$name' created successfully!"
        }
    }

    fun toggleGroupJoin(group: GroupEntity) {
        viewModelScope.launch {
            repository.toggleGroupJoin(group)
            toastMessage.value = if (!group.isJoined) "Joined ${group.name}!" else "Left ${group.name}."
        }
    }

    // Profile updates
    fun updateUserProfile(
        name: String,
        bio: String,
        location: String,
        website: String,
        avatarUrl: String,
        coverUrl: String
    ) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            fullName = name.trim(),
            bio = bio.trim(),
            location = location.trim(),
            website = website.trim(),
            avatarUrl = if (avatarUrl.isNotBlank()) avatarUrl.trim() else current.avatarUrl,
            coverUrl = if (coverUrl.isNotBlank()) coverUrl.trim() else current.coverUrl
        )
        viewModelScope.launch {
            repository.updateUserProfile(updated)
            _currentUser.value = updated
            activeProfileUser.value = updated
            isEditProfileOpen.value = false
            toastMessage.value = "Profile updated successfully!"
        }
    }

    // Notifications
    fun markAllNotificationsRead() {
        val me = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsRead(me.id)
            toastMessage.value = "All notifications marked as read."
        }
    }

    // Report
    fun reportContent(type: String, id: String, content: String, reason: String) {
        val me = _currentUser.value ?: return
        viewModelScope.launch {
            repository.submitReport(me.id, type, id, content, reason)
            toastMessage.value = "Thank you. Your report has been sent to the moderation team."
        }
    }

    // Admin operations
    fun adminBanUser(user: UserEntity) {
        val me = _currentUser.value ?: return
        if (me.role != "ADMIN") return
        viewModelScope.launch {
            val newBanned = !user.isBanned
            repository.banUser(user.id, newBanned)
            toastMessage.value = if (newBanned) "User @${user.username} suspended." else "User @${user.username} unsuspended."
        }
    }

    fun adminDeleteUser(userId: String) {
        val me = _currentUser.value ?: return
        if (me.role != "ADMIN") return
        viewModelScope.launch {
            repository.deleteUser(userId)
            toastMessage.value = "User account permanently removed."
        }
    }

    fun adminUpdateReport(reportId: String, status: String) {
        val me = _currentUser.value ?: return
        if (me.role != "ADMIN") return
        viewModelScope.launch {
            repository.updateReportStatus(reportId, status)
            toastMessage.value = "Report status updated to $status."
        }
    }

    fun clearToast() {
        toastMessage.value = null
    }

    class Factory(private val repository: SocialRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LachitViewModel(repository) as T
        }
    }
}

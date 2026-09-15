package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.LachitDatabase
import com.example.data.local.entity.PostEntity
import com.example.data.repository.SocialRepository
import com.example.firebase.FirebaseAuthService
import com.example.firebase.FirebaseConfig
import com.example.ui.components.CommentSheetModal
import com.example.ui.components.CreatePostModal
import com.example.ui.components.LachitBottomNavigationBar
import com.example.ui.components.LachitTopAppBar
import com.example.ui.components.ReportDialog
import com.example.ui.components.StoryViewerModal
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.CreateGroupDialog
import com.example.ui.screens.EditProfileDialog
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.GroupsScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.LachitSocialTheme
import com.example.ui.viewmodel.LachitViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseConfig.initialize(applicationContext)

        val db = LachitDatabase.getDatabase(this)
        val authService = FirebaseAuthService(
            auth = FirebaseConfig.auth,
            firestore = FirebaseConfig.firestore,
            localDao = db.socialDao()
        )
        val repository = SocialRepository(db.socialDao(), authService)
        val factory = LachitViewModel.Factory(repository)

        setContent {
            val viewModel: LachitViewModel = viewModel(factory = factory)
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            LachitSocialTheme(darkTheme = isDarkMode) {
                LachitApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LachitApp(viewModel: LachitViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchFilter by viewModel.searchFilter.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    // Data streams
    val feedPosts by viewModel.feedPosts.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val reels by viewModel.reels.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val friendships by viewModel.friendships.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val reports by viewModel.reports.collectAsState()
    val savedPosts by viewModel.savedPosts.collectAsState()

    val activeCommentPost by viewModel.activeCommentPost.collectAsState()
    val activePostComments by viewModel.activePostComments.collectAsState()
    val activeStoryViewer by viewModel.activeStoryViewer.collectAsState()
    val activeChatConversation by viewModel.activeChatConversation.collectAsState()
    val activeChatMessages by viewModel.activeChatMessages.collectAsState()
    val activeProfileUser by viewModel.activeProfileUser.collectAsState()

    val isCreatePostOpen by viewModel.isCreatePostOpen.collectAsState()
    val isCreateStoryOpen by viewModel.isCreateStoryOpen.collectAsState()
    val isCreateGroupOpen by viewModel.isCreateGroupOpen.collectAsState()
    val isEditProfileOpen by viewModel.isEditProfileOpen.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val isFirebaseConnected by viewModel.isFirebaseConnected.collectAsState()

    var reportTarget by remember { mutableStateOf<Pair<String, PostEntity>?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    val unreadNotificationsCount = notifications.count { !it.isRead }
    val unreadMessagesCount = conversations.sumOf { it.unreadCount }

    val showBars = currentScreen in listOf(
        Screen.FEED,
        Screen.REELS,
        Screen.FRIENDS,
        Screen.GROUPS,
        Screen.PROFILE,
        Screen.SEARCH
    ) && currentUser != null

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (showBars) {
                LachitTopAppBar(
                    currentUser = currentUser,
                    isDarkMode = isDarkMode,
                    unreadNotifications = unreadNotificationsCount,
                    unreadMessages = unreadMessagesCount,
                    onSearchClick = { viewModel.navigateTo(Screen.SEARCH) },
                    onNotificationsClick = { viewModel.navigateTo(Screen.NOTIFICATIONS) },
                    onMessagesClick = { viewModel.navigateTo(Screen.MESSAGES) },
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onAdminClick = { viewModel.navigateTo(Screen.ADMIN_DASHBOARD) }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                LachitBottomNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                currentUser == null || currentScreen == Screen.AUTH -> {
                    AuthScreen(
                        errorMessage = authError,
                        demoUsers = allUsers,
                        onLogin = { email, pass -> viewModel.login(email, pass) },
                        onRegister = { name, username, email, pass, dob, avatar ->
                            viewModel.register(name, username, email, pass, dob, avatar)
                        },
                        onSelectDemoUser = { u -> viewModel.switchAccount(u.id) },
                        onForgotPassword = { email ->
                            viewModel.sendPasswordReset(email) { _, msg ->
                                viewModel.toastMessage.value = msg
                            }
                        },
                        isLoading = isAuthLoading,
                        isFirebaseConnected = isFirebaseConnected
                    )
                }
                currentScreen == Screen.FEED -> {
                    FeedScreen(
                        currentUser = currentUser,
                        stories = stories,
                        posts = feedPosts,
                        onStoryClick = { viewModel.openStoryViewer(it) },
                        onCreateStoryClick = { viewModel.isCreateStoryOpen.value = true },
                        onCreatePostClick = { viewModel.isCreatePostOpen.value = true },
                        onReact = { post, type -> viewModel.reactToPost(post, type) },
                        onVotePoll = { post, option -> viewModel.votePoll(post, option) },
                        onOpenComments = { post -> viewModel.openCommentsForPost(post) },
                        onShare = { post -> viewModel.sharePost(post) },
                        onToggleSave = { post -> viewModel.toggleSavePost(post) },
                        onDeletePost = { postId -> viewModel.deletePost(postId) },
                        onReportPost = { post -> reportTarget = "POST" to post },
                        onUserClick = { userId ->
                            val user = allUsers.find { it.id == userId }
                            if (user != null) viewModel.viewUserProfile(user)
                        },
                        onHashtagClick = { tag -> viewModel.selectHashtag(tag) }
                    )
                }
                currentScreen == Screen.REELS -> {
                    ReelsScreen(
                        reels = reels,
                        onToggleLike = { viewModel.toggleReelLike(it) },
                        onShare = { viewModel.toastMessage.value = "Reel link copied to clipboard!" },
                        onUserClick = { creatorId ->
                            val user = allUsers.find { it.id == creatorId }
                            if (user != null) viewModel.viewUserProfile(user)
                        }
                    )
                }
                currentScreen == Screen.MESSAGES -> {
                    MessagesScreen(
                        conversations = conversations,
                        allUsers = allUsers.filter { it.id != currentUser?.id },
                        onSelectConversation = { viewModel.openChat(it) },
                        onStartChat = { viewModel.startChatWithUser(it) }
                    )
                }
                currentScreen == Screen.CHAT_DETAIL && activeChatConversation != null -> {
                    ChatDetailScreen(
                        conversation = activeChatConversation!!,
                        messages = activeChatMessages,
                        currentUser = currentUser,
                        onBack = { viewModel.navigateTo(Screen.MESSAGES) },
                        onSendMessage = { text, media -> viewModel.sendMessage(text, media) }
                    )
                }
                currentScreen == Screen.FRIENDS -> {
                    FriendsScreen(
                        currentUser = currentUser,
                        allUsers = allUsers,
                        friendships = friendships,
                        onAcceptRequest = { viewModel.acceptFriendRequest(it) },
                        onRejectRequest = { viewModel.rejectOrRemoveFriend(it) },
                        onSendRequest = { viewModel.sendFriendRequest(it) },
                        onStartChat = { viewModel.startChatWithUser(it) },
                        onUserClick = { viewModel.viewUserProfile(it) }
                    )
                }
                currentScreen == Screen.GROUPS -> {
                    GroupsScreen(
                        groups = groups,
                        onToggleJoin = { viewModel.toggleGroupJoin(it) },
                        onCreateGroupClick = { viewModel.isCreateGroupOpen.value = true }
                    )
                }
                currentScreen == Screen.PROFILE -> {
                    val profileUser = activeProfileUser ?: currentUser!!
                    val userPosts = feedPosts.filter { it.authorId == profileUser.id }
                    ProfileScreen(
                        user = profileUser,
                        currentUser = currentUser,
                        userPosts = userPosts,
                        savedPosts = savedPosts,
                        onEditProfileClick = { viewModel.isEditProfileOpen.value = true },
                        onSettingsClick = { viewModel.navigateTo(Screen.SETTINGS) },
                        onStartChat = { viewModel.startChatWithUser(it) },
                        onSendFriendRequest = { viewModel.sendFriendRequest(it) },
                        onReact = { post, type -> viewModel.reactToPost(post, type) },
                        onVotePoll = { post, option -> viewModel.votePoll(post, option) },
                        onOpenComments = { post -> viewModel.openCommentsForPost(post) },
                        onShare = { post -> viewModel.sharePost(post) },
                        onToggleSave = { post -> viewModel.toggleSavePost(post) },
                        onDeletePost = { postId -> viewModel.deletePost(postId) },
                        onReportPost = { post -> reportTarget = "POST" to post },
                        onHashtagClick = { tag -> viewModel.selectHashtag(tag) }
                    )
                }
                currentScreen == Screen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onMarkAllRead = { viewModel.markAllNotificationsRead() },
                        onNotificationClick = {
                            if (it.type == "FRIEND_REQ") {
                                viewModel.navigateTo(Screen.FRIENDS)
                            } else {
                                viewModel.navigateTo(Screen.FEED)
                            }
                        }
                    )
                }
                currentScreen == Screen.SEARCH -> {
                    SearchScreen(
                        query = searchQuery,
                        filter = searchFilter,
                        allUsers = allUsers,
                        allPosts = feedPosts,
                        allGroups = groups,
                        currentUser = currentUser,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        onFilterChange = { viewModel.setSearchFilter(it) },
                        onUserClick = { viewModel.viewUserProfile(it) },
                        onPostReact = { post, type -> viewModel.reactToPost(post, type) },
                        onPostComment = { post -> viewModel.openCommentsForPost(post) },
                        onGroupClick = { viewModel.navigateTo(Screen.GROUPS) },
                        onHashtagClick = { tag -> viewModel.selectHashtag(tag) }
                    )
                }
                currentScreen == Screen.SETTINGS -> {
                    SettingsScreen(
                        currentUser = currentUser,
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onBack = { viewModel.navigateTo(Screen.PROFILE) },
                        onLogout = { viewModel.logout() },
                        onSwitchUser = { viewModel.switchAccount(it) },
                        onEditProfile = { viewModel.isEditProfileOpen.value = true }
                    )
                }
                currentScreen == Screen.ADMIN_DASHBOARD -> {
                    AdminDashboardScreen(
                        users = allUsers,
                        posts = feedPosts,
                        reports = reports,
                        onBack = { viewModel.navigateTo(Screen.FEED) },
                        onBanUser = { viewModel.adminBanUser(it) },
                        onDeleteUser = { viewModel.adminDeleteUser(it) },
                        onUpdateReport = { repId, st -> viewModel.adminUpdateReport(repId, st) }
                    )
                }
            }

            // --- Overlays & Modals ---

            // Active Comments Sheet
            if (activeCommentPost != null) {
                CommentSheetModal(
                    post = activeCommentPost!!,
                    comments = activePostComments,
                    currentUser = currentUser,
                    onClose = { viewModel.closeComments() },
                    onAddComment = { pId, aId, content, parentId ->
                        viewModel.addComment(pId, aId, content, parentId)
                    },
                    onDeleteComment = { viewModel.deleteComment(it) }
                )
            }

            // Story Viewer Modal
            if (activeStoryViewer != null) {
                StoryViewerModal(
                    story = activeStoryViewer!!,
                    allStories = stories,
                    onClose = { viewModel.closeStoryViewer() }
                )
            }

            // Create Post Modal
            if (isCreatePostOpen) {
                CreatePostModal(
                    currentUser = currentUser,
                    onDismiss = { viewModel.isCreatePostOpen.value = false },
                    onSubmit = { content, media, feel, loc, priv, grad, q, o1, o2 ->
                        viewModel.createPost(content, media, feel, loc, priv, grad, q, o1, o2)
                    }
                )
            }

            // Create Story Dialog
            if (isCreateStoryOpen) {
                CreatePostModal(
                    currentUser = currentUser,
                    onDismiss = { viewModel.isCreateStoryOpen.value = false },
                    onSubmit = { content, media, _, _, _, grad, _, _, _ ->
                        viewModel.createStory(media, content, grad)
                    }
                )
            }

            // Create Group Dialog
            if (isCreateGroupOpen) {
                CreateGroupDialog(
                    onDismiss = { viewModel.isCreateGroupOpen.value = false },
                    onCreate = { name, desc, cover, cat, priv ->
                        viewModel.createGroup(name, desc, cover, cat, priv)
                    }
                )
            }

            // Edit Profile Dialog
            if (isEditProfileOpen && currentUser != null) {
                EditProfileDialog(
                    user = currentUser!!,
                    onDismiss = { viewModel.isEditProfileOpen.value = false },
                    onSave = { name, bio, loc, web, avatar, cover ->
                        viewModel.updateUserProfile(name, bio, loc, web, avatar, cover)
                    }
                )
            }

            // Report Dialog
            reportTarget?.let { (type, post) ->
                ReportDialog(
                    targetType = type,
                    onDismiss = { reportTarget = null },
                    onSubmitReport = { reason ->
                        viewModel.reportContent(type, post.id, post.content, reason)
                        reportTarget = null
                    }
                )
            }
        }
    }
}

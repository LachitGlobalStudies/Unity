package com.example.data.seed

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

object SeedData {
    val initialUsers = listOf(
        UserEntity(
            id = "user_me",
            username = "alex_dev",
            fullName = "Alex Borgohain",
            email = "alex@lachit.social",
            passwordHash = "password123",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
            coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1200&q=80",
            bio = "Full-stack builder & tech enthusiast 🚀 Exploring the future of open connected social networks. #LachitSocial",
            location = "Guwahati, Assam",
            website = "https://lachit.social/alex",
            dob = "1998-04-14",
            joinedDate = "March 2026",
            friendsCount = 124,
            followersCount = 890,
            followingCount = 210,
            isPrivate = false,
            role = "USER"
        ),
        UserEntity(
            id = "user_admin",
            username = "admin",
            fullName = "Lachit Admin",
            email = "admin@lachit.social",
            passwordHash = "admin123",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80",
            coverUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?auto=format&fit=crop&w=1200&q=80",
            bio = "Official Lachit Social Security & Community Trust Admin account 🛡️ Keeping our platform safe, authentic, and fast.",
            location = "Global",
            website = "https://lachit.social",
            dob = "1990-01-01",
            joinedDate = "January 2026",
            friendsCount = 500,
            followersCount = 12500,
            followingCount = 45,
            isPrivate = false,
            role = "ADMIN"
        ),
        UserEntity(
            id = "user_priya",
            username = "priya_sharma",
            fullName = "Priya Sharma",
            email = "priya@example.com",
            passwordHash = "password123",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80",
            coverUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80",
            bio = "Visual designer & UI architect ✨ Passionate about typography, clean ergonomics, and micro-interactions.",
            location = "Bengaluru, India",
            website = "https://priyadesign.io",
            dob = "1999-08-22",
            joinedDate = "February 2026",
            friendsCount = 340,
            followersCount = 2140,
            followingCount = 310,
            isPrivate = false,
            role = "USER"
        ),
        UserEntity(
            id = "user_rahul",
            username = "rahul_verma",
            fullName = "Rahul Verma",
            email = "rahul@example.com",
            passwordHash = "password123",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=400&q=80",
            coverUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1200&q=80",
            bio = "Cloud Architect & distributed systems geek ☁️ Building scalable backends with Firebase & Kotlin.",
            location = "Hyderabad, India",
            website = "https://rahulv.dev",
            dob = "1995-11-03",
            joinedDate = "February 2026",
            friendsCount = 210,
            followersCount = 1450,
            followingCount = 180,
            isPrivate = false,
            role = "USER"
        ),
        UserEntity(
            id = "user_ananya",
            username = "ananya_k",
            fullName = "Ananya Kalita",
            email = "ananya@example.com",
            passwordHash = "password123",
            avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80",
            coverUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=80",
            bio = "Nature photographer, tea enthusiast, and heritage conservationist 🌿 Living between mountains and stories.",
            location = "Jorhat, Assam",
            website = "https://ananyaphotos.com",
            dob = "2000-02-18",
            joinedDate = "March 2026",
            friendsCount = 180,
            followersCount = 3200,
            followingCount = 290,
            isPrivate = false,
            role = "USER"
        )
    )

    val initialPosts = listOf(
        PostEntity(
            id = "post_1",
            authorId = "user_ananya",
            authorName = "Ananya Kalita",
            authorUsername = "ananya_k",
            authorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80",
            content = "Sunset over the mighty Brahmaputra river today. The sky turned into liquid gold! 🌅 There is something so humbling about nature's grand canvas. #Nature #Assam #LachitSocial #Photography",
            mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1000&q=80",
            feeling = "blessed",
            location = "Brahmaputra Riverfront, Guwahati",
            privacy = "PUBLIC",
            likesCount = 42,
            commentsCount = 3,
            sharesCount = 6,
            hashtags = "#Nature,#Assam,#LachitSocial,#Photography",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 35
        ),
        PostEntity(
            id = "post_2",
            authorId = "user_rahul",
            authorName = "Rahul Verma",
            authorUsername = "rahul_verma",
            authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=400&q=80",
            content = "Quick tech poll for our developer community: Which architecture model do you prefer for high-scale social feeds?",
            pollQuestion = "Preferred Architecture for Real-time Feeds?",
            pollOption1 = "Event-driven with CQRS & Cache",
            pollOption2 = "Direct Stream with Document Sharding",
            pollVotes1 = 28,
            pollVotes2 = 14,
            userVotedOption = 1,
            feeling = "thinking",
            privacy = "PUBLIC",
            likesCount = 19,
            commentsCount = 5,
            sharesCount = 2,
            hashtags = "#Technology,#Architecture,#Engineering",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 120
        ),
        PostEntity(
            id = "post_3",
            authorId = "user_priya",
            authorName = "Priya Sharma",
            authorUsername = "priya_sharma",
            authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80",
            content = "Welcome everyone to Lachit Social! 💙 Designed with love, authentic community spirit, and speed in mind. Let us build meaningful connections!",
            backgroundGradientIndex = 2, // vibrant gradient banner
            feeling = "excited",
            privacy = "PUBLIC",
            likesCount = 85,
            commentsCount = 12,
            sharesCount = 14,
            hashtags = "#LachitSocial,#Design,#Community",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 300
        )
    )

    val initialComments = listOf(
        CommentEntity(
            id = "comment_1",
            postId = "post_1",
            authorId = "user_priya",
            authorName = "Priya Sharma",
            authorUsername = "priya_sharma",
            authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80",
            content = "Absolutely breathtaking view Ananya! The colors are magical! 😍",
            likesCount = 4,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 20
        ),
        CommentEntity(
            id = "comment_2",
            postId = "post_1",
            authorId = "user_me",
            authorName = "Alex Borgohain",
            authorUsername = "alex_dev",
            authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
            content = "Incredible capture! What camera lens did you use?",
            parentCommentId = "comment_1", // nested reply!
            likesCount = 2,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 10
        ),
        CommentEntity(
            id = "comment_3",
            postId = "post_1",
            authorId = "user_ananya",
            authorName = "Ananya Kalita",
            authorUsername = "ananya_k",
            authorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80",
            content = "Thanks Alex! 35mm f/1.4 prime lens during golden hour ✨",
            parentCommentId = "comment_1",
            likesCount = 1,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 5
        )
    )

    val initialStories = listOf(
        StoryEntity(
            id = "story_me",
            userId = "user_me",
            userName = "Your Story",
            userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
            mediaUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=600&q=80",
            caption = "Shipping new features for Lachit Social! 💻🔥",
            bgGradient = 1,
            viewsCount = 48,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2
        ),
        StoryEntity(
            id = "story_priya",
            userId = "user_priya",
            userName = "Priya Sharma",
            userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80",
            mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=600&q=80",
            caption = "Design sprint workshop in progress 🎨",
            bgGradient = 2,
            viewsCount = 92,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 4
        ),
        StoryEntity(
            id = "story_ananya",
            userId = "user_ananya",
            userName = "Ananya Kalita",
            userAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80",
            mediaUrl = "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?auto=format&fit=crop&w=600&q=80",
            caption = "Morning mist in Kaziranga 🦏🌿",
            bgGradient = 3,
            viewsCount = 145,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 6
        )
    )

    val initialReels = listOf(
        ReelEntity(
            id = "reel_1",
            creatorId = "user_ananya",
            creatorName = "Ananya Kalita",
            creatorUsername = "ananya_k",
            creatorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80",
            caption = "Exploring hidden waterfalls deep inside Meghalaya! The sound of rushing water is pure serenity. 🌊✨",
            videoThumbnailUrl = "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?auto=format&fit=crop&w=600&q=80",
            audioTitle = "Original Audio - Ananya Nature Beats",
            likesCount = 1240,
            commentsCount = 68,
            viewsCount = 14500,
            isLiked = false
        ),
        ReelEntity(
            id = "reel_2",
            creatorId = "user_priya",
            creatorName = "Priya Sharma",
            creatorUsername = "priya_sharma",
            creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80",
            caption = "Behind the scenes: 5 design tips for crafting intuitive mobile micro-interactions in 2026. 📱🎨",
            videoThumbnailUrl = "https://images.unsplash.com/photo-1581291518655-9523c932edcf?auto=format&fit=crop&w=600&q=80",
            audioTitle = "Lo-Fi Beats - Design Vibes #04",
            likesCount = 890,
            commentsCount = 42,
            viewsCount = 9200,
            isLiked = true
        )
    )

    val initialConversations = listOf(
        ConversationEntity(
            id = "conv_priya",
            otherUserId = "user_priya",
            otherUserName = "Priya Sharma",
            otherUserUsername = "priya_sharma",
            otherUserAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80",
            lastMessage = "Hey Alex! Loved your new update on Lachit Social!",
            lastTimestamp = System.currentTimeMillis() - 1000 * 60 * 15,
            isOnline = true,
            unreadCount = 1
        ),
        ConversationEntity(
            id = "conv_rahul",
            otherUserId = "user_rahul",
            otherUserName = "Rahul Verma",
            otherUserUsername = "rahul_verma",
            otherUserAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=400&q=80",
            lastMessage = "Did you check the Firestore index optimization specs?",
            lastTimestamp = System.currentTimeMillis() - 1000 * 60 * 120,
            isOnline = false,
            unreadCount = 0
        )
    )

    val initialMessages = listOf(
        MessageEntity(
            id = "msg_1",
            conversationId = "conv_priya",
            senderId = "user_priya",
            senderName = "Priya Sharma",
            senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80",
            text = "Hi Alex! How is the development going?",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
            isRead = true
        ),
        MessageEntity(
            id = "msg_2",
            conversationId = "conv_priya",
            senderId = "user_me",
            senderName = "Alex Borgohain",
            senderAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
            text = "Hey Priya! Going super well, just finishing the messaging and stories system.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
            isRead = true
        ),
        MessageEntity(
            id = "msg_3",
            conversationId = "conv_priya",
            senderId = "user_priya",
            senderName = "Priya Sharma",
            senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80",
            text = "Hey Alex! Loved your new update on Lachit Social!",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
            isRead = false
        )
    )

    val initialGroups = listOf(
        GroupEntity(
            id = "group_tech",
            name = "Tech Innovators Assam",
            description = "Community for developers, architects, designers, and AI engineers across Northeast India and beyond.",
            coverUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=800&q=80",
            category = "Technology",
            isPrivate = false,
            ownerId = "user_rahul",
            memberCount = 1420,
            isJoined = true
        ),
        GroupEntity(
            id = "group_photo",
            name = "Wildlife & Landscape Photographers",
            description = "Sharing high-resolution photography, lens recommendations, and nature storytelling.",
            coverUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=800&q=80",
            category = "Art & Photography",
            isPrivate = false,
            ownerId = "user_ananya",
            memberCount = 3840,
            isJoined = false
        )
    )

    val initialFriendships = listOf(
        FriendshipEntity(
            id = "user_me_user_priya",
            user1Id = "user_me",
            user2Id = "user_priya",
            status = "FRIENDS"
        ),
        FriendshipEntity(
            id = "user_me_user_rahul",
            user1Id = "user_me",
            user2Id = "user_rahul",
            status = "FRIENDS"
        ),
        FriendshipEntity(
            id = "user_ananya_user_me",
            user1Id = "user_ananya",
            user2Id = "user_me",
            status = "PENDING_U1_TO_U2" // incoming request to user_me
        )
    )

    val initialNotifications = listOf(
        NotificationEntity(
            id = "notif_1",
            recipientId = "user_me",
            senderId = "user_ananya",
            senderName = "Ananya Kalita",
            senderAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80",
            type = "FRIEND_REQ",
            title = "Friend Request",
            message = "Ananya Kalita sent you a friend request.",
            relatedId = "user_ananya",
            isRead = false,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 45
        ),
        NotificationEntity(
            id = "notif_2",
            recipientId = "user_me",
            senderId = "user_priya",
            senderName = "Priya Sharma",
            senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80",
            type = "POST_REACTION",
            title = "Reaction",
            message = "Priya reacted with ❤️ to your story.",
            relatedId = "story_me",
            isRead = false,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 75
        )
    )

    val initialReports = listOf(
        ReportEntity(
            id = "rep_1",
            reporterId = "user_rahul",
            targetType = "POST",
            targetId = "post_spam_test",
            targetContent = "Click here for guaranteed cryptocurrency doubling in 24 hours! Free money!",
            reason = "Spam / Fraudulent link",
            status = "PENDING",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 180
        )
    )
}

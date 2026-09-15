package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        ReactionEntity::class,
        StoryEntity::class,
        ReelEntity::class,
        MessageEntity::class,
        ConversationEntity::class,
        FriendshipEntity::class,
        FollowEntity::class,
        GroupEntity::class,
        NotificationEntity::class,
        ReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LachitDatabase : RoomDatabase() {
    abstract fun socialDao(): SocialDao

    companion object {
        @Volatile
        private var INSTANCE: LachitDatabase? = null

        fun getDatabase(context: Context): LachitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LachitDatabase::class.java,
                    "lachit_social.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

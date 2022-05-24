package com.automotivecodelab.rtclient.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.automotivecodelab.featurefavoritesimpl.data.FavoriteDatabaseModel
import com.automotivecodelab.featurefavoritesimpl.data.FavoritesDatabase
import com.automotivecodelab.featurerssfeeds.data.RssChannelDatabase
import com.automotivecodelab.featurerssfeeds.data.RssChannelDatabaseModel

@Database(
    entities = [RssChannelDatabaseModel::class, FavoriteDatabaseModel::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppDatabase : RoomDatabase(), RssChannelDatabase, FavoritesDatabase {
    companion object {
        private const val DATABASE_NAME = "db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context)
                    .also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                .build()
    }
}

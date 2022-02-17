package com.automotivecodelab.rtclient.di

import android.content.Context
import com.automotivecodelab.featurerssfeeds.data.RssChannelDao
import com.automotivecodelab.rtclient.data.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Singleton
    @Provides
    fun provideRoomDb(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideRssChannelDao(db: AppDatabase): RssChannelDao {
        return db.rssChannelDao()
    }
}

package com.leehendryp.maytheforcebewithleehendry.feed.data.di

import android.content.Context
import com.leehendryp.maytheforcebewithleehendry.core.STAR_WARS_BASE_URL
import com.leehendryp.maytheforcebewithleehendry.core.StarWarsApi
import com.leehendryp.maytheforcebewithleehendry.core.WEBHOOK_BASE_URL
import com.leehendryp.maytheforcebewithleehendry.core.WebhookApi
import com.leehendryp.maytheforcebewithleehendry.core.utils.NetworkUtils
import com.leehendryp.maytheforcebewithleehendry.feed.data.LocalDataSource
import com.leehendryp.maytheforcebewithleehendry.feed.data.remote.RemoteDataSource
import com.leehendryp.maytheforcebewithleehendry.feed.data.PeopleRepositoryImpl
import com.leehendryp.maytheforcebewithleehendry.feed.data.local.LocalDataSourceImpl
import com.leehendryp.maytheforcebewithleehendry.feed.data.local.RoomPeopleDao
import com.leehendryp.maytheforcebewithleehendry.feed.data.local.database.RoomPeopleDatabase
import com.leehendryp.maytheforcebewithleehendry.feed.data.local.database.RoomPeopleDatabase.Companion.getDatabase
import com.leehendryp.maytheforcebewithleehendry.feed.data.remote.RemoteDataSourceImpl
import com.leehendryp.maytheforcebewithleehendry.feed.domain.PeopleRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class DataModule {
    @Singleton
    @Provides
    fun provideRoomDatabase(context: Context): RoomPeopleDatabase = getDatabase(context)

    @Singleton
    @Provides
    fun provideRoomDao(database: RoomPeopleDatabase): RoomPeopleDao = database.roomPeopleDao()

    @Provides
    fun provideLocalDataSource(dao: RoomPeopleDao): LocalDataSource = LocalDataSourceImpl(dao)

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    @Singleton
    @Provides
    fun provideStarWarsNetworkService(okHttpClient: OkHttpClient): StarWarsApi = Retrofit.Builder()
        .baseUrl(STAR_WARS_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(StarWarsApi::class.java)

    @Singleton
    @Provides
    fun provideWebhookNetworkService(okHttpClient: OkHttpClient): WebhookApi = Retrofit.Builder()
        .baseUrl(WEBHOOK_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WebhookApi::class.java)

    @Provides
    fun provideRemoteDataSource(
        starWarsApi: StarWarsApi,
        webhookApi: WebhookApi
    ): RemoteDataSource = RemoteDataSourceImpl(starWarsApi, webhookApi)

    @Provides
    fun provideNetworkUtils(context: Context): NetworkUtils = NetworkUtils(context)

    @Singleton
    @Provides
    fun provideRepository(
        networkUtils: NetworkUtils,
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): PeopleRepository = PeopleRepositoryImpl(networkUtils, localDataSource, remoteDataSource)
}
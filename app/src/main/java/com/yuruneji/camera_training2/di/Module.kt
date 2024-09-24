package com.yuruneji.camera_training2.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yuruneji.camera_training2.common.Constants
import com.yuruneji.camera_training2.common.DataStoreWrapper
import com.yuruneji.camera_training2.common.UserDataProvider
import com.yuruneji.camera_training2.data.local.AppDatabase
import com.yuruneji.camera_training2.data.local.LogDao
import com.yuruneji.camera_training2.data.remote.AppService
import com.yuruneji.camera_training2.data.repository.AppRepositoryImpl
import com.yuruneji.camera_training2.domain.repository.AppRepository
import com.yuruneji.camera_training2.domain.usecase.LocationSensor
import com.yuruneji.camera_training2.domain.usecase.LogFile
import com.yuruneji.camera_training2.domain.usecase.NetworkSensor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @author toru
 * @version 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, name = "app_db").build()

    @Provides
    @Singleton
    fun provideLogDao(db: AppDatabase) = db.logDao()

    @Provides
    @Singleton
    fun provideLogFile(logFile: LogDao): LogFile {
        return LogFile(logFile)
    }

    // @Provides
    // @Singleton
    // fun provideUserDao(db: AppDatabase) = db.userDao()

    // @Provides
    // @Singleton
    // fun provideAppSettingDao(db: AppDatabase) = db.appSettingDao()

    // @Provides
    // @Singleton
    // fun provideAppSettingRepository(
    //     appSettingDao: AppSettingDao,
    // ): AppSettingRepository {
    //     return AppSettingRepositoryImpl(appSettingDao)
    // }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideAppService(okHttpClient: OkHttpClient, moshi: Moshi): AppService =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            ).build().create(AppService::class.java)

    @Provides
    @Singleton
    fun provideAppRepository(api: AppService): AppRepository = AppRepositoryImpl(api)


    // @Provides
    // @Singleton
    // fun provideFaceRectView(@ApplicationContext context: Context): FaceRectView =
    //     FaceRectView(context)

    @Provides
    @Singleton
    fun provideUserDataProvider(@ApplicationContext context: Context): UserDataProvider {
        return UserDataProvider(context)
    }

    @Provides
    @Singleton
    fun provideDataStoreWrapper(@ApplicationContext context: Context): DataStoreWrapper {
        return DataStoreWrapper(context, "settings2")
    }

    @Provides
    @Singleton
    fun provideNetworkSensor(@ApplicationContext context: Context): NetworkSensor {
        return NetworkSensor(context)
    }

}

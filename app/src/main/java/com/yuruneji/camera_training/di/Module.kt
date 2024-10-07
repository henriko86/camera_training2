package com.yuruneji.camera_training.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yuruneji.camera_training.BuildConfig
import com.yuruneji.camera_training.common.ApiType
import com.yuruneji.camera_training.common.CipherExtractor
import com.yuruneji.camera_training.common.SoundManager
import com.yuruneji.camera_training.data.local.datastore.BaseDataStore
import com.yuruneji.camera_training.data.local.datastore.LogViewDataStore
import com.yuruneji.camera_training.data.local.db.AppDatabase
import com.yuruneji.camera_training.data.local.db.LogDao
import com.yuruneji.camera_training.data.local.preference.CameraPreferences
import com.yuruneji.camera_training.data.remote.AppService
import com.yuruneji.camera_training.data.repository.AppRepositoryImpl
import com.yuruneji.camera_training.data.repository.LogRepositoryImpl
import com.yuruneji.camera_training.domain.repository.AppRepository
import com.yuruneji.camera_training.domain.repository.LogRepository
import com.yuruneji.camera_training.domain.usecase.LogFile
import com.yuruneji.camera_training.domain.usecase.NetworkSensor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
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


    /** ネットワーク状態 */
    @Provides
    @Singleton
    fun provideNetworkSensor(@ApplicationContext context: Context): NetworkSensor {
        return NetworkSensor(context)
    }


    /** 暗号化 */
    @Provides
    @Singleton
    fun provideCipherExtractor(@ApplicationContext context: Context): CipherExtractor =
        CipherExtractor(context)


    /** 設定 */
    // @Provides
    // @Singleton
    // fun provideBasePreferences(@ApplicationContext context: Context): BasePreferences =
    //     BasePreferences(context)

    @Provides
    @Singleton
    fun provideCameraPreferences(@ApplicationContext context: Context): CameraPreferences =
        CameraPreferences(context)

    @Provides
    @Singleton
    fun provideBaseDataStore(@ApplicationContext context: Context): BaseDataStore {
        return BaseDataStore(context)
    }

    @Provides
    @Singleton
    fun provideLogViewDataStore(@ApplicationContext context: Context): LogViewDataStore {
        return LogViewDataStore(context)
    }


    /** Sound */
    @Provides
    @Singleton
    fun provideSoundManager(@ApplicationContext context: Context): SoundManager {
        return SoundManager(context)
    }


    /** DB */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, name = "app_db").build()

    @Provides
    @Singleton
    fun provideLogDao(db: AppDatabase) = db.logDao()

    @Provides
    @Singleton
    fun provideLogRepository(logDao: LogDao): LogRepository {
        return LogRepositoryImpl(logDao)
    }


    /** ログ */
    @Provides
    @Singleton
    fun provideLogFile(logFile: LogDao): LogFile {
        return LogFile(logFile)
    }


    /** Json */
    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()


}


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {


    /** HTTP */
    @Provides
    fun provideHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()

    @Provides
    fun provideAppService(okHttpClient: OkHttpClient, moshi: Moshi, cameraPref: CameraPreferences): AppService {
        val url = when (cameraPref.apiType) {
            ApiType.DEVELOP.no -> BuildConfig.API_URL_DEVELOP
            ApiType.STAGING.no -> BuildConfig.API_URL_STAGING
            ApiType.PRODUCTION.no -> BuildConfig.API_URL_PRODUCTION
            else -> BuildConfig.API_URL_BASE
        }

        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            ).build()
            .create(AppService::class.java)
    }

    @Provides
    fun provideAppRepository(api: AppService): AppRepository = AppRepositoryImpl(api)


}

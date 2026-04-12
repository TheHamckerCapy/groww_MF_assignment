package com.example.groww_mf_assignment.di

import android.app.Application
import androidx.room.Room
import com.example.groww_mf_assignment.data.local.MfDao
import com.example.groww_mf_assignment.data.local.MfDatabase
import com.example.groww_mf_assignment.data.remote.MfApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    fun provideMfApiService(moshi: Moshi): MfApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.mfapi.in/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MfApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMfDatabase(app: Application): MfDatabase {
        return Room.databaseBuilder(
            app,
            MfDatabase::class.java,
            "mf_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMfDao(db: MfDatabase): MfDao {
        return db.mfDao
    }
}
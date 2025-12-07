package com.smartcourse.di

import com.smartcourse.data.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideUserRepository(
//        client: SupabaseClient
//    ): UserRepository {
//        return UserRepository(client)
//    }

}


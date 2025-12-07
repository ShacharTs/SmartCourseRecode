package com.smartcourse.di

import com.smartcourse.data.repositories.UserRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Provides
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        supabase: SupabaseClient
    ): UserRepository {
        return UserRepository(supabase)
    }
}

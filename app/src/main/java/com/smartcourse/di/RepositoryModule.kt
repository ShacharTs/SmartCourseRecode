package com.smartcourse.di


import com.google.firebase.firestore.FirebaseFirestore
import com.smartcourse.data.repositories.chat.ChatRepositoryImpl
import com.smartcourse.data.repositories.chat.IChatRepository
import com.smartcourse.data.repositories.user.ProfileRepository
import com.smartcourse.data.repositories.user.UserRepository
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

    @Provides
    @Singleton
    fun provideChatRepository(
        firestore: FirebaseFirestore,
        userProfileRepo: ProfileRepository
    ): IChatRepository {
        return ChatRepositoryImpl(firestore, userProfileRepo)
    }
}




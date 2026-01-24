package com.smartcourse.di


import com.google.firebase.firestore.FirebaseFirestore
import com.smartcourse.data.repositories.chat.ChatRepositoryImpl
import com.smartcourse.data.repositories.chat.IChatRepository
import com.smartcourse.data.repositories.user.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideChatRepository(
        firestore: FirebaseFirestore,
        userProfileRepo: ProfileRepository
    ): IChatRepository {
        return ChatRepositoryImpl(firestore, userProfileRepo)
    }
}




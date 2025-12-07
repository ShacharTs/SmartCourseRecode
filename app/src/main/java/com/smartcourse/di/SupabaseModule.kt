package com.smartcourse.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(
        context: Context
    ): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://ronaetarblsnyuakbxsv.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJvbmFldGFyYmxzbnl1YWtieHN2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMwNDk4NzIsImV4cCI6MjA3ODYyNTg3Mn0.SvfcUUP2Qmp501WB4iHOpnTi21kt-rS-uMPnzWqJyOk"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}

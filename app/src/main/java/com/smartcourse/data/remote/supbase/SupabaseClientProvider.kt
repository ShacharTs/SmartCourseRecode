package com.smartcourse.data.remote.supbase

import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest


object SupabaseClientProvider {

    lateinit var client: SupabaseClient
        private set

    fun init(context: Context) {
        // No storage, no schemes — v2 handles persistence automatically
        client = createSupabaseClient(
            supabaseUrl = "https://ronaetarblsnyuakbxsv.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJvbmFldGFyYmxzbnl1YWtieHN2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMwNDk4NzIsImV4cCI6MjA3ODYyNTg3Mn0.SvfcUUP2Qmp501WB4iHOpnTi21kt-rS-uMPnzWqJyOk"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}







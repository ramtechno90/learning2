package com.example.menuapp.data.supabase

import io.github.jan_tennert.supabase.SupabaseClient
import io.github.jan_tennert.supabase.createSupabaseClient
import io.github.jan_tennert.supabase.gotrue.GoTrue
import io.github.jan_tennert.supabase.postgrest.Postgrest
import io.github.jan_tennert.supabase.realtime.Realtime
import io.github.jan_tennert.supabase.storage.Storage
import kotlinx.serialization.json.Json

/**
 * A singleton object to manage the Supabase client instance.
 *
 * This provides a single point of access to the Supabase client throughout the application,
 * initialized with the project's specific URL and anon key.
 */
object SupabaseManager {

    private const val SUPABASE_URL = "https://mdpmktdfszztukfgqwjq.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1kcG1rdGRmc3p6dHVrZmdxd2pxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTUxMDQ0MTksImV4cCI6MjA3MDY4MDQxOX0.NBlC_7cqv7WscIryrJEPpfpktP8YerbsHfKp8UbjqHU"

    /**
     * The single, shared instance of the [SupabaseClient].
     */
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        // Install the necessary Supabase plugins.
        install(GoTrue)
        install(Postgrest)
        install(Realtime)
        install(Storage)

        // Configure Postgrest to handle JSONB columns correctly with our serializable data classes.
        defaultSerializer = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }
}

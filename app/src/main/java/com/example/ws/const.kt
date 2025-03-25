package com.example.ws

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val client : SupabaseClient = createSupabaseClient(
    "https://fnuichoiatdyljxuvfkq.supabase.co",
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZudWljaG9pYXRkeWxqeHV2ZmtxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzcxNDIxNjUsImV4cCI6MjA1MjcxODE2NX0.UKeEgzJfVsKyhPHkq8LtkRUuCGhibzh1oHRnztSEzM0"
) {
    install(Postgrest)
    install(Auth)
}


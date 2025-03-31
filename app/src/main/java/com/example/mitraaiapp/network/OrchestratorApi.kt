package com.example.mitraaiapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Data models
data class ScreenElement(
    val elementId: String,
    val text: String?,
    val description: String?,
    val resourceId: String?
)

data class ScreenContext(
    val appName: String,
    val elements: List<ScreenElement>
)

data class OrchestratorRequest(
    val userGoal: String,
    val screenContext: ScreenContext
)

data class ActionStep(
    val action: String,
    val target: Map<String, Any> // e.g., {"resource_id": "search_button", "text": "NYC"}
)

data class OrchestratorResponse(
    val steps: List<ActionStep>
)

// Retrofit interface
interface OrchestratorApiService {
    @POST("plan")
    suspend fun getPlan(@Body request: OrchestratorRequest): OrchestratorResponse
}

// Singleton object to hold Retrofit and the service
object OrchestratorApi {
    private const val BASE_URL = "http://YOUR_BACKEND_URL/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: OrchestratorApiService = retrofit.create(OrchestratorApiService::class.java)
}
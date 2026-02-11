package com.jimpgetaxi.nevrologos.data.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface AiService {
    @GET("v1beta/models")
    suspend fun getModels(
        @Query("key") apiKey: String
    ): ModelsResponse
}

@Serializable
data class ModelsResponse(
    val models: List<AiModel>
)

@Serializable
data class AiModel(
    val name: String,
    val displayName: String,
    val description: String,
    val supportedGenerationMethods: List<String> = emptyList()
)

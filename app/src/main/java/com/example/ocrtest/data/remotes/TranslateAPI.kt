package com.example.ocrtest.data.remotes

import com.example.ocrtest.data.models.TranslationResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TranslateAPI {

    @Headers("Content-Type: application/json")
    @POST("api/translate") // endpoint backend của bạn
    suspend fun translateText(
        @Body body: Map<String, String> // ví dụ {"text": "hello world", "to": "vi"}
    ): TranslationResponse
}

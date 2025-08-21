package com.example.ocrtest.data.remotes

import com.example.ocrtest.data.models.LanguagesResponse
import retrofit2.http.GET

interface MicrosoftAPI {
    @GET("languages?api-version=3.0&scope=translation")
    suspend fun getLanguages(): LanguagesResponse
}
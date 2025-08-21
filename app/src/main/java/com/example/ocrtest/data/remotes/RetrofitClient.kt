package com.example.ocrtest.data.remotes

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Logging interceptor để xem request/response
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Client của Spring Boot (dịch)
    val translateApi: TranslateAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://translatespringboot.onrender.com/")
            .client(client) // thêm client có logging
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslateAPI::class.java)
    }

    // Client của Microsoft Translator (lấy ngôn ngữ)
    val msApi: MicrosoftAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.cognitive.microsofttranslator.com/")
            .client(client) // thêm client có logging
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MicrosoftAPI::class.java)
    }
}

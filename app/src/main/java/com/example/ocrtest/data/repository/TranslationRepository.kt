package com.example.ocrtest.data.repository

import android.util.Log
import com.example.ocrtest.data.models.*
import com.example.ocrtest.data.remotes.MicrosoftAPI
import com.example.ocrtest.data.remotes.TranslateAPI

class TranslationRepository(
    private val msApi: MicrosoftAPI,
    private val translateApi: TranslateAPI
) {

    /**
     * Lấy danh sách ngôn ngữ từ Microsoft API
     */
    suspend fun getLanguages(): List<LanguageOption> {
        return try {
            val res = msApi.getLanguages()
            res.translation.map { (code, detail) ->
                LanguageOption(code, detail.name)
            }
        } catch (e: Exception) {
            Log.e("TranslationRepository", "Error getLanguages: ${e.message}", e)
            // fallback cơ bản
            listOf(LanguageOption("en", "English"), LanguageOption("vi", "Vietnamese"))
        }
    }

    /**
     * Dịch text sang targetLang, trả về OcrResult
     */
    suspend fun translateText(text: String, targetLang: String): OcrResult {
        return try {
            val cleanText = text.replace("\n", " ").trim()
            val body = mapOf("text" to cleanText, "to" to targetLang)

            val res: TranslationResponse = translateApi.translateText(body)

            val translations = res.firstOrNull()?.translations ?: emptyList()
            val translatedText = translations.joinToString(" ") { it.text }

            OcrResult(
                originalText = text,
                translatedText = translatedText,
                wordMappings = emptyList() // có thể update alignment nếu muốn
            )
        } catch (e: Exception) {
            OcrResult(
                originalText = text,
                translatedText = "Lỗi dịch: ${e.message}"
            )
        }
    }

    /**
     * Parse alignment string từ Microsoft Translator
     * ví dụ: "0:4-0:7 6:10-8:12"
     */
    private fun mapWords(original: String, translated: String, proj: String): List<WordMapping> {
        val result = mutableListOf<WordMapping>()
        proj.split(" ").forEach { pair ->
            val (src, tgt) = pair.split("-")
            val (srcStart, srcEnd) = src.split(":").map { it.toInt() }
            val (tgtStart, tgtEnd) = tgt.split(":").map { it.toInt() }

            val sourceWord = original.substring(srcStart, srcEnd + 1)
            val targetWord = translated.substring(tgtStart, tgtEnd + 1)

            result.add(
                WordMapping(
                    originalWord = sourceWord,
                    translatedWord = targetWord,
                    originalRange = srcStart..srcEnd,
                    translatedRange = tgtStart..tgtEnd
                )
            )
        }
        return result
    }
}

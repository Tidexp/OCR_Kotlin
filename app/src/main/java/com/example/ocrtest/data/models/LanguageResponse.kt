package com.example.ocrtest.data.models

/**
 * LanguagesResponse đại diện cho dữ liệu trả về từ Microsoft Translator API
 * khi lấy danh sách ngôn ngữ hỗ trợ.
 *
 * Field:
 *  - translation: Map<String, LanguageDetail>
 *      Key: mã ngôn ngữ (ví dụ "en", "vi")
 *      Value: LanguageDetail chứa tên ngôn ngữ
 *
 * Công dụng:
 * 1. Lấy danh sách ngôn ngữ do API hỗ trợ.
 * 2. Kết hợp với LanguageOption để hiển thị UI.
 *
 * Ví dụ:
 * {
 *   "translation": {
 *      "en": { "name": "English" },
 *      "vi": { "name": "Vietnamese" }
 *   }
 * }
 */
data class LanguagesResponse(
    val translation: Map<String, LanguageDetail>
)

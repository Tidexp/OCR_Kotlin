package com.example.ocrtest.data.models

/**
 * LanguageDetail đại diện cho chi tiết một ngôn ngữ được Microsoft Translator hỗ trợ.
 *
 * Field:
 *  - name: Tên ngôn ngữ (ví dụ: "English", "Vietnamese")
 *
 * Công dụng:
 * 1. Hiển thị danh sách ngôn ngữ cho người dùng chọn khi dịch.
 * 2. Kết hợp với key ngôn ngữ (ví dụ "en", "vi") để gọi API dịch.
 */
data class LanguageDetail(
    val name: String
)

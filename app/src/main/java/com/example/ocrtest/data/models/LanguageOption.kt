package com.example.ocrtest.data.models

/**
 * LanguageOption đại diện cho một ngôn ngữ trong danh sách chọn của app.
 *
 * Field:
 *  - code: Mã ngôn ngữ (ví dụ: "en", "vi")
 *  - name: Tên ngôn ngữ hiển thị (ví dụ: "English", "Vietnamese")
 *
 * Công dụng:
 * 1. Hiển thị danh sách ngôn ngữ trong UI (spinner, dropdown…)
 * 2. Dùng code để gọi API dịch, name để show cho người dùng.
 *
 * Lưu ý:
 *  - Khác với LanguageDetail từ API, LanguageOption kết hợp luôn code + tên, tiện dùng trong app.
 */
data class LanguageOption(
    val code: String,
    val name: String
)


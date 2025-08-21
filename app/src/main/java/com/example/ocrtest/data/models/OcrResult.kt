package com.example.ocrtest.data.models

/**
 * OcrResult đại diện cho kết quả của quá trình OCR + dịch.
 *
 * Field:
 *  - originalText: Văn bản gốc từ ảnh (sau OCR)
 *  - translatedText: Văn bản đã dịch
 *  - wordMappings: Danh sách WordMapping, map từng từ giữa câu gốc và câu dịch
 *
 * Công dụng:
 * 1. Hiển thị kết quả OCR cho người dùng.
 * 2. Hiển thị bản dịch.
 * 3. Cho phép highlight hoặc tracking từng từ thông qua wordMappings.
 *
 * Lưu ý:
 *  - wordMappings có thể để trống nếu không cần hiển thị map từng từ.
 */
data class OcrResult(
    val originalText: String,
    val translatedText: String,
    val wordMappings: List<WordMapping> = emptyList()
)





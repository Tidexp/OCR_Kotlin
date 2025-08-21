package com.example.ocrtest.data.models

/**
 * WordMapping đại diện cho mối quan hệ giữa một từ trong câu gốc
 * và từ tương ứng trong câu đã dịch.
 *
 * Ví dụ:
 * originalWord = "hello", translatedWord = "xin chào"
 * originalRange = 0..4   // vị trí ký tự "hello" trong câu gốc
 * translatedRange = 0..7 // vị trí ký tự "xin chào" trong câu dịch
 *
 * Công dụng:
 * 1. Highlight từng từ khi hiển thị câu gốc và câu dịch.
 * 2. Theo dõi alignment từng từ giữa OCR text và bản dịch.
 * 3. Hỗ trợ phân tích, chỉnh sửa, hoặc animation dịch từng từ.
 */
data class WordMapping(
    val originalWord: String,
    val translatedWord: String,
    val originalRange: IntRange,   // vị trí ký tự trong câu gốc
    val translatedRange: IntRange  // vị trí ký tự trong câu dịch
)

package com.example.ocrtest.data.models

/**
 * TranslationItem đại diện cho 1 phần tử trong mảng phản hồi từ API Translate.
 *
 * Field:
 *  - translations: Danh sách TranslationText
 *      Mỗi TranslationText là một bản dịch của đoạn text gửi lên,
 *      có thể chứa alignment nếu API hỗ trợ.
 *
 * Công dụng:
 * 1. Nhận dữ liệu dịch từ API.
 * 2. Parse từng TranslationText để hiển thị text đã dịch.
 * 3. Nếu alignment có, có thể dùng để tạo WordMapping.
 *
 * Lưu ý:
 *  - API trả về mảng, mỗi phần tử có translations,
 *    nên root phải là List<TranslationItem>.
 */
data class TranslationResponse(
    val translatedText: String,
    val detectedLanguage: String? = null,
    val alignment: String? = null
)


package com.example.ocrtest.data.models

/**
 * Alignment đại diện cho thông tin căn chỉnh từ giữa câu gốc và câu dịch.
 *
 * Field:
 *  - proj: String?
 *      Lưu trữ thông tin mapping từ → từ theo định dạng "srcStart:srcEnd-tgtStart:tgtEnd ..."
 *      Ví dụ: "0:4-0:7 6:10-8:12" nghĩa là:
 *          - ký tự 0..4 trong câu gốc map sang ký tự 0..7 trong câu dịch
 *          - ký tự 6..10 trong câu gốc map sang ký tự 8..12 trong câu dịch
 *
 * Công dụng:
 * 1. Dùng để tạo WordMapping, highlight từ tương ứng.
 * 2. Hỗ trợ phân tích chi tiết từng từ sau khi dịch.
 */
data class Alignment(
    val proj: String?
)


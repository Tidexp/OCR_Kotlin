import com.example.ocrtest.data.models.Alignment

/**
 * TranslationText đại diện cho một bản dịch của API Translate.
 *
 * Field:
 *  - text: Văn bản đã dịch
 *  - to: Mã ngôn ngữ đích (ví dụ: "en", "vi")
 *  - alignment: Alignment? (tùy chọn)
 *      Chứa thông tin mapping từ → từ giữa câu gốc và câu dịch,
 *      được dùng để tạo WordMapping nếu cần highlight từng từ.
 *
 * Công dụng:
 * 1. Nhận dữ liệu dịch từ API.
 * 2. Nếu alignment có dữ liệu, có thể parse ra WordMapping để highlight từng từ.
 * 3. Chứa text đã dịch và ngôn ngữ đích, dùng cho UI hoặc lưu trữ.
 */
data class TranslationText(
    val text: String,
    val to: String,
    val alignment: Alignment? // thêm field alignment
)

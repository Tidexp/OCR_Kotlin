package com.example.ocrtest.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ocrtest.data.repository.CardRepository
import com.example.ocrtest.viewmodel.ManageViewModel

// Factory dùng để tạo ViewModel có constructor với tham số (ở đây là CardRepository).
// Nếu không có Factory, ViewModelProvider chỉ biết tạo ViewModel bằng constructor rỗng → sẽ crash.
class ManageViewModelFactory(
    private val repository: CardRepository // Dependency được inject từ ngoài vào
) : ViewModelProvider.Factory {

    // Hàm này Android sẽ gọi khi cần tạo instance ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra kiểu ViewModel có đúng là ManageViewModel hay không
        if (modelClass.isAssignableFrom(ManageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // Ép kiểu để tránh warning generic
            return ManageViewModel(repository) as T // Trả về instance ManageViewModel đã inject repo
        }
        // Nếu gọi nhầm ViewModel khác thì ném ra exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

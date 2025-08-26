package com.example.ocrtest.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ocrtest.data.repository.TranslationRepository
import com.example.ocrtest.viewmodel.TranslationViewModel

/**
 * Factory riêng cho TranslationViewModel
 * Giúp inject TranslationRepository vào ViewModel
 */
class TranslationViewModelFactory(
    private val repository: TranslationRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TranslationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TranslationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

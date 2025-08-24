package com.example.ocrtest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocrtest.data.models.OcrResult
import com.example.ocrtest.data.models.LanguageOption
import com.example.ocrtest.data.repository.TranslationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TranslationViewModel(
    private val repository: TranslationRepository
) : ViewModel() {

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    fun setInputText(text: String) {
        _inputText.value = text
        _translationResult.value = null  // ✅ reset bản dịch cũ khi có OCR mới
        _detectedLang.value = null       // (optional) reset ngôn ngữ detect, để detect lại sau dịch
    }

    private val _translationResult = MutableStateFlow<OcrResult?>(null)
    val translationResult: StateFlow<OcrResult?> = _translationResult

    private val _languages = MutableStateFlow<List<LanguageOption>>(emptyList())
    val languages: StateFlow<List<LanguageOption>> = _languages

    private val _detectedLang = MutableStateFlow<String?>(null)
    val detectedLang: StateFlow<String?> = _detectedLang


    fun loadLanguages() {
        viewModelScope.launch {
            val list = repository.getLanguages()
            _languages.value = list
        }
    }

    fun translateText(targetLang: String) {
        val text = _inputText.value
        if (text.isBlank()) return

        viewModelScope.launch {
            val result: OcrResult = repository.translateText(text, targetLang)
            _translationResult.value = result
            _detectedLang.value = result.detectedLanguage // update dropdown source
        }
    }
}

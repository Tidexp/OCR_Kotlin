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
    }

    private val _translationResult = MutableStateFlow<OcrResult?>(null)
    val translationResult: StateFlow<OcrResult?> = _translationResult

    private val _languages = MutableStateFlow<List<LanguageOption>>(emptyList())
    val languages: StateFlow<List<LanguageOption>> = _languages

    fun loadLanguages() {
        viewModelScope.launch {
            val list = repository.getLanguages()
            _languages.value = list
        }
    }

    fun translateText(text: String, targetLang: String) {
        viewModelScope.launch {
            val result: OcrResult = repository.translateText(text, targetLang)
            _translationResult.value = result
        }
    }
}

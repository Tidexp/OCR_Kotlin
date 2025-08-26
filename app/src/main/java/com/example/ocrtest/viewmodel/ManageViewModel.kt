package com.example.ocrtest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocrtest.data.entities.CardItem
import com.example.ocrtest.data.repository.CardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageViewModel(
    private val repository: CardRepository
) : ViewModel() {

    // Flow danh sách card từ database, map CardItem -> title cho UI
    val cards: StateFlow<List<CardItem>> =
        repository.cardsFlow
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Thêm card mới vào database
    fun addCard(title: String) {
        viewModelScope.launch {
            repository.addCard(title)
        }
    }

    // Xóa card
    fun deleteCard(card: CardItem) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    // Cập nhật card (rename, update media...)
    fun updateCard(card: CardItem) {
        viewModelScope.launch {
            repository.updateCard(card)
        }
    }
}

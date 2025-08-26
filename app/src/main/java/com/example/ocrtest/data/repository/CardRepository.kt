package com.example.ocrtest.data.repository

import com.example.ocrtest.data.dao.CardDao
import com.example.ocrtest.data.entities.CardItem
import kotlinx.coroutines.flow.Flow

class CardRepository(private val dao: CardDao) {
    val cardsFlow: Flow<List<CardItem>> = dao.getAllCards()

    suspend fun addCard(title: String) {
        dao.insert(CardItem(title = title))
    }

    suspend fun deleteCard(card: CardItem) = dao.delete(card)
    suspend fun updateCard(card: CardItem) = dao.update(card)
}

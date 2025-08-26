package com.example.ocrtest.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity đại diện cho bảng trong database
@Entity(tableName = "cards")
data class CardItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val textContent: String? = null,
    val imageUris: String? = null,
    val audioUris: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

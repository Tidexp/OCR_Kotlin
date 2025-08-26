package com.example.ocrtest.data.dao

import androidx.room.*
import com.example.ocrtest.data.entities.CardItem
import kotlinx.coroutines.flow.Flow

// Data Access Object
// Truy vấn dữ liệu từ database
@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY createdAt DESC")
    fun getAllCards(): Flow<List<CardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: CardItem)

    @Update
    suspend fun update(card: CardItem)

    @Delete
    suspend fun delete(card: CardItem)
}

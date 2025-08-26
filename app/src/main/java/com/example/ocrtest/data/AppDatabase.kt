package com.example.ocrtest.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ocrtest.data.entities.CardItem
import com.example.ocrtest.data.dao.CardDao

@Database(entities = [CardItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}

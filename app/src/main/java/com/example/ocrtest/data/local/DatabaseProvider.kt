package com.example.ocrtest.data.local

import android.content.Context
import androidx.room.Room
import com.example.ocrtest.data.AppDatabase

// Singleton object để quản lý và cung cấp database duy nhất cho toàn app
object DatabaseProvider {

    // Giữ instance của AppDatabase (ban đầu null, chưa tạo)
    private var instance: AppDatabase? = null

    // Hàm lấy database, nếu chưa có thì build, còn có rồi thì trả về luôn
    fun getDatabase(context: Context): AppDatabase {
        // ?: = nếu instance khác null thì return luôn,
        // còn null thì synchronized block để đảm bảo thread-safe
        return instance ?: synchronized(this) {
            // Tạo database bằng Room
            val db = Room.databaseBuilder(
                context.applicationContext,   // Dùng applicationContext để tránh leak
                AppDatabase::class.java,      // Class định nghĩa database (có @Database)
                "app_database"                // Tên file database trong máy
            ).build()

            // Lưu instance lại để tái sử dụng lần sau
            instance = db
            db
        }
    }
}

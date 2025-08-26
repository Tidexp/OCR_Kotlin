package com.example.ocrtest.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Manage : Screen("manage", "Quản lý", Icons.Default.Folder)
    data object Camera : Screen("camera", "Camera", Icons.Default.CameraAlt)
    data object Extra : Screen("extra", "Khác", Icons.Default.Star) // tạm icon ngôi sao
    data object Settings : Screen("settings", "Cài đặt", Icons.Default.Settings)
}

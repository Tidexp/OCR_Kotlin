package com.example.ocrtest

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ocrtest.data.remotes.RetrofitClient
import com.example.ocrtest.data.repository.TranslationRepository
import com.example.ocrtest.navigation.BottomNavBar
import com.example.ocrtest.navigation.Screen
import com.example.ocrtest.ui.screens.ManageScreen
import com.example.ocrtest.ui.screens.OCRTranslateScreen
import com.example.ocrtest.viewmodel.ManageViewModel
import com.example.ocrtest.viewmodel.TranslationViewModel
import com.example.ocrtest.viewmodel.factories.TranslationViewModelFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import com.example.ocrtest.data.local.DatabaseProvider
import com.example.ocrtest.data.repository.CardRepository
import com.example.ocrtest.viewmodel.factories.ManageViewModelFactory

class MainActivity : ComponentActivity() {

    private val translationRepository = TranslationRepository(
        msApi = RetrofitClient.msApi,
        translateApi = RetrofitClient.translateApi
    )

    private val translationViewModel: TranslationViewModel by viewModels {
        TranslationViewModelFactory(translationRepository)
    }

    // ViewModel quản lý thẻ dùng Room
    private val manageViewModel: ManageViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(applicationContext)
        val repo = CardRepository(db.cardDao())
        ManageViewModelFactory(repo)
    }

    private var photoUri: android.net.Uri? = null

    // Launcher xin quyền CAMERA
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openCamera()
            } else {
                Toast.makeText(this, "Không có quyền camera", Toast.LENGTH_SHORT).show()
            }
        }

    // Launcher chụp ảnh
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let { uri ->
                    val bitmap = contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                    bitmap?.let { runOCR(it) }
                }
            }
        }

    // Launcher chọn ảnh từ thư viện
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { selectedUri ->
                val bitmap = contentResolver.openInputStream(selectedUri)?.use {
                    BitmapFactory.decodeStream(it)
                }
                bitmap?.let { runOCR(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            OCRTranslateScreen(
                                viewModel = translationViewModel,
                                onPickImage = { pickImageLauncher.launch("image/*") },
                                onCaptureImage = { checkCameraPermissionAndCapture() }
                            )
                        }
                        composable(Screen.Manage.route) {
                            ManageScreen(viewModel = manageViewModel)
                        }
                        composable(Screen.Camera.route) { Text("Camera Screen") }
                        composable(Screen.Extra.route) { Text("Slot tự do") }
                        composable(Screen.Settings.route) { Text("Cài đặt Screen") }
                    }
                }
            }
        }
    }

    private fun checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val photoFile = File.createTempFile("ocr_photo_", ".jpg", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            photoFile
        )
        photoUri = uri
        takePictureLauncher.launch(uri) // dùng uri cục bộ, không phải var nullable
    }

    private fun runOCR(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text
                translationViewModel.setInputText(text) // Set text vào ViewModel
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "OCR failed", e)
            }
    }
}

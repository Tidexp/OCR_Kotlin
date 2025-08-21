package com.example.ocrtest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.*
import com.example.ocrtest.data.remotes.RetrofitClient
import com.example.ocrtest.data.repository.TranslationRepository
import com.example.ocrtest.ui.screens.OCRTranslateScreen
import com.example.ocrtest.viewmodel.TranslationViewModel
import com.example.ocrtest.viewmodel.TranslationViewModelFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : ComponentActivity() {

    private val translationRepository = TranslationRepository(
        msApi = RetrofitClient.msApi,
        translateApi = RetrofitClient.translateApi
    )

    private val viewModel: TranslationViewModel by viewModels {
        TranslationViewModelFactory(translationRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let { selectedUri ->
                // Chuyển URI sang Bitmap
                val bitmap = contentResolver.openInputStream(selectedUri)?.use {
                    BitmapFactory.decodeStream(it)
                }
                bitmap?.let {
                    runOCR(it) // Chạy ML Kit OCR
                }
            }
        }

        setContent {
            MaterialTheme {
                OCRTranslateScreen(
                    viewModel = viewModel,
                    onPickImage = { pickImageLauncher.launch("image/*") }
                )
            }
        }
    }

    private fun runOCR(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text
                // Set text vào ViewModel
                viewModel.setInputText(text)
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "OCR failed", e)
            }
    }
}



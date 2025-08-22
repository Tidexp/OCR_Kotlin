package com.example.ocrtest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ocrtest.data.models.OcrResult
import com.example.ocrtest.ui.components.LanguageDropdown
import com.example.ocrtest.viewmodel.TranslationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRTranslateScreen(
    viewModel: TranslationViewModel = viewModel(),
    onPickImage: () -> Unit,
    onCaptureImage: () -> Unit
) {
    val inputText by viewModel.inputText.collectAsState()
    var selectedLang by remember { mutableStateOf("vi") }

    val translationResult by viewModel.translationResult.collectAsState()
    val languages by viewModel.languages.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadLanguages() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("📖 OCR Translator", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Row buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedButton(
                    onClick = onPickImage,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Photo, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Thư viện")
                }
                ElevatedButton(
                    onClick = onCaptureImage,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Camera")
                }
            }

            // Input card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("OCR Text:", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(6.dp))
                    TextField(
                        value = inputText,
                        onValueChange = { viewModel.setInputText(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập hoặc dán văn bản OCR") },
                        singleLine = false,
                        maxLines = 6
                    )
                }
            }

            // Language + translate button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LanguageDropdown(
                    languages = languages,
                    selectedLang = selectedLang,
                    onLangSelected = { code -> selectedLang = code },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { viewModel.translateText(inputText, selectedLang) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dịch")
                }
            }

            // Translation result
            translationResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF7F7F7)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Kết quả:", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(6.dp))
                        HighlightTranslatedText(result)
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightTranslatedText(result: OcrResult) {
    val annotatedString = buildAnnotatedString {
        append(result.translatedText)
        result.wordMappings.forEach { mapping ->
            addStyle(
                style = SpanStyle(
                    background = Color.Yellow,
                    color = Color.Black
                ),
                start = mapping.translatedRange.first,
                end = mapping.translatedRange.last + 1
            )
        }
    }
    Text(
        annotatedString,
        style = MaterialTheme.typography.bodyLarge
    )
}


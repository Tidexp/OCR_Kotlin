package com.example.ocrtest.ui.screens

import androidx.compose.foundation.layout.*
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

@Composable
fun OCRTranslateScreen(
    viewModel: TranslationViewModel = viewModel(),
    onPickImage: () -> Unit // callback mở thư viện ảnh
) {
    val inputText by viewModel.inputText.collectAsState() // bind trực tiếp với ViewModel
    var selectedLang by remember { mutableStateOf("vi") }

    val translationResult by viewModel.translationResult.collectAsState()
    val languages by viewModel.languages.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadLanguages() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nút chọn ảnh từ thư viện
        Button(onClick = onPickImage, modifier = Modifier.fillMaxWidth()) {
            Text("Chọn ảnh từ thư viện")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Enter text from OCR:")
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = inputText,
            onValueChange = { viewModel.setInputText(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Type or paste OCR text here") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown chọn ngôn ngữ đích
        LanguageDropdown(
            languages = languages,
            selectedLang = selectedLang,
            onLangSelected = { code -> selectedLang = code }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.translateText(inputText, selectedLang) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Translate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        translationResult?.let { result ->
            Text("Result:")
            Spacer(modifier = Modifier.height(4.dp))
            HighlightTranslatedText(result)
        }
    }
}

@Composable
fun HighlightTranslatedText(result: OcrResult) {
    // Build AnnotatedString để highlight từ dịch
    val annotatedString = buildAnnotatedString {
        append(result.translatedText)
        result.wordMappings.forEach { mapping ->
            // highlight bằng background yellow, bạn có thể thay màu
            addStyle(
                style = SpanStyle(background = Color.Yellow),
                start = mapping.translatedRange.first,
                end = mapping.translatedRange.last + 1
            )
        }
    }

    Text(annotatedString)
}

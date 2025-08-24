package com.example.ocrtest.ui.screens

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ocrtest.ui.components.LanguageDropdown
import com.example.ocrtest.viewmodel.TranslationViewModel
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import com.example.ocrtest.ui.helpers.ClickableTranslatedText
import com.example.ocrtest.ui.helpers.mergeTranslatedWords

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRTranslateScreen(
    viewModel: TranslationViewModel = viewModel(),
    onPickImage: () -> Unit,
    onCaptureImage: () -> Unit
) {
    val inputText by viewModel.inputText.collectAsState()
    val translationResult by viewModel.translationResult.collectAsState()
    val languages by viewModel.languages.collectAsState()
    val detectedLang by viewModel.detectedLang.collectAsState()

    var sourceLang by remember { mutableStateOf("auto") }
    var targetLang by remember { mutableStateOf("vi") }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Khởi tạo TTS
    var ttsOCR: TextToSpeech? by remember { mutableStateOf(null) }
    var ttsTranslated by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        ttsOCR = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                ttsOCR?.language = Locale.ENGLISH
            }
        }

        ttsTranslated = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsTranslated?.language = Locale("vi")
            }
        }

        onDispose {
            ttsOCR?.stop(); ttsOCR?.shutdown()
            ttsTranslated?.stop(); ttsTranslated?.shutdown()
        }
    }

    // update OCR lang khi detect
    LaunchedEffect(detectedLang) {
        detectedLang?.let { sourceLang = it }
    }

    LaunchedEffect(sourceLang) {
        ttsOCR?.let { tts ->
            val locale = Locale.forLanguageTag(sourceLang)
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.language = locale
            }
        }
    }

    LaunchedEffect(targetLang) {
        ttsTranslated?.let { tts ->
            val locale = Locale.forLanguageTag(targetLang)
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.language = locale
            }
        }
    }

    LaunchedEffect(Unit) { viewModel.loadLanguages() }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("📖 OCR Translator") }) }) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                    .padding(bottom = 72.dp), // chừa chỗ cho nút
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Buttons chọn ảnh
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ElevatedButton(onClick = onPickImage, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Photo, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Thư viện")
                    }
                    ElevatedButton(onClick = onCaptureImage, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Camera")
                    }
                }

                // OCR Text
                Box(Modifier.fillMaxWidth()) {
                    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(6.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("OCR Text Language:", style = MaterialTheme.typography.labelLarge)
                            LanguageDropdown(
                                languages = languages,
                                selectedLang = sourceLang,
                                onLangSelected = { code -> sourceLang = code },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(6.dp))

                            // OCR Text: lấy thẳng từ translationResult, reset selectedWord mỗi khi result thay đổi
                            val ocrResult = if (translationResult != null) {
                                translationResult!!.copy(
                                    wordMappings = mergeTranslatedWords(translationResult!!.wordMappings)
                                )
                            } else {
                                com.example.ocrtest.data.models.OcrResult(
                                    originalText = inputText,
                                    translatedText = "",
                                    wordMappings = emptyList() // chưa dịch thì chưa có clickable word
                                )
                            }

                            var selectedWordMeaning by remember(translationResult, inputText) {
                                mutableStateOf<String?>(null)
                            }

                            ClickableTranslatedText(
                                ocrResult = ocrResult,
                                modifier = Modifier.fillMaxWidth(),
                                editable = translationResult == null, // 🔑 trước khi dịch thì cho sửa
                                onTextChange = { viewModel.setInputText(it) },
                                onWordClick = { wordMeaning -> selectedWordMeaning = wordMeaning }
                            )

                            selectedWordMeaning?.let { meaning ->
                                Text(
                                    text = meaning,
                                    color = Color.Blue,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            if (ttsOCR != null && ttsReady) {
                                if (inputText.isNotEmpty()) {
                                    val locale = Locale.forLanguageTag(sourceLang)
                                    val availability = ttsOCR!!.isLanguageAvailable(locale)
                                    if (availability >= TextToSpeech.LANG_AVAILABLE) {
                                        ttsOCR!!.language = locale
                                        ttsOCR!!.speak(inputText, TextToSpeech.QUEUE_FLUSH, null, "OCR_${System.currentTimeMillis()}")
                                    } else {
                                        ttsOCR!!.language = Locale.ENGLISH
                                        ttsOCR!!.speak(
                                            "Ngôn ngữ $sourceLang không hỗ trợ, đọc bằng English.",
                                            TextToSpeech.QUEUE_FLUSH, null, "Fallback"
                                        )
                                    }
                                }
                            } else {
                                Log.w("TTS", "TTS chưa sẵn sàng hoặc text rỗng")
                            }
                        },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak OCR")
                    }
                }

                // Translated Text
                Box(Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FF)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Translated Text Language:", style = MaterialTheme.typography.labelLarge)
                            LanguageDropdown(
                                languages = languages,
                                selectedLang = targetLang,
                                onLangSelected = { code -> targetLang = code },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(6.dp))
                            TextField(
                                value = translationResult?.translatedText ?: "",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                singleLine = false,
                                maxLines = Int.MAX_VALUE,
                                placeholder = { Text("Chưa có bản dịch") }
                            )
                        }
                    }
                    // Translated Speak button
                    IconButton(
                        onClick = {
                            if (ttsTranslated != null && ttsReady) { // check engine đã sẵn sàng
                                translationResult?.translatedText?.takeIf { it.isNotEmpty() }?.let { text ->
                                    val locale = Locale.forLanguageTag(targetLang)
                                    val availability = ttsTranslated!!.isLanguageAvailable(locale)
                                    if (availability >= TextToSpeech.LANG_AVAILABLE) {
                                        ttsTranslated!!.language = locale
                                        ttsTranslated!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "Trans_${System.currentTimeMillis()}")
                                    } else {
                                        ttsTranslated!!.language = Locale.ENGLISH
                                        ttsTranslated!!.speak(
                                            "Ngôn ngữ $targetLang không hỗ trợ, đọc bằng English.",
                                            TextToSpeech.QUEUE_FLUSH, null, "Fallback"
                                        )
                                    }
                                }
                            } else {
                                Log.w("TTS", "TTS chưa sẵn sàng hoặc text rỗng")
                            }
                        },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak Translated")
                    }
                }
            }

            Button(
                onClick = { viewModel.translateText(targetLang) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Dịch")
            }
        }
    }
}








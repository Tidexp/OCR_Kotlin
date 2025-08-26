package com.example.ocrtest.ui.screens

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import com.example.ocrtest.ui.helpers.ClickableTranslatedText
import com.example.ocrtest.ui.helpers.mergeTranslatedWords
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("📖 OCR Translator", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF864AD2),
                            Color(0xFFDC3DD5)
                        )
                    )
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDDDE6)) // nền trắng hồng nhạt
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }

                // ✅ Row chứa 2 nút (Quét ảnh + Dịch) ở ngay trên OCR Text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Nút Quét ảnh (Dropdown)
                    Box(modifier = Modifier.weight(1f)) {
                        GradientButton(
                            text = "Quét ảnh",
                            icon = Icons.Default.CameraAlt,
                            gradient = Brush.linearGradient(
                                colors = listOf(Color(0xFF864AD2), Color(0xFFDC3DD5))
                            ),
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("📷 Chụp ảnh") },
                                onClick = {
                                    expanded = false
                                    onCaptureImage()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🖼️ Thư viện") },
                                onClick = {
                                    expanded = false
                                    onPickImage()
                                }
                            )
                        }
                    }

                    // Nút Dịch
                    GradientButton(
                        text = "Dịch",
                        icon = Icons.Default.Translate,
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF864AD2), Color(0xFFDC3DD5))
                        ),
                        onClick = { viewModel.translateText(targetLang) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // OCR Text
                Box(Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White // ✅ nền trắng
                        )
                    ) {
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
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

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(Color(0xFFFBF1F3), RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                            ) {
                                BasicTextField(
                                    value = translationResult?.translatedText ?: "",
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxSize(),
                                    readOnly = true,
                                    textStyle = TextStyle(color = Color.Black),
                                    singleLine = false,
                                    maxLines = Int.MAX_VALUE,
                                    decorationBox = { innerTextField ->
                                        if ((translationResult?.translatedText ?: "").isEmpty()) {
                                            Text("Chưa có bản dịch", color = Color.Gray)
                                        }
                                        innerTextField()
                                    }
                                )
                            }
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
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    icon: ImageVector,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(), // bỏ padding mặc định
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text(text, color = Color.White)
            }
        }
    }
}









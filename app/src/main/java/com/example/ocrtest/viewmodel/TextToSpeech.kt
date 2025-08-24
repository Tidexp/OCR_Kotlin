package com.example.ocrtest.viewmodel

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun rememberTTS(): TextToSpeech? {
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US // mặc định, có thể set theo detectedLang
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }
    return tts
}

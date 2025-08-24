package com.example.ocrtest.ui.helpers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.withStyle
import com.example.ocrtest.data.models.OcrResult
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.example.ocrtest.data.models.WordMapping

fun mergeTranslatedWords(wordMappings: List<WordMapping>): List<WordMapping> {
    if (wordMappings.isEmpty()) return emptyList()

    val sorted = wordMappings.sortedBy { it.originalRange.first }
    val result = mutableListOf<WordMapping>()
    var current = sorted.first()

    for (i in 1 until sorted.size) {
        val next = sorted[i]

        if (next.originalRange.first <= current.originalRange.last + 1 ||
            next.translatedRange.first <= current.translatedRange.last + 1
        ) {
            current = current.copy(
                originalWord = (current.originalWord + " " + next.originalWord).trim(),
                translatedWord = mergeNoDup(current.translatedWord, next.translatedWord),
                originalRange = current.originalRange.first..maxOf(current.originalRange.last, next.originalRange.last),
                translatedRange = current.translatedRange.first..maxOf(current.translatedRange.last, next.translatedRange.last)
            )
        } else {
            result.add(current)
            current = next
        }
    }
    result.add(current)

    // 🔑 loại duplicate mapping: cùng range + cùng từ gốc
    return result
        .distinctBy { it.originalRange to it.originalWord }
        .map { mapping ->
            // fix trường hợp originalWord lặp từ (your your your)
            mapping.copy(
                originalWord = dedupWords(mapping.originalWord),
                translatedWord = dedupWords(mapping.translatedWord)
            )
        }
}

// Helper xoá từ lặp
fun dedupWords(text: String): String {
    val words = text.split(" ").filter { it.isNotBlank() }
    return words.fold(mutableListOf<String>()) { acc, w ->
        if (acc.isEmpty() || acc.last() != w) acc.add(w)
        acc
    }.joinToString(" ")
}

fun mergeNoDup(a: String, b: String): String {
    return dedupWords("$a $b")
}

@Composable
fun ClickableTranslatedText(
    ocrResult: OcrResult,
    modifier: Modifier = Modifier,
    editable: Boolean = false, // 🔑 mới thêm
    onWordClick: (String) -> Unit = {},
    onTextChange: (String) -> Unit = {} // 🔑 cho phép edit OCR
) {
    if (ocrResult.wordMappings.isEmpty()) {
        if (editable) {
            // 🔹 Editable OCR text
            TextField(
                value = ocrResult.originalText,
                onValueChange = { onTextChange(it) },
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                singleLine = false,
                maxLines = Int.MAX_VALUE,
                placeholder = { Text("Text OCR...") }
            )
        } else {
            // 🔹 Plain text
            Text(
                text = ocrResult.originalText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = modifier
            )
        }
        return
    }

    // 🔹 Nếu đã có wordMappings → clickable
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val annotatedString = buildAnnotatedString {
        ocrResult.wordMappings.forEach { mapping ->
            pushStringAnnotation(tag = "LINK", annotation = mapping.translatedWord)
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(mapping.originalWord)
            }
            pop()
            append(" ")
        }
    }

    BasicText(
        text = annotatedString,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offsetPosition ->
                val offset = textLayoutResult?.getOffsetForPosition(offsetPosition)
                offset?.let {
                    annotatedString.getStringAnnotations(
                        tag = "LINK",
                        start = it,
                        end = it
                    ).firstOrNull()?.let { annotation ->
                        onWordClick(annotation.item)
                    }
                }
            }
        },
        onTextLayout = { textLayoutResult = it }
    )
}







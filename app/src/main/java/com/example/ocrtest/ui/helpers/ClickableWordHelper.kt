package com.example.ocrtest.ui.helpers

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.ocrtest.data.models.WordMapping

fun mergeTranslatedWords(wordMappings: List<WordMapping>): List<WordMapping> {
    if (wordMappings.isEmpty()) return emptyList()

    // sort theo vị trí gốc để đảm bảo thứ tự
    val sorted = wordMappings.sortedBy { it.originalRange.first }
    val result = mutableListOf<WordMapping>()
    var current = sorted.first()

    for (i in 1 until sorted.size) {
        val next = sorted[i]

        // chỉ merge nếu originalRange chạm hoặc overlap
        if (next.originalRange.first <= current.originalRange.last + 1) {
            // gộp lại, giữ đúng thứ tự theo range
            current = current.copy(
                originalWord = (current.originalWord + " " + next.originalWord).trim(),
                translatedWord = (current.translatedWord + " " + next.translatedWord).trim(),
                originalRange = current.originalRange.first..maxOf(current.originalRange.last, next.originalRange.last),
                translatedRange = current.translatedRange.first..maxOf(current.translatedRange.last, next.translatedRange.last)
            )
        } else {
            result.add(current)
            current = next
        }
    }
    result.add(current)

    // loại duplicate mapping (cùng range + từ gốc)
    return result
        .distinctBy { it.originalRange to it.originalWord }
        .map { mapping ->
            mapping.copy(
                originalWord = dedupWordsKeepOrder(mapping.originalWord),
                translatedWord = dedupWordsKeepOrder(mapping.translatedWord)
            )
        }
}

// dedup chỉ bỏ từ trùng liên tiếp, không reorder
fun dedupWordsKeepOrder(text: String): String {
    if (text.isBlank()) return text
    val words = text.split(" ").filter { it.isNotBlank() }
    if (words.isEmpty()) return text

    val result = mutableListOf<String>()
    var prev: String? = null
    for (w in words) {
        if (w != prev) {
            result.add(w)
        }
        prev = w
    }
    return result.joinToString(" ")
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
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
                    .background(Color(0xFFFBF1F3), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                BasicTextField(
                    value = ocrResult.originalText,
                    onValueChange = { onTextChange(it) },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(color = Color.Black),
                    singleLine = false,
                    maxLines = Int.MAX_VALUE,
                    decorationBox = { innerTextField ->
                        if (ocrResult.originalText.isEmpty()) {
                            Text("Text OCR...", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
            }
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







package com.example.ocrtest.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ocrtest.data.models.LanguageOption

@Composable
fun LanguageDropdown(
    languages: List<LanguageOption>,
    selectedLang: String,
    onLangSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Translate to: ${selectedLang.uppercase()}")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { lang ->
                DropdownMenuItem(
                    text = { Text(lang.name) },
                    onClick = {
                        onLangSelected(lang.code)
                        expanded = false
                    }
                )
            }
        }
    }
}

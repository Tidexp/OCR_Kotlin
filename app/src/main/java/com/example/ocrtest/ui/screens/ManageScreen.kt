package com.example.ocrtest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ocrtest.data.entities.CardItem
import com.example.ocrtest.viewmodel.ManageViewModel

@Composable
fun ManageScreen(viewModel: ManageViewModel) {
    val cards by viewModel.cards.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }, // bật dialog xác nhận
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm mới")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            cards.forEach { card ->
                CardItemView(
                    card = card,
                    onDelete = { viewModel.deleteCard(card) }
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        // Dialog xác nhận
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Xác nhận") },
                text = { Text("Bạn có chắc muốn tạo bộ thẻ mới không?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addCard("Bộ thẻ ${cards.size + 1}")
                        showDialog = false
                    }) {
                        Text("Đồng ý")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}

@Composable
fun CardItemView(
    card: CardItem,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(card.title, style = MaterialTheme.typography.titleMedium)

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = { expanded = false } // TODO: mở dialog rename
                    )
                    DropdownMenuItem(
                        text = { Text("Options") },
                        onClick = { expanded = false } // TODO: mở option khác
                    )
                    DropdownMenuItem(
                        text = { Text("Export") },
                        onClick = { expanded = false } // TODO: export sau
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

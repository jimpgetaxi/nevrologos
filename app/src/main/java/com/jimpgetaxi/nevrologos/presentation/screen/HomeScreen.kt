package com.jimpgetaxi.nevrologos.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jimpgetaxi.nevrologos.presentation.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val profiles by viewModel.profiles.collectAsState()
    val currentProfile = profiles.firstOrNull()
    var chatInput by remember { mutableStateOf("") }
    var showSymptomDialog by remember { mutableStateOf(false) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onDocumentSelected(context, uri)
    }

    if (showSymptomDialog) {
        SymptomLogDialog(
            onDismiss = { showSymptomDialog = false },
            onConfirm = { type, severity ->
                viewModel.saveSymptom(type, severity)
                showSymptomDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (currentProfile != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Νευρολόγος AI",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { showSymptomDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Log Symptom", tint = MaterialTheme.colorScheme.primary)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Chat/History Area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ChatMessageBubble("AI", "Καλώς ήρθες ${currentProfile.fullName}. Πώς μπορώ να σε βοηθήσω σήμερα;")
                }
                
                items(viewModel.chatMessages) { message ->
                    ChatMessageBubble(message.first, message.second)
                }
                
                if (viewModel.chatLoading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp).align(Alignment.CenterHorizontally))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { filePickerLauncher.launch("*/*") }, modifier = Modifier.weight(1f)) {
                    Text("Αρχείο")
                }
                Button(
                    onClick = { viewModel.startEdssCalculation() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("EDSS")
                }
                Button(
                    onClick = { viewModel.startDietAnalysis() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Διατροφή")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chat Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = { Text(if (viewModel.isDietMode) "Περιγράψτε το γεύμα σας..." else "Ρωτήστε κάτι...") },
                    modifier = Modifier.weight(1f),
                    maxLines = 3
                )
                IconButton(
                    onClick = {
                        if (chatInput.isNotBlank()) {
                            viewModel.sendChatQuery(chatInput)
                            chatInput = ""
                        }
                    },
                    enabled = chatInput.isNotBlank() && !viewModel.chatLoading
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun SymptomLogDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    var type by remember { mutableStateOf("Μούδιασμα") }
    var severity by remember { mutableFloatStateOf(5f) }
    val symptoms = listOf("Μούδιασμα", "Κόπωση", "Θολή Όραση", "Αδυναμία", "Πόνος", "Άλλο")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Καταγραφή Συμπτώματος") },
        text = {
            Column {
                Text("Τύπος Συμπτώματος:")
                var expanded by remember { mutableStateOf(false) }
                Box {
                    TextButton(onClick = { expanded = true }) { Text(type) }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        symptoms.forEach { s ->
                            DropdownMenuItem(text = { Text(s) }, onClick = { type = s; expanded = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ένταση: ${severity.toInt()}")
                Slider(value = severity, onValueChange = { severity = it }, valueRange = 1f..10f, steps = 8)
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(type, severity.toInt()) }) { Text("Αποθήκευση") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Ακύρωση") }
        }
    )
}

@Composable
fun ChatMessageBubble(role: String, content: String) {
    val backgroundColor = when (role) {
        "User" -> MaterialTheme.colorScheme.primaryContainer
        "AI" -> MaterialTheme.colorScheme.secondaryContainer
        "System" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> Color.LightGray
    }
    
    val alignment = if (role == "User") Alignment.End else Alignment.Start

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Box(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(12.dp)
        ) {
            Text(text = content, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = role, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 1.dp, start = 4.dp, end = 4.dp))
    }
}

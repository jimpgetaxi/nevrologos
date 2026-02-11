package com.jimpgetaxi.nevrologos.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
    
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onDocumentSelected(context, uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (currentProfile != null) {
            Text(
                text = "Προσωπικός Νευρολόγος AI",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
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
                // Initial Diagnosis as first message
                item {
                    ChatMessageBubble("AI", "Καλώς ήρθες ${currentProfile.fullName}. Η αρχική σου διάγνωση είναι: ${currentProfile.diagnosis}")
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
                Button(
                    onClick = { filePickerLauncher.launch("application/pdf") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("PDF")
                }
                Button(
                    onClick = { filePickerLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("Εικόνα")
                }
                Button(
                    onClick = { viewModel.startEdssCalculation() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("EDSS")
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
                    placeholder = { Text("Ρωτήστε κάτι τον νευρολόγο...") },
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
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
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
fun ChatMessageBubble(role: String, content: String) {
    val backgroundColor = when (role) {
        "User" -> MaterialTheme.colorScheme.primaryContainer
        "AI" -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.LightGray
    }
    
    val alignment = if (role == "User") Alignment.End else Alignment.Start

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(12.dp)
        ) {
            Text(text = content, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = role, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp))
    }
}

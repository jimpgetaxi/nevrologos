package com.jimpgetaxi.nevrologos.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jimpgetaxi.nevrologos.presentation.viewmodel.MainViewModel

@Composable
fun ProfileSetupScreen(viewModel: MainViewModel, onProfileCreated: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var diet by remember { mutableStateOf("Mediterranean") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Καλώς ήρθατε στον Προσωπικό Νευρολόγο AI", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ονοματεπώνυμο") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = diagnosis,
            onValueChange = { diagnosis = it },
            label = { Text("Διάγνωση (π.χ. RRMS)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Επιλογή Διατροφής:")
        listOf("Mediterranean", "Swank", "Wahls").forEach { type ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                RadioButton(selected = diet == type, onClick = { diet = type })
                Text(type)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (name.isNotBlank() && diagnosis.isNotBlank()) {
                    viewModel.createProfile(name, diagnosis, System.currentTimeMillis(), diet)
                    onProfileCreated()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Δημιουργία Προφίλ")
        }
    }
}

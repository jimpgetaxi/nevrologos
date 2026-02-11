package com.jimpgetaxi.nevrologos.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jimpgetaxi.nevrologos.presentation.viewmodel.MainViewModel

@Composable
fun ProfileSetupScreen(viewModel: MainViewModel, onProfileCreated: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var diet by remember { mutableStateOf("Mediterranean") }
    var showDiagnosisResult by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Δημιουργία Ιατρικού Προφίλ", style = MaterialTheme.typography.headlineMedium)

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ονοματεπώνυμο") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Περιγράψτε τα συμπτώματά σας ή το ιστορικό σας για AI Διάγνωση:", style = MaterialTheme.typography.titleMedium)
        
        OutlinedTextField(
            value = symptoms,
            onValueChange = { symptoms = it },
            label = { Text("Συμπτώματα / Ιστορικό") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            placeholder = { Text("π.χ. Μούδιασμα στο δεξί χέρι, θολή όραση για 2 μέρες...") }
        )

        Button(
            onClick = { viewModel.getAiDiagnosisSuggestion(symptoms) },
            enabled = symptoms.isNotBlank() && !viewModel.diagnosisLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (viewModel.diagnosisLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Ανάλυση Συμπτωμάτων (AI)")
            }
        }

        if (viewModel.tempDiagnosis.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Πρόταση AI:", style = MaterialTheme.typography.titleSmall)
                    Text(viewModel.tempDiagnosis, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Divider()

        Text("Επιλογή Διατροφικού Πρωτοκόλλου:", style = MaterialTheme.typography.titleMedium)
        listOf("Mediterranean", "Swank", "Wahls").forEach { type ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                RadioButton(selected = diet == type, onClick = { diet = type })
                Text(type)
            }
        }

        Button(
            onClick = {
                if (name.isNotBlank() && viewModel.tempDiagnosis.isNotBlank()) {
                    viewModel.createProfile(name, viewModel.tempDiagnosis, System.currentTimeMillis(), diet)
                    onProfileCreated()
                }
            },
            enabled = name.isNotBlank() && viewModel.tempDiagnosis.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Οριστικοποίηση & Είσοδος")
        }
    }
}

package com.jimpgetaxi.nevrologos.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jimpgetaxi.nevrologos.presentation.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val profiles by viewModel.profiles.collectAsState()
    val currentProfile = profiles.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (currentProfile != null) {
            Text("Καλώς ήρθες, ${currentProfile.fullName}", style = MaterialTheme.typography.headlineLarge)
            Text("Διάγνωση: ${currentProfile.diagnosis}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Αυτή είναι η αρχική οθόνη του προσωπικού σου νευρολόγου.", style = MaterialTheme.typography.bodyMedium)
        } else {
            Text("Φόρτωση...")
        }
    }
}

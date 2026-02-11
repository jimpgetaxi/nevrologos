package com.jimpgetaxi.nevrologos.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimpgetaxi.nevrologos.data.entity.UserProfile
import com.jimpgetaxi.nevrologos.data.repository.AiRepository
import com.jimpgetaxi.nevrologos.data.repository.NeurologyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NeurologyRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    val profiles: StateFlow<List<UserProfile>> = repository.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var availableModels by mutableStateOf<List<com.jimpgetaxi.nevrologos.data.network.AiModel>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var selectedModel by mutableStateOf("gemini-2.0-flash")
        private set

    var tempDiagnosis by mutableStateOf("")
        private set

    var diagnosisLoading by mutableStateOf(false)
        private set

    var chatMessages = mutableStateListOf<Pair<String, String>>() // Role to Content
        private set

    var chatLoading by mutableStateOf(false)
        private set

    var isEdssMode by mutableStateOf(false)
        private set

    private val edssHistory = mutableListOf<com.google.ai.client.generativeai.type.Content>()

    init {
        loadModels()
    }

    fun startEdssCalculation() {
        isEdssMode = true
        chatMessages.clear()
        edssHistory.clear()
        chatMessages.add("AI" to "Ξεκινάμε τον υπολογισμό της κλίμακας EDSS. Πείτε μου, πόσα μέτρα μπορείτε να περπατήσετε χωρίς βοήθεια ή ξεκούραση;")
    }

    fun sendChatQuery(query: String) {
        viewModelScope.launch {
            chatMessages.add("User" to query)
            chatLoading = true
            
            if (isEdssMode) {
                val response = aiRepository.startEdssFlow(query, edssHistory.toList())
                chatMessages.add("AI" to response)
                // Update history for next turn
                edssHistory.add(com.google.ai.client.generativeai.type.content("user") { text(query) })
                edssHistory.add(com.google.ai.client.generativeai.type.content("model") { text(response) })
            } else {
                val response = aiRepository.getAiDiagnosis(query)
                chatMessages.add("AI" to response)
            }
            chatLoading = false
        }
    }

    fun onDocumentSelected(context: android.content.Context, uri: android.net.Uri?) {
        uri?.let { selectedUri ->
            viewModelScope.launch {
                chatLoading = true
                chatMessages.add("System" to "Ξεκινάει η ανάλυση του αρχείου...")
                
                try {
                    val contentResolver = context.contentResolver
                    val mimeType = contentResolver.getType(selectedUri) ?: "application/octet-stream"
                    val inputStream = contentResolver.openInputStream(selectedUri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        val analysis = aiRepository.analyzeMedicalFile(bytes, mimeType)
                        chatMessages.add("AI" to analysis)
                    } else {
                        chatMessages.add("System" to "Αποτυχία ανάγνωσης αρχείου.")
                    }
                } catch (e: Exception) {
                    chatMessages.add("System" to "Σφάλμα: ${e.localizedMessage}")
                } finally {
                    chatLoading = false
                }
            }
        }
    }

    fun loadModels() {
        viewModelScope.launch {
            try {
                errorMessage = null
                val fetched = aiRepository.fetchAvailableModels()
                if (fetched.isNotEmpty()) {
                    availableModels = fetched
                    val best = fetched.first().name.replace("models/", "")
                    selectModel(best)
                } else {
                    errorMessage = "Δεν βρέθηκαν μοντέλα"
                }
            } catch (e: Exception) {
                errorMessage = "Σφάλμα φόρτωσης: ${e.localizedMessage}"
                availableModels = emptyList()
            }
        }
    }

    fun selectModel(modelName: String) {
        selectedModel = modelName
        aiRepository.setModel(modelName)
    }

    fun getAiDiagnosisSuggestion(symptoms: String) {
        viewModelScope.launch {
            diagnosisLoading = true
            tempDiagnosis = aiRepository.getAiDiagnosis(symptoms)
            diagnosisLoading = false
        }
    }

    fun createProfile(name: String, diagnosis: String, dob: Long, diet: String) {
        viewModelScope.launch {
            val profile = UserProfile(
                userId = UUID.randomUUID().toString(),
                fullName = name,
                diagnosis = diagnosis,
                dob = dob,
                dietType = diet
            )
            repository.insertProfile(profile)
        }
    }
}

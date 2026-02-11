package com.jimpgetaxi.nevrologos.presentation.viewmodel

import androidx.compose.runtime.getValue
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

    var tempDiagnosis by mutableStateOf("")
        private set

    var diagnosisLoading by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            aiRepository.fetchAvailableModels()
        }
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

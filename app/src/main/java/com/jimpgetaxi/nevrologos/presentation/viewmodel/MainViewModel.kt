package com.jimpgetaxi.nevrologos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimpgetaxi.nevrologos.data.entity.UserProfile
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
    private val repository: NeurologyRepository
) : ViewModel() {

    val profiles: StateFlow<List<UserProfile>> = repository.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

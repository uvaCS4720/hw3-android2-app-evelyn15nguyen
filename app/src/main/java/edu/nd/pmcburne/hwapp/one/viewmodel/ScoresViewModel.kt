package edu.nd.pmcburne.hwapp.one.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.AppDatabase
import edu.nd.pmcburne.hwapp.one.data.GameEntity
import edu.nd.pmcburne.hwapp.one.model.Gender
import edu.nd.pmcburne.hwapp.one.network.ScoresApiService
import edu.nd.pmcburne.hwapp.one.repository.ScoresRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ScoresUiState(
    val selectedDate: String = todayDateString(),
    val selectedGender: Gender = Gender.MEN,
    val games: List<GameEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val message: String? = null
)

class ScoresViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScoresRepository(
        api = ScoresApiService.create(),
        db = AppDatabase.getDatabase(application)
    )

    private val _uiState = MutableStateFlow(ScoresUiState())
    val uiState: StateFlow<ScoresUiState> = _uiState.asStateFlow()

    init {
        refreshScores()
    }

    // trigger the refresh when gender value is selected
    fun onGenderSelected(gender: Gender) {
        _uiState.value = _uiState.value.copy(selectedGender = gender)
        refreshScores()
    }

    // trigger the refresh when date value is selected
    fun onDateSelected(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        refreshScores()
    }

    fun refreshScores() {
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.value = currentState.copy(
                isLoading = true,
                message = null
            )

            val result = repository.loadScores(
                date = currentState.selectedDate,
                gender = currentState.selectedGender
            )

            _uiState.value = _uiState.value.copy(
                games = result.games,
                isLoading = false,
                isOffline = result.isOffline,
                message = result.errorMessage
            )
        }
    }

    companion object {
        private fun todayDateString(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return formatter.format(Date())
        }
    }
}

fun todayDateString(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return formatter.format(Date())
}
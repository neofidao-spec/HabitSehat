package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.model.Challenge
import com.habitsehat.app.data.model.ChallengeProgress
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChallengeWithProgress(
    val challenge: Challenge,
    val progress: ChallengeProgress? = null
)

data class ChallengesUiState(
    val isLoading: Boolean = true,
    val challenges: List<ChallengeWithProgress> = emptyList(),
    val completedBadges: List<ChallengeWithProgress> = emptyList()
)

class ChallengesViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    init {
        loadChallenges()
    }

    fun loadChallenges() {
        viewModelScope.launch {
            _uiState.value = ChallengesUiState(isLoading = true)
            try {
                repository.addDefaultChallenges()
                val allChallenges = repository.getAllChallenges()
                val allProgress = repository.getAllProgress()

                val active = allChallenges
                    .filter { challenge ->
                        val progress = allProgress.find { it.challengeId == challenge.id }
                        progress == null || !progress.completed
                    }
                    .map { challenge ->
                        val progress = allProgress.find { it.challengeId == challenge.id }
                        ChallengeWithProgress(challenge, progress)
                    }

                val completed = allProgress
                    .filter { it.completed }
                    .mapNotNull { progress ->
                        val challenge = allChallenges.find { it.id == progress.challengeId }
                        challenge?.let { ChallengeWithProgress(it, progress) }
                    }

                _uiState.value = ChallengesUiState(
                    isLoading = false,
                    challenges = active,
                    completedBadges = completed
                )
            } catch (e: Exception) {
                _uiState.value = ChallengesUiState(isLoading = false)
            }
        }
    }

    fun joinChallenge(challengeId: Long) {
        viewModelScope.launch {
            repository.joinChallenge(challengeId)
            loadChallenges()
        }
    }
}

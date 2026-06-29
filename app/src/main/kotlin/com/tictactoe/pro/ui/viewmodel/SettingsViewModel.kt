package com.tictactoe.pro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictactoe.pro.data.repository.SettingsRepository
import com.tictactoe.pro.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setThemeStyle(style: ThemeStyle) = viewModelScope.launch { settingsRepository.setThemeStyle(style) }
    fun setDynamicColor(enabled: Boolean) = viewModelScope.launch { settingsRepository.setDynamicColor(enabled) }
    fun setSoundEnabled(enabled: Boolean) = viewModelScope.launch { settingsRepository.setSoundEnabled(enabled) }
    fun setHapticEnabled(enabled: Boolean) = viewModelScope.launch { settingsRepository.setHapticEnabled(enabled) }
    fun setShowHints(show: Boolean) = viewModelScope.launch { settingsRepository.setShowHints(show) }
    fun setAnimationSpeed(speed: AnimationSpeed) = viewModelScope.launch { settingsRepository.setAnimationSpeed(speed) }
    fun setPreferredBoardSize(size: BoardSize) = viewModelScope.launch { settingsRepository.setPreferredBoardSize(size) }
    fun setPreferredDifficulty(diff: Difficulty) = viewModelScope.launch { settingsRepository.setPreferredDifficulty(diff) }
    fun setPreferredGameMode(mode: GameMode) = viewModelScope.launch { settingsRepository.setPreferredGameMode(mode) }
    fun setPlayerXName(name: String) = viewModelScope.launch { settingsRepository.setPlayerXName(name) }
    fun setPlayerOName(name: String) = viewModelScope.launch { settingsRepository.setPlayerOName(name) }
}

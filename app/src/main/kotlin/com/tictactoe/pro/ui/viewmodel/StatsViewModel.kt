package com.tictactoe.pro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictactoe.pro.data.repository.GameRepository
import com.tictactoe.pro.domain.model.Achievement
import com.tictactoe.pro.domain.model.GameStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StatsUiState(
    val stats: GameStats = GameStats(),
    val achievements: List<Achievement> = emptyList(),
    val recentGames: List<RecentGame> = emptyList(),
    val unlockedCount: Int = 0,
    val totalAchievements: Int = 0
)

data class RecentGame(
    val winner: String?,
    val gameMode: String,
    val boardSize: Int,
    val moveCount: Int,
    val timestamp: Long
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        gameRepository.getStats(),
        gameRepository.getAchievements(),
        gameRepository.getRecentGames(),
        gameRepository.getUnlockedAchievementCount()
    ) { stats, achievements, games, unlockedCount ->
        StatsUiState(
            stats = stats,
            achievements = achievements,
            recentGames = games.map { g ->
                RecentGame(g.winner, g.gameMode, g.boardSize, g.moveCount, g.timestamp)
            },
            unlockedCount = unlockedCount,
            totalAchievements = achievements.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())
}

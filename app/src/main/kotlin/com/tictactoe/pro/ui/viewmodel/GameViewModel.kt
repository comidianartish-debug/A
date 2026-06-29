package com.tictactoe.pro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictactoe.pro.data.local.entity.GameEntity
import com.tictactoe.pro.data.repository.GameRepository
import com.tictactoe.pro.domain.model.*
import com.tictactoe.pro.domain.usecase.AiEngine
import com.tictactoe.pro.domain.usecase.GameEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val board: List<List<Player?>> = List(3) { List(3) { null } },
    val currentPlayer: Player = Player.X,
    val gameResult: GameResult = GameResult.InProgress,
    val winningCells: List<Pair<Int, Int>> = emptyList(),
    val moveCount: Int = 0,
    val isAiThinking: Boolean = false,
    val moveHistory: List<Triple<Int, Int, Player>> = emptyList(),
    val scores: Map<Player, Int> = mapOf(Player.X to 0, Player.O to 0),
    val draws: Int = 0,
    val timeLeft: Int = 0,
    val config: GameConfig = GameConfig(),
    val showResultDialog: Boolean = false,
    val newlyUnlockedAchievement: Achievement? = null,
    val ultimateState: UltimateGameState? = null,
    val gameStartTime: Long = System.currentTimeMillis()
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameEngine: GameEngine,
    private val aiEngine: AiEngine,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var aiJob: Job? = null

    fun startGame(config: GameConfig) {
        val (size, winLength) = gameEngine.getBoardSize(config.boardSize)
        val board = gameEngine.createBoard(size)
        _uiState.update { state ->
            state.copy(
                board = board,
                currentPlayer = Player.X,
                gameResult = GameResult.InProgress,
                winningCells = emptyList(),
                moveCount = 0,
                isAiThinking = false,
                moveHistory = emptyList(),
                config = config,
                showResultDialog = false,
                gameStartTime = System.currentTimeMillis(),
                ultimateState = if (config.gameMode == GameMode.ULTIMATE) UltimateGameState() else null
            )
        }
        if (config.gameMode == GameMode.BLITZ) startTimer()
        if (config.gameMode != GameMode.ULTIMATE && config.gameMode == GameMode.VS_AI && Player.X != config.humanPlayer) {
            triggerAiMove()
        }
    }

    fun makeMove(row: Int, col: Int) {
        val state = _uiState.value
        if (state.gameResult !is GameResult.InProgress || state.isAiThinking) return
        if (state.config.gameMode == GameMode.GRAVITY) {
            makeGravityMove(col)
            return
        }
        if (state.config.gameMode == GameMode.ULTIMATE) {
            makeUltimateMove(row, col)
            return
        }
        val newBoard = gameEngine.makeMove(state.board, row, col, state.currentPlayer) ?: return
        val (_, winLength) = gameEngine.getBoardSize(state.config.boardSize)
        val result = gameEngine.checkResult(newBoard, winLength)
        val newHistory = state.moveHistory + Triple(row, col, state.currentPlayer)

        _uiState.update {
            it.copy(
                board = newBoard,
                currentPlayer = state.currentPlayer.opponent(),
                gameResult = result,
                winningCells = if (result is GameResult.Winner) result.winningCells else emptyList(),
                moveCount = it.moveCount + 1,
                moveHistory = newHistory,
                showResultDialog = result !is GameResult.InProgress
            )
        }

        if (result !is GameResult.InProgress) {
            onGameFinished(result)
        } else if (state.config.gameMode == GameMode.VS_AI && state.currentPlayer.opponent() != state.config.humanPlayer) {
            triggerAiMove()
        }
    }

    private fun makeGravityMove(col: Int) {
        val state = _uiState.value
        val (size, winLength) = gameEngine.getBoardSize(state.config.boardSize)
        val result2 = gameEngine.makeGravityMove(state.board, col, state.currentPlayer) ?: return
        val (newBoard, landedRow) = result2
        val result = gameEngine.checkResult(newBoard, winLength)
        val newHistory = state.moveHistory + Triple(landedRow, col, state.currentPlayer)

        _uiState.update {
            it.copy(
                board = newBoard,
                currentPlayer = state.currentPlayer.opponent(),
                gameResult = result,
                winningCells = if (result is GameResult.Winner) result.winningCells else emptyList(),
                moveCount = it.moveCount + 1,
                moveHistory = newHistory,
                showResultDialog = result !is GameResult.InProgress
            )
        }
        if (result !is GameResult.InProgress) onGameFinished(result)
        else if (state.config.gameMode == GameMode.VS_AI && state.currentPlayer.opponent() != state.config.humanPlayer) triggerAiMove()
    }

    private fun makeUltimateMove(row: Int, col: Int) {
        // Simplified ultimate logic — full impl in production
        makeMove(row, col)
    }

    private fun triggerAiMove() {
        val state = _uiState.value
        if (state.gameResult !is GameResult.InProgress) return
        _uiState.update { it.copy(isAiThinking = true) }
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            delay(400L)
            val currentState = _uiState.value
            val (_, winLength) = gameEngine.getBoardSize(currentState.config.boardSize)
            val move = if (currentState.config.gameMode == GameMode.GRAVITY) {
                val col = aiEngine.getBestGravityMove(
                    currentState.board,
                    currentState.currentPlayer,
                    currentState.config.difficulty,
                    winLength
                )
                _uiState.update { it.copy(isAiThinking = false) }
                makeGravityMove(col)
                return@launch
            } else {
                aiEngine.getBestMove(
                    currentState.board,
                    currentState.currentPlayer,
                    currentState.config.difficulty,
                    winLength
                )
            }
            _uiState.update { it.copy(isAiThinking = false) }
            val newBoard = gameEngine.makeMove(currentState.board, move.first, move.second, currentState.currentPlayer) ?: return@launch
            val result = gameEngine.checkResult(newBoard, winLength)
            _uiState.update {
                it.copy(
                    board = newBoard,
                    currentPlayer = currentState.currentPlayer.opponent(),
                    gameResult = result,
                    winningCells = if (result is GameResult.Winner) result.winningCells else emptyList(),
                    moveCount = it.moveCount + 1,
                    moveHistory = it.moveHistory + Triple(move.first, move.second, currentState.currentPlayer),
                    showResultDialog = result !is GameResult.InProgress
                )
            }
            if (result !is GameResult.InProgress) onGameFinished(result)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = 5
            while (remaining > 0 && _uiState.value.gameResult is GameResult.InProgress) {
                _uiState.update { it.copy(timeLeft = remaining) }
                delay(1000)
                remaining--
            }
            if (_uiState.value.gameResult is GameResult.InProgress) {
                _uiState.update { state ->
                    val nextPlayer = state.currentPlayer.opponent()
                    state.copy(currentPlayer = nextPlayer, timeLeft = 5)
                }
                if (_uiState.value.config.gameMode == GameMode.VS_AI &&
                    _uiState.value.currentPlayer != _uiState.value.config.humanPlayer) {
                    triggerAiMove()
                } else {
                    startTimer()
                }
            }
        }
    }

    private fun onGameFinished(result: GameResult) {
        timerJob?.cancel()
        val state = _uiState.value
        val isVsAi = state.config.gameMode == GameMode.VS_AI
        val playerWon = result is GameResult.Winner && result.player == state.config.humanPlayer
        val durationMs = System.currentTimeMillis() - state.gameStartTime

        viewModelScope.launch {
            val winner = if (result is GameResult.Winner) result.player.name else null
            gameRepository.saveGame(
                GameEntity(
                    winner = winner,
                    gameMode = state.config.gameMode.name,
                    boardSize = state.config.boardSize.size,
                    difficulty = if (isVsAi) state.config.difficulty.name else null,
                    moveCount = state.moveCount,
                    durationMs = durationMs,
                    boardSnapshot = ""
                )
            )
            gameRepository.updateStats { stats ->
                stats.copy(
                    totalGames = stats.totalGames + 1,
                    wins = if (playerWon || (result is GameResult.Winner && !isVsAi)) stats.wins + 1 else stats.wins,
                    losses = if (result is GameResult.Winner && isVsAi && !playerWon) stats.losses + 1 else stats.losses,
                    draws = if (result is GameResult.Draw) stats.draws + 1 else stats.draws,
                    winStreak = if (playerWon) stats.winStreak + 1 else 0,
                    bestStreak = if (playerWon) maxOf(stats.bestStreak, stats.winStreak + 1) else stats.bestStreak,
                    gamesVsAi = if (isVsAi) stats.gamesVsAi + 1 else stats.gamesVsAi,
                    winsVsAi = if (isVsAi && playerWon) stats.winsVsAi + 1 else stats.winsVsAi,
                    totalMoves = stats.totalMoves + state.moveCount
                )
            }
            val newScores = state.scores.toMutableMap()
            when (result) {
                is GameResult.Winner -> newScores[result.player] = (newScores[result.player] ?: 0) + 1
                is GameResult.Draw -> _uiState.update { it.copy(draws = it.draws + 1) }
                else -> {}
            }
            _uiState.update { it.copy(scores = newScores) }
        }
    }

    fun resetBoard() {
        val config = _uiState.value.config
        startGame(config)
    }

    fun undoMove() {
        val state = _uiState.value
        if (state.moveHistory.isEmpty() || state.gameResult !is GameResult.InProgress) return
        val newHistory = state.moveHistory.dropLast(1)
        val (size, _) = gameEngine.getBoardSize(state.config.boardSize)
        var board = gameEngine.createBoard(size)
        newHistory.forEach { (r, c, p) ->
            board = gameEngine.makeMove(board, r, c, p) ?: board
        }
        _uiState.update {
            it.copy(
                board = board,
                currentPlayer = state.currentPlayer.opponent(),
                moveCount = it.moveCount - 1,
                moveHistory = newHistory,
                gameResult = GameResult.InProgress,
                winningCells = emptyList()
            )
        }
    }

    fun dismissResultDialog() = _uiState.update { it.copy(showResultDialog = false) }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        aiJob?.cancel()
    }
}

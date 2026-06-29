package com.tictactoe.pro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictactoe.pro.domain.model.Player
import com.tictactoe.pro.domain.model.PuzzleChallenge
import com.tictactoe.pro.domain.usecase.GameEngine
import com.tictactoe.pro.domain.usecase.PuzzleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PuzzleUiState(
    val puzzles: List<PuzzleChallenge> = emptyList(),
    val currentPuzzle: PuzzleChallenge? = null,
    val board: List<List<Player?>> = emptyList(),
    val solved: Boolean = false,
    val failed: Boolean = false,
    val solvedPuzzleIds: Set<Int> = emptySet(),
    val totalPoints: Int = 0
)

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    private val puzzleRepository: PuzzleRepository,
    private val gameEngine: GameEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(PuzzleUiState())
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    init {
        val puzzles = puzzleRepository.getAllPuzzles()
        _uiState.update { it.copy(puzzles = puzzles) }
    }

    fun selectPuzzle(puzzle: PuzzleChallenge) {
        _uiState.update {
            it.copy(
                currentPuzzle = puzzle,
                board = puzzle.board,
                solved = false,
                failed = false
            )
        }
    }

    fun makeMove(row: Int, col: Int) {
        val state = _uiState.value
        val puzzle = state.currentPuzzle ?: return
        if (state.solved || state.failed) return
        if (row == puzzle.solutionMove.first && col == puzzle.solutionMove.second) {
            val newBoard = gameEngine.makeMove(state.board, row, col, puzzle.currentPlayer) ?: return
            val newSolved = state.solvedPuzzleIds + puzzle.id
            _uiState.update {
                it.copy(
                    board = newBoard,
                    solved = true,
                    solvedPuzzleIds = newSolved,
                    totalPoints = it.totalPoints + puzzle.points
                )
            }
        } else {
            val newBoard = gameEngine.makeMove(state.board, row, col, puzzle.currentPlayer) ?: return
            _uiState.update { it.copy(board = newBoard, failed = true) }
        }
    }

    fun resetCurrentPuzzle() {
        val puzzle = _uiState.value.currentPuzzle ?: return
        _uiState.update { it.copy(board = puzzle.board, solved = false, failed = false) }
    }

    fun nextPuzzle() {
        val state = _uiState.value
        val current = state.currentPuzzle ?: return
        val nextPuzzle = state.puzzles.firstOrNull { it.id > current.id }
        if (nextPuzzle != null) selectPuzzle(nextPuzzle)
    }
}

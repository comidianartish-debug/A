package com.tictactoe.pro.domain.usecase

import com.tictactoe.pro.domain.model.Difficulty
import com.tictactoe.pro.domain.model.GameResult
import com.tictactoe.pro.domain.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.random.Random

@Singleton
class AiEngine @Inject constructor(private val gameEngine: GameEngine) {

    private val transpositionTable = HashMap<String, Pair<Int, Int>>()

    suspend fun getBestMove(
        board: List<List<Player?>>,
        aiPlayer: Player,
        difficulty: Difficulty,
        winLength: Int = 3
    ): Pair<Int, Int> = withContext(Dispatchers.Default) {
        val availableMoves = gameEngine.getAvailableMoves(board)
        if (availableMoves.isEmpty()) return@withContext Pair(0, 0)

        if (difficulty == Difficulty.RANDOM) {
            return@withContext availableMoves.random()
        }

        if (Random.nextFloat() < difficulty.errorRate) {
            return@withContext availableMoves.random()
        }

        if (difficulty.depth == 1) {
            return@withContext getHeuristicMove(board, aiPlayer, winLength)
        }

        transpositionTable.clear()
        val depth = when {
            board.size <= 3 -> difficulty.depth.coerceAtMost(9)
            board.size <= 4 -> difficulty.depth.coerceAtMost(6)
            board.size <= 5 -> difficulty.depth.coerceAtMost(4)
            else -> difficulty.depth.coerceAtMost(3)
        }

        var bestScore = Int.MIN_VALUE
        var bestMove = availableMoves.first()

        val prioritized = availableMoves.sortedByDescending { (r, c) ->
            val center = board.size / 2
            -abs(r - center) - abs(c - center)
        }

        for ((row, col) in prioritized) {
            val newBoard = gameEngine.makeMove(board, row, col, aiPlayer) ?: continue
            val score = minimax(newBoard, depth - 1, Int.MIN_VALUE, Int.MAX_VALUE, false, aiPlayer, winLength)
            if (score > bestScore) {
                bestScore = score
                bestMove = Pair(row, col)
            }
        }
        bestMove
    }

    private fun minimax(
        board: List<List<Player?>>,
        depth: Int,
        alpha: Int,
        beta: Int,
        isMaximizing: Boolean,
        aiPlayer: Player,
        winLength: Int
    ): Int {
        val key = "${board.hashCode()}_${depth}_${isMaximizing}"
        transpositionTable[key]?.let { (cachedDepth, cachedScore) ->
            if (cachedDepth >= depth) return cachedScore
        }

        val result = gameEngine.checkResult(board, winLength)
        val score = when {
            result is GameResult.Winner && result.player == aiPlayer -> 1000 + depth
            result is GameResult.Winner -> -(1000 + depth)
            result is GameResult.Draw -> 0
            depth == 0 -> evaluateBoard(board, aiPlayer, winLength)
            else -> null
        }
        if (score != null) {
            transpositionTable[key] = Pair(depth, score)
            return score
        }

        val moves = gameEngine.getAvailableMoves(board)
        var bestScore = if (isMaximizing) Int.MIN_VALUE else Int.MAX_VALUE
        var localAlpha = alpha; var localBeta = beta

        for ((row, col) in moves) {
            val currentPlayer = if (isMaximizing) aiPlayer else aiPlayer.opponent()
            val newBoard = gameEngine.makeMove(board, row, col, currentPlayer) ?: continue
            val moveScore = minimax(newBoard, depth - 1, localAlpha, localBeta, !isMaximizing, aiPlayer, winLength)
            if (isMaximizing) {
                bestScore = maxOf(bestScore, moveScore)
                localAlpha = maxOf(localAlpha, bestScore)
            } else {
                bestScore = minOf(bestScore, moveScore)
                localBeta = minOf(localBeta, bestScore)
            }
            if (localBeta <= localAlpha) break
        }

        transpositionTable[key] = Pair(depth, bestScore)
        return bestScore
    }

    private fun evaluateBoard(board: List<List<Player?>>, aiPlayer: Player, winLength: Int): Int {
        val size = board.size
        var score = 0
        val directions = listOf(Pair(0, 1), Pair(1, 0), Pair(1, 1), Pair(1, -1))
        val center = size / 2

        for (row in 0 until size) {
            for (col in 0 until size) {
                val player = board[row][col] ?: continue
                val multiplier = if (player == aiPlayer) 1 else -1
                val centerBonus = (size - abs(row - center) - abs(col - center))
                score += multiplier * centerBonus

                for ((dr, dc) in directions) {
                    var count = 0; var blocked = false
                    var r = row; var c = col
                    for (i in 0 until winLength) {
                        if (r !in 0 until size || c !in 0 until size) { blocked = true; break }
                        if (board[r][c] == player.opponent()) { blocked = true; break }
                        if (board[r][c] == player) count++
                        r += dr; c += dc
                    }
                    if (!blocked) {
                        score += multiplier * when (count) {
                            winLength - 1 -> 50
                            winLength - 2 -> 10
                            else -> count
                        }
                    }
                }
            }
        }
        return score
    }

    private fun getHeuristicMove(board: List<List<Player?>>, aiPlayer: Player, winLength: Int): Pair<Int, Int> {
        val available = gameEngine.getAvailableMoves(board)
        for ((row, col) in available) {
            val testBoard = gameEngine.makeMove(board, row, col, aiPlayer) ?: continue
            if (gameEngine.checkResult(testBoard, winLength) is GameResult.Winner) return Pair(row, col)
        }
        for ((row, col) in available) {
            val testBoard = gameEngine.makeMove(board, row, col, aiPlayer.opponent()) ?: continue
            if (gameEngine.checkResult(testBoard, winLength) is GameResult.Winner) return Pair(row, col)
        }
        val size = board.size
        val center = size / 2
        if (board[center][center] == null) return Pair(center, center)
        return available.random()
    }

    suspend fun getBestGravityMove(
        board: List<List<Player?>>,
        aiPlayer: Player,
        difficulty: Difficulty,
        winLength: Int
    ): Int = withContext(Dispatchers.Default) {
        val availableCols = gameEngine.getAvailableGravityColumns(board)
        if (availableCols.isEmpty()) return@withContext 0
        if (difficulty.ordinal <= 1) return@withContext availableCols.random()
        for (col in availableCols) {
            val result = gameEngine.makeGravityMove(board, col, aiPlayer) ?: continue
            if (gameEngine.checkResult(result.first, winLength) is GameResult.Winner) return@withContext col
        }
        for (col in availableCols) {
            val result = gameEngine.makeGravityMove(board, col, aiPlayer.opponent()) ?: continue
            if (gameEngine.checkResult(result.first, winLength) is GameResult.Winner) return@withContext col
        }
        availableCols.random()
    }
}

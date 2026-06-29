package com.tictactoe.pro.domain.usecase

import com.tictactoe.pro.domain.model.BoardSize
import com.tictactoe.pro.domain.model.GameResult
import com.tictactoe.pro.domain.model.Player
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameEngine @Inject constructor() {

    fun checkResult(board: List<List<Player?>>, winLength: Int = 3): GameResult {
        val size = board.size
        for (row in 0 until size) {
            for (col in 0 until size) {
                val player = board[row][col] ?: continue
                val directions = listOf(
                    Pair(0, 1),
                    Pair(1, 0),
                    Pair(1, 1),
                    Pair(1, -1)
                )
                for ((dr, dc) in directions) {
                    val cells = mutableListOf<Pair<Int, Int>>()
                    var r = row; var c = col
                    while (r in 0 until size && c in 0 until size && board[r][c] == player) {
                        cells.add(Pair(r, c))
                        r += dr; c += dc
                    }
                    if (cells.size >= winLength) {
                        return GameResult.Winner(player, cells)
                    }
                }
            }
        }
        val isDraw = board.all { row -> row.all { it != null } }
        return if (isDraw) GameResult.Draw else GameResult.InProgress
    }

    fun makeMove(
        board: List<List<Player?>>,
        row: Int,
        col: Int,
        player: Player
    ): List<List<Player?>>? {
        if (board[row][col] != null) return null
        return board.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (r == row && c == col) player else cell
            }
        }
    }

    fun makeGravityMove(
        board: List<List<Player?>>,
        col: Int,
        player: Player
    ): Pair<List<List<Player?>>, Int>? {
        val size = board.size
        val newBoard = board.map { it.toMutableList() }.toMutableList()
        for (row in size - 1 downTo 0) {
            if (newBoard[row][col] == null) {
                newBoard[row][col] = player
                return Pair(newBoard.map { it.toList() }, row)
            }
        }
        return null
    }

    fun getAvailableMoves(board: List<List<Player?>>): List<Pair<Int, Int>> {
        return board.flatMapIndexed { row, rowList ->
            rowList.mapIndexedNotNull { col, cell ->
                if (cell == null) Pair(row, col) else null
            }
        }
    }

    fun getAvailableGravityColumns(board: List<List<Player?>>): List<Int> {
        return (0 until board.size).filter { col ->
            board[0][col] == null
        }
    }

    fun createBoard(size: Int): List<List<Player?>> = List(size) { List(size) { null } }

    fun getBoardSize(boardSize: BoardSize): Pair<Int, Int> = Pair(boardSize.size, boardSize.winLength)

    fun countThreats(board: List<List<Player?>>, player: Player, winLength: Int): Int {
        var threats = 0
        val size = board.size
        val directions = listOf(Pair(0, 1), Pair(1, 0), Pair(1, 1), Pair(1, -1))
        for (row in 0 until size) {
            for (col in 0 until size) {
                for ((dr, dc) in directions) {
                    var count = 0; var empty = 0
                    var r = row; var c = col
                    for (i in 0 until winLength) {
                        if (r !in 0 until size || c !in 0 until size) break
                        when (board[r][c]) {
                            player -> count++
                            null -> empty++
                            else -> { count = -1; break }
                        }
                        r += dr; c += dc
                    }
                    if (count == winLength - 1 && empty == 1) threats++
                }
            }
        }
        return threats
    }
}

package com.tictactoe.pro.domain.model

import kotlinx.serialization.Serializable

enum class Player(val symbol: String, val displayName: String) {
    X("X", "Player X"),
    O("O", "Player O");

    fun opponent(): Player = if (this == X) O else X
}

enum class GameMode(val displayName: String, val description: String) {
    VS_AI("vs AI", "Challenge the computer"),
    LOCAL_2P("2 Players", "Pass and play with a friend"),
    ULTIMATE("Ultimate", "9 boards within 1 board"),
    GRAVITY("Gravity", "Pieces fall to the bottom"),
    BLITZ("Blitz", "5 seconds per move"),
    PUZZLE("Puzzle", "Solve preset challenges");
}

enum class BoardSize(val size: Int, val displayName: String, val winLength: Int) {
    THREE(3, "3×3", 3),
    FOUR(4, "4×4", 4),
    FIVE(5, "5×5", 4),
    SIX(6, "6×6", 5),
    EIGHT(8, "8×8", 5);
}

enum class Difficulty(val displayName: String, val depth: Int, val errorRate: Float) {
    RANDOM("Random", 0, 1.0f),
    BEGINNER("Beginner", 1, 0.7f),
    EASY("Easy", 2, 0.5f),
    MEDIUM("Medium", 3, 0.3f),
    HARD("Hard", 4, 0.15f),
    EXPERT("Expert", 5, 0.05f),
    MASTER("Master", 6, 0.02f),
    GRANDMASTER("Grandmaster", 7, 0.01f),
    LEGEND("Legend", 8, 0.0f),
    MYTHIC("Mythic", 9, 0.0f);
}

@Serializable
data class Cell(
    val row: Int,
    val col: Int,
    val player: Player? = null
)

sealed class GameResult {
    data class Winner(val player: Player, val winningCells: List<Pair<Int, Int>>) : GameResult()
    object Draw : GameResult()
    object InProgress : GameResult()
}

@Serializable
data class GameState(
    val board: List<List<Player?>> = List(3) { List(3) { null } },
    val currentPlayer: Player = Player.X,
    val result: String = "in_progress",
    val winningCells: List<Pair<Int, Int>> = emptyList(),
    val moveCount: Int = 0,
    val boardSize: Int = 3,
    val gameMode: GameMode = GameMode.VS_AI,
    val moveHistory: List<Pair<Int, Int>> = emptyList()
)

data class GameConfig(
    val boardSize: BoardSize = BoardSize.THREE,
    val gameMode: GameMode = GameMode.VS_AI,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val playerXName: String = "Player X",
    val playerOName: String = "Player O",
    val timeLimit: Int = 0,
    val humanPlayer: Player = Player.X
)

data class MoveHint(
    val row: Int,
    val col: Int,
    val score: Float,
    val reason: String
)

data class GameStats(
    val totalGames: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val winStreak: Int = 0,
    val bestStreak: Int = 0,
    val gamesVsAi: Int = 0,
    val winsVsAi: Int = 0,
    val totalMoves: Int = 0,
    val averageMovesPerGame: Float = 0f,
    val favoriteMode: GameMode? = null,
    val moveHeatMap: Map<String, Int> = emptyMap()
) {
    val winRate: Float get() = if (totalGames > 0) wins.toFloat() / totalGames else 0f
    val lossRate: Float get() = if (totalGames > 0) losses.toFloat() / totalGames else 0f
    val drawRate: Float get() = if (totalGames > 0) draws.toFloat() / totalGames else 0f
}

data class UltimateGameState(
    val boards: List<List<GameState>> = List(3) { List(3) { GameState() } },
    val globalBoard: List<List<Player?>> = List(3) { List(3) { null } },
    val currentPlayer: Player = Player.X,
    val activeBoard: Pair<Int, Int>? = null,
    val result: String = "in_progress"
)

data class PuzzleChallenge(
    val id: Int,
    val title: String,
    val description: String,
    val board: List<List<Player?>>,
    val currentPlayer: Player,
    val solutionMove: Pair<Int, Int>,
    val difficulty: String,
    val points: Int
)

package com.tictactoe.pro.domain.usecase

import com.tictactoe.pro.domain.model.Player
import com.tictactoe.pro.domain.model.PuzzleChallenge
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PuzzleRepository @Inject constructor() {

    fun getAllPuzzles(): List<PuzzleChallenge> = listOf(
        PuzzleChallenge(
            id = 1, title = "Block the Win",
            description = "X is about to win — play O to block!",
            board = listOf(
                listOf(Player.X, Player.X, null),
                listOf(Player.O, null, null),
                listOf(null, Player.O, null)
            ),
            currentPlayer = Player.O,
            solutionMove = Pair(0, 2),
            difficulty = "Easy", points = 10
        ),
        PuzzleChallenge(
            id = 2, title = "Seize the Victory",
            description = "X has two in a row — finish it!",
            board = listOf(
                listOf(Player.X, Player.X, null),
                listOf(Player.O, Player.O, null),
                listOf(null, null, null)
            ),
            currentPlayer = Player.X,
            solutionMove = Pair(0, 2),
            difficulty = "Easy", points = 10
        ),
        PuzzleChallenge(
            id = 3, title = "Fork Attack",
            description = "Create a fork so you win no matter what O does",
            board = listOf(
                listOf(Player.X, null, null),
                listOf(null, Player.X, null),
                listOf(Player.O, null, Player.O)
            ),
            currentPlayer = Player.X,
            solutionMove = Pair(2, 1),
            difficulty = "Medium", points = 25
        ),
        PuzzleChallenge(
            id = 4, title = "Center Control",
            description = "Control the center to force a win",
            board = listOf(
                listOf(Player.O, null, Player.X),
                listOf(null, null, null),
                listOf(Player.X, null, Player.O)
            ),
            currentPlayer = Player.X,
            solutionMove = Pair(1, 1),
            difficulty = "Medium", points = 25
        ),
        PuzzleChallenge(
            id = 5, title = "Double Threat",
            description = "Create an unstoppable double threat",
            board = listOf(
                listOf(null, Player.O, null),
                listOf(Player.O, Player.X, null),
                listOf(null, null, Player.X)
            ),
            currentPlayer = Player.X,
            solutionMove = Pair(0, 0),
            difficulty = "Hard", points = 50
        ),
        PuzzleChallenge(
            id = 6, title = "Endgame",
            description = "Find the only winning move",
            board = listOf(
                listOf(Player.X, Player.O, Player.X),
                listOf(Player.O, Player.X, Player.O),
                listOf(Player.O, null, null)
            ),
            currentPlayer = Player.X,
            solutionMove = Pair(2, 2),
            difficulty = "Hard", points = 50
        )
    )

    fun getPuzzleById(id: Int): PuzzleChallenge? = getAllPuzzles().find { it.id == id }
}

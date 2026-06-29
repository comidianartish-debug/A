package com.tictactoe.pro.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tictactoe.pro.domain.model.*
import com.tictactoe.pro.ui.components.GameBoard
import com.tictactoe.pro.ui.components.PlayerIndicatorRow
import com.tictactoe.pro.ui.components.ScoreRow
import com.tictactoe.pro.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameMode: GameMode,
    boardSize: BoardSize,
    difficulty: Difficulty,
    onNavigateBack: () -> Unit,
    onNavigateToModeSelect: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(gameMode, boardSize, difficulty) {
        viewModel.startGame(
            GameConfig(
                boardSize = boardSize,
                gameMode = gameMode,
                difficulty = difficulty
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${gameMode.displayName} — ${boardSize.displayName}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.moveHistory.isNotEmpty() && uiState.gameResult is GameResult.InProgress) {
                        IconButton(onClick = { viewModel.undoMove() }) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo")
                        }
                    }
                    IconButton(onClick = { viewModel.resetBoard() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScoreRow(
                scores = uiState.scores,
                draws = uiState.draws,
                playerXName = uiState.config.playerXName,
                playerOName = uiState.config.playerOName
            )

            if (gameMode == GameMode.BLITZ && uiState.gameResult is GameResult.InProgress) {
                BlitzTimer(timeLeft = uiState.timeLeft)
            }

            PlayerIndicatorRow(
                currentPlayer = uiState.currentPlayer,
                isAiThinking = uiState.isAiThinking,
                gameResult = uiState.gameResult,
                gameMode = gameMode
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.board.isNotEmpty()) {
                    GameBoard(
                        board = uiState.board,
                        winningCells = uiState.winningCells,
                        onCellClick = { row, col ->
                            if (gameMode != GameMode.GRAVITY) {
                                viewModel.makeMove(row, col)
                            }
                        },
                        onColumnClick = { col ->
                            if (gameMode == GameMode.GRAVITY) {
                                viewModel.makeMove(0, col)
                            }
                        },
                        isEnabled = uiState.gameResult is GameResult.InProgress && !uiState.isAiThinking,
                        gameMode = gameMode
                    )
                }
            }

            if (uiState.gameResult !is GameResult.InProgress) {
                GameResultCard(
                    result = uiState.gameResult,
                    onPlayAgain = { viewModel.resetBoard() },
                    onChangeModes = onNavigateToModeSelect,
                    playerXName = uiState.config.playerXName,
                    playerOName = uiState.config.playerOName
                )
            }
        }
    }
}

@Composable
private fun BlitzTimer(timeLeft: Int) {
    val color = if (timeLeft <= 2) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val scale by animateFloatAsState(
        targetValue = if (timeLeft <= 2) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "timer_scale"
    )
    Card(
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Text(
            text = "⏱ $timeLeft",
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GameResultCard(
    result: GameResult,
    onPlayAgain: () -> Unit,
    onChangeModes: () -> Unit,
    playerXName: String,
    playerOName: String
) {
    val (emoji, title, subtitle) = when (result) {
        is GameResult.Winner -> {
            val name = if (result.player == Player.X) playerXName else playerOName
            Triple("🏆", "$name Wins!", "Congratulations!")
        }
        is GameResult.Draw -> Triple("🤝", "It's a Draw!", "Well played by both!")
        else -> Triple("", "", "")
    }

    AnimatedVisibility(visible = true, enter = slideInVertically { 200 } + fadeIn()) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(emoji, fontSize = 48.sp)
                Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer, textAlign = TextAlign.Center)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onChangeModes, modifier = Modifier.weight(1f)) {
                        Text("Modes")
                    }
                    Button(onClick = onPlayAgain, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Play Again")
                    }
                }
            }
        }
    }
}

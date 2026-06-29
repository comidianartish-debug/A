package com.tictactoe.pro.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.pro.domain.model.GameMode
import com.tictactoe.pro.domain.model.Player
import com.tictactoe.pro.ui.theme.PlayerOColor
import com.tictactoe.pro.ui.theme.PlayerXColor
import com.tictactoe.pro.ui.theme.WinCellColor

@Composable
fun GameBoard(
    board: List<List<Player?>>,
    winningCells: List<Pair<Int, Int>>,
    onCellClick: (Int, Int) -> Unit,
    onColumnClick: (Int) -> Unit,
    isEnabled: Boolean,
    gameMode: GameMode,
    modifier: Modifier = Modifier
) {
    val size = board.size
    val cellSize = when {
        size <= 3 -> 96.dp
        size <= 4 -> 78.dp
        size <= 5 -> 64.dp
        size <= 6 -> 54.dp
        else -> 42.dp
    }
    val gap = when {
        size <= 4 -> 6.dp
        size <= 6 -> 4.dp
        else -> 3.dp
    }

    BoxWithConstraints(modifier = modifier) {
        val totalSize = cellSize * size + gap * (size - 1)
        Column(
            verticalArrangement = Arrangement.spacedBy(gap),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (gameMode == GameMode.GRAVITY) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    repeat(size) { col ->
                        Box(
                            modifier = Modifier.size(cellSize).clickable(enabled = isEnabled) {
                                onColumnClick(col)
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("▼", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                        }
                    }
                }
            }
            repeat(size) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    repeat(size) { col ->
                        val player = board[row][col]
                        val isWinning = winningCells.contains(Pair(row, col))
                        GameCell(
                            player = player,
                            isWinning = isWinning,
                            size = cellSize,
                            onClick = { onCellClick(row, col) },
                            isEnabled = isEnabled && player == null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameCell(
    player: Player?,
    isWinning: Boolean,
    size: Dp,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    var appeared by remember(player) { mutableStateOf(player == null) }
    LaunchedEffect(player) {
        if (player != null) appeared = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isWinning) 1.12f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "cell_scale"
    )
    val bgColor = when {
        isWinning -> WinCellColor.copy(alpha = 0.25f)
        player == null -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        isWinning -> WinCellColor
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(RoundedCornerShape(if (size >= 64.dp) 16.dp else 10.dp))
            .background(bgColor)
            .border(
                width = if (isWinning) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(if (size >= 64.dp) 16.dp else 10.dp)
            )
            .clickable(enabled = isEnabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (player != null) {
            AnimatedVisibility(
                visible = appeared,
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn()
            ) {
                val fontSize = when {
                    size >= 96.dp -> 40.sp
                    size >= 78.dp -> 32.sp
                    size >= 64.dp -> 26.sp
                    size >= 54.dp -> 22.sp
                    else -> 18.sp
                }
                Text(
                    text = player.symbol,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = if (player == Player.X) PlayerXColor else PlayerOColor
                )
            }
        }
    }
}

@Composable
fun PlayerIndicatorRow(
    currentPlayer: Player,
    isAiThinking: Boolean,
    gameResult: com.tictactoe.pro.domain.model.GameResult,
    gameMode: GameMode
) {
    val isInProgress = gameResult is com.tictactoe.pro.domain.model.GameResult.InProgress
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isAiThinking) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text("AI is thinking...", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary)
        } else if (isInProgress) {
            val color = if (currentPlayer == Player.X) PlayerXColor else PlayerOColor
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f))
                    .border(2.dp, color, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(currentPlayer.symbol, fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text("${currentPlayer.displayName}'s turn", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ScoreRow(
    scores: Map<Player, Int>,
    draws: Int,
    playerXName: String,
    playerOName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScoreChip(
            label = playerXName,
            score = scores[Player.X] ?: 0,
            color = PlayerXColor,
            symbol = "✕"
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("DRAWS", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(draws.toString(), style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }
        ScoreChip(
            label = playerOName,
            score = scores[Player.O] ?: 0,
            color = PlayerOColor,
            symbol = "○"
        )
    }
}

@Composable
private fun ScoreChip(label: String, score: Int, color: Color, symbol: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(symbol, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(score.toString(), style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f), maxLines = 1)
        }
    }
}

package com.tictactoe.pro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tictactoe.pro.ui.viewmodel.StatsViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats = uiState.stats

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Text("Overall Record", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            StatBox("Total", stats.totalGames.toString(), MaterialTheme.colorScheme.primary)
                            StatBox("Wins", stats.wins.toString(), MaterialTheme.colorScheme.tertiary)
                            StatBox("Losses", stats.losses.toString(), MaterialTheme.colorScheme.error)
                            StatBox("Draws", stats.draws.toString(), MaterialTheme.colorScheme.secondary)
                        }
                        Spacer(Modifier.height(16.dp))
                        if (stats.totalGames > 0) {
                            Text("Win Rate", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { stats.winRate },
                                modifier = Modifier.fillMaxWidth().height(10.dp),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Text(
                                "${(stats.winRate * 100).roundToInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Text("Streaks & Performance", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        StatRow("Current Win Streak", "🔥 ${stats.winStreak}")
                        StatRow("Best Win Streak", "⭐ ${stats.bestStreak}")
                        StatRow("Average Moves/Game",
                            "${stats.averageMovesPerGame.let { "%.1f".format(it) }}")
                        StatRow("Total Moves Played", stats.totalMoves.toString())
                    }
                }
            }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Text("vs AI Performance", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        StatRow("Games vs AI", stats.gamesVsAi.toString())
                        StatRow("Wins vs AI", "🤖 ${stats.winsVsAi}")
                        if (stats.gamesVsAi > 0) {
                            val aiWinRate = (stats.winsVsAi * 100f / stats.gamesVsAi).roundToInt()
                            StatRow("AI Win Rate", "$aiWinRate%")
                        }
                    }
                }
            }

            if (uiState.recentGames.isNotEmpty()) {
                item {
                    Text("Recent Games", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                }
                items(uiState.recentGames.take(10)) { game ->
                    val fmt = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
                    val date = fmt.format(Date(game.timestamp))
                    val resultText = when (game.winner) {
                        null -> "Draw"
                        "X" -> "X Wins"
                        "O" -> "O Wins"
                        else -> "Unknown"
                    }
                    val emoji = when (game.winner) { null -> "🤝"; "X" -> "✕"; else -> "○" }
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$emoji $resultText", fontWeight = FontWeight.SemiBold)
                            Text("${game.boardSize}×${game.boardSize} · ${game.moveCount} moves",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(date, style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
}

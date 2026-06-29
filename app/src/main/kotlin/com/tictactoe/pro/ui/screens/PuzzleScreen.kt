package com.tictactoe.pro.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tictactoe.pro.domain.model.PuzzleChallenge
import com.tictactoe.pro.ui.components.GameBoard
import com.tictactoe.pro.ui.viewmodel.PuzzleViewModel
import com.tictactoe.pro.domain.model.GameMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    onNavigateBack: () -> Unit,
    viewModel: PuzzleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val current = uiState.currentPuzzle

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Puzzle Mode")
                        Text("${uiState.solvedPuzzleIds.size}/${uiState.puzzles.size} solved · ${uiState.totalPoints} pts",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (current == null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(uiState.puzzles) { puzzle ->
                    PuzzleListCard(
                        puzzle = puzzle,
                        isSolved = puzzle.id in uiState.solvedPuzzleIds,
                        onClick = { viewModel.selectPuzzle(puzzle) }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(current.title, style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                        Text(current.description, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Badge { Text(current.difficulty) }
                            Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                Text("+${current.points} pts")
                            }
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    GameBoard(
                        board = uiState.board,
                        winningCells = if (uiState.solved) listOf(current.solutionMove) else emptyList(),
                        onCellClick = { row, col -> viewModel.makeMove(row, col) },
                        onColumnClick = {},
                        isEnabled = !uiState.solved && !uiState.failed,
                        gameMode = GameMode.VS_AI
                    )
                }

                AnimatedVisibility(visible = uiState.solved || uiState.failed) {
                    val (emoji, title, color) = if (uiState.solved)
                        Triple("✅", "Correct! +${current.points} pts", MaterialTheme.colorScheme.tertiary)
                    else
                        Triple("❌", "Wrong move! Try again.", MaterialTheme.colorScheme.error)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(emoji, fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(title, style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, color = color)
                        }
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { viewModel.resetCurrentPuzzle() }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Reset")
                    }
                    if (uiState.solved) {
                        Button(onClick = { viewModel.nextPuzzle() }, modifier = Modifier.weight(1f)) {
                            Text("Next Puzzle")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                    OutlinedButton(onClick = { viewModel.selectPuzzle(uiState.puzzles.first())
                        viewModel.uiState.value.currentPuzzle.let {
                            // go back to list
                        }
                    }, modifier = Modifier.weight(1f)) {
                        Text("List")
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzleListCard(
    puzzle: PuzzleChallenge,
    isSolved: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSolved)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(if (isSolved) "✅" else "🧩", fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(puzzle.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(puzzle.description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            Column(horizontalAlignment = Alignment.End) {
                Badge { Text(puzzle.difficulty) }
                Spacer(Modifier.height(4.dp))
                Text("+${puzzle.points} pts", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

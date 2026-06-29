package com.tictactoe.pro.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tictactoe.pro.domain.model.*
import com.tictactoe.pro.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelectScreen(
    onNavigateToGame: (GameMode, BoardSize, Difficulty) -> Unit,
    onNavigateToPuzzles: () -> Unit,
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.settings.collectAsState()
    var selectedMode by remember { mutableStateOf(settings.preferredGameMode) }
    var selectedBoardSize by remember { mutableStateOf(settings.preferredBoardSize) }
    var selectedDifficulty by remember { mutableStateOf(settings.preferredDifficulty) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Game Mode") },
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
            item { Spacer(Modifier.height(8.dp)) }

            item {
                SectionTitle("Game Mode")
                Spacer(Modifier.height(8.dp))
            }

            items(GameMode.values().toList()) { mode ->
                ModeCard(
                    mode = mode,
                    isSelected = selectedMode == mode,
                    onClick = { selectedMode = mode }
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                SectionTitle("Board Size")
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BoardSize.values().take(3).forEach { size ->
                        FilterChip(
                            selected = selectedBoardSize == size,
                            onClick = { selectedBoardSize = size },
                            label = { Text(size.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BoardSize.values().drop(3).forEach { size ->
                        FilterChip(
                            selected = selectedBoardSize == size,
                            onClick = { selectedBoardSize = size },
                            label = { Text(size.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (selectedMode == GameMode.VS_AI) {
                item {
                    Spacer(Modifier.height(8.dp))
                    SectionTitle("AI Difficulty")
                    Spacer(Modifier.height(8.dp))
                }
                items(Difficulty.values().toList()) { diff ->
                    DifficultyCard(
                        difficulty = diff,
                        isSelected = selectedDifficulty == diff,
                        onClick = { selectedDifficulty = diff }
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onNavigateToPuzzles,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Extension, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Puzzle Mode")
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onNavigateToGame(selectedMode, selectedBoardSize, selectedDifficulty) },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Start Game", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun ModeCard(mode: GameMode, isSelected: Boolean, onClick: () -> Unit) {
    val (icon, emoji) = when (mode) {
        GameMode.VS_AI -> Pair(Icons.Default.SmartToy, "🤖")
        GameMode.LOCAL_2P -> Pair(Icons.Default.People, "👥")
        GameMode.ULTIMATE -> Pair(Icons.Default.GridOn, "🏆")
        GameMode.GRAVITY -> Pair(Icons.Default.KeyboardArrowDown, "⬇️")
        GameMode.BLITZ -> Pair(Icons.Default.Timer, "⚡")
        GameMode.PUZZLE -> Pair(Icons.Default.Extension, "🧩")
    }
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(mode.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(mode.description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun DifficultyCard(difficulty: Difficulty, isSelected: Boolean, onClick: () -> Unit) {
    val color = when (difficulty.ordinal) {
        0, 1, 2 -> MaterialTheme.colorScheme.tertiary
        3, 4 -> MaterialTheme.colorScheme.secondary
        5, 6 -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.error
    }
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(2.dp, color) else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(difficulty.displayName, style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f))
            val stars = when (difficulty.ordinal) {
                0 -> "★☆☆☆☆"; 1 -> "★★☆☆☆"; 2 -> "★★★☆☆"
                3 -> "★★★★☆"; 4 -> "★★★★★"
                5 -> "⚡★★★★★"; 6 -> "⚡⚡★★★★★"
                7 -> "⚡⚡⚡★★★★★"; 8 -> "🔥⚡⚡⚡★★★★★"; else -> "💀🔥⚡⚡⚡★★★★★"
            }
            Text(stars, style = MaterialTheme.typography.bodySmall)
        }
    }
}

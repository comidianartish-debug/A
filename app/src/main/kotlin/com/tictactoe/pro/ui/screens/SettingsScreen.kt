package com.tictactoe.pro.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tictactoe.pro.domain.model.*
import com.tictactoe.pro.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item { SectionHeader("Visual Themes") }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ThemeStyle.values().toList()) { theme ->
                        ThemeChip(
                            theme = theme,
                            isSelected = settings.themeStyle == theme,
                            onClick = { viewModel.setThemeStyle(theme) }
                        )
                    }
                }
            }

            item {
                SettingSwitch(
                    title = "Dynamic Color",
                    subtitle = "Use wallpaper colors (Android 12+)",
                    icon = Icons.Default.Palette,
                    checked = settings.dynamicColor,
                    onCheckedChange = { viewModel.setDynamicColor(it) }
                )
            }

            item { SectionHeader("Gameplay") }
            item {
                SettingSwitch(
                    title = "Sound Effects",
                    subtitle = "Play sounds on moves and results",
                    icon = Icons.Default.VolumeUp,
                    checked = settings.soundEnabled,
                    onCheckedChange = { viewModel.setSoundEnabled(it) }
                )
            }
            item {
                SettingSwitch(
                    title = "Haptic Feedback",
                    subtitle = "Vibrate on interactions",
                    icon = Icons.Default.Vibration,
                    checked = settings.hapticEnabled,
                    onCheckedChange = { viewModel.setHapticEnabled(it) }
                )
            }
            item {
                SettingSwitch(
                    title = "Show Hints",
                    subtitle = "Display best move suggestion",
                    icon = Icons.Default.Lightbulb,
                    checked = settings.showHints,
                    onCheckedChange = { viewModel.setShowHints(it) }
                )
            }

            item { SectionHeader("Animation Speed") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnimationSpeed.values().forEach { speed ->
                        FilterChip(
                            selected = settings.animationSpeed == speed,
                            onClick = { viewModel.setAnimationSpeed(speed) },
                            label = { Text(speed.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item { SectionHeader("Default Preferences") }
            item { SectionHeader("Board Size") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    BoardSize.values().take(3).forEach { size ->
                        FilterChip(
                            selected = settings.preferredBoardSize == size,
                            onClick = { viewModel.setPreferredBoardSize(size) },
                            label = { Text(size.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    BoardSize.values().drop(3).forEach { size ->
                        FilterChip(
                            selected = settings.preferredBoardSize == size,
                            onClick = { viewModel.setPreferredBoardSize(size) },
                            label = { Text(size.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item { SectionHeader("Default Difficulty") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Difficulty.values().take(5).forEach { diff ->
                        FilterChip(
                            selected = settings.preferredDifficulty == diff,
                            onClick = { viewModel.setPreferredDifficulty(diff) },
                            label = { Text(diff.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Difficulty.values().drop(5).forEach { diff ->
                        FilterChip(
                            selected = settings.preferredDifficulty == diff,
                            onClick = { viewModel.setPreferredDifficulty(diff) },
                            label = { Text(diff.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
}

@Composable
private fun SettingSwitch(
    title: String, subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Card(shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun ThemeChip(theme: ThemeStyle, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = theme.displayName,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

package com.tictactoe.pro.domain.model

data class AppSettings(
    val themeStyle: ThemeStyle = ThemeStyle.MATERIAL_YOU,
    val dynamicColor: Boolean = true,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val showHints: Boolean = false,
    val showMoveNumbers: Boolean = false,
    val animationSpeed: AnimationSpeed = AnimationSpeed.NORMAL,
    val preferredBoardSize: BoardSize = BoardSize.THREE,
    val preferredDifficulty: Difficulty = Difficulty.MEDIUM,
    val preferredGameMode: GameMode = GameMode.VS_AI,
    val playerXName: String = "Player X",
    val playerOName: String = "Player O",
    val totalPoints: Int = 0
)

enum class ThemeStyle(val displayName: String) {
    MATERIAL_YOU("Material You"),
    GLASSMORPHISM("Glassmorphism"),
    NEON("Neon Glow"),
    RETRO("Retro Arcade"),
    MINIMAL("Minimalist"),
    AMOLED("AMOLED Dark"),
    GALAXY("Galaxy"),
    NATURE("Nature"),
    GOLD("Gold Premium"),
    CYBERPUNK("Cyberpunk")
}

enum class AnimationSpeed(val displayName: String, val multiplier: Float) {
    SLOW("Slow", 1.5f),
    NORMAL("Normal", 1.0f),
    FAST("Fast", 0.6f),
    INSTANT("Instant", 0.1f)
}

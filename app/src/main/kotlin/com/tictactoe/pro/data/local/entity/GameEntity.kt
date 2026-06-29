package com.tictactoe.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tictactoe.pro.domain.model.GameMode

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val winner: String?,
    val gameMode: String,
    val boardSize: Int,
    val difficulty: String?,
    val moveCount: Int,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val boardSnapshot: String
)

@Entity(tableName = "stats")
data class StatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalGames: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val winStreak: Int = 0,
    val bestStreak: Int = 0,
    val gamesVsAi: Int = 0,
    val winsVsAi: Int = 0,
    val totalMoves: Int = 0
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val achievementId: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Int = 0
)

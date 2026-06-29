package com.tictactoe.pro.data.repository

import com.tictactoe.pro.data.local.dao.AchievementDao
import com.tictactoe.pro.data.local.dao.GameDao
import com.tictactoe.pro.data.local.dao.StatsDao
import com.tictactoe.pro.data.local.entity.AchievementEntity
import com.tictactoe.pro.data.local.entity.GameEntity
import com.tictactoe.pro.data.local.entity.StatsEntity
import com.tictactoe.pro.domain.model.Achievement
import com.tictactoe.pro.domain.model.Achievements
import com.tictactoe.pro.domain.model.GameStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val statsDao: StatsDao,
    private val achievementDao: AchievementDao
) {
    fun getRecentGames(): Flow<List<GameEntity>> = gameDao.getRecentGames()

    suspend fun saveGame(game: GameEntity) {
        gameDao.insertGame(game)
    }

    fun getStats(): Flow<GameStats> = statsDao.getStats().map { entity ->
        entity?.toDomain() ?: GameStats()
    }

    suspend fun updateStats(update: (StatsEntity) -> StatsEntity) {
        val current = statsDao.getStatsOnce() ?: StatsEntity()
        statsDao.upsertStats(update(current))
    }

    fun getAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements().map { entities ->
            val entityMap = entities.associateBy { it.achievementId }
            Achievements.all.map { achievement ->
                val entity = entityMap[achievement.id]
                achievement.copy(
                    isUnlocked = entity?.isUnlocked ?: false,
                    unlockedAt = entity?.unlockedAt,
                    progress = entity?.progress ?: 0
                )
            }
        }
    }

    suspend fun unlockAchievement(achievementId: String) {
        achievementDao.upsertAchievement(
            AchievementEntity(
                achievementId = achievementId,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis(),
                progress = 1
            )
        )
    }

    suspend fun updateAchievementProgress(achievementId: String, progress: Int) {
        val existing = achievementDao.getAchievementById(achievementId)
        achievementDao.upsertAchievement(
            (existing ?: AchievementEntity(achievementId)).copy(progress = progress)
        )
    }

    fun getUnlockedAchievementCount(): Flow<Int> = achievementDao.getUnlockedCount()

    private fun StatsEntity.toDomain() = GameStats(
        totalGames = totalGames,
        wins = wins,
        losses = losses,
        draws = draws,
        winStreak = winStreak,
        bestStreak = bestStreak,
        gamesVsAi = gamesVsAi,
        winsVsAi = winsVsAi,
        totalMoves = totalMoves,
        averageMovesPerGame = if (totalGames > 0) totalMoves.toFloat() / totalGames else 0f
    )
}

package com.tictactoe.pro.data.local.dao

import androidx.room.*
import com.tictactoe.pro.data.local.entity.AchievementEntity
import com.tictactoe.pro.data.local.entity.GameEntity
import com.tictactoe.pro.data.local.entity.StatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long

    @Query("SELECT * FROM games ORDER BY timestamp DESC LIMIT 50")
    fun getRecentGames(): Flow<List<GameEntity>>

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getTotalGames(): Int

    @Query("DELETE FROM games WHERE timestamp < :cutoff")
    suspend fun deleteOldGames(cutoff: Long)
}

@Dao
interface StatsDao {
    @Query("SELECT * FROM stats WHERE id = 1")
    fun getStats(): Flow<StatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStats(stats: StatsEntity)

    @Query("SELECT * FROM stats WHERE id = 1")
    suspend fun getStatsOnce(): StatsEntity?
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAchievement(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements WHERE achievementId = :id")
    suspend fun getAchievementById(id: String): AchievementEntity?

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedCount(): Flow<Int>
}

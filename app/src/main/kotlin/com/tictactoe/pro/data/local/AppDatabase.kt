package com.tictactoe.pro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tictactoe.pro.data.local.dao.AchievementDao
import com.tictactoe.pro.data.local.dao.GameDao
import com.tictactoe.pro.data.local.dao.StatsDao
import com.tictactoe.pro.data.local.entity.AchievementEntity
import com.tictactoe.pro.data.local.entity.GameEntity
import com.tictactoe.pro.data.local.entity.StatsEntity

@Database(
    entities = [GameEntity::class, StatsEntity::class, AchievementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun statsDao(): StatsDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        const val DATABASE_NAME = "tictactoe_pro.db"
    }
}

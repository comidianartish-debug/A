package com.tictactoe.pro.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.tictactoe.pro.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val THEME_STYLE = stringPreferencesKey("theme_style")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val SHOW_HINTS = booleanPreferencesKey("show_hints")
        val SHOW_MOVE_NUMBERS = booleanPreferencesKey("show_move_numbers")
        val ANIMATION_SPEED = stringPreferencesKey("animation_speed")
        val PREFERRED_BOARD_SIZE = stringPreferencesKey("preferred_board_size")
        val PREFERRED_DIFFICULTY = stringPreferencesKey("preferred_difficulty")
        val PREFERRED_GAME_MODE = stringPreferencesKey("preferred_game_mode")
        val PLAYER_X_NAME = stringPreferencesKey("player_x_name")
        val PLAYER_O_NAME = stringPreferencesKey("player_o_name")
        val TOTAL_POINTS = intPreferencesKey("total_points")
    }

    val settings: Flow<AppSettings> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            AppSettings(
                themeStyle = ThemeStyle.valueOf(prefs[Keys.THEME_STYLE] ?: ThemeStyle.MATERIAL_YOU.name),
                dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: true,
                soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
                hapticEnabled = prefs[Keys.HAPTIC_ENABLED] ?: true,
                showHints = prefs[Keys.SHOW_HINTS] ?: false,
                showMoveNumbers = prefs[Keys.SHOW_MOVE_NUMBERS] ?: false,
                animationSpeed = AnimationSpeed.valueOf(prefs[Keys.ANIMATION_SPEED] ?: AnimationSpeed.NORMAL.name),
                preferredBoardSize = BoardSize.valueOf(prefs[Keys.PREFERRED_BOARD_SIZE] ?: BoardSize.THREE.name),
                preferredDifficulty = Difficulty.valueOf(prefs[Keys.PREFERRED_DIFFICULTY] ?: Difficulty.MEDIUM.name),
                preferredGameMode = GameMode.valueOf(prefs[Keys.PREFERRED_GAME_MODE] ?: GameMode.VS_AI.name),
                playerXName = prefs[Keys.PLAYER_X_NAME] ?: "Player X",
                playerOName = prefs[Keys.PLAYER_O_NAME] ?: "Player O",
                totalPoints = prefs[Keys.TOTAL_POINTS] ?: 0
            )
        }

    suspend fun updateSettings(update: suspend (MutablePreferences) -> Unit) {
        dataStore.edit { prefs -> update(prefs) }
    }

    suspend fun setThemeStyle(style: ThemeStyle) = updateSettings { it[Keys.THEME_STYLE] = style.name }
    suspend fun setDynamicColor(enabled: Boolean) = updateSettings { it[Keys.DYNAMIC_COLOR] = enabled }
    suspend fun setSoundEnabled(enabled: Boolean) = updateSettings { it[Keys.SOUND_ENABLED] = enabled }
    suspend fun setHapticEnabled(enabled: Boolean) = updateSettings { it[Keys.HAPTIC_ENABLED] = enabled }
    suspend fun setShowHints(show: Boolean) = updateSettings { it[Keys.SHOW_HINTS] = show }
    suspend fun setAnimationSpeed(speed: AnimationSpeed) = updateSettings { it[Keys.ANIMATION_SPEED] = speed.name }
    suspend fun setPreferredBoardSize(size: BoardSize) = updateSettings { it[Keys.PREFERRED_BOARD_SIZE] = size.name }
    suspend fun setPreferredDifficulty(diff: Difficulty) = updateSettings { it[Keys.PREFERRED_DIFFICULTY] = diff.name }
    suspend fun setPreferredGameMode(mode: GameMode) = updateSettings { it[Keys.PREFERRED_GAME_MODE] = mode.name }
    suspend fun setPlayerXName(name: String) = updateSettings { it[Keys.PLAYER_X_NAME] = name }
    suspend fun setPlayerOName(name: String) = updateSettings { it[Keys.PLAYER_O_NAME] = name }
    suspend fun addPoints(points: Int) = updateSettings {
        it[Keys.TOTAL_POINTS] = (it[Keys.TOTAL_POINTS] ?: 0) + points
    }
}

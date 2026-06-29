package com.tictactoe.pro.domain.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: String,
    val points: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Int = 0,
    val target: Int = 1,
    val category: AchievementCategory = AchievementCategory.GENERAL
) {
    val progressFraction: Float get() = (progress.toFloat() / target).coerceIn(0f, 1f)
}

enum class AchievementCategory(val displayName: String) {
    GENERAL("General"),
    WINS("Wins"),
    STREAKS("Streaks"),
    AI("vs AI"),
    MODES("Game Modes"),
    SPEED("Speed"),
    PERFECT("Perfect Play"),
    SOCIAL("Social"),
    COLLECTION("Collection"),
    MILESTONE("Milestone")
}

object Achievements {
    val all = listOf(
        Achievement("first_win", "First Victory", "Win your first game", "trophy", 10, category = AchievementCategory.WINS, target = 1),
        Achievement("win_10", "Getting Started", "Win 10 games", "trophy", 25, category = AchievementCategory.WINS, target = 10),
        Achievement("win_50", "Rising Star", "Win 50 games", "trophy", 50, category = AchievementCategory.WINS, target = 50),
        Achievement("win_100", "Century", "Win 100 games", "trophy", 100, category = AchievementCategory.WINS, target = 100),
        Achievement("win_500", "Champion", "Win 500 games", "trophy", 250, category = AchievementCategory.WINS, target = 500),
        Achievement("win_1000", "Legend", "Win 1000 games", "trophy", 500, category = AchievementCategory.WINS, target = 1000),
        Achievement("streak_3", "Hot Streak", "Win 3 games in a row", "fire", 20, category = AchievementCategory.STREAKS, target = 3),
        Achievement("streak_5", "On Fire", "Win 5 games in a row", "fire", 50, category = AchievementCategory.STREAKS, target = 5),
        Achievement("streak_10", "Unstoppable", "Win 10 games in a row", "fire", 150, category = AchievementCategory.STREAKS, target = 10),
        Achievement("streak_25", "Dominator", "Win 25 games in a row", "fire", 500, category = AchievementCategory.STREAKS, target = 25),
        Achievement("beat_easy_ai", "Baby Steps", "Beat Easy AI", "robot", 10, category = AchievementCategory.AI, target = 1),
        Achievement("beat_medium_ai", "Getting Serious", "Beat Medium AI", "robot", 25, category = AchievementCategory.AI, target = 1),
        Achievement("beat_hard_ai", "Hard Boiled", "Beat Hard AI", "robot", 50, category = AchievementCategory.AI, target = 1),
        Achievement("beat_expert_ai", "Expert Slayer", "Beat Expert AI", "robot", 100, category = AchievementCategory.AI, target = 1),
        Achievement("beat_master_ai", "Master Beater", "Beat Master AI", "robot", 200, category = AchievementCategory.AI, target = 1),
        Achievement("beat_legend_ai", "Legend Killer", "Beat Legend AI", "robot", 400, category = AchievementCategory.AI, target = 1),
        Achievement("beat_mythic_ai", "Mythic Slayer", "Defeat the Mythic AI", "robot", 1000, category = AchievementCategory.AI, target = 1),
        Achievement("play_ultimate", "Ultimate Challenger", "Play Ultimate Tic-Tac-Toe", "grid", 30, category = AchievementCategory.MODES, target = 1),
        Achievement("win_ultimate", "Ultimate Winner", "Win Ultimate Tic-Tac-Toe", "grid", 100, category = AchievementCategory.MODES, target = 1),
        Achievement("play_gravity", "Gravity Defier", "Play Gravity mode", "arrow_down", 20, category = AchievementCategory.MODES, target = 1),
        Achievement("win_gravity", "Gravity Master", "Win 5 Gravity mode games", "arrow_down", 75, category = AchievementCategory.MODES, target = 5),
        Achievement("play_blitz", "Speed Demon", "Play Blitz mode", "flash", 20, category = AchievementCategory.MODES, target = 1),
        Achievement("win_blitz", "Blitz King", "Win 10 Blitz games", "flash", 100, category = AchievementCategory.MODES, target = 10),
        Achievement("solve_puzzle", "Puzzle Solver", "Solve your first puzzle", "puzzle", 15, category = AchievementCategory.MODES, target = 1),
        Achievement("solve_10_puzzles", "Puzzle Master", "Solve 10 puzzles", "puzzle", 75, category = AchievementCategory.MODES, target = 10),
        Achievement("solve_all_puzzles", "Puzzle Legend", "Solve all puzzles", "puzzle", 500, category = AchievementCategory.MODES, target = 20),
        Achievement("play_8x8", "Grandmaster Board", "Play on an 8×8 board", "grid_large", 25, category = AchievementCategory.GENERAL, target = 1),
        Achievement("win_8x8", "Epic Battle", "Win on an 8×8 board", "grid_large", 75, category = AchievementCategory.GENERAL, target = 1),
        Achievement("perfect_game", "Perfect Game", "Win without the opponent ever threatening a win", "star", 200, category = AchievementCategory.PERFECT, target = 1),
        Achievement("games_100", "Dedicated", "Play 100 total games", "controller", 50, category = AchievementCategory.MILESTONE, target = 100),
        Achievement("games_500", "Hardcore", "Play 500 total games", "controller", 150, category = AchievementCategory.MILESTONE, target = 500),
        Achievement("games_1000", "Veteran", "Play 1000 total games", "controller", 400, category = AchievementCategory.MILESTONE, target = 1000),
        Achievement("first_draw", "Stalemate", "Draw a game", "handshake", 10, category = AchievementCategory.GENERAL, target = 1),
        Achievement("draw_10", "Diplomat", "Draw 10 games", "handshake", 40, category = AchievementCategory.GENERAL, target = 10),
        Achievement("all_themes", "Style Icon", "Unlock all themes", "palette", 100, category = AchievementCategory.COLLECTION, target = 10),
        Achievement("early_bird", "Early Bird", "Play before 7am", "sun", 15, category = AchievementCategory.GENERAL, target = 1),
        Achievement("night_owl", "Night Owl", "Play after midnight", "moon", 15, category = AchievementCategory.GENERAL, target = 1),
        Achievement("quick_win", "Lightning Fast", "Win in 3 moves (3×3)", "flash", 100, category = AchievementCategory.SPEED, target = 1),
        Achievement("marathon", "Marathon", "Play 20 games in one session", "run", 75, category = AchievementCategory.MILESTONE, target = 20),
        Achievement("comeback", "Comeback Kid", "Win after being on the edge of losing", "comeback", 150, category = AchievementCategory.GENERAL, target = 1)
    )
}

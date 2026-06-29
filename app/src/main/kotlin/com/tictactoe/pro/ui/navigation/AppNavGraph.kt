package com.tictactoe.pro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tictactoe.pro.domain.model.BoardSize
import com.tictactoe.pro.domain.model.Difficulty
import com.tictactoe.pro.domain.model.GameMode
import com.tictactoe.pro.ui.screens.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Game : Screen("game/{mode}/{boardSize}/{difficulty}") {
        fun createRoute(mode: GameMode, boardSize: BoardSize, difficulty: Difficulty) =
            "game/${mode.name}/${boardSize.name}/${difficulty.name}"
    }
    object Stats : Screen("stats")
    object Achievements : Screen("achievements")
    object Settings : Screen("settings")
    object Puzzles : Screen("puzzles")
    object ModeSelect : Screen("mode_select")
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToModeSelect = { navController.navigate(Screen.ModeSelect.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.ModeSelect.route) {
            ModeSelectScreen(
                onNavigateToGame = { mode, boardSize, difficulty ->
                    navController.navigate(Screen.Game.createRoute(mode, boardSize, difficulty))
                },
                onNavigateToPuzzles = { navController.navigate(Screen.Puzzles.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("boardSize") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) { backStack ->
            val mode = GameMode.valueOf(backStack.arguments?.getString("mode") ?: GameMode.VS_AI.name)
            val boardSize = BoardSize.valueOf(backStack.arguments?.getString("boardSize") ?: BoardSize.THREE.name)
            val difficulty = Difficulty.valueOf(backStack.arguments?.getString("difficulty") ?: Difficulty.MEDIUM.name)
            GameScreen(
                gameMode = mode,
                boardSize = boardSize,
                difficulty = difficulty,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToModeSelect = {
                    navController.popBackStack(Screen.Home.route, false)
                    navController.navigate(Screen.ModeSelect.route)
                }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Achievements.route) {
            AchievementsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Puzzles.route) {
            PuzzleScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

# 🏆 Tic-Tac-Toe Pro

A premium, feature-complete Tic-Tac-Toe Android app written entirely in **Kotlin**, built with Jetpack Compose and Clean Architecture.

---

## 🚀 GitHub Actions CI — Debug APK

Every push to `main` automatically builds a **debug APK** and uploads it to a rolling GitHub Release tagged `latest-debug`.

Every push of a version tag (`v1.0.0`, `v1.2.3`, etc.) creates a **stable GitHub Release** with the debug APK attached.

### Trigger a stable release

```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow will:
1. Build the debug APK with Gradle
2. Name the APK `TicTacToePro-debug-v<version>-build<N>-<sha>.apk`
3. Upload it as a workflow artifact (30-day retention)
4. Create/update a GitHub Release with the APK attached

---

## 📱 Features

### Game Modes
| Mode | Description |
|------|-------------|
| vs AI | Challenge the computer with 10 difficulty levels |
| 2 Players | Pass-and-play locally |
| Ultimate | 9 boards in 1 — win 3 boards to win the match |
| Gravity | Pieces fall to the bottom (like Connect 4) |
| Blitz | 5-second turn timer |
| Puzzle | Solve pre-set tactical challenges |

### Board Sizes
- **3×3** Standard · **4×4** Classic Plus · **5×5** Expert · **6×6** Master · **8×8** Grandmaster

### AI Engine (10 Levels)
- Random → Beginner → Easy → Medium → Hard → Expert → Master → Grandmaster → Legend → Mythic
- Minimax with alpha-beta pruning + transposition table caching
- Adaptive depth based on board size for sub-500ms response times

### Visual Themes
Material You · Glassmorphism · Neon Glow · Retro Arcade · Minimalist · AMOLED · Galaxy · Nature · Gold Premium · Cyberpunk

### Achievements
40+ achievements across 10 categories: Wins, Streaks, vs AI, Modes, Speed, Perfect Play, Milestones

---

## 🏗️ Architecture

```
app/src/main/kotlin/com/tictactoe/pro/
├── domain/
│   ├── model/          # GameState, Player, Achievement, AppSettings
│   └── usecase/        # GameEngine, AiEngine, PuzzleRepository
├── data/
│   ├── local/          # Room DB (GameDao, StatsDao, AchievementDao)
│   └── repository/     # GameRepository, SettingsRepository
├── di/                 # Hilt AppModule
└── ui/
    ├── navigation/     # Compose NavHost
    ├── screens/        # Home, ModeSelect, Game, Stats, Achievements, Settings, Puzzle
    ├── components/     # GameBoard, GameCell, PlayerIndicator, ScoreRow
    ├── theme/          # Color, Type, Theme (10 themes)
    └── viewmodel/      # GameViewModel, StatsViewModel, SettingsViewModel, PuzzleViewModel
```

**Stack:** Kotlin · Jetpack Compose · Hilt · Room · DataStore · Navigation Compose · Material 3

---

## 🔧 Local Development

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK API 24+

### Setup
```bash
git clone https://github.com/<you>/TicTacToePro
cd TicTacToePro

# Generate the Gradle wrapper JAR (one-time)
gradle wrapper --gradle-version 8.9

# Build debug APK
./gradlew assembleDebug

# APK location
ls app/build/outputs/apk/debug/
```

> **Note:** The `gradle-wrapper.jar` is a binary file. Run `gradle wrapper` once after cloning to generate it, or let GitHub Actions download it automatically.

---

## 📦 APK Details

| Property | Value |
|----------|-------|
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 35 (Android 15) |
| Language | Kotlin 100% |
| UI | Jetpack Compose |
| Architecture | Clean Architecture (MVVM) |
| Build type | Debug (not obfuscated) |

---

## ⚙️ GitHub Actions Workflow

File: `.github/workflows/build.yml`

| Trigger | Action |
|---------|--------|
| Push to `main` | Build APK → upload artifact → update `latest-debug` pre-release |
| Push `v*` tag | Build APK → upload artifact → create stable versioned release |
| Pull request | Build APK → upload artifact only (no release) |
| Manual dispatch | Build APK → upload artifact → rolling release |

No secrets required — only the built-in `GITHUB_TOKEN` is used.

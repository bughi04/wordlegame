# WordleGame

> **Note**: This project was designed and implemented by me, with selective use of AI tools for research, refactoring, and productivity - similar to modern IDE assistance.

An Android word-guessing game inspired by Wordle, featuring multiple game modes and a polished user interface.

## Features

### Game Modes
- **Normal Wordle** - Classic word-guessing gameplay with easy and hard difficulty levels
- **Timed Mode** - Race against the clock with a countdown timer for each guess
- **Evil Wordle** - Dynamic difficulty that changes the target word after each guess to maximize challenge
- **Emoji Game** - Guess words based on emoji clues

### Additional Features
- **Game History** - Track all your past games with detailed statistics
- **Multiple Themes** - Light, Dark, Colorful, and High Contrast themes
- **Settings** - Customize sound effects and background music
- **Animated Feedback** - Smooth tile-flip animations and keyboard color updates
- **Word Validation** - Integration with Dictionary API for expanded word lists

## Technical Highlights

### Architecture
- **Fragment-based UI** - Modular game screens using Android Fragments
- **SQLite Database** - Persistent storage for game history using Room-like patterns
- **Repository Pattern** - Clean separation of data access logic
- **Custom RecyclerView Adapters** - Efficient rendering of game board and keyboard

### Technologies Used
- **Java** - Primary programming language
- **Android SDK** - Target API 32
- **Retrofit** - REST API communication for dictionary validation
- **Gson** - JSON serialization/deserialization
- **Material Design** - Modern UI components
- **SharedPreferences** - App settings and theme management

### Key Components
- **DictionaryManager** - Caching system for word validation with API fallback
- **EvilWordleAlgorithm** - Pattern-matching algorithm for dynamic word selection
- **GameTimer** - Countdown timer implementation for timed mode
- **WordleRowAdapter** - Custom adapter with flip animations
- **Theme System** - Dynamic theme switching with multiple color schemes

## Project Structure

```
app/src/main/java/com/example/wordlegame/
├── Activities (Main, Game, Settings, History)
├── Fragments (Normal, Timed, Evil, Emoji game modes)
├── Adapters (Board, Keyboard, History)
├── Data (Repository, Database, Models)
├── Utils (WordGenerator, DictionaryManager, PreferenceManager)
└── Resources (Layouts, Themes, Word lists)
```

## Screenshots

The app features:
- Clean, intuitive main menu
- Interactive game board with real-time feedback
- On-screen keyboard with color-coded letter states
- Game result dialogs with statistics
- Comprehensive game history viewer

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 21 or higher
- Java 8 or higher

### Installation
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run on an emulator or physical device

## Game Logic

The app implements standard Wordle rules:
- **Green** - Correct letter in correct position
- **Yellow** - Correct letter in wrong position  
- **Gray** - Letter not in the word
- **6 attempts** to guess the 5-letter word

## Future Enhancements

Potential improvements could include:
- Multiplayer mode
- Daily challenge integration
- Statistics dashboard with graphs
- More emoji word mappings
- Achievements system

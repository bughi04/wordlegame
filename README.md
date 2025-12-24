# WordleGame
![Java](https://img.shields.io/badge/Java-8-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Android](https://img.shields.io/badge/Android-12L-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-21-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Target SDK](https://img.shields.io/badge/Target%20SDK-32-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-7.3+-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit-2.9.0-48B983?style=for-the-badge&logo=square&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-1.5.0-757575?style=for-the-badge&logo=material-design&logoColor=white)

An Android word-guessing game inspired by Wordle, featuring multiple game modes and a polished user interface.

> **Note**: This project was designed and implemented by me, with selective use of AI tools for research, refactoring, and productivity - similar to modern IDE assistance.
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

## Installation & Setup

### Prerequisites
- **Java Development Kit (JDK)**: Version 8 or higher (Java 11 recommended)
- **Android Studio**: Arctic Fox or newer
- **Android SDK**: 
  - Minimum API Level 21 (Android 5.0 Lollipop)
  - Target/Compile API Level 32 (Android 12L)
- **Gradle**: Version 7.3.3+ (bundled with Android Studio)

### Quick Start
```bash
# Clone the repository
git clone https://github.com/yourusername/wordlegame.git

# Navigate to project directory
cd wordlegame

# Open in Android Studio
# File → Open → Select the 'wordlegame' folder

# Sync Gradle files
# Android Studio will prompt you to sync - click "Sync Now"
# Or manually: File → Sync Project with Gradle Files

# Build the project
./gradlew build

# Run on emulator or connected device
# Click the "Run" button (▶️) in Android Studio
# Or use: ./gradlew installDebug
```

### First Launch Notes
- The application will automatically create local database and SharedPreferences storage on first launch
- Word lists are embedded in the app resources (no download needed)
- Internet permission is required for Dictionary API word validation
- **Permissions**: The app requires INTERNET (for word validation via Dictionary API)

### Device Requirements
- **Minimum**: Android 5.0 (API 21) or higher
- **Tested on**: Android 12L (API 32)
- **Orientation**: Portrait mode recommended
- **Storage**: ~10-15 MB

### Troubleshooting

**Gradle Sync Issues:**
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

**SDK Path Issues:**
- Create a `local.properties` file in the project root
- Add: `sdk.dir=/path/to/your/Android/Sdk`
- Example (Windows): `sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk`
- Example (Mac/Linux): `sdk.dir=/Users/YourName/Library/Android/sdk`

**Build Errors:**
- Ensure JDK 8+ is set in Android Studio (File → Project Structure → SDK Location)
- Install Android SDK API Level 32 if missing (Tools → SDK Manager)
- Update Android SDK tools if prompted
- Invalidate caches: File → Invalidate Caches / Restart

**API Connection Issues:**
- The app uses Dictionary API for word validation
- Ensure device has internet connectivity
- API calls are cached locally for 7 days
- Offline mode uses pre-loaded word lists

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

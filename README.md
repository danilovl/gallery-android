# Gallery App

Modern Android gallery application for viewing local photos, built with Jetpack Compose and Material 3.

## Features

| Feature | Description |
| :--- | :--- |
| **On This Day** | Memories from today in previous years. |
| **Day View** | Group photos by day of the week. |
| **Month View** | Group photos by month of the year. |
| **Timeline** | Continuous scrollable list of all photos. |
| **Localization** | Multi-language support — English, Russian, German, French, Spanish, Italian, Portuguese, Polish, Dutch, Czech, and Chinese. |
| **Portrait Only** | The application is locked to portrait orientation for a consistent experience. |

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose + Material 3
- **Image Loading**: Coil
- **Architecture**: MVVM (ViewModel + StateFlow)

## Prerequisites

- **JDK 17+**: Required for modern Gradle and Android build tools.
- **Android Studio**: Ladybug or later recommended.
- **Android SDK**:
    - Min SDK: 26
    - Target SDK: 36

## Build and Run

1. Clone the repository: `git clone <repository-url>`
2. Open the project in Android Studio.
3. Sync Gradle and build the project.
4. Run on an emulator or a physical device (API 26+).

### Command Line Build

To build the debug APK:
```bash
# On Unix-like systems (Linux, macOS)
./gradlew assembleDebug

# On Windows
.\gradlew.bat assembleDebug
```
The output APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

To build the release APK:
```bash
# On Unix-like systems (Linux, macOS)
./gradlew assembleRelease

# On Windows
.\gradlew.bat assembleRelease
```
The output APK will be located at `app/build/outputs/apk/release/app-release.apk`.

To run unit tests:
```bash
./gradlew test
```

To run static analysis (Lint):
```bash
./gradlew lint
```

## Localization

The app supports multiple languages. Strings are located in `app/src/main/res/values-*/strings.xml`.

## MIT License

Gallery application is completely free and released under the [MIT License](./LICENSE).

## Author

Created by [Vladimir Danilov](https://github.com/danilovl).

# ImageMagick Android App

A Kotlin Android application that provides image processing capabilities using ImageMagick.

## Features

- **Image Selection**: Pick images from gallery
- **Image Processing**: Resize, blur, grayscale, sepia, negate, flip, rotate
- **Thumbnail Generation**: Create thumbnails for quick previews
- **Format Conversion**: Convert between different image formats

## Requirements

- Android API 24+ (Android 7.0 Nougat)
- ARM64-v8a architecture

## Setup

### 1. Download ImageMagick Libraries

This project uses ImageMagick native libraries. You need to download them first:

```bash
# Run from project root
curl -L -o imagemagick.zip https://github.com/MolotovCherry/Android-ImageMagick7/releases/download/7.1.2-13/Android-ImageMagick7-7.1.2-13.zip
unzip -j imagemagick.zip "lib/arm64-v8a/*" -d library-imagemagick/src/main/jniLibs/arm64-v8a/
rm imagemagick.zip
```

### 2. Build the Project

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

## Project Structure

```
kotlin-android/
├── app/                          # Main Android application
│   └── src/main/
│       ├── java/                 # Application source code
│       └── res/                  # Android resources
├── library-imagemagick/          # ImageMagick Kotlin wrapper library
│   └── src/main/
│       ├── java/                 # Library source code
│       └── jniLibs/              # Native libraries (needs download)
├── library-android/              # Android utilities library
├── library-compose/              # Jetpack Compose example
├── library-kotlin/               # Pure Kotlin library
├── gradle/                       # Gradle configuration
└── build.gradle.kts              # Root build configuration
```

## Libraries

- **ImageMagick**: Powerful image processing library
- **Jetpack Compose**: Modern Android UI toolkit
- **Kotlin Coroutines**: Asynchronous programming

## Development

This project follows the kotlin-android-template structure with:
- Gradle Kotlin DSL
- Detekt for code analysis
- GitHub Actions for CI/CD

## License

MIT License - See LICENSE file for details

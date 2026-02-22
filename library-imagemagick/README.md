# ImageMagick Android Library

This library provides Kotlin wrappers to ImageMagick for Android using the command-line binary approach.

## Prebuilt Binary

This library requires the ImageMagick command-line binary (`.so` file). Due to licensing and size considerations, this file is not included in this repository.

### Download Prebuilt Binary

1. Go to [Android-ImageMagick7 Releases](https://github.com/MolotovCherry/Android-ImageMagick7/releases)
2. Download the latest release asset (e.g., `Android-ImageMagick7-7.1.2-13.zip`)
3. Extract the archive
4. Copy the following files from the extracted `lib/arm64-v8a/` directory to `library-imagemagick/src/main/jniLibs/arm64-v8a/`:

Required files:
- `libmagick.so` (the command-line binary)
- All dependency libraries:
  - `libmagickcore-7.so`
  - `libmagickwand-7.so`
  - `libbz2-1.so`
  - `libfreetype-2.so`
  - `libjpeg.so`
  - `libopenjp2.so`
  - `libpng16.so`
  - `libtiff.so`
  - `libwebp.so`
  - `libxml2.so`
  - `liblzma.so`
  - `liblcms2.so`
  - `libfftw3.so`
  - `libiconv.so`
  - `libicuuc.so`
  - `libicui18n.so`
  - `libstdc++.so`
  - `libc++_shared.so`

### Quick Setup Script

```bash
cd /path/to/kotlin-android
mkdir -p library-imagemagick/src/main/jniLibs/arm64-v8a

# Download and extract (adjust version as needed)
curl -L -o imagemagick.zip https://github.com/MolotovCherry/Android-ImageMagick7/releases/download/7.1.2-13/Android-ImageMagick7-7.1.2-13.zip
unzip -j imagemagick.zip "lib/arm64-v8a/*" -d library-imagemagick/src/main/jniLibs/arm64-v8a/
rm imagemagick.zip
```

## Usage

```kotlin
// Initialize the library (call once in Application class or MainActivity)
val imageMagick = ImageMagick.initialize(context)

// Check if library is loaded
if (imageMagick.isLoaded()) {
    // Get version
    val version = imageMagick.getVersion()
    
    // Resize image
    imageMagick.resizeImage(
        inputPath = "/path/to/input.jpg",
        outputPath = "/path/to/output.jpg",
        width = 800,
        height = 600
    )
    
    // Apply blur
    imageMagick.blurImage(
        inputPath = "/path/to/input.jpg",
        outputPath = "/path/to/output.jpg",
        radius = 5.0,
        sigma = 2.0
    )
    
    // Convert format
    imageMagick.convertFormat(
        inputPath = "/path/to/input.png",
        outputPath = "/path/to/output.jpg",
        format = "JPEG"
    )
}
```

## Supported Operations

| Operation | Description |
|-----------|-------------|
| resize | Resize images to specific dimensions |
| convert | Convert between image formats |
| blur | Apply Gaussian blur |
| sharpen | Sharpen images |
| grayscale | Convert to grayscale |
| negate | Invert colors |
| brightness | Adjust brightness (-100 to 100) |
| contrast | Adjust contrast |
| rotate | Rotate image by degrees |
| flip | Flip vertically |
| flop | Flip horizontally |
| crop | Crop to specific region |
| thumbnail | Create thumbnail |
| sepia | Apply sepia tone |
| oilPaint | Apply oil paint effect |

## Requirements

- Android API 24+ (Android 7.0 Nougat)
- ARM64-v8a architecture
- ImageMagick binary and dependencies in jniLibs

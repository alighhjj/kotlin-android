package com.blogtown.imagemagick

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageMagick private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: ImageMagick? = null
        
        private const val BRIGHTNESS_BASE = 100
        private const val DEFAULT_DEPTH = 8
        private const val MIN_IMAGE_INFO_PARTS = 5

        fun getInstance(): ImageMagick {
            return checkNotNull(instance) {
                "ImageMagick not initialized. Call initialize(context) first."
            }
        }

        fun initialize(context: Context): ImageMagick {
            return instance ?: synchronized(this) {
                instance ?: ImageMagick(context.applicationContext).also { instance = it }
            }
        }
    }

    fun isLoaded(): Boolean {
        return try {
            getVersion()
            true
        } catch (@Suppress("SwallowedException") e: IOException) {
            false
        }
    }

    fun getVersion(): String {
        return try {
            executeCommandWithOutput("--version").substringBefore("\n")
        } catch (@Suppress("SwallowedException") e: IOException) {
            "Not loaded"
        }
    }

    fun resizeImage(
        inputPath: String,
        outputPath: String,
        width: Int,
        height: Int,
        quality: Int = 85
    ): Boolean {
        return executeCommand(
            "convert",
            "-resize", "${width}x${height}",
            "-quality", quality.toString(),
            inputPath, outputPath
        ) == 0
    }

    fun convertFormat(
        inputPath: String,
        outputPath: String,
        format: String
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-format", format,
            outputPath
        ) == 0
    }

    fun blurImage(
        inputPath: String,
        outputPath: String,
        radius: Double,
        sigma: Double
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-blur", "${radius}x${sigma}",
            outputPath
        ) == 0
    }

    fun sharpenImage(
        inputPath: String,
        outputPath: String,
        radius: Double,
        sigma: Double
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-sharpen", "${radius}x${sigma}",
            outputPath
        ) == 0
    }

    fun grayscaleImage(
        inputPath: String,
        outputPath: String
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-type", "Grayscale",
            outputPath
        ) == 0
    }

    fun negateImage(
        inputPath: String,
        outputPath: String
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-negate",
            outputPath
        ) == 0
    }

    fun adjustBrightness(
        inputPath: String,
        outputPath: String,
        brightness: Double
    ): Boolean {
        val normalized = BRIGHTNESS_BASE + brightness
        return executeCommand(
            "convert",
            inputPath,
            "-modulate", "${normalized},$BRIGHTNESS_BASE,$BRIGHTNESS_BASE",
            outputPath
        ) == 0
    }

    fun adjustContrast(
        inputPath: String,
        outputPath: String,
        contrast: Double
    ): Boolean {
        val direction = if (contrast > 0) "contrast+" else "contrast-"
        return executeCommand(
            "convert",
            inputPath,
            "-$direction",
            outputPath
        ) == 0
    }

    fun rotateImage(
        inputPath: String,
        outputPath: String,
        degrees: Double
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-rotate", degrees.toString(),
            outputPath
        ) == 0
    }

    fun flipImage(
        inputPath: String,
        outputPath: String
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-flip",
            outputPath
        ) == 0
    }

    fun flopImage(
        inputPath: String,
        outputPath: String
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-flop",
            outputPath
        ) == 0
    }

    fun cropImage(
        inputPath: String,
        outputPath: String,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-crop", "${width}x${height}+${x}+${y}",
            "+repage",
            outputPath
        ) == 0
    }

    fun createThumbnail(
        inputPath: String,
        outputPath: String,
        maxSize: Int = 256
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-thumbnail", "${maxSize}x${maxSize}",
            "-quality", "80",
            outputPath
        ) == 0
    }

    fun applySepia(
        inputPath: String,
        outputPath: String
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-type", "SepiaTone",
            "-threshold", "80%",
            outputPath
        ) == 0
    }

    fun applyOilPaint(
        inputPath: String,
        outputPath: String,
        radius: Int = 5
    ): Boolean {
        return executeCommand(
            "convert",
            inputPath,
            "-paint", radius.toString(),
            outputPath
        ) == 0
    }

    fun getImageInfo(path: String): ImageInfo? {
        return try {
            val output = executeCommandWithOutput(
                "identify",
                "-format", "%w,%h,%m,%d,%s",
                path
            )
            if (output.isBlank()) return null
            val parts = output.split(",")
            if (parts.size >= MIN_IMAGE_INFO_PARTS) {
                ImageInfo(
                    width = parts[0].toIntOrNull() ?: 0,
                    height = parts[1].toIntOrNull() ?: 0,
                    format = parts[2],
                    depth = parts[3].toIntOrNull() ?: DEFAULT_DEPTH,
                    colorspace = parts[4]
                )
            } else null
        } catch (@Suppress("SwallowedException") e: IllegalArgumentException) {
            null
        }
    }

    private fun executeCommand(vararg args: String): Int {
        return try {
            val process = ProcessBuilder(args.toList())
                .redirectErrorStream(true)
                .start()
            
            val exitCode = process.waitFor()
            exitCode
        } catch (@Suppress("SwallowedException") e: IOException) {
            -1
        } catch (@Suppress("SwallowedException") e: InterruptedException) {
            -1
        }
    }

    private fun executeCommandWithOutput(vararg args: String): String {
        return try {
            val process = ProcessBuilder(args.toList())
                .redirectErrorStream(true)
                .start()
            
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            output.trim()
        } catch (@Suppress("SwallowedException") e: IOException) {
            ""
        } catch (@Suppress("SwallowedException") e: InterruptedException) {
            ""
        }
    }
}

data class ImageInfo(
    val width: Int,
    val height: Int,
    val format: String,
    val depth: Int,
    val colorspace: String
)

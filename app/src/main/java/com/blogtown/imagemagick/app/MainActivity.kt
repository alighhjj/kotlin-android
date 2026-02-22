package com.blogtown.imagemagick.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.blogtown.imagemagick.ImageMagick
import com.blogtown.imagemagick.app.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageMagick: ImageMagick
    
    private var selectedImageUri: Uri? = null
    private var currentProcessedPath: String? = null
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val DEFAULT_RESIZE_WIDTH = 1024
        private const val DEFAULT_RESIZE_HEIGHT = 1024
        private const val DEFAULT_QUALITY = 90
        private const val BLUR_RADIUS = 5.0
        private const val BLUR_SIGMA = 2.0
        private const val ROTATE_DEGREES = 90.0
        private const val SMALL_RESIZE_DIMENSION = 512
        private const val SMALL_RESIZE_QUALITY = 85
    }
    
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.imagePreview.setImageURI(it)
            binding.imagePreview.visibility = View.VISIBLE
            binding.processButton.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        imageMagick = ImageMagick.initialize(this)
        
        setupUI()
        checkPermissions()
        
        Toast.makeText(this, "ImageMagick v${imageMagick.getVersion()}", Toast.LENGTH_SHORT).show()
    }

    private fun setupUI() {
        binding.selectImageButton.setOnClickListener {
            pickImage.launch("image/*")
        }
        
        binding.processButton.setOnClickListener {
            processSelectedImage()
        }
        
        binding.blurButton.setOnClickListener {
            processWithOperation("blur")
        }
        
        binding.grayscaleButton.setOnClickListener {
            processWithOperation("grayscale")
        }
        
        binding.sepiaButton.setOnClickListener {
            processWithOperation("sepia")
        }
        
        binding.negateButton.setOnClickListener {
            processWithOperation("negate")
        }
        
        binding.flipButton.setOnClickListener {
            processWithOperation("flip")
        }
        
        binding.rotateButton.setOnClickListener {
            processWithOperation("rotate")
        }
        
        binding.resizeButton.setOnClickListener {
            processWithOperation("resize")
        }
        
        binding.saveButton.setOnClickListener {
            saveProcessedImage()
        }
    }
    
    private fun checkPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (notGranted.isNotEmpty()) {
            requestPermissions(notGranted.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }
    
    private fun processSelectedImage() {
        val uri = selectedImageUri ?: return
        
        showLoading(true)
        
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val inputPath = getPathFromUri(uri)
                    if (inputPath == null) {
                        return@withContext Result.failure(FileNotFoundException("Cannot access image"))
                    }
                    
                    val outputFile = File(cacheDir, "processed_${System.currentTimeMillis()}.jpg")
                    currentProcessedPath = outputFile.absolutePath
                    
                    val success = imageMagick.resizeImage(
                        inputPath = inputPath,
                        outputPath = outputFile.absolutePath,
                        width = DEFAULT_RESIZE_WIDTH,
                        height = DEFAULT_RESIZE_HEIGHT,
                        quality = DEFAULT_QUALITY
                    )
                    
                    if (success) {
                        Result.success(outputFile)
                    } else {
                        Result.failure(IOException("Processing failed"))
                    }
                } catch (e: IOException) {
                    Result.failure(e)
                }
            }
            
            showLoading(false)
            
            result.onSuccess { file ->
                binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                Toast.makeText(this@MainActivity, "Image processed!", Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun processWithOperation(operation: String) {
        val uri = selectedImageUri ?: return
        
        showLoading(true)
        
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val inputPath = getPathFromUri(uri)
                    if (inputPath == null) {
                        return@withContext Result.failure(FileNotFoundException("Cannot access image"))
                    }
                    
                    val outputFile = File(cacheDir, "${operation}_${System.currentTimeMillis()}.jpg")
                    currentProcessedPath = outputFile.absolutePath
                    
                    val success = when (operation) {
                        "blur" -> imageMagick.blurImage(inputPath, outputFile.absolutePath, BLUR_RADIUS, BLUR_SIGMA)
                        "grayscale" -> imageMagick.grayscaleImage(inputPath, outputFile.absolutePath)
                        "sepia" -> imageMagick.applySepia(inputPath, outputFile.absolutePath)
                        "negate" -> imageMagick.negateImage(inputPath, outputFile.absolutePath)
                        "flip" -> imageMagick.flipImage(inputPath, outputFile.absolutePath)
                        "rotate" -> imageMagick.rotateImage(inputPath, outputFile.absolutePath, ROTATE_DEGREES)
                        "resize" -> imageMagick.resizeImage(inputPath, outputFile.absolutePath, SMALL_RESIZE_DIMENSION, SMALL_RESIZE_DIMENSION, SMALL_RESIZE_QUALITY)
                        else -> false
                    }
                    
                    if (success) {
                        Result.success(outputFile)
                    } else {
                        Result.failure(IOException("Processing failed"))
                    }
                } catch (e: IOException) {
                    Result.failure(e)
                }
            }
            
            showLoading(false)
            
            result.onSuccess { file ->
                binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                Toast.makeText(this@MainActivity, "$operation applied!", Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveProcessedImage() {
        val path = currentProcessedPath ?: return
        
        try {
            val sourceFile = File(path)
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val destFile = File(picturesDir, "IMG_${System.currentTimeMillis()}.jpg")
            
            sourceFile.copyTo(destFile, overwrite = true)
            
            Toast.makeText(this, "Saved to ${destFile.absolutePath}", Toast.LENGTH_LONG).show()
            
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destFile)))
        } catch (e: IOException) {
            Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    @Suppress("SwallowedException")
    private fun getPathFromUri(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File(cacheDir, "temp_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile.absolutePath
        } catch (e: IOException) {
            null
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.selectImageButton.isEnabled = !show
        binding.processButton.isEnabled = !show && selectedImageUri != null
    }
}

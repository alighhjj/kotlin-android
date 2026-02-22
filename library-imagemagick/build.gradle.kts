plugins {
    id("com.android.library")
}

android {
    compileSdk = libs.versions.compile.sdk.version.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.version.get().toInt()
        namespace = "com.blogtown.imagemagick"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs(listOf("src/main/jniLibs"))
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
}

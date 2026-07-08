import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvmToolchain(17)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.composeMaterialIconsExtended)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(libs.coroutinesCore)
                implementation(libs.datetime)
                implementation(libs.kermit)
                implementation(libs.koinCompose)
                implementation(libs.koinCore)
                implementation(libs.serializationJson)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidxActivityCompose)
                implementation(libs.androidsvg)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.coroutinesSwing)
                implementation(libs.batikTranscoder)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutinesTest)
                implementation(libs.koinTest)
            }
        }
    }
}

android {
    namespace = "com.tlincompose"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tlincompose"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.12"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.tlincompose.desktop.MainKt"

        nativeDistributions {
            packageName = "TLInCompose"
            packageVersion = "1.0.0"
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            linux {
                iconFile.set(project.file("src/desktopMain/resources/icons/tlincompose-linux.png"))
            }
            macOS {
                iconFile.set(project.file("src/desktopMain/resources/icons/tlincompose.icns"))
            }
            windows {
                iconFile.set(project.file("src/desktopMain/resources/icons/tlincompose.ico"))
            }
        }
    }
}

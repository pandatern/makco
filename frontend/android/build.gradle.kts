// Top-level build file
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://repo.hyper-sdk.in/repository/hyper-sdk/") }
    }
}

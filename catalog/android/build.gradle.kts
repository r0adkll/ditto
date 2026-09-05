plugins {
  alias(libs.plugins.android.application)
  id("ditto.compose")
}

android {
  namespace = "com.r0adkll.ditto.catalog"
  compileSdk { version = release(37) }

  defaultConfig {
    applicationId = "com.r0adkll.ditto.catalog"
    minSdk = 31
    targetSdk = 37
    versionCode = 1
    versionName = "0.1.0"
  }

  buildFeatures {
    compose = true
  }
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(projects.catalog.shared)
  implementation(libs.androidx.activity.compose)
}

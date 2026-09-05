// Test-only screenshot harness (ADR-016). JVM only, never published.
plugins {
  alias(libs.plugins.kotlin.jvm)
  id("ditto.compose")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

kotlin {
  explicitApi()
}

dependencies {
  api(projects.dittoCore)
  api(compose.desktop.currentOs)
  @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
  api(compose.uiTest)
  implementation(libs.kotlinx.coroutines.swing)
  implementation(libs.kotlin.test)
}

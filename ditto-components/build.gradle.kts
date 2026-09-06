plugins {
  id("ditto.multiplatform.library")
  id("ditto.compose")
  id("ditto.publishing")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.dittoCore)
      // Behaviour engines only (ADR-028): wrapped, never exposed in Ditto's public API.
      implementation(libs.compose.unstyled.bottom.sheet)
      implementation(libs.compose.unstyled.scrollbars)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
    jvmTest.dependencies {
      implementation(projects.internal.screenshot)
    }
  }
}

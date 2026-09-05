plugins {
  id("ditto.multiplatform.library")
  id("ditto.compose")
  id("ditto.publishing")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.compose.runtime)
      api(libs.compose.foundation)
      api(libs.compose.ui)
      implementation(libs.compose.ui.tooling.preview)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
    jvmTest.dependencies {
      implementation(projects.internal.screenshot)
    }
  }
}

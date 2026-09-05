// Bridges DittoTheme to Material3 so M3 components can live inside a Ditto app (ADR-004, ADR-014).
// Pinned to the same Material3 (Expressive alpha) that Campfire uses.
plugins {
  id("ditto.multiplatform.library")
  id("ditto.compose")
  id("ditto.publishing")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll(
      "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
      "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
    )
  }
  sourceSets {
    commonMain.dependencies {
      api(projects.dittoCore)
      api(libs.compose.material3)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
    jvmTest.dependencies {
      implementation(projects.internal.screenshot)
      implementation(projects.dittoComponents)
    }
  }
}

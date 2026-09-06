// Spike: Ditto components re-implemented on Compose Unstyled primitives. Not published.
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("ditto.multiplatform.library")
  id("ditto.compose")
}

kotlin {
  explicitApi = ExplicitApiMode.Disabled
  sourceSets {
    commonMain.dependencies {
      api(projects.dittoCore)
      api(projects.dittoComponents)
      implementation(libs.compose.unstyled)
    }
    jvmTest.dependencies {
      implementation(projects.internal.screenshot)
      implementation(libs.kotlin.test)
      implementation(libs.compose.unstyled)
    }
  }
}

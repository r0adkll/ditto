import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  id("ditto.compose")
}

kotlin {
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    outputModuleName.set("catalog")
    browser {
      commonWebpackConfig {
        outputFileName = "catalog.js"
      }
    }
    binaries.executable()
  }

  sourceSets {
    wasmJsMain.dependencies {
      implementation(projects.catalog.shared)
    }
  }
}

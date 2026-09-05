plugins {
  id("ditto.multiplatform.library")
  id("ditto.compose")
  id("ditto.publishing")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.dittoCore)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
    jvmTest.dependencies {
      implementation(projects.internal.screenshot)
    }
  }
}

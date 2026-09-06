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
    }
  }
}

kotlin {
  listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
    target.binaries.framework {
      baseName = "CatalogKit"
      isStatic = true
    }
  }
}

kotlin {
  sourceSets {
    commonTest.dependencies { implementation(libs.kotlin.test) }
    jvmTest.dependencies {
      implementation(projects.internal.screenshot)
      @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
      implementation(compose.uiTest)
    }
  }
}

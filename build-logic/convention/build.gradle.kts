plugins {
  `kotlin-dsl`
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.compose.gradlePlugin)
  compileOnly(libs.composeCompiler.gradlePlugin)
  compileOnly(libs.maven.publish.gradlePlugin)
  compileOnly(libs.binary.compatibility.validator.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("multiplatformLibrary") {
      id = "ditto.multiplatform.library"
      implementationClass = "com.r0adkll.ditto.convention.MultiplatformLibraryConventionPlugin"
    }
    register("compose") {
      id = "ditto.compose"
      implementationClass = "com.r0adkll.ditto.convention.ComposeConventionPlugin"
    }
    register("publishing") {
      id = "ditto.publishing"
      implementationClass = "com.r0adkll.ditto.convention.PublishingConventionPlugin"
    }
  }
}

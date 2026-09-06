// Spike: Ditto's Desktop idiom side by side with JetBrains Jewel (Int UI). Not published.
plugins {
  alias(libs.plugins.kotlin.jvm)
  id("ditto.compose")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(25)) // Jewel 0.40 class files target Java 25
  }
}

dependencies {
  implementation(projects.dittoComponents)
  implementation(compose.desktop.currentOs)
  implementation(libs.kotlinx.coroutines.swing)
  // Jewel 0.40 is built against Compose 1.11.0; we run it on 1.12.0 for the spike only.
  implementation("org.jetbrains.jewel:jewel-int-ui-standalone:0.40.0-262.10315.125")
  testImplementation(projects.internal.screenshot)
  testImplementation(libs.kotlin.test)
}

configurations.all {
  // Jewel pulls the IntelliJ coroutines fork; use the standard artifact so there is one coroutines core.
  resolutionStrategy.dependencySubstitution {
    substitute(module("org.jetbrains.intellij.deps.kotlinx:kotlinx-coroutines-core-jvm"))
      .using(module("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0"))
  }
}

compose.desktop {
  application {
    mainClass = "com.r0adkll.ditto.spike.MainKt"
  }
}

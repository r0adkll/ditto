import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  alias(libs.plugins.kotlin.jvm)
  id("ditto.compose")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

dependencies {
  implementation(projects.catalog.shared)
  implementation(compose.desktop.currentOs)
  implementation(libs.kotlinx.coroutines.swing)
}

compose.desktop {
  application {
    mainClass = "com.r0adkll.ditto.catalog.MainKt"
    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "Ditto Catalog"
      packageVersion = "1.0.0"
    }
  }
}

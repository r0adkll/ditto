plugins {
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.kotlin.multiplatform.library) apply false
  alias(libs.plugins.compose.multiplatform) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.maven.publish) apply false
  alias(libs.plugins.binary.compatibility.validator) apply false
  alias(libs.plugins.dokka)
}

/**
 * API reference for the docs site. `./gradlew dokkaGeneratePublicationHtml` writes the aggregated
 * HTML to `build/dokka/html`, which the Pages workflow copies to `/ditto/api/` — the destination
 * the docs sidebar has been linking to.
 *
 * Only the published modules are aggregated: the catalog, spikes and internal test harness are not
 * API anyone can depend on, so documenting them would be misleading.
 */
dependencies {
  dokka(projects.dittoCore)
  dokka(projects.dittoComponents)
  dokka(projects.dittoMaterial3Interop)
}

dokka {
  moduleName.set("Ditto")
  dokkaPublications.html {
    outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
  }
}

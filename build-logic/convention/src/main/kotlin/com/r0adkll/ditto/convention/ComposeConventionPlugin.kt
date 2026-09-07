package com.r0adkll.ditto.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposeConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.compose")
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    // CMP-4906 fires this check on every Compose module with a wasmJs target, whether or not it
    // has any wasm tests, and asks for `binaries.executable()` — webpack bundling on libraries that
    // publish a klib. Ditto's UI and screenshot tests are JVM-only (ADR-016), so on a module with
    // no wasm test sources the check guards nothing and only breaks `./gradlew check`. It re-arms
    // itself the moment a `src/wasmJsTest` appears, so this cannot silently hide a real problem.
    // A plain boolean rather than `onlyIf {}`: the lambda captures the project and breaks the
    // configuration cache.
    val hasWasmTests = layout.projectDirectory.dir("src/wasmJsTest").asFile.exists()
    tasks.matching { it.name.startsWith("checkComposeUiTestConfigurationFor") }.configureEach {
      enabled = hasWasmTests
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
      includeSourceInformation.set(true)
      if (providers.gradleProperty("ditto.composeCompilerReports").isPresent) {
        val dir = layout.buildDirectory.map { it.dir("reports").dir("compose") }
        reportsDestination.set(dir)
        metricsDestination.set(dir)
      }
    }
  }
}

package com.r0adkll.ditto.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposeConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.compose")
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

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

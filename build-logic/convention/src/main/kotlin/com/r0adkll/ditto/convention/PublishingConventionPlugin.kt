package com.r0adkll.ditto.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Maven Central publishing + binary-compatibility validation for published modules (ADR-017,
 * ADR-026). Coordinates, POM and signing come from `gradle.properties` (GROUP, VERSION_NAME,
 * POM_*, SONATYPE_HOST, RELEASE_SIGNING_ENABLED).
 */
class PublishingConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("com.vanniktech.maven.publish")
    pluginManager.apply("org.jetbrains.kotlinx.binary-compatibility-validator")
  }
}

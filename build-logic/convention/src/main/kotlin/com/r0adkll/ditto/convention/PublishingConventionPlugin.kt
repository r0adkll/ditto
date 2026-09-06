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

    // ADR-028 guardrail: behaviour dependencies must not leak into Ditto's public API.
    val leakCheck = tasks.register("checkNoLeakedTypes") {
      group = "verification"
      description = "Fails if third-party (non-Compose/Kotlin) types appear in the binary API dumps."
      val apiDir = layout.projectDirectory.dir("api")
      mustRunAfter(tasks.matching { it.name.endsWith("ApiDump") || it.name == "apiDump" })
      doLast {
        val forbidden = listOf("com/composeunstyled", "com/composables", "org/jetbrains/jewel")
        val offenders = apiDir.asFileTree.matching { include("**/*.api") }.files.flatMap { file ->
          file.readLines().withIndex().filter { (_, line) -> forbidden.any { it in line } }.map { (i, line) -> "${file.name}:${i + 1}: ${line.trim()}" }
        }
        if (offenders.isNotEmpty()) {
          throw org.gradle.api.GradleException("Third-party types leaked into the public API:\n" + offenders.joinToString("\n"))
        }
      }
    }
    tasks.named("check").configure { dependsOn(leakCheck) }
  }
}

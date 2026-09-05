package com.r0adkll.ditto.convention

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure

internal fun Project.configureJavaToolchain() {
  extensions.configure<JavaPluginExtension> {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(Versions.javaToolchain))
    }
  }
}

internal fun Project.dittoNamespace(): String =
  "com.r0adkll.ditto." + path.substring(1).replace(':', '.').replace("-", "_").removePrefix("ditto_")

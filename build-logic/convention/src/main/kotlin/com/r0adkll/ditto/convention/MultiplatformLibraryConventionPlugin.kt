package com.r0adkll.ditto.convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

/**
 * Library modules: all four Ditto targets (ADR-002 / ADR-017), explicit API mode (ADR-026),
 * Java 21 toolchain, Android namespace derived from the project path.
 */
class MultiplatformLibraryConventionPlugin : Plugin<Project> {
  @OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.kotlin.multiplatform")
    pluginManager.apply("com.android.kotlin.multiplatform.library")

    extensions.configure<KotlinMultiplatformExtension> {
      explicitApi = ExplicitApiMode.Strict

      compilerOptions {
        freeCompilerArgs.addAll(
          "-Xexpect-actual-classes",
          "-Xconsistent-data-class-copy-visibility",
        )
      }

      applyDefaultHierarchyTemplate()

      jvm()

      extensions.configure<KotlinMultiplatformAndroidLibraryExtension>("android") {
        namespace = dittoNamespace()
        compileSdk = Versions.compileSdk
        minSdk = Versions.minSdk
        // Required for Compose Multiplatform resources on Android (CMP-9547)
        androidResources { enable = true }
      }

      iosArm64()
      iosSimulatorArm64()

      wasmJs {
        browser()
      }

      metadata {
        compilations.configureEach {
          if (name == KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME) {
            compileTaskProvider.configure {
              // Unique metadata module names avoid KT-57914 duplicate-library warnings.
              val projectPath = this@with.path.substring(1).replace(":", "_")
              this as KotlinCompileCommon
              moduleName.set("${projectPath}_commonMain")
            }
          }
        }
      }
    }

    configureJavaToolchain()
    configureScreenshotTests()
  }
}

/** `./gradlew jvmTest -Pditto.updateGoldens=true` re-records screenshot goldens (ADR-016). */
internal fun Project.configureScreenshotTests() {
  tasks.withType<Test>().configureEach {
    systemProperty("ditto.updateGoldens", providers.gradleProperty("ditto.updateGoldens").orNull ?: "false")
  }
}

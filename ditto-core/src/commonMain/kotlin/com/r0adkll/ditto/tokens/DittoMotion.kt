package com.r0adkll.ditto.tokens

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Immutable
import com.r0adkll.ditto.Idiom

/**
 * Motion tokens (ADR-009). The Android idiom leans on springs for Expressive feel (ADR-018);
 * Apple and Desktop use tighter, easing-based motion.
 */
@Immutable
public class DittoMotion(
  public val durationShort: Int,
  public val durationMedium: Int,
  public val durationLong: Int,
  public val easingStandard: Easing,
  public val easingEmphasized: Easing,
  public val easingDecelerate: Easing,
  public val easingAccelerate: Easing,
  /** Damping ratio of the spring used for press / state transitions. */
  public val springDampingRatio: Float,
  /** Stiffness of the spring used for press / state transitions. */
  public val springStiffness: Float,
  /** Scale applied to pressable containers while pressed (1f disables the effect). */
  public val pressScale: Float,
) {
  /** The idiom's spring, for `Float` animations. */
  public val spring: SpringSpec<Float> get() = springFor()

  /** The idiom's spring, typed for any animatable value (`Dp`, `Color`, `Offset`, ...). */
  public fun <T> springFor(): SpringSpec<T> = spring(dampingRatio = springDampingRatio, stiffness = springStiffness)

  override fun equals(other: Any?): Boolean = other is DittoMotion &&
    durationShort == other.durationShort && durationMedium == other.durationMedium && durationLong == other.durationLong &&
    easingStandard == other.easingStandard && easingEmphasized == other.easingEmphasized &&
    easingDecelerate == other.easingDecelerate && easingAccelerate == other.easingAccelerate &&
    springDampingRatio == other.springDampingRatio && springStiffness == other.springStiffness && pressScale == other.pressScale

  override fun hashCode(): Int = listOf(
    durationShort, durationMedium, durationLong, easingStandard, easingEmphasized, easingDecelerate,
    easingAccelerate, springDampingRatio, springStiffness, pressScale,
  ).hashCode()

  override fun toString(): String = "DittoMotion(durationMedium=$durationMedium)"

  public companion object {
    private val Standard = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    private val Emphasized = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    private val Decelerate = CubicBezierEasing(0f, 0f, 0f, 1f)
    private val Accelerate = CubicBezierEasing(0.3f, 0f, 1f, 1f)
    private val AppleStandard = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
    private val WebStandard = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

    public fun forIdiom(idiom: Idiom): DittoMotion = when (idiom) {
      Idiom.Android -> DittoMotion(
        durationShort = 150,
        durationMedium = 300,
        durationLong = 500,
        easingStandard = Standard,
        easingEmphasized = Emphasized,
        easingDecelerate = Decelerate,
        easingAccelerate = Accelerate,
        springDampingRatio = 0.7f,
        springStiffness = Spring.StiffnessMedium,
        pressScale = 0.96f,
      )
      Idiom.Apple -> DittoMotion(
        durationShort = 120,
        durationMedium = 250,
        durationLong = 400,
        easingStandard = AppleStandard,
        easingEmphasized = AppleStandard,
        easingDecelerate = Decelerate,
        easingAccelerate = Accelerate,
        springDampingRatio = Spring.DampingRatioNoBouncy,
        springStiffness = Spring.StiffnessMediumLow,
        pressScale = 1f,
      )
      Idiom.Desktop -> DittoMotion(
        durationShort = 100,
        durationMedium = 180,
        durationLong = 300,
        easingStandard = WebStandard,
        easingEmphasized = WebStandard,
        easingDecelerate = Decelerate,
        easingAccelerate = Accelerate,
        springDampingRatio = Spring.DampingRatioNoBouncy,
        springStiffness = Spring.StiffnessHigh,
        pressScale = 1f,
      )
    }
  }
}

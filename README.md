# Ditto

![](.github/art/DittoMascot.svg) 

A [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) UI toolkit with one component API and three platform-adaptive **idioms**, built on its own design tokens rather than Material3.

| Idiom | Default on | Feel |
|---|---|---|
| `Android` | Android | Material-esque, moderated Expressive motion |
| `Apple` | iOS | Flat iOS-style: grouped lists, 44pt targets, tinted text buttons |
| `Desktop` | Desktop JVM, Web | Neutral web: compact, bordered, hover-aware, keyboard-first |

The idiom is a runtime value, so any idiom previews and screenshot-tests on any host.

```kotlin
DittoTheme(accent = Color(0xFF7C3AED)) {           // idiom + light/dark follow the platform
  Button(onClick = { }) { Text("Continue") }
}

DittoTheme(idiom = Idiom.Apple, colorMode = ColorMode.Dark) { /* preview iOS on your Mac */ }
```

## Design language

- **Colors:** a 12-step neutral ramp (Pure / Cool / Warm / Tinted presets) plus one accent. No tonal palettes. Everything else is derived; `DittoColors.validateContrast()` reports WCAG 2 failures.
- **Typography:** eight styles — display, title, heading, subheading, body, bodySmall, label, caption.
- **Shapes:** none → full, seven steps. **Spacing:** xxs → xxxl times an idiom density multiplier.
- **Elevation:** five levels rendered per idiom as shadow, ramp step, or border.
- **Interaction:** press feedback is idiom-specific; hover, cursor and focus rings follow input capability.

## Modules

| Module | Contents |
|---|---|
| `ditto-core` | `Idiom`, tokens, `DittoTheme`, `Surface` / `Text` / `Icon`, indication, system icons |
| `ditto-components` | `Button` family, `IconButton` family (more to come) |
| `catalog/*` | Showcase app for Android, iOS, Desktop and Web with idiom / color-mode switchers |
| `internal/screenshot` | JVM screenshot harness used by the tests |

Coordinates: `com.r0adkll.ditto:ditto-core` and `com.r0adkll.ditto:ditto-components`. Pre-1.0: breaking changes may land in any release.

## Development

```sh
./gradlew jvmTest                               # unit + screenshot tests
./gradlew jvmTest -Pditto.updateGoldens=true    # re-record goldens (per-OS; Linux CI is canonical)
./gradlew :catalog:desktop:run                  # desktop catalog
./gradlew :catalog:android:installDebug         # android catalog
./gradlew :catalog:web:wasmJsBrowserDevelopmentRun   # web catalog
open catalog/ios/iosApp.xcodeproj               # iOS catalog
```

Design decisions live as ADRs in the project's Obsidian vault (see `docs/DESIGN.md` for the summary).

## License

Apache 2.0. Test fonts (Inter) are under the OFL and are not shipped in published artifacts.

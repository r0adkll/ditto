# Spike: Ditto Desktop idiom vs JetBrains Jewel (Int UI)

Renders the same settings form with Ditto's Desktop idiom and with Jewel's standalone Int UI theme,
side by side, light and dark.

```sh
./gradlew :spikes:jewel-compare:test    # writes build/compare/<os>/ditto-vs-jewel.png (first run "fails" by design)
./gradlew :spikes:jewel-compare:run     # live window with both forms
```

Notes:
- Jewel 0.40.0-262.10315.125 targets Compose 1.11.0 and Java 25; this module runs on a Java 25
  toolchain and Ditto's Compose 1.12.0. Not for production use.
- The IntelliJ coroutines fork Jewel depends on is substituted with kotlinx-coroutines 1.11.0.

Findings live in the design vault: `Architecture/Jewel Comparison.md`.

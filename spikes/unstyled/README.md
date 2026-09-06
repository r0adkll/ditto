# Spike: Ditto on Compose Unstyled

Three behaviour-heavy Ditto components re-implemented on Compose Unstyled 2.9.2 primitives, keeping
Ditto's style objects: `USlider`, `UDropdownMenu`/`UMenuItem`, `UModalSheet`. Compiles for all four
Ditto targets.

```sh
./gradlew :spikes:unstyled:jvmTest -Pditto.updateGoldens=true   # writes build/parity/<os>/parity-*.png
```

Findings and the decision live in the design vault: `Architecture/Compose Unstyled Evaluation.md`.

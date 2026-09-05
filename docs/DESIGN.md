# Ditto design summary

ADRs are maintained in the author's Obsidian vault; this is the one-page index.

# Design tree (summary)

Every branch visited 2026-09-05. Links go to the ADR that settles each node.

- **Purpose** — own apps first, publishable from day one → ADR-001 Audience; own design language, not Material → Vision; atoms + molecules only → ADR-020 Scope Atoms and Molecules
- **Platforms** — Android, iOS, Desktop JVM, wasmJs → ADR-002 Platforms and Idioms, ADR-017 Repo Build Publishing Docs
- **Idioms** — `Android` / `Apple` / `Desktop` enum → ADR-013 Idiom Naming, ADR-024 API Details
  - references: Android Expressive-moderated → ADR-018 Android Idiom Flavor; Apple flat iOS 18; Desktop shadcn/Radix → ADR-023 Idiom References
  - runtime `CompositionLocal`, `when` dispatch, style locals → ADR-003 Adaptation Mechanism, ADR-007 Dispatch Shape
  - visuals + press feedback per idiom; hover/cursor/focus per input capability → ADR-008 Interaction Behavior
- **Tokens** — universal core + typed idiom extension → ADR-005 Token Philosophy, ADR-010 Extension Slot
  - schema (≈15 color roles, 8 type styles, 7 shapes, spacing scale × density, elevation 0–4, motion) → ADR-009 Core Token Schema
  - color: 12-step neutral ramp, presets Pure/Cool/Warm/Tinted, single accent, WCAG 2 contrast, idiom-tuned elevation → ADR-019 Color Generation, ADR-022 Neutral Ramp and Elevation
  - theming API: data classes, `ColorMode`, derived scheme → ADR-011 Theming API
- **Components** — tiers → Components; conventions and DoD → ADR-014 Module Structure, Component Definition of Done; a11y → ADR-021 Accessibility; icons → ADR-015 Icons; TextField/Scaffold/system icons → ADR-024 API Details; fonts/strings/haptics/previews → ADR-026 Fonts Strings Haptics Hygiene
- **Architecture** — core / components / material3-interop / catalog → ADR-014 Module Structure; no M3 in core → ADR-004 Relationship to Material3; no Circuit/DI/Coil → ADR-017 Repo Build Publishing Docs
- **Build & release** — `com.r0adkll.ditto`, pinned to Campfire → ADR-012 Coordinates and Toolchain, Toolchain; repo/CI/publishing/docs → ADR-017 Repo Build Publishing Docs; 0.x → ADR-025 Milestones and Versioning
- **Testing** — custom first-party screenshot harness, Linux goldens → ADR-016 Testing, Screenshot Testing Research
- **Process** — milestone 1 vertical slice → ADR-025 Milestones and Versioning; naming → ADR-006 Naming

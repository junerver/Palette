# Changelog

## Unreleased

### Added

- Added root-level component theme overrides through `PaletteComponentThemes`.
- Added semantic state, opacity, motion, elevation, and control-density tokens for global style customization.
- Added component token coverage across action, selection, form, navigation, data display, feedback, overlay, progress, media, utility, layout, floating action, upload, pagination, and screen components.
- Added `docs/theming.md` with precedence rules, token customization examples, component-token mapping, and migration guidance.
- Added desktop theme override tests and Defaults static audit tests for component token adoption.

### Changed

- Routed component Defaults through `PaletteTheme.componentThemes` where the value is a major visual style surface.
- Preserved existing public Defaults constants as compatibility aliases; use the new token-backed Defaults functions for root-theme-aware values.

### Breaking Changes

- None.

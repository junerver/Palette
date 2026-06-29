# Roadmap

Palette maintains per-feature roadmap documents tracking design decisions, progress, and remaining work.

## Feature roadmaps

- [Mermaid diagram support](https://github.com/junerver/Palette/blob/master/docs/compose/plans/mermaid-diagram-support-roadmap.md) — 19/19 diagrams complete
- [Code Prism alignment](https://github.com/junerver/Palette/blob/master/docs/compose/plans/palette-code-prism-alignment-roadmap.md) — Phase 1-2 complete; Phase 3 languages (C/C++/Go/Rust) shipped
- [Markdown evolution](https://github.com/junerver/Palette/blob/master/docs/compose/plans/palette-markdown-evolution-roadmap.md) — Stages A-D complete
- [Chart roadmap](https://github.com/junerver/Palette/blob/master/docs/compose/plans/palette-chart-roadmap.md) — Pie/Bar/Line shipped; interaction & more types planned

## Maturity

The current maturity analysis lives in [maturity-gaps.md](https://github.com/junerver/Palette/blob/master/docs/maturity-gaps.md). Summary:

- **Components**: all originally-identified gaps closed (Dropdown covered by `PSelect`, Chart shipped)
- **Coverage**: 80%+ line coverage gate enforced
- **Remaining gaps** (non-component): accessibility infra, this docs site, RTL/i18n breadth, performance CI gating, platform-specific features

## Docs site backlog

This site is new. Planned improvements:

- [ ] Per-component detailed API pages (currently representative components per category)
- [ ] Full interactive preview coverage (all 88 components, not just the initial set)
- [ ] Searchable API reference (Dokka integration)
- [ ] Copy-runnable examples per component

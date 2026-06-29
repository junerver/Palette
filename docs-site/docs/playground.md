# Playground

The Playground is the **same demo app** that ships for Android and Desktop, compiled to WebAssembly and embedded live below. Click any component in the sidebar to see it render, and switch themes from the panel.

!!! note "Loading"
    The preview is a WebAssembly bundle (~30 MB). It loads on first visit; subsequent navigation between components is instant. Allow a few seconds for the initial load.

<iframe src="../playground-dist/index.html" width="100%" height="600" style="border:1px solid #e0e0e0; border-radius:8px;" title="Palette Playground"></iframe>

## How it works

The `:app` module is a single cross-platform sample — Android, Desktop, and Web share one codebase (the full component catalog, sidebar, and theme switcher live in `commonMain`). Its `wasmJs` target compiles to a webpack bundle embedded here via an iframe, so what you see is **the real palette library rendering in your browser**, not a screenshot.

Theme persistence is in-memory on the web (the browser has no filesystem); the Android and Desktop builds persist to disk. Everything else — every component, every interaction — is identical across platforms.

To run it locally:

```bash
./gradlew :app:wasmJsBrowserDevelopmentRun
```

This opens the playground in a local browser with webpack dev server (incremental rebuilds on change).

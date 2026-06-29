# Playground

The Playground is an interactive component preview, built as a pure Web/WASM Compose application and embedded live below. Click a component in the sidebar to see it render.

!!! note "Loading"
    The preview is a WebAssembly bundle (~30 MB). It loads on first visit; subsequent navigation between components is instant. Allow a few seconds for the initial load.

<iframe src="../preview/index.html" width="100%" height="600" style="border:1px solid #e0e0e0; border-radius:8px;" title="Palette Playground"></iframe>

## How it works

The preview is the `:preview` Gradle submodule — a pure `wasmJs` Compose app that depends on `:palette`. Its webpack output is embedded here via an iframe, so what you see is **the real palette library rendering in your browser**, not a screenshot.

To run it locally:

```bash
./gradlew :preview:wasmJsBrowserRun
```

This opens the preview in a local browser with hot reload during development.

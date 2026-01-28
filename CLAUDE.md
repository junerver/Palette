# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Palette 是一个 Compose Multiplatform 组件库，支持 Android、Desktop (JVM) 和 iOS 平台。

## Build Commands

```bash
# 构建整个项目
./gradlew build

# 仅构建组件库
./gradlew :palette:build

# 运行 Desktop 应用
./gradlew :app:run

# 运行 Android 应用 (需连接设备/模拟器)
./gradlew :app:installDebug

# 运行测试
./gradlew :palette:allTests
./gradlew :palette:desktopTest      # Desktop 测试
./gradlew :palette:testDebugUnitTest # Android 单元测试

# 发布到本地 Maven
./gradlew :palette:publishToMavenLocal
```

## Architecture

```
Palette/
├── palette/          # 组件库模块 (发布产物)
│   └── src/
│       ├── commonMain/    # 跨平台共享代码
│       ├── commonJvmAndroid/  # JVM+Android 共享
│       ├── androidMain/   # Android 特定实现
│       ├── desktopMain/   # Desktop 特定实现
│       └── iosMain/       # iOS 特定实现
└── app/              # 示例应用模块
    └── src/
        ├── commonMain/    # 共享 UI
        ├── androidMain/   # Android 入口
        └── desktopMain/   # Desktop 入口
```

## Key Dependencies

- **compose-hooks** (`xyz.junerver.compose:hooks2`): 项目依赖的 Hooks 库，提供 React 风格的状态管理
- **Material3**: UI 组件基于 Material Design 3
- **kotlinx-datetime / kotlinx-collections-immutable**: 跨平台工具库

## Platform Targets

- Android: minSdk 24, targetSdk 35
- Desktop: JVM (Kotlin/JVM)
- iOS: x64, arm64, simulatorArm64

## Code Style

- 包名: `xyz.junerver.compose.palette`
- 组件文件使用 snake_case 命名 (如 `border_box.kt`)
- 使用 Kotlin DSL 构建脚本

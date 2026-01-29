# Repository Guidelines

## 项目结构与模块组织

- `palette/`：组件库模块（发布产物），源码在 `palette/src/`，含 `commonMain/`、`androidMain/`、`desktopMain/`、`iosMain/` 等多平台源码与对应 `*Test/` 目录。
- `app/`：示例应用模块，源码在 `app/src/`，含 `commonMain/`、`androidMain/`、`desktopMain/` 与测试目录。
- Gradle 配置位于根目录 `build.gradle.kts`、`settings.gradle.kts`、`gradle.properties`。

## 构建、测试与本地运行

- `./gradlew build`：构建全项目。
- `./gradlew :palette:build`：仅构建组件库。
- `./gradlew :app:run`：运行 Desktop 应用。
- `./gradlew :app:installDebug`：安装 Android Debug 包（需设备/模拟器）。
- `./gradlew :palette:allTests`：运行组件库全部测试。
- `./gradlew :palette:desktopTest`：运行 Desktop 测试。
- `./gradlew :palette:testDebugUnitTest`：运行 Android 单元测试。
- `./gradlew :palette:publishToMavenLocal`：发布到本地 Maven。

## 编码风格与命名规范

- Kotlin 代码风格：`kotlin.code.style=official`（见 `gradle.properties`）。
- 包名：`xyz.junerver.compose.palette`。
- 组件文件使用 snake_case 命名，例如 `border_box.kt`（见 `CLAUDE.md`）。
- 未发现统一的格式化/静态检查配置（如 ktlint/detekt/spotless），请保持与现有代码一致。

## 测试指南

- 组件库使用 Kotlin 测试依赖 `kotlin("test")`。
- Android 仪器测试运行器：`androidx.test.runner.AndroidJUnitRunner`。
- 测试源集遵循 KMP 目录约定：`commonTest/`、`androidUnitTest/`、`desktopTest/`、`iosTest/` 等。

## 提交与 PR 规范

- 提交信息常见格式：`feat:`、`refactor:` 等前缀（见最近 git log）。
- 仓库内未发现明确的 PR 模板或 commitlint 规则；提交与 PR 描述请清晰说明变更范围与影响。

## 其他说明

- 该仓库是 Compose Multiplatform 组件库，平台目标含 Android、Desktop 与 iOS（详情见 `CLAUDE.md`）。

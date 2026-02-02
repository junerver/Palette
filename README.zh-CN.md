# Palette

一个支持 Android、Desktop (JVM) 和 iOS 平台的 Compose Multiplatform 组件库。

## 特性

- **多平台支持**：Android (API 24+)、Desktop (JVM)、iOS (x64, arm64, simulatorArm64)
- **设计令牌**：集中化的颜色、间距、形状和字体系统
- **主题系统**：`PaletteTheme` 支持深色/浅色模式
- **UI 组件**：徽章、复选框、文本框、工具栏、页面容器等
- **状态管理**：集成 [compose-hooks](https://github.com/junerver/compose-hooks) 实现 React 风格状态管理

## 快速开始

```kotlin
// build.gradle.kts
dependencies {
    implementation("xyz.junerver.compose:palette:版本号")
}
```

```kotlin
import xyz.junerver.compose.palette.*

@Composable
fun MyApp() {
    PaletteTheme(darkTheme = true) {
        Screen(title = "Hello Palette") {
            // 你的内容
        }
    }
}
```

## 构建与运行

```bash
./gradlew :palette:build
./gradlew :app:run
```

## 项目结构

```
palette/
├── core/           # 设计令牌、主题、工具类
├── foundation/     # 基础组件（边框、布局）
└── components/     # UI 组件
```

## 许可证

见 [LICENSE](LICENSE) 文件。

---

[English](./README.md)

# Palette

Palette 是一个面向 Android、Desktop (JVM) 和 iOS 的 Compose Multiplatform 组件库。项目提供基于设计令牌的主题系统、可复用 UI 组件，以及用于验证组件效果的桌面示例应用。

[English](./README.md)

## 预览

<p>
  <img src="./art/palette-button-demo.png" alt="Palette 按钮示例" width="48%" />
  <img src="./art/palette-textfield-demo.png" alt="Palette 输入框示例" width="48%" />
</p>

<p>
  <img src="./art/palette-badge-demo.png" alt="Palette 徽章示例" width="48%" />
  <img src="./art/palette-table-demo.png" alt="Palette 表格示例" width="48%" />
</p>

<p>
  <img src="./art/palette-token-demo.png" alt="Palette 全局 Token 预设示例" width="48%" />
</p>

## 亮点

- **Compose Multiplatform**：Android、Desktop、iOS 共享 UI 代码。
- **Token 优先的主题系统**：通过 `PaletteTheme` 暴露颜色、间距、形状、字体、透明度、动效、阴影和控件密度。
- **组件级主题令牌**：`PaletteComponentThemes` 支持在应用主题根部统一调整组件样式，减少逐实例覆盖。
- **常用 UI 组件**：覆盖表单、反馈、导航、布局、数据展示和工具类组件。
- **示例应用**：`app` 模块提供组件搜索、深浅色切换、语言切换和全局 Token 预设。
- **compose-hooks 集成**：在适合的状态场景中使用 [compose-hooks](https://github.com/junerver/compose-hooks) 的 React 风格状态管理方式。

## 组件范围

示例应用目前按以下类别组织组件：

| 分类 | 组件 |
| --- | --- |
| 表单组件 | Button、Checkbox、Radio、Switch、Slider、TextField、Rate、Form、SearchBar、InputNumber、Cascader、Transfer、Calendar、Segmented、InputOTP、Autocomplete、TreeSelect、ColorPicker、Toggle、Mentions、CascaderPanel |
| 反馈组件 | Loading、Progress、Badge、Dialog、Toast、Skeleton、Tag、Popup、ActionSheet、ContextMenu、DashboardProgress、Alert、Popconfirm、Result、InfiniteScroll、Watermark、FloatButton |
| 导航组件 | Toolbar、Affix、Backtop、PageHeader |
| 布局组件 | RowLayout、BorderBox、Card、Avatar、Collapse、Grid、Space |
| 数据展示 | Table、List、Descriptions、Statistic、Timeline、Tree、Image、Carousel、Pagination、Empty、QRCode |

## 安装

```kotlin
// build.gradle.kts
dependencies {
    implementation("xyz.junerver.compose:palette:0.1.0")
}
```

Palette 支持 Android `minSdk 24`、Desktop JVM，以及 iOS `arm64` / `simulatorArm64`。

## 快速开始

```kotlin
import androidx.compose.runtime.Composable
import xyz.junerver.compose.palette.PaletteMaterialTheme
import xyz.junerver.compose.palette.Screen

@Composable
fun MyApp() {
    PaletteMaterialTheme(darkTheme = true) {
        Screen(title = "Hello Palette") {
            // 页面内容
        }
    }
}
```

## 主题系统

Palette 暴露两层主题能力：

1. 核心语义令牌：`PaletteTheme.colors`、`spacing`、`shapes`、`typography`、`opacity`、`motion`、`elevation`、`control`。
2. 组件令牌：由 `PaletteComponentThemes` 提供，并通过 `PaletteTheme.componentThemes` 访问。

调整品牌色时推荐使用 `derive()`，让相关语义色自动重新派生：

```kotlin
val colors = PaletteColors.light().derive(
    primary = Color(0xFF0057D9),
    surface = Color.White,
)

PaletteMaterialTheme(colors = colors) {
    App()
}
```

需要统一调整组件样式时，使用 `PaletteComponentThemes`：

```kotlin
val colors = PaletteColors.light().derive(primary = Color(0xFF0057D9))
val components = PaletteComponentThemes.default(colors = colors).let { base ->
    base.copy(
        button = base.button.copy(disabledAlpha = 0.56f),
        card = base.card.copy(cornerRadius = 0.dp, elevation = 0.dp),
    )
}

PaletteMaterialTheme(
    colors = colors,
    componentThemes = components,
) {
    App()
}
```

更多优先级规则、组件 Token 分组和迁移说明见 [docs/theming.md](docs/theming.md)。

## 构建与运行

```bash
./gradlew :palette:build
./gradlew :palette:allTests
./gradlew :app:run
./gradlew :app:hotRunDesktop
```

常用局部任务：

```bash
./gradlew :palette:desktopTest
./gradlew :palette:testDebugUnitTest
./gradlew :palette:publishToMavenLocal
./gradlew :palette:desktopBenchmarkBenchmark
```

## 项目结构

```text
Palette/
├── palette/                    # 组件库模块
│   └── src/
│       ├── commonMain/         # 共享组件、Token 和主题代码
│       ├── commonJvmAndroid/   # JVM + Android 共享代码
│       ├── androidMain/        # Android 特定实现
│       ├── desktopMain/        # Desktop 特定实现
│       └── iosMain/            # iOS 特定实现
├── app/                        # 示例应用
├── docs/                       # 设计和迁移说明
├── benchmark/                  # 基准测试模块
└── art/                        # README 使用的截图素材
```

## 开发说明

- 统一导出文件位于 `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/Palette.kt`。
- 组件主要视觉样式应通过主题 Token 或组件级 Token 获取默认值。
- 新增组件时建议包含组件实现文件和 `XxxDefaults.kt`。
- 组件逻辑改动应补充正常路径、边界条件和回归场景测试。

## 许可证

见 [LICENSE](LICENSE)。

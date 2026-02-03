# Repository Guidelines

## 项目概述

Palette 是一个 Compose Multiplatform 组件库，支持 Android、Desktop (JVM) 和 iOS 平台。

## 项目结构

```
Palette/
├── palette/                    # 组件库模块（发布产物）
│   └── src/
│       ├── commonMain/         # 跨平台共享代码
│       │   └── kotlin/xyz/junerver/compose/palette/
│       │       ├── core/       # 核心模块
│       │       │   ├── tokens/     # 设计令牌 (Colors, Shapes, Spacing, Typography)
│       │       │   ├── theme/      # 主题 (PaletteTheme, PaletteMaterialTheme)
│       │       │   └── util/       # 工具类 (ModifierExtensions, PaletteDefaults)
│       │       ├── foundation/     # 基础组件
│       │       │   ├── border/     # 边框 (BorderContainer)
│       │       │   └── layout/     # 布局 (CenterVerticallyRow)
│       │       ├── components/     # UI 组件
│       │       │   ├── badge/      # 徽章
│       │       │   ├── checkbox/   # 复选框
│       │       │   ├── screen/     # 屏幕容器
│       │       │   ├── textfield/  # 文本输入
│       │       │   └── toolbar/    # 工具栏
│       │       └── Palette.kt      # 统一导出文件
│       ├── commonJvmAndroid/   # JVM+Android 共享
│       ├── androidMain/        # Android 特定实现
│       ├── desktopMain/        # Desktop 特定实现
│       └── iosMain/            # iOS 特定实现
└── app/                        # 示例应用模块
    └── src/
        ├── commonMain/         # 共享 UI
        ├── androidMain/        # Android 入口
        └── desktopMain/        # Desktop 入口
```

## 构建命令

```bash
./gradlew build                      # 构建全项目
./gradlew :palette:build             # 仅构建组件库
./gradlew :app:run                   # 运行 Desktop 应用
./gradlew :app:hotRunDesktop         # 通过 hotrun 插件运行支持热更新的 Desktop 应用
./gradlew :app:installDebug          # 安装 Android Debug 包
./gradlew :palette:allTests          # 运行组件库全部测试
./gradlew :palette:desktopTest       # 运行 Desktop 测试
./gradlew :palette:testDebugUnitTest # 运行 Android 单元测试
./gradlew :palette:publishToMavenLocal # 发布到本地 Maven
```

## 编码规范

### 命名规范

- **包名**: `xyz.junerver.compose.palette`
- **文件名**: PascalCase（如 `PaletteTheme.kt`, `BorderContainer.kt`）
- **组件函数**: PascalCase（如 `PBadge`, `BorderTextField`）
- **Defaults 对象**: `XxxDefaults`（如 `BadgeDefaults`, `TextFieldDefaults`）

### 组件结构

每个组件目录应包含：

- `Xxx.kt` - 组件实现
- `XxxDefaults.kt` - 默认值定义

```kotlin
// XxxDefaults.kt 示例
object XxxDefaults {
    val Size: Dp = 10.dp

    @Composable
    fun color(): Color = PaletteDefaults.colors.primary
}

// Xxx.kt 示例
@Composable
fun Xxx(
    modifier: Modifier = Modifier,
    size: Dp = XxxDefaults.Size,
    color: Color = XxxDefaults.color(),
    // ...
) {
    // 实现
}
```

### 主题与 Tokens

- 使用 `PaletteTheme` 访问主题 tokens：
  - `PaletteTheme.colors` - 颜色
  - `PaletteTheme.spacing` - 间距
  - `PaletteTheme.shapes` - 形状
  - `PaletteTheme.typography` - 字体
  - `PaletteTheme.isDark` - 深色模式状态
- 使用 `PaletteDefaults` 访问全局默认值

### 状态管理

项目使用 `compose-hooks` 库进行状态管理：

```kotlin
import xyz.junerver.compose.hooks.useState

val (value, setValue) = useState(initialValue)
```

### 统一导出

新增组件需在 `Palette.kt` 中添加 typealias 导出：

```kotlin
// 导入
import xyz.junerver.compose.palette.components.xxx.Xxx as XxxImpl
import xyz.junerver.compose.palette.components.xxx.XxxDefaults as XxxDefaultsImpl

// 导出
typealias XxxDefaults = XxxDefaultsImpl
```

## 关键依赖

- **compose-hooks** (`xyz.junerver.compose:hooks2`): React 风格状态管理
- **Material3**: UI 组件基于 Material Design 3
- **kotlinx-datetime / kotlinx-collections-immutable**: 跨平台工具库

## 平台目标

- Android: minSdk 24, targetSdk 35
- Desktop: JVM (Kotlin/JVM)
- iOS: x64, arm64, simulatorArm64

## 提交规范

提交信息使用常见前缀：`feat:`, `fix:`, `refactor:`, `chore:`, `docs:`

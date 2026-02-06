@file:Suppress("unused")

package xyz.junerver.compose.palette

import xyz.junerver.compose.palette.components.avatar.AvatarDefaults as AvatarDefaultsImpl
import xyz.junerver.compose.palette.components.avatar.AvatarSize as AvatarSizeImpl
import xyz.junerver.compose.palette.components.badge.BadgeDefaults as BadgeDefaultsImpl
import xyz.junerver.compose.palette.components.button.ButtonDefaults as ButtonDefaultsImpl
import xyz.junerver.compose.palette.components.button.ButtonSize as ButtonSizeImpl
import xyz.junerver.compose.palette.components.button.ButtonType as ButtonTypeImpl
import xyz.junerver.compose.palette.components.card.CardColors as CardColorsImpl
import xyz.junerver.compose.palette.components.card.CardDefaults as CardDefaultsImpl
import xyz.junerver.compose.palette.components.card.CardVariant as CardVariantImpl
import xyz.junerver.compose.palette.components.checkbox.CheckboxDefaults as CheckboxDefaultsImpl
import xyz.junerver.compose.palette.components.collapse.CollapseDefaults as CollapseDefaultsImpl
import xyz.junerver.compose.palette.components.collapse.CollapseItemData as CollapseItemDataImpl
import xyz.junerver.compose.palette.components.descriptions.DescriptionItem as DescriptionItemImpl
import xyz.junerver.compose.palette.components.descriptions.DescriptionsDefaults as DescriptionsDefaultsImpl
import xyz.junerver.compose.palette.components.dialog.DialogDefaults as DialogDefaultsImpl
import xyz.junerver.compose.palette.components.dialog.DialogState as DialogStateImpl
import xyz.junerver.compose.palette.components.empty.EmptyDefaults as EmptyDefaultsImpl
import xyz.junerver.compose.palette.components.image.ImageDefaults as ImageDefaultsImpl
import xyz.junerver.compose.palette.components.list.ListDefaults as ListDefaultsImpl
import xyz.junerver.compose.palette.components.loading.LoadingDefaults as LoadingDefaultsImpl
import xyz.junerver.compose.palette.components.pagination.PaginationColors as PaginationColorsImpl
import xyz.junerver.compose.palette.components.pagination.PaginationDefaults as PaginationDefaultsImpl
import xyz.junerver.compose.palette.components.progress.ProgressDefaults as ProgressDefaultsImpl
import xyz.junerver.compose.palette.components.radio.RadioDefaults as RadioDefaultsImpl
import xyz.junerver.compose.palette.components.rate.RateDefaults as RateDefaultsImpl
import xyz.junerver.compose.palette.components.screen.LocalPlatformActivity as LocalPlatformActivityImpl
import xyz.junerver.compose.palette.components.screen.PlatformActivity as PlatformActivityImpl
import xyz.junerver.compose.palette.components.screen.ScreenColors as ScreenColorsImpl
import xyz.junerver.compose.palette.components.screen.ScreenDefaults as ScreenDefaultsImpl
import xyz.junerver.compose.palette.components.skeleton.SkeletonDefaults as SkeletonDefaultsImpl
import xyz.junerver.compose.palette.components.slider.SliderDefaults as SliderDefaultsImpl
import xyz.junerver.compose.palette.components.statistic.StatisticDefaults as StatisticDefaultsImpl
import xyz.junerver.compose.palette.components.statistic.TrendType as TrendTypeImpl
import xyz.junerver.compose.palette.components.switch.SwitchDefaults as SwitchDefaultsImpl
import xyz.junerver.compose.palette.components.table.TableColors as TableColorsImpl
import xyz.junerver.compose.palette.components.table.TableDefaults as TableDefaultsImpl
import xyz.junerver.compose.palette.components.table.TableScrollBehavior as TableScrollBehaviorImpl
import xyz.junerver.compose.palette.components.tag.TagColors as TagColorsImpl
import xyz.junerver.compose.palette.components.tag.TagDefaults as TagDefaultsImpl
import xyz.junerver.compose.palette.components.tag.TagSize as TagSizeImpl
import xyz.junerver.compose.palette.components.tag.TagSizeTokens as TagSizeTokensImpl
import xyz.junerver.compose.palette.components.tag.TagVariant as TagVariantImpl
import xyz.junerver.compose.palette.components.text.PText as PTextImpl
import xyz.junerver.compose.palette.components.text.TextDefaults as TextDefaultsImpl
import xyz.junerver.compose.palette.components.textfield.BorderTextFieldColors as BorderTextFieldColorsImpl
import xyz.junerver.compose.palette.components.textfield.TextFieldDefaults as TextFieldDefaultsImpl
import xyz.junerver.compose.palette.components.timeline.TimelineDefaults as TimelineDefaultsImpl
import xyz.junerver.compose.palette.components.timeline.TimelineItemData as TimelineItemDataImpl
import xyz.junerver.compose.palette.components.toast.ToastDefaults as ToastDefaultsImpl
import xyz.junerver.compose.palette.components.toast.ToastIcon as ToastIconImpl
import xyz.junerver.compose.palette.components.toast.ToastState as ToastStateImpl
import xyz.junerver.compose.palette.components.toolbar.ToolbarColors as ToolbarColorsImpl
import xyz.junerver.compose.palette.components.toolbar.ToolbarDefaults as ToolbarDefaultsImpl
import xyz.junerver.compose.palette.components.tree.TreeDefaults as TreeDefaultsImpl
import xyz.junerver.compose.palette.components.tree.TreeNode as TreeNodeImpl
import xyz.junerver.compose.palette.components.form.FormDefaults as FormDefaultsImpl
import xyz.junerver.compose.palette.components.form.FormLayout as FormLayoutImpl
import xyz.junerver.compose.palette.components.form.FormLabelPosition as FormLabelPositionImpl
import xyz.junerver.compose.palette.components.carousel.CarouselDefaults as CarouselDefaultsImpl
import xyz.junerver.compose.palette.core.spec.ComponentInteraction as ComponentInteractionImpl
import xyz.junerver.compose.palette.core.spec.ComponentSize as ComponentSizeImpl
import xyz.junerver.compose.palette.core.spec.ComponentState as ComponentStateImpl
import xyz.junerver.compose.palette.core.spec.ComponentStatus as ComponentStatusImpl
import xyz.junerver.compose.palette.core.spec.rememberComponentInteraction as rememberComponentInteractionImpl
import xyz.junerver.compose.palette.core.i18n.PaletteStrings as PaletteStringsImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteColors as LocalPaletteColorsImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteDarkTheme as LocalPaletteDarkThemeImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteShapes as LocalPaletteShapesImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteSpacing as LocalPaletteSpacingImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteStrings as LocalPaletteStringsImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteTypography as LocalPaletteTypographyImpl
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme as PaletteMaterialThemeImpl
import xyz.junerver.compose.palette.core.theme.PaletteTheme as PaletteThemeImpl
import xyz.junerver.compose.palette.core.tokens.PaletteColors as PaletteColorsImpl
import xyz.junerver.compose.palette.core.tokens.PaletteSemanticColors as PaletteSemanticColorsImpl
import xyz.junerver.compose.palette.core.tokens.PaletteShapes as PaletteShapesImpl
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing as PaletteSpacingImpl
import xyz.junerver.compose.palette.core.tokens.PaletteTypography as PaletteTypographyImpl
import xyz.junerver.compose.palette.core.util.PaletteDefaults as PaletteDefaultsImpl
import xyz.junerver.compose.palette.foundation.border.BorderContainerDefaults as BorderContainerDefaultsImpl

// Core - Tokens
typealias PaletteColors = PaletteColorsImpl
typealias PaletteSemanticColors = PaletteSemanticColorsImpl
typealias PaletteShapes = PaletteShapesImpl
typealias PaletteSpacing = PaletteSpacingImpl
typealias PaletteTypography = PaletteTypographyImpl
typealias PaletteStrings = PaletteStringsImpl
// Core - Theme
val LocalPaletteColors = LocalPaletteColorsImpl
val LocalPaletteShapes = LocalPaletteShapesImpl
val LocalPaletteSpacing = LocalPaletteSpacingImpl
val LocalPaletteStrings = LocalPaletteStringsImpl
val LocalPaletteTypography = LocalPaletteTypographyImpl
val LocalPaletteDarkTheme = LocalPaletteDarkThemeImpl
typealias PaletteTheme = PaletteThemeImpl
typealias PaletteMaterialTheme = PaletteMaterialThemeImpl

// Core - Spec
typealias ComponentInteraction = ComponentInteractionImpl
typealias ComponentSize = ComponentSizeImpl
typealias ComponentState = ComponentStateImpl
typealias ComponentStatus = ComponentStatusImpl
val rememberComponentInteraction = ::rememberComponentInteractionImpl

// Core - Util
val PaletteDefaults = PaletteDefaultsImpl

// Foundation - Border
typealias BorderContainerDefaults = BorderContainerDefaultsImpl

// Components - Badge
typealias BadgeDefaults = BadgeDefaultsImpl

// Components - Checkbox
typealias CheckboxDefaults = CheckboxDefaultsImpl

// Components - TextField
typealias BorderTextFieldColors = BorderTextFieldColorsImpl
typealias TextFieldDefaults = TextFieldDefaultsImpl

// Components - Text
val PText = ::PTextImpl
val TextDefaults = TextDefaultsImpl

// Components - Toolbar
typealias ToolbarColors = ToolbarColorsImpl
typealias ToolbarDefaults = ToolbarDefaultsImpl

// Components - Screen
val LocalPlatformActivity = LocalPlatformActivityImpl
typealias PlatformActivity = PlatformActivityImpl
typealias ScreenColors = ScreenColorsImpl
typealias ScreenDefaults = ScreenDefaultsImpl

// Components - Loading
val LoadingDefaults = LoadingDefaultsImpl

// Components - Progress
val ProgressDefaults = ProgressDefaultsImpl

// Components - Button
typealias ButtonType = ButtonTypeImpl
typealias ButtonSize = ButtonSizeImpl
val ButtonDefaults = ButtonDefaultsImpl

// Components - Radio
val RadioDefaults = RadioDefaultsImpl

// Components - Switch
val SwitchDefaults = SwitchDefaultsImpl

// Components - Slider
val SliderDefaults = SliderDefaultsImpl

// Components - Rate
val RateDefaults = RateDefaultsImpl

// Components - Dialog
val DialogDefaults = DialogDefaultsImpl
typealias DialogState = DialogStateImpl

// Components - Toast
val ToastDefaults = ToastDefaultsImpl
typealias ToastIcon = ToastIconImpl
typealias ToastState = ToastStateImpl

// Components - Skeleton
val SkeletonDefaults = SkeletonDefaultsImpl

// Components - Pagination
val PaginationDefaults = PaginationDefaultsImpl
typealias PaginationColors = PaginationColorsImpl

// Components - Empty
val EmptyDefaults = EmptyDefaultsImpl

// Components - Card
val CardDefaults = CardDefaultsImpl
typealias CardColors = CardColorsImpl
typealias CardVariant = CardVariantImpl

// Components - Tag
val TagDefaults = TagDefaultsImpl
typealias TagColors = TagColorsImpl
typealias TagVariant = TagVariantImpl
typealias TagSize = TagSizeImpl
typealias TagSizeTokens = TagSizeTokensImpl

// Components - Avatar
val AvatarDefaults = AvatarDefaultsImpl
typealias AvatarSize = AvatarSizeImpl

// Components - Collapse
val CollapseDefaults = CollapseDefaultsImpl
typealias CollapseItemData = CollapseItemDataImpl

// Components - Descriptions
val DescriptionsDefaults = DescriptionsDefaultsImpl
typealias DescriptionItem = DescriptionItemImpl

// Components - Statistic
val StatisticDefaults = StatisticDefaultsImpl
typealias TrendType = TrendTypeImpl

// Components - Tree
val TreeDefaults = TreeDefaultsImpl
typealias TreeNode<T> = TreeNodeImpl<T>

// Components - Timeline
val TimelineDefaults = TimelineDefaultsImpl
typealias TimelineItemData = TimelineItemDataImpl

// Components - Image
val ImageDefaults = ImageDefaultsImpl

// Components - List
val ListDefaults = ListDefaultsImpl

// Components - Table
val TableDefaults = TableDefaultsImpl
typealias TableColors = TableColorsImpl
typealias TableScrollBehavior = TableScrollBehaviorImpl

// Components - Carousel
val CarouselDefaults = CarouselDefaultsImpl

// Components - Form
val FormDefaults = FormDefaultsImpl
typealias FormLayout = FormLayoutImpl
typealias FormLabelPosition = FormLabelPositionImpl


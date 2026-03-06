@file:Suppress("unused")

package xyz.junerver.compose.palette

import androidx.compose.runtime.Composable
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
import xyz.junerver.compose.palette.components.menu.MenuItem as MenuItemImpl
import xyz.junerver.compose.palette.components.menu.MenuDefaults as MenuDefaultsImpl
import xyz.junerver.compose.palette.components.menu.PMenu as PMenuImpl
import xyz.junerver.compose.palette.components.tabs.TabItem as TabItemImpl
import xyz.junerver.compose.palette.components.tabs.TabsDefaults as TabsDefaultsImpl
import xyz.junerver.compose.palette.components.tabs.PTabs as PTabsImpl
import xyz.junerver.compose.palette.components.breadcrumb.BreadcrumbItem as BreadcrumbItemImpl
import xyz.junerver.compose.palette.components.breadcrumb.BreadcrumbDefaults as BreadcrumbDefaultsImpl
import xyz.junerver.compose.palette.components.breadcrumb.PBreadcrumb as PBreadcrumbImpl
import xyz.junerver.compose.palette.components.steps.StepItem as StepItemImpl
import xyz.junerver.compose.palette.components.steps.StepsDefaults as StepsDefaultsImpl
import xyz.junerver.compose.palette.components.steps.PSteps as PStepsImpl
import xyz.junerver.compose.palette.components.datepicker.DatePickerDefaults as DatePickerDefaultsImpl
import xyz.junerver.compose.palette.components.datepicker.PDatePicker as PDatePickerImpl
import xyz.junerver.compose.palette.components.timepicker.TimePickerDefaults as TimePickerDefaultsImpl
import xyz.junerver.compose.palette.components.timepicker.PTimePicker as PTimePickerImpl
import xyz.junerver.compose.palette.components.upload.UploadFile as UploadFileImpl
import xyz.junerver.compose.palette.components.upload.UploadDefaults as UploadDefaultsImpl
import xyz.junerver.compose.palette.components.upload.PUpload as PUploadImpl
import xyz.junerver.compose.palette.components.message.MessageDefaults as MessageDefaultsImpl
import xyz.junerver.compose.palette.components.message.MessageState as MessageStateImpl
import xyz.junerver.compose.palette.components.message.MessageType as MessageTypeImpl
import xyz.junerver.compose.palette.components.message.PMessage as PMessageImpl
import xyz.junerver.compose.palette.components.message.rememberMessageState as rememberMessageStateImpl
import xyz.junerver.compose.palette.components.notification.NotificationDefaults as NotificationDefaultsImpl
import xyz.junerver.compose.palette.components.notification.NotificationState as NotificationStateImpl
import xyz.junerver.compose.palette.components.notification.PNotification as PNotificationImpl
import xyz.junerver.compose.palette.components.notification.rememberNotificationState as rememberNotificationStateImpl
import xyz.junerver.compose.palette.components.tooltip.PTooltip as PTooltipImpl
import xyz.junerver.compose.palette.components.tooltip.TooltipDefaults as TooltipDefaultsImpl
import xyz.junerver.compose.palette.components.popover.PPopover as PPopoverImpl
import xyz.junerver.compose.palette.components.popover.PopoverDefaults as PopoverDefaultsImpl
import xyz.junerver.compose.palette.components.drawer.PDrawer as PDrawerImpl
import xyz.junerver.compose.palette.components.drawer.DrawerDefaults as DrawerDefaultsImpl
import xyz.junerver.compose.palette.components.drawer.DrawerPlacement as DrawerPlacementImpl
import xyz.junerver.compose.palette.components.screen.LocalPlatformActivity as LocalPlatformActivityImpl
import xyz.junerver.compose.palette.components.screen.PlatformActivity as PlatformActivityImpl
import xyz.junerver.compose.palette.components.screen.ScreenColors as ScreenColorsImpl
import xyz.junerver.compose.palette.components.screen.ScreenDefaults as ScreenDefaultsImpl
import xyz.junerver.compose.palette.components.select.PSelect as PSelectImpl
import xyz.junerver.compose.palette.components.select.SelectColors as SelectColorsImpl
import xyz.junerver.compose.palette.components.select.SelectDefaults as SelectDefaultsImpl
import xyz.junerver.compose.palette.components.select.SelectOption as SelectOptionImpl
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
import xyz.junerver.compose.palette.components.datagrid.DataGridColumn as DataGridColumnImpl
import xyz.junerver.compose.palette.components.datagrid.DataGridDefaults as DataGridDefaultsImpl
import xyz.junerver.compose.palette.components.datagrid.PDataGrid as PDataGridImpl
import xyz.junerver.compose.palette.components.datetimerange.DateTimeRange as DateTimeRangeImpl
import xyz.junerver.compose.palette.components.datetimerange.DateTimeRangeDefaults as DateTimeRangeDefaultsImpl
import xyz.junerver.compose.palette.components.datetimerange.PDateTimeRange as PDateTimeRangeImpl
import xyz.junerver.compose.palette.components.commandpalette.CommandAction as CommandActionImpl
import xyz.junerver.compose.palette.components.commandpalette.CommandPaletteDefaults as CommandPaletteDefaultsImpl
import xyz.junerver.compose.palette.components.commandpalette.PCommandPalette as PCommandPaletteImpl
import xyz.junerver.compose.palette.components.tour.PTour as PTourImpl
import xyz.junerver.compose.palette.components.tour.TourDefaults as TourDefaultsImpl
import xyz.junerver.compose.palette.components.tour.TourStep as TourStepImpl
import xyz.junerver.compose.palette.components.sortable.PSortable as PSortableImpl
import xyz.junerver.compose.palette.components.sortable.SortableDefaults as SortableDefaultsImpl
import xyz.junerver.compose.palette.components.sortable.SortableItem as SortableItemImpl
import xyz.junerver.compose.palette.components.virtuallist.PVirtualList as PVirtualListImpl
import xyz.junerver.compose.palette.components.virtuallist.VirtualListDefaults as VirtualListDefaultsImpl
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

// Components - Select
@Composable
fun <T> PSelect(
    options: List<SelectOptionImpl<T>>,
    value: T?,
    onValueChange: (T) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    enabled: Boolean = true,
    size: ComponentSizeImpl = ComponentSizeImpl.Medium,
    status: ComponentStatusImpl = ComponentStatusImpl.Default,
    placeholder: String = "",
    searchable: Boolean = false,
    searchPlaceholder: String = "",
    colors: SelectColorsImpl = SelectDefaultsImpl.colors(),
    optionContent: (@Composable (SelectOptionImpl<T>, Boolean) -> Unit)? = null,
) = PSelectImpl(
    options = options,
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    enabled = enabled,
    size = size,
    status = status,
    placeholder = placeholder,
    searchable = searchable,
    searchPlaceholder = searchPlaceholder,
    colors = colors,
    optionContent = optionContent
)
val SelectDefaults = SelectDefaultsImpl
typealias SelectColors = SelectColorsImpl
typealias SelectOption<T> = SelectOptionImpl<T>

// Components - Message
val PMessage = ::PMessageImpl
val MessageDefaults = MessageDefaultsImpl
typealias MessageType = MessageTypeImpl
typealias MessageState = MessageStateImpl
val rememberMessageState = ::rememberMessageStateImpl

// Components - Notification
val PNotification = ::PNotificationImpl
val NotificationDefaults = NotificationDefaultsImpl
typealias NotificationState = NotificationStateImpl
val rememberNotificationState = ::rememberNotificationStateImpl

// Components - Overlay
val PTooltip = ::PTooltipImpl
val TooltipDefaults = TooltipDefaultsImpl
val PPopover = ::PPopoverImpl
val PopoverDefaults = PopoverDefaultsImpl
val PDrawer = ::PDrawerImpl
val DrawerDefaults = DrawerDefaultsImpl
typealias DrawerPlacement = DrawerPlacementImpl

// Components - Navigation
val PMenu = ::PMenuImpl
val MenuDefaults = MenuDefaultsImpl
typealias MenuItem = MenuItemImpl
val PTabs = ::PTabsImpl
val TabsDefaults = TabsDefaultsImpl
typealias TabItem = TabItemImpl
val PBreadcrumb = ::PBreadcrumbImpl
val BreadcrumbDefaults = BreadcrumbDefaultsImpl
typealias BreadcrumbItem = BreadcrumbItemImpl
val PSteps = ::PStepsImpl
val StepsDefaults = StepsDefaultsImpl
typealias StepItem = StepItemImpl

// Components - Data Entry
val PDatePicker = ::PDatePickerImpl
val DatePickerDefaults = DatePickerDefaultsImpl
val PTimePicker = ::PTimePickerImpl
val TimePickerDefaults = TimePickerDefaultsImpl
val PUpload = ::PUploadImpl
val UploadDefaults = UploadDefaultsImpl
typealias UploadFile = UploadFileImpl

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

// Components - DataGrid
@Composable
fun <T> PDataGrid(
    rows: List<T>,
    columns: List<DataGridColumnImpl<T>>,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
) = PDataGridImpl(
    rows = rows,
    columns = columns,
    modifier = modifier,
)
val DataGridDefaults = DataGridDefaultsImpl
typealias DataGridColumn<T> = DataGridColumnImpl<T>

// Components - Carousel
val CarouselDefaults = CarouselDefaultsImpl

// Components - Form
val FormDefaults = FormDefaultsImpl
typealias FormLayout = FormLayoutImpl
typealias FormLabelPosition = FormLabelPositionImpl

// Components - DateTimeRange
val PDateTimeRange = ::PDateTimeRangeImpl
val DateTimeRangeDefaults = DateTimeRangeDefaultsImpl
typealias DateTimeRange = DateTimeRangeImpl

// Components - CommandPalette
val PCommandPalette = ::PCommandPaletteImpl
val CommandPaletteDefaults = CommandPaletteDefaultsImpl
typealias CommandAction = CommandActionImpl

// Components - Tour
val PTour = ::PTourImpl
val TourDefaults = TourDefaultsImpl
typealias TourStep = TourStepImpl

// Components - Sortable
@Composable
fun <T> PSortable(
    items: List<SortableItemImpl<T>>,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    itemText: (SortableItemImpl<T>) -> String = { it.id },
) = PSortableImpl(
    items = items,
    modifier = modifier,
    itemText = itemText,
)
val SortableDefaults = SortableDefaultsImpl
typealias SortableItem<T> = SortableItemImpl<T>

// Components - VirtualList
@Composable
fun <T> PVirtualList(
    items: List<T>,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    itemText: (T) -> String = { it.toString() },
    key: ((T) -> Any)? = null,
) = PVirtualListImpl(
    items = items,
    modifier = modifier,
    itemText = itemText,
    key = key,
)
val VirtualListDefaults = VirtualListDefaultsImpl

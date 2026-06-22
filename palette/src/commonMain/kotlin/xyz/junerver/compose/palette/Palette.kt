@file:Suppress("unused")

package xyz.junerver.compose.palette

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import xyz.junerver.compose.palette.code.HighlightedCode
import xyz.junerver.compose.palette.code.PaletteCodeHighlighter
import xyz.junerver.compose.palette.components.avatar.AvatarDefaults as AvatarDefaultsImpl
import xyz.junerver.compose.palette.components.avatar.AvatarShape as AvatarShapeImpl
import xyz.junerver.compose.palette.components.avatar.AvatarSize as AvatarSizeImpl
import xyz.junerver.compose.palette.components.badge.BadgeDefaults as BadgeDefaultsImpl
import xyz.junerver.compose.palette.components.bottomnavigation.BottomNavigationColors as BottomNavigationColorsImpl
import xyz.junerver.compose.palette.components.bottomnavigation.BottomNavigationDefaults as BottomNavigationDefaultsImpl
import xyz.junerver.compose.palette.components.bottomnavigation.BottomNavigationItem as BottomNavigationItemImpl
import xyz.junerver.compose.palette.components.bottomnavigation.PBottomNavigation as PBottomNavigationImpl
import xyz.junerver.compose.palette.components.button.ButtonColors as ButtonColorsImpl
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
import xyz.junerver.compose.palette.components.infinitescroll.InfiniteScrollDefaults as InfiniteScrollDefaultsImpl
import xyz.junerver.compose.palette.components.infinitescroll.PInfiniteScroll as PInfiniteScrollImpl
import xyz.junerver.compose.palette.components.list.ListDefaults as ListDefaultsImpl
import xyz.junerver.compose.palette.components.loading.LoadingDefaults as LoadingDefaultsImpl
import xyz.junerver.compose.palette.components.pagination.PaginationColors as PaginationColorsImpl
import xyz.junerver.compose.palette.components.pagination.PaginationDefaults as PaginationDefaultsImpl
import xyz.junerver.compose.palette.components.progress.DashboardProgressDefaults as DashboardProgressDefaultsImpl
import xyz.junerver.compose.palette.components.progress.PDashboardProgress as PDashboardProgressImpl
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
import xyz.junerver.compose.palette.components.popup.PPopup as PPopupImpl
import xyz.junerver.compose.palette.components.popup.PopupDefaults as PopupDefaultsImpl
import xyz.junerver.compose.palette.components.drawer.PDrawer as PDrawerImpl
import xyz.junerver.compose.palette.components.drawer.DrawerDefaults as DrawerDefaultsImpl
import xyz.junerver.compose.palette.components.drawer.DrawerPlacement as DrawerPlacementImpl
import xyz.junerver.compose.palette.components.screen.LocalPlatformActivity as LocalPlatformActivityImpl
import xyz.junerver.compose.palette.components.screen.PlatformActivity as PlatformActivityImpl
import xyz.junerver.compose.palette.components.screen.ScreenColors as ScreenColorsImpl
import xyz.junerver.compose.palette.components.screen.ScreenDefaults as ScreenDefaultsImpl
import xyz.junerver.compose.palette.components.scaffold.PScaffold as PScaffoldImpl
import xyz.junerver.compose.palette.components.scaffold.ScaffoldColors as ScaffoldColorsImpl
import xyz.junerver.compose.palette.components.scaffold.ScaffoldDefaults as ScaffoldDefaultsImpl
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
import xyz.junerver.compose.palette.components.searchbar.PSearchBar as PSearchBarImpl
import xyz.junerver.compose.palette.components.searchbar.SearchBarDefaults as SearchBarDefaultsImpl
import xyz.junerver.compose.palette.components.backtop.BacktopDefaults as BacktopDefaultsImpl
import xyz.junerver.compose.palette.components.backtop.PBacktop as PBacktopImpl
import xyz.junerver.compose.palette.components.actionsheet.PActionSheet as PActionSheetImpl
import xyz.junerver.compose.palette.components.actionsheet.ActionSheetDefaults as ActionSheetDefaultsImpl
import xyz.junerver.compose.palette.components.actionsheet.ActionSheetItem as ActionSheetItemImpl
import xyz.junerver.compose.palette.components.actionsheet.ActionSheetState as ActionSheetStateImpl
import xyz.junerver.compose.palette.components.actionsheet.rememberActionSheetState as rememberActionSheetStateImpl
import xyz.junerver.compose.palette.components.autocomplete.AutocompleteDefaults as AutocompleteDefaultsImpl
import xyz.junerver.compose.palette.components.autocomplete.AutocompleteOption as AutocompleteOptionImpl
import xyz.junerver.compose.palette.components.autocomplete.PAutocomplete as PAutocompleteImpl
import xyz.junerver.compose.palette.components.affix.AffixDefaults as AffixDefaultsImpl
import xyz.junerver.compose.palette.components.affix.AffixPosition as AffixPositionImpl
import xyz.junerver.compose.palette.components.affix.PAffix as PAffixImpl
import xyz.junerver.compose.palette.components.calendar.CalendarDefaults as CalendarDefaultsImpl
import xyz.junerver.compose.palette.components.calendar.PCalendar as PCalendarImpl
import xyz.junerver.compose.palette.components.inputnumber.InputNumberDefaults as InputNumberDefaultsImpl
import xyz.junerver.compose.palette.components.inputnumber.PInputNumber as PInputNumberImpl
import xyz.junerver.compose.palette.components.inputotp.InputOTPDefaults as InputOTPDefaultsImpl
import xyz.junerver.compose.palette.components.inputotp.PInputOTP as PInputOTPImpl
import xyz.junerver.compose.palette.components.cascader.CascaderColors as CascaderColorsImpl
import xyz.junerver.compose.palette.components.cascader.CascaderDefaults as CascaderDefaultsImpl
import xyz.junerver.compose.palette.components.cascader.CascaderExpandTrigger as CascaderExpandTriggerImpl
import xyz.junerver.compose.palette.components.cascader.CascaderOption as CascaderOptionImpl
import xyz.junerver.compose.palette.components.cascader.PCascader as PCascaderImpl
import xyz.junerver.compose.palette.components.cascaderpanel.CascaderPanelDefaults as CascaderPanelDefaultsImpl
import xyz.junerver.compose.palette.components.cascaderpanel.PCascaderPanel as PCascaderPanelImpl
import xyz.junerver.compose.palette.components.treeselect.PTreeSelect as PTreeSelectImpl
import xyz.junerver.compose.palette.components.treeselect.TreeSelectColors as TreeSelectColorsImpl
import xyz.junerver.compose.palette.components.treeselect.TreeSelectDefaults as TreeSelectDefaultsImpl
import xyz.junerver.compose.palette.components.treeselect.TreeSelectNode as TreeSelectNodeImpl
import xyz.junerver.compose.palette.components.alert.AlertDefaults as AlertDefaultsImpl
import xyz.junerver.compose.palette.components.alert.AlertType as AlertTypeImpl
import xyz.junerver.compose.palette.components.alert.PAlert as PAlertImpl
import xyz.junerver.compose.palette.components.contextmenu.PContextMenu as PContextMenuImpl
import xyz.junerver.compose.palette.components.contextmenu.ContextMenuDefaults as ContextMenuDefaultsImpl
import xyz.junerver.compose.palette.components.contextmenu.ContextMenuState as ContextMenuStateImpl
import xyz.junerver.compose.palette.components.contextmenu.ContextMenuItem as ContextMenuItemImpl
import xyz.junerver.compose.palette.components.contextmenu.rememberContextMenuState as rememberContextMenuStateImpl
import xyz.junerver.compose.palette.components.popconfirm.PPopconfirm as PPopconfirmImpl
import xyz.junerver.compose.palette.components.popconfirm.PopconfirmDefaults as PopconfirmDefaultsImpl
import xyz.junerver.compose.palette.components.transfer.PTransfer as PTransferImpl
import xyz.junerver.compose.palette.components.transfer.TransferDefaults as TransferDefaultsImpl
import xyz.junerver.compose.palette.components.transfer.TransferItem as TransferItemImpl
import xyz.junerver.compose.palette.components.result.PResult as PResultImpl
import xyz.junerver.compose.palette.components.result.ResultDefaults as ResultDefaultsImpl
import xyz.junerver.compose.palette.components.result.ResultStatus as ResultStatusImpl
import xyz.junerver.compose.palette.components.segmented.PSegmented as PSegmentedImpl
import xyz.junerver.compose.palette.components.segmented.SegmentedDefaults as SegmentedDefaultsImpl
import xyz.junerver.compose.palette.components.segmented.SegmentedOption as SegmentedOptionImpl
import xyz.junerver.compose.palette.components.colorpicker.ColorPickerDefaults as ColorPickerDefaultsImpl
import xyz.junerver.compose.palette.components.colorpicker.PColorPicker as PColorPickerImpl
import xyz.junerver.compose.palette.components.code.CodeBlockColors as CodeBlockColorsImpl
import xyz.junerver.compose.palette.components.code.CodeBlockDefaults as CodeBlockDefaultsImpl
import xyz.junerver.compose.palette.components.code.PCodeBlock as PCodeBlockImpl
import xyz.junerver.compose.palette.components.markdown.MarkdownDefaults as MarkdownDefaultsImpl
import xyz.junerver.compose.palette.components.markdown.MarkdownEditorMode as MarkdownEditorModeImpl
import xyz.junerver.compose.palette.components.markdown.PMarkdownEditor as PMarkdownEditorImpl
import xyz.junerver.compose.palette.components.markdown.PMarkdownViewer as PMarkdownViewerImpl
import xyz.junerver.compose.palette.components.mermaid.MermaidColors as MermaidColorsImpl
import xyz.junerver.compose.palette.components.mermaid.MermaidDefaults as MermaidDefaultsImpl
import xyz.junerver.compose.palette.components.mermaid.PMermaidDiagram as PMermaidDiagramImpl
import xyz.junerver.compose.palette.components.barcode.BarcodeDefaults as BarcodeDefaultsImpl
import xyz.junerver.compose.palette.components.barcode.PBarcode as PBarcodeImpl
import xyz.junerver.compose.palette.components.barcode.PaletteBarcodeType as PaletteBarcodeTypeImpl
import xyz.junerver.compose.palette.components.qrcode.PQRCode as PQRCodeImpl
import xyz.junerver.compose.palette.components.qrcode.QRCodeDefaults as QRCodeDefaultsImpl
import xyz.junerver.compose.palette.components.watermark.PWatermark as PWatermarkImpl
import xyz.junerver.compose.palette.components.watermark.WatermarkDefaults as WatermarkDefaultsImpl
import xyz.junerver.compose.palette.components.toggle.PToggle as PToggleImpl
import xyz.junerver.compose.palette.components.toggle.PToggleGroup as PToggleGroupImpl
import xyz.junerver.compose.palette.components.toggle.ToggleDefaults as ToggleDefaultsImpl
import xyz.junerver.compose.palette.components.toggle.ToggleVariant as ToggleVariantImpl
import xyz.junerver.compose.palette.components.toggle.ToggleItem as ToggleItemImpl
import xyz.junerver.compose.palette.components.mentions.PMentions as PMentionsImpl
import xyz.junerver.compose.palette.components.mentions.MentionsDefaults as MentionsDefaultsImpl
import xyz.junerver.compose.palette.components.mentions.MentionsOption as MentionsOptionImpl
import xyz.junerver.compose.palette.components.pageheader.PPageHeader as PPageHeaderImpl
import xyz.junerver.compose.palette.components.pageheader.PageHeaderDefaults as PageHeaderDefaultsImpl
import xyz.junerver.compose.palette.components.floatbutton.FloatButtonDefaults as FloatButtonDefaultsImpl
import xyz.junerver.compose.palette.components.floatbutton.FloatButtonShape as FloatButtonShapeImpl
import xyz.junerver.compose.palette.components.floatbutton.PFloatButton as PFloatButtonImpl
import xyz.junerver.compose.palette.components.grid.GridDefaults as GridDefaultsImpl
import xyz.junerver.compose.palette.components.grid.PRow as PRowImpl
import xyz.junerver.compose.palette.components.space.PSpace as PSpaceImpl
import xyz.junerver.compose.palette.components.space.SpaceDefaults as SpaceDefaultsImpl
import xyz.junerver.compose.palette.components.space.SpaceDirection as SpaceDirectionImpl
import xyz.junerver.compose.palette.core.spec.ComponentInteraction as ComponentInteractionImpl
import xyz.junerver.compose.palette.core.spec.ComponentSize as ComponentSizeImpl
import xyz.junerver.compose.palette.core.spec.ComponentState as ComponentStateImpl
import xyz.junerver.compose.palette.core.spec.ComponentStatus as ComponentStatusImpl
import xyz.junerver.compose.palette.core.spec.rememberComponentInteraction as rememberComponentInteractionImpl
import xyz.junerver.compose.palette.core.i18n.PaletteStrings as PaletteStringsImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteColors as LocalPaletteColorsImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteComponentThemes as LocalPaletteComponentThemesImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteControl as LocalPaletteControlImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteDarkTheme as LocalPaletteDarkThemeImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteElevation as LocalPaletteElevationImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteMotion as LocalPaletteMotionImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteOpacity as LocalPaletteOpacityImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteShapes as LocalPaletteShapesImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteSpacing as LocalPaletteSpacingImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteStrings as LocalPaletteStringsImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteTypography as LocalPaletteTypographyImpl
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme as PaletteMaterialThemeImpl
import xyz.junerver.compose.palette.core.theme.PaletteTheme as PaletteThemeImpl
import xyz.junerver.compose.palette.core.tokens.PaletteBorderContainerTokens as PaletteBorderContainerTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteActionSheetTokens as PaletteActionSheetTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteAppBarTokens as PaletteAppBarTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteButtonSizeTokens as PaletteButtonSizeTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteButtonTokens as PaletteButtonTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteCardTokens as PaletteCardTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteCheckboxSizeTokens as PaletteCheckboxSizeTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteCheckboxTokens as PaletteCheckboxTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteComponentThemes as PaletteComponentThemesImpl
import xyz.junerver.compose.palette.core.tokens.PaletteControlSizeTokens as PaletteControlSizeTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteControlTokens as PaletteControlTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteColors as PaletteColorsImpl
import xyz.junerver.compose.palette.core.tokens.PaletteDataGridTokens as PaletteDataGridTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteDataDisplayTokens as PaletteDataDisplayTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteDataEntryTokens as PaletteDataEntryTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteDateTimeTokens as PaletteDateTimeTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteDialogTokens as PaletteDialogTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteDrawerTokens as PaletteDrawerTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteElevation as PaletteElevationImpl
import xyz.junerver.compose.palette.core.tokens.PaletteFeedbackDisplayTokens as PaletteFeedbackDisplayTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteFloatingActionTokens as PaletteFloatingActionTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteFloatingLayerTokens as PaletteFloatingLayerTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteFormTokens as PaletteFormTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteInputTokens as PaletteInputTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteLayoutTokens as PaletteLayoutTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteMediaTokens as PaletteMediaTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteMessageTokens as PaletteMessageTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteMotion as PaletteMotionImpl
import xyz.junerver.compose.palette.core.tokens.PaletteNavigationMenuTokens as PaletteNavigationMenuTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteNotificationTokens as PaletteNotificationTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteOpacity as PaletteOpacityImpl
import xyz.junerver.compose.palette.core.tokens.PalettePopupTokens as PalettePopupTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteProgressTokens as PaletteProgressTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteRadioSizeTokens as PaletteRadioSizeTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteRadioTokens as PaletteRadioTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteSemanticColors as PaletteSemanticColorsImpl
import xyz.junerver.compose.palette.core.tokens.PaletteShapes as PaletteShapesImpl
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing as PaletteSpacingImpl
import xyz.junerver.compose.palette.core.tokens.PaletteScreenTokens as PaletteScreenTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteSelectionControlTokens as PaletteSelectionControlTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteSelectTokens as PaletteSelectTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteSwitchTokens as PaletteSwitchTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteTableTokens as PaletteTableTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteTagSizeTokens as PaletteTagSizeTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteTagTokens as PaletteTagTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteTextFieldTokens as PaletteTextFieldTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteToastTokens as PaletteToastTokensImpl
import xyz.junerver.compose.palette.core.tokens.PaletteTypography as PaletteTypographyImpl
import xyz.junerver.compose.palette.core.tokens.PaletteUtilityTokens as PaletteUtilityTokensImpl
import xyz.junerver.compose.palette.core.util.PaletteDefaults as PaletteDefaultsImpl
import xyz.junerver.compose.palette.foundation.border.BorderContainerDefaults as BorderContainerDefaultsImpl

// Core - Tokens
typealias PaletteColors = PaletteColorsImpl
typealias PaletteSemanticColors = PaletteSemanticColorsImpl
typealias PaletteShapes = PaletteShapesImpl
typealias PaletteSpacing = PaletteSpacingImpl
typealias PaletteTypography = PaletteTypographyImpl
typealias PaletteOpacity = PaletteOpacityImpl
typealias PaletteMotion = PaletteMotionImpl
typealias PaletteElevation = PaletteElevationImpl
typealias PaletteControlSizeTokens = PaletteControlSizeTokensImpl
typealias PaletteControlTokens = PaletteControlTokensImpl
typealias PaletteComponentThemes = PaletteComponentThemesImpl
typealias PaletteBorderContainerTokens = PaletteBorderContainerTokensImpl
typealias PaletteButtonSizeTokens = PaletteButtonSizeTokensImpl
typealias PaletteButtonTokens = PaletteButtonTokensImpl
typealias PaletteCheckboxSizeTokens = PaletteCheckboxSizeTokensImpl
typealias PaletteCheckboxTokens = PaletteCheckboxTokensImpl
typealias PaletteRadioSizeTokens = PaletteRadioSizeTokensImpl
typealias PaletteRadioTokens = PaletteRadioTokensImpl
typealias PaletteSwitchTokens = PaletteSwitchTokensImpl
typealias PaletteSelectionControlTokens = PaletteSelectionControlTokensImpl
typealias PaletteFormTokens = PaletteFormTokensImpl
typealias PaletteTextFieldTokens = PaletteTextFieldTokensImpl
typealias PaletteSelectTokens = PaletteSelectTokensImpl
typealias PaletteDateTimeTokens = PaletteDateTimeTokensImpl
typealias PaletteInputTokens = PaletteInputTokensImpl
typealias PaletteCardTokens = PaletteCardTokensImpl
typealias PaletteTableTokens = PaletteTableTokensImpl
typealias PaletteDataGridTokens = PaletteDataGridTokensImpl
typealias PaletteDataDisplayTokens = PaletteDataDisplayTokensImpl
typealias PaletteDataEntryTokens = PaletteDataEntryTokensImpl
typealias PaletteNavigationMenuTokens = PaletteNavigationMenuTokensImpl
typealias PaletteAppBarTokens = PaletteAppBarTokensImpl
typealias PaletteFloatingLayerTokens = PaletteFloatingLayerTokensImpl
typealias PaletteDialogTokens = PaletteDialogTokensImpl
typealias PaletteDrawerTokens = PaletteDrawerTokensImpl
typealias PalettePopupTokens = PalettePopupTokensImpl
typealias PaletteActionSheetTokens = PaletteActionSheetTokensImpl
typealias PaletteMessageTokens = PaletteMessageTokensImpl
typealias PaletteNotificationTokens = PaletteNotificationTokensImpl
typealias PaletteToastTokens = PaletteToastTokensImpl
typealias PaletteTagSizeTokens = PaletteTagSizeTokensImpl
typealias PaletteTagTokens = PaletteTagTokensImpl
typealias PaletteFeedbackDisplayTokens = PaletteFeedbackDisplayTokensImpl
typealias PaletteProgressTokens = PaletteProgressTokensImpl
typealias PaletteMediaTokens = PaletteMediaTokensImpl
typealias PaletteUtilityTokens = PaletteUtilityTokensImpl
typealias PaletteLayoutTokens = PaletteLayoutTokensImpl
typealias PaletteFloatingActionTokens = PaletteFloatingActionTokensImpl
typealias PaletteScreenTokens = PaletteScreenTokensImpl
typealias PaletteStrings = PaletteStringsImpl
// Core - Theme
val LocalPaletteColors = LocalPaletteColorsImpl
val LocalPaletteShapes = LocalPaletteShapesImpl
val LocalPaletteSpacing = LocalPaletteSpacingImpl
val LocalPaletteStrings = LocalPaletteStringsImpl
val LocalPaletteTypography = LocalPaletteTypographyImpl
val LocalPaletteOpacity = LocalPaletteOpacityImpl
val LocalPaletteMotion = LocalPaletteMotionImpl
val LocalPaletteElevation = LocalPaletteElevationImpl
val LocalPaletteControl = LocalPaletteControlImpl
val LocalPaletteComponentThemes = LocalPaletteComponentThemesImpl
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
val PScaffold = ::PScaffoldImpl
val ScaffoldDefaults = ScaffoldDefaultsImpl
typealias ScaffoldColors = ScaffoldColorsImpl

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
val PPopup = ::PPopupImpl
val PopupDefaults = PopupDefaultsImpl

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
val PBottomNavigation = ::PBottomNavigationImpl
val BottomNavigationDefaults = BottomNavigationDefaultsImpl
typealias BottomNavigationColors = BottomNavigationColorsImpl
typealias BottomNavigationItem = BottomNavigationItemImpl

// Components - Data Entry
val PDatePicker = ::PDatePickerImpl
val DatePickerDefaults = DatePickerDefaultsImpl
val PTimePicker = ::PTimePickerImpl
val TimePickerDefaults = TimePickerDefaultsImpl
val PUpload = ::PUploadImpl
val UploadDefaults = UploadDefaultsImpl
typealias UploadFile = UploadFileImpl
val PInputNumber = ::PInputNumberImpl
val InputNumberDefaults = InputNumberDefaultsImpl
val PInputOTP = ::PInputOTPImpl
val InputOTPDefaults = InputOTPDefaultsImpl

// Components - Calendar
val PCalendar = ::PCalendarImpl
val CalendarDefaults = CalendarDefaultsImpl

// Components - Loading
val LoadingDefaults = LoadingDefaultsImpl

// Components - Progress
val ProgressDefaults = ProgressDefaultsImpl
val PDashboardProgress = ::PDashboardProgressImpl
val DashboardProgressDefaults = DashboardProgressDefaultsImpl

// Components - Button
typealias ButtonColors = ButtonColorsImpl
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
typealias AvatarShape = AvatarShapeImpl
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

// Components - InfiniteScroll
val PInfiniteScroll = ::PInfiniteScrollImpl
val InfiniteScrollDefaults = InfiniteScrollDefaultsImpl

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

// Components - SearchBar
val PSearchBar = ::PSearchBarImpl
val SearchBarDefaults = SearchBarDefaultsImpl

// Components - ContextMenu
val PContextMenu = ::PContextMenuImpl
val ContextMenuDefaults = ContextMenuDefaultsImpl
typealias ContextMenuState = ContextMenuStateImpl
typealias ContextMenuItem = ContextMenuItemImpl
val rememberContextMenuState = ::rememberContextMenuStateImpl

// Components - ActionSheet
val PActionSheet = ::PActionSheetImpl
val ActionSheetDefaults = ActionSheetDefaultsImpl
typealias ActionSheetItem = ActionSheetItemImpl
typealias ActionSheetState = ActionSheetStateImpl
val rememberActionSheetState = ::rememberActionSheetStateImpl

// Components - Autocomplete
@Composable
fun PAutocomplete(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<AutocompleteOptionImpl>,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    disabled: Boolean = false,
    size: ComponentSizeImpl = ComponentSizeImpl.Medium,
    status: ComponentStatusImpl = ComponentStatusImpl.Default,
    onSelect: ((AutocompleteOptionImpl) -> Unit)? = null,
    filterOption: ((String, AutocompleteOptionImpl) -> Boolean)? = null,
) = PAutocompleteImpl(
    value = value,
    onValueChange = onValueChange,
    options = options,
    modifier = modifier,
    placeholder = placeholder,
    disabled = disabled,
    size = size,
    status = status,
    onSelect = onSelect,
    filterOption = filterOption,
)
val AutocompleteDefaults = AutocompleteDefaultsImpl
typealias AutocompleteOption = AutocompleteOptionImpl

// Components - Affix
val PAffix = ::PAffixImpl
val AffixDefaults = AffixDefaultsImpl
typealias AffixPosition = AffixPositionImpl

// Components - Alert
val PAlert = ::PAlertImpl
val AlertDefaults = AlertDefaultsImpl
typealias AlertType = AlertTypeImpl

// Components - Cascader
val PCascader = ::PCascaderImpl
val CascaderDefaults = CascaderDefaultsImpl
typealias CascaderOption = CascaderOptionImpl
typealias CascaderExpandTrigger = CascaderExpandTriggerImpl
typealias CascaderColors = CascaderColorsImpl

// Components - CascaderPanel
val PCascaderPanel = ::PCascaderPanelImpl
val CascaderPanelDefaults = CascaderPanelDefaultsImpl

// Components - TreeSelect
@Composable
fun PTreeSelect(
    value: String?,
    onValueChange: (String?) -> Unit,
    nodes: List<TreeSelectNodeImpl>,
    modifier: Modifier = Modifier,
    placeholder: String = "请选择",
    disabled: Boolean = false,
    size: ComponentSizeImpl = ComponentSizeImpl.Medium,
    showSearch: Boolean = false,
    searchPlaceholder: String = "",
    colors: TreeSelectColorsImpl = TreeSelectDefaultsImpl.colors(),
) = PTreeSelectImpl(
    value = value,
    onValueChange = onValueChange,
    nodes = nodes,
    modifier = modifier,
    placeholder = placeholder,
    disabled = disabled,
    size = size,
    showSearch = showSearch,
    searchPlaceholder = searchPlaceholder,
    colors = colors,
)
val TreeSelectDefaults = TreeSelectDefaultsImpl
typealias TreeSelectColors = TreeSelectColorsImpl
typealias TreeSelectNode = TreeSelectNodeImpl

// Components - Popconfirm
val PPopconfirm = ::PPopconfirmImpl
val PopconfirmDefaults = PopconfirmDefaultsImpl

// Components - Transfer
val PTransfer = ::PTransferImpl
val TransferDefaults = TransferDefaultsImpl
typealias TransferItem = TransferItemImpl

// Components - Result
val PResult = ::PResultImpl
val ResultDefaults = ResultDefaultsImpl
typealias ResultStatus = ResultStatusImpl

// Components - Segmented
val PSegmented = ::PSegmentedImpl
val SegmentedDefaults = SegmentedDefaultsImpl
typealias SegmentedOption = SegmentedOptionImpl

// Components - Space
val PSpace = ::PSpaceImpl
val SpaceDefaults = SpaceDefaultsImpl
typealias SpaceDirection = SpaceDirectionImpl

// Components - Grid
val PRow = ::PRowImpl
val GridDefaults = GridDefaultsImpl

// Components - QRCode
val PQRCode = ::PQRCodeImpl
val QRCodeDefaults = QRCodeDefaultsImpl

// Components - Code
@Composable
fun PCodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    language: String = "kotlin",
    showCopyAction: Boolean = true,
    showLineNumbers: Boolean = false,
    highlightedLines: Set<Int> = emptySet(),
    title: String? = null,
    firstLineNumber: Int = 1,
    colors: CodeBlockColors = CodeBlockDefaults.colors(),
    highlightedCode: HighlightedCode = PaletteCodeHighlighter.highlight(code.trimIndent(), language),
) {
    PCodeBlockImpl(
        code = code,
        modifier = modifier,
        language = language,
        showCopyAction = showCopyAction,
        showLineNumbers = showLineNumbers,
        highlightedLines = highlightedLines,
        title = title,
        firstLineNumber = firstLineNumber,
        colors = colors,
        highlightedCode = highlightedCode,
    )
}

val CodeBlockDefaults = CodeBlockDefaultsImpl
typealias CodeBlockColors = CodeBlockColorsImpl

// Components - Mermaid
val PMermaidDiagram = ::PMermaidDiagramImpl
val MermaidDefaults = MermaidDefaultsImpl
typealias MermaidColors = MermaidColorsImpl

// Components - Markdown
val PMarkdownViewer = ::PMarkdownViewerImpl
val PMarkdownEditor = ::PMarkdownEditorImpl
val MarkdownDefaults = MarkdownDefaultsImpl
typealias MarkdownEditorMode = MarkdownEditorModeImpl

// Components - Barcode
val PBarcode = ::PBarcodeImpl
val BarcodeDefaults = BarcodeDefaultsImpl
typealias PaletteBarcodeType = PaletteBarcodeTypeImpl

// Components - ColorPicker
val PColorPicker = ::PColorPickerImpl
val ColorPickerDefaults = ColorPickerDefaultsImpl

// Components - Backtop
val PBacktop = ::PBacktopImpl
val BacktopDefaults = BacktopDefaultsImpl

// Components - FloatButton
val PFloatButton = ::PFloatButtonImpl
val FloatButtonDefaults = FloatButtonDefaultsImpl
typealias FloatButtonShape = FloatButtonShapeImpl

// Components - Watermark
val PWatermark = ::PWatermarkImpl
val WatermarkDefaults = WatermarkDefaultsImpl

// Components - Toggle
val PToggle = ::PToggleImpl
val PToggleGroup = ::PToggleGroupImpl
val ToggleDefaults = ToggleDefaultsImpl
typealias ToggleVariant = ToggleVariantImpl
typealias ToggleItem = ToggleItemImpl

// Components - Mentions
@Composable
fun PMentions(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<MentionsOptionImpl>,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    disabled: Boolean = false,
    prefix: String = "@",
    onSelect: ((MentionsOptionImpl) -> Unit)? = null,
    onSearch: ((String) -> Unit)? = null,
    loading: Boolean = false,
    highlight: Boolean = false,
    highlightColor: Color = MentionsDefaultsImpl.highlightColor(),
) = PMentionsImpl(
    value = value,
    onValueChange = onValueChange,
    options = options,
    modifier = modifier,
    placeholder = placeholder,
    disabled = disabled,
    prefix = prefix,
    onSelect = onSelect,
    onSearch = onSearch,
    loading = loading,
    highlight = highlight,
    highlightColor = highlightColor,
)
val MentionsDefaults = MentionsDefaultsImpl
typealias MentionsOption = MentionsOptionImpl

// Components - PageHeader
val PPageHeader = ::PPageHeaderImpl
val PageHeaderDefaults = PageHeaderDefaultsImpl

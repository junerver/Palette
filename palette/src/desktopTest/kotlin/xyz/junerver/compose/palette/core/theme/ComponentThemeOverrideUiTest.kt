package xyz.junerver.compose.palette.core.theme

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import org.junit.Rule
import xyz.junerver.compose.palette.components.actionsheet.ActionSheetDefaults
import xyz.junerver.compose.palette.components.alert.AlertDefaults
import xyz.junerver.compose.palette.components.alert.AlertType
import xyz.junerver.compose.palette.components.autocomplete.AutocompleteDefaults
import xyz.junerver.compose.palette.components.avatar.AvatarDefaults
import xyz.junerver.compose.palette.components.avatar.AvatarSize
import xyz.junerver.compose.palette.components.backtop.BacktopDefaults
import xyz.junerver.compose.palette.components.badge.BadgeDefaults
import xyz.junerver.compose.palette.components.barcode.BarcodeDefaults
import xyz.junerver.compose.palette.components.breadcrumb.BreadcrumbDefaults
import xyz.junerver.compose.palette.components.button.ButtonDefaults
import xyz.junerver.compose.palette.components.calendar.CalendarDefaults
import xyz.junerver.compose.palette.components.card.CardDefaults
import xyz.junerver.compose.palette.components.cascader.CascaderDefaults
import xyz.junerver.compose.palette.components.cascaderpanel.CascaderPanelDefaults
import xyz.junerver.compose.palette.components.checkbox.CheckboxDefaults
import xyz.junerver.compose.palette.components.carousel.CarouselDefaults
import xyz.junerver.compose.palette.components.collapse.CollapseDefaults
import xyz.junerver.compose.palette.components.colorpicker.ColorPickerDefaults
import xyz.junerver.compose.palette.components.commandpalette.CommandPaletteDefaults
import xyz.junerver.compose.palette.components.contextmenu.ContextMenuDefaults
import xyz.junerver.compose.palette.components.datagrid.DataGridDefaults
import xyz.junerver.compose.palette.components.datepicker.DatePickerDefaults
import xyz.junerver.compose.palette.components.datetimerange.DateTimeRangeDefaults
import xyz.junerver.compose.palette.components.descriptions.DescriptionsDefaults
import xyz.junerver.compose.palette.components.dialog.DialogDefaults
import xyz.junerver.compose.palette.components.drawer.DrawerDefaults
import xyz.junerver.compose.palette.components.empty.EmptyDefaults
import xyz.junerver.compose.palette.components.affix.AffixDefaults
import xyz.junerver.compose.palette.components.floatbutton.FloatButtonDefaults
import xyz.junerver.compose.palette.components.form.FormDefaults
import xyz.junerver.compose.palette.components.grid.GridDefaults
import xyz.junerver.compose.palette.components.image.ImageDefaults
import xyz.junerver.compose.palette.components.infinitescroll.InfiniteScrollDefaults
import xyz.junerver.compose.palette.components.inputnumber.InputNumberDefaults
import xyz.junerver.compose.palette.components.inputotp.InputOTPDefaults
import xyz.junerver.compose.palette.components.list.ListDefaults
import xyz.junerver.compose.palette.components.mentions.MentionsDefaults
import xyz.junerver.compose.palette.components.message.MessageDefaults
import xyz.junerver.compose.palette.components.notification.NotificationDefaults
import xyz.junerver.compose.palette.components.pagination.PaginationDefaults
import xyz.junerver.compose.palette.components.pageheader.PageHeaderDefaults
import xyz.junerver.compose.palette.components.popconfirm.PopconfirmDefaults
import xyz.junerver.compose.palette.components.popover.PopoverDefaults
import xyz.junerver.compose.palette.components.popup.PopupDefaults
import xyz.junerver.compose.palette.components.progress.DashboardProgressDefaults
import xyz.junerver.compose.palette.components.progress.ProgressDefaults
import xyz.junerver.compose.palette.components.qrcode.QRCodeDefaults
import xyz.junerver.compose.palette.components.radio.RadioDefaults
import xyz.junerver.compose.palette.components.rate.RateDefaults
import xyz.junerver.compose.palette.components.result.ResultDefaults
import xyz.junerver.compose.palette.components.screen.ScreenDefaults
import xyz.junerver.compose.palette.components.searchbar.SearchBarDefaults
import xyz.junerver.compose.palette.components.select.SelectDefaults
import xyz.junerver.compose.palette.components.segmented.SegmentedDefaults
import xyz.junerver.compose.palette.components.skeleton.SkeletonDefaults
import xyz.junerver.compose.palette.components.space.SpaceDefaults
import xyz.junerver.compose.palette.components.slider.SliderDefaults
import xyz.junerver.compose.palette.components.sortable.SortableDefaults
import xyz.junerver.compose.palette.components.statistic.StatisticDefaults
import xyz.junerver.compose.palette.components.steps.StepsDefaults
import xyz.junerver.compose.palette.components.switch.SwitchDefaults
import xyz.junerver.compose.palette.components.table.TableDefaults
import xyz.junerver.compose.palette.components.tabs.TabsDefaults
import xyz.junerver.compose.palette.components.tag.TagDefaults
import xyz.junerver.compose.palette.components.textfield.TextFieldDefaults
import xyz.junerver.compose.palette.components.toast.ToastDefaults
import xyz.junerver.compose.palette.components.toolbar.ToolbarDefaults
import xyz.junerver.compose.palette.components.toggle.ToggleDefaults
import xyz.junerver.compose.palette.components.timeline.TimelineDefaults
import xyz.junerver.compose.palette.components.treeselect.TreeSelectDefaults
import xyz.junerver.compose.palette.components.transfer.TransferDefaults
import xyz.junerver.compose.palette.components.timepicker.TimePickerDefaults
import xyz.junerver.compose.palette.components.tooltip.TooltipDefaults
import xyz.junerver.compose.palette.components.tour.TourDefaults
import xyz.junerver.compose.palette.components.upload.UploadDefaults
import xyz.junerver.compose.palette.components.loading.LoadingDefaults
import xyz.junerver.compose.palette.components.menu.MenuDefaults
import xyz.junerver.compose.palette.components.tree.TreeDefaults
import xyz.junerver.compose.palette.components.virtuallist.VirtualListDefaults
import xyz.junerver.compose.palette.components.watermark.WatermarkDefaults
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.core.tokens.PaletteComponentThemes
import xyz.junerver.compose.palette.foundation.border.BorderContainerDefaults
import kotlin.test.Test

class ComponentThemeOverrideUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun representativeDefaults_shouldReadRootComponentThemeTokens() {
        val colors = PaletteColors.light()
        val base = PaletteComponentThemes.default(colors = colors)
        val buttonColor = Color(0xFF123456)
        val borderContainerColor = Color(0xFF193A4B)
        val checkboxColor = Color(0xFF1A4B5C)
        val radioColor = Color(0xFF2B5C6D)
        val switchColor = Color(0xFF3C6D7E)
        val selectionControlColor = Color(0xFF3D6E7F)
        val formColor = Color(0xFF4D7E8F)
        val textFieldColor = Color(0xFF234567)
        val selectColor = Color(0xFF345678)
        val mentionsColor = Color(0xFF314F68)
        val dateTimeColor = Color(0xFF385E78)
        val inputColor = Color(0xFF3A607A)
        val cardColor = Color(0xFF456789)
        val tableColor = Color(0xFF56789A)
        val dataGridColor = Color(0xFF5A8ABC)
        val dataDisplayColor = Color(0xFF5B8BBD)
        val dataEntryColor = Color(0xFF5B8CBE)
        val navigationMenuColor = Color(0xFF5C8CBE)
        val appBarColor = Color(0xFF5D8DBF)
        val floatingLayerColor = Color(0xFF5E8EC0)
        val dialogColor = Color(0xFF6789AB)
        val drawerColor = Color(0xFF6A8BAD)
        val popupColor = Color(0xFF6B8CAE)
        val actionSheetColor = Color(0xFF6C8DAF)
        val messageColor = Color(0xFF789ABC)
        val notificationColor = Color(0xFF7A9BCD)
        val toastColor = Color(0xFF8ABCDE)
        val tagColor = Color(0xFF89ABCD)
        val feedbackDisplayColor = Color(0xFF91A2B3)
        val progressColor = Color(0xFF92A3B4)
        val mediaColor = Color(0xFF93A4B5)
        val utilityColor = Color(0xFF94A5B6)
        val floatingActionColor = Color(0xFF95A6B7)
        val screenColor = Color(0xFF9ABCDE)
        val componentThemes = base.copy(
            borderContainer = base.borderContainer.copy(
                borderColor = borderContainerColor,
                contentPadding = 11.dp,
            ),
            button = base.button.copy(
                primaryContainerColor = buttonColor,
                defaultWidth = 222.dp,
            ),
            checkbox = base.checkbox.copy(
                checkedColor = checkboxColor,
                motionDuration = 123,
            ),
            radio = base.radio.copy(
                checkedColor = radioColor,
                itemPadding = 13.dp,
            ),
            switch = base.switch.copy(
                checkedTrackColor = switchColor,
                width = 77.dp,
            ),
            selectionControl = base.selectionControl.copy(
                sliderActiveTrackColor = selectionControlColor,
                rateMediumStarSize = 31.dp,
                toggleCornerRadius = 11.dp,
                segmentedItemPaddingHorizontal = 21.dp,
            ),
            form = base.form.copy(
                labelColor = formColor,
                itemSpacing = 31.dp,
            ),
            textField = base.textField.copy(
                textColor = textFieldColor,
                borderWidth = 3.dp,
            ),
            select = base.select.copy(
                selectedOptionContainerColor = selectColor,
                dropdownMaxHeight = 333.dp,
                optionHeight = 47.dp,
                cascaderColumnWidth = 212.dp,
                treeIndent = 29.dp,
                mentionHighlightColor = mentionsColor,
            ),
            dateTime = base.dateTime.copy(
                inputTextColor = dateTimeColor,
                inputIconColor = dateTimeColor,
                calendarCellSize = 44.dp,
            ),
            input = base.input.copy(
                inputNumberButtonColor = inputColor,
                otpCellWidth = 43.dp,
                searchBarHeight = 42.dp,
            ),
            card = base.card.copy(
                elevatedContainerColor = cardColor,
                contentPadding = 44.dp,
            ),
            table = base.table.copy(
                selectedRowContainerColor = tableColor,
                rowHeight = 55.dp,
            ),
            dataGrid = base.dataGrid.copy(
                headerContainerColor = dataGridColor,
                rowHeight = 41.dp,
            ),
            dataDisplay = base.dataDisplay.copy(
                listDividerColor = dataDisplayColor,
                descriptionsLabelWidth = 144.dp,
                badgeMediumSize = 18.dp,
                avatarMediumSize = 46.dp,
                paginationActiveColor = dataDisplayColor,
                paginationMinTouchSize = 51.dp,
            ),
            dataEntry = base.dataEntry.copy(
                timelineDotColor = dataEntryColor,
                transferButtonColor = dataEntryColor,
                treeSelectedColor = dataEntryColor,
                sortableDragHintColor = dataEntryColor,
                virtualListItemContentColor = dataEntryColor,
                infiniteScrollTextColor = dataEntryColor,
                uploadBorderColor = dataEntryColor,
                uploadContentPadding = 17.dp,
            ),
            navigationMenu = base.navigationMenu.copy(
                selectedTextColor = navigationMenuColor,
                contextMenuWidth = 188.dp,
                commandPaletteWidth = 488.dp,
                tabsHorizontalPadding = 17.dp,
                breadcrumbCurrentColor = navigationMenuColor,
                stepsDotSize = 28.dp,
                collapseTitleHeight = 58.dp,
            ),
            appBar = base.appBar.copy(
                toolbarBackgroundColor = appBarColor,
                toolbarHeight = 68.dp,
                pageHeaderBackColor = appBarColor,
                pageHeaderPadding = 22.dp,
            ),
            floatingLayer = base.floatingLayer.copy(
                tooltipTextColor = floatingLayerColor,
                popoverPadding = 18.dp,
                popconfirmButtonSpacing = 13.dp,
                tourContentPadding = 16.dp,
            ),
            dialog = base.dialog.copy(
                okColor = dialogColor,
                buttonHeight = 66.dp,
            ),
            drawer = base.drawer.copy(
                containerColor = drawerColor,
                contentPadding = 18.dp,
            ),
            popup = base.popup.copy(
                containerColor = popupColor,
                cornerRadius = 19.dp,
            ),
            actionSheet = base.actionSheet.copy(
                itemTextColor = actionSheetColor,
                itemHeight = 61.dp,
            ),
            message = base.message.copy(
                infoColor = messageColor,
                iconSize = 21.dp,
            ),
            notification = base.notification.copy(
                infoColor = notificationColor,
                minWidth = 288.dp,
            ),
            toast = base.toast.copy(
                backgroundColor = toastColor,
                iconSize = 111.dp,
            ),
            tag = base.tag.copy(
                defaultContentColor = tagColor,
                borderWidth = 4.dp,
            ),
            feedbackDisplay = base.feedbackDisplay.copy(
                infoColor = feedbackDisplayColor,
                resultIconSize = 77.dp,
                emptyIconSize = 88.dp,
                statisticSpacing = 19.dp,
            ),
            progress = base.progress.copy(
                progressColor = progressColor,
                linearHeight = 9.dp,
                dashboardSize = 118.dp,
                loadingSize = 23.dp,
                skeletonLineHeight = 17.dp,
            ),
            media = base.media.copy(
                carouselActiveIndicatorColor = mediaColor,
                imageContainerColor = mediaColor,
                colorPickerSelectedBorderColor = mediaColor,
            ),
            utility = base.utility.copy(
                qrCodeColor = utilityColor,
                barcodeColor = utilityColor,
                watermarkColor = utilityColor,
            ),
            layout = base.layout.copy(
                spaceMediumSpacing = 23.dp,
                gridDefaultGutter = 7.dp,
                affixDefaultOffset = 5.dp,
            ),
            floatingAction = base.floatingAction.copy(
                backtopContainerColor = floatingActionColor,
                floatButtonContainerColor = floatingActionColor,
            ),
            screen = base.screen.copy(
                backgroundColor = screenColor,
            ),
        )

        rule.setContent {
            PaletteMaterialTheme(colors = colors, componentThemes = componentThemes) {
                Text("BorderContainer color: ${BorderContainerDefaults.borderColor() == borderContainerColor}")
                Text("BorderContainer padding: ${BorderContainerDefaults.contentPadding() == 11.dp}")
                Text("Button color: ${ButtonDefaults.primaryContainerColor() == buttonColor}")
                Text("Button width: ${ButtonDefaults.defaultWidth() == 222.dp}")
                Text("Checkbox color: ${CheckboxDefaults.color() == checkboxColor}")
                Text("Checkbox motion: ${CheckboxDefaults.motionDuration() == 123}")
                Text("Radio color: ${RadioDefaults.checkedColor() == radioColor}")
                Text("Radio padding: ${RadioDefaults.padding() == 13.dp}")
                Text("Switch color: ${SwitchDefaults.checkedTrackColor() == switchColor}")
                Text("Switch width: ${SwitchDefaults.width() == 77.dp}")
                Text("Slider color: ${SliderDefaults.activeTrackColor() == selectionControlColor}")
                Text("Rate size: ${RateDefaults.starSize() == 31.dp}")
                Text("Toggle radius: ${ToggleDefaults.cornerRadius() == 11.dp}")
                Text("Segmented padding: ${SegmentedDefaults.itemPaddingHorizontal() == 21.dp}")
                Text("Form color: ${FormDefaults.labelColor == formColor}")
                Text("Form spacing: ${FormDefaults.itemSpacing() == 31.dp}")
                Text("TextField color: ${TextFieldDefaults.colors().textColor == textFieldColor}")
                Text("TextField border: ${TextFieldDefaults.borderWidth() == 3.dp}")
                Text("Select color: ${SelectDefaults.colors().selectedOptionContainerColor == selectColor}")
                Text("Select height: ${SelectDefaults.dropdownMaxHeight() == 333.dp}")
                Text("Autocomplete height: ${AutocompleteDefaults.optionHeight() == 47.dp}")
                Text("Cascader width: ${CascaderDefaults.columnWidth() == 212.dp}")
                Text("CascaderPanel width: ${CascaderPanelDefaults.columnWidth() == 212.dp}")
                Text("TreeSelect indent: ${TreeSelectDefaults.indent() == 29.dp}")
                Text("Mentions highlight: ${MentionsDefaults.highlightColor() == mentionsColor}")
                Text("DatePicker color: ${DatePickerDefaults.textColor() == dateTimeColor}")
                Text("TimePicker icon: ${TimePickerDefaults.iconColor() == dateTimeColor}")
                Text("DateTimeRange color: ${DateTimeRangeDefaults.textColor() == dateTimeColor}")
                Text("Calendar cell: ${CalendarDefaults.cellSize() == 44.dp}")
                Text("InputNumber color: ${InputNumberDefaults.buttonColor() == inputColor}")
                Text("InputOTP width: ${InputOTPDefaults.cellWidth() == 43.dp}")
                Text("SearchBar height: ${SearchBarDefaults.height() == 42.dp}")
                Text("Card color: ${CardDefaults.elevatedColors().containerColor == cardColor}")
                Text("Card padding: ${CardDefaults.contentPadding() == 44.dp}")
                Text("Table color: ${TableDefaults.colors().selectedRowContainerColor == tableColor}")
                Text("Table height: ${TableDefaults.rowHeight() == 55.dp}")
                Text("DataGrid color: ${DataGridDefaults.headerContainerColor() == dataGridColor}")
                Text("DataGrid height: ${DataGridDefaults.rowHeight() == 41.dp}")
                Text("List color: ${ListDefaults.dividerColor() == dataDisplayColor}")
                Text("Descriptions width: ${DescriptionsDefaults.labelWidth() == 144.dp}")
                Text("Badge size: ${BadgeDefaults.defaultSize() == 18.dp}")
                Text("Avatar size: ${AvatarDefaults.size(AvatarSize.Medium) == 46.dp}")
                Text("Pagination color: ${PaginationDefaults.colors().activeColor == dataDisplayColor}")
                Text("Pagination size: ${PaginationDefaults.minTouchSize() == 51.dp}")
                Text("Timeline color: ${TimelineDefaults.dotColor() == dataEntryColor}")
                Text("Transfer color: ${TransferDefaults.buttonColor() == dataEntryColor}")
                Text("Tree color: ${TreeDefaults.selectedColor() == dataEntryColor}")
                Text("Sortable color: ${SortableDefaults.dragHintColor() == dataEntryColor}")
                Text("VirtualList color: ${VirtualListDefaults.itemContentColor() == dataEntryColor}")
                Text("InfiniteScroll color: ${InfiniteScrollDefaults.textColor() == dataEntryColor}")
                Text("Upload color: ${UploadDefaults.borderColor() == dataEntryColor}")
                Text("Upload padding: ${UploadDefaults.contentPadding() == 17.dp}")
                Text("Menu color: ${MenuDefaults.selectedTextColor() == navigationMenuColor}")
                Text("ContextMenu width: ${ContextMenuDefaults.menuWidth() == 188.dp}")
                Text("CommandPalette width: ${CommandPaletteDefaults.width() == 488.dp}")
                Text("Tabs padding: ${TabsDefaults.horizontalPadding() == 17.dp}")
                Text("Breadcrumb color: ${BreadcrumbDefaults.currentColor() == navigationMenuColor}")
                Text("Steps dot: ${StepsDefaults.dotSize() == 28.dp}")
                Text("Collapse height: ${CollapseDefaults.titleHeight() == 58.dp}")
                Text("Toolbar color: ${ToolbarDefaults.colors().backgroundColor == appBarColor}")
                Text("Toolbar height: ${ToolbarDefaults.height() == 68.dp}")
                Text("PageHeader color: ${PageHeaderDefaults.backColor() == appBarColor}")
                Text("PageHeader padding: ${PageHeaderDefaults.padding() == 22.dp}")
                Text("Tooltip color: ${TooltipDefaults.textColor() == floatingLayerColor}")
                Text("Popover padding: ${PopoverDefaults.padding() == 18.dp}")
                Text("Popconfirm spacing: ${PopconfirmDefaults.buttonSpacing() == 13.dp}")
                Text("Tour padding: ${TourDefaults.contentPadding() == 16.dp}")
                Text("Dialog color: ${DialogDefaults.okColor() == dialogColor}")
                Text("Dialog height: ${DialogDefaults.buttonHeight() == 66.dp}")
                Text("Drawer color: ${DrawerDefaults.containerColor() == drawerColor}")
                Text("Drawer padding: ${DrawerDefaults.contentPadding() == 18.dp}")
                Text("Popup color: ${PopupDefaults.containerColor() == popupColor}")
                Text("Popup radius: ${PopupDefaults.cornerRadius() == 19.dp}")
                Text("ActionSheet color: ${ActionSheetDefaults.itemTextColor() == actionSheetColor}")
                Text("ActionSheet height: ${ActionSheetDefaults.itemHeight() == 61.dp}")
                Text("Message color: ${MessageDefaults.textColor(xyz.junerver.compose.palette.components.message.MessageType.Info) == messageColor}")
                Text("Message icon: ${MessageDefaults.iconSize() == 21.dp}")
                Text("Notification color: ${NotificationDefaults.accentColor(xyz.junerver.compose.palette.components.message.MessageType.Info) == notificationColor}")
                Text("Notification width: ${NotificationDefaults.minWidth() == 288.dp}")
                Text("Toast color: ${ToastDefaults.backgroundColor() == toastColor}")
                Text("Toast size: ${ToastDefaults.iconSize() == 111.dp}")
                Text("Tag color: ${TagDefaults.defaultColors().contentColor == tagColor}")
                Text("Tag border: ${TagDefaults.borderWidth() == 4.dp}")
                Text("Alert color: ${AlertDefaults.contentColor(AlertType.Info) == feedbackDisplayColor}")
                Text("Result icon: ${ResultDefaults.iconSize() == 77.dp}")
                Text("Empty icon: ${EmptyDefaults.iconSize() == 88.dp}")
                Text("Statistic spacing: ${StatisticDefaults.spacing() == 19.dp}")
                Text("Progress color: ${ProgressDefaults.progressColor() == progressColor}")
                Text("Progress height: ${ProgressDefaults.linearHeight() == 9.dp}")
                Text("Dashboard size: ${DashboardProgressDefaults.size() == 118.dp}")
                Text("Loading size: ${LoadingDefaults.size() == 23.dp}")
                Text("Skeleton height: ${SkeletonDefaults.lineHeight() == 17.dp}")
                Text("Carousel color: ${CarouselDefaults.activeIndicatorColor() == mediaColor}")
                Text("Image color: ${ImageDefaults.containerColor() == mediaColor}")
                Text("ColorPicker color: ${ColorPickerDefaults.selectedBorderColor() == mediaColor}")
                Text("QRCode color: ${QRCodeDefaults.color() == utilityColor}")
                Text("Barcode color: ${BarcodeDefaults.color() == utilityColor}")
                Text("Watermark color: ${WatermarkDefaults.color() == utilityColor}")
                Text("Space spacing: ${SpaceDefaults.mediumSpacing() == 23.dp}")
                Text("Grid gutter: ${GridDefaults.defaultGutter() == 7.dp}")
                Text("Affix offset: ${AffixDefaults.defaultOffset() == 5.dp}")
                Text("Backtop color: ${BacktopDefaults.containerColor() == floatingActionColor}")
                Text("FloatButton color: ${FloatButtonDefaults.containerColor() == floatingActionColor}")
                Text("Screen color: ${ScreenDefaults.colors().backgroundColor == screenColor}")
            }
        }

        listOf(
            "Button color: true",
            "BorderContainer color: true",
            "BorderContainer padding: true",
            "Button width: true",
            "Checkbox color: true",
            "Checkbox motion: true",
            "Radio color: true",
            "Radio padding: true",
            "Switch color: true",
            "Switch width: true",
            "Slider color: true",
            "Rate size: true",
            "Toggle radius: true",
            "Segmented padding: true",
            "Form color: true",
            "Form spacing: true",
            "TextField color: true",
            "TextField border: true",
            "Select color: true",
            "Select height: true",
            "Autocomplete height: true",
            "Cascader width: true",
            "CascaderPanel width: true",
            "TreeSelect indent: true",
            "Mentions highlight: true",
            "DatePicker color: true",
            "TimePicker icon: true",
            "DateTimeRange color: true",
            "Calendar cell: true",
            "InputNumber color: true",
            "InputOTP width: true",
            "SearchBar height: true",
            "Card color: true",
            "Card padding: true",
            "Table color: true",
            "Table height: true",
            "DataGrid color: true",
            "DataGrid height: true",
            "List color: true",
            "Descriptions width: true",
            "Badge size: true",
            "Avatar size: true",
            "Pagination color: true",
            "Pagination size: true",
            "Timeline color: true",
            "Transfer color: true",
            "Tree color: true",
            "Sortable color: true",
            "VirtualList color: true",
            "InfiniteScroll color: true",
            "Upload color: true",
            "Upload padding: true",
            "Menu color: true",
            "ContextMenu width: true",
            "CommandPalette width: true",
            "Tabs padding: true",
            "Breadcrumb color: true",
            "Steps dot: true",
            "Collapse height: true",
            "Toolbar color: true",
            "Toolbar height: true",
            "PageHeader color: true",
            "PageHeader padding: true",
            "Tooltip color: true",
            "Popover padding: true",
            "Popconfirm spacing: true",
            "Tour padding: true",
            "Dialog color: true",
            "Dialog height: true",
            "Drawer color: true",
            "Drawer padding: true",
            "Popup color: true",
            "Popup radius: true",
            "ActionSheet color: true",
            "ActionSheet height: true",
            "Message color: true",
            "Message icon: true",
            "Notification color: true",
            "Notification width: true",
            "Toast color: true",
            "Toast size: true",
            "Tag color: true",
            "Tag border: true",
            "Alert color: true",
            "Result icon: true",
            "Empty icon: true",
            "Statistic spacing: true",
            "Progress color: true",
            "Progress height: true",
            "Dashboard size: true",
            "Loading size: true",
            "Skeleton height: true",
            "Carousel color: true",
            "Image color: true",
            "ColorPicker color: true",
            "QRCode color: true",
            "Barcode color: true",
            "Watermark color: true",
            "Space spacing: true",
            "Grid gutter: true",
            "Affix offset: true",
            "Backtop color: true",
            "FloatButton color: true",
            "Screen color: true",
        ).forEach { text ->
            rule.onNodeWithText(text).assertTextEquals(text)
        }
    }

    @Test
    fun explicitDefaultBuilderParameters_shouldOverrideRootComponentThemeTokens() {
        val base = PaletteComponentThemes.default()
        val componentThemes = base.copy(
            textField = base.textField.copy(textColor = Color.Red),
            checkbox = base.checkbox.copy(checkedColor = Color.Red),
            select = base.select.copy(optionTextColor = Color.Red),
            table = base.table.copy(rowContentColor = Color.Red),
            dataDisplay = base.dataDisplay.copy(paginationActiveColor = Color.Red),
            tag = base.tag.copy(defaultContentColor = Color.Red),
            screen = base.screen.copy(backgroundColor = Color.Red),
        )
        val explicit = Color(0xFF102030)

        rule.setContent {
            PaletteMaterialTheme(componentThemes = componentThemes) {
                Text("TextField explicit: ${TextFieldDefaults.colors(textColor = explicit).textColor == explicit}")
                Text("Checkbox explicit: ${CheckboxDefaults.colors(checkedColor = explicit).checkedColor == explicit}")
                Text("Select explicit: ${SelectDefaults.colors(optionTextColor = explicit).optionTextColor == explicit}")
                Text("Table explicit: ${TableDefaults.colors(rowContentColor = explicit).rowContentColor == explicit}")
                Text("Pagination explicit: ${PaginationDefaults.colors(activeColor = explicit).activeColor == explicit}")
                Text("Tag explicit: ${TagDefaults.colors(explicit).contentColor == explicit}")
                Text("Screen explicit: ${ScreenDefaults.colors(backgroundColor = explicit).backgroundColor == explicit}")
            }
        }

        listOf(
            "TextField explicit: true",
            "Checkbox explicit: true",
            "Select explicit: true",
            "Table explicit: true",
            "Pagination explicit: true",
            "Tag explicit: true",
            "Screen explicit: true",
        ).forEach { text ->
            rule.onNodeWithText(text).assertTextEquals(text)
        }
    }
}

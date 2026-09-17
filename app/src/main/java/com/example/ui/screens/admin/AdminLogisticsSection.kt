package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLogisticsSection(
    logisticsConfig: LogisticsConfig,
    townships: List<MyanmarTownship>,
    currentLanguage: Language,
    onUpdateLogisticsConfig: (LogisticsConfig) -> Unit,
    onUpdateTownship: (MyanmarTownship) -> Unit,
    onAddCustomTownship: (nameEn: String, nameMy: String, regionEn: String, regionMy: String, fee: Int, mins: Int) -> Unit,
    onToggleTownship: (String) -> Unit,
    onResetTownships: () -> Unit,
    onAddDeliveryPartner: (DeliveryPartner) -> Unit,
    onUpdateDeliveryPartner: (DeliveryPartner) -> Unit,
    onDeleteDeliveryPartner: (String) -> Unit,
    onToggleDeliveryPartner: (String) -> Unit,
    isAddingZone: Boolean = false,
    onDismissAddZone: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegionFilter by remember { mutableStateOf("ALL") }

    // Dialog States
    var editingTownship by remember { mutableStateOf<MyanmarTownship?>(null) }
    var showAddTownshipDialog by remember { mutableStateOf(isAddingZone) }
    var editingPartner by remember { mutableStateOf<DeliveryPartner?>(null) }
    var showAddPartnerDialog by remember { mutableStateOf(false) }
    var showEditScheduleDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isAddingZone) {
        if (isAddingZone) {
            showAddTownshipDialog = true
        }
    }

    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    val regions = remember(townships) {
        listOf("ALL") + townships.map { it.regionEn }.distinct()
    }

    val filteredTownships = remember(townships, searchQuery, selectedRegionFilter) {
        townships.filter { township ->
            val matchesRegion = selectedRegionFilter == "ALL" || township.regionEn.equals(selectedRegionFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                township.nameEn.contains(searchQuery, ignoreCase = true) ||
                township.nameMy.contains(searchQuery, ignoreCase = true) ||
                township.regionEn.contains(searchQuery, ignoreCase = true) ||
                township.regionMy.contains(searchQuery, ignoreCase = true)
            matchesRegion && matchesSearch
        }
    }

    val activeTownshipCount = townships.count { it.isEnabled }
    val activePartnerCount = logisticsConfig.deliveryPartners.count { it.isEnabled }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Top Overview KPI Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Active Zones KPI
                    LogisticsMetricCard(
                        title = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ခ ဇုန်များ" else "Active Zones",
                        value = "$activeTownshipCount / ${townships.size}",
                        subValue = if (currentLanguage == Language.BURMESE) "မြို့နယ်များ ဖွင့်ထားသည်" else "Townships Enabled",
                        icon = Icons.Default.LocationOn,
                        iconColor = Color(0xFF1E88E5),
                        backgroundColor = Color(0xFFE3F2FD),
                        modifier = Modifier.weight(1f)
                    )

                    // Free Delivery Threshold KPI
                    LogisticsMetricCard(
                        title = if (currentLanguage == Language.BURMESE) "အခမဲ့ ပို့ဆောင်မှု" else "Free Delivery",
                        value = if (logisticsConfig.freeDeliveryEnabled) "≥ ${numberFormat.format(logisticsConfig.freeDeliveryThresholdMMK)} Ks" else "OFF",
                        subValue = if (logisticsConfig.freeDeliveryEnabled) "အော်ဒါပြည့်ပါက အခမဲ့" else "Disabled",
                        icon = Icons.Default.LocalShipping,
                        iconColor = if (logisticsConfig.freeDeliveryEnabled) Color(0xFF16A34A) else Color.Gray,
                        backgroundColor = if (logisticsConfig.freeDeliveryEnabled) Color(0xFFDCFCE7) else Color(0xFFF3F4F6),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Express Rush Shipping KPI
                    LogisticsMetricCard(
                        title = if (currentLanguage == Language.BURMESE) "အမြန်ပို့ဆောင်မှု" else "Express Rush",
                        value = if (logisticsConfig.expressDeliveryEnabled) "+${numberFormat.format(logisticsConfig.expressDeliveryFeeMMK)} Ks" else "OFF",
                        subValue = if (logisticsConfig.expressDeliveryEnabled) "~${logisticsConfig.expressDeliveryMinutes} mins delivery" else "Disabled",
                        icon = Icons.Default.Bolt,
                        iconColor = if (logisticsConfig.expressDeliveryEnabled) Color(0xFFD97706) else Color.Gray,
                        backgroundColor = if (logisticsConfig.expressDeliveryEnabled) Color(0xFFFEF3C7) else Color(0xFFF3F4F6),
                        modifier = Modifier.weight(1f)
                    )

                    // Active Delivery Partners KPI
                    LogisticsMetricCard(
                        title = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ရေး မိတ်ဖက်" else "Courier Fleets",
                        value = "$activePartnerCount Fleets",
                        subValue = if (currentLanguage == Language.BURMESE) "ယာဉ်မောင်း စနစ်များ" else "Active Dispatch",
                        icon = Icons.Default.TwoWheeler,
                        iconColor = Color(0xFF8B5CF6),
                        backgroundColor = Color(0xFFF3E8FF),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Segmented Tabs
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    val tabs = listOf(
                        if (currentLanguage == Language.BURMESE) "စည်းမျဉ်းများ" else "Policies",
                        if (currentLanguage == Language.BURMESE) "မြို့နယ်များ (${filteredTownships.size})" else "Townships (${filteredTownships.size})",
                        if (currentLanguage == Language.BURMESE) "ယာဉ်မောင်းများ" else "Fleets & Partners"
                    )

                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFD32F2F) else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedTab = index }
                                .testTag("logistics_tab_$index")
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // TAB 0: POLICIES & THRESHOLDS
        if (selectedTab == 0) {
            // 1. Minimum Delivery Order Budget Limit Card (Directly editable number option)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE0E7FF),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = Color(0xFF4338CA),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "အနည်းဆုံး မှာယူရမည့် ငွေပမာဏ (Budget Limit)" else "Minimum Delivery Budget Limit",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ဤပမာဏပြည့်မှသာ ပို့ဆောင်မှု အော်ဒါတင်ခွင့်ပြုမည် (၀ ကျပ် = ကန့်သတ်မရှိ)"
                                    else "Minimum food subtotal required to place a delivery order (0 = No limit)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        var minOrderText by remember(logisticsConfig.minOrderAmountMMK) {
                            mutableStateOf(if (logisticsConfig.minOrderAmountMMK > 0) logisticsConfig.minOrderAmountMMK.toString() else "0")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = minOrderText,
                                onValueChange = { minOrderText = it.filter { ch -> ch.isDigit() } },
                                label = {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "အနည်းဆုံးငွေ (ကျပ်) ရိုက်ထည့်ပါ" else "Minimum Budget Amount (MMK)",
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("logistics_min_order_input"),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                suffix = { Text("MMK", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )

                            Button(
                                onClick = {
                                    val parsed = minOrderText.toIntOrNull() ?: 0
                                    onUpdateLogisticsConfig(logisticsConfig.copy(minOrderAmountMMK = parsed))
                                },
                                enabled = minOrderText.toIntOrNull() != null && minOrderText.toIntOrNull() != logisticsConfig.minOrderAmountMMK,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
                                modifier = Modifier.testTag("logistics_apply_min_order_button")
                            ) {
                                Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Apply")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "အမြန်ရွေးချယ်ရန် ကန့်သတ်ချက်များ (Presets):" else "Quick Preset Options:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val minOrderPresets = listOf(0, 3000, 5000, 10000, 15000, 20000)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(minOrderPresets) { preset ->
                                val isSelected = logisticsConfig.minOrderAmountMMK == preset
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        minOrderText = preset.toString()
                                        onUpdateLogisticsConfig(logisticsConfig.copy(minOrderAmountMMK = preset))
                                    },
                                    label = {
                                        Text(
                                            text = if (preset == 0) (if (currentLanguage == Language.BURMESE) "ကန့်သတ်မရှိ (၀)" else "No Min (0)") else "${numberFormat.format(preset)} Ks",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFE0E7FF),
                                        selectedLabelColor = Color(0xFF4338CA)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 2. Free Delivery Policy Card (Editable number option - not just preset!)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFDCFCE7),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.LocalShipping,
                                            contentDescription = null,
                                            tint = Color(0xFF15803D),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ခ အခမဲ့ စည်းမျဉ်း" else "Free Delivery Threshold",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "သတ်မှတ်ငွေပြည့်ပါက ပို့ဆောင်ခ အခမဲ့ပေးခြင်း"
                                        else "Waive delivery fee when order total reaches threshold",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = logisticsConfig.freeDeliveryEnabled,
                                onCheckedChange = {
                                    onUpdateLogisticsConfig(logisticsConfig.copy(freeDeliveryEnabled = it))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF16A34A),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD1D5DB)
                                )
                            )
                        }

                        if (logisticsConfig.freeDeliveryEnabled) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "လက်ရှိ အခမဲ့ရမည့် သတ်မှတ်ငွေ:" else "Active Free Delivery Threshold:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${numberFormat.format(logisticsConfig.freeDeliveryThresholdMMK)} MMK",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF15803D)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            var freeDeliveryText by remember(logisticsConfig.freeDeliveryThresholdMMK) {
                                mutableStateOf(logisticsConfig.freeDeliveryThresholdMMK.toString())
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = freeDeliveryText,
                                    onValueChange = { freeDeliveryText = it.filter { ch -> ch.isDigit() } },
                                    label = {
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "အခမဲ့ရမည့်ငွေ (ကျပ်) ရိုက်ထည့်ပါ" else "Custom Free Delivery Limit (MMK)",
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("logistics_free_delivery_threshold_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    suffix = { Text("MMK", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                )

                                Button(
                                    onClick = {
                                        val parsed = freeDeliveryText.toIntOrNull() ?: 50000
                                        onUpdateLogisticsConfig(logisticsConfig.copy(freeDeliveryThresholdMMK = parsed))
                                    },
                                    enabled = freeDeliveryText.toIntOrNull() != null && freeDeliveryText.toIntOrNull() != logisticsConfig.freeDeliveryThresholdMMK,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    modifier = Modifier.testTag("logistics_apply_free_delivery_button")
                                ) {
                                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Apply")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "အမြန်ရွေးချယ်ရန် ကန့်သတ်ချက်များ (Presets):" else "Quick Preset Options:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val presets = listOf(30000, 50000, 80000, 100000, 150000)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(presets) { preset ->
                                    val isSelected = logisticsConfig.freeDeliveryThresholdMMK == preset
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            freeDeliveryText = preset.toString()
                                            onUpdateLogisticsConfig(logisticsConfig.copy(freeDeliveryThresholdMMK = preset))
                                        },
                                        label = {
                                            Text(
                                                text = "${numberFormat.format(preset)} Ks",
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFDCFCE7),
                                            selectedLabelColor = Color(0xFF15803D)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Express Rush Delivery Card (Editable fee & minutes numbers)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFEF3C7),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "အမြန်ပို့ဆောင်မှု (Express Rush)" else "Express Rush Delivery",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "မိနစ် ၂၀-၃၀ အတွင်း ဦးစားပေး အိမ်အရောက်ပို့ခြင်း"
                                        else "Priority instant dispatch for urgent seafood deliveries",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = logisticsConfig.expressDeliveryEnabled,
                                onCheckedChange = {
                                    onUpdateLogisticsConfig(logisticsConfig.copy(expressDeliveryEnabled = it))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFD97706),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD1D5DB)
                                )
                            )
                        }

                        if (logisticsConfig.expressDeliveryEnabled) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            var expressFeeText by remember(logisticsConfig.expressDeliveryFeeMMK) {
                                mutableStateOf(logisticsConfig.expressDeliveryFeeMMK.toString())
                            }
                            var expressMinutesText by remember(logisticsConfig.expressDeliveryMinutes) {
                                mutableStateOf(logisticsConfig.expressDeliveryMinutes.toString())
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = expressFeeText,
                                    onValueChange = { expressFeeText = it.filter { ch -> ch.isDigit() } },
                                    label = {
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "အမြန်ပို့ အပိုကြေး (ကျပ်)" else "Rush Surcharge (MMK)",
                                            fontSize = 11.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("logistics_express_fee_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    suffix = { Text("MMK", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )

                                OutlinedTextField(
                                    value = expressMinutesText,
                                    onValueChange = { expressMinutesText = it.filter { ch -> ch.isDigit() } },
                                    label = {
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "ခန့်မှန်း အချိန် (မိနစ်)" else "Target ETA (Mins)",
                                            fontSize = 11.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("logistics_express_minutes_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    suffix = { Text("mins", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Surcharge Quick Chips & Save Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "အပိုကြေး အမြန်ရွေးချယ်ရန်:" else "Quick Surcharge Options:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val isChanged = (expressFeeText.toIntOrNull() != null && expressFeeText.toIntOrNull() != logisticsConfig.expressDeliveryFeeMMK) ||
                                        (expressMinutesText.toIntOrNull() != null && expressMinutesText.toIntOrNull() != logisticsConfig.expressDeliveryMinutes)
                                Button(
                                    onClick = {
                                        val fee = expressFeeText.toIntOrNull() ?: 1500
                                        val mins = expressMinutesText.toIntOrNull() ?: 25
                                        onUpdateLogisticsConfig(
                                            logisticsConfig.copy(
                                                expressDeliveryFeeMMK = fee,
                                                expressDeliveryMinutes = mins
                                            )
                                        )
                                    },
                                    enabled = isChanged,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("logistics_save_express_button")
                                ) {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Save Changes",
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            val feePresets = listOf(1000, 1500, 2000, 2500, 3000)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(feePresets) { fee ->
                                    val isSelected = expressFeeText == fee.toString()
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            expressFeeText = fee.toString()
                                            val mins = expressMinutesText.toIntOrNull() ?: logisticsConfig.expressDeliveryMinutes
                                            onUpdateLogisticsConfig(logisticsConfig.copy(expressDeliveryFeeMMK = fee, expressDeliveryMinutes = mins))
                                        },
                                        label = {
                                            Text(
                                                text = "+${numberFormat.format(fee)} Ks",
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFFEF3C7),
                                            selectedLabelColor = Color(0xFFD97706)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            // Live calculation preview
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEF3C7).copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚡", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE)
                                            "ဝယ်ယူသူ ပေးချေရမည့်နှုန်း: သက်ဆိုင်ရာ မြို့နယ်ပို့ခ + အမြန်ပို့ခ (+${numberFormat.format(logisticsConfig.expressDeliveryFeeMMK)} ကျပ်) • ခန့်မှန်းကြာချိန် ~${logisticsConfig.expressDeliveryMinutes} မိနစ်"
                                        else
                                            "Pricing Rule: Township Base Fee + Rush Surcharge (+${numberFormat.format(logisticsConfig.expressDeliveryFeeMMK)} MMK) • Estimated delivery within ~${logisticsConfig.expressDeliveryMinutes} minutes",
                                        fontSize = 11.sp,
                                        color = Color(0xFF92400E),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Standard Delivery Base Fee Card (Editable number option)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF3E8FF),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.TwoWheeler,
                                        contentDescription = null,
                                        tint = Color(0xFF7C3AED),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ပုံမှန် အခြေခံ ပို့ဆောင်ခ (Standard Base Fee)" else "Standard Base Delivery Fee",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ဇုန်အသစ်များအတွက် အခြေခံသတ်မှတ် ပို့ဆောင်ခ"
                                    else "Default baseline delivery fee applied across regular townships",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        var standardFeeText by remember(logisticsConfig.standardDeliveryFeeMMK) {
                            mutableStateOf(logisticsConfig.standardDeliveryFeeMMK.toString())
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = standardFeeText,
                                onValueChange = { standardFeeText = it.filter { ch -> ch.isDigit() } },
                                label = {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "ပုံမှန်ပို့ခ (ကျပ်) ရိုက်ထည့်ပါ" else "Standard Base Fee (MMK)",
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("logistics_standard_fee_input"),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                suffix = { Text("MMK", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )

                            Button(
                                onClick = {
                                    val parsed = standardFeeText.toIntOrNull() ?: 2000
                                    onUpdateLogisticsConfig(logisticsConfig.copy(standardDeliveryFeeMMK = parsed))
                                },
                                enabled = standardFeeText.toIntOrNull() != null && standardFeeText.toIntOrNull() != logisticsConfig.standardDeliveryFeeMMK,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                modifier = Modifier.testTag("logistics_apply_standard_fee_button")
                            ) {
                                Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Apply")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        val standardPresets = listOf(1500, 2000, 2500, 3000, 3500)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(standardPresets) { fee ->
                                val isSelected = logisticsConfig.standardDeliveryFeeMMK == fee
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        standardFeeText = fee.toString()
                                        onUpdateLogisticsConfig(logisticsConfig.copy(standardDeliveryFeeMMK = fee))
                                    },
                                    label = {
                                        Text(
                                            text = "${numberFormat.format(fee)} Ks",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFF3E8FF),
                                        selectedLabelColor = Color(0xFF7C3AED)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 5. Store Self-Pickup (Takeaway) Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE0E7FF),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = Color(0xFF4338CA),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ဆိုင်မှ ကိုယ်တိုင်လာယူခြင်း (Self-Pickup)" else "Store Self-Pickup (Takeaway)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူ ဆိုင်သို့ လာယူနိုင်ပြီး ပို့ဆောင်ခ ၀ ကျပ် ဖြစ်ပါသည်"
                                    else "Allow customers to pick up orders at shop for 0 MMK delivery fee",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = logisticsConfig.selfPickupEnabled,
                            onCheckedChange = {
                                onUpdateLogisticsConfig(logisticsConfig.copy(selfPickupEnabled = it))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4338CA),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFD1D5DB)
                            )
                        )
                    }
                }
            }

            // 6. Cold-Chain Insulation Packaging Card (Editable packaging fee)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFE0F2FE),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AcUnit,
                                            contentDescription = null,
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "ပင်လယ်စာ အအေးထိန်း ရေခဲသေတ္တာ ထုပ်ပိုးမှု" else "Cold-Chain Thermal Ice Box",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "လတ်ဆတ်မှု မပျက်စေရန် အပူထိန်းသေတ္တာနှင့် ရေခဲဂျယ်ဖြင့် ပို့ဆောင်ခြင်း"
                                        else "Insulated box & ice gel packs to preserve fresh seafood freshness",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = logisticsConfig.coldChainPackagingEnabled,
                                onCheckedChange = {
                                    onUpdateLogisticsConfig(logisticsConfig.copy(coldChainPackagingEnabled = it))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF0284C7),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD1D5DB)
                                )
                            )
                        }

                        if (logisticsConfig.coldChainPackagingEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(10.dp))

                            var coldChainFeeText by remember(logisticsConfig.coldChainPackagingFeeMMK) {
                                mutableStateOf(logisticsConfig.coldChainPackagingFeeMMK.toString())
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = coldChainFeeText,
                                    onValueChange = { coldChainFeeText = it.filter { ch -> ch.isDigit() } },
                                    label = {
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "ရေခဲသေတ္တာ ထုပ်ပိုးခ (၀ ကျပ် = အခမဲ့)" else "Packaging Surcharge (0 = Free)",
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("logistics_cold_chain_fee_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    suffix = { Text("MMK", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                )

                                Button(
                                    onClick = {
                                        val parsed = coldChainFeeText.toIntOrNull() ?: 0
                                        onUpdateLogisticsConfig(logisticsConfig.copy(coldChainPackagingFeeMMK = parsed))
                                    },
                                    enabled = coldChainFeeText.toIntOrNull() != null && coldChainFeeText.toIntOrNull() != logisticsConfig.coldChainPackagingFeeMMK,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    modifier = Modifier.testTag("logistics_apply_cold_chain_fee_button")
                                ) {
                                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Apply")
                                }
                            }
                        }
                    }
                }
            }

            // 7. Operating Hours & Kitchen Prep Time Card (Editable prep time)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်မှု အချိန်နှင့် လုပ်ငန်းချိန်များ" else "Dispatch Schedule & Preparation Time",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showEditScheduleDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Schedule",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LogisticsInfoRow(
                            label = if (currentLanguage == Language.BURMESE) "နေ့စဉ် ပို့ဆောင်ချိန်" else "Operating Dispatch Hours",
                            value = logisticsConfig.deliveryOperatingHours
                        )
                        LogisticsInfoRow(
                            label = if (currentLanguage == Language.BURMESE) "နေ့ချင်းရောက် အော်ဒါ နောက်ဆုံးလက်ခံချိန်" else "Same-Day Order Cut-off Time",
                            value = logisticsConfig.sameDayCutoffTime
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        var prepTimeText by remember(logisticsConfig.defaultPrepTimeMinutes) {
                            mutableStateOf(logisticsConfig.defaultPrepTimeMinutes.toString())
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = prepTimeText,
                                onValueChange = { prepTimeText = it.filter { ch -> ch.isDigit() } },
                                label = {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "မီးဖိုချောင် အခြေခံ ပြင်ဆင်ချိန် (မိနစ်)" else "Base Kitchen Prep Time (Minutes)",
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("logistics_prep_time_input"),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                suffix = { Text("mins", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )

                            Button(
                                onClick = {
                                    val parsed = prepTimeText.toIntOrNull() ?: 20
                                    onUpdateLogisticsConfig(logisticsConfig.copy(defaultPrepTimeMinutes = parsed))
                                },
                                enabled = prepTimeText.toIntOrNull() != null && prepTimeText.toIntOrNull() != logisticsConfig.defaultPrepTimeMinutes,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("logistics_apply_prep_time_button")
                            ) {
                                Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Apply")
                            }
                        }
                    }
                }
            }
        }

        // TAB 1: TOWNSHIPS & DELIVERY ZONES
        if (selectedTab == 1) {
            // Search Bar & Region Filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("logistics_search_township"),
                        placeholder = {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "မြို့နယ် ရှာဖွေရန်..." else "Search township or region...",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Region Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(regions) { region ->
                            val isSelected = selectedRegionFilter == region
                            val label = when (region) {
                                "ALL" -> if (currentLanguage == Language.BURMESE) "အားလုံး (${townships.size})" else "All (${townships.size})"
                                "Yangon" -> if (currentLanguage == Language.BURMESE) "ရန်ကုန်" else "Yangon"
                                "Mandalay" -> if (currentLanguage == Language.BURMESE) "မန္တလေး" else "Mandalay"
                                "Naypyidaw" -> if (currentLanguage == Language.BURMESE) "နေပြည်တော်" else "Naypyidaw"
                                else -> region
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedRegionFilter = region },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFD32F2F),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Action buttons row: Add Zone + Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "${filteredTownships.size} မြို့နယ် တွေ့ရှိပါသည်" else "${filteredTownships.size} zones found",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TextButton(
                                onClick = { showResetConfirmDialog = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "မူလအတိုင်း ပြန်ထား" else "Reset Fees",
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = { showAddTownshipDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ဇုန်အသစ်" else "Add Zone",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Townships List
            items(filteredTownships, key = { it.id }) { township ->
                TownshipItemCard(
                    township = township,
                    currentLanguage = currentLanguage,
                    numberFormat = numberFormat,
                    onToggleEnabled = { onToggleTownship(township.id) },
                    onEditClick = { editingTownship = township }
                )
            }
        }

        // TAB 2: COURIERS & DELIVERY FLEETS
        if (selectedTab == 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "စာပို့နှင့် ယာဉ်မောင်း စနစ်များ" else "Delivery Fleets & Couriers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ဆိုင်ပိုင် ယာဉ်မောင်းနှင့် အပြင်ပို့ဆောင်ရေး စီမံခန့်ခွဲခြင်း"
                            else "Manage in-house express dispatch and 3rd-party logistics",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showAddPartnerDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "မိတ်ဖက်အသစ်" else "Add Courier",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            items(logisticsConfig.deliveryPartners, key = { it.id }) { partner ->
                DeliveryPartnerCard(
                    partner = partner,
                    currentLanguage = currentLanguage,
                    onToggleEnabled = { onToggleDeliveryPartner(partner.id) },
                    onEditClick = { editingPartner = partner },
                    onDeleteClick = { onDeleteDeliveryPartner(partner.id) }
                )
            }
        }
    }

    // Edit Township Dialog
    if (editingTownship != null) {
        val township = editingTownship!!
        var feeInput by remember { mutableStateOf(township.deliveryFeeMMK.toString()) }
        var minsInput by remember { mutableStateOf(township.estimatedMinutes.toString()) }
        var isEnabled by remember { mutableStateOf(township.isEnabled) }

        AlertDialog(
            onDismissRequest = { editingTownship = null },
            title = {
                Text(
                    text = "${township.name(currentLanguage)} (${township.region(currentLanguage)})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "ဤမြို့နယ်အတွက် ပို့ဆောင်ခနှင့် ကြာမြင့်ချိန် သတ်မှတ်ပါ"
                        else "Update delivery fee and estimated transit time for this zone",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = feeInput,
                        onValueChange = { feeInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ခ (MMK)" else "Delivery Fee (MMK)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = minsInput,
                        onValueChange = { minsInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (currentLanguage == Language.BURMESE) "ခန့်မှန်း ကြာချိန် (မိနစ်)" else "Estimated Minutes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်မှု ဖွင့်ထားမည်" else "Enable Delivery to Zone",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val fee = feeInput.toIntOrNull() ?: township.deliveryFeeMMK
                        val mins = minsInput.toIntOrNull() ?: township.estimatedMinutes
                        onUpdateTownship(township.copy(deliveryFeeMMK = fee, estimatedMinutes = mins, isEnabled = isEnabled))
                        editingTownship = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTownship = null }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }

    // Add Custom Delivery Zone Dialog
    if (showAddTownshipDialog) {
        var nameEnInput by remember { mutableStateOf("") }
        var nameMyInput by remember { mutableStateOf("") }
        var regionEnInput by remember { mutableStateOf("Yangon") }
        var regionMyInput by remember { mutableStateOf("ရန်ကုန်") }
        var feeInput by remember { mutableStateOf("2000") }
        var minsInput by remember { mutableStateOf("30") }

        AlertDialog(
            onDismissRequest = {
                showAddTownshipDialog = false
                onDismissAddZone()
            },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "မြို့နယ်/ပို့ဆောင်ခဇုန် အသစ်ထည့်မည်" else "Add New Delivery Zone",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameEnInput,
                        onValueChange = { nameEnInput = it },
                        label = { Text("Township Name (English)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nameMyInput,
                        onValueChange = { nameMyInput = it },
                        label = { Text("မြို့နယ် အမည် (မြန်မာ)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = regionEnInput,
                        onValueChange = { regionEnInput = it },
                        label = { Text("Region (e.g. Yangon, Mandalay)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = feeInput,
                            onValueChange = { feeInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Fee (MMK)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = minsInput,
                            onValueChange = { minsInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Minutes") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameEnInput.isNotBlank()) {
                            val fee = feeInput.toIntOrNull() ?: 2000
                            val mins = minsInput.toIntOrNull() ?: 30
                            onAddCustomTownship(nameEnInput, nameMyInput, regionEnInput, regionMyInput, fee, mins)
                            showAddTownshipDialog = false
                            onDismissAddZone()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    enabled = nameEnInput.isNotBlank()
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "ထည့်မည်" else "Add Zone")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddTownshipDialog = false
                    onDismissAddZone()
                }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }

    // Add / Edit Courier Partner Dialog
    if (showAddPartnerDialog || editingPartner != null) {
        val partner = editingPartner
        var nameInput by remember { mutableStateOf(partner?.name ?: "") }
        var typeInput by remember { mutableStateOf(partner?.type ?: "IN_HOUSE") }
        var phoneInput by remember { mutableStateOf(partner?.contactPhone ?: "") }
        var etaInput by remember { mutableStateOf(partner?.estimatedDeliveryTime ?: "25-35 mins") }
        var noteInput by remember { mutableStateOf(partner?.note ?: "") }

        AlertDialog(
            onDismissRequest = {
                showAddPartnerDialog = false
                editingPartner = null
            },
            title = {
                Text(
                    text = if (partner == null) {
                        if (currentLanguage == Language.BURMESE) "ယာဉ်မောင်း/မိတ်ဖက် အသစ်ထည့်မည်" else "Add Delivery Partner"
                    } else {
                        if (currentLanguage == Language.BURMESE) "မိတ်ဖက် အချက်အလက် ပြင်မည်" else "Edit Courier Partner"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Fleet / Partner Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Type Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = typeInput == "IN_HOUSE",
                            onClick = { typeInput = "IN_HOUSE" },
                            label = { Text("In-House Fleet") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = typeInput == "THIRD_PARTY",
                            onClick = { typeInput = "THIRD_PARTY" },
                            label = { Text("3rd Party Logistics") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Dispatch Phone / Hotline") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = etaInput,
                        onValueChange = { etaInput = it },
                        label = { Text("Estimated Turnaround (e.g. 25-35 mins)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Service Notes / Coverage") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = false,
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            if (partner == null) {
                                onAddDeliveryPartner(
                                    DeliveryPartner(
                                        name = nameInput.trim(),
                                        type = typeInput,
                                        contactPhone = phoneInput.trim(),
                                        estimatedDeliveryTime = etaInput.trim(),
                                        note = noteInput.trim(),
                                        isEnabled = true
                                    )
                                )
                            } else {
                                onUpdateDeliveryPartner(
                                    partner.copy(
                                        name = nameInput.trim(),
                                        type = typeInput,
                                        contactPhone = phoneInput.trim(),
                                        estimatedDeliveryTime = etaInput.trim(),
                                        note = noteInput.trim()
                                    )
                                )
                            }
                            showAddPartnerDialog = false
                            editingPartner = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    enabled = nameInput.isNotBlank()
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddPartnerDialog = false
                    editingPartner = null
                }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }

    // Edit Operating Hours & Dispatch Schedule Dialog
    if (showEditScheduleDialog) {
        var hoursInput by remember { mutableStateOf(logisticsConfig.deliveryOperatingHours) }
        var cutoffInput by remember { mutableStateOf(logisticsConfig.sameDayCutoffTime) }
        var prepInput by remember { mutableStateOf(logisticsConfig.defaultPrepTimeMinutes.toString()) }
        var expressFeeInput by remember { mutableStateOf(logisticsConfig.expressDeliveryFeeMMK.toString()) }

        AlertDialog(
            onDismissRequest = { showEditScheduleDialog = false },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "လုပ်ငန်းချိန်နှင့် ပို့ဆောင်ခ ချိန်ညှိရန်" else "Edit Dispatch Schedule & Times",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = hoursInput,
                        onValueChange = { hoursInput = it },
                        label = { Text("Operating Hours") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = cutoffInput,
                        onValueChange = { cutoffInput = it },
                        label = { Text("Same-Day Cut-off Time") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = prepInput,
                        onValueChange = { prepInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Base Prep Time (Minutes)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = expressFeeInput,
                        onValueChange = { expressFeeInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Express Rush Fee (MMK)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val prep = prepInput.toIntOrNull() ?: 20
                        val expressFee = expressFeeInput.toIntOrNull() ?: 1500
                        onUpdateLogisticsConfig(
                            logisticsConfig.copy(
                                deliveryOperatingHours = hoursInput.trim(),
                                sameDayCutoffTime = cutoffInput.trim(),
                                defaultPrepTimeMinutes = prep,
                                expressDeliveryFeeMMK = expressFee
                            )
                        )
                        showEditScheduleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditScheduleDialog = false }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }

    // Reset Defaults Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "မူလသတ်မှတ်ချက်သို့ ပြန်ထားမည်လား?" else "Reset Delivery Fees?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "မြို့နယ်အားလုံး၏ ပို့ဆောင်ခနှင့် ကြာချိန်များကို မူလ standard ဈေးနှုန်းများအတိုင်း ပြန်လည်သတ်မှတ်ပါမည်။"
                    else "This will restore all township delivery fees and estimated transit durations to default factory standards."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetTownships()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "ပြန်လည်ထားမည်" else "Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun LogisticsMetricCard(
    title: String,
    value: String,
    subValue: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    shape = CircleShape,
                    color = backgroundColor,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subValue,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TownshipItemCard(
    township: MyanmarTownship,
    currentLanguage: Language,
    numberFormat: NumberFormat,
    onToggleEnabled: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (township.isEnabled) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (township.isEnabled) 1.dp else 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = township.name(currentLanguage),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (township.isEnabled) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = township.region(currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${numberFormat.format(township.deliveryFeeMMK)} MMK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (township.isEnabled) Color(0xFFD32F2F) else Color.Gray
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "~${township.estimatedMinutes} mins",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!township.isEnabled) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ယာယီရပ်နား" else "Suspended",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB91C1C),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Fee",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Switch(
                    checked = township.isEnabled,
                    onCheckedChange = { onToggleEnabled() },
                    modifier = Modifier.scale(0.85f),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF16A34A),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD1D5DB)
                    )
                )
            }
        }
    }
}

private fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.padding(0.dp) // dummy to allow syntax if needed
)

@Composable
private fun DeliveryPartnerCard(
    partner: DeliveryPartner,
    currentLanguage: Language,
    onToggleEnabled: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = if (partner.type == "IN_HOUSE") Color(0xFFFDE8E8) else Color(0xFFE0F2FE),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (partner.type == "IN_HOUSE") Icons.Default.DeliveryDining else Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = if (partner.type == "IN_HOUSE") Color(0xFFD32F2F) else Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = partner.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (partner.type == "IN_HOUSE") Color(0xFFFEF3C7) else Color(0xFFE0E7FF)
                            ) {
                                Text(
                                    text = if (partner.type == "IN_HOUSE") "IN-HOUSE" else "3RD-PARTY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (partner.type == "IN_HOUSE") Color(0xFFB45309) else Color(0xFF4338CA),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (partner.contactPhone.isNotBlank()) {
                            Text(
                                text = "☎ ${partner.contactPhone}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Switch(
                    checked = partner.isEnabled,
                    onCheckedChange = { onToggleEnabled() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF16A34A),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD1D5DB)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ETA: ${partner.estimatedDeliveryTime}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                    if (partner.id != "in_house_fleet") {
                        IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            if (partner.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = partner.note,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LogisticsInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

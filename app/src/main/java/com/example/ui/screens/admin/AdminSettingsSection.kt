package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BrandEntity
import com.example.data.BusinessSettingsEntity
import com.example.data.CategoryEntity
import com.example.model.Language
import com.example.model.StoreSettingsConfig
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsSection(
    storeSettings: StoreSettingsConfig,
    businessSettings: BusinessSettingsEntity,
    categories: List<CategoryEntity>,
    brands: List<BrandEntity>,
    currentLanguage: Language,
    onUpdateSettings: (StoreSettingsConfig) -> Unit,
    onUpdateBusinessSettings: (BusinessSettingsEntity) -> Unit,
    onSaveCategory: (CategoryEntity) -> Unit,
    onArchiveCategory: (String, Boolean) -> Unit,
    onSaveBrand: (BrandEntity) -> Unit,
    onArchiveBrand: (String, Boolean) -> Unit,
    onBackToStorefront: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Business Info, 1: Shipping & Packaging, 2: Categories, 3: Brands, 4: System
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    // Dialog States
    var showEditBusinessInfoDialog by remember { mutableStateOf(false) }
    var showEditShippingDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryEntity?>(null) }
    var isAddingCategory by remember { mutableStateOf(false) }
    var brandToEdit by remember { mutableStateOf<BrandEntity?>(null) }
    var isAddingBrand by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Settings Sub-Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        if (currentLanguage == Language.BURMESE) "🏢 လုပ်ငန်းအချက်အလက်" else "🏢 Business Info",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        if (currentLanguage == Language.BURMESE) "🚚 ပို့ဆောင်ခနှင့် ထုပ်ပိုးခ" else "🚚 Logistics & Packing",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        if (currentLanguage == Language.BURMESE) "📂 အမျိုးအစားများ (${categories.size})" else "📂 Categories (${categories.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = {
                    Text(
                        if (currentLanguage == Language.BURMESE) "🏷️ တံဆိပ်များ (${brands.size})" else "🏷️ Brands (${brands.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 4,
                onClick = { selectedTab = 4 },
                text = {
                    Text(
                        if (currentLanguage == Language.BURMESE) "⚙️ စနစ်လည်ပတ်မှု" else "⚙️ Operations",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: Business & Brand Information
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
                                    Column {
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "ပင်မလုပ်ငန်းနှင့် တံဆိပ် အချက်အလက်များ" else "Central Business & Brand Information",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "အက်ပ်တစ်ခုလုံး (ငွေချေလွှာ၊ အံဆွဲနှင့် ခြေရာခံမှု) တွင် ချက်ချင်း အသုံးပြုမည့် အချက်အလက်များ"
                                            else "Directly updates storefront, receipts, drawer, and tracking screens",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                    IconButton(
                                        onClick = { showEditBusinessInfoDialog = true },
                                        modifier = Modifier.testTag("edit_business_info_btn")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "လုပ်ငန်း/တံဆိပ် အမည် (EN)" else "Brand Name (EN)",
                                    value = businessSettings.brandNameEn
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "လုပ်ငန်း/တံဆိပ် အမည် (မြန်မာ)" else "Brand Name (MY)",
                                    value = businessSettings.brandNameMy
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "ဆောင်ပုဒ် (EN)" else "Tagline (EN)",
                                    value = businessSettings.taglineEn
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "ဆောင်ပုဒ် (မြန်မာ)" else "Tagline (MY)",
                                    value = businessSettings.taglineMy
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "ဆက်သွယ်ရန် ဖုန်းနံပါတ်" else "Hotline Phone",
                                    value = businessSettings.supportPhone
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "အီးမေးလ်" else "Support Email",
                                    value = businessSettings.supportEmail
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "လိပ်စာ (မြန်မာ)" else "Address (MY)",
                                    value = businessSettings.physicalAddressMy
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "ဖွင့်လှစ်ချိန် (မြန်မာ)" else "Hours (MY)",
                                    value = businessSettings.businessHoursMy
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: Shipping, Logistics & Packaging Rules
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
                                    Column {
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ခနှင့် ထုပ်ပိုးခ စည်းမျဉ်းများ" else "Shipping & Packaging Logistics",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "ငွေရှင်းသည့်အခါ အလိုအလျောက် တွက်ချက်ပေးမည့် နှုန်းထားများ"
                                            else "Rates automatically applied during checkout & cart calculation",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                    IconButton(
                                        onClick = { showEditShippingDialog = true },
                                        modifier = Modifier.testTag("edit_shipping_info_btn")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "ပုံမှန် ပို့ဆောင်ခ" else "Standard Delivery Fee",
                                    value = "${numberFormat.format(businessSettings.standardDeliveryFeeMMK)} MMK"
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "အမြန်ပို့ဆောင်ခ" else "Express Delivery Fee",
                                    value = "${numberFormat.format(businessSettings.expressDeliveryFeeMMK)} MMK"
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "အခမဲ့ပို့ဆောင်ပေးမည့် အနည်းဆုံးပမာဏ" else "Free Shipping Threshold",
                                    value = "${numberFormat.format(businessSettings.freeDeliveryThresholdMMK)} MMK"
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "ရေခဲပုံး အအေးထိန်းထုပ်ပိုးခ" else "Cold Storage Ice Box Fee",
                                    value = "${numberFormat.format(businessSettings.coldStorageBoxFeeMMK)} MMK"
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "အထူးဆေးကြော လေလုံထုပ်ပိုးခ" else "Vacuum Cleaning & Prep Fee",
                                    value = "${numberFormat.format(businessSettings.vacuumCleaningFeeMMK)} MMK"
                                )
                                SettingInfoRow(
                                    label = if (currentLanguage == Language.BURMESE) "အနည်းဆုံး မှာယူရမည့် တန်ဖိုး" else "Minimum Order Amount",
                                    value = "${numberFormat.format(businessSettings.minOrderAmountMMK)} MMK"
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: Categories (CRUD)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း အမျိုးအစားများ စီမံရန်" else "Manage Categories",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { isAddingCategory = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("add_category_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (currentLanguage == Language.BURMESE) "အသစ်ထည့်မည်" else "Add Category", fontSize = 12.sp)
                            }
                        }
                    }

                    items(categories, key = { it.id }) { cat ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (cat.isArchived) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = cat.iconEmoji, fontSize = 18.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = cat.displayName(currentLanguage),
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            if (cat.isArchived) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = MaterialTheme.colorScheme.errorContainer
                                                ) {
                                                    Text(
                                                        text = if (currentLanguage == Language.BURMESE) "🔒 သိမ်းဆည်းထား" else "🔒 Archived",
                                                        fontSize = 9.sp,
                                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) cat.nameEn else cat.nameMy,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row {
                                    IconButton(onClick = { categoryToEdit = cat }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(
                                        onClick = { onArchiveCategory(cat.id, !cat.isArchived) }
                                    ) {
                                        Icon(
                                            imageVector = if (cat.isArchived) Icons.Default.Restore else Icons.Default.Archive,
                                            contentDescription = if (cat.isArchived) "Restore" else "Archive",
                                            tint = if (cat.isArchived) Color(0xFF15803D) else Color(0xFFD32F2F)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: Brands (CRUD)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ကုန်အမှတ်တံဆိပ်များ စီမံရန်" else "Manage Brands & Vendors",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { isAddingBrand = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("add_brand_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (currentLanguage == Language.BURMESE) "အသစ်ထည့်မည်" else "Add Brand", fontSize = 12.sp)
                            }
                        }
                    }

                    items(brands, key = { it.id }) { b ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (b.isArchived) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "🏷️ ${b.displayName(currentLanguage)}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (b.isArchived) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.errorContainer
                                            ) {
                                                Text(
                                                    text = if (currentLanguage == Language.BURMESE) "🔒 သိမ်းဆည်းထား" else "🔒 Archived",
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) b.descriptionMy.ifBlank { b.descriptionEn }
                                        else b.descriptionEn.ifBlank { b.descriptionMy },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row {
                                    IconButton(onClick = { brandToEdit = b }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(
                                        onClick = { onArchiveBrand(b.id, !b.isArchived) }
                                    ) {
                                        Icon(
                                            imageVector = if (b.isArchived) Icons.Default.Restore else Icons.Default.Archive,
                                            contentDescription = if (b.isArchived) "Restore" else "Archive",
                                            tint = if (b.isArchived) Color(0xFF15803D) else Color(0xFFD32F2F)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // TAB 4: Operational Settings & Switch Storefront
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
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "ဆိုင် ဖွင့်/ပိတ် အခြေအနေ" else "Store Operating Status",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (storeSettings.isStoreOpen) {
                                            if (currentLanguage == Language.BURMESE) "ဆိုင်ဖွင့်ထားသည် (အော်ဒါများ ပုံမှန်လက်ခံနေပါသည်)"
                                            else "Store is OPEN (Accepting live customer orders)"
                                        } else {
                                            if (currentLanguage == Language.BURMESE) "ဆိုင်ပိတ်ထားသည် (အော်ဒါအသစ်များ ယာယီရပ်နားထားပါသည်)"
                                            else "Store is CLOSED (Not accepting new orders)"
                                        },
                                        fontSize = 11.sp,
                                        color = if (storeSettings.isStoreOpen) Color(0xFF15803D) else Color(0xFFB91C1C),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Switch(
                                    checked = storeSettings.isStoreOpen,
                                    onCheckedChange = {
                                        onUpdateSettings(storeSettings.copy(isStoreOpen = it))
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFFD32F2F)
                                    ),
                                    modifier = Modifier.testTag("admin_store_open_switch")
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "စနစ် လုပ်ဆောင်ချက်များ" else "System Navigation",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedButton(
                                    onClick = onToggleLanguage,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "ဘာသာစကားပြောင်းလဲရန်: English သို့"
                                        else "Switch Language to: မြန်မာ (Burmese)",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Button(
                                    onClick = onBackToStorefront,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("admin_settings_storefront_btn")
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူ စာမျက်နှာသို့ သွားမည်"
                                        else "Switch to Customer Storefront",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG 1: Edit Business Information
    if (showEditBusinessInfoDialog) {
        var nameEn by remember { mutableStateOf(businessSettings.brandNameEn) }
        var nameMy by remember { mutableStateOf(businessSettings.brandNameMy) }
        var tagEn by remember { mutableStateOf(businessSettings.taglineEn) }
        var tagMy by remember { mutableStateOf(businessSettings.taglineMy) }
        var phone by remember { mutableStateOf(businessSettings.supportPhone) }
        var email by remember { mutableStateOf(businessSettings.supportEmail) }
        var addrEn by remember { mutableStateOf(businessSettings.physicalAddressEn) }
        var addrMy by remember { mutableStateOf(businessSettings.physicalAddressMy) }
        var hoursEn by remember { mutableStateOf(businessSettings.businessHoursEn) }
        var hoursMy by remember { mutableStateOf(businessSettings.businessHoursMy) }

        AlertDialog(
            onDismissRequest = { showEditBusinessInfoDialog = false },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "လုပ်ငန်းနှင့် တံဆိပ် အချက်အလက် ပြင်ဆင်ရန်" else "Edit Business & Brand Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nameEn,
                        onValueChange = { nameEn = it },
                        label = { Text("Brand Name (EN)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nameMy,
                        onValueChange = { nameMy = it },
                        label = { Text("Brand Name (MY)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tagMy,
                        onValueChange = { tagMy = it },
                        label = { Text("Tagline (MY)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Hotline Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Support Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = addrMy,
                        onValueChange = { addrMy = it },
                        label = { Text("Address (MY)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = hoursMy,
                        onValueChange = { hoursMy = it },
                        label = { Text("Opening Hours (MY)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateBusinessSettings(
                            businessSettings.copy(
                                brandNameEn = nameEn.trim(),
                                brandNameMy = nameMy.trim(),
                                taglineEn = tagEn.trim(),
                                taglineMy = tagMy.trim(),
                                supportPhone = phone.trim(),
                                supportEmail = email.trim(),
                                physicalAddressEn = addrEn.trim(),
                                physicalAddressMy = addrMy.trim(),
                                businessHoursEn = hoursEn.trim(),
                                businessHoursMy = hoursMy.trim()
                            )
                        )
                        // Also sync storeSettings phone and address
                        onUpdateSettings(
                            storeSettings.copy(
                                storePhone = phone.trim(),
                                storeAddress = addrMy.trim(),
                                openingHours = hoursMy.trim()
                            )
                        )
                        showEditBusinessInfoDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "ဒေတာဘေ့စ်တွင် သိမ်းမည်" else "Save to Database")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBusinessInfoDialog = false }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်ဆောင်ပါ" else "Cancel")
                }
            }
        )
    }

    // DIALOG 2: Edit Shipping & Packaging Fees
    if (showEditShippingDialog) {
        var stdFeeText by remember { mutableStateOf(businessSettings.standardDeliveryFeeMMK.toString()) }
        var expFeeText by remember { mutableStateOf(businessSettings.expressDeliveryFeeMMK.toString()) }
        var freeThresholdText by remember { mutableStateOf(businessSettings.freeDeliveryThresholdMMK.toString()) }
        var coldFeeText by remember { mutableStateOf(businessSettings.coldStorageBoxFeeMMK.toString()) }
        var vacuumFeeText by remember { mutableStateOf(businessSettings.vacuumCleaningFeeMMK.toString()) }
        var minOrderText by remember { mutableStateOf(businessSettings.minOrderAmountMMK.toString()) }

        AlertDialog(
            onDismissRequest = { showEditShippingDialog = false },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ခနှင့် ထုပ်ပိုးခ နှုန်းထားများ ပြင်ဆင်ရန်" else "Edit Delivery & Packaging Fees",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = stdFeeText,
                        onValueChange = { stdFeeText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Standard Delivery Fee (MMK)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = expFeeText,
                        onValueChange = { expFeeText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Express Delivery Fee (MMK)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = freeThresholdText,
                        onValueChange = { freeThresholdText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Free Shipping Threshold (MMK)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = coldFeeText,
                        onValueChange = { coldFeeText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Cold Storage Ice Box Fee (MMK)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = vacuumFeeText,
                        onValueChange = { vacuumFeeText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Vacuum Cleaning Prep Fee (MMK)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = minOrderText,
                        onValueChange = { minOrderText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Minimum Order Amount (MMK)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedStd = stdFeeText.toIntOrNull() ?: 2000
                        val parsedExp = expFeeText.toIntOrNull() ?: 3500
                        val parsedFree = freeThresholdText.toIntOrNull() ?: 50000
                        val parsedCold = coldFeeText.toIntOrNull() ?: 2500
                        val parsedVac = vacuumFeeText.toIntOrNull() ?: 1500
                        val parsedMin = minOrderText.toIntOrNull() ?: 5000

                        onUpdateBusinessSettings(
                            businessSettings.copy(
                                standardDeliveryFeeMMK = parsedStd,
                                expressDeliveryFeeMMK = parsedExp,
                                freeDeliveryThresholdMMK = parsedFree,
                                coldStorageBoxFeeMMK = parsedCold,
                                vacuumCleaningFeeMMK = parsedVac,
                                minOrderAmountMMK = parsedMin
                            )
                        )
                        // Also sync storeSettings
                        onUpdateSettings(
                            storeSettings.copy(
                                standardDeliveryFeeMMK = parsedStd,
                                minOrderAmountMMK = parsedMin
                            )
                        )
                        showEditShippingDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းဆည်းမည်" else "Save Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditShippingDialog = false }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်ဆောင်ပါ" else "Cancel")
                }
            }
        )
    }

    // DIALOG 3: Create / Edit Category
    if (isAddingCategory || categoryToEdit != null) {
        val cat = categoryToEdit
        var nameEn by remember { mutableStateOf(cat?.nameEn ?: "") }
        var nameMy by remember { mutableStateOf(cat?.nameMy ?: "") }
        var emoji by remember { mutableStateOf(cat?.iconEmoji ?: "🐟") }

        AlertDialog(
            onDismissRequest = {
                isAddingCategory = false
                categoryToEdit = null
            },
            title = {
                Text(
                    text = if (cat != null) {
                        if (currentLanguage == Language.BURMESE) "အမျိုးအစား ပြင်ဆင်ရန်" else "Edit Category"
                    } else {
                        if (currentLanguage == Language.BURMESE) "အမျိုးအစား အသစ်ထည့်ရန်" else "Add New Category"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = emoji,
                            onValueChange = { emoji = it },
                            label = { Text("Icon Emoji") },
                            modifier = Modifier.width(90.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = nameEn,
                            onValueChange = { nameEn = it },
                            label = { Text("Category Name (EN)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = nameMy,
                        onValueChange = { nameMy = it },
                        label = { Text("အမျိုးအစား အမည် (မြန်မာ)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val categoryEntity = CategoryEntity(
                            id = cat?.id ?: "cat_${UUID.randomUUID().toString().take(8)}",
                            nameEn = nameEn.ifBlank { "New Category" },
                            nameMy = nameMy.ifBlank { "အမျိုးအစားသစ်" },
                            iconEmoji = emoji.ifBlank { "🐟" },
                            isArchived = cat?.isArchived ?: false,
                            sortOrder = cat?.sortOrder ?: (categories.size + 1)
                        )
                        onSaveCategory(categoryEntity)
                        isAddingCategory = false
                        categoryToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Save Category")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isAddingCategory = false
                    categoryToEdit = null
                }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်ဆောင်ပါ" else "Cancel")
                }
            }
        )
    }

    // DIALOG 4: Create / Edit Brand
    if (isAddingBrand || brandToEdit != null) {
        val b = brandToEdit
        var nameEn by remember { mutableStateOf(b?.nameEn ?: "") }
        var nameMy by remember { mutableStateOf(b?.nameMy ?: "") }
        var descEn by remember { mutableStateOf(b?.descriptionEn ?: "") }
        var descMy by remember { mutableStateOf(b?.descriptionMy ?: "") }

        AlertDialog(
            onDismissRequest = {
                isAddingBrand = false
                brandToEdit = null
            },
            title = {
                Text(
                    text = if (b != null) {
                        if (currentLanguage == Language.BURMESE) "တံဆိပ် ပြင်ဆင်ရန်" else "Edit Brand"
                    } else {
                        if (currentLanguage == Language.BURMESE) "တံဆိပ် အသစ်ထည့်ရန်" else "Add New Brand"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nameEn,
                        onValueChange = { nameEn = it },
                        label = { Text("Brand Name (EN)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nameMy,
                        onValueChange = { nameMy = it },
                        label = { Text("တံဆိပ် အမည် (မြန်မာ)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = descEn,
                        onValueChange = { descEn = it },
                        label = { Text("Description (EN)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = descMy,
                        onValueChange = { descMy = it },
                        label = { Text("ဖော်ပြချက် (မြန်မာ)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val brandEntity = BrandEntity(
                            id = b?.id ?: "brand_${UUID.randomUUID().toString().take(8)}",
                            nameEn = nameEn.ifBlank { "New Brand" },
                            nameMy = nameMy.ifBlank { "တံဆိပ်သစ်" },
                            descriptionEn = descEn,
                            descriptionMy = descMy,
                            isArchived = b?.isArchived ?: false
                        )
                        onSaveBrand(brandEntity)
                        isAddingBrand = false
                        brandToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Save Brand")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isAddingBrand = false
                    brandToEdit = null
                }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်ဆောင်ပါ" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = value.ifBlank { "-" },
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1.8f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

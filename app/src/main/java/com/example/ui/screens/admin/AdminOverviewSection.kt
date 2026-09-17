package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.OrderEntity
import com.example.data.ProductEntity
import com.example.data.UserAccountEntity
import com.example.model.AdminSection
import com.example.model.FoodItem
import com.example.model.Language
import com.example.model.OrderStatus
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminOverviewSection(
    products: List<FoodItem>,
    orders: List<OrderEntity>,
    users: List<UserAccountEntity>,
    currentLanguage: Language,
    onNavigateToSection: (AdminSection) -> Unit,
    onQuickAdvanceOrder: (String, OrderStatus) -> Unit,
    onBackToStorefront: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val totalRevenue = orders.sumOf { it.grandTotalMMK }
    val activeOrdersCount = orders.count {
        it.status != OrderStatus.DELIVERED.name && it.status != OrderStatus.CANCELLED.name
    }
    val inStockProductsCount = products.count { it.isAvailable }
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Back Button to Customer Storefront
        if (onBackToStorefront != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBackToStorefront() }
                        .testTag("admin_overview_back_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Storefront",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "စတိုးဆိုင်သို့ ပြန်သွားရန်" else "Back to Customer Storefront",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ပင်မ စားသောက်ဖွယ်ရာ စာမျက်နှာ" else "Return to main food ordering menu",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // High-level KPI Cards Grid (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = if (currentLanguage == Language.BURMESE) "ရောင်းရငွေ" else "Total Revenue",
                        value = "${numberFormat.format(totalRevenue)} K",
                        subtitle = if (currentLanguage == Language.BURMESE) "အော်ဒါအားလုံးမှ" else "From all orders",
                        icon = Icons.Default.Payments,
                        iconTint = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = if (currentLanguage == Language.BURMESE) "အော်ဒါများ" else "Total Orders",
                        value = "${orders.size}",
                        subtitle = if (currentLanguage == Language.BURMESE) "လည်ပတ်ဆဲ: $activeOrdersCount" else "Active: $activeOrdersCount",
                        icon = Icons.Default.ReceiptLong,
                        iconTint = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = if (currentLanguage == Language.BURMESE) "ဟင်းလျာများ" else "Products",
                        value = "${products.size}",
                        subtitle = if (currentLanguage == Language.BURMESE) "ရှိ: $inStockProductsCount" else "In stock: $inStockProductsCount",
                        icon = Icons.Default.Inventory2,
                        iconTint = Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = if (currentLanguage == Language.BURMESE) "သုံးစွဲသူများ" else "Customers",
                        value = "${users.size}",
                        subtitle = if (currentLanguage == Language.BURMESE) "မှတ်ပုံတင်ပြီးသူ" else "Registered users",
                        icon = Icons.Default.People,
                        iconTint = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Navigation Shortcuts
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အမြန် စီမံခန့်ခွဲမှု လမ်းကြောင်းများ" else "Quick Management Shortcuts",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        QuickActionPill(
                            icon = Icons.Default.Inventory2,
                            label = if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း" else "Products",
                            onClick = { onNavigateToSection(AdminSection.PRODUCTS) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionPill(
                            icon = Icons.Default.FormatListBulleted,
                            label = if (currentLanguage == Language.BURMESE) "ပစ္စည်းရှိ/ပြတ်" else "Inventory",
                            onClick = { onNavigateToSection(AdminSection.INVENTORY) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionPill(
                            icon = Icons.Default.ReceiptLong,
                            label = if (currentLanguage == Language.BURMESE) "အော်ဒါများ" else "Orders",
                            onClick = { onNavigateToSection(AdminSection.ORDERS) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionPill(
                            icon = Icons.Default.Campaign,
                            label = if (currentLanguage == Language.BURMESE) "ပရိုမိုးရှင်း" else "Promos",
                            onClick = { onNavigateToSection(AdminSection.PROMOTIONS) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        QuickActionPill(
                            icon = Icons.Default.AccountBalanceWallet,
                            label = if (currentLanguage == Language.BURMESE) "ငွေပေးချေမှု" else "Payments",
                            onClick = { onNavigateToSection(AdminSection.PAYMENTS) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionPill(
                            icon = Icons.Default.LocalShipping,
                            label = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ရေး" else "Logistics",
                            onClick = { onNavigateToSection(AdminSection.LOGISTICS) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionPill(
                            icon = Icons.Default.BarChart,
                            label = if (currentLanguage == Language.BURMESE) "စာရင်းများ" else "Reports",
                            onClick = { onNavigateToSection(AdminSection.REPORTS) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionPill(
                            icon = Icons.Default.Settings,
                            label = if (currentLanguage == Language.BURMESE) "စနစ်ပြင်ဆင်မှု" else "Settings",
                            onClick = { onNavigateToSection(AdminSection.SETTINGS) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Recent Orders Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "မကြာသေးမီက အော်ဒါများ" else "Recent Orders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigateToSection(AdminSection.ORDERS) }) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အားလုံးကြည့်မည်" else "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }

        // Recent Orders List (up to 5)
        val recentOrders = orders.take(5)
        if (recentOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "အော်ဒါမှတ်တမ်း မရှိသေးပါ" else "No recent orders yet",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(recentOrders, key = { it.orderId }) { order ->
                RecentOrderSummaryCard(
                    order = order,
                    currentLanguage = currentLanguage,
                    onQuickAdvance = onQuickAdvanceOrder
                )
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Surface(
                    shape = CircleShape,
                    color = iconTint.copy(alpha = 0.12f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickActionPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 2.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFFDE8E8),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RecentOrderSummaryCard(
    order: OrderEntity,
    currentLanguage: Language,
    onQuickAdvance: (String, OrderStatus) -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val statusEnum = try {
        OrderStatus.valueOf(order.status)
    } catch (e: Exception) {
        OrderStatus.PLACED
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${order.orderId}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.customerName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                Text(
                    text = "${if (currentLanguage == Language.BURMESE) order.townshipNameMy else order.townshipNameEn} • ${numberFormat.format(order.grandTotalMMK)} MMK",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Status Badge
            val (badgeBg, badgeFg) = when (statusEnum) {
                OrderStatus.PLACED -> Color(0xFFEFF6FF) to Color(0xFF1D4ED8)
                OrderStatus.PREPARING -> Color(0xFFFEF3C7) to Color(0xFFB45309)
                OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFEDE9FE) to Color(0xFF6D28D9)
                OrderStatus.DELIVERED -> Color(0xFFECFDF5) to Color(0xFF047857)
                OrderStatus.CANCELLED -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeBg,
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(
                    text = statusEnum.title(currentLanguage),
                    color = badgeFg,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

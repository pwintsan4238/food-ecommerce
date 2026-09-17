package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.Language
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AdminInventorySection(
    products: List<FoodItem>,
    currentLanguage: Language,
    onToggleAvailability: (FoodItem) -> Unit,
    onEditProduct: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "IN_STOCK", "OUT_OF_STOCK"

    val inStockCount = products.count { it.isAvailable }
    val outOfStockCount = products.size - inStockCount

    val filteredProducts = remember(products, searchQuery, selectedFilter) {
        products.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                item.nameEn.contains(searchQuery, ignoreCase = true) ||
                item.nameMy.contains(searchQuery, ignoreCase = true)

            val matchesStock = when (selectedFilter) {
                "IN_STOCK" -> item.isAvailable
                "OUT_OF_STOCK" -> !item.isAvailable
                else -> true
            }

            matchesSearch && matchesStock
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Stock KPI Banner
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
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ပစ္စည်းလက်ကျန် အခြေအနေ" else "Inventory Availability",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ခလုတ်ဖွင့်/ပိတ်၍ ဆိုင်တွင် ရရှိနိုင်မှု သတ်မှတ်ပါ"
                            else "Toggle switches to instantly mark items In/Out of stock",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = "$inStockCount In",
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = "$outOfStockCount Out",
                                color = Color(0xFFB91C1C),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Search Box
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inventory_search_input"),
                placeholder = {
                    Text(
                        if (currentLanguage == Language.BURMESE) "ပစ္စည်းအမည်ဖြင့် ရှာဖွေပါ..." else "Search inventory item...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Filter Chips (Horizontally scrollable for responsiveness)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text(if (currentLanguage == Language.BURMESE) "အားလုံး (${products.size})" else "All (${products.size})", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "IN_STOCK",
                    onClick = { selectedFilter = "IN_STOCK" },
                    label = { Text(if (currentLanguage == Language.BURMESE) "ပစ္စည်းရှိ ($inStockCount)" else "In Stock ($inStockCount)", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "OUT_OF_STOCK",
                    onClick = { selectedFilter = "OUT_OF_STOCK" },
                    label = { Text(if (currentLanguage == Language.BURMESE) "ပစ္စည်းပြတ် ($outOfStockCount)" else "Out of Stock ($outOfStockCount)", fontSize = 12.sp) }
                )
            }
        }

        // Product Items with Toggle Switch
        if (filteredProducts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "ရှာဖွေမှုနှင့်ကိုက်ညီသော ပစ္စည်းမရှိပါ" else "No matching products found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredProducts, key = { it.id }) { product ->
                InventoryItemCard(
                    product = product,
                    currentLanguage = currentLanguage,
                    onToggle = { onToggleAvailability(product) },
                    onEdit = { onEditProduct(product) }
                )
            }
        }
    }
}

@Composable
private fun InventoryItemCard(
    product: FoodItem,
    currentLanguage: Language,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (product.isAvailable) MaterialTheme.colorScheme.surface else Color(0xFFF9FAFB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (product.isAvailable) 2.dp else 0.dp),
        border = if (!product.isAvailable) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onEdit() }
            .testTag("inventory_item_card_${product.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) product.nameMy else product.nameEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (product.isAvailable) MaterialTheme.colorScheme.onSurface else Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (currentLanguage == Language.BURMESE) product.nameEn else product.nameMy,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${numberFormat.format(product.priceMMK)} MMK",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = if (product.isAvailable) Color(0xFFD32F2F) else Color(0xFF9CA3AF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = product.category.displayName(currentLanguage),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Big Switch matching reference image
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Switch(
                    checked = product.isAvailable,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFD32F2F),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD1D5DB)
                    ),
                    modifier = Modifier.testTag("inventory_switch_${product.id}")
                )
                Text(
                    text = if (product.isAvailable) {
                        if (currentLanguage == Language.BURMESE) "ပစ္စည်းရှိ" else "In Stock"
                    } else {
                        if (currentLanguage == Language.BURMESE) "ပစ္စည်းပြတ်" else "Out of Stock"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (product.isAvailable) Color(0xFF15803D) else Color(0xFFB91C1C)
                )
            }
        }
    }
}

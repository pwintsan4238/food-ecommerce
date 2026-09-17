package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.data.ReviewEntity
import com.example.model.FoodAddOn
import com.example.model.FoodItem
import com.example.model.Language
import com.example.model.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailModalBottomSheet(
    foodItem: FoodItem,
    currentLanguage: Language,
    selectedRegionName: String? = null,
    reviews: List<ReviewEntity> = emptyList(),
    onSubmitReview: ((rating: Int, comment: String) -> Unit)? = null,
    onAddToCart: (
        quantity: Int,
        spiceLevel: String,
        addOns: List<FoodAddOn>,
        notes: String,
        includeColdStorage: Boolean,
        includeSpecialPrep: Boolean,
        fulfillmentPreference: String
    ) -> Unit,
    onProceedToCheckout: ((
        quantity: Int,
        spiceLevel: String,
        addOns: List<FoodAddOn>,
        notes: String,
        includeColdStorage: Boolean,
        includeSpecialPrep: Boolean,
        fulfillmentPreference: String
    ) -> Unit)? = null,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var quantity by remember { mutableIntStateOf(foodItem.minPurchaseQty.coerceAtLeast(1)) }
    var selectedSpiceLevel by remember { mutableStateOf("Normal") }
    val selectedAddOns = remember { mutableStateListOf<FoodAddOn>() }
    var specialNotes by remember { mutableStateOf("") }

    // User selected fulfillment preference (Delivery vs Self Pick-up)
    var selectedFulfillment by remember {
        mutableStateOf(if (foodItem.allowDelivery) "Delivery" else "Self Pickup")
    }

    // Additional Costs (Custom fields) toggles
    var includeColdStorage by remember { mutableStateOf(false) }
    var includeSpecialPrep by remember { mutableStateOf(false) }

    val parsedVariants = remember(foodItem.variants) {
        foodItem.variants.split(",").map { it.trim() }.filter { it.isNotBlank() }
    }
    var selectedVariant by remember(foodItem.variants) {
        mutableStateOf(parsedVariants.firstOrNull() ?: "")
    }

    // Dynamic Price & Discount calculations
    val additionalCostPerUnit = (if (includeColdStorage) foodItem.coldStorageFeeMMK else 0) +
            (if (includeSpecialPrep) foodItem.specialPrepFeeMMK else 0)
    val unitPriceWithAddOns = foodItem.priceMMK + selectedAddOns.sumOf { it.priceMMK } + additionalCostPerUnit
    val rawSubtotal = unitPriceWithAddOns * quantity
    val (discountMMK, discountReason) = foodItem.calculateDiscount(quantity)
    val finalCalculatedTotal = (rawSubtotal - discountMMK).coerceAtLeast(0)

    val isMinAmountMet = foodItem.minPurchaseAmountMMK <= 0 || rawSubtotal >= foodItem.minPurchaseAmountMMK
    val isRegionAllowed = foodItem.isRegionAllowed(selectedRegionName)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Realistic Photo Hero Box & Close Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (foodItem.imageUrl.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model = foodItem.imageUrl,
                            contentDescription = foodItem.nameEn,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text(text = foodItem.iconEmoji, fontSize = 48.sp)
                                }
                            },
                            error = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text(text = foodItem.iconEmoji, fontSize = 48.sp)
                                }
                            }
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = foodItem.iconEmoji, fontSize = 56.sp)
                        }
                    }

                    // Floating close button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("close_food_detail_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Category pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "${foodItem.iconEmoji} ${foodItem.category.displayName(currentLanguage)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Out of Stock or Low Stock Warning Banner
            if (!foodItem.isAvailable || foodItem.stockQuantity <= 0) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEE2E2),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "⚠️ ပစ္စည်းပြတ်လပ်နေပါသည် (Out of Stock)" else "⚠️ Currently Out of Stock",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ဤကုန်ပစ္စည်းအား လက်ရှိတွင် မှာယူ၍မရနိုင်သေးပါ။ မကြာမီ ပြန်လည်ရရှိပါမည်။" else "This product is currently out of stock and cannot be ordered at this moment.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                        }
                    }
                }
            } else if (foodItem.stockQuantity in 1..5) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "⚠️ လက်ကျန်နည်းနေပါပြီ (${foodItem.stockQuantity} ခုသာ ကျန်ရှိပါသည်)"
                                    else "⚠️ Low Stock Alert (Only ${foodItem.stockQuantity} remaining!)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ဝယ်လိုအားများသဖြင့် လက်မလွတ်ရအောင် အမြန်မှာယူလိုက်ပါ"
                                    else "High demand item, grab yours before it runs out!",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            }

            // Title & Price Row
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Brand, SKU and Stock row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = foodItem.brand,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "SKU: ${foodItem.effectiveSku}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (!foodItem.isAvailable || foodItem.stockQuantity <= 0) Color(0xFFFEE2E2)
                            else if (foodItem.stockQuantity <= 5) Color(0xFFFEF3C7)
                            else Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = if (!foodItem.isAvailable || foodItem.stockQuantity <= 0) {
                                    if (currentLanguage == Language.BURMESE) "📦 ပစ္စည်းပြတ်" else "📦 Out of Stock"
                                } else if (foodItem.stockQuantity <= 5) {
                                    if (currentLanguage == Language.BURMESE) "📦 လက်ကျန်: ${foodItem.stockQuantity}" else "📦 Only ${foodItem.stockQuantity} left"
                                } else {
                                    if (currentLanguage == Language.BURMESE) "📦 လက်ကျန်: ${foodItem.stockQuantity} ${foodItem.unitMy}" else "📦 Stock: ${foodItem.stockQuantity} ${foodItem.unitEn}"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!foodItem.isAvailable || foodItem.stockQuantity <= 0) Color(0xFF991B1B)
                                else if (foodItem.stockQuantity <= 5) Color(0xFF92400E)
                                else Color(0xFF166534),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (currentLanguage == Language.BURMESE) foodItem.nameMy else foodItem.nameEn,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        if (foodItem.hasOriginalPriceDiscount) {
                            Text(
                                text = Strings.mmkCurrency(currentLanguage, foodItem.originalPriceMMK),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textDecoration = TextDecoration.LineThrough,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFEE2E2),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text(
                                    text = "-${foodItem.originalPriceDiscountPercent}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            text = Strings.mmkCurrency(currentLanguage, foodItem.priceMMK),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "/ " + (if (currentLanguage == Language.BURMESE) foodItem.unitMy else foodItem.unitEn),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "🔪 " + foodItem.prepStyle.displayName(currentLanguage),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = "🍽️ " + foodItem.occasion.displayName(currentLanguage),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        if (foodItem.isPremium) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "👑 အဆင့်မြင့်" else "👑 Premium",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 1: Fulfillment Options (On/Off switches & selection)
            // -------------------------------------------------------------
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.fulfillmentOptionsTitle(currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // On/off indicator badges & selection chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Home Delivery Option
                            val isDeliveryEnabled = foodItem.allowDelivery
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (!isDeliveryEnabled) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                } else if (selectedFulfillment == "Delivery") {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable(enabled = isDeliveryEnabled) {
                                        selectedFulfillment = "Delivery"
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🚚", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "အိမ်အရောက်ပို့" else "Delivery",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (!isDeliveryEnabled) Color.Gray
                                            else if (selectedFulfillment == "Delivery") MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isDeliveryEnabled) (if (currentLanguage == Language.BURMESE) "ဖွင့်ထားသည် (ON)" else "Available (ON)")
                                        else (if (currentLanguage == Language.BURMESE) "ပိတ်ထားသည် (OFF)" else "Unavailable (OFF)"),
                                        fontSize = 10.sp,
                                        color = if (!isDeliveryEnabled) Color.Red
                                        else if (selectedFulfillment == "Delivery") MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                        else Color(0xFF16A34A),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Self Pick-up Option
                            val isPickupEnabled = foodItem.allowSelfPickup
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (!isPickupEnabled) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                } else if (selectedFulfillment == "Self Pickup") {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable(enabled = isPickupEnabled) {
                                        selectedFulfillment = "Self Pickup"
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🏪", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "ဆိုင်မှယူမည်" else "Self Pick-up",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (!isPickupEnabled) Color.Gray
                                            else if (selectedFulfillment == "Self Pickup") MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isPickupEnabled) (if (currentLanguage == Language.BURMESE) "ဖွင့်ထားသည် (ON)" else "Available (ON)")
                                        else (if (currentLanguage == Language.BURMESE) "ပိတ်ထားသည် (OFF)" else "Unavailable (OFF)"),
                                        fontSize = 10.sp,
                                        color = if (!isPickupEnabled) Color.Red
                                        else if (selectedFulfillment == "Self Pickup") MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                        else Color(0xFF16A34A),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 2: Limit Choices to Cities & Regions
            // -------------------------------------------------------------
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.regionCoverageTitle(currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (foodItem.allowedRegions.isNotBlank() && !foodItem.allowedRegions.equals("All", ignoreCase = true)) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE)
                                    "အောက်ပါ ဒေသများသို့သာ ပို့ဆောင်ပေးနိုင်ပါသည်:"
                                else
                                    "Restricted delivery coverage. Available only in:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                foodItem.allowedRegions.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { region ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "📍 $region",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            if (!selectedRegionName.isNullOrBlank()) {
                                if (isRegionAllowed) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "✅ သင်ရွေးချယ်ထားသော $selectedRegionName သို့ ပို့ဆောင်ပေးနိုင်ပါသည်"
                                            else "✅ Delivery available to your selected area: $selectedRegionName",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF166534),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFEE2E2)
                                    ) {
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "⚠️ သင်ရွေးချယ်ထားသော $selectedRegionName သို့ မပို့ဆောင်နိုင်ပါ (ကန့်သတ်ဒေသအတွင်းသာ ရနိုင်ပါသည်)"
                                            else "⚠️ Not available for delivery in $selectedRegionName (limited coverage)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF991B1B),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = Strings.allRegionsText(currentLanguage),
                                fontSize = 11.sp,
                                color = Color(0xFF166534),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 3: Minimum Purchase Requirement
            // -------------------------------------------------------------
            if (foodItem.minPurchaseQty > 1 || foodItem.minPurchaseAmountMMK > 0) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMinAmountMet) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            else Color(0xFFFFFBEB)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isMinAmountMet) MaterialTheme.colorScheme.primary else Color(0xFFB45309),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.minPurchaseTitle(currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isMinAmountMet) MaterialTheme.colorScheme.primary else Color(0xFFB45309)
                                )
                            }
                            if (foodItem.minPurchaseQty > 1) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE)
                                        "• အနည်းဆုံး ${foodItem.minPurchaseQty} ${foodItem.unitMy} စတင်မှာယူရပါမည်"
                                    else
                                        "• Minimum order quantity: ${foodItem.minPurchaseQty} ${foodItem.unitEn}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (foodItem.minPurchaseAmountMMK > 0) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE)
                                        "• အနည်းဆုံး ကျသင့်ငွေ ${Strings.mmkCurrency(currentLanguage, foodItem.minPurchaseAmountMMK)} ပြည့်မီရပါမည်"
                                    else
                                        "• Minimum purchase amount: ${Strings.mmkCurrency(currentLanguage, foodItem.minPurchaseAmountMMK)}",
                                    fontSize = 11.sp,
                                    color = if (isMinAmountMet) MaterialTheme.colorScheme.onSurface else Color(0xFFB45309),
                                    fontWeight = if (!isMinAmountMet) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 4: Additional Costs (Cold Storage & Special Prep)
            // -------------------------------------------------------------
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = Strings.additionalServicesTitle(currentLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // 1. Cold Storage Packaging Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (includeColdStorage) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AcUnit,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = foodItem.coldStorageTitle(currentLanguage),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "+${Strings.mmkCurrency(currentLanguage, foodItem.coldStorageFeeMMK)} / ${foodItem.displayUnit(currentLanguage)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0284C7)
                                    )
                                }
                            }
                            Switch(
                                checked = includeColdStorage,
                                onCheckedChange = { includeColdStorage = it }
                            )
                        }

                        // 2. Special Preparation Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (includeSpecialPrep) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCut,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = foodItem.specialPrepTitle(currentLanguage),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "+${Strings.mmkCurrency(currentLanguage, foodItem.specialPrepFeeMMK)} / ${foodItem.displayUnit(currentLanguage)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                            }
                            Switch(
                                checked = includeSpecialPrep,
                                onCheckedChange = { includeSpecialPrep = it }
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 5: Discount Options (Over XX Kyats or Kg)
            // -------------------------------------------------------------
            if (foodItem.discountPercentByQty > 0 || foodItem.discountPercentByAmount > 0) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (discountMMK > 0) Color(0xFFF0FDF4)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.discountTiersTitle(currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF16A34A)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (foodItem.discountPercentByQty > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFDCFCE7),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text(
                                                text = if (currentLanguage == Language.BURMESE) "📦 ${foodItem.discountMinQty} ခု/ကီလို အထက်ဝယ်လျှင်"
                                                else "📦 Buy ${foodItem.discountMinQty}+ ${foodItem.displayUnit(currentLanguage)}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF15803D)
                                            )
                                            Text(
                                                text = "${foodItem.discountPercentByQty}% OFF",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF166534)
                                            )
                                        }
                                    }
                                }

                                if (foodItem.discountPercentByAmount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFDCFCE7),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text(
                                                text = if (currentLanguage == Language.BURMESE) "💰 ${Strings.mmkCurrency(currentLanguage, foodItem.discountMinAmountMMK)} အထက်"
                                                else "💰 Over ${Strings.mmkCurrency(currentLanguage, foodItem.discountMinAmountMMK)}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF15803D)
                                            )
                                            Text(
                                                text = "${foodItem.discountPercentByAmount}% OFF",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF166534)
                                            )
                                        }
                                    }
                                }
                            }

                            // Active discount feedback badge
                            if (discountMMK > 0 && discountReason != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF22C55E)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🎉", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${Strings.discountApplied(currentLanguage)}: -$discountMMK MMK ($discountReason)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Product Info Detail Field Card (Customer Read-only)
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📋", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း အချက်အလက် (Product Info)"
                                else "Product Info Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (currentLanguage == Language.BURMESE) foodItem.descriptionMy else foodItem.descriptionEn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "⏱️ ပြင်ဆင်ချိန်: ${foodItem.prepTimeMin} မိနစ်"
                                else "⏱️ Prep Time: ${foodItem.prepTimeMin} mins",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "📦 အတိုင်းအတာ: ${foodItem.unitMy}"
                                else "📦 Unit: ${foodItem.unitEn}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Specifications Card (if provided by admin)
            if (foodItem.specifications.isNotBlank()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🏷️", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "စံချိန်စံညွှန်းနှင့် သတ်မှတ်ချက်များ (Specifications)"
                                    else "Product Specifications",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = foodItem.specifications,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // Variants Selection (if provided by admin)
            if (parsedVariants.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ရွေးချယ်နိုင်သော အမျိုးအစား (Select Variant)" else "Select Variant / Options",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            parsedVariants.forEach { variant ->
                                val isSelected = variant == selectedVariant
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedVariant = variant },
                                    label = {
                                        Text(
                                            text = variant,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Spice Level Selection
            item {
                Column {
                    Text(
                        text = Strings.spicyLevel(currentLanguage),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpiceOptionChip(
                            label = Strings.spicyMild(currentLanguage),
                            isSelected = selectedSpiceLevel == "Mild",
                            onClick = { selectedSpiceLevel = "Mild" },
                            modifier = Modifier.weight(1f)
                        )
                        SpiceOptionChip(
                            label = Strings.spicyMedium(currentLanguage),
                            isSelected = selectedSpiceLevel == "Normal" || selectedSpiceLevel == "Medium",
                            onClick = { selectedSpiceLevel = "Normal" },
                            modifier = Modifier.weight(1f)
                        )
                        SpiceOptionChip(
                            label = Strings.spicyHot(currentLanguage),
                            isSelected = selectedSpiceLevel == "Hot",
                            onClick = { selectedSpiceLevel = "Hot" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Add-ons Section if available
            if (foodItem.availableAddOns.isNotEmpty()) {
                item {
                    Text(
                        text = Strings.addOns(currentLanguage),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(foodItem.availableAddOns, key = { it.id }) { addOn ->
                    val isChecked = selectedAddOns.contains(addOn)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (isChecked) selectedAddOns.remove(addOn) else selectedAddOns.add(addOn)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) selectedAddOns.add(addOn) else selectedAddOns.remove(addOn)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) addOn.nameMy else addOn.nameEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "+${Strings.mmkCurrency(currentLanguage, addOn.priceMMK)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Special notes input
            item {
                OutlinedTextField(
                    value = specialNotes,
                    onValueChange = { specialNotes = it },
                    label = { Text(Strings.specialInstructions(currentLanguage), fontSize = 13.sp) },
                    placeholder = {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ဥပမာ- နံနံပင်မထည့်ပါနှင့်၊ အချိုလျှော့..." else "e.g. Less spicy, extra lime...",
                            fontSize = 12.sp
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("special_instructions_input")
                )
            }

            // Quantity and Subtotal Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "အရေအတွက်" else "Quantity",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (foodItem.minPurchaseQty > 1) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "အနည်းဆုံး: ${foodItem.minPurchaseQty}" else "Min: ${foodItem.minPurchaseQty}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Stepper (Respects minPurchaseQty)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val minLimit = foodItem.minPurchaseQty.coerceAtLeast(1)
                                if (quantity > minLimit) quantity--
                            },
                            enabled = quantity > foodItem.minPurchaseQty.coerceAtLeast(1),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = {
                                if (quantity < foodItem.stockQuantity) quantity++
                            },
                            enabled = quantity < foodItem.stockQuantity,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }

            // Price Breakdown Summary
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း မူရင်းတန်ဖိုး ($quantity x ${Strings.mmkCurrency(currentLanguage, foodItem.priceMMK)})"
                                else "Base Price ($quantity x ${Strings.mmkCurrency(currentLanguage, foodItem.priceMMK)})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Strings.mmkCurrency(currentLanguage, foodItem.priceMMK * quantity),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (includeColdStorage) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "❄️ ${foodItem.coldStorageTitle(currentLanguage)} ($quantity x ${Strings.mmkCurrency(currentLanguage, foodItem.coldStorageFeeMMK)})",
                                    fontSize = 11.sp,
                                    color = Color(0xFF0284C7)
                                )
                                Text(
                                    text = "+${Strings.mmkCurrency(currentLanguage, foodItem.coldStorageFeeMMK * quantity)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7)
                                )
                            }
                        }

                        if (includeSpecialPrep) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🔪 ${foodItem.specialPrepTitle(currentLanguage)} ($quantity x ${Strings.mmkCurrency(currentLanguage, foodItem.specialPrepFeeMMK)})",
                                    fontSize = 11.sp,
                                    color = Color(0xFFD97706)
                                )
                                Text(
                                    text = "+${Strings.mmkCurrency(currentLanguage, foodItem.specialPrepFeeMMK * quantity)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }

                        if (selectedAddOns.isNotEmpty()) {
                            val addOnTotal = selectedAddOns.sumOf { it.priceMMK } * quantity
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "➕ Add-ons Total",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "+${Strings.mmkCurrency(currentLanguage, addOnTotal)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (discountMMK > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🎉 $discountReason",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                                Text(
                                    text = "-${Strings.mmkCurrency(currentLanguage, discountMMK)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "စုစုပေါင်း ကျသင့်ငွေ" else "Subtotal Total",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (discountMMK > 0) {
                                    Text(
                                        text = Strings.mmkCurrency(currentLanguage, rawSubtotal),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = Strings.mmkCurrency(currentLanguage, finalCalculatedTotal),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Customer Reviews Section
            item {
                val productReviews = remember(reviews, foodItem.id) {
                    reviews.filter { it.productId == foodItem.id || it.productName == foodItem.nameEn }
                }
                var showReviewInput by remember { mutableStateOf(false) }
                var userRating by remember { mutableIntStateOf(5) }
                var userComment by remember { mutableStateOf("") }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFEAB308),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "သုံးသပ်ချက်နှင့် အဆင့်သတ်မှတ်ချက်များ" else "Reviews & Ratings",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "${productReviews.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (onSubmitReview != null && !showReviewInput) {
                                TextButton(
                                    onClick = { showReviewInput = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "✍️ သုံးသပ်မည်" else "✍️ Write Review",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Write Review Form
                        if (showReviewInput) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "အဆင့်သတ်မှတ်ရန်" else "Select Rating",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        for (i in 1..5) {
                                            IconButton(
                                                onClick = { userRating = i },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "$i star",
                                                    tint = if (i <= userRating) Color(0xFFEAB308) else MaterialTheme.colorScheme.outlineVariant,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = userComment,
                                        onValueChange = { userComment = it },
                                        placeholder = {
                                            Text(
                                                if (currentLanguage == Language.BURMESE) "အရသာ၊ လတ်ဆတ်မှု၊ ထုပ်ပိုးမှုဆိုင်ရာ သုံးသပ်ချက် ရေးသားပါ..."
                                                else "Share your review about freshness, taste, or packing..."
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .testTag("product_review_comment_input"),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(onClick = { showReviewInput = false }) {
                                            Text(if (currentLanguage == Language.BURMESE) "မလုပ်ဆောင်ပါ" else "Cancel")
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Button(
                                            onClick = {
                                                if (userComment.isNotBlank()) {
                                                    onSubmitReview?.invoke(userRating, userComment.trim())
                                                    userComment = ""
                                                    showReviewInput = false
                                                }
                                            },
                                            enabled = userComment.isNotBlank(),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("submit_product_review_btn")
                                        ) {
                                            Text(if (currentLanguage == Language.BURMESE) "ပေးပို့မည်" else "Submit")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Reviews list
                        if (productReviews.isEmpty()) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ဤကုန်ပစ္စည်းအတွက် သုံးသပ်ချက် မရှိသေးပါ။ ပထမဆုံး သုံးသပ်သူ ဖြစ်လိုက်ပါ!"
                                else "No customer reviews yet. Be the first to share your experience!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                productReviews.forEach { review ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = review.customerName.ifBlank { "Customer" },
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                    for (s in 1..5) {
                                                        Icon(
                                                            imageVector = Icons.Default.Star,
                                                            contentDescription = null,
                                                            tint = if (s <= review.rating) Color(0xFFEAB308) else MaterialTheme.colorScheme.outlineVariant,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            if (review.comment.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = review.comment,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            // Admin Store Reply (if present!)
                                            if (review.adminReply.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(8.dp)) {
                                                        Text(
                                                            text = if (currentLanguage == Language.BURMESE) "👑 ဆိုင်မှ တုံ့ပြန်ချက် (Store Reply):" else "👑 Store Response:",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = review.adminReply,
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons: Proceed to Checkout (Primary) & Add to Cart (Secondary)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isStockAvailable = foodItem.isAvailable && foodItem.stockQuantity > 0
                    val canOrder = isStockAvailable && isMinAmountMet && (foodItem.allowDelivery || foodItem.allowSelfPickup)
                    val effectiveNotes = if (selectedVariant.isNotBlank()) "[$selectedVariant] $specialNotes".trim() else specialNotes

                    // Primary: Proceed / Order Now Button
                    Button(
                        onClick = {
                            if (canOrder) {
                                if (onProceedToCheckout != null) {
                                    onProceedToCheckout(
                                        quantity,
                                        selectedSpiceLevel,
                                        selectedAddOns.toList(),
                                        effectiveNotes,
                                        includeColdStorage,
                                        includeSpecialPrep,
                                        selectedFulfillment
                                    )
                                } else {
                                    onAddToCart(
                                        quantity,
                                        selectedSpiceLevel,
                                        selectedAddOns.toList(),
                                        effectiveNotes,
                                        includeColdStorage,
                                        includeSpecialPrep,
                                        selectedFulfillment
                                    )
                                }
                                onDismiss()
                            }
                        },
                        enabled = canOrder,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("proceed_to_checkout_button")
                    ) {
                        Text(
                            text = if (!isStockAvailable) {
                                if (currentLanguage == Language.BURMESE) "ပစ္စည်းပြတ်လပ်နေပါသည် (Out of Stock)" else "Currently Out of Stock"
                            } else {
                                "${Strings.proceedToCheckout(currentLanguage)} • ${Strings.mmkCurrency(currentLanguage, finalCalculatedTotal)}"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (isStockAvailable) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Proceed",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Secondary: Add to Cart & Keep Browsing
                    OutlinedButton(
                        onClick = {
                            if (canOrder) {
                                onAddToCart(
                                    quantity,
                                    selectedSpiceLevel,
                                    selectedAddOns.toList(),
                                    effectiveNotes,
                                    includeColdStorage,
                                    includeSpecialPrep,
                                    selectedFulfillment
                                )
                                onDismiss()
                            }
                        },
                        enabled = canOrder,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("confirm_add_to_cart_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = if (canOrder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (!isStockAvailable) {
                                if (currentLanguage == Language.BURMESE) "ဝယ်ယူ၍မရသေးပါ" else "Unavailable"
                            } else {
                                Strings.addToCart(currentLanguage)
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpiceOptionChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


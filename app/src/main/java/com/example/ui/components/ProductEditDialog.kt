package com.example.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.data.BrandEntity
import com.example.data.CategoryEntity
import com.example.data.ProductEntity
import com.example.model.DiningOccasion
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.Language
import com.example.model.PrepStyle
import com.example.model.Strings
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditDialog(
    product: FoodItem?,
    currentLanguage: Language,
    categories: List<CategoryEntity> = emptyList(),
    brands: List<BrandEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    val isEdit = product != null

    var nameEn by remember { mutableStateOf(product?.nameEn ?: "") }
    var nameMy by remember { mutableStateOf(product?.nameMy ?: "") }
    var sku by remember { mutableStateOf(product?.sku ?: "") }
    var brand by remember { mutableStateOf(product?.brand ?: "တိမ်တမန်ပင်လယ်စာ (Taim Ta Man)") }
    var stockQuantityText by remember { mutableStateOf((product?.stockQuantity ?: 50).toString()) }
    var isArchived by remember { mutableStateOf(product?.isArchived ?: false) }
    var descriptionEn by remember { mutableStateOf(product?.descriptionEn ?: "") }
    var descriptionMy by remember { mutableStateOf(product?.descriptionMy ?: "") }
    var priceText by remember { mutableStateOf(product?.priceMMK?.toString() ?: "30000") }
    var originalPriceText by remember { mutableStateOf(if ((product?.originalPriceMMK ?: 0) > 0) product?.originalPriceMMK.toString() else "") }
    var specifications by remember { mutableStateOf(product?.specifications ?: "") }
    var variants by remember { mutableStateOf(product?.variants ?: "") }
    var unitEn by remember { mutableStateOf(product?.unitEn ?: "per 1 kg") }
    var unitMy by remember { mutableStateOf(product?.unitMy ?: "၁ ကီလို") }
    var selectedCategory by remember { mutableStateOf(product?.category ?: FoodCategory.FISH) }
    var selectedPrepStyle by remember { mutableStateOf(product?.prepStyle ?: PrepStyle.READY_TO_COOK) }
    var selectedOccasion by remember { mutableStateOf(product?.occasion ?: DiningOccasion.FAMILY_DINNER) }
    var prepTimeText by remember { mutableStateOf(product?.prepTimeMin?.toString() ?: "15") }
    var isPopular by remember { mutableStateOf(product?.isPopular ?: false) }
    var isSpicy by remember { mutableStateOf(product?.isSpicy ?: false) }
    var isPremium by remember { mutableStateOf(product?.isPremium ?: false) }
    var isAvailable by remember { mutableStateOf(product?.isAvailable ?: true) }
    var imageUrl by remember { mutableStateOf(product?.imageUrl ?: "") }
    var iconEmoji by remember { mutableStateOf(product?.iconEmoji ?: selectedCategory.iconEmoji) }

    // Fulfillment options
    var allowSelfPickup by remember { mutableStateOf(product?.allowSelfPickup ?: true) }
    var allowDelivery by remember { mutableStateOf(product?.allowDelivery ?: true) }
    var allowedRegions by remember { mutableStateOf(product?.allowedRegions ?: "") }

    // Minimum purchase
    var minPurchaseQtyText by remember { mutableStateOf((product?.minPurchaseQty ?: 1).toString()) }
    var minPurchaseAmountText by remember { mutableStateOf((product?.minPurchaseAmountMMK ?: 0).toString()) }

    // Additional costs (custom fields)
    var coldStorageFeeText by remember { mutableStateOf((product?.coldStorageFeeMMK ?: 2500).toString()) }
    var coldStorageTitleEn by remember { mutableStateOf(product?.coldStorageTitleEn ?: "Ice Box & Cold Storage Packaging") }
    var coldStorageTitleMy by remember { mutableStateOf(product?.coldStorageTitleMy ?: "ရေခဲပုံးနှင့် အအေးထိန်းထုပ်ပိုးမှု") }

    var specialPrepFeeText by remember { mutableStateOf((product?.specialPrepFeeMMK ?: 1500).toString()) }
    var specialPrepTitleEn by remember { mutableStateOf(product?.specialPrepTitleEn ?: "Special Cleaning & Vacuum Sealing") }
    var specialPrepTitleMy by remember { mutableStateOf(product?.specialPrepTitleMy ?: "အထူးဆေးကြော သန့်စင် လေလုံထုပ်ပိုးမှု") }

    // Discount options
    var discountMinQtyText by remember { mutableStateOf((product?.discountMinQty ?: 3).toString()) }
    var discountPercentByQtyText by remember { mutableStateOf((product?.discountPercentByQty ?: 5).toString()) }
    var discountMinAmountText by remember { mutableStateOf((product?.discountMinAmountMMK ?: 60000).toString()) }
    var discountPercentByAmountText by remember { mutableStateOf((product?.discountPercentByAmount ?: 10).toString()) }

    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveProductImageToInternalStorage(context, uri)
            if (!savedPath.isNullOrBlank()) {
                imageUrl = savedPath
            }
        }
    }

    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var prepMenuExpanded by remember { mutableStateOf(false) }
    var occasionMenuExpanded by remember { mutableStateOf(false) }

    val quickEmojis = listOf("🐟", "🦐", "🦀", "🦞", "🦑", "🐙", "🦪", "🍲", "🍜", "🍤", "🥗", "🍱", "✨")

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 16.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isEdit) {
                                if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း အချက်အလက်များ ပြင်ဆင်ရန်"
                                else "Update Product Details"
                            } else {
                                Strings.addProduct(currentLanguage)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (isEdit && product != null) {
                            Text(
                                text = product.displayName(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // SECTION 1: Product Names
                SectionHeader(
                    title = if (currentLanguage == Language.BURMESE) "အမည်နှင့် အခြေခံအချက်အလက်" else "Product Names & Brand"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text(if (currentLanguage == Language.BURMESE) "SKU / ကုန်ပစ္စည်းကုဒ်" else "SKU / Code") },
                        placeholder = { Text("e.g., SKU-BARRA-01") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_sku_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text(if (currentLanguage == Language.BURMESE) "ကုန်အမှတ်တံဆိပ် (Brand)" else "Brand / Vendor") },
                        placeholder = { Text("e.g., Taim Ta Man") },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("product_brand_input"),
                        singleLine = true
                    )
                }

                if (brands.isNotEmpty()) {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(brands.filter { !it.isArchived }) { b ->
                            FilterChip(
                                selected = brand == b.nameMy || brand == b.nameEn,
                                onClick = { brand = if (currentLanguage == Language.BURMESE) b.nameMy else b.nameEn },
                                label = { Text(b.displayName(currentLanguage), fontSize = 11.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text(Strings.productNameEn(currentLanguage)) },
                    placeholder = { Text("e.g., Fresh Andaman Sea Bass") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_name_en_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = nameMy,
                    onValueChange = { nameMy = it },
                    label = { Text(Strings.productNameMy(currentLanguage)) },
                    placeholder = { Text("ဥပမာ- ကပ္ပလီ ပင်လယ်ငါးကွမ်းရှပ်") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_name_my_input"),
                    singleLine = true
                )

                // SECTION 2: Pricing & Timing
                SectionHeader(
                    title = if (currentLanguage == Language.BURMESE) "ဈေးနှုန်းနှင့် အချိန်" else "Pricing & Measurement"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text(Strings.priceMMKLabel(currentLanguage)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("product_price_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = originalPriceText,
                        onValueChange = { originalPriceText = it },
                        label = { Text(if (currentLanguage == Language.BURMESE) "မူရင်းဈေး (လျှော့ဈေးအတွက်)" else "Original MMK (Strikethrough)") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("product_original_price_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = prepTimeText,
                        onValueChange = { prepTimeText = it },
                        label = { Text(Strings.prepTimeMinLabel(currentLanguage)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("product_prep_time_input"),
                        singleLine = true
                    )
                }

                // Units & Inventory Stock
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = unitEn,
                        onValueChange = { unitEn = it },
                        label = { Text("Unit (EN)") },
                        placeholder = { Text("e.g. per 1 kg / 1 box") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_unit_en_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = unitMy,
                        onValueChange = { unitMy = it },
                        label = { Text("Unit (MY)") },
                        placeholder = { Text("ဥပမာ- ၁ ကီလို / ၁ ပွဲ") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_unit_my_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stockQuantityText,
                        onValueChange = { stockQuantityText = it },
                        label = { Text(if (currentLanguage == Language.BURMESE) "လက်ကျန်စတော့" else "Stock Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_stock_input"),
                        singleLine = true
                    )
                }

                // Archive / Soft-Delete Status Toggle
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isArchived) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
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
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း သိမ်းဆည်းမည် (Archive / Soft-Delete)"
                                else "Archive / Soft-Delete Product",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isArchived) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (currentLanguage == Language.BURMESE)
                                    "ဝယ်ယူသူ စာမျက်နှာတွင် ဖျောက်ထားမည်ဖြစ်သော်လည်း ယခင်အော်ဒါမှတ်တမ်းများနှင့် ဝယ်ယူခဲ့သော ဈေးနှုန်းများကို မူလအတိုင်း ထိန်းသိမ်းထားပါမည်"
                                else
                                    "Hides from customer storefront while preserving historical order receipts & purchased prices",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = isArchived,
                            onCheckedChange = { isArchived = it },
                            modifier = Modifier.testTag("product_archive_switch")
                        )
                    }
                }

                // SECTION 3: Category, Prep Style, Occasion
                SectionHeader(
                    title = if (currentLanguage == Language.BURMESE) "အမျိုးအစားနှင့် ပုံစံ" else "Category & Style"
                )

                // Category Selector
                ExposedDropdownMenuBox(
                    expanded = categoryMenuExpanded,
                    onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = "${selectedCategory.iconEmoji} ${selectedCategory.title(currentLanguage)}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(Strings.categoryLabel(currentLanguage)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("product_category_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false }
                    ) {
                        FoodCategory.values().filter { it != FoodCategory.ALL }.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.iconEmoji} ${cat.title(currentLanguage)}") },
                                onClick = {
                                    selectedCategory = cat
                                    if (iconEmoji == selectedCategory.iconEmoji || iconEmoji.isBlank()) {
                                        iconEmoji = cat.iconEmoji
                                    }
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Prep Style Selector
                ExposedDropdownMenuBox(
                    expanded = prepMenuExpanded,
                    onExpandedChange = { prepMenuExpanded = !prepMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPrepStyle.title(currentLanguage),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(Strings.prepStyleLabel(currentLanguage)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prepMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("product_prep_style_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = prepMenuExpanded,
                        onDismissRequest = { prepMenuExpanded = false }
                    ) {
                        PrepStyle.values().filter { it != PrepStyle.ALL }.forEach { style ->
                            DropdownMenuItem(
                                text = { Text(style.title(currentLanguage)) },
                                onClick = {
                                    selectedPrepStyle = style
                                    prepMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Occasion Selector
                ExposedDropdownMenuBox(
                    expanded = occasionMenuExpanded,
                    onExpandedChange = { occasionMenuExpanded = !occasionMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedOccasion.title(currentLanguage),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(Strings.occasionLabel(currentLanguage)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = occasionMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("product_occasion_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = occasionMenuExpanded,
                        onDismissRequest = { occasionMenuExpanded = false }
                    ) {
                        DiningOccasion.values().filter { it != DiningOccasion.ALL }.forEach { occ ->
                            DropdownMenuItem(
                                text = { Text(occ.title(currentLanguage)) },
                                onClick = {
                                    selectedOccasion = occ
                                    occasionMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // SECTION 4: Product Info Detail Fields (Descriptions)
                SectionHeader(
                    title = if (currentLanguage == Language.BURMESE) "အသေးစိတ် ဖော်ပြချက် (Product Info Detail Field)"
                    else "Product Info Detail Fields"
                )

                OutlinedTextField(
                    value = descriptionEn,
                    onValueChange = { descriptionEn = it },
                    label = { Text(Strings.descriptionEnLabel(currentLanguage)) },
                    placeholder = { Text("Detailed English description, freshness info, source...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_description_en_input"),
                    minLines = 3,
                    maxLines = 6
                )

                OutlinedTextField(
                    value = descriptionMy,
                    onValueChange = { descriptionMy = it },
                    label = { Text(Strings.descriptionMyLabel(currentLanguage)) },
                    placeholder = { Text("မြန်မာဘာသာဖြင့် အသေးစိတ် ဖော်ပြချက်၊ လတ်ဆတ်မှုနှင့် အရင်းအမြစ်...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_description_my_input"),
                    minLines = 3,
                    maxLines = 6
                )

                OutlinedTextField(
                    value = specifications,
                    onValueChange = { specifications = it },
                    label = { Text(if (currentLanguage == Language.BURMESE) "စံချိန်စံညွှန်းနှင့် သတ်မှတ်ချက်များ (Specifications / Origin / Storage)" else "Specifications (Origin, Storage, Catch Method)") },
                    placeholder = { Text("e.g., Origin: Sittwe Coast | Catch: Wild deep-sea | Storage: Keep chilled at 0°C-4°C") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_specifications_input"),
                    minLines = 2,
                    maxLines = 4
                )

                OutlinedTextField(
                    value = variants,
                    onValueChange = { variants = it },
                    label = { Text(if (currentLanguage == Language.BURMESE) "ရွေးချယ်နိုင်သော အမျိုးအစား/အရွယ်အစားများ (Variants - ကော်မာခြား၍)" else "Variants / Sizes (Comma-separated)") },
                    placeholder = { Text("e.g., Regular Cut (500g), Family Pack (1 kg), Jumbo Grade") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_variants_input"),
                    singleLine = true
                )

                // SECTION 5: Media & Visuals (Product Photo Upload and Emoji)
                SectionHeader(
                    title = if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း ဓာတ်ပုံ တင်ရန်နှင့် သင်္ကေတ" else "Product Photo Upload & Icon"
                )

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (imageUrl.isNotBlank()) {
                            // Attached photo preview & management
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                    modifier = Modifier.size(76.dp)
                                ) {
                                    SubcomposeAsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Uploaded Product Photo Preview",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.White),
                                        loading = {
                                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                            }
                                        },
                                        error = {
                                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Text(iconEmoji, fontSize = 28.sp)
                                            }
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF2E7D32).copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "✓ " + Strings.photoUploaded(currentLanguage),
                                            fontSize = 10.sp,
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = if (imageUrl.startsWith("/"))
                                            (if (currentLanguage == Language.BURMESE) "📁 ဖုန်းတွင်း ဓာတ်ပုံဖိုင်" else "📁 Local Device Photo")
                                        else
                                            (if (currentLanguage == Language.BURMESE) "📷 လက်ရှိ ဓာတ်ပုံ" else "📷 Current Photo"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = imageUrl.substringAfterLast("/").take(24),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier
                                                .height(32.dp)
                                                .testTag("change_product_photo_button")
                                        ) {
                                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = Strings.changePhoto(currentLanguage),
                                                fontSize = 11.sp
                                            )
                                        }

                                        IconButton(
                                            onClick = { imageUrl = "" },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.DeleteOutline,
                                                contentDescription = Strings.removePhoto(currentLanguage),
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Upload button when no image attached
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("upload_product_photo_button")
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Strings.uploadProductPhoto(currentLanguage),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = if (currentLanguage == Language.BURMESE)
                                    "ဖုန်းပြခန်းမှ လတ်ဆတ်သော ပင်လယ်စာ ဓာတ်ပုံကို တိုက်ရိုက် ရွေးချယ်တင်သွင်းနိုင်ပါသည်။ (URL ရိုက်ထည့်ရန် မလိုပါ)"
                                else
                                    "Select a product photo directly from your device gallery. (No image URL needed)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Icon Emoji quick selector
                Column {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အိုင်ကွန် သင်္ကေတ ရွေးချယ်ပါ" else "Icon Emoji",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(quickEmojis) { emoji ->
                            val isSelected = iconEmoji == emoji
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { iconEmoji = emoji }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(emoji, fontSize = 22.sp)
                                }
                            }
                        }
                    }
                }

                // SECTION 6: Status & Badges
                SectionHeader(
                    title = if (currentLanguage == Language.BURMESE) "အခြေအနေနှင့် တံဆိပ်များ" else "Status & Badges"
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // In Stock / Available
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ဆိုင်တွင် ရရှိနိုင်မှု (In Stock)" else "Available in Store",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isAvailable) {
                                        if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူများ မှာယူနိုင်ပါသည်" else "Customers can order this item"
                                    } else {
                                        if (currentLanguage == Language.BURMESE) "ပစ္စည်းပြတ်နေပါသည် (Out of Stock)" else "Temporarily out of stock"
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isAvailable,
                                onCheckedChange = { isAvailable = it },
                                modifier = Modifier.testTag("product_available_switch")
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Popular
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(Strings.isPopularLabel(currentLanguage), fontSize = 13.sp)
                            Switch(
                                checked = isPopular,
                                onCheckedChange = { isPopular = it },
                                modifier = Modifier.testTag("product_popular_switch")
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Spicy
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(Strings.isSpicyLabel(currentLanguage), fontSize = 13.sp)
                            Switch(
                                checked = isSpicy,
                                onCheckedChange = { isSpicy = it },
                                modifier = Modifier.testTag("product_spicy_switch")
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Premium
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(Strings.isPremiumLabel(currentLanguage), fontSize = 13.sp)
                            Switch(
                                checked = isPremium,
                                onCheckedChange = { isPremium = it },
                                modifier = Modifier.testTag("product_premium_switch")
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 1. Fulfillment Switches & Regional Limits
                // -------------------------------------------------------------
                SectionHeader(
                    title = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်မှုစနစ်နှင့် အကျုံးဝင်ဒေသများ (Fulfillment & Regions)"
                    else "Fulfillment Options & Region Coverage"
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Self Pick-up Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "🏪 ဆိုင်သို့ ကိုယ်တိုင်လာယူခွင့် (Self Pick-up)" else "🏪 Self Pick-up Option",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူ ဆိုင်မှ လာယူနိုင်သည်" else "Customers can pick up order in-store",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = allowSelfPickup,
                                onCheckedChange = { allowSelfPickup = it },
                                modifier = Modifier.testTag("product_self_pickup_switch")
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Delivery Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "🚚 အိမ်အရောက် ပို့ဆောင်ခွင့် (Delivery)" else "🚚 Home Delivery Option",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "အိမ်အရောက် ပို့ဆောင်ပေးနိုင်သည်" else "Enable doorstep delivery for this item",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = allowDelivery,
                                onCheckedChange = { allowDelivery = it },
                                modifier = Modifier.testTag("product_delivery_switch")
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Limit Choices to Cities & Regions
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "📍 အကျုံးဝင်သော မြို့နယ်/ဒေသ ကန့်သတ်ချက် (Limit to Cities & Regions)"
                            else "📍 Limit Choices to Cities & Regions",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "အားလုံးအတွက်ဆိုလျှင် ကွက်လပ်ထားပါ သို့မဟုတ် ဒေသအမည်များ (ကော်မာခြား) ထည့်ပါ"
                            else "Leave blank for All Regions, or specify allowed regions separated by commas",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Quick presets chips
                        val regionPresets = listOf("All", "Yangon", "Mandalay", "Naypyidaw", "Bago", "Shan State", "Mon State")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (preset in regionPresets) {
                                val isSelected = if (preset == "All") allowedRegions.isBlank() || allowedRegions.equals("All", true)
                                else allowedRegions.contains(preset, ignoreCase = true)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (preset == "All") {
                                            allowedRegions = ""
                                        } else {
                                            val currentList = allowedRegions.split(",").map { it.trim() }.filter { it.isNotBlank() && !it.equals("All", true) }.toMutableList()
                                            if (currentList.any { it.equals(preset, true) }) {
                                                currentList.removeAll { it.equals(preset, true) }
                                            } else {
                                                currentList.add(preset)
                                            }
                                            allowedRegions = currentList.joinToString(", ")
                                        }
                                    },
                                    label = { Text(preset, fontSize = 11.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = allowedRegions,
                            onValueChange = { allowedRegions = it },
                            label = { Text(if (currentLanguage == Language.BURMESE) "သတ်မှတ်ထားသော ဒေသများ" else "Allowed Cities & Regions") },
                            placeholder = { Text("e.g. Yangon, Mandalay, Bago (or empty for nationwide)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // -------------------------------------------------------------
                // 2. Minimum Purchase
                // -------------------------------------------------------------
                SectionHeader(
                    title = if (currentLanguage == Language.BURMESE) "⚖️ အနည်းဆုံး ဝယ်ယူမှု ကန့်သတ်ချက် (Minimum Purchase)"
                    else "Minimum Purchase Requirements"
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = minPurchaseQtyText,
                                onValueChange = { minPurchaseQtyText = it },
                                label = { Text(if (currentLanguage == Language.BURMESE) "အနည်းဆုံး အရေအတွက်/ကီလို" else "Min. Quantity/kg") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = minPurchaseAmountText,
                                onValueChange = { minPurchaseAmountText = it },
                                label = { Text(if (currentLanguage == Language.BURMESE) "အနည်းဆုံး ကျသင့်ငွေ (MMK)" else "Min. Amount (MMK)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 3. Additional Costs (Custom Fields for Cold Storage & Special Prep)
                // -------------------------------------------------------------
                SectionHeader(
                    title = if (currentLanguage == Language.BURMESE) "❄️ အပိုဆောင်း ကုန်ကျစရိတ်များ (Cold Storage & Special Prep Fees)"
                    else "Additional Costs (Cold Storage & Special Prep)"
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "၁။ ရေခဲပုံးနှင့် အအေးထိန်းထုပ်ပိုးမှု ကုန်ကျစရိတ်"
                            else "1. Cold Storage Packaging Additional Fee",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedTextField(
                            value = coldStorageFeeText,
                            onValueChange = { coldStorageFeeText = it },
                            label = { Text(if (currentLanguage == Language.BURMESE) "ရေခဲပုံး ကုန်ကျစရိတ် (MMK)" else "Cold Storage Fee (MMK)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = coldStorageTitleEn,
                                onValueChange = { coldStorageTitleEn = it },
                                label = { Text("Cold Storage Label (EN)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = coldStorageTitleMy,
                                onValueChange = { coldStorageTitleMy = it },
                                label = { Text("Cold Storage Label (MY)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Text(
                            text = if (currentLanguage == Language.BURMESE) "၂။ အထူးဆေးကြောသန့်စင်/လေလုံထုပ်ပိုးမှု ကုန်ကျစရိတ်"
                            else "2. Special Preparation Additional Fee",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedTextField(
                            value = specialPrepFeeText,
                            onValueChange = { specialPrepFeeText = it },
                            label = { Text(if (currentLanguage == Language.BURMESE) "အထူးပြင်ဆင်ခ (MMK)" else "Special Prep Fee (MMK)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = specialPrepTitleEn,
                                onValueChange = { specialPrepTitleEn = it },
                                label = { Text("Special Prep Label (EN)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = specialPrepTitleMy,
                                onValueChange = { specialPrepTitleMy = it },
                                label = { Text("Special Prep Label (MY)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 4. Discount Options (Bulk Quantity or Kyats Purchase)
                // -------------------------------------------------------------
                SectionHeader(
                    title = if (currentLanguage == Language.BURMESE) "🎉 လျှော့စျေး အစီအစဉ်များ (Discounts: Over XX Kyats or Kg)"
                    else "Discount Options (Over XX Kyats or kg)"
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "၁။ ကီလို/အရေအတွက် ပြည့်လျှင် လျှော့စျေး (%)"
                            else "1. Volume Discount: If users buy over XX kg / units",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = discountMinQtyText,
                                onValueChange = { discountMinQtyText = it },
                                label = { Text(if (currentLanguage == Language.BURMESE) "အနည်းဆုံး ကီလို/ခုရေ" else "Min Qty / kg") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = discountPercentByQtyText,
                                onValueChange = { discountPercentByQtyText = it },
                                label = { Text(if (currentLanguage == Language.BURMESE) "လျှော့စျေး (%)" else "Discount (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Text(
                            text = if (currentLanguage == Language.BURMESE) "၂။ ငွေပမာဏ ပြည့်လျှင် လျှော့စျေး (%)"
                            else "2. Value Discount: If users buy over XX Kyats",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = discountMinAmountText,
                                onValueChange = { discountMinAmountText = it },
                                label = { Text(if (currentLanguage == Language.BURMESE) "အနည်းဆုံး ကျပ်ငွေ" else "Min Kyats") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = discountPercentByAmountText,
                                onValueChange = { discountPercentByAmountText = it },
                                label = { Text(if (currentLanguage == Language.BURMESE) "လျှော့စျေး (%)" else "Discount (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalId = product?.id ?: "product_${System.currentTimeMillis()}"
                    val parsedPrice = priceText.toIntOrNull() ?: 30000
                    val parsedOriginalPrice = originalPriceText.toIntOrNull() ?: 0
                    val parsedTime = prepTimeText.toIntOrNull() ?: 15
                    val parsedMinQty = minPurchaseQtyText.toIntOrNull() ?: 1
                    val parsedMinAmount = minPurchaseAmountText.toIntOrNull() ?: 0
                    val parsedColdFee = coldStorageFeeText.toIntOrNull() ?: 2500
                    val parsedPrepFee = specialPrepFeeText.toIntOrNull() ?: 1500
                    val parsedDiscQty = discountMinQtyText.toIntOrNull() ?: 3
                    val parsedDiscQtyPct = discountPercentByQtyText.toIntOrNull() ?: 5
                    val parsedDiscAmount = discountMinAmountText.toIntOrNull() ?: 60000
                    val parsedDiscAmountPct = discountPercentByAmountText.toIntOrNull() ?: 10

                    val newEntity = ProductEntity(
                        id = finalId,
                        sku = sku.ifBlank { finalId.replace("product_", "SKU-").uppercase() },
                        brand = brand.ifBlank { "တိမ်တမန်ပင်လယ်စာ (Taim Ta Man)" },
                        nameEn = nameEn.ifBlank { "Fresh Coastal Seafood" },
                        nameMy = nameMy.ifBlank { "လတ်ဆတ်သော ပင်လယ်စာ" },
                        descriptionEn = descriptionEn.ifBlank { "Premium coastal seafood cleaned, packed and kept on ice." },
                        descriptionMy = descriptionMy.ifBlank { "ရေခဲပုံးဖြင့် စနစ်တကျ ထုပ်ပိုးထားသော အဆင့်မြင့် ပင်လယ်စာ။" },
                        priceMMK = parsedPrice,
                        originalPriceMMK = parsedOriginalPrice,
                        specifications = specifications,
                        variants = variants,
                        unitEn = unitEn.ifBlank { "per 1 kg" },
                        unitMy = unitMy.ifBlank { "၁ ကီလို" },
                        category = selectedCategory.name,
                        prepStyle = selectedPrepStyle.name,
                        occasion = selectedOccasion.name,
                        rating = product?.rating ?: 4.9,
                        prepTimeMin = parsedTime,
                        isPopular = isPopular,
                        isSpicy = isSpicy,
                        isVegetarian = false,
                        isPremium = isPremium,
                        isAvailable = isAvailable,
                        iconEmoji = iconEmoji,
                        imageUrl = imageUrl,
                        allowSelfPickup = allowSelfPickup,
                        allowDelivery = allowDelivery,
                        allowedRegions = allowedRegions,
                        minPurchaseQty = parsedMinQty,
                        minPurchaseAmountMMK = parsedMinAmount,
                        coldStorageFeeMMK = parsedColdFee,
                        coldStorageTitleEn = coldStorageTitleEn.ifBlank { "Ice Box & Cold Storage Packaging" },
                        coldStorageTitleMy = coldStorageTitleMy.ifBlank { "ရေခဲပုံးနှင့် အအေးထိန်းထုပ်ပိုးမှု" },
                        specialPrepFeeMMK = parsedPrepFee,
                        specialPrepTitleEn = specialPrepTitleEn.ifBlank { "Special Cleaning & Vacuum Sealing" },
                        specialPrepTitleMy = specialPrepTitleMy.ifBlank { "အထူးဆေးကြော သန့်စင် လေလုံထုပ်ပိုးမှု" },
                        discountMinQty = parsedDiscQty,
                        discountPercentByQty = parsedDiscQtyPct,
                        discountMinAmountMMK = parsedDiscAmount,
                        discountPercentByAmount = parsedDiscAmountPct,
                        isArchived = isArchived,
                        stockQuantity = stockQuantityText.toIntOrNull() ?: 50
                    )
                    onSave(newEntity)
                },
                modifier = Modifier.testTag("save_product_button")
            ) {
                Text(
                    if (currentLanguage == Language.BURMESE) "အချက်အလက်များ သိမ်းမည်" else Strings.saveProduct(currentLanguage)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLanguage == Language.BURMESE) "မသိမ်းပါ" else "Cancel")
            }
        }
    )
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(14.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun saveProductImageToInternalStorage(context: Context, sourceUri: Uri): String? {
    return try {
        val imagesDir = File(context.filesDir, "product_images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        val targetFile = File(imagesDir, "prod_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        targetFile.absolutePath
    } catch (e: Exception) {
        sourceUri.toString()
    }
}

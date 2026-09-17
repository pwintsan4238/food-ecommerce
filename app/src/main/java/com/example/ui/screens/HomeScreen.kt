package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.model.BrowseCategoryMode
import com.example.model.DiningOccasion
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.Language
import com.example.model.PrepStyle
import com.example.model.PriceRangeFilter
import com.example.model.Strings
import com.example.ui.components.FoodDetailModalBottomSheet
import com.example.ui.components.FoodItemCard
import com.example.ui.components.LanguageToggle
import com.example.ui.components.TaimTaManBrandLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    foodItems: List<FoodItem>,
    searchQuery: String,
    browseMode: BrowseCategoryMode,
    selectedCategory: FoodCategory,
    selectedPrepStyle: PrepStyle,
    selectedOccasion: DiningOccasion,
    selectedPriceRange: PriceRangeFilter,
    activeFilterCount: Int,
    currentLanguage: Language,
    cartItemCount: Int,
    cartSubtotalMMK: Int,
    onSearchQueryChange: (String) -> Unit,
    onBrowseModeSelected: (BrowseCategoryMode) -> Unit,
    onCategorySelected: (FoodCategory) -> Unit,
    onPrepStyleSelected: (PrepStyle) -> Unit,
    onOccasionSelected: (DiningOccasion) -> Unit,
    onPriceRangeSelected: (PriceRangeFilter) -> Unit,
    onClearFilters: () -> Unit,
    onLanguageToggle: () -> Unit,
    onQuickAddToCart: (FoodItem) -> Unit,
    onCustomizedAddToCart: (FoodItem, Int, String, List<com.example.model.FoodAddOn>, String, Boolean, Boolean, String) -> Unit,
    selectedRegionName: String? = null,
    onOpenCart: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    onOpenAccount: () -> Unit = {},
    isGuest: Boolean = false,
    onOpenAuth: () -> Unit = {},
    isAdmin: Boolean = false,
    onOpenAdmin: () -> Unit = {},
    onAdminBack: () -> Unit = {},
    reviews: List<com.example.data.ReviewEntity> = emptyList(),
    onSubmitReview: ((productId: String, productName: String, rating: Int, comment: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedFoodForDetail by remember { mutableStateOf<FoodItem?>(null) }
    val featuredItems = remember(foodItems) {
        foodItems.filter { it.rating >= 4.7 }.take(6).ifEmpty { foodItems.take(4) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // High Density Header with Hamburger Menu, Brand Logo, Account Icon & Language Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Admin Back Button (only shown when logged in as admin to return to previous page / Admin Dashboard)
                        if (isAdmin) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable { onAdminBack() }
                                    .testTag("admin_home_back_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back to Admin Dashboard",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        // 1. Hamburger Icon with Slide Menu
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { onOpenDrawer() }
                                .testTag("top_hamburger_menu_btn")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = Strings.menuDrawer(currentLanguage),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        TaimTaManBrandLogo(
                            size = 40.dp,
                            showSubtext = false,
                            modifier = Modifier.testTag("app_brand_logo")
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = Strings.appTitle(currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 17.sp,
                                lineHeight = 19.sp
                            )
                            Text(
                                text = Strings.appSubtitle(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 2. Account Icon (Acc icon) - opens auth form if guest, opens settings/profile if signed in
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (isGuest) onOpenAuth() else onOpenAccount()
                                }
                                .testTag("top_acc_icon_btn")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isGuest) Icons.Default.PersonOutline else Icons.Default.Person,
                                    contentDescription = if (isGuest) Strings.loginOrSignUp(currentLanguage) else Strings.accountTitle(currentLanguage),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        LanguageToggle(
                            currentLanguage = currentLanguage,
                            onLanguageToggle = onLanguageToggle
                        )
                    }
                }
            }

            // Admin Active Banner with one-tap entry to Admin Console
            if (isAdmin) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenAdmin() }
                            .testTag("home_admin_mode_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${Strings.adminBadge(currentLanguage)} Active",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း၊ ဈေးနှုန်း၊ အသုံးပြုသူနှင့် အော်ဒါများ စီမံရန်"
                                        else "Manage products, pricing, users & orders",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Button(
                                onClick = onOpenAdmin,
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .testTag("admin_banner_open_btn")
                            ) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "စီမံခန့်ခွဲခန်း" else "Console",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Guest User Welcome Banner with one-tap Log In / Sign Up
            if (isGuest) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_guest_mode_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PersonOutline,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = Strings.guestModeActive(currentLanguage),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Text(
                                        text = Strings.guestUserDesc(currentLanguage),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onOpenAuth,
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("guest_banner_login_btn")
                            ) {
                                Text(
                                    text = Strings.loginTitle(currentLanguage),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }


            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = Strings.searchPlaceholder(currentLanguage),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("food_search_input")
                )
            }

            // Browsing Dimension Modes (Seafood Type, Prep Style, Occasion, Price Range)
            // Horizontally scrollable by side
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Top Mode Selector Tabs (Scroll horizontally side-by-side)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(BrowseCategoryMode.values()) { mode ->
                            val isModeSelected = browseMode == mode
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isModeSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onBrowseModeSelected(mode) }
                                    .testTag("browse_mode_${mode.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = mode.icon(), fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = mode.displayName(currentLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = if (isModeSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isModeSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Dynamic Sub-Category Filter Chips (Scroll horizontally side-by-side)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (browseMode) {
                            BrowseCategoryMode.BY_SEAFOOD_TYPE -> {
                                items(FoodCategory.values()) { cat ->
                                    CategoryChip(
                                        title = cat.displayName(currentLanguage),
                                        icon = cat.iconEmoji,
                                        isSelected = selectedCategory == cat,
                                        onClick = { onCategorySelected(cat) }
                                    )
                                }
                            }
                            BrowseCategoryMode.BY_PREP_STYLE -> {
                                items(PrepStyle.values()) { style ->
                                    CategoryChip(
                                        title = style.displayName(currentLanguage),
                                        icon = if (style == PrepStyle.ALL) "✨" else "🔪",
                                        isSelected = selectedPrepStyle == style,
                                        onClick = { onPrepStyleSelected(style) }
                                    )
                                }
                            }
                            BrowseCategoryMode.BY_OCCASION -> {
                                items(DiningOccasion.values()) { occ ->
                                    CategoryChip(
                                        title = occ.displayName(currentLanguage),
                                        icon = if (occ == DiningOccasion.ALL) "✨" else "🍽️",
                                        isSelected = selectedOccasion == occ,
                                        onClick = { onOccasionSelected(occ) }
                                    )
                                }
                            }
                            BrowseCategoryMode.BY_PRICE_RANGE -> {
                                items(PriceRangeFilter.values()) { price ->
                                    CategoryChip(
                                        title = price.displayName(currentLanguage),
                                        icon = if (price == PriceRangeFilter.ALL) "✨" else "💰",
                                        isSelected = selectedPriceRange == price,
                                        onClick = { onPriceRangeSelected(price) }
                                    )
                                }
                            }
                        }

                        // Reset filters chip if any filters are active
                        if (activeFilterCount > 0) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onClearFilters() }
                                        .testTag("clear_filters_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear Filters",
                                            tint = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "ရှင်းလင်းမည် ($activeFilterCount)" else "Clear ($activeFilterCount)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Featured Seafood Horizontal Scrollable Cards ("ဘေးသို့ဆွဲကြည့်ပါ / Swipe by side")
            if (featuredItems.isNotEmpty() && searchQuery.isBlank() && activeFilterCount == 0) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "🌟 အထူးရွေးချယ်ထားသော ပင်လယ်စာများ" else "🌟 Chef's Featured Seafood",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ဘေးသို့ဆွဲပါ ➔" else "Swipe side ➔",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(featuredItems, key = { "featured_${it.id}" }) { item ->
                                FeaturedSeafoodCard(
                                    foodItem = item,
                                    currentLanguage = currentLanguage,
                                    onItemClick = { selectedFoodForDetail = item },
                                    onQuickAdd = { onQuickAddToCart(item) }
                                )
                            }
                        }
                    }
                }
            }

            // Section Title & Item Count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (browseMode) {
                            BrowseCategoryMode.BY_SEAFOOD_TYPE -> Strings.browseBySeafoodType(currentLanguage)
                            BrowseCategoryMode.BY_PREP_STYLE -> Strings.browseByPrepStyle(currentLanguage)
                            BrowseCategoryMode.BY_OCCASION -> Strings.browseByOccasion(currentLanguage)
                            BrowseCategoryMode.BY_PRICE_RANGE -> Strings.browseByPrice(currentLanguage)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${foodItems.size} " + (if (currentLanguage == Language.BURMESE) "မျိုး" else "items"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Food Items List
            if (foodItems.isEmpty()) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "အစားအစာ ရှာမတွေ့ပါ" else "No food items found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(foodItems, key = { it.id }) { item ->
                    FoodItemCard(
                        foodItem = item,
                        currentLanguage = currentLanguage,
                        onItemClick = { selectedFoodForDetail = item },
                        onQuickAdd = { onQuickAddToCart(item) }
                    )
                }
            }

            // Bottom space for floating cart
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Floating Cart / Proceed Bar (prominently visible whenever items are selected in cart)
        AnimatedVisibility(
            visible = cartItemCount > 0,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 10.dp,
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onOpenCart() }
                    .testTag("floating_proceed_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Count Badge & Subtotal
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = "$cartItemCount",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = Strings.cart(currentLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                            Text(
                                text = Strings.mmkCurrency(currentLanguage, cartSubtotalMMK),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    // Right: Explicit "Proceed" Action Button
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onOpenCart() }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .testTag("proceed_action_button")
                        ) {
                            Text(
                                text = Strings.proceed(currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Proceed",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Food Detail / Customization Bottom Sheet
        selectedFoodForDetail?.let { food ->
            FoodDetailModalBottomSheet(
                foodItem = food,
                currentLanguage = currentLanguage,
                selectedRegionName = selectedRegionName,
                reviews = reviews,
                onSubmitReview = { rating, comment ->
                    onSubmitReview?.invoke(food.id, food.nameEn, rating, comment)
                },
                onAddToCart = { qty, spice, addOns, notes, coldStorage, specialPrep, fulfillment ->
                    onCustomizedAddToCart(food, qty, spice, addOns, notes, coldStorage, specialPrep, fulfillment)
                },
                onProceedToCheckout = { qty, spice, addOns, notes, coldStorage, specialPrep, fulfillment ->
                    onCustomizedAddToCart(food, qty, spice, addOns, notes, coldStorage, specialPrep, fulfillment)
                    onOpenCart()
                },
                onDismiss = { selectedFoodForDetail = null }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderModifier = if (!isSelected) {
        Modifier.border(
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shape = RoundedCornerShape(12.dp)
        )
    } else {
        Modifier
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .then(borderModifier)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FeaturedSeafoodCard(
    foodItem: FoodItem,
    currentLanguage: Language,
    onItemClick: () -> Unit,
    onQuickAdd: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .width(185.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onItemClick() }
            .testTag("featured_card_${foodItem.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Photo with Rating Badge and Prep Style Tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (foodItem.imageUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = foodItem.imageUrl,
                        contentDescription = foodItem.nameEn,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(foodItem.iconEmoji, fontSize = 32.sp)
                            }
                        },
                        error = {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(foodItem.iconEmoji, fontSize = 32.sp)
                            }
                        }
                    )
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(foodItem.iconEmoji, fontSize = 40.sp)
                    }
                }

                // Rating overlay
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${foodItem.rating}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Prep style tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = foodItem.prepStyle.displayName(currentLanguage),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Info & Price + Quick Add
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = if (currentLanguage == Language.BURMESE) foodItem.nameMy else foodItem.nameEn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${Strings.mmkCurrency(currentLanguage, foodItem.priceMMK)} / ${if (currentLanguage == Language.BURMESE) foodItem.unitMy else foodItem.unitEn}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = foodItem.occasion.displayName(currentLanguage),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable { onQuickAdd() }
                            .testTag("featured_add_${foodItem.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.Language
import com.example.model.Strings
import com.example.ui.components.FoodDetailModalBottomSheet
import com.example.ui.components.FoodItemCard
import com.example.ui.components.LanguageToggle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    foodItems: List<FoodItem>,
    searchQuery: String,
    selectedCategory: FoodCategory,
    currentLanguage: Language,
    cartItemCount: Int,
    cartSubtotalMMK: Int,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (FoodCategory) -> Unit,
    onLanguageToggle: () -> Unit,
    onQuickAddToCart: (FoodItem) -> Unit,
    onCustomizedAddToCart: (FoodItem, Int, String, List<com.example.model.FoodAddOn>, String) -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFoodForDetail by remember { mutableStateOf<FoodItem?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // High Density Header with Restaurant Icon Badge and Language Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = "App Logo",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = Strings.appTitle(currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 18.sp,
                                lineHeight = 20.sp
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

                    LanguageToggle(
                        currentLanguage = currentLanguage,
                        onLanguageToggle = onLanguageToggle
                    )
                }
            }

            // High Density Hero Promotion Banner with Crisp Outline & Photo
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "မြန်မာ့အရသာစစ်စစ်" else "AUTHENTIC MYANMAR TASTE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "လျင်မြန်စွာ ပို့ဆောင်ပေးပါသည်" else "Fresh, Fast & Minimalist Ordering",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "အကောင့်ဖွင့်ရန်မလိုဘဲ အမည်နှင့် ဖုန်းနံပါတ်ဖြင့် တိုက်ရိုက်မှာယူနိုင်ပါသည်" else "Zero registration. Direct 1-tap checkout with name & phone.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            SubcomposeAsyncImage(
                                model = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=500&auto=format&fit=crop&q=80",
                                contentDescription = "Hero Myanmar Food",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                loading = {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Text(text = "🍲", fontSize = 32.sp)
                                    }
                                },
                                error = {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Text(text = "🍲", fontSize = 32.sp)
                                    }
                                }
                            )
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

            // Categories Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip(
                        title = Strings.categoryAll(currentLanguage),
                        icon = "✨",
                        isSelected = selectedCategory == FoodCategory.ALL,
                        onClick = { onCategorySelected(FoodCategory.ALL) }
                    )
                    CategoryChip(
                        title = Strings.categoryNoodles(currentLanguage),
                        icon = "🍜",
                        isSelected = selectedCategory == FoodCategory.NOODLES,
                        onClick = { onCategorySelected(FoodCategory.NOODLES) }
                    )
                    CategoryChip(
                        title = Strings.categoryRice(currentLanguage),
                        icon = "🍛",
                        isSelected = selectedCategory == FoodCategory.RICE,
                        onClick = { onCategorySelected(FoodCategory.RICE) }
                    )
                    CategoryChip(
                        title = Strings.categorySalads(currentLanguage),
                        icon = "🥗",
                        isSelected = selectedCategory == FoodCategory.SALADS,
                        onClick = { onCategorySelected(FoodCategory.SALADS) }
                    )
                    CategoryChip(
                        title = Strings.categoryDrinks(currentLanguage),
                        icon = "🧋",
                        isSelected = selectedCategory == FoodCategory.DRINKS,
                        onClick = { onCategorySelected(FoodCategory.DRINKS) }
                    )
                }
            }

            // Section Title
            item {
                Text(
                    text = Strings.popularDishes(currentLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
                Spacer(modifier = Modifier.height(84.dp))
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
                onAddToCart = { qty, spice, addOns, notes ->
                    onCustomizedAddToCart(food, qty, spice, addOns, notes)
                },
                onProceedToCheckout = { qty, spice, addOns, notes ->
                    onCustomizedAddToCart(food, qty, spice, addOns, notes)
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

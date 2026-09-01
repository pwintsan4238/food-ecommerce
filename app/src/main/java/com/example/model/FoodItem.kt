package com.example.model

enum class FoodCategory(val id: String) {
    ALL("all"),
    NOODLES("noodles"),
    RICE("rice"),
    SALADS("salads"),
    DRINKS("drinks")
}

data class FoodAddOn(
    val id: String,
    val nameEn: String,
    val nameMy: String,
    val priceMMK: Int
)

data class FoodItem(
    val id: String,
    val nameEn: String,
    val nameMy: String,
    val descriptionEn: String,
    val descriptionMy: String,
    val priceMMK: Int,
    val category: FoodCategory,
    val rating: Double,
    val prepTimeMin: Int,
    val isPopular: Boolean = false,
    val isSpicy: Boolean = false,
    val isVegetarian: Boolean = false,
    val availableAddOns: List<FoodAddOn> = emptyList(),
    val iconEmoji: String = "🍜",
    val imageUrl: String = ""
)

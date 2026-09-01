package com.example.model

data class CartItem(
    val foodItem: FoodItem,
    val quantity: Int = 1,
    val selectedSpiceLevel: String = "Normal",
    val selectedAddOns: List<FoodAddOn> = emptyList(),
    val specialNotes: String = ""
) {
    val unitPriceWithAddOns: Int
        get() = foodItem.priceMMK + selectedAddOns.sumOf { it.priceMMK }

    val totalPrice: Int
        get() = unitPriceWithAddOns * quantity
}

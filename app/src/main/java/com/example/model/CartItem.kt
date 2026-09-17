package com.example.model

data class CartItem(
    val foodItem: FoodItem,
    val quantity: Int = 1,
    val selectedSpiceLevel: String = "Normal",
    val selectedAddOns: List<FoodAddOn> = emptyList(),
    val specialNotes: String = "",
    val includeColdStorage: Boolean = false,
    val includeSpecialPrep: Boolean = false,
    val selectedFulfillment: String = if (foodItem.allowDelivery) "Delivery" else "Self Pickup"
) {
    val fulfillmentPreference: String
        get() = selectedFulfillment
    val additionalCostPerUnit: Int
        get() = (if (includeColdStorage) foodItem.coldStorageFeeMMK else 0) +
                (if (includeSpecialPrep) foodItem.specialPrepFeeMMK else 0)

    val unitPriceWithAddOns: Int
        get() = foodItem.priceMMK + selectedAddOns.sumOf { it.priceMMK } + additionalCostPerUnit

    val subtotalBeforeDiscount: Int
        get() = unitPriceWithAddOns * quantity

    val discountInfo: Pair<Int, String?>
        get() = foodItem.calculateDiscount(quantity)

    val discountAmount: Int
        get() = discountInfo.first

    val totalPrice: Int
        get() = (subtotalBeforeDiscount - discountAmount).coerceAtLeast(0)
}


package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.DiningOccasion
import com.example.model.FoodAddOn
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.PrepStyle

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val sku: String = "",
    val brand: String = "တိမ်တမန်ပင်လယ်စာ (Taim Ta Man)",
    val nameEn: String,
    val nameMy: String,
    val descriptionEn: String,
    val descriptionMy: String,
    val priceMMK: Int,
    val originalPriceMMK: Int = 0,
    val specifications: String = "",
    val variants: String = "",
    val unitEn: String = "per 1 kg",
    val unitMy: String = "၁ ကီလို",
    val category: String = FoodCategory.FISH.name,
    val prepStyle: String = PrepStyle.READY_TO_COOK.name,
    val occasion: String = DiningOccasion.FAMILY_DINNER.name,
    val rating: Double = 4.9,
    val prepTimeMin: Int = 15,
    val isPopular: Boolean = false,
    val isSpicy: Boolean = false,
    val isVegetarian: Boolean = false,
    val isPremium: Boolean = false,
    val isAvailable: Boolean = true,
    val iconEmoji: String = "🐟",
    val imageUrl: String = "",
    val allowSelfPickup: Boolean = true,
    val allowDelivery: Boolean = true,
    val allowedRegions: String = "",
    val minPurchaseQty: Int = 1,
    val minPurchaseAmountMMK: Int = 0,
    val coldStorageFeeMMK: Int = 2500,
    val coldStorageTitleEn: String = "Ice Box & Cold Storage Packaging",
    val coldStorageTitleMy: String = "ရေခဲပုံးနှင့် အအေးထိန်းထုပ်ပိုးမှု",
    val specialPrepFeeMMK: Int = 1500,
    val specialPrepTitleEn: String = "Special Cleaning & Vacuum Sealing",
    val specialPrepTitleMy: String = "အထူးဆေးကြော သန့်စင် လေလုံထုပ်ပိုးမှု",
    val discountMinQty: Int = 0,
    val discountPercentByQty: Int = 0,
    val discountMinAmountMMK: Int = 0,
    val discountPercentByAmount: Int = 0,
    val isArchived: Boolean = false,
    val stockQuantity: Int = 50
) {
    fun toFoodItem(commonAddOns: List<FoodAddOn> = emptyList()): FoodItem {
        val cat = try {
            FoodCategory.valueOf(category)
        } catch (e: Exception) {
            FoodCategory.FISH
        }
        val prep = try {
            PrepStyle.valueOf(prepStyle)
        } catch (e: Exception) {
            PrepStyle.READY_TO_COOK
        }
        val occ = try {
            DiningOccasion.valueOf(occasion)
        } catch (e: Exception) {
            DiningOccasion.FAMILY_DINNER
        }
        return FoodItem(
            id = id,
            sku = sku,
            brand = brand.ifBlank { "တိမ်တမန်ပင်လယ်စာ (Taim Ta Man)" },
            nameEn = nameEn,
            nameMy = nameMy,
            descriptionEn = descriptionEn,
            descriptionMy = descriptionMy,
            priceMMK = priceMMK,
            originalPriceMMK = originalPriceMMK,
            specifications = specifications,
            variants = variants,
            unitEn = unitEn,
            unitMy = unitMy,
            category = cat,
            prepStyle = prep,
            occasion = occ,
            rating = rating,
            prepTimeMin = prepTimeMin,
            isPopular = isPopular,
            isSpicy = isSpicy,
            isVegetarian = isVegetarian,
            isPremium = isPremium,
            isAvailable = isAvailable,
            availableAddOns = commonAddOns,
            iconEmoji = iconEmoji.ifBlank { cat.iconEmoji },
            imageUrl = imageUrl,
            allowSelfPickup = allowSelfPickup,
            allowDelivery = allowDelivery,
            allowedRegions = allowedRegions,
            minPurchaseQty = minPurchaseQty,
            minPurchaseAmountMMK = minPurchaseAmountMMK,
            coldStorageFeeMMK = coldStorageFeeMMK,
            coldStorageTitleEn = coldStorageTitleEn,
            coldStorageTitleMy = coldStorageTitleMy,
            specialPrepFeeMMK = specialPrepFeeMMK,
            specialPrepTitleEn = specialPrepTitleEn,
            specialPrepTitleMy = specialPrepTitleMy,
            discountMinQty = discountMinQty,
            discountPercentByQty = discountPercentByQty,
            discountMinAmountMMK = discountMinAmountMMK,
            discountPercentByAmount = discountPercentByAmount,
            isArchived = isArchived,
            stockQuantity = stockQuantity
        )
    }

    companion object {
        fun fromFoodItem(item: FoodItem): ProductEntity {
            return ProductEntity(
                id = item.id,
                sku = item.sku,
                brand = item.brand,
                nameEn = item.nameEn,
                nameMy = item.nameMy,
                descriptionEn = item.descriptionEn,
                descriptionMy = item.descriptionMy,
                priceMMK = item.priceMMK,
                originalPriceMMK = item.originalPriceMMK,
                specifications = item.specifications,
                variants = item.variants,
                unitEn = item.unitEn,
                unitMy = item.unitMy,
                category = item.category.name,
                prepStyle = item.prepStyle.name,
                occasion = item.occasion.name,
                rating = item.rating,
                prepTimeMin = item.prepTimeMin,
                isPopular = item.isPopular,
                isSpicy = item.isSpicy,
                isVegetarian = item.isVegetarian,
                isPremium = item.isPremium,
                isAvailable = item.isAvailable,
                iconEmoji = item.iconEmoji,
                imageUrl = item.imageUrl,
                allowSelfPickup = item.allowSelfPickup,
                allowDelivery = item.allowDelivery,
                allowedRegions = item.allowedRegions,
                minPurchaseQty = item.minPurchaseQty,
                minPurchaseAmountMMK = item.minPurchaseAmountMMK,
                coldStorageFeeMMK = item.coldStorageFeeMMK,
                coldStorageTitleEn = item.coldStorageTitleEn,
                coldStorageTitleMy = item.coldStorageTitleMy,
                specialPrepFeeMMK = item.specialPrepFeeMMK,
                specialPrepTitleEn = item.specialPrepTitleEn,
                specialPrepTitleMy = item.specialPrepTitleMy,
                discountMinQty = item.discountMinQty,
                discountPercentByQty = item.discountPercentByQty,
                discountMinAmountMMK = item.discountMinAmountMMK,
                discountPercentByAmount = item.discountPercentByAmount,
                isArchived = item.isArchived,
                stockQuantity = item.stockQuantity
            )
        }
    }
}

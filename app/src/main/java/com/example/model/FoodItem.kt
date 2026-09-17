package com.example.model

enum class FoodCategory(val id: String, val iconEmoji: String) {
    ALL("all", "✨"),
    FISH("fish", "🐟"),
    SHRIMP("shrimp", "🦐"),
    CRAB("crab", "🦀"),
    LOBSTER("lobster", "🦞"),
    SQUID("squid", "🦑"),
    OCTOPUS("octopus", "🐙"),
    SHELLFISH("shellfish", "🦪");

    fun displayName(lang: Language): String = when (this) {
        ALL -> if (lang == Language.BURMESE) "အားလုံး" else "All"
        FISH -> if (lang == Language.BURMESE) "ငါး" else "Fish"
        SHRIMP -> if (lang == Language.BURMESE) "ပုစွန်" else "Shrimp"
        CRAB -> if (lang == Language.BURMESE) "ကဏန်း" else "Crab"
        LOBSTER -> if (lang == Language.BURMESE) "ပုစွန်ထုပ်" else "Lobster"
        SQUID -> if (lang == Language.BURMESE) "ပြည်ကြီးငါး" else "Squid"
        OCTOPUS -> if (lang == Language.BURMESE) "ရေဘဝဲ" else "Octopus"
        SHELLFISH -> if (lang == Language.BURMESE) "အခွံပါပင်လယ်စာ" else "Shellfish"
    }

    fun title(lang: Language): String = displayName(lang)
}

enum class PrepStyle(val id: String) {
    ALL("all"),
    READY_TO_COOK("ready_to_cook"),
    CLEANED_SLICED("cleaned_sliced"),
    BONELESS("boneless"),
    PEELED_DEVEINED("peeled_deveined"),
    MARINATED("marinated");

    fun displayName(lang: Language): String = when (this) {
        ALL -> if (lang == Language.BURMESE) "အားလုံး" else "All"
        READY_TO_COOK -> if (lang == Language.BURMESE) "ချက်ရန်အသင့်" else "Ready to Cook"
        CLEANED_SLICED -> if (lang == Language.BURMESE) "သန့်စင်ပြီး အတုံးလှီးထားသော" else "Cleaned & Sliced"
        BONELESS -> if (lang == Language.BURMESE) "အရိုးမပါ" else "Boneless / Fillet"
        PEELED_DEVEINED -> if (lang == Language.BURMESE) "အခွံခွာပြီး အူကြောဖယ်ထားသော" else "Peeled & Deveined"
        MARINATED -> if (lang == Language.BURMESE) "အရသာနှပ်ပြီးသား" else "Marinated"
    }

    fun title(lang: Language): String = displayName(lang)
}

enum class DiningOccasion(val id: String) {
    ALL("all"),
    FAMILY_DINNER("family_dinner"),
    BBQ_GRILL("bbq_grill"),
    HOTPOT("hotpot"),
    PARTY_EVENT("party_event"),
    PREMIUM("premium");

    fun displayName(lang: Language): String = when (this) {
        ALL -> if (lang == Language.BURMESE) "အားလုံး" else "All"
        FAMILY_DINNER -> if (lang == Language.BURMESE) "မိသားစုညစာ" else "Family Dinner"
        BBQ_GRILL -> if (lang == Language.BURMESE) "BBQ / မီးကင်" else "BBQ / Grill"
        HOTPOT -> if (lang == Language.BURMESE) "ဟော့ပေါ့" else "Hotpot"
        PARTY_EVENT -> if (lang == Language.BURMESE) "ပါတီနှင့် ဧည့်ခံပွဲ" else "Party & Event"
        PREMIUM -> if (lang == Language.BURMESE) "အဆင့်မြင့် ပင်လယ်စာများ" else "Premium Seafood"
    }

    fun title(lang: Language): String = displayName(lang)
}

enum class PriceRangeFilter(val id: String) {
    ALL("all"),
    UNDER_30K("under_30k"),
    FROM_30K_TO_50K("30k_50k"),
    FROM_50K_TO_100K("50k_100k"),
    FROM_100K_TO_200K("100k_200k"),
    ABOVE_200K("above_200k"),
    PREMIUM("premium");

    fun displayName(lang: Language): String = when (this) {
        ALL -> if (lang == Language.BURMESE) "အားလုံး" else "All"
        UNDER_30K -> if (lang == Language.BURMESE) "၃၀,၀၀၀ ကျပ်အောက်" else "Under 30,000 Ks"
        FROM_30K_TO_50K -> if (lang == Language.BURMESE) "၃၀,၀၀၀ – ၅၀,၀၀၀ ကျပ်" else "30,000 – 50,000 Ks"
        FROM_50K_TO_100K -> if (lang == Language.BURMESE) "၅၀,၀၀၀ – ၁၀၀,၀၀၀ ကျပ်" else "50,000 – 100,000 Ks"
        FROM_100K_TO_200K -> if (lang == Language.BURMESE) "၁၀၀,၀၀၀ – ၂၀၀,၀၀၀ ကျပ်" else "100,000 – 200,000 Ks"
        ABOVE_200K -> if (lang == Language.BURMESE) "၂၀၀,၀၀၀ ကျပ်အထက်" else "Above 200,000 Ks"
        PREMIUM -> if (lang == Language.BURMESE) "အဆင့်မြင့် ပင်လယ်စာများ" else "Premium Seafood"
    }

    fun matches(priceMMK: Int, isPremium: Boolean): Boolean = when (this) {
        ALL -> true
        UNDER_30K -> priceMMK < 30_000
        FROM_30K_TO_50K -> priceMMK in 30_000..50_000
        FROM_50K_TO_100K -> priceMMK in 50_000..100_000
        FROM_100K_TO_200K -> priceMMK in 100_000..200_000
        ABOVE_200K -> priceMMK > 200_000
        PREMIUM -> isPremium || priceMMK >= 100_000
    }
}

enum class BrowseCategoryMode {
    BY_SEAFOOD_TYPE,
    BY_PREP_STYLE,
    BY_OCCASION,
    BY_PRICE_RANGE;

    fun displayName(lang: Language): String = when (this) {
        BY_SEAFOOD_TYPE -> if (lang == Language.BURMESE) "ပင်လယ်စာအမျိုးအစား" else "Seafood Type"
        BY_PREP_STYLE -> if (lang == Language.BURMESE) "ပြင်ဆင်ပြီးပုံစံ" else "Prep Style"
        BY_OCCASION -> if (lang == Language.BURMESE) "စားသုံးမည့်အခါ" else "Occasion"
        BY_PRICE_RANGE -> if (lang == Language.BURMESE) "ဈေးနှုန်းအလိုက်" else "Price Range"
    }

    fun icon(): String = when (this) {
        BY_SEAFOOD_TYPE -> "🦐"
        BY_PREP_STYLE -> "🔪"
        BY_OCCASION -> "🍽️"
        BY_PRICE_RANGE -> "💰"
    }
}

data class FoodAddOn(
    val id: String,
    val nameEn: String,
    val nameMy: String,
    val priceMMK: Int
)

data class FoodItem(
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
    val category: FoodCategory,
    val prepStyle: PrepStyle = PrepStyle.READY_TO_COOK,
    val occasion: DiningOccasion = DiningOccasion.FAMILY_DINNER,
    val rating: Double = 4.9,
    val prepTimeMin: Int = 15,
    val isPopular: Boolean = false,
    val isSpicy: Boolean = false,
    val isVegetarian: Boolean = false,
    val isPremium: Boolean = false,
    val isAvailable: Boolean = true,
    val availableAddOns: List<FoodAddOn> = emptyList(),
    val iconEmoji: String = "🐟",
    val imageUrl: String = "",
    // Fulfillment switches
    val allowSelfPickup: Boolean = true,
    val allowDelivery: Boolean = true,
    // Limit to cities and regions (empty means all regions allowed)
    val allowedRegions: String = "",
    // Minimum purchase constraints
    val minPurchaseQty: Int = 1,
    val minPurchaseAmountMMK: Int = 0,
    // Additional costs (custom fields)
    val coldStorageFeeMMK: Int = 2500,
    val coldStorageTitleEn: String = "Ice Box & Cold Storage Packaging",
    val coldStorageTitleMy: String = "ရေခဲပုံးနှင့် အအေးထိန်းထုပ်ပိုးမှု",
    val specialPrepFeeMMK: Int = 1500,
    val specialPrepTitleEn: String = "Special Cleaning & Vacuum Sealing",
    val specialPrepTitleMy: String = "အထူးဆေးကြော သန့်စင် လေလုံထုပ်ပိုးမှု",
    // Discount options (bulk qty or spend over MMK)
    val discountMinQty: Int = 0,
    val discountPercentByQty: Int = 0,
    val discountMinAmountMMK: Int = 0,
    val discountPercentByAmount: Int = 0,
    // Inventory and Archiving (soft delete)
    val isArchived: Boolean = false,
    val stockQuantity: Int = 50
) {
    val isOutOfStock: Boolean
        get() = stockQuantity <= 0 || !isAvailable

    val effectiveSku: String
        get() = sku.ifBlank { id.replace("seafood_", "").replace("_", "-").uppercase() }

    val hasOriginalPriceDiscount: Boolean
        get() = originalPriceMMK > priceMMK

    val originalPriceDiscountPercent: Int
        get() = if (hasOriginalPriceDiscount) {
            (((originalPriceMMK - priceMMK).toDouble() / originalPriceMMK) * 100).toInt()
        } else 0
    fun displayName(lang: Language): String = if (lang == Language.BURMESE) nameMy else nameEn
    fun displayUnit(lang: Language): String = if (lang == Language.BURMESE) unitMy else unitEn
    fun title(lang: Language): String = displayName(lang)

    fun coldStorageTitle(lang: Language): String =
        if (lang == Language.BURMESE) coldStorageTitleMy else coldStorageTitleEn

    fun specialPrepTitle(lang: Language): String =
        if (lang == Language.BURMESE) specialPrepTitleMy else specialPrepTitleEn

    /**
     * Calculates discount amount and discount reason based on quantity and raw price.
     */
    fun calculateDiscount(qty: Int): Pair<Int, String?> {
        val rawAmount = priceMMK * qty
        var percent = 0
        var reason: String? = null

        if (discountMinQty in 1..qty && discountPercentByQty > 0) {
            percent = discountPercentByQty
            reason = "$discountPercentByQty% Off (Bulk $discountMinQty+ units)"
        }
        if (discountMinAmountMMK > 0 && rawAmount >= discountMinAmountMMK && discountPercentByAmount > percent) {
            percent = discountPercentByAmount
            reason = "$discountPercentByAmount% Off (Over ${discountMinAmountMMK} MMK)"
        }

        val discountMMK = if (percent > 0) (rawAmount * percent) / 100 else 0
        return Pair(discountMMK, reason)
    }

    /**
     * Checks whether a region/city name is allowed for this product.
     */
    fun isRegionAllowed(regionName: String?): Boolean {
        if (allowedRegions.isBlank() || allowedRegions.equals("All", ignoreCase = true) || allowedRegions.equals("All Regions", ignoreCase = true)) {
            return true
        }
        if (regionName.isNullOrBlank()) return true
        val list = allowedRegions.split(",").map { it.trim().lowercase() }
        val target = regionName.trim().lowercase()
        return list.any { target.contains(it) || it.contains(target) }
    }
}

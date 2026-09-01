package com.example.data

import com.example.model.FoodAddOn
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.MyanmarTownship
import com.example.model.MyanmarTownshipsData
import kotlinx.coroutines.flow.Flow

class FoodRepository(private val orderDao: OrderDao) {

    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val userProfile: Flow<UserProfileEntity?> = orderDao.getUserProfile()

    suspend fun saveOrder(order: OrderEntity) = orderDao.insertOrder(order)
    suspend fun updateOrderStatus(orderId: String, status: String) = orderDao.updateOrderStatus(orderId, status)
    suspend fun deleteOrder(orderId: String) = orderDao.deleteOrder(orderId)
    suspend fun saveProfile(profile: UserProfileEntity) = orderDao.saveUserProfile(profile)

    fun getTownshipById(id: String): MyanmarTownship {
        return MyanmarTownshipsData.allTownships.find { it.id == id } ?: MyanmarTownshipsData.defaultTownship
    }

    val menuItems: List<FoodItem> = listOf(
        FoodItem(
            id = "food_mohinga",
            nameEn = "Royal Mohinga Soup",
            nameMy = "နန်းတော် မုန့်ဟင်းခါး",
            descriptionEn = "Myanmar's national dish: Lemongrass catfish broth, rice vermicelli, crunchy split-pea fritters, and boiled egg.",
            descriptionMy = "မြန်မာ့ဂုဏ်ဆောင် ငါးခူအနှစ်၊ စပါးလင်နံ့သင်းသင်း၊ ပဲကြော်ကြွပ်ကြွပ်၊ ဘဲဥတို့ဖြင့် သန့်ရှင်းလတ်ဆတ်စွာ ချက်ပြုတ်ထားပါသည်။",
            priceMMK = 3500,
            category = FoodCategory.NOODLES,
            rating = 4.9,
            prepTimeMin = 15,
            isPopular = true,
            isSpicy = false,
            iconEmoji = "🍲",
            imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?auto=format&fit=crop&w=800&q=80",
            availableAddOns = listOf(
                FoodAddOn("addon_egg", "Boiled Duck Egg", "ဘဲဥပြုတ်", 500),
                FoodAddOn("addon_pekyaw", "Crispy Pea Fritter (Pe Kyaw)", "ပဲကြော်အပို", 400),
                FoodAddOn("addon_fishcake", "Fried Fish Cake (Nga Hpe)", "ငါးဖယ်ကြော်", 800)
            )
        ),
        FoodItem(
            id = "food_shan_noodles",
            nameEn = "Authentic Shan Sticky Noodles",
            nameMy = "ရှမ်းခေါက်ဆွဲ (ရှမ်းပြည်စစ်စစ်)",
            descriptionEn = "Soft Shan rice noodles tossed with savory spiced chicken, roasted crushed peanuts, chili oil, and pickled greens.",
            descriptionMy = "ရှမ်းပြည်နယ်ထွက် ခေါက်ဆွဲစေးစေး၊ နူးညံ့သောကြက်သားကြော်၊ မြေပဲလှော်မွှေးမွှေး၊ မုန်ညင်းချဉ်တို့ဖြင့် အထူးစီမံထားပါသည်။",
            priceMMK = 3800,
            category = FoodCategory.NOODLES,
            rating = 4.9,
            prepTimeMin = 15,
            isPopular = true,
            isSpicy = true,
            iconEmoji = "🍜",
            imageUrl = "https://images.unsplash.com/photo-1617093727343-374698b1b08d?auto=format&fit=crop&w=800&q=80",
            availableAddOns = listOf(
                FoodAddOn("addon_extra_chicken", "Extra Minced Chicken", "ကြက်သားပို", 1000),
                FoodAddOn("addon_pickled_greens", "Extra Pickled Mustard Greens", "မုန်ညင်းချဉ်ပို", 300),
                FoodAddOn("addon_tofu_fritter", "Shan Tofu Fritter", "တို့ဟူးကြော်", 500)
            )
        ),
        FoodItem(
            id = "food_laphet_thoke",
            nameEn = "Traditional Tea Leaf Salad (Laphet Thoke)",
            nameMy = "ရွှေလက်ဖက်သုပ် (အကြော်စုံ)",
            descriptionEn = "Organic fermented tea leaves mixed with crispy fried garlic, roasted peanuts, broad beans, toasted sesame, tomatoes, and lime.",
            descriptionMy = "ပလောင်လက်ဖက်စစ်စစ်၊ ကြက်သွန်ဖြူကြော်၊ ပဲကြော်စုံ၊ နှမ်းလှော်၊ ခရမ်းချဉ်သီးတို့ဖြင့် ချဉ်စပ်မွှေးကြိုင်စွာ သုပ်ထားပါသည်။",
            priceMMK = 3200,
            category = FoodCategory.SALADS,
            rating = 4.8,
            prepTimeMin = 10,
            isPopular = true,
            isSpicy = true,
            isVegetarian = true,
            iconEmoji = "🥗",
            imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=800&q=80",
            availableAddOns = listOf(
                FoodAddOn("addon_extra_beans", "Extra Crispy Beans", "အကြော်စုံပို", 500),
                FoodAddOn("addon_dry_shrimp", "Dried Prawn Flakes", "ပုစွန်ခြောက်မှုန့်", 800),
                FoodAddOn("addon_fresh_chili", "Raw Green Chilies", "ငရုတ်သီးစိမ်း", 200)
            )
        ),
        FoodItem(
            id = "food_danbauk",
            nameEn = "Golden Danbauk Chicken Biryani",
            nameMy = "ရွှေကြက်သား ဒံပေါက်",
            descriptionEn = "Fragrant spiced basmati rice slow-cooked with tender chicken thigh, roasted cashews, sweet raisins, and sour mango pickle.",
            descriptionMy = "အမွှေးနံ့သာစုံလင်စွာဖြင့် နူးအိနေသော ကြက်ပေါင်ကြီး၊ သီဟိုဠ်စေ့၊ စပျစ်ခြောက်၊ သရက်သီးသနပ်တို့ဖြင့် တွဲဖက်ထားပါသည်။",
            priceMMK = 5800,
            category = FoodCategory.RICE,
            rating = 4.9,
            prepTimeMin = 20,
            isPopular = true,
            isSpicy = false,
            iconEmoji = "🍗",
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=800&q=80",
            availableAddOns = listOf(
                FoodAddOn("addon_mango_pickle", "Extra Mango Pickle", "သရက်သီးသနပ်ပို", 400),
                FoodAddOn("addon_soup", "Spiced Lentil Soup", "ပဲဟင်းရည်ပို", 300),
                FoodAddOn("addon_egg", "Boiled Egg", "ကြက်ဥပြုတ်", 500)
            )
        ),
        FoodItem(
            id = "food_ohn_no_khao_swe",
            nameEn = "Coconut Chicken Noodles (Ohn No Khao Swé)",
            nameMy = "အုန်းနို့ခေါက်ဆွဲ အထူး",
            descriptionEn = "Rich coconut chicken curry broth with wheat noodles, crispy onion fritters, boiled egg, chili flakes, and fresh lime wedge.",
            descriptionMy = "အုန်းနို့ပျစ်ပျစ်၊ ကြက်သားနူးနူး၊ ကြက်သွန်နီကြော်ကြွပ်ကြွပ်၊ ဘဲဥတို့ဖြင့် ချိုစိမ့်မွှေးကြိုင်သော ခေါက်ဆွဲဟင်းလျာ။",
            priceMMK = 4200,
            category = FoodCategory.NOODLES,
            rating = 4.8,
            prepTimeMin = 15,
            isPopular = true,
            isSpicy = false,
            iconEmoji = "🍲",
            imageUrl = "https://images.unsplash.com/photo-1594041680534-e8c8cdebd659?auto=format&fit=crop&w=800&q=80",
            availableAddOns = listOf(
                FoodAddOn("addon_crispy_onion", "Crispy Onion Fritter", "ကြက်သွန်ကြော်ပို", 400),
                FoodAddOn("addon_egg", "Boiled Egg", "ဘဲဥပြုတ်", 500)
            )
        ),
        FoodItem(
            id = "food_kyay_oh",
            nameEn = "Special Kyay Oh Soup / Dry",
            nameMy = "ကြေးအိုး အထူး (အရည်/အသုပ်)",
            descriptionEn = "Savory vermicelli bowl with homemade minced meatballs, quail eggs, pork intestines, mustard greens, and garlic chili dipping sauce.",
            descriptionMy = "ကြာဇံ၊ အသားလုံး၊ ငုံးဥ၊ အသီးအရွက်စုံလင်စွာဖြင့် မီးဖိုချောင်မှ ပူပူနွေးနွေး ပြင်ဆင်ပေးထားပါသည်။",
            priceMMK = 4500,
            category = FoodCategory.NOODLES,
            rating = 4.7,
            prepTimeMin = 18,
            isPopular = false,
            isSpicy = false,
            iconEmoji = "🍜",
            imageUrl = "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?auto=format&fit=crop&w=800&q=80",
            availableAddOns = listOf(
                FoodAddOn("addon_quail_eggs", "Quail Eggs (3 pcs)", "ငုံးဥ (၃ လုံး)", 600),
                FoodAddOn("addon_meatballs", "Extra Meatballs", "အသားလုံးပို", 1000)
            )
        ),
        FoodItem(
            id = "food_samosa_thoke",
            nameEn = "Crispy Samosa Thoke Salad",
            nameMy = "စမူဆာသုပ် (ဟင်းရည်စမ်း)",
            descriptionEn = "Freshly fried spiced potato samosas sliced and drowned in tangy chickpea broth with shredded cabbage, shallots, and fresh mint.",
            descriptionMy = "အာလူးစမူဆာကြွပ်ကြွပ်၊ ကုလားပဲဟင်းရည်၊ ဂေါ်ဖီ၊ ပူဒီနာ၊ ငရုတ်သီးစိမ်းတို့ဖြင့် သုပ်ထားသော နာမည်ကြီး လမ်းဘေးစာ။",
            priceMMK = 2800,
            category = FoodCategory.SALADS,
            rating = 4.7,
            prepTimeMin = 10,
            isPopular = false,
            isSpicy = true,
            isVegetarian = true,
            iconEmoji = "🥟",
            imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=800&q=80",
            availableAddOns = listOf(
                FoodAddOn("addon_extra_samosa", "Extra Samosa (1 pc)", "စမူဆာ ၁ ခုပို", 600),
                FoodAddOn("addon_falafel", "Chickpea Falafel (Baya Kyaw)", "ဘယာကြော်", 500)
            )
        ),
        FoodItem(
            id = "food_shan_tofu_kyaw",
            nameEn = "Crispy Shan Tofu Fritters",
            nameMy = "ရှမ်းတို့ဟူးကြော် ပူပူစပ်စပ်",
            descriptionEn = "Crispy golden exterior with creamy melt-in-your-mouth interior, served with fiery garlic-tamarind chili dip.",
            descriptionMy = "အပြင်ကြွပ် အတွင်းနူးညံ့သော ရှမ်းတို့ဟူးစစ်စစ်ကြော်နှင့် ချဉ်စပ်မန်ကျည်းသီး ငရုတ်ရည်။",
            priceMMK = 2500,
            category = FoodCategory.SALADS,
            rating = 4.8,
            prepTimeMin = 12,
            isPopular = false,
            isSpicy = true,
            isVegetarian = true,
            iconEmoji = "🧆",
            imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=800&q=80",
            availableAddOns = listOf(
                FoodAddOn("addon_dip", "Extra Tamarind Garlic Dip", "မန်ကျည်းအချဉ်ရည်ပို", 300)
            )
        ),
        FoodItem(
            id = "food_shwe_tea",
            nameEn = "Traditional Shwe Myanmar Milk Tea",
            nameMy = "ရွှေလက်ဖက်ရည် (ချိုစိမ့်)",
            descriptionEn = "Strong brewed Myanmar black tea pulled to creamy perfection with condensed and evaporated milk.",
            descriptionMy = "လက်ဖက်ရည်ချိုစိမ့် မွှေးမွှေးလေး၊ ရိုးရာလက်ရာစစ်စစ် အအေး/အပူ ရွေးချယ်နိုင်ပါသည်။",
            priceMMK = 1800,
            category = FoodCategory.DRINKS,
            rating = 4.9,
            prepTimeMin = 5,
            isPopular = true,
            isSpicy = false,
            isVegetarian = true,
            iconEmoji = "🧋",
            imageUrl = "https://images.unsplash.com/photo-1576092768241-dec231879fc3?auto=format&fit=crop&w=800&q=80",
            availableAddOns = listOf(
                FoodAddOn("addon_ice", "Iced Option", "ရေခဲဖြင့်", 200),
                FoodAddOn("addon_boba", "Tapioca Pearls", "ပုလဲ", 500)
            )
        ),
        FoodItem(
            id = "food_falooda",
            nameEn = "Royal Myanmar Rose Falooda",
            nameMy = "တော်ဝင် ဖာလူဒါ အေးအေးလေး",
            descriptionEn = "Decadent dessert beverage with fragrant rose syrup, milk, egg pudding, vanilla ice cream scoop, strawberry jelly, and sago pearls.",
            descriptionMy = "နှင်းဆီနံ့သင်းသင်း၊ နို့စိမ်း၊ ရေခဲမုန့်၊ ဂျယ်လီ၊ သာကူ၊ ပူတင်းတို့ဖြင့် စီမံထားသော နာမည်ကြီး အချိုပွဲ။",
            priceMMK = 3500,
            category = FoodCategory.DRINKS,
            rating = 4.9,
            prepTimeMin = 8,
            isPopular = true,
            isSpicy = false,
            isVegetarian = true,
            iconEmoji = "🍨",
            imageUrl = "https://images.unsplash.com/photo-1563805042-7684c019e1cb?auto=format&fit=crop&w=800&q=80",
            availableAddOns = listOf(
                FoodAddOn("addon_icecream_scoop", "Extra Ice Cream Scoop", "ရေခဲမုန့် ၁ လုံးပို", 800)
            )
        ),
        FoodItem(
            id = "food_sugarcane_juice",
            nameEn = "Fresh Lime Sugarcane Juice",
            nameMy = "လတ်ဆတ်သော သံပုရာကြံရည်",
            descriptionEn = "Cold-pressed natural sugarcane juice with a zesty splash of fresh lime over crushed ice.",
            descriptionMy = "သဘာဝကြံရည်စစ်စစ်ကို သံပုရာသီးလတ်လတ်ဆတ်ဆတ် ညှစ်ထည့်ထားပြီး ရင်အေးစေပါသည်။",
            priceMMK = 1500,
            category = FoodCategory.DRINKS,
            rating = 4.6,
            prepTimeMin = 5,
            isPopular = false,
            isSpicy = false,
            isVegetarian = true,
            iconEmoji = "🥤",
            imageUrl = "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=800&q=80"
        )
    )
}

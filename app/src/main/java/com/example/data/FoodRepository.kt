package com.example.data

import com.example.model.DiningOccasion
import com.example.model.FoodAddOn
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.MyanmarTownship
import com.example.model.MyanmarTownshipsData
import com.example.model.PrepStyle
import kotlinx.coroutines.flow.Flow

class FoodRepository(private val orderDao: OrderDao) {

    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val userProfile: Flow<UserProfileEntity?> = orderDao.getUserProfile()
    val allProducts: Flow<List<ProductEntity>> = orderDao.getAllProducts()
    val allUserAccounts: Flow<List<UserAccountEntity>> = orderDao.getAllUserAccounts()
    val allReviews: Flow<List<ReviewEntity>> = orderDao.getAllReviews()
    val allCustomerMessages: Flow<List<CustomerMessageEntity>> = orderDao.getAllCustomerMessages()
    val businessSettings: Flow<BusinessSettingsEntity?> = orderDao.getBusinessSettings()
    val allCategories: Flow<List<CategoryEntity>> = orderDao.getAllCategories()
    val allBrands: Flow<List<BrandEntity>> = orderDao.getAllBrands()

    suspend fun saveOrder(order: OrderEntity) = orderDao.insertOrder(order)
    suspend fun updateOrderStatus(orderId: String, status: String) = orderDao.updateOrderStatus(orderId, status)
    suspend fun updatePaymentStatus(orderId: String, paymentStatus: String) = orderDao.updatePaymentStatus(orderId, paymentStatus)
    suspend fun updateTrackingInfo(orderId: String, trackingNumber: String, partnerName: String) = orderDao.updateTrackingInfo(orderId, trackingNumber, partnerName)
    suspend fun cancelOrderWithReason(orderId: String, reason: String) = orderDao.cancelOrderWithReason(orderId, reason)
    suspend fun requestOrderReturn(orderId: String, returnReason: String) = orderDao.requestOrderReturn(orderId, returnReason)
    suspend fun updateReturnStatus(orderId: String, returnStatus: String, paymentStatus: String) = orderDao.updateReturnStatus(orderId, returnStatus, paymentStatus)
    suspend fun updateDeliveredAt(orderId: String, deliveredAt: Long) = orderDao.updateDeliveredAt(orderId, deliveredAt)
    suspend fun deleteOrder(orderId: String) = orderDao.deleteOrder(orderId)
    suspend fun saveProfile(profile: UserProfileEntity) = orderDao.saveUserProfile(profile)
    suspend fun clearProfile() = orderDao.clearUserProfile()

    // Business Settings & Brand Control
    suspend fun saveBusinessSettings(settings: BusinessSettingsEntity) = orderDao.saveBusinessSettings(settings)

    // Categories Management
    suspend fun saveCategory(category: CategoryEntity) = orderDao.insertCategory(category)
    suspend fun updateCategory(category: CategoryEntity) = orderDao.updateCategory(category)
    suspend fun setCategoryArchived(id: String, isArchived: Boolean) = orderDao.setCategoryArchived(id, isArchived)
    suspend fun deleteCategory(id: String) = orderDao.deleteCategory(id)

    // Brands Management
    suspend fun saveBrand(brand: BrandEntity) = orderDao.insertBrand(brand)
    suspend fun updateBrand(brand: BrandEntity) = orderDao.updateBrand(brand)
    suspend fun setBrandArchived(id: String, isArchived: Boolean) = orderDao.setBrandArchived(id, isArchived)
    suspend fun deleteBrand(id: String) = orderDao.deleteBrand(id)

    // Customer Reviews & Ratings
    fun getReviewsForProduct(productId: String): Flow<List<ReviewEntity>> = orderDao.getReviewsForProduct(productId)
    suspend fun saveReview(review: ReviewEntity) = orderDao.insertReview(review)
    suspend fun replyToReview(reviewId: String, reply: String) = orderDao.replyToReview(reviewId, reply)
    suspend fun deleteReview(reviewId: String) = orderDao.deleteReview(reviewId)

    // Customer Messages & Contact Requests
    suspend fun saveCustomerMessage(message: CustomerMessageEntity) = orderDao.insertCustomerMessage(message)
    suspend fun replyToCustomerMessage(messageId: String, adminReply: String, status: String) = orderDao.replyToCustomerMessage(messageId, adminReply, status)
    suspend fun deleteCustomerMessage(messageId: String) = orderDao.deleteCustomerMessage(messageId)

    // Product CRUD & Soft Delete / Archive
    suspend fun insertProduct(product: ProductEntity) = orderDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = orderDao.updateProduct(product)
    suspend fun deleteProduct(productId: String) = orderDao.deleteProduct(productId)
    suspend fun setProductArchived(productId: String, isArchived: Boolean) = orderDao.setProductArchived(productId, isArchived)
    suspend fun updateProductStock(productId: String, quantity: Int) = orderDao.updateProductStock(productId, quantity)
    suspend fun updateProductPrice(productId: String, price: Int) = orderDao.updateProductPrice(productId, price)

    // User Account CRUD
    suspend fun saveUserAccount(user: UserAccountEntity) = orderDao.insertUserAccount(user)
    suspend fun updateUserAccountDetails(phone: String, name: String, email: String, preferredTownshipId: String, defaultAddressNote: String, adminNotes: String) =
        orderDao.updateUserAccountDetails(phone, name, email, preferredTownshipId, defaultAddressNote, adminNotes)
    suspend fun deleteUserAccount(phone: String) = orderDao.deleteUserAccount(phone)
    suspend fun getUserAccountByPhone(phone: String) = orderDao.getUserAccountByPhone(phone)

    suspend fun initializeDefaultsIfEmpty() {
        // Business Settings initialization
        if (orderDao.getBusinessSettingsCount() == 0) {
            orderDao.saveBusinessSettings(BusinessSettingsEntity())
        }

        // Default Categories initialization
        if (orderDao.getCategoryCount() == 0) {
            val defaultCategories = listOf(
                CategoryEntity(
                    id = "FISH",
                    nameEn = "Fish & Fillets",
                    nameMy = "ငါးမျိုးစုံ",
                    iconEmoji = "🐟",
                    descriptionEn = "Hilsa, seabass, pomfret, and red snapper fresh from Rakhine waters.",
                    descriptionMy = "ငါးသလောက်၊ ကကတစ်၊ ငါးမုတ်နှင့် ငါးပါးနီ လတ်လတ်ဆတ်ဆတ်များ။",
                    sortOrder = 1
                ),
                CategoryEntity(
                    id = "SHRIMP",
                    nameEn = "Prawns & Shrimps",
                    nameMy = "ပုစွန်မျိုးစုံ",
                    iconEmoji = "🦐",
                    descriptionEn = "Jumbo Andaman tiger prawns, freshwater giant prawns, and white prawns.",
                    descriptionMy = "ကျားပုစွန်၊ ရေချိုပုစွန်ထုပ်ကြီးနှင့် ပုစွန်ဖြူများ။",
                    sortOrder = 2
                ),
                CategoryEntity(
                    id = "CRAB",
                    nameEn = "Mud & Sea Crabs",
                    nameMy = "ဂဏန်းမျိုးစုံ",
                    iconEmoji = "🦀",
                    descriptionEn = "Live meaty mud crabs and natural egg crabs tied with sea grass.",
                    descriptionMy = "အသားဂဏန်း၊ ဥဂဏန်း လတ်လတ်ဆတ်ဆတ်များ။",
                    sortOrder = 3
                ),
                CategoryEntity(
                    id = "LOBSTER",
                    nameEn = "Lobsters",
                    nameMy = "ကျောက်ပုစွန်",
                    iconEmoji = "🦞",
                    descriptionEn = "Deep-sea rock lobsters and painted spiny lobsters packed on ice.",
                    descriptionMy = "ပင်လယ်နက်ထွက် ကျောက်ပုစွန် အဆင့်မြင့် ပင်လယ်စာများ။",
                    sortOrder = 4
                ),
                CategoryEntity(
                    id = "SQUID",
                    nameEn = "Squids & Calamari",
                    nameMy = "ပြည်ကြီးငါး",
                    iconEmoji = "🦑",
                    descriptionEn = "Thick tender coastal squids, cleaned and ready for BBQ grill or curry.",
                    descriptionMy = "ကင်ရန် သို့မဟုတ် ဟင်းချက်ရန် သန့်စင်ပြီး ပြည်ကြီးငါးများ။",
                    sortOrder = 5
                ),
                CategoryEntity(
                    id = "OCTOPUS",
                    nameEn = "Fresh Octopus",
                    nameMy = "ရေဘဝဲ",
                    iconEmoji = "🐙",
                    descriptionEn = "Fresh small and baby octopuses ideal for spicy hotpot or garlic stir-fry.",
                    descriptionMy = "ဟော့ပေါ့နှင့် အစပ်ကြော်အတွက် အထူးသင့်လျော်သော ရေဘဝဲများ။",
                    sortOrder = 6
                ),
                CategoryEntity(
                    id = "SHELLFISH",
                    nameEn = "Clams, Oysters & Mussels",
                    nameMy = "ခရု၊ ကမာ၊ ယောက်သွား",
                    iconEmoji = "🦪",
                    descriptionEn = "Fresh coastal rock oysters, blood clams, and green mussels.",
                    descriptionMy = "ကမ်းရိုးတန်းထွက် ကမာ၊ သွေးခရုနှင့် ယောက်သွားများ။",
                    sortOrder = 7
                )
            )
            orderDao.insertCategories(defaultCategories)
        }

        // Default Brands initialization
        if (orderDao.getBrandCount() == 0) {
            val defaultBrands = listOf(
                BrandEntity(
                    id = "brand_taim_ta_man",
                    nameEn = "Taim Ta Man Seafood",
                    nameMy = "တိမ်တမန်ပင်လယ်စာ",
                    originRegionEn = "Rakhine Coast",
                    originRegionMy = "ရခိုင်ကမ်းရိုးတန်း",
                    description = "Signature artisanal coastal seafood and authentic Rakhine marinades.",
                    logoEmoji = "🦞",
                    contactPhone = "09450012345"
                ),
                BrandEntity(
                    id = "brand_rakhine_coast",
                    nameEn = "Rakhine Coast Artisanal Catch",
                    nameMy = "ရခိုင်ကမ်းရိုးတန်း ထွက်ကုန်",
                    originRegionEn = "Sittwe & Thandwe",
                    originRegionMy = "စစ်တွေနှင့် သံတွဲ",
                    description = "Daily harvested sea catch brought directly from local fishing boats.",
                    logoEmoji = "🌊",
                    contactPhone = "09450012345"
                ),
                BrandEntity(
                    id = "brand_andaman_deep",
                    nameEn = "Andaman Deep Sea Harvest",
                    nameMy = "အက်ဒမန်ပင်လယ်နက်",
                    originRegionEn = "Myeik Archipelago",
                    originRegionMy = "မြိတ်ကျွန်းစု",
                    description = "Premium deep sea wild harvest, giant lobsters and tiger prawns.",
                    logoEmoji = "⚓",
                    contactPhone = "09450012345"
                ),
                BrandEntity(
                    id = "brand_ayeyarwady_estuary",
                    nameEn = "Ayeyarwady Delta Estuary",
                    nameMy = "ဧရာဝတီမြစ်ဝကျွန်းပေါ်",
                    originRegionEn = "Bogale & Pyapon",
                    originRegionMy = "ဘိုကလေးနှင့် ဖျာပုံ",
                    description = "Mangrove harvested live mud crabs and natural freshwater prawns.",
                    logoEmoji = "🌾",
                    contactPhone = "09450012345"
                )
            )
            orderDao.insertBrands(defaultBrands)
        }

        if (orderDao.getProductCount() == 0) {
            val productEntities = menuItems.map { ProductEntity.fromFoodItem(it) }
            orderDao.insertProducts(productEntities)
        }
        if (orderDao.getUserAccountCount() == 0) {
            val defaultAccounts = listOf(
                UserAccountEntity(
                    phone = "09999999999",
                    name = "Admin Ko Min Min",
                    role = "ADMIN",
                    preferredTownshipId = "ygn_kamayut",
                    defaultAddressNote = "Central Seafood Office, Insein Road",
                    password = "admin",
                    email = "admin@myanmarfood.com",
                    totalOrdersCount = 0,
                    totalSpentMMK = 0,
                    adminNotes = "Super Admin Account"
                ),
                UserAccountEntity(
                    phone = "09777123456",
                    name = "Ko Mg Mg",
                    role = "CUSTOMER",
                    preferredTownshipId = "ygn_bahan",
                    defaultAddressNote = "No. 42, Golden Valley",
                    password = "123",
                    email = "mgmg@gmail.com",
                    totalOrdersCount = 4,
                    totalSpentMMK = 185000,
                    adminNotes = "VIP Customer - Likes extra ice box packing"
                ),
                UserAccountEntity(
                    phone = "09788654321",
                    name = "Daw Hla Hla",
                    role = "CUSTOMER",
                    preferredTownshipId = "ygn_sanchaung",
                    defaultAddressNote = "Room 3B, Padonmar Street",
                    password = "123",
                    email = "hlahla@outlook.com",
                    totalOrdersCount = 2,
                    totalSpentMMK = 72000,
                    adminNotes = "Call 15 mins before arrival"
                )
            )
            orderDao.insertUserAccounts(defaultAccounts)

            // Seed initial reviews
            orderDao.insertReview(
                ReviewEntity(
                    productId = "seafood_tiger_prawns_jumbo",
                    productName = "Jumbo Andaman Tiger Prawns",
                    customerName = "Ko Mg Mg",
                    customerPhone = "09777123456",
                    rating = 5,
                    comment = "Super fresh and sweet prawns! Grilled them with the garlic butter add-on, family absolutely loved it.",
                    timestamp = System.currentTimeMillis() - 86400000L,
                    adminReply = "Thank you so much Ko Mg Mg! We get fresh Andaman catch daily.",
                    isApproved = true
                )
            )
            orderDao.insertReview(
                ReviewEntity(
                    productId = "seafood_mud_crab_live",
                    productName = "Live Mud Crabs (Egg Crab)",
                    customerName = "Daw Hla Hla",
                    customerPhone = "09788654321",
                    rating = 5,
                    comment = "Crabs were lively and packed with rich roe. Perfect for sweet and sour crab curry.",
                    timestamp = System.currentTimeMillis() - 172800000L,
                    isApproved = true
                )
            )

            // Seed initial customer messages / contact inquiries
            orderDao.insertCustomerMessage(
                CustomerMessageEntity(
                    customerName = "Ko Mg Mg",
                    customerPhone = "09777123456",
                    customerEmail = "mgmg@gmail.com",
                    subject = "Inquiry regarding bulk order for family gathering",
                    message = "Hello, do you provide 5kg bulk packs of Tiger Prawns with clean ice box for weekend events in Bahan?",
                    category = "PRODUCT_QUESTION",
                    status = "RESOLVED",
                    timestamp = System.currentTimeMillis() - 43200000L,
                    adminReply = "Yes Ko Mg Mg! We offer custom insulated styrofoam packing with crushed ice at no extra charge for orders over 100,000 MMK.",
                    repliedAt = System.currentTimeMillis() - 21600000L
                )
            )
            orderDao.insertCustomerMessage(
                CustomerMessageEntity(
                    customerName = "Daw Tin Tin",
                    customerPhone = "09444987654",
                    customerEmail = "tintin@gmail.com",
                    subject = "Delivery timing to South Dagon",
                    message = "Can I request early morning delivery around 9:00 AM before cooking lunch?",
                    category = "DELIVERY_ISSUE",
                    status = "NEW",
                    timestamp = System.currentTimeMillis() - 3600000L
                )
            )
        }
    }

    fun getTownshipById(id: String): MyanmarTownship {
        return MyanmarTownshipsData.allTownships.find { it.id == id } ?: MyanmarTownshipsData.defaultTownship
    }

    val commonSeafoodAddOns = listOf(
        FoodAddOn("addon_chili_lime", "Special Rakhine Seafood Chili-Lime Sauce", "ရခိုင်ပင်လယ်စာ အထူးအချဉ်ရည်", 1500),
        FoodAddOn("addon_aromatics", "Lemongrass, Ginger & Garlic Aromatics Pack", "စပါးလင်/ဂျင်း/ကြက်သွန်ဖြူ ဟင်းခတ်အတွဲ", 1000),
        FoodAddOn("addon_ice_box", "Insulated Foam Box with Extra Ice Chill Packing", "ရေခဲပုံးအပို ထုပ်ပိုးခြင်း", 2000),
        FoodAddOn("addon_garlic_butter", "Garlic Herb Butter Glaze for Grill", "မီးကင်ထောပတ်နှင့် ကြက်သွန်ဖြူဆော့စ်", 1500)
    )

    val menuItems: List<FoodItem> = listOf(
        // 1. 🐟 ငါး (Fish)
        FoodItem(
            id = "seafood_barramundi_whole",
            nameEn = "Fresh Sea Barramundi (Cleaned & Scaled)",
            nameMy = "ငါးကကတစ် အကောင်လိုက် (သန့်စင်ပြီး)",
            descriptionEn = "Fresh coastal barramundi, gutted, descaled, and prepped ready to steam with lime or fry crispy.",
            descriptionMy = "ရခိုင်ကမ်းရိုးတန်းထွက် လတ်ဆတ်သော ငါးကကတစ်ကြီးများကို အကြေးခွါ ကလီစာထုတ် သန့်စင်ပြီး ချက်ရန်အသင့် ပြင်ဆင်ပေးထားပါသည်။",
            priceMMK = 35000,
            unitEn = "per 1 kg",
            unitMy = "၁ ကီလို",
            category = FoodCategory.FISH,
            prepStyle = PrepStyle.READY_TO_COOK,
            occasion = DiningOccasion.FAMILY_DINNER,
            rating = 4.9,
            prepTimeMin = 15,
            isPopular = true,
            isSpicy = false,
            iconEmoji = "🐟",
            imageUrl = "https://images.unsplash.com/photo-1534939561126-855b8675edd7?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_salmon_fillet",
            nameEn = "Premium Salmon Fillet (Skin-On, Boneless)",
            nameMy = "နော်ဝေးဆော်လမွန် အသားလွှာ (အရိုးမပါ)",
            descriptionEn = "Sashimi-grade fresh salmon fillet, fully deboned and vacuum-sealed to preserve rich natural omega-3 oils.",
            descriptionMy = "အရိုးလုံးဝမပါဘဲ အသားလွှာသီးသန့် သန့်စင်ထားပြီး မီးကင်၊ ဟော့ပေါ့ (သို့) ဒယ်အိုးကပ်ကြော်ရန် အလွန်သင့်တော်ပါသည်။",
            priceMMK = 58000,
            unitEn = "per 500 g",
            unitMy = "၅၀၀ ဂရမ်",
            category = FoodCategory.FISH,
            prepStyle = PrepStyle.BONELESS,
            occasion = DiningOccasion.PREMIUM,
            rating = 5.0,
            prepTimeMin = 10,
            isPopular = true,
            isPremium = true,
            iconEmoji = "🐟",
            imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_pomfret_steaks",
            nameEn = "White Pomfret Steaks (Cleaned & Sliced)",
            nameMy = "ငါးမုတ်ဖြူ အတုံးလှီး (ဟော့ပေါ့/ကြော်ရန်)",
            descriptionEn = "Tender white pomfret expertly sliced into succulent steaks. Ideal for aromatic hotpot broth or golden pan-fry.",
            descriptionMy = "ငါးမုတ်ဖြူသား နူးနူးညံ့ညံ့ကို အတုံးလှီးဖြတ်ထားပြီး ဟော့ပေါ့အရည်သောက်၊ ချဉ်စပ်ဟင်းချက်ရန် သန့်စင်ပြီးသားဖြစ်ပါသည်။",
            priceMMK = 48000,
            unitEn = "per 1 kg",
            unitMy = "၁ ကီလို",
            category = FoodCategory.FISH,
            prepStyle = PrepStyle.CLEANED_SLICED,
            occasion = DiningOccasion.HOTPOT,
            rating = 4.8,
            prepTimeMin = 15,
            iconEmoji = "🐟",
            imageUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_featherback_paste",
            nameEn = "Featherback Fish Paste (Ready to Cook)",
            nameMy = "ရခိုင်ငါးဖယ်ခြစ်သား စစ်စစ် (ဟင်းချက်/ကြော်ရန်)",
            descriptionEn = "100% natural bouncy featherback fish paste pounded with garlic and sea salt. Ready to shape into bouncy balls or patties.",
            descriptionMy = "ရခိုင်ငါးဖယ်စစ်စစ် ကစီလုံးဝမရောဘဲ နူးညံ့စေးပိုင်စွာ ခြစ်ထားသော ငါးဖယ်လုံး၊ ငါးဖယ်ကြော်နှင့် ဟင်းချက်ရန် အဆင်သင့်။",
            priceMMK = 18000,
            unitEn = "per 500 g",
            unitMy = "၅၀၀ ဂရမ်",
            category = FoodCategory.FISH,
            prepStyle = PrepStyle.READY_TO_COOK,
            occasion = DiningOccasion.FAMILY_DINNER,
            rating = 4.8,
            prepTimeMin = 10,
            iconEmoji = "🐟",
            imageUrl = "https://images.unsplash.com/photo-1580476262798-bddd9f4b7369?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_marinated_seabass_bbq",
            nameEn = "Marinated Whole Seabass for BBQ Grill",
            nameMy = "ငါးကကတစ် မီးကင်အရသာနှပ်ပြီးသား",
            descriptionEn = "Whole seabass slit and thoroughly rubbed with Rakhine lemongrass, kaffir lime, garlic, and chili marinade.",
            descriptionMy = "ရခိုင်ရိုးရာ စပါးလင်၊ ရှောက်ရွက်၊ ငရုတ်သီး၊ ကြက်သွန်ဖြူဆော့စ်ဖြင့် အရသာနှပ်ထားပြီး မီးသွေးဖို (သို့) Oven တွင် ချက်ချင်းကင်ရုံသာ။",
            priceMMK = 38000,
            unitEn = "per 1 pack (1 pc)",
            unitMy = "၁ ထုပ် (၁ ကောင်)",
            category = FoodCategory.FISH,
            prepStyle = PrepStyle.MARINATED,
            occasion = DiningOccasion.BBQ_GRILL,
            rating = 4.9,
            prepTimeMin = 20,
            isSpicy = true,
            iconEmoji = "🐟",
            imageUrl = "https://images.unsplash.com/photo-1534939561126-855b8675edd7?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),

        // 2. 🦐 ပုစွန် (Shrimp/Prawn)
        FoodItem(
            id = "seafood_tiger_prawns_peeled",
            nameEn = "Jumbo Tiger Prawns (Peeled & Deveined)",
            nameMy = "ကျားပုစွန်ကြီး အခွံခွာပြီး အူကြောထုတ်",
            descriptionEn = "Plump wild black tiger prawns, shells removed and intestinal vein extracted. Crisp, sweet, and succulent.",
            descriptionMy = "ပင်လယ်ကျားပုစွန်အကောင်ကြီးများကို အခွံခွါ အူကြောဖယ်ရှားပြီးဖြစ်၍ ဟော့ပေါ့၊ ထောပတ်ကြော်နှင့် အသုပ်အတွက် အချိန်ကုန်သက်သာစေပါသည်။",
            priceMMK = 45000,
            unitEn = "per 500 g",
            unitMy = "၅၀၀ ဂရမ်",
            category = FoodCategory.SHRIMP,
            prepStyle = PrepStyle.PEELED_DEVEINED,
            occasion = DiningOccasion.FAMILY_DINNER,
            rating = 4.9,
            prepTimeMin = 10,
            isPopular = true,
            iconEmoji = "🦐",
            imageUrl = "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_white_prawns_whole",
            nameEn = "Fresh White Sea Prawns (Head-On, Cleaned)",
            nameMy = "ပင်လယ်ပုစွန်ဖြူ လတ်လတ်ဆတ်ဆတ်",
            descriptionEn = "Freshly landed translucent sea prawns with tender shells and intensely rich sweet head roe. Perfect for hotpot and curries.",
            descriptionMy = "ပင်လယ်ပြင်မှ တိုက်ရိုက်ဖမ်းယူထားသော ပုစွန်ဖြူလတ်လတ်ဆတ်ဆတ်၊ ခေါင်းပိုင်းအဆီပြည့်ပြည့်ဖြင့် ဟော့ပေါ့နှင့် ဟင်းချက်ရန် အထူးကောင်းမွန်။",
            priceMMK = 38000,
            unitEn = "per 1 kg",
            unitMy = "၁ ကီလို",
            category = FoodCategory.SHRIMP,
            prepStyle = PrepStyle.CLEANED_SLICED,
            occasion = DiningOccasion.HOTPOT,
            rating = 4.8,
            prepTimeMin = 12,
            iconEmoji = "🦐",
            imageUrl = "https://images.unsplash.com/photo-1559742811-822873691df8?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_marinated_prawn_skewers",
            nameEn = "Marinated BBQ Prawn Skewers (Ready to Grill)",
            nameMy = "ပုစွန်မီးကင် အရသာနှပ်တုတ်ထိုးအတွဲ",
            descriptionEn = "10 jumbo prawns skewered on bamboo sticks, marinated in mild spicy garlic pepper glaze. Ready for outdoor barbecue.",
            descriptionMy = "ဝါးတုတ်ထိုးတွင် ပုစွန်ကြီးများကို သီတန်းပြီး ကြက်သွန်ဖြူ ငရုတ်ကောင်း ထောပတ်မွှေးဆော့စ်ဖြင့် နှပ်ထားသော မီးကင်အထူးပွဲ။",
            priceMMK = 32000,
            unitEn = "per 1 pack (10 skewers)",
            unitMy = "၁ ထုပ် (၁၀ ချောင်း)",
            category = FoodCategory.SHRIMP,
            prepStyle = PrepStyle.MARINATED,
            occasion = DiningOccasion.BBQ_GRILL,
            rating = 4.9,
            prepTimeMin = 15,
            isSpicy = true,
            iconEmoji = "🦐",
            imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),

        // 3. 🦀 ကဏန်း (Crab)
        FoodItem(
            id = "seafood_soft_shell_crab",
            nameEn = "Rakhine Soft-Shell Crabs (Ready to Fry)",
            nameMy = "ရခိုင်ကမ်းရိုးတန်း ကဏန်းပျော့ (ကြော်ရန်အသင့်)",
            descriptionEn = "Prime molted whole crabs with delicate edible shells. Cleaned and trimmed; coat lightly in flour and flash fry.",
            descriptionMy = "အခွံတစ်ခုလုံး ဝါးစားနိုင်သော ရခိုင်ကဏန်းပျော့လတ်ဆတ်စစ်စစ်။ မုန့်နှစ်ကပ်ကြော်ရုံဖြင့် ကြွပ်ရွမွှေးကြိုင်သော အရသာကို ရရှိစေပါသည်။",
            priceMMK = 36000,
            unitEn = "per 1 pack (5 pcs)",
            unitMy = "၁ ထုပ် (၅ ကောင်)",
            category = FoodCategory.CRAB,
            prepStyle = PrepStyle.READY_TO_COOK,
            occasion = DiningOccasion.FAMILY_DINNER,
            rating = 4.9,
            prepTimeMin = 15,
            isPopular = true,
            iconEmoji = "🦀",
            imageUrl = "https://images.unsplash.com/photo-1559737558-24523a63b0a7?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_mud_crab_live",
            nameEn = "Jumbo Sea Mud Crabs (Big Claws, Cleaned)",
            nameMy = "ပင်လယ်ကဏန်းမကြီး အကောင်လိုက် (လက်မတုပ်)",
            descriptionEn = "Heavyweight mud crabs bursting with sweet leg meat and golden creamy roe. Cleaned, cracked, and ready for chilli crab or steamed banquet.",
            descriptionMy = "လက်မကြီးများ အသားပြည့်တင်းပြီး ကဏန်းဥအဆီတောင့်တောင့်များ ပါဝင်သော ရခိုင်ပင်လယ်ကဏန်းမကြီးများ။ ပါတီပွဲများအတွက် ထိပ်တန်းရွေးချယ်မှု။",
            priceMMK = 55000,
            unitEn = "per 1 kg",
            unitMy = "၁ ကီလို",
            category = FoodCategory.CRAB,
            prepStyle = PrepStyle.CLEANED_SLICED,
            occasion = DiningOccasion.PARTY_EVENT,
            rating = 4.8,
            prepTimeMin = 20,
            iconEmoji = "🦀",
            imageUrl = "https://images.unsplash.com/photo-1559737558-24523a63b0a7?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_pure_crab_meat",
            nameEn = "Pure Jumbo Lump Crab Meat (Boneless)",
            nameMy = "သဘာဝ ကဏန်းသား အသားသီးသန့် (အရိုးမပါ)",
            descriptionEn = "Hand-picked pure sweet crab body and claw lump meat. 100% shell-free, ready for soups, fried rice, and hotpot.",
            descriptionMy = "ကဏန်းအခွံလုံးဝမပါဘဲ အသားသီးသန့် လက်ဖြင့်သန့်စင်ခြွေယူထားသော ကဏန်းသားအိအိ။ ဟော့ပေါ့၊ စွပ်ပြုတ်နှင့် ကဏန်းထမင်းကြော်အတွက် အထူးသင့်တော်။",
            priceMMK = 48000,
            unitEn = "per 300 g",
            unitMy = "၃၀၀ ဂရမ်",
            category = FoodCategory.CRAB,
            prepStyle = PrepStyle.BONELESS,
            occasion = DiningOccasion.HOTPOT,
            rating = 4.9,
            prepTimeMin = 10,
            iconEmoji = "🦀",
            imageUrl = "https://images.unsplash.com/photo-1541544741938-0af808871cc0?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),

        // 4. 🦞 ပုစွန်ထုပ် (Lobster)
        FoodItem(
            id = "seafood_spiny_rock_lobster",
            nameEn = "Fresh Rakhine Spiny Rock Lobster (Whole)",
            nameMy = "ရခိုင်ကျောက်ဆူး ပုစွန်ထုပ်ကြီး (အကောင်လိုက်)",
            descriptionEn = "Prized Andaman sea spiny rock lobster. Thick, sweet muscular tail meat with intense ocean sweetness.",
            descriptionMy = "ရခိုင်ပင်လယ်နက်ထွက် ကျောက်ဆူးပုစွန်ထုပ်ကြီး အကောင်လိုက်။ မီးကင်၊ ချိစ်ဖုတ် (သို့) ရခိုင်စတိုင် အမွှေးအကြိုင်ပေါင်းချက်ရန် အဆင့်မြင့်ပွဲ။",
            priceMMK = 145000,
            unitEn = "per 1 pc (800 g)",
            unitMy = "၁ ကောင် (၈၀၀ ဂရမ်)",
            category = FoodCategory.LOBSTER,
            prepStyle = PrepStyle.READY_TO_COOK,
            occasion = DiningOccasion.PREMIUM,
            rating = 5.0,
            prepTimeMin = 25,
            isPopular = true,
            isPremium = true,
            iconEmoji = "🦞",
            imageUrl = "https://images.unsplash.com/photo-1599084993091-1cb5c0721cc6?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_marinated_lobster_tails",
            nameEn = "Garlic Butter Marinated Lobster Tails (BBQ)",
            nameMy = "ပုစွန်ထုပ်အမြီး ထောပတ်မွှေးနှပ် (BBQ ကင်ရန်)",
            descriptionEn = "Two split lobster tails generously basted with garlic, parsley, and French butter. Ready to place straight on the grill grate.",
            descriptionMy = "ပုစွန်ထုပ်အမြီး ၂ မြီးကို အလယ်မှခွဲကာ ကြက်သွန်ဖြူထောပတ်မွှေးဆော့စ်ဖြင့် နှပ်ထားပြီး မီးကင်ပွဲများတွင် ချက်ချင်းကင်ရန် အသင့်။",
            priceMMK = 85000,
            unitEn = "per 1 pack (2 tails)",
            unitMy = "၁ ထုပ် (၂ မြီး)",
            category = FoodCategory.LOBSTER,
            prepStyle = PrepStyle.MARINATED,
            occasion = DiningOccasion.BBQ_GRILL,
            rating = 4.9,
            prepTimeMin = 15,
            isPremium = true,
            iconEmoji = "🦞",
            imageUrl = "https://images.unsplash.com/photo-1625938145744-e380486a482b?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_imperial_king_lobster_set",
            nameEn = "Imperial Giant King Lobster Banquet Set",
            nameMy = "ဂျင်ဘို ပုစွန်ထုပ်ဧကရာဇ် ပါတီအတွဲ",
            descriptionEn = "Spectacular 1.5kg grand banquet lobster package cut and portioned into medallions with accompanying seasoning sauces.",
            descriptionMy = "အထူးဧည့်ခံပွဲကြီးများအတွက် အလေးချိန် ၁.၅ ကီလိုရှိသော ပုစွန်ထုပ်ဧကရာဇ်ကြီးကို အတုံးလှီးပြင်ဆင်ပေးထားသော အဆင့်မြင့်ဆုံး ပင်လယ်စာအစုံ။",
            priceMMK = 240000,
            unitEn = "per 1 set (1.5 kg)",
            unitMy = "၁ စုံ (၁.၅ ကီလို)",
            category = FoodCategory.LOBSTER,
            prepStyle = PrepStyle.CLEANED_SLICED,
            occasion = DiningOccasion.PARTY_EVENT,
            rating = 5.0,
            prepTimeMin = 30,
            isPremium = true,
            iconEmoji = "🦞",
            imageUrl = "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),

        // 5. 🦑 ပြည်ကြီးငါး (Squid)
        FoodItem(
            id = "seafood_squid_rings_cleaned",
            nameEn = "Cleaned Squid Calamari Rings (Hotpot Ready)",
            nameMy = "ပြည်ကြီးငါး အကွင်းလှီး သန့်စင်ပြီး (ဟော့ပေါ့သုံး)",
            descriptionEn = "Fresh white squid tubes peeled, washed, and cut into uniform rings. Ready for quick hotpot dip or crispy calamari fry.",
            descriptionMy = "ပြည်ကြီးငါးကို အခွံသန့်စင် အကွင်းလိုက် အညီအမျှ လှီးဖြတ်ထားပြီး ဟော့ပေါ့တွင် နှစ်စက္ကန့်အနည်းငယ် နှစ်ရုံဖြင့် နူးညံ့ကြွပ်ဆတ်စေပါသည်။",
            priceMMK = 26000,
            unitEn = "per 500 g",
            unitMy = "၅၀၀ ဂရမ်",
            category = FoodCategory.SQUID,
            prepStyle = PrepStyle.CLEANED_SLICED,
            occasion = DiningOccasion.HOTPOT,
            rating = 4.8,
            prepTimeMin = 10,
            iconEmoji = "🦑",
            imageUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_bbq_marinated_squid",
            nameEn = "Spicy Marinated Whole BBQ Squid (Ready to Grill)",
            nameMy = "ပြည်ကြီးငါး မီးကင်ရန် အရသာနှပ်ပြီးသား",
            descriptionEn = "3 whole fresh squids scored with diamond cuts and steeped in spicy Rakhine chili-garlic paste. Grill for 3 minutes per side.",
            descriptionMy = "ပြည်ကြီးငါးအကောင်ကြီး ၃ ကောင်ကို အမွှေးအကြိုင်ဝင်စေရန် မွှန်းပြီး ရခိုင်ငရုတ်သီးစပ်စပ်မွှေးမွှေးဖြင့် နှပ်ထားသော နာမည်ကြီး မီးကင်။",
            priceMMK = 34000,
            unitEn = "per 1 pack (3 pcs)",
            unitMy = "၁ ထုပ် (၃ ကောင်)",
            category = FoodCategory.SQUID,
            prepStyle = PrepStyle.MARINATED,
            occasion = DiningOccasion.BBQ_GRILL,
            rating = 4.9,
            prepTimeMin = 15,
            isSpicy = true,
            iconEmoji = "🦑",
            imageUrl = "https://images.unsplash.com/photo-1532336414038-cf19250c5757?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_whole_squid_roe",
            nameEn = "Fresh Whole Squid with Roe (Ready to Cook)",
            nameMy = "ပြည်ကြီးငါး ဥပါအကောင်လိုက် (ချက်ရန်အသင့်)",
            descriptionEn = "Firm, plump whole ocean squids loaded with natural creamy roe inside. Washed and prepped ready for spicy Rakhine curry.",
            descriptionMy = "ပြည်ကြီးငါးဗိုက်အတွင်း ဥအဆီပြည့်ပြည့်ပါဝင်သော အကောင်လိုက်လတ်လတ်ဆတ်ဆတ်။ ရခိုင်ပြည်ကြီးငါးဥ ချဉ်စပ်ဟင်းချက်ရန် အသင့်တော်ဆုံး။",
            priceMMK = 42000,
            unitEn = "per 1 kg",
            unitMy = "၁ ကီလို",
            category = FoodCategory.SQUID,
            prepStyle = PrepStyle.READY_TO_COOK,
            occasion = DiningOccasion.FAMILY_DINNER,
            rating = 4.8,
            prepTimeMin = 15,
            iconEmoji = "🦑",
            imageUrl = "https://images.unsplash.com/photo-1544551763-77ef2d0cfc6c?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),

        // 6. 🐙 ရေဘဝဲ (Octopus)
        FoodItem(
            id = "seafood_baby_octopus_cleaned",
            nameEn = "Cleaned Baby Octopus (Hotpot & Stir Fry)",
            nameMy = "ရေဘဝဲပေါက်စ သန့်စင်ပြီး (ဟော့ပေါ့/မီးကင်)",
            descriptionEn = "Tender baby octopuses thoroughly desalted and tenderized. Ready for fiery stir-fry or hotpot simmering.",
            descriptionMy = "ရေဘဝဲပေါက်စလေးများကို ကလီစာနှင့် သဲများ သန့်စင်ဖယ်ရှားပြီး အဆင်သင့်ချက်ပြုတ်နိုင်ရန် ပြင်ဆင်ထားပါသည်။ ကြွပ်ကြွပ်ရွရွ အရသာရှိ။",
            priceMMK = 28000,
            unitEn = "per 500 g",
            unitMy = "၅၀၀ ဂရမ်",
            category = FoodCategory.OCTOPUS,
            prepStyle = PrepStyle.READY_TO_COOK,
            occasion = DiningOccasion.HOTPOT,
            rating = 4.8,
            prepTimeMin = 10,
            iconEmoji = "🐙",
            imageUrl = "https://images.unsplash.com/photo-1545247181-516773cae754?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_octopus_tentacles_sliced",
            nameEn = "Jumbo Octopus Tentacles (Cleaned & Sliced)",
            nameMy = "ရေဘဝဲလက်မကြီးများ လှီးဖြတ်ပြီး (အတုံးလှီး)",
            descriptionEn = "Thick, succulent octopus tentacles sliced into bite-sized medallions. Cook quickly over high heat for maximum tenderness.",
            descriptionMy = "ရေဘဝဲလက်မကြီးများကို အနေတော် အတုံးလှီးထားပြီး မီးကင်၊ ဟော့ပေါ့နှင့် ငရုတ်ပွကြော်တို့အတွက် ဝါးရလွယ်ကူစေရန် စီမံထားပါသည်။",
            priceMMK = 46000,
            unitEn = "per 500 g",
            unitMy = "၅၀၀ ဂရမ်",
            category = FoodCategory.OCTOPUS,
            prepStyle = PrepStyle.CLEANED_SLICED,
            occasion = DiningOccasion.BBQ_GRILL,
            rating = 4.9,
            prepTimeMin = 15,
            iconEmoji = "🐙",
            imageUrl = "https://images.unsplash.com/photo-1545247181-516773cae754?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_marinated_spicy_octopus",
            nameEn = "Spicy Marinated Octopus Skewers (BBQ Ready)",
            nameMy = "ရေဘဝဲ အစပ်အရသာနှပ်ပြီးသား (မီးကင်ရန်)",
            descriptionEn = "Octopus pieces marinated in smoky chili oil, roasted sesame seeds, and Myanmar lime juice. Bursting with barbecue flavor.",
            descriptionMy = "မီးခိုးနံ့သင်း ငရုတ်သီးဆီ၊ နှမ်းလှော်၊ သံပုရာရည်တို့ဖြင့် အချိုးကျ အရသာနှပ်ထားသော ရေဘဝဲမီးကင်ပွဲ။",
            priceMMK = 35000,
            unitEn = "per 1 pack (400 g)",
            unitMy = "၁ ထုပ် (၄၀၀ ဂရမ်)",
            category = FoodCategory.OCTOPUS,
            prepStyle = PrepStyle.MARINATED,
            occasion = DiningOccasion.BBQ_GRILL,
            rating = 4.9,
            prepTimeMin = 15,
            isSpicy = true,
            iconEmoji = "🐙",
            imageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),

        // 7. 🦪 အခွံပါပင်လယ်စာ (Shellfish)
        FoodItem(
            id = "seafood_rakhine_oysters",
            nameEn = "Fresh Rakhine Live Pacific Oysters (Half-Shell)",
            nameMy = "ရခိုင်ကမ်းရိုးတန်း ယောက်သွားလတ်လတ်ဆတ်ဆတ်",
            descriptionEn = "6 plump, juicy Rakhine oysters shucked on half-shell, delivered on ice with lime wedges and chili dipping sauce.",
            descriptionMy = "ရခိုင်ပင်လယ်ကွေ့မှ လတ်လတ်ဆတ်ဆတ် ဖမ်းယူထားသော ယောက်သွားကြီး ၆ ကောင်။ ရေခဲပေါ်တင်ကာ သံပုရာသီး၊ ရခိုင်အချဉ်တို့ဖြင့် တွဲဖက်စားသုံးရန်။",
            priceMMK = 32000,
            unitEn = "per 1 pack (6 pcs)",
            unitMy = "၁ ထုပ် (၆ ကောင်)",
            category = FoodCategory.SHELLFISH,
            prepStyle = PrepStyle.READY_TO_COOK,
            occasion = DiningOccasion.PARTY_EVENT,
            rating = 5.0,
            prepTimeMin = 10,
            isPopular = true,
            iconEmoji = "🦪",
            imageUrl = "https://images.unsplash.com/photo-1533777857889-4be7c70b33f7?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_purged_sea_clams",
            nameEn = "Fresh Purged Black Sea Clams (Hotpot Ready)",
            nameMy = "ပင်လယ်ခုံးကောင် လတ်ဆတ် သဲချွတ်ပြီး (ဟော့ပေါ့သုံး)",
            descriptionEn = "1kg of sweet black sea clams, naturally saltwater purged to eliminate all sand. Opens quickly in boiling soup.",
            descriptionMy = "သဲလုံးဝမပါစေရန် သဘာဝဆားငန်ရေဖြင့် သန့်စင်ချွတ်ထားပြီးဖြစ်သော ပင်လယ်ခုံးကောင်ကြီးများ။ ဟော့ပေါ့နှင့် စပါးလင်ပြုတ်ရည်အတွက် အလွန်ချို။",
            priceMMK = 22000,
            unitEn = "per 1 kg",
            unitMy = "၁ ကီလို",
            category = FoodCategory.SHELLFISH,
            prepStyle = PrepStyle.READY_TO_COOK,
            occasion = DiningOccasion.HOTPOT,
            rating = 4.8,
            prepTimeMin = 12,
            iconEmoji = "🦪",
            imageUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_marinated_garlic_scallops",
            nameEn = "Garlic Butter Marinated Half-Shell Scallops",
            nameMy = "စကဲလော့ အခွံပါ ထောပတ်ကြက်သွန်ဖြူနှပ်",
            descriptionEn = "8 plump scallops on shell topped with finely minced garlic, butter, and scallions. Ready to broil or barbecue until bubbling.",
            descriptionMy = "အခွံပါ စကဲလော့ ၈ ခုပေါ်တွင် ထောပတ်မွှေးနှင့် ကြက်သွန်ဖြူတို့ကို အချိုးကျတင်ထားပြီး မီးကင်ဖိုပေါ်တွင် ပွက်ပွက်ဆူသည်အထိ ကင်ရုံသာ။",
            priceMMK = 48000,
            unitEn = "per 1 pack (8 pcs)",
            unitMy = "၁ ထုပ် (၈ ခု)",
            category = FoodCategory.SHELLFISH,
            prepStyle = PrepStyle.MARINATED,
            occasion = DiningOccasion.BBQ_GRILL,
            rating = 4.9,
            prepTimeMin = 15,
            iconEmoji = "🦪",
            imageUrl = "https://images.unsplash.com/photo-1532550907401-a500c9a57435?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        ),
        FoodItem(
            id = "seafood_grand_imperial_banquet_platter",
            nameEn = "Taim Ta Man Grand Imperial Banquet Platter",
            nameMy = "တိမ်တမန် အဆင့်မြင့် ပင်လယ်စာအစုံ ပါတီဗန်းကြီး",
            descriptionEn = "The ultimate 3.5kg celebration feast: Rock lobster, tiger prawns, whole mud crabs, squid rings, and scallops packed on ice.",
            descriptionMy = "မိသားစုပါတီနှင့် မင်္ဂလာဧည့်ခံပွဲများအတွက် ပုစွန်ထုပ်၊ ကျားပုစွန်၊ ကဏန်း၊ ပြည်ကြီးငါးနှင့် စကဲလော့တို့ စုံလင်စွာပါဝင်သော တိမ်တမန်၏ အထူးဗန်းကြီး။",
            priceMMK = 280000,
            unitEn = "per 1 set (3.5 kg)",
            unitMy = "၁ ဗန်းကြီး (၃.၅ ကီလို)",
            category = FoodCategory.SHELLFISH,
            prepStyle = PrepStyle.READY_TO_COOK,
            occasion = DiningOccasion.PREMIUM,
            rating = 5.0,
            prepTimeMin = 35,
            isPopular = true,
            isPremium = true,
            iconEmoji = "🦪",
            imageUrl = "https://images.unsplash.com/photo-1615141982883-c7ad0e69fd62?auto=format&fit=crop&w=800&q=80",
            availableAddOns = commonSeafoodAddOns
        )
    )
}

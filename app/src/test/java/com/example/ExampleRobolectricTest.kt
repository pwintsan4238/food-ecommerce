package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FoodRepository
import com.example.data.OrderDao
import com.example.data.OrderEntity
import com.example.data.ProductEntity
import com.example.data.UserProfileEntity
import com.example.model.CartItem
import com.example.model.FoodAddOn
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.Language
import com.example.model.MyanmarTownshipsData
import com.example.model.OrderItemSnapshot
import com.example.model.OrderStatus
import com.example.model.Strings
import com.example.viewmodel.FoodOrderViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `verify MainActivity launches successfully without crashing`() {
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
    assertNotNull(controller.get())
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("တိမ်တမန်", appName)
  }

  @Test
  fun `verify myanmar townships dataset`() {
    val townships = MyanmarTownshipsData.allTownships
    assertTrue("Should have more than 10 townships in Yangon/Mandalay", townships.size >= 10)
    val bahan = townships.find { it.id == "ygn_bahan" }
    assertNotNull(bahan)
    assertEquals(1500, bahan?.deliveryFeeMMK)
  }

  @Test
  fun `verify cart pricing and add-ons calculation`() {
    val repo = FoodRepository(createMockOrderDao())
    val fish = repo.menuItems.first { it.id == "seafood_barramundi_whole" }
    val lemonAddon = FoodAddOn("addon_lemon_chili", "Fresh Lime & Chili Dip", "သံပရာငရုတ်သီးဆော့စ်", 1500)
    
    val cartItem = CartItem(
      foodItem = fish,
      quantity = 2,
      selectedAddOns = listOf(lemonAddon)
    )
    
    // Single item = 35000 + 1500 = 36500. For qty 2 = 73000 MMK
    assertEquals(36500, cartItem.unitPriceWithAddOns)
    assertEquals(73000, cartItem.totalPrice)
  }

  @Test
  fun `verify product fulfillment regional restriction and discount calculation`() {
    val baseFish = FoodItem(
      id = "custom_test_fish",
      nameEn = "Test Seabass",
      nameMy = "စမ်းသပ်ငါး",
      descriptionEn = "Test description",
      descriptionMy = "စမ်းသပ်ဖော်ပြချက်",
      priceMMK = 20000,
      category = FoodCategory.FISH,
      allowSelfPickup = true,
      allowDelivery = true,
      allowedRegions = "Yangon, Mandalay",
      minPurchaseQty = 2,
      minPurchaseAmountMMK = 30000,
      coldStorageFeeMMK = 2500,
      specialPrepFeeMMK = 1500,
      discountMinQty = 3,
      discountPercentByQty = 10,
      discountMinAmountMMK = 50000,
      discountPercentByAmount = 15
    )

    // Regional checks
    assertTrue(baseFish.isRegionAllowed("Yangon"))
    assertTrue(baseFish.isRegionAllowed("Mandalay"))
    assertFalse(baseFish.isRegionAllowed("Naypyidaw"))

    // Test cold storage and special prep fees
    val itemWithFees = CartItem(
      foodItem = baseFish,
      quantity = 2,
      includeColdStorage = true,
      includeSpecialPrep = true
    )
    // Unit price = 20000 + 2500 + 1500 = 24000 MMK
    assertEquals(24000, itemWithFees.unitPriceWithAddOns)
    // Qty 2 -> subtotalBeforeDiscount = 48000
    assertEquals(48000, itemWithFees.totalPrice)

    // Test discount: qty 3 -> raw amount = 60000 >= 50000 threshold -> 15% discount = 9000 MMK
    val itemWithDiscount = CartItem(
      foodItem = baseFish,
      quantity = 3,
      includeColdStorage = false,
      includeSpecialPrep = false
    )
    val discountResult = baseFish.calculateDiscount(3)
    assertEquals(9000, discountResult.first)
    assertEquals(51000, itemWithDiscount.totalPrice)
  }

  @Test
  fun `verify bilingual currency and status strings`() {
    val priceTextBurmese = Strings.mmkCurrency(Language.BURMESE, 35000)
    val priceTextEnglish = Strings.mmkCurrency(Language.ENGLISH, 35000)
    
    assertTrue(priceTextBurmese.contains("ကျပ်"))
    assertTrue(priceTextEnglish.contains("MMK"))
    
    assertEquals("အော်ဒါ ပြင်ဆင်နေပါသည်", OrderStatus.PREPARING.title(Language.BURMESE))
    assertEquals("Order Preparing", OrderStatus.PREPARING.title(Language.ENGLISH))
    assertEquals(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.PREPARING.nextStatus())
  }

  @Test
  fun `verify menu filtering by category`() {
    val repo = FoodRepository(createMockOrderDao())
    val allItems = repo.menuItems
    assertTrue(allItems.isNotEmpty())
    
    val fishItems = allItems.filter { it.category == FoodCategory.FISH }
    assertTrue("Should contain fish items", fishItems.isNotEmpty())
    assertTrue(fishItems.all { it.category == FoodCategory.FISH })

    val prawnItems = allItems.filter { it.category == FoodCategory.SHRIMP }
    assertTrue("Should contain prawn items", prawnItems.isNotEmpty())
  }

  @Test
  fun `verify kbzpay account details and localized strings`() {
    val kbzNumber = Strings.kbzAccountNumber()
    assertTrue(kbzNumber.isNotEmpty())
    assertTrue(kbzNumber.contains("09-789"))

    val accNameBurmese = Strings.kbzAccountName(Language.BURMESE)
    val accNameEnglish = Strings.kbzAccountName(Language.ENGLISH)
    assertTrue(accNameBurmese.contains("ဒေါ်ပွင့်စံ"))
    assertTrue(accNameEnglish.contains("Daw Pwint San"))

    val titleMm = Strings.kbzPayTitle(Language.BURMESE)
    val titleEn = Strings.kbzPayTitle(Language.ENGLISH)
    assertTrue(titleMm.contains("KBZPay"))
    assertTrue(titleEn.contains("KBZPay"))
  }

  @Test
  fun `verify home tab default and bilingual titles`() {
    val homeMm = Strings.homeTab(Language.BURMESE)
    val homeEn = Strings.homeTab(Language.ENGLISH)
    assertEquals("ပင်မစာမျက်နှာ", homeMm)
    assertEquals("Home", homeEn)
  }

  @Test
  fun `verify guest user and auth bilingual strings`() {
    val guestMm = Strings.guestUser(Language.BURMESE)
    val guestEn = Strings.guestUser(Language.ENGLISH)
    assertEquals("ဧည့်သည်တော်", guestMm)
    assertEquals("Guest User", guestEn)

    val loginOrSignUpMm = Strings.loginOrSignUp(Language.BURMESE)
    val loginOrSignUpEn = Strings.loginOrSignUp(Language.ENGLISH)
    assertTrue(loginOrSignUpMm.isNotEmpty())
    assertEquals("Log In or Sign Up", loginOrSignUpEn)

    val continueAsGuestEn = Strings.continueAsGuest(Language.ENGLISH)
    assertEquals("Continue as Guest", continueAsGuestEn)
  }

  @Test
  fun `verify logout resets guest state and keeps user on home screen`() = runTest {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = FoodOrderViewModel(application)

    // Set authenticated profile
    viewModel.loginOrSignUp("Mg Aung", "09777123456")
    org.robolectric.shadows.ShadowLooper.idleMainLooper()
    assertEquals("Mg Aung", viewModel.customerName.value)
    assertEquals("09777123456", viewModel.customerPhone.value)
    assertFalse(viewModel.isGuestUser.value)
    assertFalse(viewModel.showAuthDialog.value)

    // Logout
    viewModel.logout()
    org.robolectric.shadows.ShadowLooper.idleMainLooper()

    // Assert that profile is cleared, guest user is true, home tab is active, and no unwanted popup
    assertTrue(viewModel.customerName.value.isEmpty())
    assertTrue(viewModel.customerPhone.value.isEmpty())
    assertTrue(viewModel.isGuestUser.value)
    assertFalse(viewModel.showAuthDialog.value)
    assertEquals(FoodOrderViewModel.ScreenTab.HOME, viewModel.currentTab.value)
  }

  @Test
  fun `verify admin sections and slide drawer menu items`() {
    val sections = com.example.model.AdminSection.entries
    assertEquals(13, sections.size)
    assertEquals("Overview", com.example.model.AdminSection.OVERVIEW.titleEn)
    assertEquals("Products", com.example.model.AdminSection.PRODUCTS.titleEn)
    assertEquals("Orders", com.example.model.AdminSection.ORDERS.titleEn)
    assertEquals("Customers", com.example.model.AdminSection.CUSTOMERS.titleEn)
    assertEquals("Inventory", com.example.model.AdminSection.INVENTORY.titleEn)
    assertEquals("Payments", com.example.model.AdminSection.PAYMENTS.titleEn)
    assertEquals("Logistics", com.example.model.AdminSection.LOGISTICS.titleEn)
    assertEquals("Promotions", com.example.model.AdminSection.PROMOTIONS.titleEn)
    assertEquals("Reports", com.example.model.AdminSection.REPORTS.titleEn)
    assertEquals("Settings", com.example.model.AdminSection.SETTINGS.titleEn)
    assertEquals("Returns & Refunds", com.example.model.AdminSection.RETURNS.titleEn)
    assertEquals("Messages", com.example.model.AdminSection.MESSAGES.titleEn)
    assertEquals("Reviews & Ratings", com.example.model.AdminSection.REVIEWS.titleEn)

    assertEquals("ခြုံငုံသုံးသပ်ချက်", com.example.model.AdminSection.OVERVIEW.title(Language.BURMESE))
    assertEquals("ကုန်ပစ္စည်းများ", com.example.model.AdminSection.PRODUCTS.title(Language.BURMESE))
    assertEquals("ပို့ဆောင်ရေး", com.example.model.AdminSection.LOGISTICS.title(Language.BURMESE))
  }

  @Test
  fun `verify admin quick login and section navigation`() = runTest {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = FoodOrderViewModel(application)

    assertFalse(viewModel.isAdmin.value)

    viewModel.loginAsAdmin()
    org.robolectric.shadows.ShadowLooper.idleMainLooper()

    assertTrue(viewModel.isAdmin.value)
    assertEquals(FoodOrderViewModel.ScreenTab.ADMIN, viewModel.currentTab.value)
    assertEquals(com.example.model.AdminSection.OVERVIEW, viewModel.selectedAdminSection.value)

    viewModel.setAdminSection(com.example.model.AdminSection.INVENTORY)
    assertEquals(com.example.model.AdminSection.INVENTORY, viewModel.selectedAdminSection.value)

    viewModel.setAdminSection(com.example.model.AdminSection.PAYMENTS)
    assertEquals(com.example.model.AdminSection.PAYMENTS, viewModel.selectedAdminSection.value)
  }

  @Test
  fun `verify editable logistics budget limits and custom options`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = FoodOrderViewModel(application)
    org.robolectric.shadows.ShadowLooper.idleMainLooper()

    // Test custom editable minimum delivery order limit and express options
    val customConfig = viewModel.logisticsConfig.value.copy(
        minOrderAmountMMK = 18500,
        freeDeliveryThresholdMMK = 65000,
        expressDeliveryFeeMMK = 2800,
        expressDeliveryMinutes = 22,
        standardDeliveryFeeMMK = 2200,
        coldChainPackagingFeeMMK = 1200
    )
    viewModel.updateLogisticsConfig(customConfig)
    org.robolectric.shadows.ShadowLooper.idleMainLooper()

    assertEquals(18500, viewModel.logisticsConfig.value.minOrderAmountMMK)
    assertEquals(65000, viewModel.logisticsConfig.value.freeDeliveryThresholdMMK)
    assertEquals(2800, viewModel.logisticsConfig.value.expressDeliveryFeeMMK)
    assertEquals(22, viewModel.logisticsConfig.value.expressDeliveryMinutes)
    assertEquals(2200, viewModel.logisticsConfig.value.standardDeliveryFeeMMK)
    assertEquals(1200, viewModel.logisticsConfig.value.coldChainPackagingFeeMMK)
    // Verify synchronized with store settings
    assertEquals(18500, viewModel.storeSettings.value.minOrderAmountMMK)
    assertEquals(2200, viewModel.storeSettings.value.standardDeliveryFeeMMK)
  }

  @Test
  fun `verify product entity mapping preserves all fields across admin and user storefront`() {
    val repo = FoodRepository(createMockOrderDao())
    val originalProduct = ProductEntity(
      id = "test_e2e_prod",
      sku = "SKU-TEST-999",
      nameEn = "Giant Mud Crab (Fresh)",
      nameMy = "ဂဏန်းမည်း အရှင် (လတ်ဆတ်)",
      descriptionEn = "Live mud crab sourced directly from Ayeyarwady mangroves.",
      descriptionMy = "ဧရာဝတီ ဒီရေတောမှ လတ်လတ်ဆတ်ဆတ် ဖမ်းဆီးထားသော ရွှံ့ဂဏန်းမည်း။",
      priceMMK = 45000,
      originalPriceMMK = 52000,
      specifications = "Weight: 800g - 1kg per crab",
      variants = "Medium, Large, Extra Large",
      unitEn = "per 1 kg",
      unitMy = "၁ ကီလို",
      category = "CRAB",
      prepStyle = "READY_TO_COOK",
      occasion = "FAMILY_DINNER",
      prepTimeMin = 25,
      isPopular = true,
      isSpicy = false,
      isPremium = true,
      isAvailable = true,
      imageUrl = "https://example.com/mud_crab.jpg",
      iconEmoji = "🦀",
      stockQuantity = 42,
      isArchived = false,
      brand = "Ayeyarwady Crab Co",
      allowSelfPickup = true,
      allowDelivery = true,
      allowedRegions = "Yangon, Mandalay, Bago",
      minPurchaseQty = 2,
      minPurchaseAmountMMK = 80000,
      coldStorageFeeMMK = 3000,
      coldStorageTitleEn = "Dry Ice Packaging",
      coldStorageTitleMy = "ရေခဲခြောက်ထုပ်ပိုးမှု",
      specialPrepFeeMMK = 2000,
      specialPrepTitleEn = "Shell Cleaned & Split",
      specialPrepTitleMy = "အခွံခွာဆေးကြော သန့်စင်ပေးခြင်း",
      discountMinQty = 5,
      discountPercentByQty = 8,
      discountMinAmountMMK = 200000,
      discountPercentByAmount = 12
    )

    // Convert to User storefront model
    val userFoodItem = originalProduct.toFoodItem(repo.commonSeafoodAddOns)

    // Verify all fields are preserved
    assertEquals("test_e2e_prod", userFoodItem.id)
    assertEquals("SKU-TEST-999", userFoodItem.sku)
    assertEquals("Giant Mud Crab (Fresh)", userFoodItem.nameEn)
    assertEquals("ဂဏန်းမည်း အရှင် (လတ်ဆတ်)", userFoodItem.nameMy)
    assertEquals("Live mud crab sourced directly from Ayeyarwady mangroves.", userFoodItem.descriptionEn)
    assertEquals("Ayeyarwady Crab Co", userFoodItem.brand)
    assertEquals(45000, userFoodItem.priceMMK)
    assertEquals(52000, userFoodItem.originalPriceMMK)
    assertEquals(42, userFoodItem.stockQuantity)
    assertEquals(false, userFoodItem.isArchived)
    assertEquals("https://example.com/mud_crab.jpg", userFoodItem.imageUrl)
    assertEquals("Yangon, Mandalay, Bago", userFoodItem.allowedRegions)
    assertEquals(3000, userFoodItem.coldStorageFeeMMK)
    assertEquals(2000, userFoodItem.specialPrepFeeMMK)
    assertEquals(5, userFoodItem.discountMinQty)
    assertEquals(8, userFoodItem.discountPercentByQty)
    assertEquals(200000, userFoodItem.discountMinAmountMMK)
    assertEquals(12, userFoodItem.discountPercentByAmount)

    // Verify regional and fee checks
    assertTrue(userFoodItem.isRegionAllowed("Yangon"))
    assertTrue(userFoodItem.isRegionAllowed("Mandalay"))
    assertFalse(userFoodItem.isRegionAllowed("Sittwe"))

    // Convert back to ProductEntity (as Admin would save after edits)
    val editedFoodItem = userFoodItem.copy(
      priceMMK = 42000,
      stockQuantity = 38,
      brand = "Premium Ayeyarwady"
    )
    val reSavedEntity = ProductEntity.fromFoodItem(editedFoodItem)
    assertEquals(42000, reSavedEntity.priceMMK)
    assertEquals(38, reSavedEntity.stockQuantity)
    assertEquals("Premium Ayeyarwady", reSavedEntity.brand)
  }

  @Test
  fun `verify order item snapshot serialization for order creation and inventory restoration`() {
    val snapshots = listOf(
      OrderItemSnapshot(
        productId = "crab_001",
        nameEn = "Mud Crab",
        nameMy = "ဂဏန်း",
        unitPriceMMK = 45000,
        quantity = 2,
        subtotalMMK = 90000,
        specialNotes = "No spicy",
        fulfillmentPreference = "DELIVERY"
      )
    )
    val json = OrderItemSnapshot.listToJsonString(snapshots)
    assertTrue(json.contains("crab_001"))
    assertTrue(json.contains("90000"))

    val parsed = OrderItemSnapshot.listFromJsonString(json)
    assertEquals(1, parsed.size)
    assertEquals("crab_001", parsed[0].productId)
    assertEquals(2, parsed[0].quantity)
    assertEquals(90000, parsed[0].subtotalMMK)
  }

  private fun createMockOrderDao(): OrderDao {
    return object : OrderDao {
      override fun getAllOrders(): Flow<List<OrderEntity>> = flowOf(emptyList())
      override fun getOrderById(orderId: String): Flow<OrderEntity?> = flowOf(null)
      override fun getLatestActiveOrder(): Flow<OrderEntity?> = flowOf(null)
      override suspend fun insertOrder(order: OrderEntity) {}
      override suspend fun updateOrder(order: OrderEntity) {}
      override suspend fun updateOrderStatus(orderId: String, status: String, updatedAt: Long) {}
      override suspend fun updatePaymentStatus(orderId: String, paymentStatus: String) {}
      override suspend fun updateTrackingInfo(orderId: String, trackingNumber: String, partnerName: String) {}
      override suspend fun cancelOrderWithReason(orderId: String, reason: String, cancelledAt: Long) {}
      override suspend fun requestOrderReturn(orderId: String, returnReason: String, requestedAt: Long) {}
      override suspend fun updateReturnStatus(orderId: String, returnStatus: String, paymentStatus: String) {}
      override suspend fun updateDeliveredAt(orderId: String, deliveredAt: Long) {}
      override suspend fun deleteOrder(orderId: String) {}
      override fun getUserProfile(): Flow<UserProfileEntity?> = flowOf(null)
      override suspend fun saveUserProfile(profile: UserProfileEntity) {}
      override suspend fun clearUserProfile() {}
      override fun getAllProducts(): Flow<List<com.example.data.ProductEntity>> = flowOf(emptyList())
      override suspend fun getProductCount(): Int = 0
      override suspend fun insertProduct(product: com.example.data.ProductEntity) {}
      override suspend fun insertProducts(products: List<com.example.data.ProductEntity>) {}
      override suspend fun updateProduct(product: com.example.data.ProductEntity) {}
      override suspend fun deleteProduct(id: String) {}
      override suspend fun setProductArchived(id: String, isArchived: Boolean) {}
      override suspend fun updateProductStock(id: String, quantity: Int) {}
      override suspend fun updateProductPrice(id: String, price: Int) {}
      override fun getBusinessSettings(): Flow<com.example.data.BusinessSettingsEntity?> = flowOf(null)
      override suspend fun getBusinessSettingsCount(): Int = 0
      override suspend fun saveBusinessSettings(settings: com.example.data.BusinessSettingsEntity) {}
      override fun getAllCategories(): Flow<List<com.example.data.CategoryEntity>> = flowOf(emptyList())
      override suspend fun getCategoryCount(): Int = 0
      override suspend fun insertCategory(category: com.example.data.CategoryEntity) {}
      override suspend fun insertCategories(categories: List<com.example.data.CategoryEntity>) {}
      override suspend fun updateCategory(category: com.example.data.CategoryEntity) {}
      override suspend fun setCategoryArchived(id: String, isArchived: Boolean) {}
      override suspend fun deleteCategory(id: String) {}
      override fun getAllBrands(): Flow<List<com.example.data.BrandEntity>> = flowOf(emptyList())
      override suspend fun getBrandCount(): Int = 0
      override suspend fun insertBrand(brand: com.example.data.BrandEntity) {}
      override suspend fun insertBrands(brands: List<com.example.data.BrandEntity>) {}
      override suspend fun updateBrand(brand: com.example.data.BrandEntity) {}
      override suspend fun setBrandArchived(id: String, isArchived: Boolean) {}
      override suspend fun deleteBrand(id: String) {}
      override fun getAllUserAccounts(): Flow<List<com.example.data.UserAccountEntity>> = flowOf(emptyList())
      override suspend fun getUserAccountCount(): Int = 0
      override suspend fun getUserAccountByPhone(phone: String): com.example.data.UserAccountEntity? = null
      override suspend fun insertUserAccount(user: com.example.data.UserAccountEntity) {}
      override suspend fun insertUserAccounts(users: List<com.example.data.UserAccountEntity>) {}
      override suspend fun updateUserAccountDetails(phone: String, name: String, email: String, preferredTownshipId: String, defaultAddressNote: String, adminNotes: String) {}
      override suspend fun deleteUserAccount(phone: String) {}
      override fun getAllReviews(): Flow<List<com.example.data.ReviewEntity>> = flowOf(emptyList())
      override fun getReviewsForProduct(productId: String): Flow<List<com.example.data.ReviewEntity>> = flowOf(emptyList())
      override suspend fun insertReview(review: com.example.data.ReviewEntity) {}
      override suspend fun updateReview(review: com.example.data.ReviewEntity) {}
      override suspend fun replyToReview(reviewId: String, reply: String) {}
      override suspend fun deleteReview(id: String) {}
      override fun getAllCustomerMessages(): Flow<List<com.example.data.CustomerMessageEntity>> = flowOf(emptyList())
      override suspend fun insertCustomerMessage(message: com.example.data.CustomerMessageEntity) {}
      override suspend fun updateCustomerMessage(message: com.example.data.CustomerMessageEntity) {}
      override suspend fun replyToCustomerMessage(id: String, adminReply: String, status: String, repliedAt: Long) {}
      override suspend fun deleteCustomerMessage(id: String) {}
    }
  }
}


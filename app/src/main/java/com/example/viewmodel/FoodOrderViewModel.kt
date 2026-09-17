package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BrandEntity
import com.example.data.BusinessSettingsEntity
import com.example.data.CategoryEntity
import com.example.data.CustomerMessageEntity
import com.example.data.FoodRepository
import com.example.data.OrderEntity
import com.example.data.ProductEntity
import com.example.data.ReviewEntity
import com.example.data.UserAccountEntity
import com.example.data.UserProfileEntity
import com.example.model.AdminSection
import com.example.model.BrowseCategoryMode
import com.example.model.CartItem
import com.example.model.DeliveryPartner
import com.example.model.DeliveryType
import com.example.model.DiningOccasion
import com.example.model.FoodAddOn
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.Language
import com.example.model.LogisticsConfig
import com.example.model.MyanmarTownship
import com.example.model.MyanmarTownshipsData
import com.example.model.OrderItemSnapshot
import com.example.model.OrderStatus
import com.example.model.PaymentConfig
import com.example.model.PaymentMethod
import com.example.model.PrepStyle
import com.example.model.PriceRangeFilter
import com.example.model.PromotionItem
import com.example.model.QrPaymentOption
import com.example.model.StoreSettingsConfig
import com.example.model.Strings
import com.example.notification.OrderNotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class FoodOrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FoodRepository
    private var autoSimulatorJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FoodRepository(database.orderDao())
        viewModelScope.launch {
            repository.initializeDefaultsIfEmpty()
        }
        viewModelScope.launch {
            repository.allProducts.collect { dbProducts ->
                val productMap = dbProducts.associateBy { it.id }
                _cartItems.update { currentItems ->
                    currentItems.map { cartItem ->
                        val updated = productMap[cartItem.foodItem.id]
                        if (updated != null) {
                            cartItem.copy(foodItem = updated.toFoodItem(repository.commonSeafoodAddOns))
                        } else {
                            cartItem
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.businessSettings.collect { settings ->
                if (settings != null) {
                    _logisticsConfig.update {
                        it.copy(
                            standardDeliveryFeeMMK = settings.standardDeliveryFeeMMK,
                            expressDeliveryFeeMMK = settings.expressDeliveryFeeMMK,
                            freeDeliveryThresholdMMK = settings.freeDeliveryThresholdMMK,
                            freeDeliveryEnabled = settings.freeDeliveryEnabled,
                            minOrderAmountMMK = settings.minOrderAmountMMK
                        )
                    }
                    _storeSettings.update {
                        it.copy(
                            storeName = settings.brandNameEn,
                            storePhone = settings.storePhone,
                            storeAddress = settings.storeAddressEn,
                            businessHours = settings.openingHoursEn,
                            minOrderAmountMMK = settings.minOrderAmountMMK,
                            standardDeliveryFeeMMK = settings.standardDeliveryFeeMMK,
                            freeDeliveryThresholdMMK = settings.freeDeliveryThresholdMMK
                        )
                    }
                }
            }
        }
    }

    // UI Navigation Tab
    enum class ScreenTab {
        HOME, TRACKING, HISTORY, PROFILE, ADMIN;
        companion object {
            val MENU = HOME
        }
    }
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    data class AdminNavLocation(
        val tab: ScreenTab,
        val adminSection: AdminSection = AdminSection.OVERVIEW
    )

    private val adminNavHistory = mutableListOf<AdminNavLocation>()

    fun adminNavigateBack(): Boolean {
        if (adminNavHistory.isNotEmpty()) {
            val previous = adminNavHistory.removeAt(adminNavHistory.lastIndex)
            _currentTab.value = previous.tab
            _selectedAdminSection.value = previous.adminSection
            return true
        } else if (_currentTab.value != ScreenTab.ADMIN || _selectedAdminSection.value != AdminSection.OVERVIEW) {
            _currentTab.value = ScreenTab.ADMIN
            _selectedAdminSection.value = AdminSection.OVERVIEW
            return true
        }
        return false
    }

    fun navigateTo(tab: ScreenTab) {
        if (_currentUserRole.value == "ADMIN") {
            val currentLoc = AdminNavLocation(_currentTab.value, _selectedAdminSection.value)
            val targetLoc = AdminNavLocation(tab, _selectedAdminSection.value)
            if (currentLoc != targetLoc) {
                adminNavHistory.add(currentLoc)
            }
        }
        _currentTab.value = tab
    }

    fun resetToHome() {
        _currentTab.value = ScreenTab.HOME
        _searchQuery.value = ""
        _selectedCategory.value = FoodCategory.ALL
    }

    // Language State
    private val _currentLanguage = MutableStateFlow(Language.BURMESE)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == Language.BURMESE) Language.ENGLISH else Language.BURMESE
    }

    fun setLanguage(lang: Language) {
        _currentLanguage.value = lang
    }

    // Search and Browsing Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _browseMode = MutableStateFlow(BrowseCategoryMode.BY_SEAFOOD_TYPE)
    val browseMode: StateFlow<BrowseCategoryMode> = _browseMode.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FoodCategory.ALL)
    val selectedCategory: StateFlow<FoodCategory> = _selectedCategory.asStateFlow()

    private val _selectedPrepStyle = MutableStateFlow(PrepStyle.ALL)
    val selectedPrepStyle: StateFlow<PrepStyle> = _selectedPrepStyle.asStateFlow()

    private val _selectedOccasion = MutableStateFlow(DiningOccasion.ALL)
    val selectedOccasion: StateFlow<DiningOccasion> = _selectedOccasion.asStateFlow()

    private val _selectedPriceRange = MutableStateFlow(PriceRangeFilter.ALL)
    val selectedPriceRange: StateFlow<PriceRangeFilter> = _selectedPriceRange.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setBrowseMode(mode: BrowseCategoryMode) {
        _browseMode.value = mode
    }

    fun setSelectedCategory(category: FoodCategory) {
        _selectedCategory.value = category
    }

    fun setSelectedPrepStyle(style: PrepStyle) {
        _selectedPrepStyle.value = style
    }

    fun setSelectedOccasion(occasion: DiningOccasion) {
        _selectedOccasion.value = occasion
    }

    fun setSelectedPriceRange(range: PriceRangeFilter) {
        _selectedPriceRange.value = range
    }

    fun clearAllFilters() {
        _selectedCategory.value = FoodCategory.ALL
        _selectedPrepStyle.value = PrepStyle.ALL
        _selectedOccasion.value = DiningOccasion.ALL
        _selectedPriceRange.value = PriceRangeFilter.ALL
        _searchQuery.value = ""
    }

    val activeFilterCount: StateFlow<Int> = combine(
        _selectedCategory,
        _selectedPrepStyle,
        _selectedOccasion,
        _selectedPriceRange
    ) { cat, prep, occ, price ->
        var count = 0
        if (cat != FoodCategory.ALL) count++
        if (prep != PrepStyle.ALL) count++
        if (occ != DiningOccasion.ALL) count++
        if (price != PriceRangeFilter.ALL) count++
        count
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Centralized Business Settings from Room Database
    val businessSettings: StateFlow<BusinessSettingsEntity> = repository.businessSettings
        .map { it ?: BusinessSettingsEntity() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BusinessSettingsEntity())

    // Dynamic Categories and Brands from Room Database
    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBrands: StateFlow<List<BrandEntity>> = repository.allBrands
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin view of products (both active and archived)
    val adminAllProducts: StateFlow<List<FoodItem>> = repository.allProducts
        .map { prods ->
            if (prods.isEmpty()) {
                repository.menuItems
            } else {
                prods.map { it.toFoodItem(repository.commonSeafoodAddOns) }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.menuItems)

    // Reactive products list for customer storefront (excludes archived/soft-deleted items)
    val allProductsList: StateFlow<List<FoodItem>> = repository.allProducts
        .map { prods ->
            val active = prods.filter { !it.isArchived }
            if (active.isEmpty() && prods.isEmpty()) {
                repository.menuItems
            } else {
                active.map { it.toFoodItem(repository.commonSeafoodAddOns) }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.menuItems)

    data class MenuFilters(
        val query: String,
        val category: FoodCategory,
        val prep: PrepStyle,
        val occasion: DiningOccasion,
        val priceRange: PriceRangeFilter
    )

    private val _menuFilters: StateFlow<MenuFilters> = combine(
        combine(_searchQuery, _selectedCategory) { q, c -> Pair(q, c) },
        _selectedPrepStyle,
        _selectedOccasion,
        _selectedPriceRange
    ) { (query, category), prep, occasion, priceRange ->
        MenuFilters(query, category, prep, occasion, priceRange)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MenuFilters("", FoodCategory.ALL, PrepStyle.ALL, DiningOccasion.ALL, PriceRangeFilter.ALL)
    )

    // Filtered Food Menu
    val filteredMenuItems: StateFlow<List<FoodItem>> = combine(
        allProductsList,
        _menuFilters
    ) { products, filters ->
        products.filter { item ->
            val matchesCategory = (filters.category == FoodCategory.ALL || item.category == filters.category)
            val matchesPrep = (filters.prep == PrepStyle.ALL || item.prepStyle == filters.prep)
            val matchesOccasion = (filters.occasion == DiningOccasion.ALL || item.occasion == filters.occasion)
            val matchesPrice = filters.priceRange.matches(item.priceMMK, item.isPremium)
            val matchesSearch = filters.query.isBlank() ||
                    item.nameEn.contains(filters.query, ignoreCase = true) ||
                    item.nameMy.contains(filters.query, ignoreCase = true) ||
                    item.descriptionEn.contains(filters.query, ignoreCase = true) ||
                    item.descriptionMy.contains(filters.query, ignoreCase = true) ||
                    item.brand.contains(filters.query, ignoreCase = true) ||
                    item.effectiveSku.contains(filters.query, ignoreCase = true)
            matchesCategory && matchesPrep && matchesOccasion && matchesPrice && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.menuItems)

    // Cart Management
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val cartTotalCount: StateFlow<Int> = _cartItems.combine(_cartItems) { items, _ ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotalMMK: StateFlow<Int> = _cartItems.combine(_cartItems) { items, _ ->
        items.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Customer Info (Zero Registration, Minimalist Form)
    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _customerPhone = MutableStateFlow("")
    val customerPhone: StateFlow<String> = _customerPhone.asStateFlow()

    // Logistics Configuration
    private val _logisticsConfig = MutableStateFlow(LogisticsConfig())
    val logisticsConfig: StateFlow<LogisticsConfig> = _logisticsConfig.asStateFlow()

    // Dynamic Townships List (Managed by Logistics Admin)
    private val _townships = MutableStateFlow<List<MyanmarTownship>>(MyanmarTownshipsData.allTownships)
    val townships: StateFlow<List<MyanmarTownship>> = _townships.asStateFlow()

    // Delivery Type (Standard Delivery, Express Rush, Store Self-Pickup)
    private val _deliveryType = MutableStateFlow(DeliveryType.STANDARD)
    val deliveryType: StateFlow<DeliveryType> = _deliveryType.asStateFlow()

    fun setDeliveryType(type: DeliveryType) {
        _deliveryType.value = type
    }

    private val _selectedTownship = MutableStateFlow<MyanmarTownship>(MyanmarTownshipsData.defaultTownship)
    val selectedTownship: StateFlow<MyanmarTownship> = _selectedTownship.asStateFlow()

    private val _deliveryAddressNote = MutableStateFlow("")
    val deliveryAddressNote: StateFlow<String> = _deliveryAddressNote.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.COD)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    // Calculated Grand Total with Dynamic Logistics (Free Delivery rule, Rush extra fee, Pickup discount)
    val deliveryFeeMMK: StateFlow<Int> = combine(
        _selectedTownship,
        cartSubtotalMMK,
        _deliveryType,
        _logisticsConfig
    ) { township, subtotal, type, logistics ->
        when (type) {
            DeliveryType.PICKUP -> 0
            DeliveryType.EXPRESS -> {
                val base = if (logistics.freeDeliveryEnabled && logistics.freeDeliveryThresholdMMK > 0 && subtotal >= logistics.freeDeliveryThresholdMMK) {
                    0
                } else {
                    township.deliveryFeeMMK
                }
                base + logistics.expressDeliveryFeeMMK
            }
            DeliveryType.STANDARD -> {
                if (logistics.freeDeliveryEnabled && logistics.freeDeliveryThresholdMMK > 0 && subtotal >= logistics.freeDeliveryThresholdMMK) {
                    0
                } else {
                    township.deliveryFeeMMK
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyanmarTownshipsData.defaultTownship.deliveryFeeMMK)

    val grandTotalMMK: StateFlow<Int> = combine(cartSubtotalMMK, deliveryFeeMMK) { subtotal, fee ->
        if (subtotal > 0) subtotal + fee else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Orders Flow from Room Database
    val orderHistory: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allOrders: StateFlow<List<OrderEntity>> get() = orderHistory

    // Currently Selected or Active Tracked Order
    private val _trackedOrderId = MutableStateFlow<String?>(null)
    val trackedOrderId: StateFlow<String?> = _trackedOrderId.asStateFlow()

    val currentTrackedOrder: StateFlow<OrderEntity?> = combine(
        orderHistory,
        _trackedOrderId
    ) { orders, trackedId ->
        if (trackedId != null) {
            orders.find { it.orderId == trackedId }
        } else {
            // Default to latest active or latest placed order
            orders.firstOrNull { it.status != OrderStatus.DELIVERED.name && it.status != OrderStatus.CANCELLED.name }
                ?: orders.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // In-App Notification Toast Banner
    private val _inAppNotification = MutableStateFlow<String?>(null)
    val inAppNotification: StateFlow<String?> = _inAppNotification.asStateFlow()

    // KBZ Pay QR & 10-Minute Timer State
    private val _showKbzPayDialog = MutableStateFlow(false)
    val showKbzPayDialog: StateFlow<Boolean> = _showKbzPayDialog.asStateFlow()

    private val _kbzPayTimeRemainingSeconds = MutableStateFlow(600) // 10 minutes
    val kbzPayTimeRemainingSeconds: StateFlow<Int> = _kbzPayTimeRemainingSeconds.asStateFlow()

    private val _isKbzPayPaid = MutableStateFlow(false)
    val isKbzPayPaid: StateFlow<Boolean> = _isKbzPayPaid.asStateFlow()

    private val _kbzPayOrderId = MutableStateFlow<String?>(null)
    val kbzPayOrderId: StateFlow<String?> = _kbzPayOrderId.asStateFlow()

    // Auth & Guest Mode Dialog State
    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    // Current User Role: "GUEST", "CUSTOMER", "ADMIN"
    private val _currentUserRole = MutableStateFlow("GUEST")
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    val isAdmin: StateFlow<Boolean> = combine(_currentUserRole, _currentUserRole) { role, _ ->
        role == "ADMIN"
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // User Accounts list for Admin inspection
    val allUserAccounts: StateFlow<List<UserAccountEntity>> = repository.allUserAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allUsersList: StateFlow<List<UserAccountEntity>> get() = allUserAccounts

    // Customer Reviews & Ratings
    val allReviews: StateFlow<List<ReviewEntity>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Customer Inquiries & Messages
    val allCustomerMessages: StateFlow<List<CustomerMessageEntity>> = repository.allCustomerMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isGuestUser: StateFlow<Boolean> = combine(_customerName, _customerPhone) { name, phone ->
        name.isBlank() && phone.isBlank()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private var kbzTimerJob: Job? = null

    init {
        // Load saved user info from database
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                profile?.let {
                    if (_customerName.value.isBlank() && it.name.isNotBlank()) _customerName.value = it.name
                    if (_customerPhone.value.isBlank() && it.phone.isNotBlank()) _customerPhone.value = it.phone
                    if (it.preferredTownshipId.isNotBlank()) {
                        val savedTownship = repository.getTownshipById(it.preferredTownshipId)
                        _selectedTownship.value = savedTownship
                    }
                    if (_deliveryAddressNote.value.isBlank() && it.defaultAddressNote.isNotBlank()) {
                        _deliveryAddressNote.value = it.defaultAddressNote
                    }
                }
            }
        }
    }

    fun openKbzPayDialog(orderId: String? = null, context: Context? = null) {
        _kbzPayOrderId.value = orderId ?: _trackedOrderId.value
        _showKbzPayDialog.value = true
        if (context != null && kbzTimerJob == null) {
            startKbzPaymentCountdown(context, orderId)
        }
    }

    fun closeKbzPayDialog() {
        _showKbzPayDialog.value = false
    }

    fun confirmKbzPayPaid() {
        _isKbzPayPaid.value = true
        kbzTimerJob?.cancel()
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "KBZPay ဖြင့် ငွေပေးချေမှု အတည်ပြုပြီးပါပြီ ✓"
            else "KBZPay Payment confirmed successfully ✓"
        )
    }

    fun startKbzPaymentCountdown(context: Context, orderId: String? = null) {
        kbzTimerJob?.cancel()
        _kbzPayTimeRemainingSeconds.value = 600
        _isKbzPayPaid.value = false
        _kbzPayOrderId.value = orderId

        kbzTimerJob = viewModelScope.launch {
            while (_kbzPayTimeRemainingSeconds.value > 0) {
                delay(1000)
                _kbzPayTimeRemainingSeconds.value -= 1
            }

            // Timer expired after 10 minutes - if not paid, trigger reminder
            if (!_isKbzPayPaid.value) {
                val targetOrderId = _kbzPayOrderId.value ?: _trackedOrderId.value ?: "PENDING"
                val amount = grandTotalMMK.value
                val lang = _currentLanguage.value

                OrderNotificationHelper.showPaymentReminderNotification(
                    context = context,
                    orderId = targetOrderId,
                    amountMMK = amount,
                    lang = lang,
                    remainingMinutes = 10
                )

                showInAppNotification(
                    if (lang == Language.BURMESE) "⚠️ KBZPay ငွေလွှဲရန် ၁၀ မိနစ်ပြည့်ပါပြီ။ ကျေးဇူးပြု၍ ငွေလွှဲပေးပါရန်။"
                    else "⚠️ KBZPay 10-minute timer ended. Please complete payment to avoid cancellation."
                )

                // Restart timer loop for next reminder cycle
                _kbzPayTimeRemainingSeconds.value = 600
                startKbzPaymentCountdown(context, orderId)
            }
        }
    }

    fun simulateKbzTimerExpiry(context: Context) {
        val targetOrderId = _kbzPayOrderId.value ?: _trackedOrderId.value ?: "MM-TEST"
        val amount = if (grandTotalMMK.value > 0) grandTotalMMK.value else 12500
        val lang = _currentLanguage.value

        OrderNotificationHelper.showPaymentReminderNotification(
            context = context,
            orderId = targetOrderId,
            amountMMK = amount,
            lang = lang,
            remainingMinutes = 10
        )

        showInAppNotification(
            if (lang == Language.BURMESE) "⚠️ KBZPay ၁၀ မိနစ် သတိပေးချက် Notification ပို့ပြီးပါပြီ!"
            else "⚠️ Sent 10-minute KBZPay Payment Reminder Push Notification!"
        )
    }

    fun setCustomerName(name: String) {
        _customerName.value = name
    }

    fun setCustomerPhone(phone: String) {
        _customerPhone.value = phone
    }

    fun setSelectedTownship(township: MyanmarTownship) {
        _selectedTownship.value = township
    }

    fun setDeliveryAddressNote(note: String) {
        _deliveryAddressNote.value = note
    }

    fun setPaymentMethod(method: PaymentMethod, context: Context? = null) {
        val config = _paymentConfig.value
        val total = _cartItems.value.sumOf { it.totalPrice } + _selectedTownship.value.deliveryFeeMMK
        val lang = _currentLanguage.value
        if (method == PaymentMethod.COD) {
            if (!config.codEnabled) {
                showInAppNotification(
                    if (lang == Language.BURMESE) "ပစ္စည်းရောက်ငွေချေစနစ်ကို ယာယီပိတ်ထားပါသည်"
                    else "Cash on Delivery is currently disabled"
                )
                return
            }
            if (config.codMinOrderAmountMMK > 0 && total < config.codMinOrderAmountMMK) {
                val diff = config.codMinOrderAmountMMK - total
                val nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
                showInAppNotification(
                    Strings.codLockedNote(lang, nf.format(config.codMinOrderAmountMMK), nf.format(diff))
                )
                return
            }
            if (config.codMaxOrderAmountMMK > 0 && total > config.codMaxOrderAmountMMK) {
                val nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
                showInAppNotification(
                    if (lang == Language.BURMESE) "ပစ္စည်းရောက်ငွေချေစနစ်ကို အများဆုံး ${nf.format(config.codMaxOrderAmountMMK)} ကျပ်အထိသာ လက်ခံပါသည်"
                    else "Cash on Delivery is limited to max ${nf.format(config.codMaxOrderAmountMMK)} MMK"
                )
                return
            }
        }
        _selectedPaymentMethod.value = method
        if (method == PaymentMethod.KPAY) {
            _showKbzPayDialog.value = true
            if (context != null) {
                startKbzPaymentCountdown(context)
            }
        }
    }

    fun setTrackedOrder(orderId: String) {
        _trackedOrderId.value = orderId
        _currentTab.value = ScreenTab.TRACKING
    }

    fun addToCart(
        foodItem: FoodItem,
        quantity: Int = 1,
        spiceLevel: String = "Normal",
        addOns: List<FoodAddOn> = emptyList(),
        notes: String = "",
        includeColdStorage: Boolean = false,
        includeSpecialPrep: Boolean = false,
        selectedFulfillment: String = if (foodItem.allowDelivery) "Delivery" else "Self Pickup"
    ) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst {
            it.foodItem.id == foodItem.id &&
            it.selectedSpiceLevel == spiceLevel &&
            it.selectedAddOns == addOns &&
            it.specialNotes == notes &&
            it.includeColdStorage == includeColdStorage &&
            it.includeSpecialPrep == includeSpecialPrep &&
            it.selectedFulfillment == selectedFulfillment
        }

        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(
                CartItem(
                    foodItem = foodItem,
                    quantity = quantity,
                    selectedSpiceLevel = spiceLevel,
                    selectedAddOns = addOns,
                    specialNotes = notes,
                    includeColdStorage = includeColdStorage,
                    includeSpecialPrep = includeSpecialPrep,
                    selectedFulfillment = selectedFulfillment
                )
            )
        }
        _cartItems.value = current
        showInAppNotification("Added ${foodItem.nameEn} to cart")
    }

    fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOf(cartItem)
        if (index >= 0) {
            if (newQuantity <= 0) {
                current.removeAt(index)
            } else {
                current[index] = cartItem.copy(quantity = newQuantity)
            }
            _cartItems.value = current
        }
    }

    fun removeCartItem(cartItem: CartItem) {
        _cartItems.value = _cartItems.value.filter { it != cartItem }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun placeOrder(context: Context): Result<String> {
        val name = _customerName.value.trim()
        val phone = _customerPhone.value.trim()

        if (name.isBlank() || phone.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your name and phone number"))
        }

        if (_cartItems.value.isEmpty()) {
            return Result.failure(IllegalStateException("Cart is empty"))
        }

        val township = _selectedTownship.value
        val items = _cartItems.value
        val subtotal = items.sumOf { it.totalPrice }
        val fee = deliveryFeeMMK.value
        val grandTotal = subtotal + fee

        val paymentConfig = _paymentConfig.value
        val lang = _currentLanguage.value

        if (_selectedPaymentMethod.value == PaymentMethod.COD) {
            if (!paymentConfig.codEnabled) {
                val msg = if (lang == Language.BURMESE) "ပစ္စည်းရောက်ငွေချေစနစ်ကို ယာယီပိတ်ထားပါသည်" else "Cash on Delivery is currently disabled"
                showInAppNotification(msg)
                return Result.failure(IllegalStateException(msg))
            }
            if (paymentConfig.codMinOrderAmountMMK > 0 && grandTotal < paymentConfig.codMinOrderAmountMMK) {
                val diff = paymentConfig.codMinOrderAmountMMK - grandTotal
                val nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
                val msg = Strings.codLockedNote(lang, nf.format(paymentConfig.codMinOrderAmountMMK), nf.format(diff))
                showInAppNotification(msg)
                return Result.failure(IllegalArgumentException(msg))
            }
            if (paymentConfig.codMaxOrderAmountMMK > 0 && grandTotal > paymentConfig.codMaxOrderAmountMMK) {
                val nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
                val msg = if (lang == Language.BURMESE)
                    "ပစ္စည်းရောက်ငွေချေစနစ်ကို အများဆုံး ${nf.format(paymentConfig.codMaxOrderAmountMMK)} ကျပ်အထိသာ လက်ခံပါသည်"
                else
                    "Cash on Delivery is limited to maximum ${nf.format(paymentConfig.codMaxOrderAmountMMK)} MMK"
                showInAppNotification(msg)
                return Result.failure(IllegalArgumentException(msg))
            }
        }

        // Format items summary e.g. "Mohinga x 2, Shan Noodles x 1"
        val summary = items.joinToString(", ") { item ->
            val itemName = if (lang == Language.BURMESE) item.foodItem.nameMy else item.foodItem.nameEn
            "$itemName x${item.quantity}"
        }

        val typeSuffix = when (_deliveryType.value) {
            DeliveryType.EXPRESS -> " [⚡ Express]"
            DeliveryType.PICKUP -> " [🏬 Self-Pickup]"
            DeliveryType.STANDARD -> ""
        }

        val estMins = when (_deliveryType.value) {
            DeliveryType.EXPRESS -> _logisticsConfig.value.expressDeliveryMinutes
            DeliveryType.PICKUP -> _logisticsConfig.value.defaultPrepTimeMinutes
            DeliveryType.STANDARD -> township.estimatedMinutes
        }

        val orderNumber = "MM-${Random.nextInt(1000, 9999)}"

        // Build complete snapshot of every item and its selected options/variants
        val snapshots = items.map { cartItem ->
            val variantText = if (cartItem.specialNotes.contains("[Variant:")) {
                cartItem.specialNotes.substringAfter("[Variant:").substringBefore("]").trim()
            } else ""
            OrderItemSnapshot(
                productId = cartItem.foodItem.id,
                nameEn = cartItem.foodItem.nameEn,
                nameMy = cartItem.foodItem.nameMy,
                brand = cartItem.foodItem.brand,
                sku = cartItem.foodItem.effectiveSku,
                variant = variantText,
                quantity = cartItem.quantity,
                unitPriceMMK = cartItem.foodItem.priceMMK,
                subtotalMMK = cartItem.totalPrice,
                spiceLevel = cartItem.selectedSpiceLevel,
                addOns = cartItem.selectedAddOns.map { it.nameEn },
                specialNotes = cartItem.specialNotes,
                includeColdStorage = cartItem.includeColdStorage,
                includeSpecialPrep = cartItem.includeSpecialPrep,
                fulfillmentPreference = cartItem.fulfillmentPreference
            )
        }
        val itemsJson = OrderItemSnapshot.listToJsonString(snapshots)

        val partner = _logisticsConfig.value.deliveryPartners.firstOrNull { it.isEnabled }
        val trackingNo = "TRK-${Random.nextInt(100000, 999999)}"
        val initialPaymentStatus = when (_selectedPaymentMethod.value) {
            PaymentMethod.COD -> "COD_PENDING"
            else -> "PENDING"
        }

        val orderEntity = OrderEntity(
            orderId = orderNumber,
            timestamp = System.currentTimeMillis(),
            customerName = name,
            customerPhone = phone,
            customerEmail = "",
            townshipId = township.id,
            townshipNameEn = township.nameEn,
            townshipNameMy = township.nameMy,
            deliveryAddressNote = _deliveryAddressNote.value.trim(),
            itemsSummary = "$summary$typeSuffix",
            totalItemCount = items.sumOf { it.quantity },
            foodSubtotalMMK = subtotal,
            deliveryFeeMMK = fee,
            grandTotalMMK = grandTotal,
            discountMMK = 0,
            promoCode = "",
            paymentMethod = _selectedPaymentMethod.value.id,
            paymentStatus = initialPaymentStatus,
            status = OrderStatus.PLACED.name,
            statusUpdatedAt = System.currentTimeMillis(),
            estimatedMinutes = estMins,
            orderItemsJson = itemsJson,
            deliveryType = _deliveryType.value.name,
            trackingNumber = trackingNo,
            deliveryPartnerName = partner?.name ?: "In-House Delivery",
            customerNotes = _deliveryAddressNote.value.trim()
        )

        viewModelScope.launch {
            // Save order to Room
            repository.saveOrder(orderEntity)

            // Real-time dynamic inventory deduction for ordered seafood items
            items.forEach { cartItem ->
                val currentStock = cartItem.foodItem.stockQuantity
                val newStock = (currentStock - cartItem.quantity).coerceAtLeast(0)
                repository.updateProductStock(cartItem.foodItem.id, newStock)
            }

            // Ensure customer account exists and track user activity in Room
            val existingAccount = repository.getUserAccountByPhone(phone)
            if (existingAccount != null) {
                repository.saveUserAccount(
                    existingAccount.copy(
                        name = name,
                        preferredTownshipId = township.id,
                        defaultAddressNote = _deliveryAddressNote.value.trim(),
                        totalOrdersCount = existingAccount.totalOrdersCount + 1,
                        totalSpentMMK = existingAccount.totalSpentMMK + grandTotal
                    )
                )
            } else {
                repository.saveUserAccount(
                    UserAccountEntity(
                        phone = phone,
                        name = name,
                        role = "CUSTOMER",
                        preferredTownshipId = township.id,
                        defaultAddressNote = _deliveryAddressNote.value.trim(),
                        totalOrdersCount = 1,
                        totalSpentMMK = grandTotal
                    )
                )
            }

            // Save user profile for seamless next-order experience
            repository.saveProfile(
                UserProfileEntity(
                    id = 1,
                    name = name,
                    phone = phone,
                    preferredTownshipId = township.id,
                    defaultAddressNote = _deliveryAddressNote.value.trim()
                )
            )

            // Trigger Push Notification
            OrderNotificationHelper.showOrderStatusNotification(
                context = context,
                orderId = orderNumber,
                status = OrderStatus.PLACED,
                lang = lang,
                itemsSummary = summary
            )

            // Reset cart and switch to tracking tab
            _cartItems.value = emptyList()
            _trackedOrderId.value = orderNumber
            _currentTab.value = ScreenTab.TRACKING

            showInAppNotification(
                if (lang == Language.BURMESE) "အော်ဒါ #$orderNumber အောင်မြင်စွာ တင်ပြီးပါပြီ!"
                else "Order #$orderNumber placed successfully!"
            )

            // Start auto progression simulation (Placed -> Preparing -> Out -> Delivered)
            startAutoOrderSimulation(orderNumber, context)

            // If payment method is KBZPay, start the 10-minute payment timer
            if (_selectedPaymentMethod.value == PaymentMethod.KPAY) {
                startKbzPaymentCountdown(context, orderNumber)
            }
        }

        return Result.success(orderNumber)
    }

    private fun startAutoOrderSimulation(orderId: String, context: Context) {
        autoSimulatorJob?.cancel()
        autoSimulatorJob = viewModelScope.launch {
            // Wait 12 seconds then advance to PREPARING
            delay(12000)
            advanceOrderStatus(orderId, OrderStatus.PREPARING, context)

            // Wait 15 seconds then advance to OUT_FOR_DELIVERY
            delay(15000)
            advanceOrderStatus(orderId, OrderStatus.OUT_FOR_DELIVERY, context)

            // Wait 20 seconds then advance to DELIVERED
            delay(20000)
            advanceOrderStatus(orderId, OrderStatus.DELIVERED, context)
        }
    }

    fun simulateNextStatusManually(orderId: String, context: Context) {
        val currentOrder = _orderHistory.value.find { it.orderId == orderId } ?: return
        val currentStatus = try {
            OrderStatus.valueOf(currentOrder.status)
        } catch (e: Exception) {
            OrderStatus.PLACED
        }

        val next = currentStatus.nextStatus() ?: return
        advanceOrderStatus(orderId, next, context)
    }

    private fun advanceOrderStatus(orderId: String, nextStatus: OrderStatus, context: Context) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, nextStatus.name)
            val lang = _currentLanguage.value
            val order = _orderHistory.value.find { it.orderId == orderId }
            val summary = order?.itemsSummary ?: ""

            OrderNotificationHelper.showOrderStatusNotification(
                context = context,
                orderId = orderId,
                status = nextStatus,
                lang = lang,
                itemsSummary = summary
            )

            showInAppNotification(
                if (lang == Language.BURMESE) "အော်ဒါ #$orderId: ${nextStatus.title(lang)}"
                else "Order #$orderId: ${nextStatus.title(lang)}"
            )
        }
    }

    fun cancelOrder(orderId: String, context: Context, reason: String = "Customer cancelled order") {
        autoSimulatorJob?.cancel()
        viewModelScope.launch {
            val order = _orderHistory.value.find { it.orderId == orderId }
            if (order != null && order.status != OrderStatus.CANCELLED.name) {
                // Restore inventory stock for cancelled items
                val snapshots = OrderItemSnapshot.listFromJsonString(order.orderItemsJson)
                snapshots.forEach { snap ->
                    val prod = adminAllProducts.value.find { it.id == snap.productId }
                    if (prod != null) {
                        repository.updateProductStock(snap.productId, prod.stockQuantity + snap.quantity)
                    }
                }
            }
            repository.cancelOrderWithReason(orderId, reason)
            val lang = _currentLanguage.value
            OrderNotificationHelper.showOrderStatusNotification(
                context = context,
                orderId = orderId,
                status = OrderStatus.CANCELLED,
                lang = lang,
                itemsSummary = "Order cancelled: $reason"
            )
            showInAppNotification(
                if (lang == Language.BURMESE) "အော်ဒါ #$orderId ကို ပယ်ဖျက်လိုက်ပါပြီ ($reason)"
                else "Order #$orderId has been cancelled: $reason"
            )
        }
    }

    fun requestOrderReturn(orderId: String, returnReason: String) {
        viewModelScope.launch {
            repository.requestOrderReturn(orderId, returnReason)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "အော်ဒါ #$orderId အတွက် ပြန်ပို့/ငွေပြန်အမ်းရန် တောင်းဆိုချက် ပေးပို့ပြီးပါပြီ"
                else "Return & refund request submitted for order #$orderId"
            )
        }
    }

    fun reorder(order: OrderEntity) {
        // Find matching food items from catalog or add a default item
        val lang = _currentLanguage.value
        _customerName.value = order.customerName
        _customerPhone.value = order.customerPhone
        val township = repository.getTownshipById(order.townshipId)
        _selectedTownship.value = township
        _deliveryAddressNote.value = order.deliveryAddressNote

        // Add first popular item as quick basket refill
        val firstItem = repository.menuItems.firstOrNull()
        if (firstItem != null) {
            _cartItems.value = listOf(CartItem(foodItem = firstItem, quantity = 1))
        }
        _currentTab.value = ScreenTab.HOME
        showInAppNotification(
            if (lang == Language.BURMESE) "ယခင်အော်ဒါ အချက်အလက်များ ဖြည့်သွင်းပြီးပါပြီ"
            else "Previous order info loaded into cart!"
        )
    }

    fun logout() {
        _customerName.value = ""
        _customerPhone.value = ""
        _deliveryAddressNote.value = ""
        _selectedTownship.value = MyanmarTownshipsData.defaultTownship
        _cartItems.value = emptyList()
        _currentUserRole.value = "GUEST"
        _currentTab.value = ScreenTab.HOME
        adminNavHistory.clear()
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "အကောင့်မှ ထွက်ပြီးပါပြီ။ ဧည့်သည်အဖြစ် ဆက်လက်ကြည့်ရှုနိုင်ပါသည်။"
            else "Logged out. You can now browse as a guest."
        )
        _showAuthDialog.value = false

        viewModelScope.launch {
            repository.clearProfile()
        }
    }

    fun loginAsAdmin(adminName: String = "Admin Ko Min Min", adminPhone: String = "09999999999") {
        _customerName.value = adminName
        _customerPhone.value = adminPhone
        _currentUserRole.value = "ADMIN"
        _showAuthDialog.value = false
        _currentTab.value = ScreenTab.ADMIN
        _selectedAdminSection.value = AdminSection.OVERVIEW
        adminNavHistory.clear()
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "မင်္ဂလာပါ စီမံခန့်ခွဲသူ မင်းမင်း၊ Admin စနစ်သို့ အောင်မြင်စွာ ဝင်ရောက်ပြီးပါပြီ"
            else "Welcome Admin! Logged into Admin Management Console"
        )
    }

    // Admin CRUD for Products
    fun saveOrUpdateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "${product.nameMy} ကုန်ပစ္စည်းကို သိမ်းဆည်းပြီးပါပြီ"
                else "Saved product: ${product.nameEn}"
            )
        }
    }

    fun deleteProduct(productId: String, productName: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            // Clean up any occurrences from active cart to avoid orphaned items
            _cartItems.update { list -> list.filterNot { it.foodItem.id == productId } }
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "$productName အား စာရင်းမှ ဖျက်ပြီးပါပြီ"
                else "Product deleted: $productName"
            )
        }
    }

    fun quickUpdateProductPrice(product: FoodItem, newPriceMMK: Int) {
        viewModelScope.launch {
            val updatedEntity = ProductEntity.fromFoodItem(product.copy(priceMMK = newPriceMMK))
            repository.insertProduct(updatedEntity)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "${product.nameMy} ဈေးနှုန်းအား $newPriceMMK ကျပ်သို့ ပြင်ဆင်ပြီးပါပြီ"
                else "Updated ${product.nameEn} price to $newPriceMMK MMK"
            )
        }
    }

    // Admin User Account Management
    fun createUserAccount(
        name: String,
        phone: String,
        role: String = "CUSTOMER",
        townshipId: String = "ygn_kamayut",
        addressNote: String = "",
        password: String = "1234"
    ) {
        viewModelScope.launch {
            val newUser = UserAccountEntity(
                phone = phone.trim(),
                name = name.trim(),
                role = role,
                preferredTownshipId = townshipId,
                defaultAddressNote = addressNote.trim(),
                password = password
            )
            repository.saveUserAccount(newUser)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "$name အတွက် အကောင့်သစ် ($role) ဖွင့်ပေးပြီးပါပြီ"
                else "Created user account ($role): $name"
            )
        }
    }

    fun deleteUserAccount(phone: String, name: String) {
        viewModelScope.launch {
            repository.deleteUserAccount(phone)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "$name ($phone) အကောင့်အား ဖျက်ပြီးပါပြီ"
                else "User account deleted: $name"
            )
        }
    }

    fun loginAsUser(user: UserAccountEntity) {
        _customerName.value = user.name
        _customerPhone.value = user.phone
        _currentUserRole.value = user.role
        val township = repository.getTownshipById(user.preferredTownshipId)
        _selectedTownship.value = township
        _deliveryAddressNote.value = user.defaultAddressNote
        if (user.role == "ADMIN") {
            _currentTab.value = ScreenTab.ADMIN
            _selectedAdminSection.value = AdminSection.OVERVIEW
            adminNavHistory.clear()
        } else {
            _currentTab.value = ScreenTab.HOME
        }
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "${user.name} အကောင့်ဖြင့် ဝင်ရောက်အသုံးပြုနေပါပြီ"
            else "Switched and logged in as ${user.name}"
        )
    }

    // Admin Order Management
    fun adminUpdateOrderStatus(orderId: String, status: OrderStatus, context: Context) {
        viewModelScope.launch {
            if (status == OrderStatus.CANCELLED) {
                val order = repository.allOrders.firstOrNull()?.find { it.orderId == orderId }
                    ?: _orderHistory.value.find { it.orderId == orderId }
                if (order != null && order.status != OrderStatus.CANCELLED.name) {
                    val snapshots = OrderItemSnapshot.listFromJsonString(order.orderItemsJson)
                    snapshots.forEach { snap ->
                        val prod = adminAllProducts.value.find { it.id == snap.productId }
                        if (prod != null) {
                            repository.updateProductStock(snap.productId, prod.stockQuantity + snap.quantity)
                        }
                    }
                }
            }
            repository.updateOrderStatus(orderId, status.name)
            OrderNotificationHelper.showOrderStatusNotification(
                context = context,
                orderId = orderId,
                status = status,
                lang = _currentLanguage.value,
                itemsSummary = "Updated by Admin"
            )
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "အော်ဒါ #$orderId အား '${status.title(Language.BURMESE)}' သို့ ပြောင်းလဲပြီးပါပြီ"
                else "Order #$orderId updated to '${status.title(Language.ENGLISH)}'"
            )
        }
    }

    fun adminDeleteOrder(orderId: String) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "အော်ဒါ #$orderId ကို မှတ်တမ်းမှ ဖျက်ပြီးပါပြီ"
                else "Order #$orderId removed"
            )
        }
    }

    fun adminUpdateTrackingInfo(orderId: String, trackingNumber: String, partnerName: String) {
        viewModelScope.launch {
            repository.updateTrackingInfo(orderId, trackingNumber, partnerName)
            showInAppNotification("Logistics info updated for #$orderId")
        }
    }

    fun adminUpdatePaymentStatus(orderId: String, paymentStatus: String) {
        viewModelScope.launch {
            repository.updatePaymentStatus(orderId, paymentStatus)
            showInAppNotification("Payment status for #$orderId set to $paymentStatus")
        }
    }

    fun adminUpdateReturnStatus(orderId: String, returnStatus: String, paymentStatus: String) {
        viewModelScope.launch {
            val existingOrder = _orderHistory.value.find { it.orderId == orderId }
                ?: repository.allOrders.firstOrNull()?.find { it.orderId == orderId }
            val wasAlreadyRestored = existingOrder?.returnStatus == "APPROVED" || existingOrder?.returnStatus == "COMPLETED"

            repository.updateReturnStatus(orderId, returnStatus, paymentStatus)

            if ((returnStatus == "APPROVED" || returnStatus == "COMPLETED") && !wasAlreadyRestored) {
                if (existingOrder != null && existingOrder.orderItemsJson.isNotBlank()) {
                    val snapshots = OrderItemSnapshot.listFromJsonString(existingOrder.orderItemsJson)
                    snapshots.forEach { snap ->
                        val prod = adminAllProducts.value.find { it.id == snap.productId }
                        if (prod != null) {
                            repository.updateProductStock(snap.productId, prod.stockQuantity + snap.quantity)
                        }
                    }
                }
            }
            showInAppNotification("Return status for #$orderId set to $returnStatus (Payment: $paymentStatus)")
        }
    }

    fun adminUpdateCustomerDetails(phone: String, name: String, email: String, townshipId: String, defaultAddressNote: String, adminNotes: String) {
        viewModelScope.launch {
            repository.updateUserAccountDetails(phone, name, email, townshipId, defaultAddressNote, adminNotes)
            showInAppNotification("Customer profile updated for $name ($phone)")
        }
    }

    // Reviews & Ratings
    fun submitReview(productId: String, productName: String, orderId: String = "", rating: Int, comment: String) {
        viewModelScope.launch {
            val review = ReviewEntity(
                orderId = orderId,
                productId = productId,
                productName = productName,
                customerName = _customerName.value.ifBlank { "Customer" },
                customerPhone = _customerPhone.value,
                rating = rating,
                comment = comment,
                timestamp = System.currentTimeMillis()
            )
            repository.saveReview(review)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "သင့်သုံးသပ်ချက်ကို အောင်မြင်စွာ ပေးပို့ပြီးပါပြီ။ ကျေးဇူးတင်ပါသည်!"
                else "Thank you for your rating & review!"
            )
        }
    }

    fun adminReplyToReview(reviewId: String, reply: String) {
        viewModelScope.launch {
            repository.replyToReview(reviewId, reply)
            showInAppNotification("Admin reply posted successfully!")
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
            showInAppNotification("Review removed.")
        }
    }

    // Customer Inquiries & Contact Messages
    fun submitCustomerMessage(name: String, phone: String, email: String, orderId: String, subject: String, category: String, message: String) {
        viewModelScope.launch {
            val msg = CustomerMessageEntity(
                customerName = name.ifBlank { _customerName.value }.ifBlank { "Customer" },
                customerPhone = phone.ifBlank { _customerPhone.value },
                customerEmail = email,
                orderId = orderId,
                subject = subject,
                category = category,
                message = message,
                timestamp = System.currentTimeMillis(),
                status = "NEW"
            )
            repository.saveCustomerMessage(msg)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "မေးမြန်းချက် ပေးပို့ပြီးပါပြီ။ မကြာမီ ဆက်သွယ်ပါမည်။"
                else "Message submitted successfully! Our team will respond shortly."
            )
        }
    }

    fun adminReplyToCustomerMessage(messageId: String, reply: String, status: String) {
        viewModelScope.launch {
            repository.replyToCustomerMessage(messageId, reply, status)
            showInAppNotification("Reply recorded. Message status: $status")
        }
    }

    fun deleteCustomerMessage(messageId: String) {
        viewModelScope.launch {
            repository.deleteCustomerMessage(messageId)
            showInAppNotification("Message removed.")
        }
    }

    fun openAuthDialog() {
        _showAuthDialog.value = true
    }

    fun closeAuthDialog() {
        _showAuthDialog.value = false
    }

    fun continueAsGuest() {
        _showAuthDialog.value = false
        _currentTab.value = ScreenTab.HOME
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "ဧည့်သည်တော်အဖြစ် လွတ်လပ်စွာ ကြည့်ရှုနိုင်ပါသည်"
            else "Browsing menu as guest user"
        )
    }

    fun loginOrSignUp(name: String, phone: String) {
        _customerName.value = name.trim()
        _customerPhone.value = phone.trim()
        _currentUserRole.value = "CUSTOMER"
        _showAuthDialog.value = false
        viewModelScope.launch {
            repository.saveProfile(
                UserProfileEntity(
                    id = 1,
                    name = name.trim(),
                    phone = phone.trim(),
                    preferredTownshipId = _selectedTownship.value.id,
                    defaultAddressNote = _deliveryAddressNote.value.trim()
                )
            )
            repository.saveUserAccount(
                UserAccountEntity(
                    phone = phone.trim(),
                    name = name.trim(),
                    role = "CUSTOMER",
                    preferredTownshipId = _selectedTownship.value.id,
                    defaultAddressNote = _deliveryAddressNote.value.trim()
                )
            )
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "မင်္ဂလာပါ ${name.trim()}၊ အကောင့်အောင်မြင်စွာ ဝင်ရောက်ပြီးပါပြီ"
                else "Welcome ${name.trim()}! Successfully logged in"
            )
        }
    }

    fun switchAccount(newName: String, newPhone: String, newTownship: MyanmarTownship? = null, newAddressNote: String = "") {
        _customerName.value = newName.trim()
        _customerPhone.value = newPhone.trim()
        if (newTownship != null) {
            _selectedTownship.value = newTownship
        }
        _deliveryAddressNote.value = newAddressNote.trim()

        viewModelScope.launch {
            repository.saveProfile(
                UserProfileEntity(
                    id = 1,
                    name = newName.trim(),
                    phone = newPhone.trim(),
                    preferredTownshipId = _selectedTownship.value.id,
                    defaultAddressNote = newAddressNote.trim()
                )
            )
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "အကောင့်သို့ ပြောင်းလဲအသုံးပြုနေပါပြီ"
                else "Switched account profile!"
            )
        }
    }

    fun showInAppNotification(message: String) {
        _inAppNotification.value = message
        viewModelScope.launch {
            delay(4000)
            if (_inAppNotification.value == message) {
                _inAppNotification.value = null
            }
        }
    }

    fun dismissInAppNotification() {
        _inAppNotification.value = null
    }

    // Admin Selected Section
    private val _selectedAdminSection = MutableStateFlow(AdminSection.OVERVIEW)
    val selectedAdminSection: StateFlow<AdminSection> = _selectedAdminSection.asStateFlow()

    fun setAdminSection(section: AdminSection) {
        if (_currentUserRole.value == "ADMIN") {
            val currentLoc = AdminNavLocation(_currentTab.value, _selectedAdminSection.value)
            val targetLoc = AdminNavLocation(ScreenTab.ADMIN, section)
            if (currentLoc != targetLoc) {
                adminNavHistory.add(currentLoc)
            }
        }
        _currentTab.value = ScreenTab.ADMIN
        _selectedAdminSection.value = section
    }

    // Inventory Availability Quick Toggle
    fun toggleProductAvailability(product: FoodItem) {
        viewModelScope.launch {
            val updated = ProductEntity.fromFoodItem(product.copy(isAvailable = !product.isAvailable))
            repository.insertProduct(updated)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "${product.nameMy} အား ${if (!product.isAvailable) "ပစ္စည်းရှိ (In Stock)" else "ပစ္စည်းပြတ် (Out of Stock)"} သို့ ပြောင်းလဲပြီးပါပြီ"
                else "${product.nameEn} marked as ${if (!product.isAvailable) "In Stock" else "Out of Stock"}"
            )
        }
    }

    // Promotions Management
    private val _promotions = MutableStateFlow<List<PromotionItem>>(
        listOf(
            PromotionItem(
                id = "p1",
                titleEn = "Free Delivery Yangon",
                titleMy = "ရန်ကုန်မြို့တွင်း အခမဲ့ ပို့ဆောင်မှု",
                code = "FREESHIP",
                discountPercent = 100,
                isActive = true,
                descriptionEn = "Free delivery on all orders over 30,000 MMK",
                descriptionMy = "ကျပ် ၃၀,၀၀၀ အထက် အော်ဒါများ ပို့ဆောင်ခ အခမဲ့"
            ),
            PromotionItem(
                id = "p2",
                titleEn = "Rakhine Mont Di Feast (15% OFF)",
                titleMy = "ရခိုင်မုန့်တီ အထူးလျှော့ဈေး (၁၅% လျှော့)",
                code = "MONTDI15",
                discountPercent = 15,
                isActive = true,
                descriptionEn = "15% off traditional Rakhine seafood noodle soups",
                descriptionMy = "ရခိုင်ရိုးရာ မုန့်တီဟင်းရည်သောက်များ အားလုံး ၁၅% လျှော့ဈေး"
            ),
            PromotionItem(
                id = "p3",
                titleEn = "Seafood Special Combo",
                titleMy = "ပင်လယ်စာ အထူး ပရိုမိုးရှင်း",
                code = "SEAFOOD10",
                discountPercent = 10,
                isActive = false,
                descriptionEn = "10% off Grilled Giant Prawns & Crab Masala",
                descriptionMy = "ပုစွန်ထုပ်ကြီးကင်နှင့် ဂဏန်းမဆလာ ၁၀% လျှော့ဈေး"
            )
        )
    )
    val promotions: StateFlow<List<PromotionItem>> = _promotions.asStateFlow()

    fun togglePromotion(promoId: String) {
        _promotions.value = _promotions.value.map {
            if (it.id == promoId) it.copy(isActive = !it.isActive) else it
        }
        val p = _promotions.value.find { it.id == promoId }
        val lang = _currentLanguage.value
        if (p != null) {
            showInAppNotification(
                if (lang == Language.BURMESE) "${p.titleMy} ပရိုမိုးရှင်းကို ${if (p.isActive) "ဖွင့်" else "ပိတ်"}ပြီးပါပြီ"
                else "Promotion '${p.titleEn}' ${if (p.isActive) "activated" else "deactivated"}"
            )
        }
    }

    fun addPromotion(titleEn: String, titleMy: String, code: String, discountPercent: Int, descEn: String, descMy: String) {
        val newPromo = PromotionItem(
            id = "p_${System.currentTimeMillis()}",
            titleEn = titleEn.trim().ifBlank { "Promotion" },
            titleMy = titleMy.trim().ifBlank { titleEn.trim().ifBlank { "ပရိုမိုးရှင်း" } },
            code = code.trim().uppercase().ifBlank { "PROMO" },
            discountPercent = discountPercent.coerceIn(1, 100),
            isActive = true,
            descriptionEn = descEn.trim(),
            descriptionMy = descMy.trim()
        )
        _promotions.value = listOf(newPromo) + _promotions.value
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "ပရိုမိုးရှင်းအသစ် (${newPromo.code}) ထည့်သွင်းပြီးပါပြီ"
            else "New promo code (${newPromo.code}) added!"
        )
    }

    fun deletePromotion(promoId: String) {
        _promotions.value = _promotions.value.filter { it.id != promoId }
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "ပရိုမိုးရှင်းကို ဖျက်ပစ်လိုက်ပါပြီ"
            else "Promotion removed"
        )
    }

    // Payments Configuration
    private val _paymentConfig = MutableStateFlow(PaymentConfig())
    val paymentConfig: StateFlow<PaymentConfig> = _paymentConfig.asStateFlow()

    fun updatePaymentConfig(newConfig: PaymentConfig) {
        _paymentConfig.value = newConfig
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "ငွေပေးချေမှု စနစ်များ အောင်မြင်စွာ ပြင်ဆင်ပြီးပါပြီ"
            else "Payment settings updated successfully"
        )
    }

    fun addQrPaymentOption(option: QrPaymentOption) {
        val current = _paymentConfig.value
        val updated = current.copy(
            qrPaymentOptions = current.qrPaymentOptions + option
        )
        updatePaymentConfig(updated)
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "QR ပေးချေမှု အသစ် (${option.name}) ထည့်သွင်းပြီးပါပြီ"
            else "Added new QR payment option: ${option.name}"
        )
    }

    fun updateQrPaymentOption(option: QrPaymentOption) {
        val current = _paymentConfig.value
        val updated = current.copy(
            qrPaymentOptions = current.qrPaymentOptions.map { if (it.id == option.id) option else it }
        )
        updatePaymentConfig(updated)
    }

    fun deleteQrPaymentOption(optionId: String) {
        val current = _paymentConfig.value
        val removed = current.qrPaymentOptions.find { it.id == optionId }
        val updated = current.copy(
            qrPaymentOptions = current.qrPaymentOptions.filterNot { it.id == optionId }
        )
        updatePaymentConfig(updated)
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "QR ပေးချေမှု နည်းလမ်း ဖျက်ပြီးပါပြီ"
            else "Removed QR payment option"
        )
    }

    fun toggleQrPaymentOption(optionId: String) {
        val current = _paymentConfig.value
        val updated = current.copy(
            qrPaymentOptions = current.qrPaymentOptions.map {
                if (it.id == optionId) it.copy(isEnabled = !it.isEnabled) else it
            }
        )
        updatePaymentConfig(updated)
    }

    // Store Settings Configuration
    private val _storeSettings = MutableStateFlow(StoreSettingsConfig())
    val storeSettings: StateFlow<StoreSettingsConfig> = _storeSettings.asStateFlow()

    fun updateStoreSettings(newSettings: StoreSettingsConfig) {
        _storeSettings.value = newSettings
        _logisticsConfig.value = _logisticsConfig.value.copy(
            standardDeliveryFeeMMK = newSettings.standardDeliveryFeeMMK,
            minOrderAmountMMK = newSettings.minOrderAmountMMK
        )
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "ဆိုင်ချိန်ညှိချက်များ အောင်မြင်စွာ ပြင်ဆင်ပြီးပါပြီ"
            else "Store settings saved successfully"
        )
    }

    // Logistics Management Methods
    fun updateLogisticsConfig(config: LogisticsConfig) {
        _logisticsConfig.value = config
        _storeSettings.value = _storeSettings.value.copy(
            standardDeliveryFeeMMK = config.standardDeliveryFeeMMK,
            minOrderAmountMMK = config.minOrderAmountMMK
        )
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "ပို့ဆောင်ရေး စည်းမျဉ်းများ အောင်မြင်စွာ ပြင်ဆင်ပြီးပါပြီ"
            else "Logistics & delivery settings updated"
        )
    }

    fun updateTownship(updated: MyanmarTownship) {
        _townships.value = _townships.value.map {
            if (it.id == updated.id) updated else it
        }
        if (_selectedTownship.value.id == updated.id) {
            _selectedTownship.value = updated
        }
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "${updated.name(lang)} ပို့ဆောင်ခ အောင်မြင်စွာ ပြင်ဆင်ပြီးပါပြီ"
            else "Updated delivery info for ${updated.nameEn}"
        )
    }

    fun addCustomTownship(
        nameEn: String,
        nameMy: String,
        regionEn: String,
        regionMy: String,
        feeMMK: Int,
        estMinutes: Int
    ) {
        val newId = "custom_${System.currentTimeMillis()}"
        val newTownship = MyanmarTownship(
            id = newId,
            nameEn = nameEn.trim(),
            nameMy = if (nameMy.isNotBlank()) nameMy.trim() else nameEn.trim(),
            regionEn = if (regionEn.isNotBlank()) regionEn.trim() else "Yangon",
            regionMy = if (regionMy.isNotBlank()) regionMy.trim() else "ရန်ကုန်",
            deliveryFeeMMK = feeMMK,
            estimatedMinutes = estMinutes,
            isEnabled = true
        )
        _townships.value = _townships.value + newTownship
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "${newTownship.name(lang)} အား ပို့ဆောင်မှုဇုန်အဖြစ် ထည့်သွင်းပြီးပါပြီ"
            else "Added ${newTownship.nameEn} to delivery zones"
        )
    }

    fun toggleTownshipEnabled(townshipId: String) {
        _townships.value = _townships.value.map {
            if (it.id == townshipId) it.copy(isEnabled = !it.isEnabled) else it
        }
        val current = _selectedTownship.value
        if (current.id == townshipId) {
            val updated = _townships.value.find { it.id == townshipId }
            if (updated != null) {
                _selectedTownship.value = updated
            }
        }
    }

    fun resetTownshipsToDefault() {
        _townships.value = MyanmarTownshipsData.allTownships
        val current = _selectedTownship.value
        val defaultMatch = MyanmarTownshipsData.allTownships.find { it.id == current.id } ?: MyanmarTownshipsData.defaultTownship
        _selectedTownship.value = defaultMatch
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "မြို့နယ် ပို့ဆောင်ခများကို မူလတန်ဖိုးအတိုင်း ပြန်လည်သတ်မှတ်ပြီးပါပြီ"
            else "Reset delivery fees to defaults"
        )
    }

    fun addDeliveryPartner(partner: DeliveryPartner) {
        _logisticsConfig.value = _logisticsConfig.value.copy(
            deliveryPartners = _logisticsConfig.value.deliveryPartners + partner
        )
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "ပို့ဆောင်ရေး မိတ်ဖက် (${partner.name}) အသစ်ထည့်သွင်းပြီးပါပြီ"
            else "Added delivery partner: ${partner.name}"
        )
    }

    fun updateDeliveryPartner(updated: DeliveryPartner) {
        _logisticsConfig.value = _logisticsConfig.value.copy(
            deliveryPartners = _logisticsConfig.value.deliveryPartners.map {
                if (it.id == updated.id) updated else it
            }
        )
    }

    fun deleteDeliveryPartner(partnerId: String) {
        _logisticsConfig.value = _logisticsConfig.value.copy(
            deliveryPartners = _logisticsConfig.value.deliveryPartners.filter { it.id != partnerId }
        )
        val lang = _currentLanguage.value
        showInAppNotification(
            if (lang == Language.BURMESE) "ပို့ဆောင်ရေး မိတ်ဖက် ဖျက်ပြီးပါပြီ"
            else "Removed delivery partner"
        )
    }

    fun toggleDeliveryPartner(partnerId: String) {
        _logisticsConfig.value = _logisticsConfig.value.copy(
            deliveryPartners = _logisticsConfig.value.deliveryPartners.map {
                if (it.id == partnerId) it.copy(isEnabled = !it.isEnabled) else it
            }
        )
    }

    // ==========================================
    // ADMIN BUSINESS INFORMATION & SETTINGS CONTROLS
    // ==========================================

    fun adminUpdateBusinessSettings(settings: BusinessSettingsEntity) {
        viewModelScope.launch {
            repository.saveBusinessSettings(settings)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "လုပ်ငန်းဆိုင်ရာ အချက်အလက်များနှင့် ပို့ဆောင်ခ စည်းမျဉ်းများ သိမ်းဆည်းပြီးပါပြီ"
                else "Business settings & delivery rules saved to database"
            )
        }
    }

    // ==========================================
    // ADMIN PRODUCT & INVENTORY & ARCHIVE CONTROLS
    // ==========================================

    fun adminSaveProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "ကုန်ပစ္စည်း (${product.nameMy}) အချက်အလက်များကို အောင်မြင်စွာ သိမ်းဆည်းပြီးပါပြီ"
                else "Product saved successfully"
            )
        }
    }

    fun adminUpdateProductPrice(productId: String, priceMMK: Int) {
        viewModelScope.launch {
            repository.updateProductPrice(productId, priceMMK)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "ဈေးနှုန်းအသစ် ${priceMMK} MMK ပြင်ဆင်ပြီးပါပြီ"
                else "Product price updated to $priceMMK MMK"
            )
        }
    }

    fun adminUpdateProductStock(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateProductStock(productId, quantity)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "လက်ကျန်အရေအတွက် $quantity ပြင်ဆင်ပြီးပါပြီ"
                else "Inventory stock updated to $quantity units"
            )
        }
    }

    fun adminArchiveProduct(productId: String, isArchived: Boolean) {
        viewModelScope.launch {
            repository.setProductArchived(productId, isArchived)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (isArchived) {
                    if (lang == Language.BURMESE) "ကုန်ပစ္စည်းကို သိမ်းဆည်းထားပြီးဖြစ်သည် (ဝယ်ယူသူများ မမြင်ရပါ)"
                    else "Product archived (hidden from customer store)"
                } else {
                    if (lang == Language.BURMESE) "ကုန်ပစ္စည်းကို ပြန်လည်ပြသပေးထားပါပြီ"
                    else "Product unarchived and visible to customers"
                }
            )
        }
    }

    fun adminDeleteProduct(productId: String, hardDelete: Boolean = false) {
        viewModelScope.launch {
            if (hardDelete) {
                repository.deleteProduct(productId)
            } else {
                // Soft delete / Archive to protect historical order records
                repository.setProductArchived(productId, true)
            }
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "ကုန်ပစ္စည်း အောင်မြင်စွာ ဖျက်သိမ်း/သိမ်းဆည်းပြီးပါပြီ"
                else "Product archived/removed safely (order history preserved)"
            )
        }
    }

    // ==========================================
    // ADMIN CATEGORY CONTROLS (CRUD)
    // ==========================================

    fun adminSaveCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.saveCategory(category)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "အမျိုးအစား (${category.nameMy}) အောင်မြင်စွာ ထည့်သွင်း/ပြင်ဆင်ပြီးပါပြီ"
                else "Category saved: ${category.nameEn}"
            )
        }
    }

    fun adminArchiveCategory(categoryId: String, isArchived: Boolean) {
        viewModelScope.launch {
            repository.setCategoryArchived(categoryId, isArchived)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (isArchived) {
                    if (lang == Language.BURMESE) "အမျိုးအစား သိမ်းဆည်းထားပြီးပါပြီ"
                    else "Category archived"
                } else {
                    if (lang == Language.BURMESE) "အမျိုးအစား ပြန်လည်ဖွင့်လှစ်ပြီးပါပြီ"
                    else "Category restored"
                }
            )
        }
    }

    fun adminDeleteCategory(categoryId: String) {
        viewModelScope.launch {
            repository.setCategoryArchived(categoryId, true)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "အမျိုးအစား ဖျက်သိမ်းပြီးပါပြီ"
                else "Category archived/deleted"
            )
        }
    }

    // ==========================================
    // ADMIN BRAND CONTROLS (CRUD)
    // ==========================================

    fun adminSaveBrand(brand: BrandEntity) {
        viewModelScope.launch {
            repository.saveBrand(brand)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "တံဆိပ် (${brand.nameMy}) အောင်မြင်စွာ သိမ်းဆည်းပြီးပါပြီ"
                else "Brand saved: ${brand.nameEn}"
            )
        }
    }

    fun adminArchiveBrand(brandId: String, isArchived: Boolean) {
        viewModelScope.launch {
            repository.setBrandArchived(brandId, isArchived)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (isArchived) {
                    if (lang == Language.BURMESE) "တံဆိပ် သိမ်းဆည်းထားပြီးပါပြီ"
                    else "Brand archived"
                } else {
                    if (lang == Language.BURMESE) "တံဆိပ် ပြန်လည်ပြသပေးထားပါပြီ"
                    else "Brand restored"
                }
            )
        }
    }

    fun adminDeleteBrand(brandId: String) {
        viewModelScope.launch {
            repository.setBrandArchived(brandId, true)
            val lang = _currentLanguage.value
            showInAppNotification(
                if (lang == Language.BURMESE) "တံဆိပ် ဖျက်သိမ်းပြီးပါပြီ"
                else "Brand archived/deleted"
            )
        }
    }

    private val _orderHistory = repository.allOrders.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
}

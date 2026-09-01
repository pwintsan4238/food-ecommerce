package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FoodRepository
import com.example.data.OrderEntity
import com.example.data.UserProfileEntity
import com.example.model.CartItem
import com.example.model.FoodAddOn
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.Language
import com.example.model.MyanmarTownship
import com.example.model.MyanmarTownshipsData
import com.example.model.OrderStatus
import com.example.model.PaymentMethod
import com.example.notification.OrderNotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class FoodOrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FoodRepository
    private var autoSimulatorJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FoodRepository(database.orderDao())
    }

    // UI Navigation Tab
    enum class ScreenTab { MENU, TRACKING, HISTORY, PROFILE }
    private val _currentTab = MutableStateFlow(ScreenTab.MENU)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    fun navigateTo(tab: ScreenTab) {
        _currentTab.value = tab
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

    // Search and Category
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FoodCategory.ALL)
    val selectedCategory: StateFlow<FoodCategory> = _selectedCategory.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: FoodCategory) {
        _selectedCategory.value = category
    }

    // Filtered Food Menu
    val filteredMenuItems: StateFlow<List<FoodItem>> = combine(
        _searchQuery,
        _selectedCategory,
        _currentLanguage
    ) { query, category, lang ->
        repository.menuItems.filter { item ->
            val matchesCategory = (category == FoodCategory.ALL || item.category == category)
            val matchesSearch = query.isBlank() ||
                    item.nameEn.contains(query, ignoreCase = true) ||
                    item.nameMy.contains(query, ignoreCase = true) ||
                    item.descriptionEn.contains(query, ignoreCase = true) ||
                    item.descriptionMy.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
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

    private val _selectedTownship = MutableStateFlow<MyanmarTownship>(MyanmarTownshipsData.defaultTownship)
    val selectedTownship: StateFlow<MyanmarTownship> = _selectedTownship.asStateFlow()

    private val _deliveryAddressNote = MutableStateFlow("")
    val deliveryAddressNote: StateFlow<String> = _deliveryAddressNote.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.COD)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    // Calculated Grand Total
    val deliveryFeeMMK: StateFlow<Int> = _selectedTownship.combine(_selectedTownship) { township, _ ->
        township.deliveryFeeMMK
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyanmarTownshipsData.defaultTownship.deliveryFeeMMK)

    val grandTotalMMK: StateFlow<Int> = combine(cartSubtotalMMK, deliveryFeeMMK) { subtotal, fee ->
        if (subtotal > 0) subtotal + fee else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Orders Flow from Room Database
    val orderHistory: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
        notes: String = ""
    ) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst {
            it.foodItem.id == foodItem.id &&
            it.selectedSpiceLevel == spiceLevel &&
            it.selectedAddOns == addOns &&
            it.specialNotes == notes
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
                    specialNotes = notes
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
        val fee = township.deliveryFeeMMK
        val grandTotal = subtotal + fee

        // Format items summary e.g. "Mohinga x 2, Shan Noodles x 1"
        val lang = _currentLanguage.value
        val summary = items.joinToString(", ") { item ->
            val itemName = if (lang == Language.BURMESE) item.foodItem.nameMy else item.foodItem.nameEn
            "$itemName x${item.quantity}"
        }

        val orderNumber = "MM-${Random.nextInt(1000, 9999)}"
        val orderEntity = OrderEntity(
            orderId = orderNumber,
            timestamp = System.currentTimeMillis(),
            customerName = name,
            customerPhone = phone,
            townshipId = township.id,
            townshipNameEn = township.nameEn,
            townshipNameMy = township.nameMy,
            deliveryAddressNote = _deliveryAddressNote.value.trim(),
            itemsSummary = summary,
            totalItemCount = items.sumOf { it.quantity },
            foodSubtotalMMK = subtotal,
            deliveryFeeMMK = fee,
            grandTotalMMK = grandTotal,
            paymentMethod = _selectedPaymentMethod.value.id,
            status = OrderStatus.PLACED.name,
            statusUpdatedAt = System.currentTimeMillis(),
            estimatedMinutes = township.estimatedMinutes
        )

        viewModelScope.launch {
            // Save order to Room
            repository.saveOrder(orderEntity)

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

    fun cancelOrder(orderId: String, context: Context) {
        autoSimulatorJob?.cancel()
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.CANCELLED.name)
            val lang = _currentLanguage.value
            OrderNotificationHelper.showOrderStatusNotification(
                context = context,
                orderId = orderId,
                status = OrderStatus.CANCELLED,
                lang = lang,
                itemsSummary = "Order cancelled"
            )
            showInAppNotification(
                if (lang == Language.BURMESE) "အော်ဒါ #$orderId ကို ပယ်ဖျက်လိုက်ပါပြီ"
                else "Order #$orderId has been cancelled"
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
        _currentTab.value = ScreenTab.MENU
        showInAppNotification(
            if (lang == Language.BURMESE) "ယခင်အော်ဒါ အချက်အလက်များ ဖြည့်သွင်းပြီးပါပြီ"
            else "Previous order info loaded into cart!"
        )
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

    private val _orderHistory = repository.allOrders.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
}

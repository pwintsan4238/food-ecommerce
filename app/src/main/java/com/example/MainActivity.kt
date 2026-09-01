package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.UserProfileEntity
import com.example.model.Language
import com.example.model.OrderStatus
import com.example.model.Strings
import com.example.notification.OrderNotificationHelper
import com.example.ui.components.CartBottomSheet
import com.example.ui.components.InAppNotificationBanner
import com.example.ui.components.KbzPayDialog
import com.example.ui.components.MyanmarTownshipPickerBottomSheet
import com.example.ui.screens.CustomerInfoScreen
import android.content.Intent
import androidx.activity.viewModels
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.FoodOrderViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FoodOrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create Android Notification Channel
        OrderNotificationHelper.createNotificationChannel(this)

        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                MyanmarFoodApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val orderId = intent?.getStringExtra("EXTRA_ORDER_ID")
        val openKbzPay = intent?.getBooleanExtra("EXTRA_OPEN_KBZ_PAY", false) ?: false
        if (!orderId.isNullOrBlank()) {
            viewModel.setTrackedOrder(orderId)
            viewModel.navigateTo(FoodOrderViewModel.ScreenTab.TRACKING)
            if (openKbzPay) {
                viewModel.openKbzPayDialog(orderId, this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyanmarFoodApp(viewModel: FoodOrderViewModel = viewModel()) {
    val context = LocalContext.current

    // Request Notification Permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // ViewModel State observation
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val filteredMenuItems by viewModel.filteredMenuItems.collectAsStateWithLifecycle()

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartItemCount by viewModel.cartTotalCount.collectAsStateWithLifecycle()
    val cartSubtotalMMK by viewModel.cartSubtotalMMK.collectAsStateWithLifecycle()
    val deliveryFeeMMK by viewModel.deliveryFeeMMK.collectAsStateWithLifecycle()
    val grandTotalMMK by viewModel.grandTotalMMK.collectAsStateWithLifecycle()

    val customerName by viewModel.customerName.collectAsStateWithLifecycle()
    val customerPhone by viewModel.customerPhone.collectAsStateWithLifecycle()
    val selectedTownship by viewModel.selectedTownship.collectAsStateWithLifecycle()
    val deliveryAddressNote by viewModel.deliveryAddressNote.collectAsStateWithLifecycle()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsStateWithLifecycle()

    val orderHistory by viewModel.orderHistory.collectAsStateWithLifecycle()
    val currentTrackedOrder by viewModel.currentTrackedOrder.collectAsStateWithLifecycle()
    val inAppNotification by viewModel.inAppNotification.collectAsStateWithLifecycle()

    // KBZ Pay State
    val showKbzPayDialog by viewModel.showKbzPayDialog.collectAsStateWithLifecycle()
    val kbzPayTimeRemainingSeconds by viewModel.kbzPayTimeRemainingSeconds.collectAsStateWithLifecycle()
    val isKbzPayPaid by viewModel.isKbzPayPaid.collectAsStateWithLifecycle()
    val kbzPayOrderId by viewModel.kbzPayOrderId.collectAsStateWithLifecycle()

    // Modals state
    var showCartSheet by remember { mutableStateOf(false) }
    var showTownshipPicker by remember { mutableStateOf(false) }

    val activeOrderCount = remember(orderHistory) {
        orderHistory.count { it.status != OrderStatus.DELIVERED.name && it.status != OrderStatus.CANCELLED.name }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // 1. Menu Tab
                NavigationBarItem(
                    selected = currentTab == FoodOrderViewModel.ScreenTab.MENU,
                    onClick = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.MENU) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == FoodOrderViewModel.ScreenTab.MENU) Icons.Filled.RestaurantMenu else Icons.Outlined.RestaurantMenu,
                            contentDescription = Strings.homeTab(currentLanguage)
                        )
                    },
                    label = {
                        Text(
                            text = Strings.homeTab(currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == FoodOrderViewModel.ScreenTab.MENU) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("nav_tab_menu")
                )

                // 2. Tracking Tab
                NavigationBarItem(
                    selected = currentTab == FoodOrderViewModel.ScreenTab.TRACKING,
                    onClick = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.TRACKING) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (activeOrderCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text("$activeOrderCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentTab == FoodOrderViewModel.ScreenTab.TRACKING) Icons.Filled.LocationOn else Icons.Outlined.LocationOn,
                                contentDescription = Strings.trackingTab(currentLanguage)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = Strings.trackingTab(currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == FoodOrderViewModel.ScreenTab.TRACKING) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("nav_tab_tracking")
                )

                // 3. History Tab
                NavigationBarItem(
                    selected = currentTab == FoodOrderViewModel.ScreenTab.HISTORY,
                    onClick = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.HISTORY) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == FoodOrderViewModel.ScreenTab.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = Strings.historyTab(currentLanguage)
                        )
                    },
                    label = {
                        Text(
                            text = Strings.historyTab(currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == FoodOrderViewModel.ScreenTab.HISTORY) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("nav_tab_history")
                )

                // 4. Profile / Info Tab
                NavigationBarItem(
                    selected = currentTab == FoodOrderViewModel.ScreenTab.PROFILE,
                    onClick = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.PROFILE) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == FoodOrderViewModel.ScreenTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = Strings.profileTab(currentLanguage)
                        )
                    },
                    label = {
                        Text(
                            text = Strings.profileTab(currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == FoodOrderViewModel.ScreenTab.PROFILE) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                FoodOrderViewModel.ScreenTab.MENU -> {
                    HomeScreen(
                        foodItems = filteredMenuItems,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        currentLanguage = currentLanguage,
                        cartItemCount = cartItemCount,
                        cartSubtotalMMK = cartSubtotalMMK,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onCategorySelected = { viewModel.setSelectedCategory(it) },
                        onLanguageToggle = { viewModel.toggleLanguage() },
                        onQuickAddToCart = { viewModel.addToCart(it) },
                        onCustomizedAddToCart = { food, qty, spice, addOns, notes ->
                            viewModel.addToCart(food, qty, spice, addOns, notes)
                        },
                        onOpenCart = { showCartSheet = true }
                    )
                }

                FoodOrderViewModel.ScreenTab.TRACKING -> {
                    OrderTrackingScreen(
                        currentTrackedOrder = currentTrackedOrder,
                        allOrders = orderHistory,
                        currentLanguage = currentLanguage,
                        kbzPayTimeRemainingSeconds = kbzPayTimeRemainingSeconds,
                        isKbzPayPaid = isKbzPayPaid,
                        onLanguageToggle = { viewModel.toggleLanguage() },
                        onSelectOrderToTrack = { viewModel.setTrackedOrder(it) },
                        onSimulateNextStatus = { viewModel.simulateNextStatusManually(it, context) },
                        onCancelOrder = { viewModel.cancelOrder(it, context) },
                        onBrowseMenuClick = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.MENU) },
                        onOpenKbzPayQr = { orderId -> viewModel.openKbzPayDialog(orderId, context) }
                    )
                }

                FoodOrderViewModel.ScreenTab.HISTORY -> {
                    OrderHistoryScreen(
                        orders = orderHistory,
                        currentLanguage = currentLanguage,
                        onLanguageToggle = { viewModel.toggleLanguage() },
                        onTrackOrder = { viewModel.setTrackedOrder(it) },
                        onReorder = { order -> viewModel.reorder(order) },
                        onBrowseMenuClick = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.MENU) }
                    )
                }

                FoodOrderViewModel.ScreenTab.PROFILE -> {
                    CustomerInfoScreen(
                        customerName = customerName,
                        customerPhone = customerPhone,
                        selectedTownship = selectedTownship,
                        deliveryAddressNote = deliveryAddressNote,
                        currentLanguage = currentLanguage,
                        onNameChange = { viewModel.setCustomerName(it) },
                        onPhoneChange = { viewModel.setCustomerPhone(it) },
                        onTownshipClick = { showTownshipPicker = true },
                        onAddressNoteChange = { viewModel.setDeliveryAddressNote(it) },
                        onLanguageToggle = { viewModel.toggleLanguage() },
                        onSaveProfile = {
                            viewModel.placeOrder(context) // or save info
                            viewModel.showInAppNotification(
                                if (currentLanguage == Language.BURMESE) "အချက်အလက်များကို သိမ်းဆည်းပြီးပါပြီ"
                                else "Delivery info saved locally!"
                            )
                        }
                    )
                }
            }

            // In-App Notification Toast Banner at the Top
            InAppNotificationBanner(
                message = inAppNotification,
                onDismiss = { viewModel.dismissInAppNotification() },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Cart Bottom Sheet
    if (showCartSheet) {
        CartBottomSheet(
            cartItems = cartItems,
            customerName = customerName,
            customerPhone = customerPhone,
            selectedTownship = selectedTownship,
            deliveryAddressNote = deliveryAddressNote,
            selectedPaymentMethod = selectedPaymentMethod,
            subtotalMMK = cartSubtotalMMK,
            deliveryFeeMMK = deliveryFeeMMK,
            grandTotalMMK = grandTotalMMK,
            currentLanguage = currentLanguage,
            kbzPayTimeRemainingSeconds = kbzPayTimeRemainingSeconds,
            isKbzPayPaid = isKbzPayPaid,
            onNameChange = { viewModel.setCustomerName(it) },
            onPhoneChange = { viewModel.setCustomerPhone(it) },
            onTownshipClick = { showTownshipPicker = true },
            onAddressNoteChange = { viewModel.setDeliveryAddressNote(it) },
            onPaymentMethodChange = { viewModel.setPaymentMethod(it, context) },
            onOpenKbzPayQr = { viewModel.openKbzPayDialog(null, context) },
            onUpdateQuantity = { item, qty -> viewModel.updateCartItemQuantity(item, qty) },
            onRemoveItem = { item -> viewModel.removeCartItem(item) },
            onPlaceOrder = {
                val result = viewModel.placeOrder(context)
                if (result.isSuccess) {
                    showCartSheet = false
                }
            },
            onDismiss = { showCartSheet = false }
        )
    }

    // Myanmar Township Picker Bottom Sheet
    if (showTownshipPicker) {
        MyanmarTownshipPickerBottomSheet(
            selectedTownship = selectedTownship,
            currentLanguage = currentLanguage,
            onTownshipSelected = { township ->
                viewModel.setSelectedTownship(township)
            },
            onDismiss = { showTownshipPicker = false }
        )
    }

    // KBZ Pay QR & 10-Minute Timer Dialog
    if (showKbzPayDialog) {
        val amount = if (grandTotalMMK > 0) grandTotalMMK else (currentTrackedOrder?.grandTotalMMK ?: 12500)
        KbzPayDialog(
            amountMMK = amount,
            orderId = kbzPayOrderId ?: currentTrackedOrder?.orderId,
            timeRemainingSeconds = kbzPayTimeRemainingSeconds,
            isPaid = isKbzPayPaid,
            currentLanguage = currentLanguage,
            onConfirmPaid = { viewModel.confirmKbzPayPaid() },
            onSimulateTimerExpiry = { viewModel.simulateKbzTimerExpiry(context) },
            onDismiss = { viewModel.closeKbzPayDialog() }
        )
    }
}

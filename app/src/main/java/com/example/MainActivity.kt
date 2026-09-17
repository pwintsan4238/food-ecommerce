package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
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
import com.example.ui.components.AdminNavigationDrawer
import com.example.ui.components.AppNavigationDrawer
import com.example.ui.components.AuthDialogSheet
import com.example.ui.components.CartBottomSheet
import com.example.ui.components.ContactSupportDialog
import com.example.ui.components.InAppNotificationBanner
import com.example.ui.components.KbzPayDialog
import com.example.ui.components.MyanmarTownshipPickerBottomSheet
import com.example.ui.components.SettingsDialogSheet
import com.example.ui.components.SwitchAccountSheet
import com.example.ui.screens.AdminDashboardScreen
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
        } else {
            // Default to Home page on standard app open
            viewModel.navigateTo(FoodOrderViewModel.ScreenTab.HOME)
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
    val browseMode by viewModel.browseMode.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedPrepStyle by viewModel.selectedPrepStyle.collectAsStateWithLifecycle()
    val selectedOccasion by viewModel.selectedOccasion.collectAsStateWithLifecycle()
    val selectedPriceRange by viewModel.selectedPriceRange.collectAsStateWithLifecycle()
    val activeFilterCount by viewModel.activeFilterCount.collectAsStateWithLifecycle()
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

    // Centralized Settings & Reviews
    val businessSettings by viewModel.businessSettings.collectAsStateWithLifecycle()
    val allReviews by viewModel.allReviews.collectAsStateWithLifecycle()

    // Auth & Guest State
    val showAuthDialog by viewModel.showAuthDialog.collectAsStateWithLifecycle()
    val isGuestUser by viewModel.isGuestUser.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProductsList.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsersList.collectAsStateWithLifecycle()
    val selectedAdminSection by viewModel.selectedAdminSection.collectAsStateWithLifecycle()
    val paymentConfig by viewModel.paymentConfig.collectAsStateWithLifecycle()
    val logisticsConfig by viewModel.logisticsConfig.collectAsStateWithLifecycle()
    val deliveryType by viewModel.deliveryType.collectAsStateWithLifecycle()
    val townships by viewModel.townships.collectAsStateWithLifecycle()

    // Modals state
    var showCartSheet by remember { mutableStateOf(false) }
    var showTownshipPicker by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showSwitchAccountSheet by remember { mutableStateOf(false) }
    var showContactSupportDialog by remember { mutableStateOf(false) }
    var contactSupportOrderId by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val activeOrderCount = remember(orderHistory) {
        orderHistory.count { it.status != OrderStatus.DELIVERED.name && it.status != OrderStatus.CANCELLED.name }
    }

    // System Back Gesture:
    // 0. If any bottom sheet or dialog is open, dismiss the modal first
    BackHandler(enabled = showCartSheet || showSettingsSheet || showSwitchAccountSheet || showTownshipPicker || showAuthDialog || showContactSupportDialog) {
        showCartSheet = false
        showSettingsSheet = false
        showSwitchAccountSheet = false
        showTownshipPicker = false
        showContactSupportDialog = false
        contactSupportOrderId = ""
        if (showAuthDialog) viewModel.closeAuthDialog()
    }

    // 1. If drawer is open, close drawer
    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }

    // 2. If logged in as admin: back button navigates through admin history (default is Admin Dashboard)
    BackHandler(enabled = !drawerState.isOpen && isAdmin) {
        viewModel.adminNavigateBack()
    }

    // 3. If regular user / not admin: back button returns to Home page if on another tab
    BackHandler(enabled = !drawerState.isOpen && !isAdmin && currentTab != FoodOrderViewModel.ScreenTab.HOME) {
        viewModel.navigateTo(FoodOrderViewModel.ScreenTab.HOME)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (isAdmin) {
                AdminNavigationDrawer(
                    currentSection = selectedAdminSection,
                    currentLanguage = currentLanguage,
                    onSelectSection = { section ->
                        viewModel.setAdminSection(section)
                        viewModel.navigateTo(FoodOrderViewModel.ScreenTab.ADMIN)
                        coroutineScope.launch { drawerState.close() }
                    },
                    onLogout = {
                        viewModel.logout()
                        viewModel.navigateTo(FoodOrderViewModel.ScreenTab.HOME)
                        coroutineScope.launch { drawerState.close() }
                    },
                    onCloseDrawer = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            } else {
                AppNavigationDrawer(
                    customerName = customerName,
                    customerPhone = customerPhone,
                    currentLanguage = currentLanguage,
                    activeOrderCount = activeOrderCount,
                    cartItemCount = cartItemCount,
                    onNavigateHome = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.HOME) },
                    onNavigateTracking = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.TRACKING) },
                    onNavigateHistory = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.HISTORY) },
                    onNavigateProfile = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.PROFILE) },
                    onOpenCart = { showCartSheet = true },
                    onOpenSettings = { showSettingsSheet = true },
                    onSwitchAccount = { showSwitchAccountSheet = true },
                    onLogout = { viewModel.logout() },
                    onOpenAuth = { viewModel.openAuthDialog() },
                    isAdmin = isAdmin,
                    onNavigateAdmin = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.ADMIN) },
                    brandName = businessSettings.brandNameEn,
                    supportPhone = businessSettings.supportPhone,
                    onContactSupport = { showContactSupportDialog = true },
                    onCloseDrawer = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (currentTab != FoodOrderViewModel.ScreenTab.ADMIN) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        // 1. Home Tab
                    NavigationBarItem(
                        selected = currentTab == FoodOrderViewModel.ScreenTab.HOME,
                        onClick = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == FoodOrderViewModel.ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = Strings.homeTab(currentLanguage)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.homeTab(currentLanguage),
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == FoodOrderViewModel.ScreenTab.HOME) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
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

                    // 5. Admin Tab (Visible when logged in as admin)
                    if (isAdmin) {
                        NavigationBarItem(
                            selected = currentTab == FoodOrderViewModel.ScreenTab.ADMIN,
                            onClick = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.ADMIN) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == FoodOrderViewModel.ScreenTab.ADMIN) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                    contentDescription = Strings.adminPortal(currentLanguage)
                                )
                            },
                            label = {
                                Text(
                                    text = Strings.adminBadge(currentLanguage),
                                    fontSize = 11.sp,
                                    fontWeight = if (currentTab == FoodOrderViewModel.ScreenTab.ADMIN) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("nav_tab_admin")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    FoodOrderViewModel.ScreenTab.HOME -> {
                        HomeScreen(
                            foodItems = filteredMenuItems,
                            searchQuery = searchQuery,
                            browseMode = browseMode,
                            selectedCategory = selectedCategory,
                            selectedPrepStyle = selectedPrepStyle,
                            selectedOccasion = selectedOccasion,
                            selectedPriceRange = selectedPriceRange,
                            activeFilterCount = activeFilterCount,
                            currentLanguage = currentLanguage,
                            cartItemCount = cartItemCount,
                            cartSubtotalMMK = cartSubtotalMMK,
                            reviews = allReviews,
                            onSubmitReview = { foodId, name, rating, comment ->
                                viewModel.submitReview(foodId, name, "", rating, comment)
                            },
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onBrowseModeSelected = { viewModel.setBrowseMode(it) },
                            onCategorySelected = { viewModel.setSelectedCategory(it) },
                            onPrepStyleSelected = { viewModel.setSelectedPrepStyle(it) },
                            onOccasionSelected = { viewModel.setSelectedOccasion(it) },
                            onPriceRangeSelected = { viewModel.setSelectedPriceRange(it) },
                            onClearFilters = { viewModel.clearAllFilters() },
                            onLanguageToggle = { viewModel.toggleLanguage() },
                            onQuickAddToCart = { viewModel.addToCart(it) },
                            onCustomizedAddToCart = { food, qty, spice, addOns, notes, coldStorage, specialPrep, fulfillment ->
                                viewModel.addToCart(food, qty, spice, addOns, notes, coldStorage, specialPrep, fulfillment)
                            },
                            selectedRegionName = selectedTownship.nameEn,
                            onOpenCart = { showCartSheet = true },
                            onOpenDrawer = {
                                coroutineScope.launch { drawerState.open() }
                            },
                            onOpenAccount = {
                                showSettingsSheet = true
                            },
                            isGuest = isGuestUser,
                            onOpenAuth = { viewModel.openAuthDialog() },
                            isAdmin = isAdmin,
                            onOpenAdmin = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.ADMIN) },
                            onAdminBack = { viewModel.adminNavigateBack() }
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
                            onBrowseMenuClick = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.HOME) },
                            onOpenKbzPayQr = { orderId -> viewModel.openKbzPayDialog(orderId, context) },
                            brandName = businessSettings.brandNameEn,
                            supportPhone = businessSettings.supportPhone,
                            isAdmin = isAdmin,
                            onAdminBack = { viewModel.adminNavigateBack() }
                        )
                    }

                    FoodOrderViewModel.ScreenTab.HISTORY -> {
                        OrderHistoryScreen(
                            orders = orderHistory,
                            currentLanguage = currentLanguage,
                            onLanguageToggle = { viewModel.toggleLanguage() },
                            onTrackOrder = { viewModel.setTrackedOrder(it) },
                            onReorder = { order -> viewModel.reorder(order) },
                            onBrowseMenuClick = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.HOME) },
                            onRequestReturn = { orderId, reason -> viewModel.requestOrderReturn(orderId, reason) },
                            onContactSupport = { orderId ->
                                contactSupportOrderId = orderId
                                showContactSupportDialog = true
                            },
                            isAdmin = isAdmin,
                            onAdminBack = { viewModel.adminNavigateBack() }
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
                            },
                            onSwitchAccount = { showSwitchAccountSheet = true },
                            onLogout = { viewModel.logout() },
                            onOpenAuth = { viewModel.openAuthDialog() },
                            isAdmin = isAdmin,
                            onAdminBack = { viewModel.adminNavigateBack() }
                        )
                    }

                    FoodOrderViewModel.ScreenTab.ADMIN -> {
                        AdminDashboardScreen(
                            viewModel = viewModel,
                            onBackToStorefront = { viewModel.navigateTo(FoodOrderViewModel.ScreenTab.HOME) },
                            onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
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
    }

    // Settings Bottom Sheet
    if (showSettingsSheet) {
        SettingsDialogSheet(
            currentLanguage = currentLanguage,
            customerName = customerName,
            customerPhone = customerPhone,
            onLanguageToggle = { viewModel.toggleLanguage() },
            onOpenProfile = {
                viewModel.navigateTo(FoodOrderViewModel.ScreenTab.PROFILE)
            },
            onSwitchAccount = {
                showSwitchAccountSheet = true
            },
            onContactSupport = {
                showContactSupportDialog = true
            },
            onLogout = {
                viewModel.logout()
            },
            onOpenAuth = {
                viewModel.openAuthDialog()
            },
            isAdmin = isAdmin,
            onOpenAdmin = {
                viewModel.navigateTo(FoodOrderViewModel.ScreenTab.ADMIN)
            },
            onAdminLogin = {
                viewModel.loginAsAdmin()
            },
            onDismiss = { showSettingsSheet = false }
        )
    }

    // Switch Account Sheet
    if (showSwitchAccountSheet) {
        SwitchAccountSheet(
            currentLanguage = currentLanguage,
            currentName = customerName,
            currentPhone = customerPhone,
            onConfirmSwitch = { newName, newPhone ->
                viewModel.switchAccount(newName, newPhone)
            },
            onDismiss = { showSwitchAccountSheet = false }
        )
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
            paymentConfig = paymentConfig,
            logisticsConfig = logisticsConfig,
            deliveryType = deliveryType,
            onNameChange = { viewModel.setCustomerName(it) },
            onPhoneChange = { viewModel.setCustomerPhone(it) },
            onTownshipClick = { showTownshipPicker = true },
            onAddressNoteChange = { viewModel.setDeliveryAddressNote(it) },
            onDeliveryTypeChange = { viewModel.setDeliveryType(it) },
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
            townships = townships,
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
            qrOptions = paymentConfig.qrPaymentOptions.filter { it.isEnabled },
            onConfirmPaid = { viewModel.confirmKbzPayPaid() },
            onSimulateTimerExpiry = { viewModel.simulateKbzTimerExpiry(context) },
            onDismiss = { viewModel.closeKbzPayDialog() }
        )
    }

    // Auth Dialog Sheet (Login / Sign Up / Continue as Guest)
    if (showAuthDialog) {
        AuthDialogSheet(
            currentLanguage = currentLanguage,
            onLoginSuccess = { name, phone ->
                viewModel.loginOrSignUp(name, phone)
            },
            onContinueAsGuest = {
                viewModel.continueAsGuest()
            },
            onAdminLogin = {
                viewModel.loginAsAdmin()
            },
            onDismiss = {
                viewModel.closeAuthDialog()
            }
        )
    }

    // Contact & Customer Support Dialog
    if (showContactSupportDialog) {
        ContactSupportDialog(
            currentLanguage = currentLanguage,
            initialCustomerName = customerName,
            initialCustomerPhone = customerPhone,
            initialOrderId = contactSupportOrderId,
            supportPhone = businessSettings.supportPhone,
            supportEmail = businessSettings.supportEmail,
            onSubmitMessage = { name, phone, email, orderId, subject, category, message ->
                viewModel.submitCustomerMessage(name, phone, email, orderId, subject, category, message)
            },
            onDismiss = {
                showContactSupportDialog = false
                contactSupportOrderId = ""
            }
        )
    }
}

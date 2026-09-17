package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.data.ProductEntity
import com.example.data.UserAccountEntity
import com.example.model.*
import com.example.ui.components.ProductEditDialog
import com.example.ui.screens.admin.*
import com.example.viewmodel.FoodOrderViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class AdminTab {
    PRODUCTS, USERS, ORDERS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: FoodOrderViewModel,
    onBackToStorefront: () -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val allProducts by viewModel.adminAllProducts.collectAsState()
    val allUsers by viewModel.allUserAccounts.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val selectedAdminSection by viewModel.selectedAdminSection.collectAsState()
    val promotions by viewModel.promotions.collectAsState()
    val paymentConfig by viewModel.paymentConfig.collectAsState()
    val storeSettings by viewModel.storeSettings.collectAsState()
    val logisticsConfig by viewModel.logisticsConfig.collectAsState()
    val businessSettings by viewModel.businessSettings.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()
    val allBrands by viewModel.allBrands.collectAsState()
    val townships by viewModel.townships.collectAsState()
    val allReviews by viewModel.allReviews.collectAsState()
    val allCustomerMessages by viewModel.allCustomerMessages.collectAsState()

    // Dialogs state
    var productToEdit by remember { mutableStateOf<FoodItem?>(null) }
    var isAddingNewProduct by remember { mutableStateOf(false) }
    var isCreatingUser by remember { mutableStateOf(false) }
    var isAddingNewPromo by remember { mutableStateOf(false) }
    var isAddingNewQrPayment by remember { mutableStateOf(false) }
    var isAddingDeliveryZone by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<FoodItem?>(null) }
    var userToDelete by remember { mutableStateOf<UserAccountEntity?>(null) }
    var customerToEdit by remember { mutableStateOf<UserAccountEntity?>(null) }
    var selectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(null) }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val handleBack = {
        val handled = viewModel.adminNavigateBack()
        if (!handled && selectedAdminSection != AdminSection.OVERVIEW) {
            viewModel.setAdminSection(AdminSection.OVERVIEW)
        } else if (!handled) {
            onBackToStorefront()
        }
    }

    androidx.activity.compose.BackHandler {
        handleBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedAdminSection.title(currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFDE8E8)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "တိမ်တမန် စီမံခန့်ခွဲရေး"
                            else "Admin Console",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = handleBack,
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                actions = {
                    // Language Toggle "MM" / "EN"
                    TextButton(
                        onClick = { viewModel.toggleLanguage() },
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("admin_lang_toggle")
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "MM" else "EN",
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }

                    // Notification Bell with badge "3"
                    IconButton(
                        onClick = { showNotificationsSheet = true },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("admin_bell_button")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color(0xFFD32F2F),
                                    contentColor = Color.White
                                ) {
                                    Text("3", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Admin Notifications",
                                tint = Color(0xFF334155),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Storefront Button
                    IconButton(
                        onClick = onBackToStorefront,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("admin_storefront_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Back to Storefront",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Admin Slide Menu Button
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("admin_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Admin Slide Menu",
                            tint = Color(0xFF1E293B),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            when (selectedAdminSection) {
                AdminSection.PRODUCTS, AdminSection.INVENTORY, AdminSection.OVERVIEW -> {
                    ExtendedFloatingActionButton(
                        onClick = { isAddingNewProduct = true },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text(Strings.addProduct(currentLanguage)) },
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White,
                        modifier = Modifier.testTag("add_product_fab")
                    )
                }
                AdminSection.CUSTOMERS -> {
                    ExtendedFloatingActionButton(
                        onClick = { isCreatingUser = true },
                        icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                        text = { Text(Strings.createUserAccount(currentLanguage)) },
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White,
                        modifier = Modifier.testTag("create_user_fab")
                    )
                }
                AdminSection.PROMOTIONS -> {
                    ExtendedFloatingActionButton(
                        onClick = { isAddingNewPromo = true },
                        icon = { Icon(Icons.Default.Campaign, contentDescription = null) },
                        text = { Text(if (currentLanguage == Language.BURMESE) "ပရိုမိုးရှင်း အသစ်ထည့်" else "New Promo") },
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White,
                        modifier = Modifier.testTag("create_promo_fab")
                    )
                }
                AdminSection.PAYMENTS -> {
                    ExtendedFloatingActionButton(
                        onClick = { isAddingNewQrPayment = true },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text(if (currentLanguage == Language.BURMESE) "QR ပေးချေမှု အသစ်ထည့်" else "Add QR Payment") },
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White,
                        modifier = Modifier.testTag("add_qr_payment_fab")
                    )
                }
                AdminSection.LOGISTICS -> {
                    ExtendedFloatingActionButton(
                        onClick = { isAddingDeliveryZone = true },
                        icon = { Icon(Icons.Default.AddLocationAlt, contentDescription = null) },
                        text = { Text(if (currentLanguage == Language.BURMESE) "ဇုန်အသစ်ထည့်" else "Add Zone") },
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White,
                        modifier = Modifier.testTag("add_logistics_zone_fab")
                    )
                }
                else -> {}
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // In-page Back Banner for all sub-sections
            if (selectedAdminSection != AdminSection.OVERVIEW) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { handleBack() }
                        .testTag("admin_inpage_back_bar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Overview",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ပင်မ စီမံခန့်ခွဲရေးသို့ ပြန်သွားရန်" else "Back to Admin Overview",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedAdminSection) {
                    AdminSection.OVERVIEW -> {
                        AdminOverviewSection(
                            products = allProducts,
                            orders = allOrders,
                            users = allUsers,
                            currentLanguage = currentLanguage,
                            onNavigateToSection = { viewModel.setAdminSection(it) },
                            onQuickAdvanceOrder = { orderId, newStatus ->
                                viewModel.adminUpdateOrderStatus(orderId, newStatus, context)
                            },
                            onBackToStorefront = onBackToStorefront
                        )
                    }
                AdminSection.PRODUCTS -> {
                    AdminProductsSection(
                        products = allProducts,
                        currentLanguage = currentLanguage,
                        onEditProduct = { productToEdit = it },
                        onDeleteProduct = { productToDelete = it },
                        onQuickPriceChange = { product, newPrice ->
                            viewModel.quickUpdateProductPrice(product, newPrice)
                        }
                    )
                }
                AdminSection.ORDERS -> {
                    AdminOrdersSection(
                        orders = allOrders,
                        currentLanguage = currentLanguage,
                        onUpdateOrderStatus = { orderId, status ->
                            viewModel.adminUpdateOrderStatus(orderId, status, context)
                        },
                        onDeleteOrder = { orderId ->
                            viewModel.adminDeleteOrder(orderId)
                        },
                        onSelectOrderForDetail = { selectedOrderForDetail = it }
                    )
                }
                AdminSection.RETURNS -> {
                    AdminReturnsSection(
                        orders = allOrders,
                        currentLanguage = currentLanguage,
                        onUpdateReturnStatus = { orderId, returnStatus, paymentStatus ->
                            viewModel.adminUpdateReturnStatus(orderId, returnStatus, paymentStatus)
                        }
                    )
                }
                AdminSection.CUSTOMERS -> {
                    AdminUsersSection(
                        users = allUsers,
                        currentLanguage = currentLanguage,
                        onLoginAsUser = { viewModel.loginAsUser(it) },
                        onDeleteUser = { userToDelete = it },
                        onCreateUserClick = { isCreatingUser = true },
                        onEditCustomer = { customerToEdit = it }
                    )
                }
                AdminSection.MESSAGES -> {
                    AdminMessagesSection(
                        messages = allCustomerMessages,
                        currentLanguage = currentLanguage,
                        onReplyToMessage = { messageId, reply, status ->
                            viewModel.adminReplyToCustomerMessage(messageId, reply, status)
                        },
                        onDeleteMessage = { messageId ->
                            viewModel.deleteCustomerMessage(messageId)
                        }
                    )
                }
                AdminSection.REVIEWS -> {
                    AdminReviewsSection(
                        reviews = allReviews,
                        currentLanguage = currentLanguage,
                        onReplyToReview = { reviewId, reply ->
                            viewModel.adminReplyToReview(reviewId, reply)
                        },
                        onDeleteReview = { reviewId ->
                            viewModel.deleteReview(reviewId)
                        }
                    )
                }
                AdminSection.INVENTORY -> {
                    AdminInventorySection(
                        products = allProducts,
                        currentLanguage = currentLanguage,
                        onToggleAvailability = { viewModel.toggleProductAvailability(it) },
                        onEditProduct = { productToEdit = it }
                    )
                }
                AdminSection.PAYMENTS -> {
                    AdminPaymentsSection(
                        paymentConfig = paymentConfig,
                        orders = allOrders,
                        currentLanguage = currentLanguage,
                        onUpdateConfig = { viewModel.updatePaymentConfig(it) },
                        isAddingQr = isAddingNewQrPayment,
                        onDismissAddQr = { isAddingNewQrPayment = false }
                    )
                }
                AdminSection.LOGISTICS -> {
                    AdminLogisticsSection(
                        logisticsConfig = logisticsConfig,
                        townships = townships,
                        currentLanguage = currentLanguage,
                        onUpdateLogisticsConfig = { viewModel.updateLogisticsConfig(it) },
                        onUpdateTownship = { viewModel.updateTownship(it) },
                        onAddCustomTownship = { nameEn, nameMy, regEn, regMy, fee, mins ->
                            viewModel.addCustomTownship(nameEn, nameMy, regEn, regMy, fee, mins)
                        },
                        onToggleTownship = { viewModel.toggleTownshipEnabled(it) },
                        onResetTownships = { viewModel.resetTownshipsToDefault() },
                        onAddDeliveryPartner = { viewModel.addDeliveryPartner(it) },
                        onUpdateDeliveryPartner = { viewModel.updateDeliveryPartner(it) },
                        onDeleteDeliveryPartner = { viewModel.deleteDeliveryPartner(it) },
                        onToggleDeliveryPartner = { viewModel.toggleDeliveryPartner(it) },
                        isAddingZone = isAddingDeliveryZone,
                        onDismissAddZone = { isAddingDeliveryZone = false }
                    )
                }
                AdminSection.PROMOTIONS -> {
                    AdminPromotionsSection(
                        promotions = promotions,
                        currentLanguage = currentLanguage,
                        onTogglePromotion = { viewModel.togglePromotion(it) },
                        onAddPromotion = { titleEn, titleMy, code, discount, descEn, descMy ->
                            viewModel.addPromotion(titleEn, titleMy, code, discount, descEn, descMy)
                        },
                        onDeletePromotion = { viewModel.deletePromotion(it) },
                        isAddingPromo = isAddingNewPromo,
                        onDismissAddPromo = { isAddingNewPromo = false }
                    )
                }
                AdminSection.REPORTS -> {
                    AdminReportsSection(
                        orders = allOrders,
                        products = allProducts,
                        currentLanguage = currentLanguage
                    )
                }
                AdminSection.SETTINGS -> {
                    AdminSettingsSection(
                        storeSettings = storeSettings,
                        businessSettings = businessSettings,
                        categories = allCategories,
                        brands = allBrands,
                        currentLanguage = currentLanguage,
                        onUpdateSettings = { viewModel.updateStoreSettings(it) },
                        onUpdateBusinessSettings = { viewModel.adminUpdateBusinessSettings(it) },
                        onSaveCategory = { viewModel.adminSaveCategory(it) },
                        onArchiveCategory = { catId, arch -> viewModel.adminArchiveCategory(catId, arch) },
                        onSaveBrand = { viewModel.adminSaveBrand(it) },
                        onArchiveBrand = { brandId, arch -> viewModel.adminArchiveBrand(brandId, arch) },
                        onBackToStorefront = onBackToStorefront,
                        onToggleLanguage = { viewModel.toggleLanguage() }
                    )
                }
            }
        }
    }
}

    // Notifications Bottom Sheet / Dialog
    if (showNotificationsSheet) {
        AlertDialog(
            onDismissRequest = { showNotificationsSheet = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "စီမံခန့်ခွဲသူ အသိပေးချက်များ (၃ ခု)"
                        else "Admin Notifications (3)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEFF6FF),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "အော်ဒါအသစ် #1042 ရောက်ရှိပါသည်" else "New Order #1042 Received",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF1D4ED8)
                            )
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ကမာရွတ်မြို့နယ်မှ • ၂၈,၅၀၀ ကျပ် (KBZPay)"
                                else "From Kamayut • 28,500 MMK (KBZPay)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFECFDF5),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "KBZPay ငွေပေးချေမှု အတည်ပြုပြီး" else "KBZPay Payment Verified",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF047857)
                            )
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "အော်ဒါ #1041 အတွက် ၃၅,၀၀၀ ကျပ် လက်ခံရရှိပါသည်"
                                else "Order #1041 • 35,000 MMK verified successfully",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF2F2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ပစ္စည်းလက်ကျန် နည်းနေပါသည်" else "Low Inventory Alert",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFB91C1C)
                            )
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ရခိုင် မုန့်တီ (အရည်) ၂ ပွဲသာ ကျန်ပါတော့သည်"
                                else "Rakhine Mont Di (Soup) only 2 portions left",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationsSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "ပိတ်မည်" else "Close")
                }
            }
        )
    }

    // Modal Sheet for Add/Edit Product
    if (isAddingNewProduct || productToEdit != null) {
        ProductEditDialog(
            product = productToEdit,
            currentLanguage = currentLanguage,
            categories = allCategories,
            brands = allBrands,
            onDismiss = {
                productToEdit = null
                isAddingNewProduct = false
            },
            onSave = { updatedProduct ->
                viewModel.adminSaveProduct(updatedProduct)
                productToEdit = null
                isAddingNewProduct = false
            }
        )
    }

    // Modal Sheet for Create User
    if (isCreatingUser) {
        CreateUserDialog(
            currentLanguage = currentLanguage,
            onDismiss = { isCreatingUser = false },
            onSave = { name, phone, role, townshipId, addressNote, password ->
                viewModel.createUserAccount(
                    name = name,
                    phone = phone,
                    role = role,
                    townshipId = townshipId,
                    addressNote = addressNote,
                    password = password
                )
                isCreatingUser = false
            }
        )
    }

    // Delete / Archive Product Confirmation
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = {
                Text(
                    if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း ဖျက်သိမ်း/သိမ်းဆည်းခြင်း"
                    else "Delete or Archive Product"
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (currentLanguage == Language.BURMESE)
                            "${product.nameMy} (${product.nameEn}) အား ဆောင်ရွက်လိုသည့် လုပ်ဆောင်ချက်ကို ရွေးချယ်ပါ:"
                        else
                            "Choose how you want to handle '${product.nameEn}':",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (currentLanguage == Language.BURMESE)
                            "• Archive / Soft-Delete (အကြံပြုထားသည်): ဝယ်ယူသူများ မမြင်ရအောင် ဖျောက်ထားမည်ဖြစ်ပြီး ယခင်ဝယ်ယူထားသော အော်ဒါမှတ်တမ်းများ မပျက်စီးအောင် ထိန်းသိမ်းထားပါမည်။\n• Permanent Delete: ဒေတာဘေ့စ်မှ အပြီးတိုင် ဖျက်ပစ်မည်။"
                        else
                            "• Archive / Soft-Delete (Recommended): Hides from store catalog while preserving historical order receipts & purchased prices.\n• Permanent Delete: Completely removes from database.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            viewModel.adminDeleteProduct(product.id, hardDelete = true)
                            productToDelete = null
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(if (currentLanguage == Language.BURMESE) "အပြီးတိုင်ဖျက်" else "Hard Delete")
                    }
                    Button(
                        onClick = {
                            viewModel.adminArchiveProduct(product.id, true)
                            productToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier.testTag("confirm_delete_product_btn")
                    ) {
                        Text(if (currentLanguage == Language.BURMESE) "Archive လုပ်မည်" else "Archive / Soft-Delete")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်ဆောင်ပါ" else "Cancel")
                }
            }
        )
    }

    // Delete User Confirmation
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text(Strings.deleteAccount(currentLanguage)) },
            text = {
                Text(
                    if (currentLanguage == Language.BURMESE) "${user.name} (${user.phone}) အကောင့်အား ဖျက်ပစ်ရန် သေချာပါသလား?"
                    else "Are you sure you want to delete user account '${user.name}' (${user.phone})?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUserAccount(user.phone, user.name)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_user_btn")
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "ဖျက်မည်" else "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text(if (currentLanguage == Language.BURMESE) "မဖျက်တော့ပါ" else "Cancel")
                }
            }
        )
    }

    // Modal Dialog for Complete Order & Transaction Details
    selectedOrderForDetail?.let { order ->
        val liveOrder = allOrders.find { it.orderId == order.orderId } ?: order
        AdminOrderDetailDialog(
            order = liveOrder,
            currentLanguage = currentLanguage,
            onDismiss = { selectedOrderForDetail = null },
            onUpdateOrderStatus = { status ->
                viewModel.adminUpdateOrderStatus(liveOrder.orderId, status, context)
            },
            onUpdatePaymentStatus = { payStatus ->
                viewModel.adminUpdatePaymentStatus(liveOrder.orderId, payStatus)
            },
            onUpdateTrackingInfo = { trackingNo, partnerName ->
                viewModel.adminUpdateTrackingInfo(liveOrder.orderId, trackingNo, partnerName)
            }
        )
    }

    // Modal Dialog for Customer Profile & Admin CRM Notes
    customerToEdit?.let { customer ->
        val liveCustomer = allUsers.find { it.phone == customer.phone } ?: customer
        AdminEditCustomerDialog(
            customer = liveCustomer,
            currentLanguage = currentLanguage,
            onDismiss = { customerToEdit = null },
            onSave = { name, email, townshipId, defaultAddressNote, adminNotes ->
                viewModel.adminUpdateCustomerDetails(
                    phone = liveCustomer.phone,
                    name = name,
                    email = email,
                    townshipId = townshipId,
                    defaultAddressNote = defaultAddressNote,
                    adminNotes = adminNotes
                )
                customerToEdit = null
            }
        )
    }
}

// -------------------------------------------------------------------------
// Metrics Bar
// -------------------------------------------------------------------------
@Composable
private fun AdminMetricsBar(
    productsCount: Int,
    usersCount: Int,
    ordersCount: Int,
    totalRevenueMMK: Int,
    currentLanguage: Language
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetricPill(
                label = if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း" else "Products",
                value = "$productsCount",
                icon = Icons.Default.Inventory2,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            MetricPill(
                label = if (currentLanguage == Language.BURMESE) "အသုံးပြုသူ" else "Users",
                value = "$usersCount",
                icon = Icons.Default.Group,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            MetricPill(
                label = if (currentLanguage == Language.BURMESE) "အော်ဒါများ" else "Orders",
                value = "$ordersCount",
                icon = Icons.Default.ReceiptLong,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
            MetricPill(
                label = if (currentLanguage == Language.BURMESE) "ဝင်ငွေ" else "Revenue",
                value = "${totalRevenueMMK / 1000}K Ks",
                icon = Icons.Default.Payments,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// -------------------------------------------------------------------------
// Section 1: Products & Pricing
// -------------------------------------------------------------------------
@Composable
private fun AdminProductsSection(
    products: List<FoodItem>,
    currentLanguage: Language,
    onEditProduct: (FoodItem) -> Unit,
    onDeleteProduct: (FoodItem) -> Unit,
    onQuickPriceChange: (FoodItem, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<FoodCategory?>(null) }
    var statusFilter by remember { mutableStateOf<String>("ALL") } // ALL, ACTIVE, ARCHIVED

    val filteredList = remember(products, searchQuery, selectedCategory, statusFilter) {
        products.filter { item ->
            val matchesCategory = selectedCategory == null || item.category == selectedCategory
            val matchesStatus = when (statusFilter) {
                "ACTIVE" -> !item.isArchived
                "ARCHIVED" -> item.isArchived
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    item.nameEn.contains(searchQuery, ignoreCase = true) ||
                    item.nameMy.contains(searchQuery, ignoreCase = true) ||
                    item.brand.contains(searchQuery, ignoreCase = true) ||
                    item.category.title(currentLanguage).contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesStatus && matchesSearch
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search and Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("admin_product_search_input"),
                placeholder = {
                    Text(
                        if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း / တံဆိပ် ရှာဖွေရန်..."
                        else "Search products by name or brand...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Status & Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = statusFilter == "ALL",
                    onClick = { statusFilter = "ALL" },
                    label = { Text(if (currentLanguage == Language.BURMESE) "အားလုံး" else "All") }
                )
            }
            item {
                FilterChip(
                    selected = statusFilter == "ACTIVE",
                    onClick = { statusFilter = "ACTIVE" },
                    label = { Text(if (currentLanguage == Language.BURMESE) "အရောင်းဖွင့်ထားဆဲ" else "Active Only") }
                )
            }
            item {
                FilterChip(
                    selected = statusFilter == "ARCHIVED",
                    onClick = { statusFilter = "ARCHIVED" },
                    label = { Text(if (currentLanguage == Language.BURMESE) "သိမ်းဆည်းထားသည် (Archived)" else "Archived Only") }
                )
            }
            items(FoodCategory.values().filter { it != FoodCategory.ALL }) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                    label = { Text("${cat.iconEmoji} ${cat.title(currentLanguage)}") }
                )
            }
        }

        Text(
            text = "${filteredList.size} ${if (currentLanguage == Language.BURMESE) "ခု တွေ့ရှိပါသည်" else "items listed"}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // Products List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
        ) {
            items(filteredList, key = { it.id }) { product ->
                AdminProductCard(
                    product = product,
                    currentLanguage = currentLanguage,
                    onEdit = { onEditProduct(product) },
                    onDelete = { onDeleteProduct(product) },
                    onQuickPriceAdjust = { delta ->
                        val newPrice = (product.priceMMK + delta).coerceAtLeast(500)
                        onQuickPriceChange(product, newPrice)
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminProductCard(
    product: FoodItem,
    currentLanguage: Language,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onQuickPriceAdjust: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onEdit() }
            .testTag("admin_product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Emoji / Category badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = product.iconEmoji, fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.displayName(currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (currentLanguage == Language.BURMESE) product.nameEn else product.nameMy,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (product.isArchived) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "🔒 သိမ်းဆည်းထားသည်" else "🔒 Archived",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = product.category.title(currentLanguage),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        if (product.brand.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
                            ) {
                                Text(
                                    text = "🏷️ ${product.brand}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (product.stockQuantity <= 5) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "စတော့: ${product.stockQuantity}" else "Stock: ${product.stockQuantity}",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Edit and Delete Icon buttons
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.testTag("edit_product_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Product",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_product_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Product",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Product Info Detail Field Preview with Tap to Edit indicator
            val desc = if (currentLanguage == Language.BURMESE) {
                product.descriptionMy.ifBlank { product.descriptionEn }
            } else {
                product.descriptionEn.ifBlank { product.descriptionMy }
            }
            if (desc.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📋 $desc",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ပြင်ရန် နှိပ်ပါ" else "Tap to edit",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Pricing & Quick Adjustments
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                    Text(
                        text = Strings.mmkCurrency(currentLanguage, product.priceMMK),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${product.displayUnit(currentLanguage)} • ~${product.prepTimeMin} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Quick Price Bumps (+1,000 / -1,000 MMK)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အမြန်ပြင်:" else "Quick:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    OutlinedButton(
                        onClick = { onQuickPriceAdjust(-1000) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("price_minus_${product.id}")
                    ) {
                        Text("-1K", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedButton(
                        onClick = { onQuickPriceAdjust(1000) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("price_plus_${product.id}")
                    ) {
                        Text("+1K", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Section 2: User Accounts Management
// -------------------------------------------------------------------------
@Composable
private fun AdminUsersSection(
    users: List<UserAccountEntity>,
    currentLanguage: Language,
    onLoginAsUser: (UserAccountEntity) -> Unit,
    onDeleteUser: (UserAccountEntity) -> Unit,
    onCreateUserClick: () -> Unit,
    onEditCustomer: (UserAccountEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${users.size} ${if (currentLanguage == Language.BURMESE) "ခု ဖွင့်ထားပြီး" else "registered accounts"}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onCreateUserClick,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("admin_create_user_button")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = Strings.createUserAccount(currentLanguage),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "အသုံးပြုသူ စာရင်း မရှိသေးပါ"
                    else "No user accounts registered yet",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(users, key = { it.phone }) { user ->
                    UserAccountCard(
                        user = user,
                        currentLanguage = currentLanguage,
                        onLoginAs = { onLoginAsUser(user) },
                        onDelete = { onDeleteUser(user) },
                        onEdit = { onEditCustomer(user) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserAccountCard(
    user: UserAccountEntity,
    currentLanguage: Language,
    onLoginAs: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val isAdmin = user.role == "ADMIN"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_card_${user.phone}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAdmin) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (isAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isAdmin) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (isAdmin) "ADMIN" else "CUSTOMER",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isAdmin) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "📞 ${user.phone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (user.email.isNotBlank()) {
                        Text(
                            text = "✉️ ${user.email}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (user.defaultAddressNote.isNotBlank()) {
                        Text(
                            text = "📍 ${user.defaultAddressNote}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_user_${user.phone}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete User",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Customer Lifetime Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "မှာယူမှု စုစုပေါင်း" else "Total Orders",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${user.totalOrdersCount} ${if (currentLanguage == Language.BURMESE) "ကြိမ်" else "orders"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "သုံးစွဲငွေ စုစုပေါင်း" else "Total Spent",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = Strings.mmkCurrency(currentLanguage, user.totalSpentMMK),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Admin CRM Notes Preview if any
            if (user.adminNotes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFFBEB),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.StickyNote2,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = user.adminNotes,
                            fontSize = 11.sp,
                            color = Color(0xFF92400E),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Action Buttons Row: Edit Details + Login As
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("edit_user_${user.phone}"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အချက်အလက် ပြင်" else "Edit / Notes",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                FilledTonalButton(
                    onClick = onLoginAs,
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("login_as_${user.phone}"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.loginAsThisUser(currentLanguage),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Section 3: Order Inspection & Fulfillment
// -------------------------------------------------------------------------
@Composable
private fun AdminOrdersSection(
    orders: List<OrderEntity>,
    currentLanguage: Language,
    onUpdateOrderStatus: (String, OrderStatus) -> Unit,
    onDeleteOrder: (String) -> Unit,
    onSelectOrderForDetail: (OrderEntity) -> Unit
) {
    var selectedStatusFilter by remember { mutableStateOf<OrderStatus?>(null) }
    var orderSearchQuery by remember { mutableStateOf("") }

    val filteredOrders = remember(orders, selectedStatusFilter, orderSearchQuery) {
        orders.filter { order ->
            val matchesStatus = selectedStatusFilter == null || order.status == selectedStatusFilter?.name
            val matchesSearch = orderSearchQuery.isBlank() ||
                    order.orderId.contains(orderSearchQuery, ignoreCase = true) ||
                    order.customerName.contains(orderSearchQuery, ignoreCase = true) ||
                    order.customerPhone.contains(orderSearchQuery, ignoreCase = true)
            matchesStatus && matchesSearch
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = orderSearchQuery,
                onValueChange = { orderSearchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_order_search_input"),
                placeholder = {
                    Text(
                        if (currentLanguage == Language.BURMESE) "အော်ဒါ ID၊ အမည် သို့မဟုတ် ဖုန်းဖြင့် ရှာရန်..."
                        else "Search orders by ID, customer name, phone...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Status Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text(if (currentLanguage == Language.BURMESE) "အားလုံး" else "All") }
                )
            }
            items(OrderStatus.values()) { status ->
                FilterChip(
                    selected = selectedStatusFilter == status,
                    onClick = { selectedStatusFilter = if (selectedStatusFilter == status) null else status },
                    label = { Text(status.title(currentLanguage)) }
                )
            }
        }

        Text(
            text = "${filteredOrders.size} ${if (currentLanguage == Language.BURMESE) "ခု ပြသနေပါသည်" else "orders found"}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "ရှာဖွေမှုနှင့် ကိုက်ညီသော အော်ဒါ မရှိပါ"
                    else "No orders matching criteria",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
            ) {
                items(filteredOrders, key = { it.orderId }) { order ->
                    AdminOrderCard(
                        order = order,
                        currentLanguage = currentLanguage,
                        onUpdateStatus = { status -> onUpdateOrderStatus(order.orderId, status) },
                        onDelete = { onDeleteOrder(order.orderId) },
                        onViewDetails = { onSelectOrderForDetail(order) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminOrderCard(
    order: OrderEntity,
    currentLanguage: Language,
    onUpdateStatus: (OrderStatus) -> Unit,
    onDelete: () -> Unit,
    onViewDetails: () -> Unit
) {
    val currentStatus = try {
        OrderStatus.valueOf(order.status)
    } catch (e: Exception) {
        OrderStatus.PLACED
    }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val formattedTime = remember(order.timestamp) { dateFormat.format(Date(order.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
            .testTag("admin_order_card_${order.orderId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: ID + Status + Delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "#${order.orderId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (currentStatus) {
                            OrderStatus.PLACED -> MaterialTheme.colorScheme.secondaryContainer
                            OrderStatus.PREPARING -> MaterialTheme.colorScheme.primaryContainer
                            OrderStatus.OUT_FOR_DELIVERY -> MaterialTheme.colorScheme.tertiaryContainer
                            OrderStatus.DELIVERED -> Color(0xFFE8F5E9)
                            OrderStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                        }
                    ) {
                        Text(
                            text = currentStatus.title(currentLanguage),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (currentStatus) {
                                OrderStatus.DELIVERED -> Color(0xFF2E7D32)
                                OrderStatus.CANCELLED -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete order",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Badges Row: Payment Status + Return Status (if any) + Tracking (if any)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Payment Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (order.paymentStatus) {
                        "PAID" -> Color(0xFFDCFCE7)
                        "REFUNDED" -> Color(0xFFF3E8FF)
                        else -> Color(0xFFFEF3C7)
                    }
                ) {
                    Text(
                        text = "💳 ${order.paymentStatus}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (order.paymentStatus) {
                            "PAID" -> Color(0xFF166534)
                            "REFUNDED" -> Color(0xFF6B21A8)
                            else -> Color(0xFF92400E)
                        }
                    )
                }

                // Return Status Badge if present
                if (order.returnStatus.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFEDD5)
                    ) {
                        Text(
                            text = "🔄 ${order.returnStatus}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9A3412)
                        )
                    }
                }

                // Tracking Badge if present
                if (order.trackingNumber.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE0E7FF)
                    ) {
                        Text(
                            text = "📦 ${order.trackingNumber}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3730A3)
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Customer Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${order.customerName} • 📞 ${order.customerPhone}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            if (order.deliveryAddressNote.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.deliveryAddressNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Items breakdown
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                }
            }

            // Total & Payment
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (currentLanguage == Language.BURMESE) "ငွေပေးချေမှု:" else "Payment:"} ${order.paymentMethod}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 8.dp)
                )
                Text(
                    text = Strings.mmkCurrency(currentLanguage, order.grandTotalMMK),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Button to View Full Transaction Details
            OutlinedButton(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth().testTag("view_order_details_${order.orderId}"),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (currentLanguage == Language.BURMESE) "အရောင်းအဝယ် အသေးစိတ် အပြည့်အစုံ ကြည့်မည်" else "View Complete Transaction Details",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status Management Action Buttons
            Text(
                text = Strings.updateOrderStatusTitle(currentLanguage),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (currentStatus == OrderStatus.PLACED) {
                    FilledTonalButton(
                        onClick = { onUpdateStatus(OrderStatus.PREPARING) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "အော်ဒါ ပြင်ဆင်မည်" else "Order Preparing",
                            fontSize = 11.sp
                        )
                    }
                }

                if (currentStatus == OrderStatus.PREPARING) {
                    FilledTonalButton(
                        onClick = { onUpdateStatus(OrderStatus.OUT_FOR_DELIVERY) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်" else "Dispatch",
                            fontSize = 11.sp
                        )
                    }
                }

                if (currentStatus == OrderStatus.OUT_FOR_DELIVERY) {
                    Button(
                        onClick = { onUpdateStatus(OrderStatus.DELIVERED) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ပို့ပြီးပါပြီ" else "Delivered",
                            fontSize = 11.sp
                        )
                    }
                }

                if (currentStatus != OrderStatus.DELIVERED && currentStatus != OrderStatus.CANCELLED) {
                    OutlinedButton(
                        onClick = { onUpdateStatus(OrderStatus.CANCELLED) },
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ပယ်ဖျက်" else "Cancel",
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}



// -------------------------------------------------------------------------
// Dialog: Create User Account
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateUserDialog(
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, role: String, townshipId: String, addressNote: String, password: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("CUSTOMER") }
    var selectedTownshipId by remember { mutableStateOf("ygn_kamayut") }
    var addressNote by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("1234") }

    var roleMenuExpanded by remember { mutableStateOf(false) }
    var townshipMenuExpanded by remember { mutableStateOf(false) }

    val townships = MyanmarTownshipsData.allTownships
    val selectedTownship = townships.find { it.id == selectedTownshipId } ?: MyanmarTownshipsData.defaultTownship

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Strings.createUserAccount(currentLanguage),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Strings.enterName(currentLanguage)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_new_user_name_input"),
                    singleLine = true
                )

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(Strings.enterPhone(currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_new_user_phone_input"),
                    singleLine = true
                )

                // Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = roleMenuExpanded,
                    onExpandedChange = { roleMenuExpanded = !roleMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = if (role == "ADMIN") "Administrator (စီမံခန့်ခွဲသူ)" else "Customer (ဝယ်ယူသူ)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(Strings.roleLabel(currentLanguage)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = roleMenuExpanded,
                        onDismissRequest = { roleMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Customer (ဝယ်ယူသူ)") },
                            onClick = {
                                role = "CUSTOMER"
                                roleMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Administrator (စီမံခန့်ခွဲသူ)") },
                            onClick = {
                                role = "ADMIN"
                                roleMenuExpanded = false
                            }
                        )
                    }
                }

                // Township Dropdown
                ExposedDropdownMenuBox(
                    expanded = townshipMenuExpanded,
                    onExpandedChange = { townshipMenuExpanded = !townshipMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedTownship.name(currentLanguage),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (currentLanguage == Language.BURMESE) "မြို့နယ်" else "Township") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = townshipMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = townshipMenuExpanded,
                        onDismissRequest = { townshipMenuExpanded = false }
                    ) {
                        townships.forEach { tsp ->
                            DropdownMenuItem(
                                text = { Text(tsp.name(currentLanguage)) },
                                onClick = {
                                    selectedTownshipId = tsp.id
                                    townshipMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Address Note
                OutlinedTextField(
                    value = addressNote,
                    onValueChange = { addressNote = it },
                    label = { Text(if (currentLanguage == Language.BURMESE) "လိပ်စာ အပြည့်အစုံ" else "Full Delivery Address") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(Strings.optionalPassword(currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(name, phone, role, selectedTownshipId, addressNote, password)
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                modifier = Modifier.testTag("submit_create_user_btn")
            ) {
                Text(Strings.createUserAccount(currentLanguage))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
            }
        }
    )
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.model.CartItem
import com.example.model.DeliveryType
import com.example.model.Language
import com.example.model.LogisticsConfig
import com.example.model.MyanmarTownship
import com.example.model.PaymentConfig
import com.example.model.PaymentMethod
import com.example.model.Strings
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBottomSheet(
    cartItems: List<CartItem>,
    customerName: String,
    customerPhone: String,
    selectedTownship: MyanmarTownship,
    deliveryAddressNote: String,
    selectedPaymentMethod: PaymentMethod,
    subtotalMMK: Int,
    deliveryFeeMMK: Int,
    grandTotalMMK: Int,
    currentLanguage: Language,
    kbzPayTimeRemainingSeconds: Int = 600,
    isKbzPayPaid: Boolean = false,
    paymentConfig: PaymentConfig = PaymentConfig(),
    logisticsConfig: LogisticsConfig = LogisticsConfig(),
    deliveryType: DeliveryType = DeliveryType.STANDARD,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onTownshipClick: () -> Unit,
    onAddressNoteChange: (String) -> Unit,
    onDeliveryTypeChange: (DeliveryType) -> Unit = {},
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    onOpenKbzPayQr: () -> Unit = {},
    onUpdateQuantity: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onPlaceOrder: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var validationError by remember { mutableStateOf<String?>(null) }
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sheet Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Strings.cart(currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${cartItems.sumOf { it.quantity }} items selected",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_cart_sheet_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            if (cartItems.isEmpty()) {
                // Empty Cart State
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🦐", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = Strings.emptyCartTitle(currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Strings.emptyCartSubtitle(currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Itemized Cart List with High Density Container
                item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "SELECTED ITEMS / မှာယူထားသော အစားအစာ" else "SELECTED ITEMS / မှာယူထားသော အစားအစာ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                cartItems.forEach { item ->
                                    CartItemRow(
                                        item = item,
                                        currentLanguage = currentLanguage,
                                        onQuantityChange = { newQty -> onUpdateQuantity(item, newQty) },
                                        onRemove = { onRemoveItem(item) }
                                    )
                                }
                            }
                        }
                    }

                    // Minimalist Customer Info Card (High Density Card Container)
                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "USER INFORMATION / အချက်အလက်" else "USER INFORMATION / အချက်အလက်",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Customer Name
                                OutlinedTextField(
                                    value = customerName,
                                    onValueChange = {
                                        onNameChange(it)
                                        validationError = null
                                    },
                                    label = { Text(if (currentLanguage == Language.BURMESE) "Name / အမည်" else "Name / အမည်", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("checkout_customer_name_input")
                                )

                                // Phone Number
                                OutlinedTextField(
                                    value = customerPhone,
                                    onValueChange = {
                                        onPhoneChange(it)
                                        validationError = null
                                    },
                                    label = { Text(if (currentLanguage == Language.BURMESE) "Phone / ဖုန်းနံပါတ်" else "Phone / ဖုန်းနံပါတ်", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                    placeholder = { Text("09xxxxxxxxx", fontSize = 12.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("checkout_customer_phone_input")
                                )

                                // Free Delivery Threshold Progress Banner
                                if (logisticsConfig.freeDeliveryEnabled && logisticsConfig.freeDeliveryThresholdMMK > 0) {
                                    val qualifies = subtotalMMK >= logisticsConfig.freeDeliveryThresholdMMK
                                    val remaining = (logisticsConfig.freeDeliveryThresholdMMK - subtotalMMK).coerceAtLeast(0)
                                    val progress = (subtotalMMK.toFloat() / logisticsConfig.freeDeliveryThresholdMMK).coerceIn(0f, 1f)

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (qualifies) Color(0xFFDCFCE7) else Color(0xFFEFF6FF),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = if (qualifies) Icons.Default.CheckCircle else Icons.Default.LocalShipping,
                                                        contentDescription = null,
                                                        tint = if (qualifies) Color(0xFF15803D) else Color(0xFF2563EB),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = if (qualifies) {
                                                            if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ခ အခမဲ့ ရရှိပါသည်!" else "Free Delivery Unlocked!"
                                                        } else {
                                                            if (currentLanguage == Language.BURMESE) "အခမဲ့ပို့ဆောင်ရန် ${numberFormat.format(remaining)} Ks လိုအပ်ပါသည်"
                                                            else "Add ${numberFormat.format(remaining)} MMK for Free Delivery"
                                                        },
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (qualifies) Color(0xFF15803D) else Color(0xFF1E40AF)
                                                    )
                                                }
                                                if (qualifies) {
                                                    Text(
                                                        text = "0 MMK",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color(0xFF15803D)
                                                    )
                                                }
                                            }
                                            if (!qualifies) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                LinearProgressIndicator(
                                                    progress = { progress },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(4.dp)
                                                        .clip(RoundedCornerShape(2.dp)),
                                                    color = Color(0xFF2563EB),
                                                    trackColor = Color(0xFFDBEAFE)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Delivery Option (Standard, Express Rush, Store Pickup)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    FilterChip(
                                        selected = deliveryType == DeliveryType.STANDARD,
                                        onClick = { onDeliveryTypeChange(DeliveryType.STANDARD) },
                                        label = {
                                            Text(
                                                text = if (currentLanguage == Language.BURMESE) "ပုံမှန်" else "Standard",
                                                fontSize = 11.sp
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (logisticsConfig.expressDeliveryEnabled) {
                                        FilterChip(
                                            selected = deliveryType == DeliveryType.EXPRESS,
                                            onClick = { onDeliveryTypeChange(DeliveryType.EXPRESS) },
                                            label = {
                                                Text(
                                                    text = if (currentLanguage == Language.BURMESE) "အမြန် (+${numberFormat.format(logisticsConfig.expressDeliveryFeeMMK)})"
                                                    else "Express (+${numberFormat.format(logisticsConfig.expressDeliveryFeeMMK)})",
                                                    fontSize = 10.sp
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                            },
                                            modifier = Modifier.weight(1.1f)
                                        )
                                    }

                                    if (logisticsConfig.selfPickupEnabled) {
                                        FilterChip(
                                            selected = deliveryType == DeliveryType.PICKUP,
                                            onClick = { onDeliveryTypeChange(DeliveryType.PICKUP) },
                                            label = {
                                                Text(
                                                    text = if (currentLanguage == Language.BURMESE) "ဆိုင်လာယူ" else "Pickup",
                                                    fontSize = 11.sp
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(14.dp))
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                // Township Shipping Selector
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onTownshipClick() }
                                        .testTag("checkout_township_selector_button")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = if (currentLanguage == Language.BURMESE) "Township / မြို့နယ်" else "Township / မြို့နယ်",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "${if (currentLanguage == Language.BURMESE) selectedTownship.nameMy else selectedTownship.nameEn} (${if (currentLanguage == Language.BURMESE) selectedTownship.regionMy else selectedTownship.regionEn})",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = Strings.mmkCurrency(currentLanguage, selectedTownship.deliveryFeeMMK),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.ArrowForwardIos,
                                                contentDescription = "Change",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }

                                // Optional street / unit note
                                OutlinedTextField(
                                    value = deliveryAddressNote,
                                    onValueChange = onAddressNoteChange,
                                    label = { Text(Strings.deliveryAddressNote(currentLanguage), fontSize = 12.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("checkout_address_note_input")
                                )
                            }
                        }
                    }

                    // Payment Method Toggle (COD / KPay / WavePay)
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = Strings.paymentMethod(currentLanguage).uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val codMin = paymentConfig.codMinOrderAmountMMK
                                val codMax = paymentConfig.codMaxOrderAmountMMK
                                val isCodMinSatisfied = codMin <= 0 || grandTotalMMK >= codMin
                                val isCodMaxSatisfied = codMax <= 0 || grandTotalMMK <= codMax
                                val isCodAvailable = paymentConfig.codEnabled && isCodMinSatisfied && isCodMaxSatisfied

                                // If COD is selected but no longer available, auto-switch to KPAY
                                LaunchedEffect(isCodAvailable, selectedPaymentMethod) {
                                    if (!isCodAvailable && selectedPaymentMethod == PaymentMethod.COD) {
                                        onPaymentMethodChange(PaymentMethod.KPAY)
                                    }
                                }

                                val codBadgeText = when {
                                    !paymentConfig.codEnabled -> "Off"
                                    !isCodMinSatisfied -> "≥ ${numberFormat.format(codMin)}"
                                    !isCodMaxSatisfied -> "≤ ${numberFormat.format(codMax)}"
                                    codMin > 0 -> "Unlocked"
                                    else -> null
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PaymentOptionPill(
                                        title = Strings.cod(currentLanguage),
                                        isSelected = selectedPaymentMethod == PaymentMethod.COD,
                                        isAvailable = isCodAvailable,
                                        badgeText = codBadgeText,
                                        onClick = {
                                            if (!isCodAvailable) {
                                                if (!paymentConfig.codEnabled) {
                                                    validationError = if (currentLanguage == Language.BURMESE) "ပစ္စည်းရောက်ငွေချေစနစ်ကို ယာယီပိတ်ထားပါသည်"
                                                    else "Cash on Delivery is currently disabled"
                                                } else if (!isCodMinSatisfied) {
                                                    val needed = codMin - grandTotalMMK
                                                    validationError = Strings.codLockedNote(currentLanguage, numberFormat.format(codMin), numberFormat.format(needed))
                                                } else if (!isCodMaxSatisfied) {
                                                    validationError = if (currentLanguage == Language.BURMESE) "ပစ္စည်းရောက်ငွေချေစနစ်ကို အများဆုံး ${numberFormat.format(codMax)} ကျပ်အထိသာ လက်ခံပါသည်"
                                                    else "Cash on Delivery is limited to max ${numberFormat.format(codMax)} MMK"
                                                }
                                            } else {
                                                validationError = null
                                                onPaymentMethodChange(PaymentMethod.COD)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    PaymentOptionPill(
                                        title = Strings.kpay(currentLanguage),
                                        isSelected = selectedPaymentMethod == PaymentMethod.KPAY,
                                        onClick = {
                                            validationError = null
                                            onPaymentMethodChange(PaymentMethod.KPAY)
                                            onOpenKbzPayQr()
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    PaymentOptionPill(
                                        title = Strings.wavepay(currentLanguage),
                                        isSelected = selectedPaymentMethod == PaymentMethod.WAVEPAY,
                                        onClick = {
                                            validationError = null
                                            onPaymentMethodChange(PaymentMethod.WAVEPAY)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Interactive COD Budget Threshold Guidance Banner
                                if (codMin > 0) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val neededToUnlock = (codMin - grandTotalMMK).coerceAtLeast(0)
                                    val progress = if (codMin > 0) (grandTotalMMK.toFloat() / codMin).coerceIn(0f, 1f) else 1f

                                    if (!isCodMinSatisfied) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Lock,
                                                            contentDescription = "COD Threshold",
                                                            tint = MaterialTheme.colorScheme.tertiary,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = if (currentLanguage == Language.BURMESE) "COD သတ်မှတ်ချက် (Threshold)" else "COD Budget Threshold",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.tertiary
                                                        )
                                                    }
                                                    Text(
                                                        text = "${(progress * 100).toInt()}%",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.tertiary
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(5.dp))
                                                LinearProgressIndicator(
                                                    progress = { progress },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(5.dp)
                                                        .clip(RoundedCornerShape(3.dp)),
                                                    color = MaterialTheme.colorScheme.tertiary,
                                                    trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = Strings.codLockedNote(
                                                        currentLanguage,
                                                        numberFormat.format(codMin),
                                                        numberFormat.format(neededToUnlock)
                                                    ),
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    lineHeight = 15.sp
                                                )
                                            }
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFF2E7D32).copy(alpha = 0.1f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2E7D32),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = Strings.codUnlockedNote(currentLanguage, numberFormat.format(codMin)),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF2E7D32)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Interactive KBZ Pay Active Banner when selected
                                if (selectedPaymentMethod == PaymentMethod.KPAY) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val minutes = kbzPayTimeRemainingSeconds / 60
                                    val seconds = kbzPayTimeRemainingSeconds % 60
                                    val formattedTime = String.format("%02d:%02d", minutes, seconds)

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF003874).copy(alpha = 0.08f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF003874).copy(alpha = 0.25f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { onOpenKbzPayQr() }
                                            .testTag("kpay_active_banner_cart")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = Color(0xFF003874),
                                                        modifier = Modifier.padding(end = 6.dp)
                                                    ) {
                                                        Text(
                                                            text = "KPay QR",
                                                            color = Color.White,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = if (isKbzPayPaid) "✓ Paid / ငွေလွှဲပြီး" else "⏰ $formattedTime remaining",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isKbzPayPaid) Color(0xFF2E7D32) else Color(0xFF003874)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "တိမ်တမန် / Daw Pwint San (09-789 456 123)",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFF003874),
                                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                            ) {
                                                Text(
                                                    text = if (currentLanguage == Language.BURMESE) "QR ကြည့်မည်" else "View QR",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Price Summary Bill
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = Strings.subtotal(currentLanguage),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = Strings.mmkCurrency(currentLanguage, subtotalMMK),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.DeliveryDining,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = Strings.deliveryFee(currentLanguage),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (deliveryFeeMMK == 0) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFDCFCE7)
                                        ) {
                                            Text(
                                                text = if (currentLanguage == Language.BURMESE) "အခမဲ့ (FREE)" else "FREE",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF15803D),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = Strings.mmkCurrency(currentLanguage, deliveryFeeMMK),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = Strings.grandTotal(currentLanguage),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = Strings.mmkCurrency(currentLanguage, grandTotalMMK),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // Notification Callout Banner
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeliveryDining,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "အော်ဒါအခြေအနေများကို Push Notification ဖြင့် ပေးပို့ပါမည်။" else "Status updates will be sent via push notification.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // Validation Error Notice
                    if (validationError != null) {
                        item {
                            Text(
                                text = validationError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Place Order Action Button (Rounded Full High Density)
                    item {
                        Button(
                            onClick = {
                                val outOfStockItem = cartItems.find { !it.foodItem.isAvailable }
                                val underMinQtyItem = cartItems.find { it.quantity < it.foodItem.minPurchaseQty }
                                val underMinAmtItem = cartItems.find { it.foodItem.minPurchaseAmountMMK > 0 && it.subtotalBeforeDiscount < it.foodItem.minPurchaseAmountMMK }
                                val invalidFulfillmentItem = cartItems.find { item ->
                                    (item.fulfillmentPreference.contains("Self", ignoreCase = true) && !item.foodItem.allowSelfPickup) ||
                                    (!item.fulfillmentPreference.contains("Self", ignoreCase = true) && !item.foodItem.allowDelivery)
                                }
                                val unsupportedRegionItem = cartItems.find { item ->
                                    val regEn = selectedTownship.regionEn
                                    val regMy = selectedTownship.regionMy
                                    !item.foodItem.isRegionAllowed(regEn) && !item.foodItem.isRegionAllowed(regMy)
                                }

                                if (customerName.isBlank() || customerPhone.isBlank()) {
                                    validationError = Strings.requiredFieldsPrompt(currentLanguage)
                                } else if (outOfStockItem != null) {
                                    val name = if (currentLanguage == Language.BURMESE) outOfStockItem.foodItem.nameMy else outOfStockItem.foodItem.nameEn
                                    validationError = if (currentLanguage == Language.BURMESE) "⚠️ '$name' သည် ပစ္စည်းပြတ်လပ်နေသဖြင့် ဖယ်ရှားပေးပါ" else "⚠️ '$name' is currently out of stock. Please remove it to proceed."
                                } else if (underMinQtyItem != null) {
                                    val name = if (currentLanguage == Language.BURMESE) underMinQtyItem.foodItem.nameMy else underMinQtyItem.foodItem.nameEn
                                    validationError = if (currentLanguage == Language.BURMESE) "⚠️ '$name' အတွက် အနည်းဆုံး ${underMinQtyItem.foodItem.minPurchaseQty} ခု ဝယ်ယူရပါမည်" else "⚠️ Minimum purchase quantity for '$name' is ${underMinQtyItem.foodItem.minPurchaseQty}"
                                } else if (underMinAmtItem != null) {
                                    val name = if (currentLanguage == Language.BURMESE) underMinAmtItem.foodItem.nameMy else underMinAmtItem.foodItem.nameEn
                                    validationError = if (currentLanguage == Language.BURMESE) "⚠️ '$name' အတွက် အနည်းဆုံး ကျသင့်ငွေမှာ ${Strings.mmkCurrency(currentLanguage, underMinAmtItem.foodItem.minPurchaseAmountMMK)} ဖြစ်ပါသည်" else "⚠️ Minimum purchase amount for '$name' is ${Strings.mmkCurrency(currentLanguage, underMinAmtItem.foodItem.minPurchaseAmountMMK)}"
                                } else if (invalidFulfillmentItem != null) {
                                    val name = if (currentLanguage == Language.BURMESE) invalidFulfillmentItem.foodItem.nameMy else invalidFulfillmentItem.foodItem.nameEn
                                    validationError = if (currentLanguage == Language.BURMESE) "⚠️ '$name' သည် ရွေးချယ်ထားသော ပို့ဆောင်မှုပုံစံဖြင့် မရရှိနိုင်ပါ" else "⚠️ '$name' is not available with the chosen fulfillment preference."
                                } else if (unsupportedRegionItem != null) {
                                    val name = if (currentLanguage == Language.BURMESE) unsupportedRegionItem.foodItem.nameMy else unsupportedRegionItem.foodItem.nameEn
                                    validationError = if (currentLanguage == Language.BURMESE) "⚠️ '$name' သည် ရွေးချယ်ထားသော မြို့နယ်/ဒေသသို့ ပို့ဆောင်ပေး၍မရပါ" else "⚠️ '$name' does not deliver to ${selectedTownship.nameEn}."
                                } else {
                                    onPlaceOrder()
                                }
                            },
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("place_order_button")
                        ) {
                            Text(
                                text = "${Strings.orderNow(currentLanguage).uppercase()} / မှာယူပါ • ${Strings.mmkCurrency(currentLanguage, grandTotalMMK)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Extra spacer for comfortable bottom scrolling above home indicator
                    item {
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }
        }
    }

@Composable
private fun CartItemRow(
    item: CartItem,
    currentLanguage: Language,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (item.foodItem.imageUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = item.foodItem.imageUrl,
                        contentDescription = item.foodItem.nameEn,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(text = item.foodItem.iconEmoji, fontSize = 22.sp)
                            }
                        },
                        error = {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(text = item.foodItem.iconEmoji, fontSize = 22.sp)
                            }
                        }
                    )
                } else {
                    Text(text = item.foodItem.iconEmoji, fontSize = 22.sp)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Brand and SKU
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.foodItem.brand,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                    Text(
                        text = "•",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        text = item.foodItem.effectiveSku,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1
                    )
                }

                Text(
                    text = if (currentLanguage == Language.BURMESE) item.foodItem.nameMy else item.foodItem.nameEn,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (item.specialNotes.isNotBlank()) {
                    Text(
                        text = "🏷️ ${item.specialNotes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (item.selectedAddOns.isNotEmpty() || item.selectedSpiceLevel.isNotEmpty()) {
                    val addOnsText = item.selectedAddOns.joinToString(", ") {
                        if (currentLanguage == Language.BURMESE) it.nameMy else it.nameEn
                    }
                    Text(
                        text = "${item.selectedSpiceLevel}${if (addOnsText.isNotBlank()) " • $addOnsText" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                // Status and fulfillment badges
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!item.foodItem.isAvailable) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "⚠️ ပစ္စည်းပြတ်" else "⚠️ Out of Stock",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (item.quantity < item.foodItem.minPurchaseQty) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "Min: ${item.foodItem.minPurchaseQty}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Fulfillment tag
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = if (item.fulfillmentPreference.contains("Self", ignoreCase = true)) "🏪 Pickup" else "🚚 Delivery",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    if (item.includeColdStorage) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE0F2FE)
                        ) {
                            Text(
                                text = "❄️ Ice Box",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0369A1),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (item.includeSpecialPrep) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "🔪 Prep",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.discountAmount > 0) {
                        Text(
                            text = Strings.mmkCurrency(currentLanguage, item.subtotalBeforeDiscount),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = Strings.mmkCurrency(currentLanguage, item.totalPrice),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (item.discountAmount > 0) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${if (currentLanguage == Language.BURMESE) item.foodItem.unitMy else item.foodItem.unitEn})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                if (item.discountAmount > 0) {
                    Text(
                        text = "🎉 Saved -${Strings.mmkCurrency(currentLanguage, item.discountAmount)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                }
            }

            // Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            ) {
                IconButton(
                    onClick = {
                        if (item.quantity > 1) {
                            onQuantityChange(item.quantity - 1)
                        } else {
                            onRemove()
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (item.quantity > 1) Icons.Default.Remove else Icons.Default.DeleteOutline,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(16.dp),
                        tint = if (item.quantity > 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                    )
                }
                Text(
                    text = "${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentOptionPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAvailable: Boolean = true,
    badgeText: String? = null
) {
    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        !isAvailable -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerColor,
        border = if (!isAvailable) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) else null,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (!isAvailable) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        modifier = Modifier.size(11.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        !isAvailable -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (badgeText != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isAvailable) Color(0xFF2E7D32).copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAvailable) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

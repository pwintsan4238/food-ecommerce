package com.example.ui.screens.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.data.OrderEntity
import com.example.model.Language
import com.example.model.PaymentConfig
import com.example.model.QrPaymentOption
import com.example.model.Strings
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminPaymentsSection(
    paymentConfig: PaymentConfig,
    orders: List<OrderEntity>,
    currentLanguage: Language,
    onUpdateConfig: (PaymentConfig) -> Unit,
    isAddingQr: Boolean = false,
    onDismissAddQr: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddOrEditDialog by remember { mutableStateOf(false) }
    var editingOption by remember { mutableStateOf<QrPaymentOption?>(null) }
    var optionToDelete by remember { mutableStateOf<QrPaymentOption?>(null) }
    var previewQrImage by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showCodThresholdDialog by remember { mutableStateOf(false) }

    // Synchronize with external FAB or trigger
    LaunchedEffect(isAddingQr) {
        if (isAddingQr) {
            editingOption = null
            showAddOrEditDialog = true
        }
    }

    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val totalRevenue = orders.sumOf { it.grandTotalMMK }
    val codOrdersCount = orders.count { it.paymentMethod.contains("CASH", ignoreCase = true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Payment Channels Header with prominent Add Plus button
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ငွေပေးချေမှု နည်းလမ်းများ" else "Payment Gateways",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "QR ငွေပေးချေမှုစနစ်များ ထည့်သွင်းခြင်းနှင့် ဖွင့်/ပိတ် စီမံပါ"
                                else "Manage payment options and add new QR payment gateways",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // PLUS BUTTON in Header
                        Button(
                            onClick = {
                                editingOption = null
                                showAddOrEditDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("admin_add_qr_payment_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add QR", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "QR အသစ်ထည့်" else "Add QR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section Title: QR Payment Options
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "QR ငွေပေးချေမှု နည်းလမ်းများ (${paymentConfig.qrPaymentOptions.size})"
                        else "QR Payment Methods (${paymentConfig.qrPaymentOptions.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = if (currentLanguage == Language.BURMESE) "အကောင့်များ" else "Gateways",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // List of all QR Payment Options (KBZPay, WavePay, AYA Pay, Custom, etc.)
        items(
            items = paymentConfig.qrPaymentOptions,
            key = { it.id }
        ) { qrOption ->
            val brandColor = parseHexColor(qrOption.colorHex)
            val ordersCount = orders.count {
                it.paymentMethod.contains(qrOption.name, ignoreCase = true) ||
                        (qrOption.name.contains("KBZ", ignoreCase = true) && it.paymentMethod.contains("kpay", ignoreCase = true)) ||
                        (qrOption.name.contains("Wave", ignoreCase = true) && it.paymentMethod.contains("wavepay", ignoreCase = true))
            }

            PaymentGatewayCard(
                title = qrOption.name,
                subtitle = "${qrOption.accountName} • ${qrOption.accountPhoneOrNo}",
                icon = Icons.Default.QrCode,
                iconTint = brandColor,
                imageUrl = qrOption.qrImageUrl.ifBlank { null },
                onImageClick = if (qrOption.qrImageUrl.isNotBlank()) {
                    { previewQrImage = Pair(qrOption.name, qrOption.qrImageUrl) }
                } else null,
                isEnabled = qrOption.isEnabled,
                onToggle = {
                    val updatedOptions = paymentConfig.qrPaymentOptions.map {
                        if (it.id == qrOption.id) it.copy(isEnabled = !it.isEnabled) else it
                    }
                    var updatedConfig = paymentConfig.copy(qrPaymentOptions = updatedOptions)
                    if (qrOption.name.contains("KBZ", ignoreCase = true) || qrOption.name.contains("KPay", ignoreCase = true)) {
                        updatedConfig = updatedConfig.copy(kbzPayEnabled = !qrOption.isEnabled)
                    }
                    onUpdateConfig(updatedConfig)
                },
                onEditClick = {
                    editingOption = qrOption
                    showAddOrEditDialog = true
                },
                onDeleteClick = if (paymentConfig.qrPaymentOptions.size > 1) {
                    { optionToDelete = qrOption }
                } else null,
                statsText = if (ordersCount > 0) {
                    if (currentLanguage == Language.BURMESE) "$ordersCount ကြိမ် ပေးချေခဲ့သည်" else "$ordersCount payments processed"
                } else if (qrOption.qrCodeNote.isNotBlank()) {
                    qrOption.qrCodeNote
                } else null
            )
        }

        // Add New QR Payment Option - Outlined Card Action
        item {
            OutlinedCard(
                onClick = {
                    editingOption = null
                    showAddOrEditDialog = true
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFFD32F2F).copy(alpha = 0.45f)),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color(0xFFD32F2F).copy(alpha = 0.04f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_add_new_qr_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFD32F2F).copy(alpha = 0.12f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add QR Option",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "+ QR ငွေပေးချေမှု နည်းလမ်းအသစ် ထည့်မည်" else "+ Add New QR Payment Option",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFD32F2F)
                        )
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "WavePay, AYA Pay, CB Pay သို့မဟုတ် စိတ်ကြိုက် QR ထည့်သွင်းပါ"
                            else "Add WavePay, AYA Pay, CB Pay, UAB Pay or custom QR",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section Title: Other Payment Channels
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (currentLanguage == Language.BURMESE) "အခြား ငွေပေးချေမှု နည်းလမ်းများ" else "Other Payment Methods",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Cash on Delivery (COD) Card
        item {
            val codThresholdText = when {
                paymentConfig.codMinOrderAmountMMK > 0 && paymentConfig.codMaxOrderAmountMMK > 0 ->
                    if (currentLanguage == Language.BURMESE)
                        "ကန့်သတ်ချက်: ${numberFormat.format(paymentConfig.codMinOrderAmountMMK)} ~ ${numberFormat.format(paymentConfig.codMaxOrderAmountMMK)} ကျပ်"
                    else
                        "Budget limit: ${numberFormat.format(paymentConfig.codMinOrderAmountMMK)} - ${numberFormat.format(paymentConfig.codMaxOrderAmountMMK)} MMK"
                paymentConfig.codMinOrderAmountMMK > 0 ->
                    if (currentLanguage == Language.BURMESE)
                        "ကန့်သတ်ချက်: အနည်းဆုံး ${numberFormat.format(paymentConfig.codMinOrderAmountMMK)} ကျပ်နှင့်အထက်"
                    else
                        "Budget limit: Min ${numberFormat.format(paymentConfig.codMinOrderAmountMMK)} MMK"
                paymentConfig.codMaxOrderAmountMMK > 0 ->
                    if (currentLanguage == Language.BURMESE)
                        "ကန့်သတ်ချက်: အများဆုံး ${numberFormat.format(paymentConfig.codMaxOrderAmountMMK)} ကျပ်အထိ"
                    else
                        "Budget limit: Max ${numberFormat.format(paymentConfig.codMaxOrderAmountMMK)} MMK"
                else ->
                    if (currentLanguage == Language.BURMESE)
                        "ကန့်သတ်ချက်မရှိ (အော်ဒါအားလုံး ရွေးချယ်နိုင်)"
                    else
                        "No budget limit (Available for all orders)"
            }

            PaymentGatewayCard(
                title = if (currentLanguage == Language.BURMESE) "ပစ္စည်းရောက်ငွေချေ (Cash on Delivery)" else "Cash on Delivery (COD)",
                subtitle = (if (currentLanguage == Language.BURMESE) "အိမ်အရောက် ငွေသားဖြင့် လက်ခံပေးချေခြင်း" else "Pay with cash directly to rider on arrival") + "\n• $codThresholdText",
                icon = Icons.Default.LocalAtm,
                iconTint = Color(0xFF059669),
                isEnabled = paymentConfig.codEnabled,
                onToggle = {
                    onUpdateConfig(paymentConfig.copy(codEnabled = !paymentConfig.codEnabled))
                },
                onEditClick = {
                    showCodThresholdDialog = true
                },
                statsText = if (currentLanguage == Language.BURMESE) "$codOrdersCount ကြိမ် ပေးချေခဲ့သည်" else "$codOrdersCount payments processed"
            )
        }

        // Dedicated Payment Budget Thresholds & Rules Management Card
        item {
            PaymentBudgetThresholdsCard(
                paymentConfig = paymentConfig,
                currentLanguage = currentLanguage,
                onConfigureClick = { showCodThresholdDialog = true },
                onQuickSetMin = { minAmount ->
                    onUpdateConfig(paymentConfig.copy(codMinOrderAmountMMK = minAmount))
                }
            )
        }

        // Mobile Banking Card
        item {
            PaymentGatewayCard(
                title = if (currentLanguage == Language.BURMESE) "မိုဘိုင်းဘဏ်ငွေလွှဲ (CB / AYA / KBZ Direct)" else "Direct Bank Transfer (CB / AYA / KBZ)",
                subtitle = if (currentLanguage == Language.BURMESE) "ဘဏ်အကောင့်သို့ တိုက်ရိုက် ငွေလွှဲခြင်း" else "Direct transfer to business bank accounts",
                icon = Icons.Default.AccountBalance,
                iconTint = Color(0xFFD97706),
                isEnabled = paymentConfig.mobileBankingEnabled,
                onToggle = {
                    onUpdateConfig(paymentConfig.copy(mobileBankingEnabled = !paymentConfig.mobileBankingEnabled))
                }
            )
        }

        // Revenue Breakdown Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "ငွေလက်ခံရရှိမှု အကျဉ်းချုပ်" else "Payment Collection Summary",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "စုစုပေါင်း ငွေပမာဏ" else "Total Collected",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${numberFormat.format(totalRevenue)} MMK",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFFD32F2F)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "အောင်မြင်သော အရောင်းအဝယ်" else "Successful Transactions",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${orders.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    optionToDelete?.let { option ->
        AlertDialog(
            onDismissRequest = { optionToDelete = null },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "QR ငွေပေးချေမှု နည်းလမ်း ဖျက်ရန်" else "Delete QR Payment Option",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = if (currentLanguage == Language.BURMESE)
                        "\"${option.name}\" QR နည်းလမ်းကို ဖျက်ပစ်ရန် သေချာပါသလား?"
                    else
                        "Are you sure you want to delete \"${option.name}\" QR payment option?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedOptions = paymentConfig.qrPaymentOptions.filterNot { it.id == option.id }
                        onUpdateConfig(paymentConfig.copy(qrPaymentOptions = updatedOptions))
                        optionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "ဖျက်မည်" else "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { optionToDelete = null }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }

    // Add / Edit QR Payment Option Dialog
    if (showAddOrEditDialog) {
        AddOrEditQrDialog(
            existingOption = editingOption,
            currentLanguage = currentLanguage,
            onDismiss = {
                showAddOrEditDialog = false
                editingOption = null
                onDismissAddQr()
            },
            onSave = { savedOption ->
                if (editingOption == null) {
                    // Adding new
                    val updatedList = paymentConfig.qrPaymentOptions + savedOption
                    var newConfig = paymentConfig.copy(qrPaymentOptions = updatedList)
                    if (savedOption.name.contains("KBZ", ignoreCase = true) || savedOption.name.contains("KPay", ignoreCase = true)) {
                        newConfig = newConfig.copy(
                            kbzPayPhone = savedOption.accountPhoneOrNo,
                            kbzPayAccountName = savedOption.accountName
                        )
                    }
                    onUpdateConfig(newConfig)
                } else {
                    // Updating existing
                    val updatedList = paymentConfig.qrPaymentOptions.map {
                        if (it.id == savedOption.id) savedOption else it
                    }
                    var newConfig = paymentConfig.copy(qrPaymentOptions = updatedList)
                    if (savedOption.name.contains("KBZ", ignoreCase = true) || savedOption.name.contains("KPay", ignoreCase = true)) {
                        newConfig = newConfig.copy(
                            kbzPayPhone = savedOption.accountPhoneOrNo,
                            kbzPayAccountName = savedOption.accountName
                        )
                    }
                    onUpdateConfig(newConfig)
                }
                showAddOrEditDialog = false
                editingOption = null
                onDismissAddQr()
            }
        )
    }

    // QR Image Full Screen Preview Dialog
    previewQrImage?.let { (title, url) ->
        Dialog(onDismissRequest = { previewQrImage = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { previewQrImage = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                        modifier = Modifier.size(240.dp)
                    ) {
                        SubcomposeAsyncImage(
                            model = url,
                            contentDescription = "$title QR Image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            loading = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                }
                            },
                            error = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူများ ငွေချေရာတွင် ဤ QR ကို တိုက်ရိုက် စကင်ဖတ်နိုင်ပါသည်"
                        else "Customers can scan this QR code directly during checkout",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // COD Budget Threshold Settings Dialog
    if (showCodThresholdDialog) {
        CodThresholdDialog(
            currentConfig = paymentConfig,
            currentLanguage = currentLanguage,
            onDismiss = { showCodThresholdDialog = false },
            onSave = { minAmount, maxAmount ->
                onUpdateConfig(
                    paymentConfig.copy(
                        codMinOrderAmountMMK = minAmount,
                        codMaxOrderAmountMMK = maxAmount
                    )
                )
                showCodThresholdDialog = false
            }
        )
    }
}

@Composable
private fun PaymentGatewayCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    statsText: String? = null,
    imageUrl: String? = null,
    onImageClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.5.dp, iconTint.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .then(
                                if (onImageClick != null) Modifier.clickable { onImageClick() }
                                else Modifier
                            )
                    ) {
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            contentDescription = "$title QR Image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = iconTint
                                    )
                                }
                            },
                            error = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = iconTint,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        )
                    }
                } else {
                    Surface(
                        shape = CircleShape,
                        color = iconTint.copy(alpha = 0.12f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!imageUrl.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = iconTint.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "QR 🖼️",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = iconTint,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (statsText != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = statsText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = iconTint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onEditClick != null) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Details",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (onDeleteClick != null) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Option",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFD32F2F),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD1D5DB)
                    )
                )
            }
        }
    }
}

private data class QrWalletPreset(
    val title: String,
    val name: String,
    val colorHex: String,
    val defaultNote: String
)

private val qrPresets = listOf(
    QrWalletPreset("KBZPay", "KBZPay (KPay QR)", "#003874", "Scan KPay QR or transfer to phone number"),
    QrWalletPreset("WavePay", "WavePay QR", "#F59E0B", "Scan WavePay QR or transfer to phone number"),
    QrWalletPreset("AYA Pay", "AYA Pay QR", "#DC2626", "Scan AYA Pay QR code with app"),
    QrWalletPreset("CB Pay", "CB Pay QR", "#0284C7", "Scan CB Pay QR code or transfer directly"),
    QrWalletPreset("UAB Pay", "UAB Pay QR", "#7C3AED", "Scan UAB Pay QR code with wallet"),
    QrWalletPreset("Custom", "Custom QR", "#059669", "Scan QR code to complete payment")
)

private val brandPalette = listOf(
    "#003874" to "KBZ Blue",
    "#F59E0B" to "Wave Amber",
    "#DC2626" to "AYA Red",
    "#0284C7" to "CB Cyan",
    "#059669" to "Emerald Green",
    "#7C3AED" to "UAB Purple",
    "#1E293B" to "Dark Slate"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddOrEditQrDialog(
    existingOption: QrPaymentOption?,
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onSave: (QrPaymentOption) -> Unit
) {
    val isEditing = existingOption != null
    val context = LocalContext.current

    var nameInput by remember { mutableStateOf(existingOption?.name ?: "") }
    var accountNameInput by remember {
        mutableStateOf(existingOption?.accountName ?: "TAIM TA MAN SEAFOOD CO., LTD.")
    }
    var phoneInput by remember { mutableStateOf(existingOption?.accountPhoneOrNo ?: "") }
    var noteInput by remember {
        mutableStateOf(existingOption?.qrCodeNote ?: "Scan QR with app or transfer to phone number")
    }
    var qrImageUrlInput by remember { mutableStateOf(existingOption?.qrImageUrl ?: "") }
    var selectedColorHex by remember { mutableStateOf(existingOption?.colorHex ?: "#003874") }
    var minAmountInput by remember {
        mutableStateOf(if ((existingOption?.minOrderAmountMMK ?: 0) > 0) existingOption!!.minOrderAmountMMK.toString() else "")
    }
    var maxAmountInput by remember {
        mutableStateOf(if ((existingOption?.maxOrderAmountMMK ?: 0) > 0) existingOption!!.maxOrderAmountMMK.toString() else "")
    }
    var isEnabled by remember { mutableStateOf(existingOption?.isEnabled ?: true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveQrImageToInternalStorage(context, uri)
            if (!savedPath.isNullOrBlank()) {
                qrImageUrlInput = savedPath
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD32F2F).copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEditing) {
                                if (currentLanguage == Language.BURMESE) "QR ငွေပေးချေမှု ပြင်ဆင်ရန်" else "Edit QR Payment Option"
                            } else {
                                if (currentLanguage == Language.BURMESE) "QR ငွေပေးချေမှု အသစ်ထည့်ရန်" else "Add New QR Payment Option"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider()

                // Quick Presets Row (Only when adding, or to quickly switch preset)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အမြန် ရွေးချယ်နိုင်သော စနစ်များ" else "Quick Myanmar Wallet Presets",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        qrPresets.forEach { preset ->
                            val isSelected = nameInput.contains(preset.title, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    nameInput = preset.name
                                    selectedColorHex = preset.colorHex
                                    if (noteInput.isBlank() || noteInput == "Scan QR with app or transfer to phone number") {
                                        noteInput = preset.defaultNote
                                    }
                                    errorMessage = null
                                },
                                label = { Text(preset.title, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = parseHexColor(preset.colorHex),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // 1. Payment Name
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = {
                        nameInput = it
                        errorMessage = null
                    },
                    label = {
                        Text(
                            if (currentLanguage == Language.BURMESE) "QR ငွေပေးချေမှု အမည် (ဥပမာ WavePay QR, AYA Pay)"
                            else "QR Gateway Name (e.g. WavePay QR, AYA Pay)"
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_qr_name_input")
                )

                // 2. Merchant Account Name
                OutlinedTextField(
                    value = accountNameInput,
                    onValueChange = {
                        accountNameInput = it
                        errorMessage = null
                    },
                    label = {
                        Text(
                            if (currentLanguage == Language.BURMESE) "အကောင့်ပိုင်ရှင် အမည် (Merchant Account Name)"
                            else "Merchant Account Name"
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_qr_account_name_input")
                )

                // 3. Phone / Account Number
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = {
                        phoneInput = it
                        errorMessage = null
                    },
                    label = {
                        Text(
                            if (currentLanguage == Language.BURMESE) "ဖုန်းနံပါတ် သို့မဟုတ် အကောင့်နံပါတ်"
                            else "Phone Number or Account Number"
                        )
                    },
                    placeholder = { Text("09-xxxxxxxxx") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_qr_phone_input")
                )

                // 4. Instructions / Note
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = {
                        Text(
                            if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူများအတွက် ညွှန်ကြားချက် / မှတ်ချက်"
                            else "Instructions / Note for Customers"
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_qr_note_input"),
                    maxLines = 2
                )

                // 5. Upload QR Image Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = parseHexColor(selectedColorHex),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Strings.uploadQrImage(currentLanguage),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (qrImageUrlInput.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2E7D32).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "✓ " + Strings.qrImageAttached(currentLanguage),
                                    fontSize = 10.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = if (currentLanguage == Language.BURMESE)
                            "KBZPay, WavePay သို့မဟုတ် ဘဏ် QR ဓာတ်ပုံကို တင်ထားပါက ဝယ်ယူသူများ တိုက်ရိုက် စကင်ဖတ် ငွေချေနိုင်ပါသည်"
                        else
                            "Upload your KBZPay, WavePay or Bank QR code image. Customers can scan it directly during checkout.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (qrImageUrlInput.isNotBlank()) {
                        // Image Preview Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, parseHexColor(selectedColorHex).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                modifier = Modifier.size(76.dp)
                            ) {
                                SubcomposeAsyncImage(
                                    model = qrImageUrlInput,
                                    contentDescription = "Uploaded QR Preview",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White),
                                    loading = {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                        }
                                    },
                                    error = {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.Gray)
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (qrImageUrlInput.startsWith("/")) "📁 Local File" else "🌐 Web Image",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = qrImageUrlInput.substringAfterLast("/").take(24),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(Strings.changeQrImage(currentLanguage), fontSize = 11.sp)
                                    }

                                    IconButton(
                                        onClick = { qrImageUrlInput = "" },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = Strings.removeQrImage(currentLanguage),
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Upload Button
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = parseHexColor(selectedColorHex)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_upload_qr_button")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Strings.chooseQrFromGallery(currentLanguage),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 5. Brand Color Picker
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အမှတ်တံဆိပ် အရောင် ရွေးချယ်ပါ" else "Brand Accent Color",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        brandPalette.forEach { (hex, _) ->
                            val color = parseHexColor(hex)
                            val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColorHex = hex }
                                    .then(
                                        if (isSelected) {
                                            Modifier.border(2.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        } else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. Active Status Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ချက်ချင်း အသုံးပြုမည်" else "Enable Immediately",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူများ ငွေချေရာတွင် ပြသပါမည်"
                            else "Make visible at customer checkout",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFD32F2F)
                        )
                    )
                }

                // Error Message if any
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val trimmedName = nameInput.trim()
                            val trimmedAccountName = accountNameInput.trim()
                            val trimmedPhone = phoneInput.trim()

                            if (trimmedName.isBlank()) {
                                errorMessage = if (currentLanguage == Language.BURMESE) "ကျေးဇူးပြု၍ QR ငွေပေးချေမှု အမည် ထည့်ပါ"
                                else "Please enter QR payment name"
                                return@Button
                            }
                            if (trimmedAccountName.isBlank()) {
                                errorMessage = if (currentLanguage == Language.BURMESE) "ကျေးဇူးပြု၍ အကောင့်ပိုင်ရှင် အမည် ထည့်ပါ"
                                else "Please enter account name"
                                return@Button
                            }
                            if (trimmedPhone.isBlank()) {
                                errorMessage = if (currentLanguage == Language.BURMESE) "ကျေးဇူးပြု၍ ဖုန်း သို့မဟုတ် အကောင့်နံပါတ် ထည့်ပါ"
                                else "Please enter phone or account number"
                                return@Button
                            }

                            val resultOption = QrPaymentOption(
                                id = existingOption?.id ?: java.util.UUID.randomUUID().toString(),
                                name = trimmedName,
                                accountName = trimmedAccountName,
                                accountPhoneOrNo = trimmedPhone,
                                isEnabled = isEnabled,
                                qrCodeNote = noteInput.trim(),
                                qrImageUrl = qrImageUrlInput.trim(),
                                colorHex = selectedColorHex,
                                minOrderAmountMMK = minAmountInput.toIntOrNull() ?: 0,
                                maxOrderAmountMMK = maxAmountInput.toIntOrNull() ?: 0
                            )
                            onSave(resultOption)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("admin_save_qr_payment_btn")
                    ) {
                        Text(
                            text = if (isEditing) {
                                if (currentLanguage == Language.BURMESE) "သိမ်းဆည်းမည်" else "Save Changes"
                            } else {
                                if (currentLanguage == Language.BURMESE) "QR ထည့်သွင်းမည်" else "Add QR Payment"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun parseHexColor(hex: String, fallback: Color = Color(0xFF003874)): Color {
    return try {
        val clean = hex.removePrefix("#").trim()
        when (clean.length) {
            6 -> Color(android.graphics.Color.parseColor("#$clean"))
            8 -> Color(android.graphics.Color.parseColor("#$clean"))
            else -> fallback
        }
    } catch (e: Exception) {
        fallback
    }
}

private fun saveQrImageToInternalStorage(context: Context, sourceUri: Uri): String? {
    return try {
        val qrDir = File(context.filesDir, "qr_codes")
        if (!qrDir.exists()) {
            qrDir.mkdirs()
        }
        val targetFile = File(qrDir, "qr_${System.currentTimeMillis()}.png")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        targetFile.absolutePath
    } catch (e: Exception) {
        sourceUri.toString()
    }
}

@Composable
private fun PaymentBudgetThresholdsCard(
    paymentConfig: PaymentConfig,
    currentLanguage: Language,
    onConfigureClick: () -> Unit,
    onQuickSetMin: (Int) -> Unit
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
    val codMin = paymentConfig.codMinOrderAmountMMK
    val codMax = paymentConfig.codMaxOrderAmountMMK
    val isThresholdActive = codMin > 0 || codMax > 0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0284C7).copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.paymentBudgetThresholds(currentLanguage),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ငွေပမာဏအလိုက် ငွေချေစနစ် ခွင့်ပြုခြင်း"
                            else "Order budget limits & qualification rules",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                FilledTonalButton(
                    onClick = onConfigureClick,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "ပြင်ဆင်ရန်" else "Configure",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Rule Highlight
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isThresholdActive) Color(0xFF059669).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, if (isThresholdActive) Color(0xFF059669).copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isThresholdActive) "🎯" else "ℹ️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "လက်ရှိ COD စည်းမျဉ်း:" else "Active COD Rule:",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isThresholdActive) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = when {
                                codMin > 0 && codMax > 0 ->
                                    if (currentLanguage == Language.BURMESE)
                                        "${numberFormat.format(codMin)} ~ ${numberFormat.format(codMax)} ကျပ်အတွင်းမှသာ COD ရွေးချယ်နိုင်သည်"
                                    else
                                        "Only orders between ${numberFormat.format(codMin)} and ${numberFormat.format(codMax)} MMK qualify for COD"
                                codMin > 0 ->
                                    if (currentLanguage == Language.BURMESE)
                                        "${numberFormat.format(codMin)} ကျပ်နှင့်အထက် ဝယ်ယူမှသာ COD ရွေးချယ်နိုင်သည်"
                                    else
                                        "Orders over ${numberFormat.format(codMin)} MMK qualify for Cash on Delivery"
                                else ->
                                    if (currentLanguage == Language.BURMESE)
                                        "ကန့်သတ်မထားပါ (အော်ဒါအားလုံး COD ရွေးချယ်နိုင်သည်)"
                                    else
                                        "No threshold (COD is available for all orders)"
                            },
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Inline Editable Number Option
            var customCodMinInput by remember(paymentConfig.codMinOrderAmountMMK) {
                mutableStateOf(if (paymentConfig.codMinOrderAmountMMK > 0) paymentConfig.codMinOrderAmountMMK.toString() else "0")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customCodMinInput,
                    onValueChange = { customCodMinInput = it.filter { ch -> ch.isDigit() } },
                    label = {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "COD အနည်းဆုံးငွေ (ကျပ်) ရိုက်ထည့်ပါ" else "Custom COD Min Budget (MMK)",
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_cod_min_order_input"),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    suffix = { Text("MMK", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )

                Button(
                    onClick = {
                        val parsed = customCodMinInput.toIntOrNull() ?: 0
                        onQuickSetMin(parsed)
                    },
                    enabled = customCodMinInput.toIntOrNull() != null && customCodMinInput.toIntOrNull() != paymentConfig.codMinOrderAmountMMK,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("admin_apply_cod_min_button")
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Apply")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick 1-Tap Presets
            Text(
                text = if (currentLanguage == Language.BURMESE) "အမြန်ရွေးချယ်ရန် ကန့်သတ်ချက်များ (Quick Presets):" else "Quick 1-Tap COD Presets:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val presets = listOf(
                    0 to if (currentLanguage == Language.BURMESE) "ကန့်သတ်မရှိ (0)" else "No Min",
                    30_000 to "30,000 Ks",
                    50_000 to "50,000 Ks",
                    100_000 to "⭐ 100,000 Ks",
                    150_000 to "150,000 Ks",
                    200_000 to "200,000 Ks"
                )
                presets.forEach { (presetAmount, label) ->
                    val isSelected = paymentConfig.codMinOrderAmountMMK == presetAmount
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            customCodMinInput = presetAmount.toString()
                            onQuickSetMin(presetAmount)
                        },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun CodThresholdDialog(
    currentConfig: PaymentConfig,
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onSave: (minAmountMMK: Int, maxAmountMMK: Int) -> Unit
) {
    var minAmountText by remember {
        mutableStateOf(if (currentConfig.codMinOrderAmountMMK > 0) currentConfig.codMinOrderAmountMMK.toString() else "")
    }
    var maxAmountText by remember {
        mutableStateOf(if (currentConfig.codMaxOrderAmountMMK > 0) currentConfig.codMaxOrderAmountMMK.toString() else "")
    }

    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF059669).copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Strings.codThresholdTitle(currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ငွေပမာဏ ကန့်သတ်ချက်များ သတ်မှတ်မည်"
                                else "Set budget limit threshold rules",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Explanatory tip card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "💡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.codThresholdDesc(currentLanguage),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Minimum Order Amount Required (Min Budget)
                Text(
                    text = Strings.minOrderRequired(currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = Strings.codMinThresholdHelp(currentLanguage),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = minAmountText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) minAmountText = input
                    },
                    label = { Text(if (currentLanguage == Language.BURMESE) "အနည်းဆုံး မှာယူရမည့်ငွေ (MMK)" else "Minimum Order Amount (MMK)") },
                    placeholder = { Text("0 = No minimum (e.g. 100000)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        val parsed = minAmountText.toIntOrNull() ?: 0
                        if (parsed > 0) {
                            Text(
                                text = "${numberFormat.format(parsed)} Ks",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669),
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick presets for Minimum
                Text(
                    text = if (currentLanguage == Language.BURMESE) "အမြန်ရွေးချယ်ရန်:" else "Quick Presets:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val minPresets = listOf(
                        0 to if (currentLanguage == Language.BURMESE) "ကန့်သတ်မရှိ (0)" else "No Min (0)",
                        30_000 to "30,000 Ks",
                        50_000 to "50,000 Ks",
                        100_000 to "⭐ 100,000 Ks",
                        150_000 to "150,000 Ks",
                        200_000 to "200,000 Ks"
                    )
                    minPresets.forEach { (amount, label) ->
                        val isSelected = (minAmountText.toIntOrNull() ?: 0) == amount
                        FilterChip(
                            selected = isSelected,
                            onClick = { minAmountText = if (amount == 0) "" else amount.toString() },
                            label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Maximum Order Amount (Safety Cap)
                Text(
                    text = Strings.maxOrderAllowed(currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (currentLanguage == Language.BURMESE) "ဤပမာဏထက်ကျော်လွန်ပါက ယာဉ်မောင်းလုံခြုံရေးအတွက် COD ပိတ်ထားမည် (0 = အကန့်အသတ်မရှိ)"
                    else "Over this amount, COD is disabled for driver safety (0 = Unlimited)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = maxAmountText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) maxAmountText = input
                    },
                    label = { Text(if (currentLanguage == Language.BURMESE) "အများဆုံး လက်ခံမည့်ငွေ (MMK)" else "Maximum Order Cap (MMK)") },
                    placeholder = { Text("0 = No maximum limit") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        val parsed = maxAmountText.toIntOrNull() ?: 0
                        if (parsed > 0) {
                            Text(
                                text = "${numberFormat.format(parsed)} Ks",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick presets for Maximum
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val maxPresets = listOf(
                        0 to if (currentLanguage == Language.BURMESE) "ကန့်သတ်မရှိ (No Max)" else "No Max",
                        200_000 to "200,000 Ks",
                        300_000 to "300,000 Ks",
                        500_000 to "500,000 Ks",
                        1_000_000 to "1,000,000 Ks"
                    )
                    maxPresets.forEach { (amount, label) ->
                        val isSelected = (maxAmountText.toIntOrNull() ?: 0) == amount
                        FilterChip(
                            selected = isSelected,
                            onClick = { maxAmountText = if (amount == 0) "" else amount.toString() },
                            label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Real-time Preview Banner
                val currentMin = minAmountText.toIntOrNull() ?: 0
                val currentMax = maxAmountText.toIntOrNull() ?: 0
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "🔍 သတ်မှတ်ချက် အကျိုးသက်ရောက်မှု ကြိုတင်ကြည့်ရှုခြင်း:"
                            else "🔍 Live Customer Experience Preview:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val ruleText = when {
                            currentMin > 0 && currentMax > 0 ->
                                if (currentLanguage == Language.BURMESE)
                                    "ဝယ်ယူသူများသည် ${numberFormat.format(currentMin)} ကျပ် မှ ${numberFormat.format(currentMax)} ကျပ်အတွင်း အော်ဒါများအတွက်သာ ပစ္စည်းရောက်ငွေချေ (COD) ကို ရွေးချယ်ခွင့်ရပါမည်။"
                                else
                                    "Customers can select Cash on Delivery only for orders between ${numberFormat.format(currentMin)} MMK and ${numberFormat.format(currentMax)} MMK."
                            currentMin > 0 ->
                                if (currentLanguage == Language.BURMESE)
                                    "ဝယ်ယူသူများသည် ${numberFormat.format(currentMin)} ကျပ်နှင့်အထက် ဝယ်ယူမှသာ ပစ္စည်းရောက်ငွေချေ (COD) ကို ရွေးချယ်ခွင့်ရပါမည်။"
                                else
                                    "Customers can select Cash on Delivery only if their order is ${numberFormat.format(currentMin)} MMK or more."
                            currentMax > 0 ->
                                if (currentLanguage == Language.BURMESE)
                                    "ဝယ်ယူသူများသည် ${numberFormat.format(currentMax)} ကျပ်အထိသာ ပစ္စည်းရောက်ငွေချေ (COD) ရွေးချယ်နိုင်ပါမည်။"
                                else
                                    "Cash on Delivery will be capped at ${numberFormat.format(currentMax)} MMK."
                            else ->
                                if (currentLanguage == Language.BURMESE)
                                    "အော်ဒါငွေပမာဏ မည်မျှဖြစ်စေ ပစ္စည်းရောက်ငွေချေ (COD) ကို အကန့်အသတ်မရှိ ရွေးချယ်နိုင်ပါမည်။"
                                else
                                    "Cash on Delivery will be available for all orders regardless of budget."
                        }
                        Text(
                            text = ruleText,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(Strings.cancel(currentLanguage))
                    }
                    Button(
                        onClick = {
                            val minVal = minAmountText.toIntOrNull() ?: 0
                            val maxVal = maxAmountText.toIntOrNull() ?: 0
                            onSave(minVal, maxVal)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "သတ်မှတ်ချက် သိမ်းမည်" else "Save Threshold",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

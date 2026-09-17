package com.example.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.model.Language
import com.example.model.QrPaymentOption
import com.example.model.Strings
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KbzPayDialog(
    amountMMK: Int,
    orderId: String? = null,
    timeRemainingSeconds: Int = 600, // 10 minutes default
    isPaid: Boolean = false,
    currentLanguage: Language,
    qrOptions: List<QrPaymentOption> = emptyList(),
    onConfirmPaid: () -> Unit,
    onSimulateTimerExpiry: () -> Unit,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedLabel by remember { mutableStateOf<String?>(null) }
    var showEnlargedQr by remember { mutableStateOf(false) }
    var paymentSlipUri by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val slipPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveSlipImageToInternalStorage(context, uri)
            if (!savedPath.isNullOrBlank()) {
                paymentSlipUri = savedPath
            }
        }
    }

    val activeQrOptions = remember(qrOptions) { qrOptions.filter { it.isEnabled } }
    var selectedQrOptionId by remember(activeQrOptions) {
        mutableStateOf(activeQrOptions.firstOrNull()?.id)
    }
    val currentSelectedOption = activeQrOptions.find { it.id == selectedQrOptionId }

    val kbzBlue = currentSelectedOption?.let {
        try {
            Color(android.graphics.Color.parseColor(it.colorHex))
        } catch (e: Exception) {
            Color(0xFF003874)
        }
    } ?: Color(0xFF003874)
    val kbzCyan = Color(0xFF00A3E0)

    val gatewayName = currentSelectedOption?.name ?: Strings.kbzPayTitle(currentLanguage)
    val accountName = currentSelectedOption?.accountName ?: Strings.kbzAccountName(currentLanguage)
    val accountNumber = currentSelectedOption?.accountPhoneOrNo ?: Strings.kbzAccountNumber()
    val transferNote = currentSelectedOption?.qrCodeNote?.ifBlank { null } ?: (orderId?.let { "Order #$it" } ?: "Food Order")
    val badgeText = remember(gatewayName) {
        when {
            gatewayName.contains("KBZ", ignoreCase = true) || gatewayName.contains("KPay", ignoreCase = true) -> "KPay"
            gatewayName.contains("Wave", ignoreCase = true) -> "Wave"
            gatewayName.contains("AYA", ignoreCase = true) -> "AYA"
            gatewayName.contains("CB", ignoreCase = true) -> "CB"
            gatewayName.contains("UAB", ignoreCase = true) -> "UAB"
            else -> "QR"
        }
    }

    // Format minutes and seconds
    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    val timerProgress = (timeRemainingSeconds.toFloat() / 600f).coerceIn(0f, 1f)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("kbz_pay_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // KBZ Pay Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(kbzBlue)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // QR Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = badgeText,
                                        color = kbzBlue,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = gatewayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Official Merchant QR Payment",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .testTag("close_kbz_pay_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // If multiple active QR options are available, show switcher chips
                if (activeQrOptions.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        activeQrOptions.forEach { opt ->
                            val isSelected = opt.id == (currentSelectedOption?.id ?: activeQrOptions.first().id)
                            val optColor = try {
                                Color(android.graphics.Color.parseColor(opt.colorHex))
                            } catch (e: Exception) {
                                Color(0xFF003874)
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedQrOptionId = opt.id },
                                label = { Text(opt.name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = optColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 10-Minute Countdown Timer Banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (timeRemainingSeconds <= 120) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                        else kbzCyan.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (timeRemainingSeconds <= 120) MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                            else kbzCyan.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = if (timeRemainingSeconds <= 120) MaterialTheme.colorScheme.error else kbzBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = Strings.paymentTimerRemaining(currentLanguage),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (timeRemainingSeconds <= 120) MaterialTheme.colorScheme.error else kbzBlue
                                    )
                                }

                                Text(
                                    text = formattedTime,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = if (timeRemainingSeconds <= 120) MaterialTheme.colorScheme.error else kbzBlue,
                                    modifier = Modifier.testTag("kbz_timer_display")
                                )
                            }

                            LinearProgressIndicator(
                                progress = { timerProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = if (timeRemainingSeconds <= 120) MaterialTheme.colorScheme.error else kbzBlue,
                                trackColor = Color.LightGray.copy(alpha = 0.3f)
                            )

                            Text(
                                text = Strings.tenMinuteWarning(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // QR Code Scan Container
                    val hasUploadedQrImage = !currentSelectedOption?.qrImageUrl.isNullOrBlank()
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(2.dp, kbzBlue.copy(alpha = 0.2f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .size(210.dp)
                            .testTag("kbz_qr_scan_box")
                            .then(
                                if (hasUploadedQrImage) {
                                    Modifier.clickable { showEnlargedQr = true }
                                } else Modifier
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (hasUploadedQrImage) {
                                SubcomposeAsyncImage(
                                    model = currentSelectedOption?.qrImageUrl,
                                    contentDescription = "$gatewayName QR Code",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    loading = {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(28.dp),
                                                strokeWidth = 2.5.dp,
                                                color = kbzBlue
                                            )
                                        }
                                    },
                                    error = {
                                        // Fallback to Vector QR Canvas
                                        KbzQrCodeCanvas(modifier = Modifier.size(150.dp), qrColor = kbzBlue)
                                    }
                                )
                            } else {
                                // Authentic Vector QR Matrix Drawing
                                KbzQrCodeCanvas(modifier = Modifier.size(150.dp), qrColor = kbzBlue)
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (hasUploadedQrImage) Icons.Default.ZoomIn else Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = kbzBlue,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (hasUploadedQrImage) {
                                        if (currentLanguage == Language.BURMESE) "ချဲ့ကြည့်ရန်နှိပ်ပါ • $gatewayName QR"
                                        else "Tap to zoom • $gatewayName QR"
                                    } else "Scan with $gatewayName",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = kbzBlue
                                )
                            }
                        }
                    }

                    // Copied Feedback Banner
                    AnimatedVisibility(
                        visible = copiedLabel != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${copiedLabel ?: ""} ${Strings.copiedToClipboard(currentLanguage)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // Account Information & Notes Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 1. Account Name
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Strings.kbzAccNameLabel(currentLanguage),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = accountName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(accountName))
                                        copiedLabel = Strings.kbzAccNameLabel(currentLanguage)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Account Name",
                                        tint = kbzBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // 2. KBZ Pay Phone Number
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Strings.kbzPayNumberLabel(currentLanguage),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = accountNumber,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = kbzBlue
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = kbzBlue,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(accountNumber.replace("-", "").replace(" ", "")))
                                            copiedLabel = Strings.kbzPayNumberLabel(currentLanguage)
                                        }
                                        .testTag("copy_kpay_number_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (currentLanguage == Language.BURMESE) "ကူးယူရန်" else "Copy",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // 3. Transfer Amount
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "လွှဲရမည့် ငွေပမာဏ" else "Amount to Transfer",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = Strings.mmkCurrency(currentLanguage, amountMMK),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString("$amountMMK"))
                                        copiedLabel = "Amount"
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Amount",
                                        tint = kbzBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // 4. Transfer Note / Reference
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Strings.kbzTransferNoteLabel(currentLanguage),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = transferNote,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(transferNote))
                                        copiedLabel = Strings.kbzTransferNoteLabel(currentLanguage)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Note",
                                        tint = kbzBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Customer Payment Slip / Receipt Screenshot Upload
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = kbzBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = Strings.uploadPaymentSlip(currentLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (paymentSlipUri != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                        modifier = Modifier.size(60.dp)
                                    ) {
                                        SubcomposeAsyncImage(
                                            model = paymentSlipUri,
                                            contentDescription = "Uploaded Payment Slip",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize(),
                                            loading = {
                                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                                }
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF2E7D32).copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = Strings.paymentSlipAttached(currentLanguage),
                                                fontSize = 10.sp,
                                                color = Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = paymentSlipUri!!.substringAfterLast("/").take(20),
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(
                                                onClick = {
                                                    slipPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(Strings.changePhoto(currentLanguage), fontSize = 10.sp)
                                            }
                                            IconButton(
                                                onClick = { paymentSlipUri = null },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.DeleteOutline,
                                                    contentDescription = Strings.removePhoto(currentLanguage),
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        slipPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = kbzBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("upload_payment_slip_button")
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = Strings.chooseSlipFromGallery(currentLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = if (currentLanguage == Language.BURMESE)
                                        "ငွေလွှဲပြေစာ စခရင်ရှော့ ဓာတ်ပုံကို တိုက်ရိုက် တင်နိုင်ပါသည်။ (အတည်ပြု မြန်ဆန်စေပါသည်)"
                                    else
                                        "Upload your payment receipt screenshot directly from gallery for faster verification.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Test Reminder Trigger Quick Button
                    OutlinedButton(
                        onClick = onSimulateTimerExpiry,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_payment_reminder_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "၁၀ မိနစ်ပြည့် သတိပေးချက် စမ်းသပ်မည်" else "Test 10-Min Payment Reminder Noti",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    // Primary Action: "I Have Paid / ငွေလွှဲပြီးပါပြီ"
                    Button(
                        onClick = {
                            onConfirmPaid()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPaid) Color(0xFF2E7D32) else kbzBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_kbz_payment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPaid) {
                                if (currentLanguage == Language.BURMESE) "ငွေပေးချေပြီးပါပြီ ✓" else "Payment Confirmed ✓"
                            } else {
                                Strings.iHavePaid(currentLanguage)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Fullscreen / Zoomed QR Preview for scanning convenience
    if (showEnlargedQr && !currentSelectedOption?.qrImageUrl.isNullOrBlank()) {
        Dialog(onDismissRequest = { showEnlargedQr = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                            text = "$gatewayName QR Code",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = kbzBlue
                        )
                        IconButton(onClick = { showEnlargedQr = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.DarkGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SubcomposeAsyncImage(
                        model = currentSelectedOption?.qrImageUrl,
                        contentDescription = "$gatewayName QR Code Enlarged",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = kbzBlue)
                            }
                        },
                        error = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                KbzQrCodeCanvas(modifier = Modifier.size(240.dp), qrColor = kbzBlue)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အခြားဖုန်းဖြင့် စကင်ဖတ်၍ ငွေချေပါ"
                        else "Scan with your banking or wallet app to pay",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * Clean Canvas-rendered QR Matrix with realistic finder patterns & center KPay badge
 */
@Composable
private fun KbzQrCodeCanvas(
    modifier: Modifier = Modifier,
    qrColor: Color = Color(0xFF003874)
) {
    val kbzBlue = qrColor
    val kbzCyan = Color(0xFF00A3E0)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val matrixSize = 21 // 21x21 QR Grid
        val cellSize = w / matrixSize

        // Background
        drawRoundRect(
            color = Color.White,
            size = Size(w, h),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )

        // Helper to draw Finder Pattern at (cellX, cellY)
        fun drawFinderPattern(cellX: Int, cellY: Int) {
            val left = cellX * cellSize
            val top = cellY * cellSize
            val finderWidth = 7 * cellSize

            // Outer dark box
            drawRoundRect(
                color = kbzBlue,
                topLeft = Offset(left, top),
                size = Size(finderWidth, finderWidth),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            // Inner light box
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(left + cellSize, top + cellSize),
                size = Size(finderWidth - 2 * cellSize, finderWidth - 2 * cellSize),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
            // Center dark square
            drawRoundRect(
                color = kbzBlue,
                topLeft = Offset(left + 2 * cellSize, top + 2 * cellSize),
                size = Size(finderWidth - 4 * cellSize, finderWidth - 4 * cellSize),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }

        // 3 Corner Finder Patterns
        drawFinderPattern(0, 0)
        drawFinderPattern(14, 0)
        drawFinderPattern(0, 14)

        // Deterministic Pseudo-random QR data dots
        val pattern = listOf(
            0b10101100101, 0b01100101101, 0b11001101010, 0b01011010011,
            0b10010110101, 0b11100101001, 0b01011100110, 0b10100110101,
            0b01101010011, 0b10011010110, 0b11010010101, 0b01010111001,
            0b10110010110, 0b01101100101, 0b10010101110, 0b11101001001,
            0b01010110110, 0b10101001101, 0b01100101011, 0b10011100101,
            0b11010011010
        )

        for (row in 0 until matrixSize) {
            for (col in 0 until matrixSize) {
                // Skip finder areas
                val inTopLeft = row < 8 && col < 8
                val inTopRight = row < 8 && col >= 13
                val inBottomLeft = row >= 13 && col < 8
                val inCenter = row in 8..12 && col in 8..12

                if (!inTopLeft && !inTopRight && !inBottomLeft && !inCenter) {
                    val bitVal = (pattern[row % pattern.size] shr (col % 11)) and 1
                    if (bitVal == 1) {
                        drawRoundRect(
                            color = kbzBlue,
                            topLeft = Offset(col * cellSize + 0.5f, row * cellSize + 0.5f),
                            size = Size(cellSize - 1f, cellSize - 1f),
                            cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                        )
                    }
                }
            }
        }

        // Center KBZ Shield Emblem
        val centerLeft = 8 * cellSize
        val centerTop = 8 * cellSize
        val centerSize = 5 * cellSize

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(centerLeft - 2f, centerTop - 2f),
            size = Size(centerSize + 4f, centerSize + 4f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        drawRoundRect(
            color = kbzCyan,
            topLeft = Offset(centerLeft, centerTop),
            size = Size(centerSize, centerSize),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        drawRoundRect(
            color = kbzBlue,
            topLeft = Offset(centerLeft + 2f, centerTop + 2f),
            size = Size(centerSize - 4f, centerSize - 4f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
    }
}

private fun saveSlipImageToInternalStorage(context: Context, sourceUri: Uri): String? {
    return try {
        val slipsDir = File(context.filesDir, "payment_slips")
        if (!slipsDir.exists()) {
            slipsDir.mkdirs()
        }
        val targetFile = File(slipsDir, "slip_${System.currentTimeMillis()}.jpg")
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

package com.example.ui.screens.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.model.Language
import com.example.model.OrderItemSnapshot
import com.example.model.OrderStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailDialog(
    order: OrderEntity,
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onUpdateOrderStatus: (OrderStatus) -> Unit,
    onUpdatePaymentStatus: (String) -> Unit,
    onUpdateTrackingInfo: (String, String) -> Unit
) {
    val context = LocalContext.current
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault()) }
    val formattedPlacedDate = remember(order.timestamp) { dateFormat.format(Date(order.timestamp)) }
    val formattedDeliveredDate = remember(order.deliveredAt) {
        if (order.deliveredAt > 0) dateFormat.format(Date(order.deliveredAt)) else null
    }

    // Parse item snapshots from JSON
    val itemSnapshots = remember(order.orderItemsJson) {
        OrderItemSnapshot.listFromJsonString(order.orderItemsJson)
    }

    var showTrackingEditDialog by remember { mutableStateOf(false) }
    var showPaymentStatusDropdown by remember { mutableStateOf(false) }
    var showOrderStatusDropdown by remember { mutableStateOf(false) }

    val paymentStatusOptions = listOf("PENDING", "PAID", "COD_PENDING", "COD_COLLECTED", "REFUNDED")
    val orderStatusOptions = OrderStatus.values().toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 680.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${if (currentLanguage == Language.BURMESE) "အော်ဒါအပြည့်အစုံ" else "Complete Transaction"} #${order.orderId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formattedPlacedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status Management & Action Row
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "အော်ဒါနှင့် ငွေပေးချေမှု အခြေအနေ ပြင်ဆင်ရန်" else "Status Controls",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Order Status Selector
                            ExposedDropdownMenuBox(
                                expanded = showOrderStatusDropdown,
                                onExpandedChange = { showOrderStatusDropdown = !showOrderStatusDropdown },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = order.status,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (currentLanguage == Language.BURMESE) "အော်ဒါအဆင့်" else "Order Status", fontSize = 11.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showOrderStatusDropdown) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .testTag("admin_order_status_dropdown")
                                )
                                ExposedDropdownMenu(
                                    expanded = showOrderStatusDropdown,
                                    onDismissRequest = { showOrderStatusDropdown = false }
                                ) {
                                    orderStatusOptions.forEach { status ->
                                        DropdownMenuItem(
                                            text = { Text(status.title(currentLanguage)) },
                                            onClick = {
                                                onUpdateOrderStatus(status)
                                                showOrderStatusDropdown = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Payment Status Selector
                            ExposedDropdownMenuBox(
                                expanded = showPaymentStatusDropdown,
                                onExpandedChange = { showPaymentStatusDropdown = !showPaymentStatusDropdown },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = order.paymentStatus,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (currentLanguage == Language.BURMESE) "ငွေပေးချေမှု" else "Payment", fontSize = 11.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPaymentStatusDropdown) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .testTag("admin_payment_status_dropdown")
                                )
                                ExposedDropdownMenu(
                                    expanded = showPaymentStatusDropdown,
                                    onDismissRequest = { showPaymentStatusDropdown = false }
                                ) {
                                    paymentStatusOptions.forEach { payStatus ->
                                        DropdownMenuItem(
                                            text = { Text(payStatus) },
                                            onClick = {
                                                onUpdatePaymentStatus(payStatus)
                                                showPaymentStatusDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        if (formattedDeliveredDate != null) {
                            Text(
                                text = "Delivered At: $formattedDeliveredDate",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF059669),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Customer Contact & Shipping Details
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
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
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူနှင့် ပို့ဆောင်ရမည့် လိပ်စာ" else "Customer & Shipping Information",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            if (order.customerPhone.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        try {
                                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.customerPhone}")))
                                        } catch (e: Exception) {}
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Text(
                            text = "${if (currentLanguage == Language.BURMESE) "အမည်" else "Name"}: ${order.customerName}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${if (currentLanguage == Language.BURMESE) "ဖုန်းနံပါတ်" else "Phone"}: ${order.customerPhone}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (order.customerEmail.isNotBlank()) {
                            Text(
                                text = "Email: ${order.customerEmail}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${if (currentLanguage == Language.BURMESE) "မြို့နယ်" else "Township"}: ${if (currentLanguage == Language.BURMESE) order.townshipNameMy else order.townshipNameEn}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${if (currentLanguage == Language.BURMESE) "အသေးစိတ်လိပ်စာ" else "Detailed Address"}: ${order.deliveryAddressNote.ifBlank { "N/A" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Logistics & Tracking Information
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
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
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ရေးနှင့် ခြေရာခံနံပါတ်" else "Logistics & Tracking",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            IconButton(
                                onClick = { showTrackingEditDialog = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Tracking", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(
                            text = "${if (currentLanguage == Language.BURMESE) "ပို့ဆောင်မှုပုံစံ" else "Delivery Type"}: ${order.deliveryType}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ရေး မိတ်ဖက်" else "Logistics Partner"}: ${order.deliveryPartnerName.ifBlank { "Yangon Seafood Express" }}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${if (currentLanguage == Language.BURMESE) "ခြေရာခံကုဒ်" else "Tracking Number"}: ${order.trackingNumber.ifBlank { "TRK-Pending" }}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // Products & Selected Variants Breakdown
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "မှာယူထားသော ကုန်ပစ္စည်းများနှင့် အရွယ်အစားများ" else "Products, Variants & Quantities",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (itemSnapshots.isNotEmpty()) {
                            itemSnapshots.forEach { item ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${if (currentLanguage == Language.BURMESE) item.nameMy else item.nameEn} x${item.quantity}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${numberFormat.format(item.subtotalMMK)} MMK",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    if (item.variant.isNotBlank()) {
                                        Text(
                                            text = "Selected Variant: ${item.variant}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (item.sku.isNotBlank()) {
                                        Text(
                                            text = "SKU: ${item.sku}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    if (item.addOns.isNotEmpty()) {
                                        Text(
                                            text = "Add-ons: ${item.addOns.joinToString(", ")}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (item.specialNotes.isNotBlank()) {
                                        Text(
                                            text = "Notes: ${item.specialNotes}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Divider(modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        } else {
                            // Fallback to itemsSummary string
                            Text(
                                text = order.itemsSummary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Financial Summary Breakdown (Subtotal, Discount, Shipping, Total)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ငွေစာရင်း ရှင်းတမ်း" else "Payment & Price Breakdown",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (currentLanguage == Language.BURMESE) "ကုန်ပစ္စည်း ကျသင့်ငွေ" else "Food Subtotal", style = MaterialTheme.typography.bodySmall)
                            Text("${numberFormat.format(order.foodSubtotalMMK)} MMK", style = MaterialTheme.typography.bodySmall)
                        }

                        if (order.discountMMK > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${if (currentLanguage == Language.BURMESE) "လျှော့ဈေး" else "Discount"} (${order.promoCode.ifBlank { "Promo" }})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF10B981)
                                )
                                Text("-${numberFormat.format(order.discountMMK)} MMK", style = MaterialTheme.typography.bodySmall, color = Color(0xFF10B981))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ခ" else "Delivery Fee", style = MaterialTheme.typography.bodySmall)
                            Text("${numberFormat.format(order.deliveryFeeMMK)} MMK", style = MaterialTheme.typography.bodySmall)
                        }

                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "စုစုပေါင်း ပေးချေငွေ" else "Grand Total",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${numberFormat.format(order.grandTotalMMK)} MMK",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Method: ${order.paymentMethod}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Status: ${order.paymentStatus}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (order.paymentStatus) {
                                    "PAID", "COD_COLLECTED" -> Color(0xFF059669)
                                    "REFUNDED" -> Color(0xFF3B82F6)
                                    else -> Color(0xFFD97706)
                                }
                            )
                        }
                    }
                }

                // Return & Cancellation info if present
                if (order.cancellationReason.isNotBlank() || order.returnReason.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (order.returnReason.isNotBlank()) "Return Request (${order.returnStatus})" else "Order Cancelled",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = order.returnReason.ifBlank { order.cancellationReason },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(if (currentLanguage == Language.BURMESE) "ပိတ်မည်" else "Done")
            }
        }
    )

    // Edit Tracking Dialog
    if (showTrackingEditDialog) {
        var newTrackingNo by remember { mutableStateOf(order.trackingNumber) }
        var newPartnerName by remember { mutableStateOf(order.deliveryPartnerName) }

        AlertDialog(
            onDismissRequest = { showTrackingEditDialog = false },
            title = { Text(if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ရေး ခြေရာခံကုဒ် ပြင်ဆင်ရန်" else "Update Logistics & Tracking") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTrackingNo,
                        onValueChange = { newTrackingNo = it },
                        label = { Text(if (currentLanguage == Language.BURMESE) "ခြေရာခံ နံပါတ်" else "Tracking Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPartnerName,
                        onValueChange = { newPartnerName = it },
                        label = { Text(if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ရေး မိတ်ဖက်အမည်" else "Delivery Partner Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateTrackingInfo(newTrackingNo.trim(), newPartnerName.trim())
                        showTrackingEditDialog = false
                    }
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showTrackingEditDialog = false }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }
}

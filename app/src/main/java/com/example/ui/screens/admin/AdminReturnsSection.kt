package com.example.ui.screens.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.model.Strings
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminReturnsSection(
    orders: List<OrderEntity>,
    currentLanguage: Language,
    onUpdateReturnStatus: (String, String, String) -> Unit, // orderId, returnStatus, paymentStatus
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("ALL") }
    val context = LocalContext.current

    // Target orders that have cancellations or returns
    val cancellationAndReturnOrders = orders.filter {
        it.returnStatus != "NONE" || it.status == "CANCELLED" || it.cancellationReason.isNotBlank()
    }

    val filteredOrders = cancellationAndReturnOrders.filter { o ->
        val matchesTab = when (selectedTab) {
            "REQUESTED" -> o.returnStatus == "REQUESTED"
            "APPROVED" -> o.returnStatus == "APPROVED"
            "REFUNDED" -> o.paymentStatus == "REFUNDED"
            "CANCELLED" -> o.status == "CANCELLED"
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                o.orderId.contains(searchQuery, ignoreCase = true) ||
                o.customerName.contains(searchQuery, ignoreCase = true) ||
                o.customerPhone.contains(searchQuery, ignoreCase = true) ||
                o.cancellationReason.contains(searchQuery, ignoreCase = true) ||
                o.returnReason.contains(searchQuery, ignoreCase = true)

        matchesTab && matchesSearch
    }

    val requestedCount = cancellationAndReturnOrders.count { it.returnStatus == "REQUESTED" }
    val approvedCount = cancellationAndReturnOrders.count { it.returnStatus == "APPROVED" }
    val cancelledCount = cancellationAndReturnOrders.count { it.status == "CANCELLED" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Filter Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "ပစ္စည်းပြန်ပို့မှုနှင့် ပယ်ဖျက်မှု စီမံခန့်ခွဲရေး" else "Returns, Refunds & Cancellations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        placeholder = { Text(if (currentLanguage == Language.BURMESE) "အော်ဒါ၊ အမည်၊ အကြောင်းပြချက်ဖြင့် ရှာရန်..." else "Search order, customer, reason...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_returns_search_input")
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedTab == "ALL",
                                onClick = { selectedTab = "ALL" },
                                label = { Text("${if (currentLanguage == Language.BURMESE) "အားလုံး" else "All"} (${cancellationAndReturnOrders.size})") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedTab == "REQUESTED",
                                onClick = { selectedTab = "REQUESTED" },
                                label = { Text("${if (currentLanguage == Language.BURMESE) "ပြန်ပို့ရန် တောင်းဆိုမှု" else "Return Requests"} ($requestedCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFEF4444).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFFDC2626)
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedTab == "APPROVED",
                                onClick = { selectedTab = "APPROVED" },
                                label = { Text("${if (currentLanguage == Language.BURMESE) "ခွင့်ပြုပြီး" else "Approved"} ($approvedCount)") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedTab == "CANCELLED",
                                onClick = { selectedTab = "CANCELLED" },
                                label = { Text("${if (currentLanguage == Language.BURMESE) "ပယ်ဖျက်ထားသော အော်ဒါ" else "Cancelled"} ($cancelledCount)") }
                            )
                        }
                    }
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentReturn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ပြန်ပို့မှု သို့မဟုတ် ပယ်ဖျက်မှု မှတ်တမ်း မရှိပါ" else "No returns or cancellations found",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredOrders, key = { it.orderId }) { order ->
                ReturnOrderCard(
                    order = order,
                    currentLanguage = currentLanguage,
                    onApproveReturn = {
                        onUpdateReturnStatus(order.orderId, "APPROVED", "REFUNDED")
                    },
                    onRejectReturn = {
                        onUpdateReturnStatus(order.orderId, "REJECTED", order.paymentStatus)
                    },
                    onMarkRefunded = {
                        onUpdateReturnStatus(order.orderId, order.returnStatus, "REFUNDED")
                    },
                    onCallCustomer = { phone ->
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ReturnOrderCard(
    order: OrderEntity,
    currentLanguage: Language,
    onApproveReturn: () -> Unit,
    onRejectReturn: () -> Unit,
    onMarkRefunded: () -> Unit,
    onCallCustomer: (String) -> Unit
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(order.timestamp) { dateFormat.format(Date(order.timestamp)) }
    var showActionConfirmDialog by remember { mutableStateOf<String?>(null) } // "APPROVE", "REJECT", "REFUND"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Order ID + Status Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "#${order.orderId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (order.returnStatus != "NONE") {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (order.returnStatus) {
                                "REQUESTED" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                "APPROVED" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                "REJECTED" -> Color(0xFF6B7280).copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
                        ) {
                            Text(
                                text = "Return: ${order.returnStatus}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (order.returnStatus) {
                                    "REQUESTED" -> Color(0xFFDC2626)
                                    "APPROVED" -> Color(0xFF059669)
                                    else -> Color(0xFF374151)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (order.status == "CANCELLED") {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDC2626).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ပယ်ဖျက်ပြီး" else "CANCELLED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Customer Contact Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${order.customerName} • ${order.customerPhone}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${if (currentLanguage == Language.BURMESE) order.townshipNameMy else order.townshipNameEn} (${order.deliveryAddressNote.ifBlank { "No address note" }})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (order.customerPhone.isNotBlank()) {
                    IconButton(
                        onClick = { onCallCustomer(order.customerPhone) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Items & Total Price
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${if (currentLanguage == Language.BURMESE) "မှာယူထားသော စာရင်း" else "Ordered Items"}: ${order.itemsSummary}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${if (currentLanguage == Language.BURMESE) "စုစုပေါင်း ကျသင့်ငွေ" else "Total Amount"}: ${numberFormat.format(order.grandTotalMMK)} MMK",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Payment: ${order.paymentStatus} (${order.paymentMethod})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // User-generated Reason Alert Box
            if (order.returnReason.isNotBlank() || order.cancellationReason.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (order.returnReason.isNotBlank())
                                    (if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူ၏ ပစ္စည်းပြန်ပို့ရသည့် အကြောင်းပြချက်" else "Customer Return Reason")
                                else
                                    (if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူ၏ အော်ဒါပယ်ဖျက်ရသည့် အကြောင်းပြချက်" else "Customer Cancellation Reason"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                        }
                        Text(
                            text = order.returnReason.ifBlank { order.cancellationReason },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF7F1D1D)
                        )
                    }
                }
            }

            // Action Buttons for Return Processing
            if (order.returnStatus == "REQUESTED") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showActionConfirmDialog = "APPROVE" },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("approve_return_btn_${order.orderId}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLanguage == Language.BURMESE) "ခွင့်ပြု & ငွေပြန်အမ်း" else "Approve & Refund", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { showActionConfirmDialog = "REJECT" },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reject_return_btn_${order.orderId}")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLanguage == Language.BURMESE) "ငြင်းပယ်မည်" else "Reject", fontSize = 12.sp)
                    }
                }
            } else if (order.paymentStatus != "REFUNDED" && (order.returnStatus == "APPROVED" || order.status == "CANCELLED")) {
                Button(
                    onClick = { showActionConfirmDialog = "REFUND" },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (currentLanguage == Language.BURMESE) "ငွေပြန်အမ်းပြီးဟု သတ်မှတ်မည်" else "Mark Payment Refunded")
                }
            }
        }
    }

    // Action Confirmation Dialog
    showActionConfirmDialog?.let { action ->
        AlertDialog(
            onDismissRequest = { showActionConfirmDialog = null },
            icon = {
                Icon(
                    imageVector = when (action) {
                        "APPROVE" -> Icons.Default.CheckCircle
                        "REJECT" -> Icons.Default.Cancel
                        else -> Icons.Default.Payments
                    },
                    contentDescription = null,
                    tint = if (action == "REJECT") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = when (action) {
                        "APPROVE" -> if (currentLanguage == Language.BURMESE) "ပစ္စည်းပြန်ပို့မှု ခွင့်ပြုပြီး ငွေပြန်အမ်းမည်လား။" else "Approve Return & Refund?"
                        "REJECT" -> if (currentLanguage == Language.BURMESE) "ပစ္စည်းပြန်ပို့မှု ငြင်းပယ်မည်လား။" else "Reject Return Request?"
                        else -> if (currentLanguage == Language.BURMESE) "ငွေပြန်အမ်းပြီးဟု မှတ်တမ်းတင်မည်လား။" else "Mark Payment Refunded?"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Order #${order.orderId} (${order.customerName}, ${numberFormat.format(order.grandTotalMMK)} MMK)"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (action) {
                            "APPROVE" -> onApproveReturn()
                            "REJECT" -> onRejectReturn()
                            "REFUND" -> onMarkRefunded()
                        }
                        showActionConfirmDialog = null
                    }
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "သေချာပါသည်" else "Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showActionConfirmDialog = null }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }
}

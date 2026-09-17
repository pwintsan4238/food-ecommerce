package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.model.Language
import com.example.model.OrderStatus
import com.example.model.Strings
import com.example.ui.components.LanguageToggle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderHistoryScreen(
    orders: List<OrderEntity>,
    currentLanguage: Language,
    onLanguageToggle: () -> Unit,
    onTrackOrder: (String) -> Unit,
    onReorder: (OrderEntity) -> Unit,
    onBrowseMenuClick: () -> Unit,
    onRequestReturn: (orderId: String, reason: String) -> Unit = { _, _ -> },
    onContactSupport: (orderId: String) -> Unit = {},
    isAdmin: Boolean = false,
    onAdminBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var orderForReturn by remember { mutableStateOf<OrderEntity?>(null) }
    var returnReasonInput by remember { mutableStateOf("") }
    var returnReasonError by remember { mutableStateOf(false) }

    if (orderForReturn != null) {
        val targetOrder = orderForReturn!!
        AlertDialog(
            onDismissRequest = {
                orderForReturn = null
                returnReasonInput = ""
                returnReasonError = false
            },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "ပစ္စည်းပြန်ပို့ / ငွေပြန်အမ်း တောင်းဆိုခြင်း" else "Request Return & Refund",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE)
                            "အော်ဒါ #${targetOrder.orderId} အတွက် ပစ္စည်းပြန်ပို့လိုသော အကြောင်းပြချက်ကို ထည့်သွင်းပေးပါရန်။"
                        else
                            "Please state the reason for returning Order #${targetOrder.orderId}:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = returnReasonInput,
                        onValueChange = {
                            returnReasonInput = it
                            if (it.isNotBlank()) returnReasonError = false
                        },
                        label = {
                            Text(if (currentLanguage == Language.BURMESE) "အကြောင်းပြချက် (မဖြစ်မနေ)" else "Reason (Required)")
                        },
                        placeholder = {
                            Text(if (currentLanguage == Language.BURMESE) "ဥပမာ - ပစ္စည်းမှားယွင်းရောက်ရှိခြင်း သို့မဟုတ် ပျက်စီးနေခြင်း" else "e.g., Wrong item received, defective packaging, damaged goods")
                        },
                        isError = returnReasonError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("return_reason_input"),
                        minLines = 3,
                        maxLines = 5
                    )
                    if (returnReasonError) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ကျေးဇူးပြု၍ အကြောင်းပြချက် ထည့်သွင်းပါ" else "Please provide a reason",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (returnReasonInput.isBlank()) {
                            returnReasonError = true
                        } else {
                            onRequestReturn(targetOrder.orderId, returnReasonInput.trim())
                            orderForReturn = null
                            returnReasonInput = ""
                            returnReasonError = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_submit_return_btn")
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "တောင်းဆိုမှု ပေးပို့မည်" else "Submit Request")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    orderForReturn = null
                    returnReasonInput = ""
                    returnReasonError = false
                }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isAdmin) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable { onAdminBack() }
                                .testTag("admin_history_back_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.orderHistoryTitle(currentLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                LanguageToggle(
                    currentLanguage = currentLanguage,
                    onLanguageToggle = onLanguageToggle
                )
            }
        }

        if (orders.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "📜", fontSize = 56.sp)
                        Text(
                            text = Strings.noOrdersYet(currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Strings.emptyCartSubtitle(currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onBrowseMenuClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("browse_menu_from_history_button")
                        ) {
                            Text(
                                text = Strings.homeTab(currentLanguage),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            items(orders, key = { it.orderId }) { order ->
                OrderHistoryCard(
                    order = order,
                    currentLanguage = currentLanguage,
                    onTrackOrder = { onTrackOrder(order.orderId) },
                    onReorder = { onReorder(order) },
                    onRequestReturn = { orderForReturn = order },
                    onContactSupport = { onContactSupport(order.orderId) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OrderHistoryCard(
    order: OrderEntity,
    currentLanguage: Language,
    onTrackOrder: () -> Unit,
    onReorder: () -> Unit,
    onRequestReturn: () -> Unit = {},
    onContactSupport: () -> Unit = {}
) {
    val currentStatus = try {
        OrderStatus.valueOf(order.status)
    } catch (e: Exception) {
        OrderStatus.PLACED
    }
    val isFinished = currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELLED

    val formattedDate = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(order.timestamp))

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_card_${order.orderId}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ORDER #${order.orderId}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (currentStatus) {
                            OrderStatus.DELIVERED -> MaterialTheme.colorScheme.tertiaryContainer
                            OrderStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ) {
                        Text(
                            text = currentStatus.title(currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (currentStatus) {
                                OrderStatus.DELIVERED -> MaterialTheme.colorScheme.onTertiaryContainer
                                OrderStatus.CANCELLED -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (order.returnStatus != "NONE") {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (order.returnStatus) {
                                "APPROVED", "REFUNDED" -> MaterialTheme.colorScheme.tertiaryContainer
                                "REJECTED" -> MaterialTheme.colorScheme.errorContainer
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            }
                        ) {
                            Text(
                                text = "Return: ${order.returnStatus}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (order.returnStatus) {
                                    "APPROVED", "REFUNDED" -> MaterialTheme.colorScheme.onTertiaryContainer
                                    "REJECTED" -> MaterialTheme.colorScheme.onErrorContainer
                                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = order.itemsSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            Text(
                text = "${order.townshipNameMy} (${order.townshipNameEn})${if (order.deliveryAddressNote.isNotBlank()) " • ${order.deliveryAddressNote}" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Cancellation Reason
            if (order.cancellationReason.isNotBlank()) {
                Text(
                    text = "${if (currentLanguage == Language.BURMESE) "ပယ်ဖျက်ရသည့် အကြောင်းရင်း" else "Cancellation Reason"}: ${order.cancellationReason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Return Reason
            if (order.returnReason.isNotBlank()) {
                Text(
                    text = "${if (currentLanguage == Language.BURMESE) "ပြန်ပို့ရသည့် အကြောင်းရင်း" else "Return Reason"}: ${order.returnReason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.mmkCurrency(currentLanguage, order.grandTotalMMK),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!isFinished) {
                        Button(
                            onClick = onTrackOrder,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = Strings.trackOrder(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = onReorder,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = Strings.orderAgain(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Return & Support Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (currentStatus == OrderStatus.DELIVERED && order.returnStatus == "NONE") {
                    OutlinedButton(
                        onClick = onRequestReturn,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("request_return_button_${order.orderId}")
                    ) {
                        Icon(imageVector = Icons.Default.AssignmentReturn, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ပစ္စည်းပြန်ပို့မည်" else "Request Return",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                OutlinedButton(
                    onClick = onContactSupport,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("contact_support_order_btn_${order.orderId}")
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အကူအညီတောင်းမည်" else "Contact Support",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

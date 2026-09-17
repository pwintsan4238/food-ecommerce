package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.model.Language
import com.example.model.Strings
import com.example.ui.components.LanguageToggle
import com.example.ui.components.OrderTimelineTracker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    currentTrackedOrder: OrderEntity?,
    allOrders: List<OrderEntity>,
    currentLanguage: Language,
    kbzPayTimeRemainingSeconds: Int = 600,
    isKbzPayPaid: Boolean = false,
    onLanguageToggle: () -> Unit,
    onSelectOrderToTrack: (String) -> Unit,
    onSimulateNextStatus: (String) -> Unit,
    onCancelOrder: (String) -> Unit,
    onBrowseMenuClick: () -> Unit,
    onOpenKbzPayQr: (String) -> Unit = {},
    isAdmin: Boolean = false,
    onAdminBack: () -> Unit = {},
    brandName: String = "",
    supportPhone: String = "",
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                .testTag("admin_tracking_back_button")
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
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.orderTrackingTitle(currentLanguage),
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

        if (currentTrackedOrder == null) {
            // Empty State
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
                        Text(text = "🛵", fontSize = 56.sp)
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
                            modifier = Modifier.testTag("browse_menu_from_tracking_button")
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
            // Push Notification Banner notice
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.autoTrackingActive(currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // KBZ Pay Specific Payment Status & QR Card
            if (currentTrackedOrder.paymentMethod == "kpay") {
                item {
                    val minutes = kbzPayTimeRemainingSeconds / 60
                    val seconds = kbzPayTimeRemainingSeconds % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)
                    val kbzBlue = androidx.compose.ui.graphics.Color(0xFF003874)

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, kbzBlue.copy(alpha = 0.3f)),
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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = kbzBlue,
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "KPay",
                                            color = androidx.compose.ui.graphics.Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = if (currentLanguage == Language.BURMESE) "KBZPay ငွေပေးချေမှု" else "KBZPay Payment Status",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = if (isKbzPayPaid) "✓ Paid" else "⏰ $formattedTime left",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isKbzPayPaid) androidx.compose.ui.graphics.Color(0xFF2E7D32) else kbzBlue
                                )
                            }

                            val displayBrand = brandName.ifBlank { if (currentLanguage == Language.BURMESE) "တိမ်တမန်" else "Taim Ta Man" }
                            val displayContact = if (supportPhone.isNotBlank()) " ($displayBrand - $supportPhone)" else ""
                            Text(
                                text = if (currentLanguage == Language.BURMESE)
                                    "ကျသင့်ငွေ: ${Strings.mmkCurrency(currentLanguage, currentTrackedOrder.grandTotalMMK)}$displayContact"
                                else
                                    "Total: ${Strings.mmkCurrency(currentLanguage, currentTrackedOrder.grandTotalMMK)}$displayContact",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { onOpenKbzPayQr(currentTrackedOrder.orderId) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = kbzBlue),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("view_kbz_qr_tracking_button")
                            ) {
                                Text(
                                    text = Strings.viewKbzQr(currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Primary Tracked Order Card
            item {
                OrderTimelineTracker(
                    order = currentTrackedOrder,
                    currentLanguage = currentLanguage,
                    onSimulateNextStatus = { onSimulateNextStatus(currentTrackedOrder.orderId) },
                    onCancelOrder = { onCancelOrder(currentTrackedOrder.orderId) }
                )
            }

            // If there are other orders, list them
            val otherOrders = allOrders.filter { it.orderId != currentTrackedOrder.orderId }
            if (otherOrders.isNotEmpty()) {
                item {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အခြား အော်ဒါများ" else "Other Active Orders",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(otherOrders, key = { it.orderId }) { order ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectOrderToTrack(order.orderId) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "#${order.orderId} • ${order.townshipNameMy}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = order.itemsSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = order.status,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.model.Language
import com.example.model.OrderStatus
import com.example.model.Strings

@Composable
fun OrderTimelineTracker(
    order: OrderEntity,
    currentLanguage: Language,
    onSimulateNextStatus: () -> Unit,
    onCancelOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStatus = try {
        OrderStatus.valueOf(order.status)
    } catch (e: Exception) {
        OrderStatus.PLACED
    }

    val isCancelled = currentStatus == OrderStatus.CANCELLED
    val isDelivered = currentStatus == OrderStatus.DELIVERED

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // High Density Order Tracker Header Box (Dashed Purple Accent)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isCancelled) MaterialTheme.colorScheme.error else Color(0xFF2E7D32))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ORDER TRACKER / အခြေအနေ" else "ORDER TRACKER / အခြေအနေ",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "အော်ဒါ အမှတ် #${order.orderId}" else "Order #${order.orderId}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        color = if (isCancelled) MaterialTheme.colorScheme.errorContainer
                        else if (isDelivered) MaterialTheme.colorScheme.tertiaryContainer
                        else MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isCancelled) Icons.Default.HourglassTop
                                else if (isDelivered) Icons.Default.Check
                                else Icons.Default.DeliveryDining,
                                contentDescription = null,
                                tint = if (isCancelled) MaterialTheme.colorScheme.onErrorContainer
                                else if (isDelivered) MaterialTheme.colorScheme.onTertiaryContainer
                                else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCancelled) (if (currentLanguage == Language.BURMESE) "ပယ်ဖျက်ပြီး" else "Cancelled")
                                else if (isDelivered) (if (currentLanguage == Language.BURMESE) "ရောက်ရှိပြီး" else "Delivered")
                                else "${order.estimatedMinutes} mins",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isCancelled) MaterialTheme.colorScheme.onErrorContainer
                                else if (isDelivered) MaterialTheme.colorScheme.onTertiaryContainer
                                else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

            // 4-Step Interactive Timeline
            if (!isCancelled) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    TimelineStepRow(
                        stepNumber = 1,
                        title = OrderStatus.PLACED.title(currentLanguage),
                        description = OrderStatus.PLACED.description(currentLanguage),
                        isPassed = currentStatus.stepIndex > 1,
                        isActive = currentStatus == OrderStatus.PLACED,
                        isLast = false,
                        icon = Icons.Default.Fastfood
                    )
                    TimelineStepRow(
                        stepNumber = 2,
                        title = OrderStatus.PREPARING.title(currentLanguage),
                        description = OrderStatus.PREPARING.description(currentLanguage),
                        isPassed = currentStatus.stepIndex > 2,
                        isActive = currentStatus == OrderStatus.PREPARING,
                        isLast = false,
                        icon = Icons.Default.Kitchen
                    )
                    TimelineStepRow(
                        stepNumber = 3,
                        title = OrderStatus.OUT_FOR_DELIVERY.title(currentLanguage),
                        description = OrderStatus.OUT_FOR_DELIVERY.description(currentLanguage),
                        isPassed = currentStatus.stepIndex > 3,
                        isActive = currentStatus == OrderStatus.OUT_FOR_DELIVERY,
                        isLast = false,
                        icon = Icons.Default.SportsMotorsports
                    )
                    TimelineStepRow(
                        stepNumber = 4,
                        title = OrderStatus.DELIVERED.title(currentLanguage),
                        description = OrderStatus.DELIVERED.description(currentLanguage),
                        isPassed = currentStatus.stepIndex >= 4,
                        isActive = currentStatus == OrderStatus.DELIVERED,
                        isLast = true,
                        icon = Icons.Default.Check
                    )
                }
            } else {
                // Cancelled Notice
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = OrderStatus.CANCELLED.description(currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // Rider Contact Info when Out For Delivery
            if (currentStatus == OrderStatus.OUT_FOR_DELIVERY || currentStatus == OrderStatus.PREPARING) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsMotorsports,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentLanguage == Language.BURMESE) "ပို့ဆောင်ရေး ကိုမင်းမင်း (Rider)" else "Ko Min Min (Delivery Rider)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "09450098765 • Yamaha FZ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { /* simulated call */ },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.testTag("call_rider_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = Strings.callRider(currentLanguage), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Order Summary Bill Preview
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${if (currentLanguage == Language.BURMESE) "မှာယူထားသော စာရင်း" else "Items"}: ${order.itemsSummary}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${if (currentLanguage == Language.BURMESE) "ပို့ဆောင်မည့်သူ" else "Customer"}: ${order.customerName} (${order.customerPhone})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${Strings.grandTotal(currentLanguage)}: ${Strings.mmkCurrency(currentLanguage, order.grandTotalMMK)} • ${order.paymentMethod.uppercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Controls & Simulator Actions
            if (!isDelivered && !isCancelled) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onSimulateNextStatus,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simulate_next_status_button")
                    ) {
                        Text(
                            text = Strings.simulateStatusUpdate(currentLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    if (currentStatus == OrderStatus.PLACED) {
                        OutlinedButton(
                            onClick = onCancelOrder,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cancel_order_button")
                        ) {
                            Text(
                                text = Strings.cancelOrder(currentLanguage),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineStepRow(
    stepNumber: Int,
    title: String,
    description: String,
    isPassed: Boolean,
    isActive: Boolean,
    isLast: Boolean,
    icon: ImageVector
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val stepColor by animateColorAsState(
        targetValue = when {
            isPassed || isActive -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        },
        label = "step_color"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Left Column: Step Icon and Vertical Connecting Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .then(if (isActive) Modifier.scale(pulseScale) else Modifier)
                    .clip(CircleShape)
                    .background(
                        if (isPassed) MaterialTheme.colorScheme.primary
                        else if (isActive) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    imageVector = if (isPassed) Icons.Default.Check else icon,
                    contentDescription = null,
                    tint = if (isPassed) MaterialTheme.colorScheme.onPrimary
                    else if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(stepColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Column: Title and Description
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isActive || isPassed) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive || isPassed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isActive) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

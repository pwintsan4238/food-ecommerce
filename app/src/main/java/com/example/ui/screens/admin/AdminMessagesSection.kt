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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Reply
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.CustomerMessageEntity
import com.example.model.Language
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminMessagesSection(
    messages: List<CustomerMessageEntity>,
    currentLanguage: Language,
    onReplyToMessage: (String, String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedMessageForReply by remember { mutableStateOf<CustomerMessageEntity?>(null) }
    var messageToDelete by remember { mutableStateOf<CustomerMessageEntity?>(null) }
    val context = LocalContext.current

    val filteredMessages = messages.filter { msg ->
        val matchesFilter = when (selectedFilter) {
            "NEW" -> msg.status.uppercase() == "NEW"
            "IN_PROGRESS" -> msg.status.uppercase() == "IN_PROGRESS"
            "RESOLVED" -> msg.status.uppercase() == "RESOLVED"
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                msg.customerName.contains(searchQuery, ignoreCase = true) ||
                msg.customerPhone.contains(searchQuery, ignoreCase = true) ||
                msg.subject.contains(searchQuery, ignoreCase = true) ||
                msg.message.contains(searchQuery, ignoreCase = true) ||
                msg.orderId.contains(searchQuery, ignoreCase = true)

        matchesFilter && matchesSearch
    }

    val newCount = messages.count { it.status.uppercase() == "NEW" }
    val inProgressCount = messages.count { it.status.uppercase() == "IN_PROGRESS" }
    val resolvedCount = messages.count { it.status.uppercase() == "RESOLVED" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Search & Filters Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူများ၏ မေးမြန်းချက်နှင့် မက်ဆေ့ချ်များ" else "Customer Inquiries & Messages",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        placeholder = { Text(if (currentLanguage == Language.BURMESE) "အမည်၊ ဖုန်း၊ အော်ဒါ၊ ခေါင်းစဉ်ဖြင့် ရှာဖွေပါ..." else "Search by name, phone, order, subject...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_messages_search_input")
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedFilter == "ALL",
                                onClick = { selectedFilter = "ALL" },
                                label = { Text("${if (currentLanguage == Language.BURMESE) "အားလုံး" else "All"} (${messages.size})") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedFilter == "NEW",
                                onClick = { selectedFilter = "NEW" },
                                label = { Text("${if (currentLanguage == Language.BURMESE) "အသစ်" else "New"} ($newCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFEF4444).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFFDC2626)
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedFilter == "IN_PROGRESS",
                                onClick = { selectedFilter = "IN_PROGRESS" },
                                label = { Text("${if (currentLanguage == Language.BURMESE) "ဆောင်ရွက်ဆဲ" else "In Progress"} ($inProgressCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF3B82F6).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFF2563EB)
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedFilter == "RESOLVED",
                                onClick = { selectedFilter = "RESOLVED" },
                                label = { Text("${if (currentLanguage == Language.BURMESE) "ပြီးပြတ်" else "Resolved"} ($resolvedCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF10B981).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFF059669)
                                )
                            )
                        }
                    }
                }
            }
        }

        if (filteredMessages.isEmpty()) {
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
                            imageVector = Icons.Default.MarkEmailRead,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "မေးမြန်းချက်များ မရှိသေးပါ" else "No customer messages found",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredMessages, key = { it.id }) { msg ->
                CustomerMessageCard(
                    message = msg,
                    currentLanguage = currentLanguage,
                    onReplyClick = { selectedMessageForReply = msg },
                    onDeleteClick = { messageToDelete = msg },
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

    // Reply Dialog
    selectedMessageForReply?.let { msg ->
        var replyContent by remember(msg.id) { mutableStateOf(msg.adminReply) }
        var targetStatus by remember(msg.id) { mutableStateOf(if (msg.status == "NEW") "IN_PROGRESS" else msg.status) }

        AlertDialog(
            onDismissRequest = { selectedMessageForReply = null },
            icon = { Icon(Icons.Default.Reply, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "မေးမြန်းချက်အား အကြောင်းပြန်ရန်" else "Reply to Customer",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "${if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူ" else "Customer"}: ${msg.customerName} (${msg.customerPhone})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${if (currentLanguage == Language.BURMESE) "ခေါင်းစဉ်" else "Subject"}: ${msg.subject}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg.message,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အခြေအနေ သတ်မှတ်ချက်" else "Update Status",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = targetStatus == "IN_PROGRESS",
                            onClick = { targetStatus = "IN_PROGRESS" },
                            label = { Text(if (currentLanguage == Language.BURMESE) "ဆောင်ရွက်ဆဲ" else "In Progress") }
                        )
                        FilterChip(
                            selected = targetStatus == "RESOLVED",
                            onClick = { targetStatus = "RESOLVED" },
                            label = { Text(if (currentLanguage == Language.BURMESE) "ပြီးပြတ်ပါပြီ" else "Resolved") }
                        )
                    }

                    OutlinedTextField(
                        value = replyContent,
                        onValueChange = { replyContent = it },
                        label = { Text(if (currentLanguage == Language.BURMESE) "စီမံခန့်ခွဲသူ အကြောင်းပြန်စာ" else "Admin Reply Note") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_reply_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReplyToMessage(msg.id, replyContent.trim(), targetStatus)
                        selectedMessageForReply = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("submit_admin_reply_btn")
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "အကြောင်းပြန်ချက် သိမ်းမည်" else "Send Reply")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedMessageForReply = null }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    messageToDelete?.let { msg ->
        AlertDialog(
            onDismissRequest = { messageToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "မက်ဆေ့ချ် ဖျက်မည်လား။" else "Delete Customer Message?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (currentLanguage == Language.BURMESE)
                        "'${msg.customerName}' ၏ မေးမြန်းချက်ကို အပြီးတိုင် ဖျက်ပါမည်လား။"
                    else
                        "Are you sure you want to permanently delete this message from ${msg.customerName}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteMessage(msg.id)
                        messageToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "ဖျက်မည်" else "Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { messageToDelete = null }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun CustomerMessageCard(
    message: CustomerMessageEntity,
    currentLanguage: Language,
    onReplyClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCallCustomer: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(message.timestamp) { dateFormat.format(Date(message.timestamp)) }

    val statusColor = when (message.status.uppercase()) {
        "NEW" -> Color(0xFFEF4444)
        "IN_PROGRESS" -> Color(0xFF3B82F6)
        "RESOLVED" -> Color(0xFF10B981)
        else -> MaterialTheme.colorScheme.outline
    }

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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Status Badge + Category + Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(statusColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (message.status.uppercase()) {
                                "NEW" -> if (currentLanguage == Language.BURMESE) "အသစ်" else "NEW"
                                "IN_PROGRESS" -> if (currentLanguage == Language.BURMESE) "ဆောင်ရွက်ဆဲ" else "IN PROGRESS"
                                "RESOLVED" -> if (currentLanguage == Language.BURMESE) "ပြီးပြတ်ပါပြီ" else "RESOLVED"
                                else -> message.status
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Customer Name and Contact Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = message.customerName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = message.customerPhone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (message.customerPhone.isNotBlank()) {
                        IconButton(
                            onClick = { onCallCustomer(message.customerPhone) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                        }
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Order ID link if available
            if (message.orderId.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${if (currentLanguage == Language.BURMESE) "သက်ဆိုင်ရာ အော်ဒါ" else "Linked Order"}: #${message.orderId}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Subject & Message Body
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = message.subject,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Admin Reply Section (if already replied)
            if (message.adminReply.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "စီမံခန့်ခွဲသူ၏ ပြန်ကြားချက်" else "Admin Response",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = message.adminReply,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Action: Reply button
            Button(
                onClick = onReplyClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (message.adminReply.isNotBlank())
                        (if (currentLanguage == Language.BURMESE) "အကြောင်းပြန်ချက် ပြင်ဆင်ရန်" else "Update Reply")
                    else
                        (if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူအား အကြောင်းပြန်မည်" else "Reply to Inquiry"),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

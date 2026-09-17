package com.example.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.UserAccountEntity
import com.example.model.Language
import com.example.model.Strings

@Composable
fun AdminEditCustomerDialog(
    customer: UserAccountEntity,
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onSave: (name: String, email: String, townshipId: String, defaultAddressNote: String, adminNotes: String) -> Unit
) {
    var name by remember { mutableStateOf(customer.name) }
    var email by remember { mutableStateOf(customer.email) }
    var defaultAddress by remember { mutableStateOf(customer.defaultAddressNote) }
    var preferredTownship by remember { mutableStateOf(customer.preferredTownshipId) }
    var adminNotes by remember { mutableStateOf(customer.adminNotes) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("admin_customer_edit_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ဖောက်သည် အချက်အလက် ပြင်ဆင်ရန်" else "Customer Profile & Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "📞 ${customer.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                // Customer Metrics Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "မှာယူမှု စုစုပေါင်း" else "Total Orders",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${customer.totalOrdersCount} ${if (currentLanguage == Language.BURMESE) "ကြိမ်" else "orders"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        VerticalDivider(modifier = Modifier.height(32.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "သုံးစွဲငွေ စုစုပေါင်း" else "Total Spent",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Strings.mmkCurrency(currentLanguage, customer.totalSpentMMK),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (currentLanguage == Language.BURMESE) "ဖောက်သည် အမည်" else "Customer Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("customer_edit_name_input")
                )

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (currentLanguage == Language.BURMESE) "အီးမေးလ်" else "Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("customer_edit_email_input")
                )

                // Shipping / Delivery Address
                OutlinedTextField(
                    value = defaultAddress,
                    onValueChange = { defaultAddress = it },
                    label = { Text(if (currentLanguage == Language.BURMESE) "ပုံသေ ပို့ဆောင်ရမည့် လိပ်စာ" else "Default Delivery Address") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("customer_edit_address_input")
                )

                // Preferred Township
                OutlinedTextField(
                    value = preferredTownship,
                    onValueChange = { preferredTownship = it },
                    label = { Text(if (currentLanguage == Language.BURMESE) "ဦးစားပေး မြို့နယ် ID" else "Preferred Township ID") },
                    leadingIcon = { Icon(Icons.Default.HomeWork, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("customer_edit_township_input")
                )

                // Admin CRM Internal Notes
                OutlinedTextField(
                    value = adminNotes,
                    onValueChange = { adminNotes = it },
                    label = { Text(if (currentLanguage == Language.BURMESE) "Admin အတွင်းရေး မှတ်ချက်များ (CRM Notes)" else "Admin Notes (Customer Preferences / VIP)") },
                    placeholder = {
                        Text(
                            if (currentLanguage == Language.BURMESE) "ဥပမာ - VIP ဖောက်သည်၊ စပ်စပ်ပိုကြိုက်၊ ငွေလွှဲပြေစာ အမြဲတောင်းသည်"
                            else "e.g. VIP Customer, prefers extra chili, always requests physical receipt",
                            fontSize = 11.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("customer_edit_admin_notes_input")
                )

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
                        onClick = { onSave(name, email, preferredTownship, defaultAddress, adminNotes) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("customer_edit_save_button")
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "သိမ်းဆည်းမည်" else "Save Changes",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

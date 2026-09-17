package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.example.model.Language
import com.example.model.PromotionItem

@Composable
fun AdminPromotionsSection(
    promotions: List<PromotionItem>,
    currentLanguage: Language,
    onTogglePromotion: (String) -> Unit,
    onAddPromotion: (String, String, String, Int, String, String) -> Unit,
    onDeletePromotion: (String) -> Unit,
    isAddingPromo: Boolean = false,
    onDismissAddPromo: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    // Sync with external trigger (e.g. "+ New" top bar button)
    val shouldShowDialog = showCreateDialog || isAddingPromo

    val activeCount = promotions.count { it.isActive }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Top Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "ပရိုမိုးရှင်းနှင့် လျှော့ဈေးကူပွန်များ" else "Promotions & Discounts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "လက်ရှိ အသုံးပြုနိုင်သော ပရိုမိုးရှင်း ($activeCount) ခုရှိပါသည်"
                            else "$activeCount active campaigns running",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("admin_add_promo_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "အသစ်ထည့်" else "New Promo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Promotions List
        if (promotions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "ပရိုမိုးရှင်း မရှိသေးပါ" else "No promotions created yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(promotions, key = { it.id }) { promo ->
                PromotionCard(
                    promo = promo,
                    currentLanguage = currentLanguage,
                    onToggle = { onTogglePromotion(promo.id) },
                    onDelete = { onDeletePromotion(promo.id) }
                )
            }
        }
    }

    // Create Promotion Dialog
    if (shouldShowDialog) {
        CreatePromotionDialog(
            currentLanguage = currentLanguage,
            onDismiss = {
                showCreateDialog = false
                onDismissAddPromo()
            },
            onSave = { titleEn, titleMy, code, discount, descEn, descMy ->
                onAddPromotion(titleEn, titleMy, code, discount, descEn, descMy)
                showCreateDialog = false
                onDismissAddPromo()
            }
        )
    }
}

@Composable
private fun PromotionCard(
    promo: PromotionItem,
    currentLanguage: Language,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (promo.isActive) MaterialTheme.colorScheme.surface else Color(0xFFF9FAFB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (promo.isActive) 2.dp else 0.dp),
        border = if (!promo.isActive) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)) else null,
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
                Surface(
                    shape = CircleShape,
                    color = if (promo.isActive) Color(0xFFFDE8E8) else Color(0xFFE5E7EB),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = if (promo.isActive) Color(0xFFD32F2F) else Color(0xFF9CA3AF),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (currentLanguage == Language.BURMESE) promo.titleMy else promo.titleEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (promo.isActive) MaterialTheme.colorScheme.onSurface else Color(0xFF6B7280),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (promo.isActive) Color(0xFFFEF2F2) else Color(0xFFF3F4F6)
                        ) {
                            Text(
                                text = promo.code,
                                color = if (promo.isActive) Color(0xFFB91C1C) else Color(0xFF6B7280),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (currentLanguage == Language.BURMESE) promo.descriptionMy else promo.descriptionEn,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${promo.discountPercent}% OFF",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Switch(
                    checked = promo.isActive,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFD32F2F),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD1D5DB)
                    ),
                    modifier = Modifier.testTag("promo_switch_${promo.id}")
                )
            }
        }
    }
}

@Composable
private fun CreatePromotionDialog(
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int, String, String) -> Unit
) {
    var titleEn by remember { mutableStateOf("") }
    var titleMy by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var discountText by remember { mutableStateOf("15") }
    var descEn by remember { mutableStateOf("") }
    var descMy by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (currentLanguage == Language.BURMESE) "ပရိုမိုးရှင်းအသစ် ဖန်တီးရန်" else "Create New Promotion",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text(if (currentLanguage == Language.BURMESE) "ကူပွန်ကုဒ် (ဥပမာ: SUMMER15)" else "Coupon Code (e.g. SUMMER15)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                )

                OutlinedTextField(
                    value = discountText,
                    onValueChange = { discountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text(if (currentLanguage == Language.BURMESE) "လျှော့ဈေး ရာခိုင်နှုန်း (%)" else "Discount Percentage (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = titleEn,
                    onValueChange = { titleEn = it },
                    label = { Text("Title (English)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = titleMy,
                    onValueChange = { titleMy = it },
                    label = { Text("Title (Burmese / မြန်မာ)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = descEn,
                    onValueChange = { descEn = it },
                    label = { Text(if (currentLanguage == Language.BURMESE) "ဖော်ပြချက်" else "Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val discount = discountText.toIntOrNull() ?: 10
                    onSave(titleEn, titleMy, code, discount, descEn, descMy)
                },
                enabled = code.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text(if (currentLanguage == Language.BURMESE) "ဖန်တီးမည်" else "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
            }
        }
    )
}

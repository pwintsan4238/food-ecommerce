package com.example.ui.screens.admin

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ReviewEntity
import com.example.model.Language
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminReviewsSection(
    reviews: List<ReviewEntity>,
    currentLanguage: Language,
    onReplyToReview: (String, String) -> Unit,
    onDeleteReview: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var starFilter by remember { mutableStateOf<Int?>(null) } // null = All
    var selectedReviewForReply by remember { mutableStateOf<ReviewEntity?>(null) }
    var reviewToDelete by remember { mutableStateOf<ReviewEntity?>(null) }

    val filteredReviews = reviews.filter { r ->
        val matchesStar = starFilter == null || r.rating == starFilter
        val matchesSearch = searchQuery.isBlank() ||
                r.customerName.contains(searchQuery, ignoreCase = true) ||
                r.customerPhone.contains(searchQuery, ignoreCase = true) ||
                r.productName.contains(searchQuery, ignoreCase = true) ||
                r.comment.contains(searchQuery, ignoreCase = true)
        matchesStar && matchesSearch
    }

    val avgRating = if (reviews.isNotEmpty()) {
        reviews.map { it.rating }.average()
    } else 5.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Summary & Metrics Header Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူ သုံးသပ်ချက်နှင့် အဆင့်သတ်မှတ်ချက်များ" else "Customer Reviews & Ratings",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${reviews.size} ${if (currentLanguage == Language.BURMESE) "ခု ပေးပို့ထားသည်" else "total reviews received"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format(Locale.US, "%.1f", avgRating),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        placeholder = { Text(if (currentLanguage == Language.BURMESE) "ဟင်းလျာ၊ အမည်၊ မှတ်ချက်ဖြင့် ရှာရန်..." else "Search by dish, customer name, review...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_reviews_search_input")
                    )

                    // Star filter row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = starFilter == null,
                                onClick = { starFilter = null },
                                label = { Text(if (currentLanguage == Language.BURMESE) "အားလုံး" else "All") }
                            )
                        }
                        listOf(5, 4, 3, 2, 1).forEach { stars ->
                            val count = reviews.count { it.rating == stars }
                            item {
                                FilterChip(
                                    selected = starFilter == stars,
                                    onClick = { starFilter = if (starFilter == stars) null else stars },
                                    label = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("$stars")
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("($count)")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (filteredReviews.isEmpty()) {
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
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "သုံးသပ်ချက် မရှိသေးပါ" else "No reviews match your filter",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredReviews, key = { it.id }) { review ->
                AdminReviewCard(
                    review = review,
                    currentLanguage = currentLanguage,
                    onReplyClick = { selectedReviewForReply = review },
                    onDeleteClick = { reviewToDelete = review }
                )
            }
        }
    }

    // Reply to Review Dialog
    selectedReviewForReply?.let { review ->
        var replyText by remember(review.id) { mutableStateOf(review.adminReply) }

        AlertDialog(
            onDismissRequest = { selectedReviewForReply = null },
            icon = { Icon(Icons.Default.Reply, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "သုံးသပ်ချက်အား အကြောင်းပြန်ရန်" else "Reply to Review",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${if (currentLanguage == Language.BURMESE) "ဝယ်ယူသူ" else "Customer"}: ${review.customerName} (${review.rating} ★)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${if (currentLanguage == Language.BURMESE) "ပစ္စည်း" else "Item"}: ${review.productName.ifBlank { "Overall Order" }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"${review.comment}\"",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        label = { Text(if (currentLanguage == Language.BURMESE) "စီမံခန့်ခွဲသူ အကြောင်းပြန်စာ" else "Admin Official Response") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_review_reply_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReplyToReview(review.id, replyText.trim())
                        selectedReviewForReply = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("submit_review_reply_btn")
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "သိမ်းမည်" else "Post Reply")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedReviewForReply = null }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }

    // Delete Review Confirmation
    reviewToDelete?.let { review ->
        AlertDialog(
            onDismissRequest = { reviewToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = {
                Text(
                    text = if (currentLanguage == Language.BURMESE) "သုံးသပ်ချက် ဖျက်မည်လား။" else "Delete Review?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (currentLanguage == Language.BURMESE)
                        "${review.customerName} ပေးထားသော သုံးသပ်ချက်ကို အပြီးတိုင် ဖျက်ပါမည်လား။"
                    else
                        "Are you sure you want to permanently remove this review by ${review.customerName}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteReview(review.id)
                        reviewToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (currentLanguage == Language.BURMESE) "ဖျက်မည်" else "Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { reviewToDelete = null }) {
                    Text(if (currentLanguage == Language.BURMESE) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun AdminReviewCard(
    review: ReviewEntity,
    currentLanguage: Language,
    onReplyClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(review.timestamp) { dateFormat.format(Date(review.timestamp)) }

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
            // Header: Stars + Date + Delete action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = if (index < review.rating) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${review.rating}/5",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFD97706)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Customer Name & Product Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = review.customerName.ifBlank { "Customer" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (review.productName.isNotBlank()) {
                        Text(
                            text = "${if (currentLanguage == Language.BURMESE) "ဟင်းလျာ" else "Product"}: ${review.productName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Customer Review Comment
            if (review.comment.isNotBlank()) {
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Admin Reply Section (if posted)
            if (review.adminReply.isNotBlank()) {
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
                                text = if (currentLanguage == Language.BURMESE) "ဆိုင်၏ အကြောင်းပြန်ချက်" else "Official Store Response",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = review.adminReply,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Reply button
            Button(
                onClick = onReplyClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (review.adminReply.isNotBlank())
                        (if (currentLanguage == Language.BURMESE) "အကြောင်းပြန်ချက် ပြင်ဆင်မည်" else "Edit Reply")
                    else
                        (if (currentLanguage == Language.BURMESE) "သုံးသပ်ချက်အား အကြောင်းပြန်မည်" else "Reply to Review"),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "customer_messages")
data class CustomerMessageEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String = "",
    val orderId: String = "",
    val subject: String,
    val message: String,
    val category: String = "GENERAL", // "ORDER_INQUIRY", "PRODUCT_QUESTION", "DELIVERY_ISSUE", "FEEDBACK", "OTHER"
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "NEW", // "NEW", "IN_PROGRESS", "RESOLVED"
    val adminReply: String = "",
    val repliedAt: Long = 0L
)

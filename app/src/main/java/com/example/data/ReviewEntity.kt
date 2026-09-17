package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val orderId: String = "",
    val productId: String = "",
    val productName: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val adminReply: String = "",
    val isApproved: Boolean = true
)

package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String = "",
    val townshipId: String,
    val townshipNameEn: String,
    val townshipNameMy: String,
    val deliveryAddressNote: String = "",
    val itemsSummary: String,
    val totalItemCount: Int,
    val foodSubtotalMMK: Int,
    val deliveryFeeMMK: Int,
    val grandTotalMMK: Int,
    val discountMMK: Int = 0,
    val promoCode: String = "",
    val paymentMethod: String,
    val paymentStatus: String = "PENDING", // "PAID", "PENDING", "COD_PENDING", "REFUNDED"
    val status: String,
    val statusUpdatedAt: Long = System.currentTimeMillis(),
    val estimatedMinutes: Int = 30,
    val orderItemsJson: String = "[]",
    val deliveryType: String = "STANDARD", // "STANDARD", "EXPRESS", "PICKUP"
    val trackingNumber: String = "",
    val deliveryPartnerName: String = "",
    val customerNotes: String = "",
    val cancellationReason: String = "",
    val cancelledAt: Long = 0L,
    val returnReason: String = "",
    val returnStatus: String = "NONE", // "NONE", "REQUESTED", "APPROVED", "REJECTED", "REFUNDED"
    val returnRequestedAt: Long = 0L,
    val deliveredAt: Long = 0L
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "",
    val phone: String = "",
    val preferredTownshipId: String = "ygn_kamayut",
    val defaultAddressNote: String = ""
)

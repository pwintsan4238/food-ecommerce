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
    val townshipId: String,
    val townshipNameEn: String,
    val townshipNameMy: String,
    val deliveryAddressNote: String = "",
    val itemsSummary: String,
    val totalItemCount: Int,
    val foodSubtotalMMK: Int,
    val deliveryFeeMMK: Int,
    val grandTotalMMK: Int,
    val paymentMethod: String,
    val status: String,
    val statusUpdatedAt: Long = System.currentTimeMillis(),
    val estimatedMinutes: Int = 30
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

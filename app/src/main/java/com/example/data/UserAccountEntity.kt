package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey
    val phone: String,
    val name: String,
    val role: String = "CUSTOMER", // "ADMIN" or "CUSTOMER"
    val preferredTownshipId: String = "ygn_kamayut",
    val defaultAddressNote: String = "",
    val password: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val email: String = "",
    val totalOrdersCount: Int = 0,
    val totalSpentMMK: Int = 0,
    val adminNotes: String = ""
)

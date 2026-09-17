package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Language

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String, // e.g. "FISH", "SHRIMP", "CRAB", "LOBSTER", "SQUID", "OCTOPUS", "SHELLFISH", or custom
    val nameEn: String,
    val nameMy: String,
    val iconEmoji: String = "🐟",
    val descriptionEn: String = "",
    val descriptionMy: String = "",
    val sortOrder: Int = 0,
    val isArchived: Boolean = false
) {
    fun displayName(language: Language): String = if (language == Language.BURMESE && nameMy.isNotBlank()) nameMy else nameEn
}


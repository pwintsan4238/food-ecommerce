package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Language

@Entity(tableName = "brands")
data class BrandEntity(
    @PrimaryKey
    val id: String, // e.g. "brand_taim_ta_man", "brand_rakhine_coast"
    val nameEn: String,
    val nameMy: String,
    val originRegionEn: String = "Rakhine Coast",
    val originRegionMy: String = "ရခိုင်ကမ်းရိုးတန်း",
    val description: String = "",
    val descriptionEn: String = "",
    val descriptionMy: String = "",
    val logoEmoji: String = "🏷️",
    val contactPhone: String = "",
    val isArchived: Boolean = false
) {
    fun displayName(language: Language): String = if (language == Language.BURMESE && nameMy.isNotBlank()) nameMy else nameEn
}

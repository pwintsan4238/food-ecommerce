package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_settings")
data class BusinessSettingsEntity(
    @PrimaryKey
    val id: Int = 1,

    // Brand Information
    val brandNameEn: String = "Taim Ta Man Seafood",
    val brandNameMy: String = "တိမ်တမန်ပင်လယ်စာ",
    val brandSloganEn: String = "Fresh coastal seafood delivered daily straight from Rakhine waters",
    val brandSloganMy: String = "ရခိုင်ကမ်းရိုးတန်းထွက် လတ်ဆတ်သော ပင်လယ်စာ စစ်စစ်များ",
    val taglineEn: String = "Fresh coastal seafood delivered daily straight from Rakhine waters",
    val taglineMy: String = "ရခိုင်ကမ်းရိုးတန်းထွက် လတ်ဆတ်သော ပင်လယ်စာ စစ်စစ်များ",
    val brandDescriptionEn: String = "Authentic Rakhine coastal seafood, fresh catches, and traditional marinades packed on ice and delivered fast across Yangon.",
    val brandDescriptionMy: String = "ရခိုင်ကမ်းရိုးတန်းမှ လတ်လတ်ဆတ်ဆတ် ဖမ်းယူရရှိသော ပင်လယ်စာများနှင့် ရိုးရာအစားအစာများကို အအေးထိန်းထုပ်ပိုးမှုဖြင့် အိမ်အရောက် ပို့ဆောင်ပေးပါသည်။",
    val brandLogoEmoji: String = "🦞",

    // Contact Information
    val storePhone: String = "09450012345",
    val supportPhone: String = "09450012345",
    val supportEmail: String = "support@taimtamanseafood.com",
    val storeAddressEn: String = "No. 42, Inya Road, Kamayut Township, Yangon",
    val physicalAddressEn: String = "No. 42, Inya Road, Kamayut Township, Yangon",
    val storeAddressMy: String = "အမှတ် (၄၂)၊ အင်းလျားလမ်း၊ ကမာရွတ်မြို့နယ်၊ ရန်ကုန်မြို့",
    val physicalAddressMy: String = "အမှတ် (၄၂)၊ အင်းလျားလမ်း၊ ကမာရွတ်မြို့နယ်၊ ရန်ကုန်မြို့",
    val openingHoursEn: String = "9:00 AM - 9:30 PM (Daily)",
    val businessHoursEn: String = "9:00 AM - 9:30 PM (Daily)",
    val openingHoursMy: String = "မနက် ၉:၀၀ မှ ည ၉:၃၀ ထိ (နေ့စဉ်)",
    val businessHoursMy: String = "မနက် ၉:၀၀ မှ ည ၉:၃၀ ထိ (နေ့စဉ်)",
    val viberNumber: String = "09450012345",
    val telegramHandle: String = "@TaimTaManSeafood",
    val facebookPage: String = "facebook.com/TaimTaManSeafood",

    // Store Operating Status
    val isStoreOpen: Boolean = true,
    val closedNoticeEn: String = "Our store is currently closed for daily catch restocking. Accepting orders will resume at 9:00 AM.",
    val closedNoticeMy: String = "လတ်ဆတ်သော ပင်လယ်စာများ ထပ်မံဖြည့်တင်းနေသဖြင့် ဆိုင်ယာယီပိတ်ထားပါသည်။ မနက် ၉:၀၀ တွင် ပြန်လည်ဖွင့်ပါမည်။",
    val minOrderAmountMMK: Int = 5000,

    // Shipping / Logistics Settings
    val standardDeliveryFeeMMK: Int = 2000,
    val expressDeliveryFeeMMK: Int = 1500,
    val freeDeliveryThresholdMMK: Int = 50000,
    val coldStorageBoxFeeMMK: Int = 2500,
    val vacuumCleaningFeeMMK: Int = 1500,
    val freeDeliveryEnabled: Boolean = true,
    val expressDeliveryEnabled: Boolean = true
)

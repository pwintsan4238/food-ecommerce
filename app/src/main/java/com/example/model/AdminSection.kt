package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.ui.graphics.vector.ImageVector

enum class AdminSection(
    val titleEn: String,
    val titleMy: String,
    val icon: ImageVector
) {
    OVERVIEW("Overview", "ခြုံငုံသုံးသပ်ချက်", Icons.Default.Dashboard),
    PRODUCTS("Products", "ကုန်ပစ္စည်းများ", Icons.Default.Inventory2),
    ORDERS("Orders", "အော်ဒါများ", Icons.Default.ReceiptLong),
    RETURNS("Returns & Refunds", "ပစ္စည်းပြန်ပို့မှုများ", Icons.Default.AssignmentReturn),
    CUSTOMERS("Customers", "ဝယ်ယူသူများ", Icons.Default.People),
    MESSAGES("Messages", "မက်ဆေ့ချ်များ", Icons.Default.QuestionAnswer),
    REVIEWS("Reviews & Ratings", "သုံးသပ်ချက်များ", Icons.Default.RateReview),
    INVENTORY("Inventory", "ပစ္စည်းလက်ကျန်", Icons.Default.FormatListBulleted),
    PAYMENTS("Payments", "ငွေပေးချေမှု", Icons.Default.AccountBalanceWallet),
    LOGISTICS("Logistics", "ပို့ဆောင်ရေး", Icons.Default.LocalShipping),
    PROMOTIONS("Promotions", "ပရိုမိုးရှင်းများ", Icons.Default.Campaign),
    REPORTS("Reports", "စာရင်းအစီရင်ခံစာ", Icons.Default.BarChart),
    SETTINGS("Settings", "စနစ်ပြင်ဆင်မှု", Icons.Default.Settings);

    fun title(lang: Language): String = if (lang == Language.BURMESE) titleMy else titleEn
}

data class PromotionItem(
    val id: String,
    val titleEn: String,
    val titleMy: String,
    val code: String,
    val discountPercent: Int,
    val isActive: Boolean,
    val descriptionEn: String,
    val descriptionMy: String
)

data class QrPaymentOption(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val accountName: String,
    val accountPhoneOrNo: String,
    val isEnabled: Boolean = true,
    val qrCodeNote: String = "",
    val qrImageUrl: String = "",
    val colorHex: String = "#003874",
    val minOrderAmountMMK: Int = 0,
    val maxOrderAmountMMK: Int = 0
)

data class PaymentConfig(
    val kbzPayEnabled: Boolean = true,
    val codEnabled: Boolean = true,
    val mobileBankingEnabled: Boolean = true,
    val kbzPayPhone: String = "09987654321",
    val kbzPayAccountName: String = "TAIM TA MAN SEAFOOD CO., LTD.",
    val codMinOrderAmountMMK: Int = 0,
    val codMaxOrderAmountMMK: Int = 0,
    val qrPaymentOptions: List<QrPaymentOption> = listOf(
        QrPaymentOption(
            id = "kbzpay_default",
            name = "KBZPay (KPay QR)",
            accountName = "TAIM TA MAN SEAFOOD CO., LTD.",
            accountPhoneOrNo = "09987654321",
            isEnabled = true,
            qrCodeNote = "Scan KPay QR or transfer to phone number",
            colorHex = "#003874"
        ),
        QrPaymentOption(
            id = "wavepay_default",
            name = "WavePay QR",
            accountName = "TAIM TA MAN SEAFOOD CO., LTD.",
            accountPhoneOrNo = "09789456123",
            isEnabled = true,
            qrCodeNote = "Scan WavePay QR or transfer to phone number",
            colorHex = "#F59E0B"
        )
    )
)

data class StoreSettingsConfig(
    val isStoreOpen: Boolean = true,
    val storeName: String = "Taim Ta Man Seafood",
    val storePhone: String = "09450012345",
    val minOrderAmountMMK: Int = 5000,
    val standardDeliveryFeeMMK: Int = 2000,
    val freeDeliveryThresholdMMK: Int = 50000,
    val openingHours: String = "9:00 AM - 9:30 PM (Daily)",
    val businessHours: String = "9:00 AM - 9:30 PM (Daily)",
    val storeAddress: String = "No. 42, Inya Road, Kamayut Township, Yangon"
)
